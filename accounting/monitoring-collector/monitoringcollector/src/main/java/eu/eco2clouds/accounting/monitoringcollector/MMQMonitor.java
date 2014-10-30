////////////////////////////////////////////////////////////////////////
//
// Copyright (c) The University of Edinburgh, 2013
//
// This software may not be used, sold, licensed, transferred, copied
// or reproduced in whole or in part in any manner or form or in or
// on any media by any person other than in accordance with the terms
// of the Licence Agreement supplied with the software, or otherwise
// without the prior written consent of the copyright owners.
//
// This software is distributed WITHOUT ANY WARRANTY, without even the
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
// PURPOSE, except where stated in the Licence Agreement supplied with
// the software.
//
//	Created By :			Dominic Sloan-Murphy, Iakovos Panourgias
//	Last Updated Date :		28 Aug 2014
//	Created for Project :	ECO2Clouds
//
////////////////////////////////////////////////////////////////////////
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////

package eu.eco2clouds.accounting.monitoringcollector;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

import com.google.gson.Gson;

/**
 * Monitors the BonFIRE Management Message Queue (MMQ) for ECO2Clouds experiment and compute resource events to know when to start and stop tracking a resource.
 *  When an ECO2Clouds event occurs, it is decoded and the relevant information passed to this object’s DatabaseConnector instance.
 *  The database function called depends on the message’s Route.
 */
public class MMQMonitor implements Monitor {

	/** The Constant EXCHANGE_NAME used to configure the RabbitMQ message queue connection. */
	private static final String EXCHANGE_NAME = "resourceUsage";
	
	/** The Constant VIRTUAL_HOST used to configure the RabbitMQ message queue connection. */
	private static final String VIRTUAL_HOST = "bonfire";
	
	/** The Constant CONSUMER_TAG used to identify the current consumer on a channel for starting and stopping it. */
	private static final String CONSUMER_TAG = "ECO2Clouds_Accounting_Service_Monitor";

	/** Used to represent the AMQP data channel in RabbitMQ. */
	private Channel channel = null;
	
	/** Used to represent the AMQP connection in RabbitMQ. */
	private Connection connection = null;
	
	/** Produces properly configured RabbitMQ Connection objects. */
	public ConnectionFactory factory = null;
	
	/** The consumer object which monitors the message queue using blocking semantics.  */
	private QueueingConsumer consumer = null;

	/** Google Gson object used to deserialise the JSON messsage queue data into MMQEvent objects. */
	private Gson gson = new Gson();

	/** Used to abstract database functions from the monitor itself. */
	public IDatabaseConnector dbConnector = new DatabaseConnector();
	
	/** Tracks currently active experiments. Used to filter irrelevant message queue events. */
	public HashSet<Integer> experiments = new HashSet<Integer>(); 
	
	/** Associates VMs with an experiment. Used to filter irrelevant message queue events. */
	public HashMap<URI,Integer> vmAssociations = new HashMap<URI,Integer>();
	
	/** The queue name returned after declaration of the RabbitMQ channel. */
	private String queueName;
	
	/** Stores a list of group names which are accepted in the ECO2Clouds family. */
	private static ArrayList<String> groupNames = new ArrayList<String>();
	
	public MMQMonitor() {
		factory = new ConnectionFactory();
		factory.setHost(ConfigurationValues.mmqHost);
		factory.setVirtualHost(VIRTUAL_HOST);
		factory.setUsername(ConfigurationValues.mmqUsername);
		factory.setPassword(ConfigurationValues.mmqPassword);
	}

	// Try to create the connection and bind to MMQ.
	/**
	 * Make connection.
	 */
	public void makeConnection() {

		// Ensure valid topics attribute
		if (ConfigurationValues.mmqTopics == null) {
			MessageHandler.error("Accounting service monitoring collector topics array is null. Unrecoverable failure.");
			throw new NullPointerException();
		}
		
		// Exit on failure.
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
		} catch (IOException e) {
			MessageHandler.error("Accounting service monitoring collector failed to subscribe to Management Message Queue at " + ConfigurationValues.mmqHost + ". Unrecoverable failure.");
			throw new RuntimeException(e);
		}
		
		try {
			queueName = channel.queueDeclare().getQueue();
			for(int i=0; i<ConfigurationValues.mmqTopics.length; i++) {
				channel.queueBind(queueName, EXCHANGE_NAME, ConfigurationValues.mmqTopics[i]);
			}
		} catch (IOException e) {
			MessageHandler.error("Accounting service monitoring collector failed to bind to a Management Message Queue topic. Unrecoverable failure.");
			throw new RuntimeException(e);
		}
	}
	
	
	// ***TODO: ASSUMING THE EXPERIMENT QUEUE IS ACTUALLY ORDERED LIKE A QUEUE AND AN EXPERIMENT CREATE MESSAGE WILL ALWAYS BE SERVED BEFORE ASSOCIATED COMPUTE/AGGREGATOR CREATE***
	/**
	 * Process experiment creation.
	 *
	 * @param event the event
	 */
	private void processExperimentCreation(MMQEvent event) {

		int experimentId = getExperimentIdFromURI(event.getObjectId());
		
		// Ensure event is relevant
		boolean foundMatch = false;
		String groupName ="";
		String userName = "";
		
		for (String group: groupNames) {
			if (event.getGroupId().toLowerCase().startsWith(group.toLowerCase())) {
				foundMatch = true;
				groupName = event.getGroupId();
				userName = event.getUserId();
			}
		}
		
		if ( !foundMatch ) {
			MessageHandler.print("ExperimentId=" + experimentId + " creation rejected: not in eco2clouds.");
			return;
		}

		// Instantiate a new Experiment and start tracking
		// There is no associated aggregator at this time, collector will have to handle this.
		experiments.add(experimentId);
		dbConnector.insertNewExperiment(experimentId, groupName, userName);

		MessageHandler.print("Experiment creation accepted: experimentId=" + experimentId);
	}

	//  
	/**
	 * Process experiment termination.
	 *
	 * @param event the event
	 */
	private void processExperimentTermination(MMQEvent event) {

		// Ensure this event corresponds to an eco2clouds experiment we are tracking
		int experimentId = getExperimentIdFromURI(event.getObjectId());
		if (!experiments.contains(experimentId)) {
			MessageHandler.print("Experimentid= " + experimentId + " termination rejected: experiment not being tracked.");
			return;
		}

		// Stop tracking and clean up all associated VMs
		experiments.remove(experimentId);
		vmAssociations.values().removeAll(Collections.singleton(experimentId)); // Can't just do remove(experimentId) as that would only remove the first instance
		dbConnector.setExperimentTerminated(experimentId);

		MessageHandler.print("Experiment termination accepted: experimentId=" + experimentId);
	}

	// Do no track these states in this revision - attempt metrics gather regardless.
	/**
	 * Process compute stopped.
	 *
	 * @param event the event
	 */
	private void processComputeStopped(MMQEvent event) { }
	
	/**
	 * Process compute suspended.
	 *
	 * @param event the event
	 */
	private void processComputeSuspended(MMQEvent event) { }
	
	/**
	 * Process compute resumed.
	 *
	 * @param event the event
	 */
	private void processComputeResumed(MMQEvent event) { }	
	
	// Special case: if required as message queue does not distinguish between aggregator and compute VM creation
	/**
	 * Process compute creation.
	 *
	 * @param event the event
	 */
	private void processComputeCreation(MMQEvent event) {
		
		// Ensure this event corresponds to an eco2clouds experiment we are tracking
		int experimentId = getExperimentIdFromURI(event.getObjectData().getExperimentId());
		if (!experiments.contains(experimentId)) {
			MessageHandler.print("Compute creation rejected: experimentId=" + experimentId + " not being tracked.");
			return;
		}

		if (event.getObjectData().getName().equalsIgnoreCase("BonFIRE-monitor")) {
			dbConnector.setExperimentAggregator(experimentId, event.getObjectId());
			MessageHandler.print("Aggregator compute creation accepted: experimentId=" + experimentId + " aggregator=" + event.getObjectId().toString());
		}
		vmAssociations.put(event.getObjectId(),experimentId);
		dbConnector.insertNewVM(experimentId, event.getObjectId(), event.getTimestamp(), event.getObjectData().getHost(), getSiteFromURI(event.getObjectId()));
		MessageHandler.print("VM compute creation accepted: experimentId=" + experimentId + ", compute(SITE)=" + event.getObjectId().toString() + ", host=" + event.getObjectData().getHost() + ", site=" + getSiteFromURI(event.getObjectId()) + ".");
	}
	
	/**
	 * Process compute updated.
	 *
	 * @param event the event
	 */
	private void processComputeUpdated(MMQEvent event) {
		
		// Ensure this event corresponds to an eco2clouds experiment we are tracking
		int experimentId = getExperimentIdFromURI(event.getObjectData().getExperimentId());
		if (!experiments.contains(experimentId)) {
			MessageHandler.print("Compute updated rejected: experimentId=" + experimentId + " not being tracked.");
			return;
		}

		if (!vmAssociations.containsKey(event.getObjectId())) {
			MessageHandler.print("Compute updated event: experimentId=" + experimentId + " for VM:" + event.getObjectId().toString() + " is not in the VM association!!!");
			return;
		}
		
		if (event.getObjectData().getHost() == null) {
			MessageHandler.error("Compute updated event: experimentId=" + experimentId + " has NULL HOST!!!");
			return;
		}

		dbConnector.updateVM(experimentId, event.getObjectId(), event.getTimestamp(), event.getObjectData().getHost(), getSiteFromURI(event.getObjectId()));
		MessageHandler.print("VM compute updated accepted: experimentId=" + experimentId + ", compute(SITE)=" + event.getObjectId().toString() + ", host=" + event.getObjectData().getHost() + ", site=" + getSiteFromURI(event.getObjectId()) + ".");
	}
	
	/**
	 * Process compute deleted message.
	 *
	 * @param event the event
	 */
	private void processComputeDelete(MMQEvent event) {
		
		// Ensure this event corresponds to an eco2clouds experiment we are tracking
//		int experimentId = getExperimentIdFromURI(event.getObjectData().getExperimentId());
//		if (!experiments.contains(experimentId)) {
//			MessageHandler.print("Compute destroyed rejected: experimentId=" + experimentId + " not being tracked.");
//			return;
//		}
		
		if (!vmAssociations.containsKey(event.getObjectId())) {
			MessageHandler.print("Compute destroyed event: experimentId=NULL for VM:" + event.getObjectId().toString() + " is not in the VM association!!!");
			return;
		}
		
		dbConnector.terminateVM(0, event.getObjectId(), event.getTimestamp(), "", "");
		MessageHandler.print("VM compute destroyed accepted: experimentId=NULL, compute=" + event.getObjectId().toString() + ", host=NULL, location=NULL.");
	}
	
	/**
	 * Process compute done message.
	 *
	 * @param event the event
	 */
	private void processComputeDoneMessage(MMQEvent event) {

		// ***TODO: Should we check for aggregator stops here?***

		// Ensure this event corresponds to a VM we are tracking
		// Can't check experiment ID in this case as stop events don't contain that information.
		URI location = event.getObjectId();
		if (!vmAssociations.containsKey(location)) {
			MessageHandler.print("Compute deletion id=" + location + " rejected: resource not being tracked.");
			return;
		}	

		// Stop tracking VM (remove as cannot resume from this state)
		int experimentId = vmAssociations.get(location);
		vmAssociations.remove(location);
		
		// Collector not informed in this iteration.
		// TODO: Improve efficiency, collector will be making requests for finished VMs and having to handle the error.
		//dbConnector.setVMDone(experimentId, location);
		
		MessageHandler.print("Compute deletion accepted: experimentId=" + experimentId + " compute=" + location.toString());
	}

	// ***TODO: Functionality not implemented yet - VMs cannot migrate physical hosts for now - assuming event structure***
	/*
	private void processComputeMigrateMessage(MMQEvent event) {
		
		// Ensure this event corresponds to an eco2clouds experiment we are tracking
		int experimentId = getExperimentIdFromURI(event.getObjectData().getExperimentId());
		if (!experiments.contains(experimentId)) {
			MessageHandler.print("Compute migration rejected: experiment not being tracked.");
			return;
		}		
	}*/
		
	/**
	 * Handles the message queue event depending on the queue topic it originated from.
	 *
	 * @param routingKey the originating message queue topic. Used as a key to select how the event should be processed.  
	 * @param event the event
	 */
	public void routeMessage(String routingKey, MMQEvent event) {
		
		// Special case: compute (aggregator or VM) stopped (substring required to remove site specific topic information - means standard switch cannot be used).
		// ***TODO: FIND PROPER FAILED STATE EXAMPLE*** //|| routingKey.substring(routingKey.indexOf('.') + 1).equals("compute.state.stopped.failed")  ) {
		if ( routingKey.substring(routingKey.indexOf('.') + 1).equals("compute.state.stopped.done") ) {
			processComputeDoneMessage(event);
		}
		
		else {
			Route route = Route.fromString(routingKey);	
			switch(route) {
			case EXPERIMENT_CREATION:
				processExperimentCreation(event);
				break;
			case EXPERIMENT_TERMINATION:
				processExperimentTermination(event);
				break;
			case COMPUTE_CREATION:
				processComputeCreation(event);
				break;
			case COMPUTE_STOPPED: // NB A stopped resource can still be put into the RUNNING state
				processComputeStopped(event);
				break;
			case COMPUTE_SUSPENDED:
				processComputeSuspended(event);
				break;
			case COMPUTE_RESUMED:
				processComputeResumed(event);
				break;
			case COMPUTE_UPDATED:
				processComputeUpdated(event);
				break;
			case COMPUTE_DELETE:
				processComputeDelete(event);
				break;
			}
		}
	}

	/**
	 * Decodes the numerical experiment ID from a URI. e.g. Gets 2946 from "/experiments/2946".
	 *
	 * @param uri the URI to be decoded.
	 * @return the experiment id of the given URI.
	 */
	private int getExperimentIdFromURI(URI uri) {

		// Test for invalid input
		if ( (uri == null) || (uri.toString().equals("")) ) {
			MessageHandler.error("Invalid resource URI detected.");
			throw new IllegalArgumentException();
		}

		String path = uri.getPath();
		String idStr = path.substring(path.lastIndexOf('/') + 1);
		return Integer.parseInt(idStr);
	}
	
	// TODO: Fix redundancy between this function and getExperimentIdFromURI() 
	private String getSiteFromURI(URI uri) {

		// Test for invalid input
		if ( (uri == null) || (uri.toString().equals("")) ) {
			MessageHandler.error("Invalid resource URI detected.");
			throw new IllegalArgumentException();
		}

		String path = uri.getPath();
		String[] splitPath = path.split("/");
		return splitPath[2];
	}

	private void readConfigAndPopulateListOfGroups() {
		groupNames.add("eco2clouds");
		
		File file = new File("config.txt");
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				   // process the line.
				String[] splited = line.split("\\s+");
				if ( splited.length != 1 ) {
					MessageHandler.error("config.txt has illegal entry. Ignoring. (line:" + line + ").");
					continue;
				}

				MessageHandler.print("MMQMonitor::readConfigAndPopulateListOfGroups(): Adding group:" + splited[0] + ", to list.");
				groupNames.add(splited[0]);
			}
			br.close();
			
			return;
			
		} catch (FileNotFoundException e) {
			MessageHandler.error("MMQMonitor::readConfigAndPopulateListOfGroups(): config.txt could not be found. Adding default entry.");
			groupNames.add("eco2");
		}  catch (IOException e) {
			MessageHandler.error("MMQMonitor::readConfigAndPopulateListOfGroups(): Parsing config.txt threw an IOException. Adding default entry.");
			groupNames.add("eco2");
		}
		
		return;
	}
	

	/**
	 * <p>Starts the monitoring of the BonFIRE management message queue and handles each message as it is received. Each message is deserialised from JSON into an MMQEvent object and shipped to the routeMessage() method for processing.</p>
	 * 
	 * <p>Implemented using the RabbitMQ QueueingConsumer and closely following the example code given in the BonFIRE Wiki documentation.</p>
	 * 
	 * <p>To stop, must be interrupted from another thread otherwise messages will be consumed indefinitely.</p> 
	 * 
	 * @see java.lang.Runnable#run()
	 * @see <a href="http://wiki.bonfire-project.eu/index.php/Producers_and_consumers_code#Consumer">BonFIRE Wiki Producers and Consumers Code</a>
	 */
	@Override
	public void run() {

		boolean stopMonitoring = false;
		int readDatabse = 40;
		
		// Connect to MMQ and database
		makeConnection();
		dbConnector.connect();
		consumer = new QueueingConsumer(channel);

		// Read config file and populate list of names
		readConfigAndPopulateListOfGroups();
		// TODO: Sleep for five seconds and allow the other thread to verify the DB. Then read the DB
		//       and initialise the HASH MAPs.
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			MessageHandler.error("Monitor died whilst sleeping.");
			stopMonitoring = true;
		}
		
		try {
			MessageHandler.print("Monitoring...");
			channel.basicConsume(queueName, true, CONSUMER_TAG, consumer);
		} catch (IOException e1) {
			MessageHandler.error("Monitor failed to consume message queue item.");
		}

		ArrayList<Experiment> activeExperiments = dbConnector.getActiveExperiments();
		for (Experiment experiment: activeExperiments) {
			experiments.add(experiment.id);
			
			ArrayList<VirtualMachine> activeVMs = dbConnector.getActiveVirtualMachinesOfExperiment(experiment.id);
			for (VirtualMachine vm: activeVMs) {
				URI tempURI;
				try {
					tempURI = new URI(vm.location);
					vmAssociations.put(tempURI,experiment.id);
				} catch (URISyntaxException e) {
					MessageHandler.error("MMQMonitor(): " + vm.location + " is not a valid URI. Ignoring....");
				}
			}
		}

		// Consume messages until thread is interrupted
		while (!stopMonitoring) {
			QueueingConsumer.Delivery delivery = null;

			if ( readDatabse > 0) {
				MessageHandler.print("MMQMonitor(): Resetting and Updating memory.");
				experiments.clear();
				vmAssociations.clear();
				activeExperiments = dbConnector.getActiveExperiments();
				for (Experiment experiment: activeExperiments) {
					if (!experiments.contains(experiment.id)) {
						MessageHandler.print("MMQMonitor(): Added experimentid:" + experiment.id + ".");
						experiments.add(experiment.id);
					}
					
					ArrayList<VirtualMachine> activeVMs = dbConnector.getActiveVirtualMachinesOfExperiment(experiment.id);
					for (VirtualMachine vm: activeVMs) {
						URI tempURI;
						try {
							tempURI = new URI(vm.location);
							if (!vmAssociations.containsKey(tempURI)) {
								MessageHandler.print("MMQMonitor(): Added VM:" + experiment.id + ", location:" + tempURI + ".");
								vmAssociations.put(tempURI,experiment.id);
							} else {
								MessageHandler.print("MMQMonitor(): did NOT add VM:" + experiment.id + ", location:" + tempURI + ".");
							}
						} catch (URISyntaxException e) {
							MessageHandler.error("MMQMonitor(): " + vm.location + " is not a valid URI. Ignoring....");
						}
					}
				}
				readDatabse--;
			}
			
			try {
				delivery = consumer.nextDelivery();

				String message = new String(delivery.getBody());
				String routingKey = delivery.getEnvelope().getRoutingKey();

				MessageHandler.print(" [x] Received '" + routingKey + "':'" + message + "'");

				MMQEvent event = gson.fromJson(message, MMQEvent.class);

				routeMessage(routingKey, event);
			} catch (ShutdownSignalException e) {			
				stopMonitoring = true;
				MessageHandler.error("MMQMonitor:run(): Got ShutdownSignalException. Exception Message: " + e.getMessage() + ".");
			} catch (ConsumerCancelledException e) {			
				stopMonitoring = true;
				MessageHandler.error("MMQMonitor:run(): Got ConsumerCancelledException. Exception Message: " + e.getMessage() + ".");
			} catch (InterruptedException e) {			
				stopMonitoring = true;
				MessageHandler.error("MMQMonitor:run(): Got InterruptedException. Exception Message: " + e.getMessage() + ".");
			}
		}

		MessageHandler.print("Shutdown request detected. Stopping Monitor...");

		// Clean up and close
		dbConnector.disconnect();
		try {
			if (connection != null) {
				connection.close();
				connection = null;
			}
		} catch (IOException e) {
			MessageHandler.error("MMQMonitor:run(): Got IOException. Exception Message: " + e.getMessage() + ".");
		} // Do nothing, closing anyway.
		catch (Exception e) { 
			MessageHandler.error("MMQMonitor:run(): Got Exception. Exception Message: " + e.getMessage() + ".");
		} // Do nothing, closing anyway.

	}

	/**
	 * <p>The Enum Route.</p>
	 * 
	 * <p>Enumerates the possible message types read from the BonFIRE management message queue.</p>
	 * 
	 * Used for switch statement in routeMessage(). Java switch on String only available in JDK7+. This Enum is used for compatibility.
	 */
	private enum Route {
		
		/** Experiment creation. */
		EXPERIMENT_CREATION("res-mng.experiment.create"),
		
		/** Experiment termination. */
		EXPERIMENT_TERMINATION("res-mng.experiment.state.terminated"),
		
		/** Compute creation - includes both aggregator and other compute resource creation. */
		COMPUTE_CREATION("res-mng.compute.create"),
		
		/** Compute stopped. */
		COMPUTE_STOPPED("res-mng.compute.state.stopped"),
		
		/** Compute suspended. */
		COMPUTE_SUSPENDED("res-mng.compute.state.suspended"),
		
		/** Compute resumed. */
		COMPUTE_RESUMED("res-mng.compute.state.resume"),
		
		/** Compute updated. */
		COMPUTE_UPDATED("res-mng.compute.update"),
		
		/** Compute updated. */
		COMPUTE_DELETE("res-mng.compute.delete");

		/** <p>The string representation of the BonFIRE management message queue topic associated with an Enum value.</p>
		 * 
		 *  e.g. {@code "res-mng.experiment.create"} for {@code EXPERIMENT_CREATION}. */
		private String text;

		/**
		 * Instantiates a new route.
		 *
		 * @param {@link MMQMonitor.Route#text} 
		 */
		Route(String text) {
			this.text = text;
		}

		/**
		 * Getter for the {@link MMQMonitor.Route#text} attribute.
		 *
		 * @return the {@link MMQMonitor.Route#text} attribute.
		 */
		public String getText() {
			return this.text;
		}

		/**
		 * <p>Matches the supplied text to the corresponding Enum value (ignoring case) and returns a Route instance.</p>
		 * 
		 * e.g. {@code fromString("res-mng.experiment.create")} returns {@code EXPERIMENT_CREATION}.
		 *
		 * @param text the text to be matched with an Enum value.
		 * @return the corresponding route.
		 */
		public static Route fromString(String text) {
			if (text != null) {
				for (Route r : Route.values()) {
					if (text.equalsIgnoreCase(r.getText())) {
						return r;
					}
				}
			}
			MessageHandler.error("No constant with text " + text + " found in Route enum.");
			throw new IllegalArgumentException();
		}
	}
	
	public void addGroupToGroupNames(String groupName)
	{
		groupNames.add(groupName);
	}
	
	public static void main(String[] args) throws URISyntaxException {
		MMQMonitor mon = new MMQMonitor();
		mon.run();
		
		/*mon.dbConnector.connect();
		
		// Add experiment
		int experimentId = 2946;
		
		MMQEvent event = new MMQEvent();
		event.setTimestamp(1370860773L);
		event.setEventType("create");
		event.setObjectType("experiment");
		event.setObjectId(new URI("/experiments/2946"));
		event.setSource("res-mng");
		event.setGroupId("eco2clouds");
		event.setUserId("demo");
		
		MMQEvent.ObjectData objectData = event.new ObjectData();
		objectData.setName("mmq-test-eco2clouds");
		objectData.setDuration(600);
		
		event.setObjectData(objectData);
		
		mon.routeMessage("res-mng.experiment.create", event);
		
		MMQEvent event = new MMQEvent();
		URI aggregatorLocation = new URI("/locations/uk-epcc/computes/456");
		event.setTimestamp(1370860790L);
		event.setEventType("create");
		event.setObjectType("compute");
		event.setObjectId(aggregatorLocation);
		event.setSource("res-mng");
		event.setGroupId("eco2clouds");
		event.setUserId("demo");
		
		MMQEvent.ObjectData objectData = event.new ObjectData();
		objectData.setName("BonFIRE-monitor");
		// Skip irrelevant fields
		objectData.setExperimentId(new URI("/experiments/2946"));
		
		event.setObjectData(objectData);
			
		// Add aggregator
		mon.routeMessage("res-mng.compute.create", event);
		
		mon.dbConnector.disconnect();
		*/
	}
}