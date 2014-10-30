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
//	Last Updated Date :		03 Sept 2014
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



















import eu.eco2clouds.accounting.monitoringcollector.MessageHandler.Level;

/**
 * Periodically gets the list of active ECO2Clouds resources from the accounting database and calls the BonFIRE Zabbix API abstraction for all metrics recorded for that experiment since the last query.
 */
public class MetricsCollector implements Collector {

	/** Used to signal the collector to stop. Can be set by an external thread. */
	private volatile boolean stopCollecting = false;
	
	/** Used to calculate the duration of processing to reduce error on API calls for historical data when getting all new metrics which have been recorded since the Collector thread was asleep. */
	long startTime = System.currentTimeMillis();
	
	/** Used to abstract database functions from the collector itself. */
	public IDatabaseConnector dbConnector = new DatabaseConnector();
	
	/** The connection factory which uses certificate authentication for mutual authentication with the BonFIRE API. */
	SSLSocketFactory sslSocketFactory = null;
	
	/** A pool of threads */
	private ExecutorService tpes 	= Executors.newFixedThreadPool(ConfigurationValues.firstLevelOfThreads);
	
	/** */
    private MetricsCollectorThread workers[]	= new MetricsCollectorThread[220];
    
    /** */
    private Future futures[] 		= new Future[220];
		
    /** Stores the list of application specific metrics. */
    private static ArrayList<String> applicationMetrics = new ArrayList<String>(); 
		
	/** Stores a list of group names which are accepted in the ECO2Clouds family. */
	private static ArrayList<String> groupNames = new ArrayList<String>();
		
	/** Stores the various metrics. */
	private static Integer highPriorityMetricsPHCounter = 0;
	private static Integer mediumPriorityMetricsPHCounter = 0;
	private static Integer lowPriorityMetricsPHCounter = 0;
	private static Integer highPriorityMetricsVMCounter = 0;
	private static Integer mediumPriorityMetricsVMCounter = 0;
	private static Integer lowPriorityMetricsVMCounter = 0;
	
	/** Store the experiments from the Scheduler */
	private static ArrayList<Experiment> experimentsFromScheduler = new ArrayList<Experiment>();
	
	/** Store the user names from the Scheduler */
	private static ArrayList<String> userNamesFromScheduler = new ArrayList<String>();
	
	/** Last collection date/time. */
	Calendar siteCollectionTime = Calendar.getInstance();
	Calendar phCollectionTime = Calendar.getInstance();
	Calendar phCollectionTimeMedium = Calendar.getInstance();
	Calendar phCollectionTimeLow = Calendar.getInstance();
	Calendar vmCollectionTime = Calendar.getInstance();
	Calendar vmCollectionTimeMedium = Calendar.getInstance();
	Calendar vmCollectionTimeLow = Calendar.getInstance();
		
	/**
	 * Instantiates a new metrics collector.
	 */
	public MetricsCollector() {
	}
	
	/**
	 * Must be run in its own thread.
	 * Updates all eco-metrics for Experiments, Virtual Machines, Hosts and Sites; sleeps for the set polling rate (default 10s) and repeats until told to stop collecting (i.e. until stopCollecting = true). 
	 */
	@Override
	public void run() {
			
		printDetails();
		
		// Sets up trusted certificate chain for mutual authentication between this client and the BonFIRE API. 
		establishSecureContext();
		
		dbConnector.connect();
		
		// Read config file and populate list of names
		MessageHandler.debug("Reading group configuration file...");
		readConfigAndPopulateListOfGroups();
		MessageHandler.debug("Read group configuration file.\n");
		
		MessageHandler.debug("Upgrading database...");
		upgradeDB();
		MessageHandler.debug("Upgraded database.\n");
		
		MessageHandler.debug("Retrieve user names/groups from the Scheduler...");
		buildUpUserNameGroupsRelationships();
		MessageHandler.debug("Retrieved user names/groups from the Scheduler.\n");
		
		MessageHandler.debug("Updating database with existing experiments/VMs...");
		verifyDB();
		MessageHandler.debug("Updated database with existing experiments/VMs.\n");
		
		applicationMetrics.add("applicationmetric_1");
		applicationMetrics.add("applicationmetric_2");
		applicationMetrics.add("applicationmetric_3");
		applicationMetrics.add("applicationmetric_4");
		applicationMetrics.add("applicationmetric_5");
		siteCollectionTime.set(2001,2,2);
		phCollectionTime.set(2001,2,2);
		phCollectionTimeMedium.set(2001,2,2);
		phCollectionTimeLow.set(2001,2,2);
		vmCollectionTime.set(2001,2,2);
		vmCollectionTimeMedium.set(2001,2,2);
		vmCollectionTimeLow.set(2001,2,2);
		
		while (!stopCollecting) {
			
			// Get the operation start time in milliseconds. Used to calculate the duration of processing to reduce error on API calls for historical data
			startTime = System.currentTimeMillis();

			MessageHandler.debug("Metrics Collector awake.");
			
			MessageHandler.debug("Updating VMs without a Physical Host...");
			updatePhysicalHostlessVMs();
			MessageHandler.debug("Updated VMs without a Physical Host.");
			
			MessageHandler.debug("Updating database with any new sites or hosts...");
			updateDatabaseInfrastructureStatus();
			MessageHandler.debug("Updated database site and host records.");
			
			MessageHandler.debug("Updating site metrics...");
			updateDatabaseSitesMetrics();
			MessageHandler.debug("Updated site metrics.");
			
			MessageHandler.debug("Updating host metrics...");
			updateDatabaseHostsMetrics();
			MessageHandler.debug("Updated host metrics.");
			
			MessageHandler.debug("Updating virtual machines metrics...");
			updateDatabaseVMMetrics();
			MessageHandler.debug("Updated virtual machines metrics.");
			
			MessageHandler.debug("Metrics Collector sleeping...");
			
			// Wait until next round of polling
			try {
				System.gc();
				Thread.sleep(ConfigurationValues.pollingRate);
			} catch (InterruptedException e) {
				MessageHandler.error("MetricsCollector:run(): Got InterruptedException. Exception Message: " + e.getMessage() + ".");
				stopCollecting = true;
			}
		}
		
		tpes.shutdown();
		dbConnector.disconnect();
		stopCollecting = false;
	}
	
	private void buildUpUserNameGroupsRelationships() {
		Connection connection = null;
		boolean connected = false;
		String url = "localhost/e2c_scheduler";
		
		int counter = 0;
		while (!connected) {
			try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				connection = DriverManager.getConnection("jdbc:mysql://" + url + "?" + "user=" + ConfigurationValues.mysqlUsername + "&password=" + ConfigurationValues.mysqlPassword);
				connected = true;
				//return true;
			} catch (InstantiationException e) {
				MessageHandler.error("buildUpUserNameGroupsRelationships(): DB_ERROR: Monitoring Collector unable to connect to database server at " + url + ". Fatal. Discarding Data. InstantiationException. " + e.getMessage());
				//return false; // throw new RuntimeException(e);
			} catch (ClassNotFoundException e) {
				MessageHandler.error("buildUpUserNameGroupsRelationships(): DB_ERROR: Monitoring Collector unable to connect to database server at " + url + ". Fatal. Discarding Data. ClassNotFoundException. " + e.getMessage());
				//return false; // throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				MessageHandler.error("buildUpUserNameGroupsRelationships(): DB_ERROR: Monitoring Collector unable to connect to database server at " + url + ". Fatal. Discarding Data. IllegalAccessException. " + e.getMessage());
				//return false; // throw new RuntimeException(e);
			} catch (SQLException e) {
				MessageHandler.error("buildUpUserNameGroupsRelationships(): DB_ERROR: Monitoring Collector unable to connect to database server at " + url + ". Fatal. Discarding Data. SQLException. " + e.getMessage());
				//return false; // throw new RuntimeException(e);
			}
		
			if ( counter > 0) {
				MessageHandler.error("buildUpUserNameGroupsRelationships(): DB_ERROR: Monitoring Collector unable to connect to database server at " + url + ". Fatal. Retrying.");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					MessageHandler.error("buildUpUserNameGroupsRelationships(): Monitor died whilst sleeping.");
				}
			}
			
			counter++;
			if ( counter > 2 )
			{
				MessageHandler.error("buildUpUserNameGroupsRelationships(): DB_ERROR: Monitoring Collector unable to connect to database server at " + url + ". Fatal. Exiting without getting the required data.");
				MessageHandler.print("buildUpUserNameGroupsRelationships(): DB_ERROR: Monitoring Collector unable to connect to database server at " + url + ". Fatal. Exiting without getting the required data.");
				return;
			}
		}
		
		// Fetch the data
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			statement = connection.prepareStatement("SELECT bonfire_group_id, bonfire_user_id, bonfire_experiment_id from experiments " +
					   								"ORDER BY id;");
			
			results = statement.executeQuery();
			
			while (results.next()) {
				experimentsFromScheduler.add(new Experiment(results.getInt("bonfire_experiment_id"), "", results.getString("bonfire_group_id"), results.getString("bonfire_user_id")));
			}
			
		} catch (SQLException e) {
			MessageHandler.error("buildUpUserNameGroupsRelationships(): DB_ERROR: Error retrieving user names and groups. Exception: " + e.getMessage() + " Skipping.");
		}
		
		
		// Disconnect
		try { connection.close(); } catch (SQLException e) { 
			MessageHandler.error("buildUpUserNameGroupsRelationships(): DB_ERROR: Error closing connection (disconnect). Exception: " + e.getMessage() + " Skipping.");
		}
		
		MessageHandler.print("buildUpUserNameGroupsRelationships(): Found " + experimentsFromScheduler.size() + " experiments running from the Scheduler DB.");
		for (Experiment experiment: experimentsFromScheduler) {
			MessageHandler.print("buildUpUserNameGroupsRelationships(): Experiments in Scheduler DB: experiment (" + experiment.id + ")[aggr:" + experiment.aggregator_location + 
					", user:" + experiment.userName + ", group:" + experiment.groupName + "]." );
			boolean addEntry = true;
			for (String username: userNamesFromScheduler) {
				if ( username.equals(experiment.userName) )
				{
					addEntry = false;
					break;
				}
			}
			
			if ( !groupNames.contains(experiment.groupName) ) {
				addEntry = false;
				break;
			}
				
			if (addEntry)
				userNamesFromScheduler.add(new String(experiment.userName));
		}
		
		for (String username: userNamesFromScheduler) {
			MessageHandler.print("buildUpUserNameGroupsRelationships(): users that we will handle:" + username + "." );
		}
		
		results = null;
		statement = null;
		connected = false;
		connection = null;
	}
	
	public void printDetails() {
		MessageHandler.print("The Monitoring Collector is using " + ConfigurationValues.firstLevelOfThreads + " of first level threads and " + ConfigurationValues.secondLevelOfThreads + " second level threads.");
		
		countMetrics();
		int totalMetricsPH = highPriorityMetricsPHCounter + mediumPriorityMetricsPHCounter + lowPriorityMetricsPHCounter;
		MessageHandler.print("The Monitoring Collector is handling " + totalMetricsPH + " PH metrics.");
		MessageHandler.print("\t" + highPriorityMetricsPHCounter +  " are HIGH priority.");
		MessageHandler.print("\t" + mediumPriorityMetricsPHCounter +  " are Medium priority.");
		MessageHandler.print("\t" + lowPriorityMetricsPHCounter +  " are low priority.");
		MessageHandler.print("The Monitoring Collector is handling the following HIGH priority PH metrics:");
		for (int i = 0; i < highPriorityMetricsPHCounter ; i++) {
			MessageHandler.print("\t" + ConfigurationValues.highPriorityMetricsPH.get(i) + ".");
		}
		MessageHandler.print("The Monitoring Collector is handling the following Medium priority PH metrics:");
		for (int i = 0; i < mediumPriorityMetricsPHCounter ; i++) {
			MessageHandler.print("\t" + ConfigurationValues.mediumPriorityMetricsPH.get(i) + ".");
		}
		MessageHandler.print("The Monitoring Collector is handling the following low priority PH metrics:");
		for (int i = 0; i < lowPriorityMetricsPHCounter ; i++) {
			MessageHandler.print("\t" + ConfigurationValues.lowPriorityMetricsPH.get(i) + ".");
		}
		
		int totalMetricsVM = highPriorityMetricsVMCounter + mediumPriorityMetricsVMCounter + lowPriorityMetricsVMCounter;
		MessageHandler.print("The Monitoring Collector is handling " + totalMetricsVM + " VM metrics.");
		MessageHandler.print("\t" + highPriorityMetricsVMCounter +  " are HIGH priority.");
		MessageHandler.print("\t" + mediumPriorityMetricsVMCounter +  " are Medium priority.");
		MessageHandler.print("\t" + lowPriorityMetricsVMCounter +  " are low priority.");
		MessageHandler.print("The Monitoring Collector is handling the following HIGH priority VM metrics:");
		for (int i = 0; i < highPriorityMetricsVMCounter ; i++) {
			MessageHandler.print("\t" + ConfigurationValues.highPriorityMetricsVM.get(i) + ".");
		}
		MessageHandler.print("The Monitoring Collector is handling the following Medium priority VM metrics:");
		for (int i = 0; i < mediumPriorityMetricsVMCounter ; i++) {
			MessageHandler.print("\t" + ConfigurationValues.mediumPriorityMetricsVM.get(i) + ".");
		}
		MessageHandler.print("The Monitoring Collector is handling the following low priority VM metrics:");
		for (int i = 0; i < lowPriorityMetricsVMCounter ; i++) {
			MessageHandler.print("\t" + ConfigurationValues.lowPriorityMetricsVM.get(i) + ".");
		}
	}
	
	public void countMetrics() {
		
		for (String priority: ConfigurationValues.highPriorityMetricsPH) {
			if ( (priority != null) && (priority != "") && (priority.length() > 1))
				highPriorityMetricsPHCounter++;
		}
		for (String priority: ConfigurationValues.mediumPriorityMetricsPH) {
			if ( (priority != null) && (priority != "") && (priority.length() > 1))
				mediumPriorityMetricsPHCounter++;
		}
		for (String priority: ConfigurationValues.lowPriorityMetricsPH) {
			if ( (priority != null) && (priority != "") && (priority.length() > 1))
				lowPriorityMetricsPHCounter++;
		}
		for (String priority: ConfigurationValues.highPriorityMetricsVM) {
			if ( (priority != null) && (priority != "") && (priority.length() > 1))
				highPriorityMetricsVMCounter++;
		}
		for (String priority: ConfigurationValues.mediumPriorityMetricsVM) {
			if ( (priority != null) && (priority != "") && (priority.length() > 1))
				mediumPriorityMetricsVMCounter++;
		}
		for (String priority: ConfigurationValues.lowPriorityMetricsVM) {
			if ( (priority != null) && (priority != "") && (priority.length() > 1))
				lowPriorityMetricsVMCounter++;
		}
	}
	
	public void upgradeDB()  {
		ArrayList<Experiment> experimentsInDB = dbConnector.getAllExperiments();
		if (experimentsInDB == null) {
			MessageHandler.error("upgradeDB(): experimentsInDB is NULL.");
			return;			
		}
		
		MessageHandler.print("upgradeDB(): Got " + experimentsInDB.size() + " experiments from the DB for experiment.");
		
		if ( experimentsInDB.size() == 0 ) {
			MessageHandler.debug("upgradeDB(): no Experiments in DB.");
			return;
		}	
		
		for (Experiment experiment: experimentsInDB) {
			if ( (experiment.groupName != null) && (experiment.groupName.length() > 0) ) {
				MessageHandler.print("upgradeDB(): Experiment " + experiment.id + " has a groupName (" + experiment.groupName + ").");
			}
			else {
				MessageHandler.print("upgradeDB(): Experiment " + experiment.id + " does NOT have a groupName. Setting to eco2clouds.");
				boolean result = dbConnector.updateExperimentGroup(experiment.id, "eco2clouds");
			}
				
			if ( (experiment.userName != null) && (experiment.userName.length() > 0) ) {
				MessageHandler.print("upgradeDB(): Experiment " + experiment.id + " has a userName (" + experiment.userName + ").");
			}
			else {
				MessageHandler.print("upgradeDB(): Experiment " + experiment.id + " does NOT have a userName. Setting to eco2clouds.");	// eco2clouds is also a username
				boolean result = dbConnector.updateExperimentUser(experiment.id, "eco2clouds");	// eco2clouds is also a username
			}
		}
	}
	
	public void updatePhysicalHostlessVMs()
	{
		ArrayList<VirtualMachine> physicalHostlessVMs = dbConnector.getVirtualMachinesWithoutPhysicalHost();
		if ( physicalHostlessVMs == null ) {
			MessageHandler.error("updatePhysicalHostlessVMs(): physicalHostlessVMs is NULL.");
			return;
		}
		if ( physicalHostlessVMs.size() == 0 ) {
			MessageHandler.debug("updatePhysicalHostlessVMs(): no PhysicalHostless VMs.");
			return;
		}
		
		MessageHandler.debug("updatePhysicalHostlessVMs(): Got " + physicalHostlessVMs.size() + " physical hostless VMs from the DB.");
		
		for (VirtualMachine vm: physicalHostlessVMs) {
			
			Experiment experiment = dbConnector.getExperiment(vm.experimentId);
			if ( experiment == null ) {
				MessageHandler.error("updatePhysicalHostlessVMs(): could not find Experiment (NULL).");
				continue;
			}
			if ( !experiment.active ) {
				MessageHandler.debug("updatePhysicalHostlessVMs(): Experiment (" + experiment.id + ") is NOT active. Ignoring VM:" + vm.location + ". Continuing.");
				continue;
			}
			
			MessageHandler.debug("updatePhysicalHostlessVMs(): Checking experiment:" + vm.experimentId + ", experiment groupName:" + experiment.groupName + ", experiment userName:" + experiment.userName + ", vm:" + vm.id + ", location:" + vm.location + ", start_time:" + vm.start_time + ".");
			
			String host = getHostFromComputeXML(ConfigurationValues.bonfireAPI + vm.location, experiment.userName);
			if ( (host == null) || (host.length() < 3) ) {
				MessageHandler.error("updatePhysicalHostlessVMs(): host is NULL or empty. Skipping this VM (" + vm.id + "," + vm.location + ").");
				continue;
			}
			
			dbConnector.updateVMPhysicalHost(vm.id, vm.start_time, host);
			MessageHandler.print("updatePhysicalHostlessVMs(): Updated vm:" + vm.id + ", location:" + vm.location + ", start_time:" + vm.start_time + ", TO HOST:" + host + ".");
		}
	}
	
	/**
	 * Verifies that the DB contains the current list of active experiments.
	 */
	public void verifyDB()
	{
		verifyExperiments();
		
		ArrayList<Experiment> experimentsInDB = dbConnector.getActiveExperiments();
		if ( experimentsInDB == null) {
			MessageHandler.error("verifyDB(): experimentsInDB is NULL.");
			return;
		}
		
		for (Experiment experiment: experimentsInDB) {
			MessageHandler.debug("verifyDB(): Verifying experiment:" + experiment.id + ", aggregator_location:" + experiment.aggregator_location + ", user:" + experiment.userName + ", group:" + experiment.groupName + ".");
			verifyVMs(experiment);
			verifyExperimentLog(experiment);
			MessageHandler.debug("verifyDB(): Verified experiment:" + experiment.id + ", aggregator_location:" + experiment.aggregator_location + ", user:" + experiment.userName + ", group:" + experiment.groupName + ".");
		}
	}
	
	/**
	 * Parses the Log of an Experiment. If a Compute/VM has been created and deleted whilst we were DOWN, the Log will tell us when 
	 *   the compute/vm was created and destroyed. Modify the DB with the new/missing values.
	 * 
	 * @param Experiment the experiment
	 */
	private void verifyExperimentLog(Experiment experiment)
	{
		ArrayList<MMQEvent> logOfVMsOnCloud = getLogOfExperiment(experiment);	// Get the log of an Experiment from the Cloud.

		if (logOfVMsOnCloud == null ) {
			MessageHandler.error("verifyExperimentLog():Log is NULL!! Skipping.");
			return;
		}
		if ( logOfVMsOnCloud.size() == 0 ) {
			MessageHandler.debug("verifyExperimentLog():Log is EMPTY!! Skipping.");
			return;
		}
		
		MessageHandler.print("verifyExperimentLog(): Got " + logOfVMsOnCloud.size() + " events from the CLOUD for experiment:" + experiment.id + ", aggro:" + experiment.aggregator_location + ".");	// DEBUG
		
		ArrayList<VirtualMachine> VMsInDB = dbConnector.getAllVirtualMachinesOfExperiment(experiment.id);
		if ( VMsInDB == null ) {
			MessageHandler.error("verifyExperimentLog(): VMsInDB is NULL!! Skipping.");
			return;
		}
		MessageHandler.debug("verifyExperimentLog(): Got " + VMsInDB.size() + " VMs from the DB for experiment:" + experiment.id + ", aggro:" + experiment.aggregator_location + ".");	// DEBUG
		for (VirtualMachine vm :VMsInDB) {
			MessageHandler.debug("verifyExperimentLog(): " + vm.location + ".");	// DEBUG
		}
		
		// Run through the Events log and find if a COMPUTE has been created and destroyed.
		// If it has we must re-create the events in the DB.
		while ( logOfVMsOnCloud.size() > 0 ) {
			MMQEvent event = logOfVMsOnCloud.get(0);
			if (event == null) {
				MessageHandler.error("verifyExperimentLog():event is NULL!! Break.");				
				break;
			}
			
			String kind = event.getObjectType();
			if ( !kind.equals("compute") ) {
				MessageHandler.error("verifyExperimentLog():event in not a compute!! Remove and Continue.");
				logOfVMsOnCloud.remove(0);
				continue;
			}
			
			String status = event.getEventType();
			if ( status.equals("created")) {
				String path = event.getSource();
				if ( path == null || path.length() <= 5) {
					MessageHandler.error("verifyExperimentLog():event has a NULL path!! Remove and Continue.");
					logOfVMsOnCloud.remove(0);
					continue;
				}
				
				if ( logOfVMsOnCloud.size() == 1 ) {
					MessageHandler.debug("verifyExperimentLog(): Reached the last event in the log!! Continue.");	
					logOfVMsOnCloud.remove(0);
					continue;
				}
				
				MessageHandler.debug("verifyExperimentLog(): " + logOfVMsOnCloud.size() + " events in the list. Found event: kind=" + kind + ", status=" + status + ", path=" + path + ".");	// DEBUG	
				
				boolean foundMatch = false;	// Used to remove (or not) the first entry of the ArrayList.
				for (int i = 1 ; i < logOfVMsOnCloud.size() ; i++ ) {
					MessageHandler.debug("verifyExperimentLog(): Comparing with i:" + i + ".");	// DEBUG
					MMQEvent tempEvent = logOfVMsOnCloud.get(i);
					if ( tempEvent == null )
					{
						MessageHandler.debug("verifyExperimentLog():event [i:" + i + "] was NULL!! Continue.");	// Common case where the status is not created, destroyed, shutdown or running
						continue;
					}
					if ( tempEvent.getSource() == null || tempEvent.getSource().length() <= 5) {
						MessageHandler.error("verifyExperimentLog():event has a NULL Path!! Continue.");
						continue;
					}
					if ( tempEvent.getEventType() == null || tempEvent.getEventType().length() <= 5) {
						MessageHandler.error("verifyExperimentLog():event has a NULL Status!! Continue.");
						continue;
					}
					
					if ( tempEvent.getSource().equals(path) && tempEvent.getEventType().equals("destroyed") ) {
						foundMatch = true;	// Found a match. Since we are going to remove the first entry in for/loop, indicate that we MUST not remove it at the end of the loop (double removal).
						URI location;
						try {
							location = new URI(path);
							
							boolean foundVMinDB = false;
							for (VirtualMachine vm: VMsInDB) {
								if ( vm.location.equalsIgnoreCase(path)) {
									foundVMinDB = true;
									break;
								}
							}
							
							if ( foundVMinDB == true ) {
								MessageHandler.print("verifyExperimentLog(): [do NOT insertNewVM] for experiment:" + experiment.id + ", vm:" + location + ".");
							} else {
								MessageHandler.print("verifyExperimentLog(): [insertNewVM] for experiment:" + experiment.id + ".Create VM: Set start_date for vm:" + location + ", experiment ID:" + experiment.id + ", to:" + event.getTimestamp() + ".");
								dbConnector.insertNewVM(experiment.id, location, event.getTimestamp(), null, null);
								MessageHandler.print("verifyExperimentLog(): Terminate VM: Set end_date for vm:" + location + ", experiment ID:" + experiment.id + ", to:" + tempEvent.getTimestamp() + ".");
								dbConnector.terminateVM(experiment.id, location, tempEvent.getTimestamp(), "", "");	// Set the end_time of the VM to the timestamp.
							}
							logOfVMsOnCloud.remove(0);	// Remove first entry and break from the the for/loop.
							break;
							
						} catch (URISyntaxException e) {
							MessageHandler.error("verifyExperimentLog(): " + path + " is not a valid URI. Exiting....");
			    			return;
						}

					}
				}
				if ( !foundMatch )
					logOfVMsOnCloud.remove(0);		// Only remove the first entry if we did NOT find a match. If a match was found, the entry has been already removed.
			} else {
				logOfVMsOnCloud.remove(0);		// Remove the first entry, since the status is NOT "created".
			}
		}
	}
	
	/**
	 * Extracts events from an Event node (from an Experiment Log)
	 * 
	 * @param childNode an XML node, which contains ALL the events
	 * @return an MMQEvent (parsed from the XML Node).
	 */
	private MMQEvent extractEventFromEventNode(Node childNode)
	{
		MMQEvent tempEvent = new MMQEvent();
		
    	Node kindNode = getNode("kind", childNode.getChildNodes() );
        if (kindNode == null ) {
        	MessageHandler.error("extractEventFromEventNode():XML document is not correct. Could not find KIND tag!! Skipping.");
			return null;
        }
    	String kind = getNodeValue(kindNode);
    	if ( kind.length() <= 3 ) {
    		MessageHandler.error("extractEventFromEventNode():XML document is not correct. KIND tag is too short (" + kind + ")!! Skipping.");
			return null;
    	}
    	if ( !kind.equals("compute") ) {
    		return null;	// not a compute node!!!!        		
    	}
    	
    	Node statusNode = getNode("status", childNode.getChildNodes() );
        if (statusNode == null ) {
        	MessageHandler.error("extractEventFromEventNode():XML document is not correct. Could not find STATUS tag!! Skipping.");
			return null;
        }
    	String status = getNodeValue(statusNode);
    	if ( status.length() <= 1 ) {
    		MessageHandler.error("extractEventFromEventNode():XML document is not correct. STATUS tag zero length!! Skipping.");
			return null;
    	}
    	if ( !(status.equals("created") || status.equals("destroyed") || status.equals("shutdown") || status.equals("running"))) {
    		MessageHandler.debug("extractEventFromEventNode():XML document is not correct. Status (" + status + ") is not created, destroyed, shutdown or running!! Skipping.");
    		return null;	// not our compute node event!!!!        		
    	}
    	
    	Node pathNode = getNode("path", childNode.getChildNodes() );
        if (pathNode == null ) {
        	MessageHandler.error("extractEventFromEventNode():XML document is not correct. Could not find PATH tag!! Skipping.");
			return null;
        }
    	String path = getNodeValue(pathNode);
    	if ( path.length() <= 1 ) {
    		MessageHandler.error("extractEventFromEventNode():XML document is not correct. PATH tag zero length!! Skipping.");
			return null;
    	}
    	
    	Node timestampNode = getNode("timestamp", childNode.getChildNodes() );
        if (timestampNode == null ) {
        	MessageHandler.error("extractEventFromEventNode():XML document is not correct. Could not find TIMESTAMP tag!! Skipping.");
			return null;
        }
    	String time = getNodeValue(timestampNode);

    	
    	SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    	Date date;
		try {
			date = sdf.parse(time);
			long timeInMillisSinceEpoch = date.getTime(); 
        	
        	tempEvent.setObjectType(kind);		// Set the data properties of the MMQ event object.
        	tempEvent.setEventType(status);
        	tempEvent.setSource(path);
        	tempEvent.setTimestamp(timeInMillisSinceEpoch / 1000);
        	
        	return tempEvent;
		} catch (ParseException e) {
        	MessageHandler.error("extractEventFromEventNode():XML document is not correct. Could not parse timestamp:" + time + ". Exception Message: " + e.getMessage() );
			return null;
		} catch (Exception e){
			MessageHandler.error("extractEventFromEventNode():XML document is not correct. Could not parse timestamp:" + time + ", returned a Generic Exception [" + e.getMessage() + ". Exiting....");
		    return null;
		}
	}
	
	/**
	 * Gets the Log of an experiment
	 * 
	 * @param Experiment the experiment which we want to parse.
	 * @return an ArrayList of MMQEvents.
	 */
	private ArrayList<MMQEvent> getLogOfExperiment(Experiment experiment)
	{
		ArrayList<MMQEvent> logOfVMsOnCloud = new ArrayList<MMQEvent>();
		
		String URLString = ConfigurationValues.bonfireAPI + "/experiments/" + experiment.id + "/events";
		String Response = null;

		// Retrieve list of Experiments
		try {
			InputStream in = getURLContent(URLString, true, false, experiment.userName);
			Response = convertStreamToString(in);
			in.close();
		} catch (IOException e) {
			MessageHandler.error("getLogOfExperiment(): HTTP communication error attempting to gather Experiment from url: " + URLString + ". Exception Message: " + e.getMessage() + " Skipping.");
			return null;
		}
		
		// Build an XML file out of the response
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc = null;
	    try
	    {
	        builder = factory.newDocumentBuilder();
	        doc = builder.parse( new InputSource( new StringReader( Response ) ) );
	    } catch (Exception e) {
			MessageHandler.error("getLogOfExperiment():XML document [" + URLString + "] is not correct: " + Response + ". Exception Message: " + e.getMessage() + " Skipping.");
			return null;
	    }
	    
	    NodeList eventNodeList = doc.getElementsByTagName("event");
	    if ( eventNodeList == null || eventNodeList.getLength() == 0)
	    {
	    	MessageHandler.error("getLogOfExperiment():XML document [" + URLString + "] is not correct. Could NOT find event tag!! Skipping.");
			return null;
	    }
	    
	    for (int i = 0; i < eventNodeList.getLength(); i++)
        {
	    	Node childNode = eventNodeList.item(i);
	    	
	    	MMQEvent tempEvent = new MMQEvent();
	    	tempEvent = extractEventFromEventNode(childNode);
	    	logOfVMsOnCloud.add(tempEvent);
        }
	    
		return logOfVMsOnCloud;
	}
	
	private void verifyVMs(Experiment experiment)
	{
//		if ( experiment.aggregator_location != null)
//			if (experiment.aggregator_location.length() > 6 )
//				return;
		
		ArrayList<VirtualMachine> activeVMsInDB = dbConnector.getActiveVirtualMachinesOfExperiment(experiment.id);
		if ( activeVMsInDB == null ) {
			MessageHandler.error("verifyVMs(): activeVMsInDB is NULL. Skipping.");
			return;
		}
		
		MessageHandler.debug("verifyVMs(): Got " + activeVMsInDB.size() + " active VMs from the DB for experiment:" + experiment.id + ", aggro:" + experiment.aggregator_location + ".");	// DEBUG
		for (VirtualMachine vm :activeVMsInDB) {
			MessageHandler.debug("verifyVMs(): " + vm.location + ".");	// DEBUG
		}
		
		ArrayList<VirtualMachine> activeVMsOnCloud = new ArrayList<VirtualMachine>();
		String URLString = ConfigurationValues.bonfireAPI + "/experiments/" + experiment.id;
		String Response = null;

		// Retrieve list of VMs
		try {
			InputStream in = getURLContent(URLString, true, true, experiment.userName);
			Response = convertStreamToString(in);
			in.close();
		} catch (IOException e) {
			MessageHandler.error("verifyVMs(): HTTP communication error attempting to gather Experiment from url: " + URLString + ". Exception Message: " + e.getMessage() + " Skipping.");
			return;
		}

		// Build an XML file out of the response
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc = null;
	    try
	    {
	        builder = factory.newDocumentBuilder();
	        doc = builder.parse( new InputSource( new StringReader( Response ) ) );
	    } catch (Exception e) {
			MessageHandler.error("verifyVMs():XML document is not correct: " + Response + ". Exception Message: " + e.getMessage() + " Skipping.");
			return;
	    }
	    
	    NodeList computeNodeList = doc.getElementsByTagName("compute");
	    if (computeNodeList == null || computeNodeList.getLength() == 0) {
	    	MessageHandler.error("verifyVMs():" + URLString + " returned no compute node. Exiting....");
			return;
	    }
	    	
		for (int i = 0; i < computeNodeList.getLength(); ++i)
        {
        	Node childNode = computeNodeList.item(i);
        	
        	Node nameNode = getNode("name", childNode.getChildNodes() );
            if (nameNode == null ) {
    			MessageHandler.error("verifyVMs():" + URLString + " returned no name node. Continue....");
    			continue;
            }
        	String computeName = getNodeValue(nameNode);
        	if ( computeName.length() == 0) {
        		MessageHandler.error("verifyVMs():" + URLString + " returned zero length name node. Continue....");
        		continue;
        	}
        	
        	String tempHREF = getNodeAttr("href", childNode);
        	if (tempHREF == null || tempHREF.length() == 0) {
        		MessageHandler.error("verifyVMs():" + URLString + " returned no HREF node. Continue....");
        		continue;
        	}
        	URI href;
			try {
				href = new URI(tempHREF);
			} catch (URISyntaxException e) {
				MessageHandler.error("verifyVMs(): " + tempHREF + " is not a valid URI. Continue....");
				continue;
			}
			String site = "";
			if ( tempHREF.contains("/") )
			{
				String[] parts = tempHREF.split("/");
				if ( parts.length > 4)
					site = parts[2];
				else {
					MessageHandler.error("verifyVMs(): " + tempHREF + " is not a valid SITE URI.. Continue....");
					continue;
				}
			}
			else
			{
				MessageHandler.error("verifyVMs(): " + tempHREF + " is not a valid SITE URI. Continue....");
				continue;
			}
			
			String host = getHostFromComputeXML(ConfigurationValues.bonfireAPI + tempHREF, experiment.userName);
			if ( (host == null) || (host.length() < 3) )
			{
				MessageHandler.error("verifyVMs(): " + tempHREF + " is not a valid HOST URI. Continue....");
				continue;
			}

        	long timeCreate = getTimeFromExperimentEventsLogFile(ConfigurationValues.bonfireAPI + "/experiments/" + experiment.id + "/events", 
        														tempHREF, "created", experiment.id, experiment.userName);
        	if ( timeCreate == 0 ) {
        		MessageHandler.error("verifyVMs(): " + timeCreate + " is not a valid CREATE TIMESTAMP (" + ConfigurationValues.bonfireAPI + "/experiments/" + experiment.id + "/events) for:" + tempHREF + ". Exiting....");
        		continue;
        	}
        	
        	long timeDestroyed = getTimeFromExperimentEventsLogFile(ConfigurationValues.bonfireAPI + "/experiments/" + experiment.id + "/events", 
        															tempHREF, "destroyed", experiment.id, experiment.userName);
        	
        	if ( computeName.equalsIgnoreCase("BonFIRE-monitor"))
        	{
	        	dbConnector.setExperimentAggregator(experiment.id, (URI) href);
	        	MessageHandler.print("verifyVMs(): Updated aggregator for experiment " + experiment.id + ", to:" + href + "." );
        	}
        	
        	MessageHandler.debug("verifyVMs(): expID:" + experiment.id + ", href:" + href + ", timeCreate:" + timeCreate + ", timeDelete:" + timeDestroyed + ", host:" + host + ", site:" + site + "."); // DEBUG REMOVE
        	activeVMsOnCloud.add(new VirtualMachine(0, tempHREF, experiment.aggregator_location, host, timeCreate, timeDestroyed, experiment.id));
        }
		
		MessageHandler.debug("verifyVMs(): Got " + activeVMsOnCloud.size() + " active VMs from the Cloud for experiment:" + experiment.id + ", aggro:" + experiment.aggregator_location + ".");	// DEBUG
		for (VirtualMachine vm :activeVMsOnCloud) {
			MessageHandler.debug("verifyVMs(): " + vm.location + ".");	// DEBUG
		}
		
		// Check that the Experiments we have in the DB are still active in the Cloud.
		// If they are NOT active in the Cloud, make them inactive in the DB.
		for (VirtualMachine vm : activeVMsInDB) {
			MessageHandler.debug("verifyVMs(): comparing VM in DB:" + vm.id + ", " + vm.location + ", " + vm.aggregator_location + ", " + vm.site + "."); // DEBUG REMOVE
			
			Boolean foundVM = false;
			for (int i = 0 ; i < activeVMsOnCloud.size() ; i++ )
			{
				if ( activeVMsOnCloud.get(i).location.equals(vm.location) )
					foundVM = true;
			}
			
			// If we did not find a VM in the Cloud, it means that 
			//   the VM was deleted in the Cloud. We should also de-activate it
			if ( foundVM == false )
			{
				URI href;
				try {
					href = new URI(vm.location);
					
					dbConnector.terminateVM(experiment.id, href, vm.end_time, vm.Physical_Host_location, vm.site);
					MessageHandler.print("verifyVMs(): vm (" + experiment.id + ", location:" + vm.location + ", Physical_Host_location:" + vm.Physical_Host_location + ", site:" + vm.site + ", href:" + href + ") is now de-activated." );
				} catch (URISyntaxException e) {
					MessageHandler.error("verifyVMs():.. " + vm.location + " is not a valid URI. Did not manage to de-activate!!!! Exiting....");
					continue;
				}
			}			
		}
		
		activeVMsInDB.clear();
		activeVMsInDB = dbConnector.getActiveVirtualMachinesOfExperiment(experiment.id);
				
		// Check that the VM that are active in the Cloud are also in the DB
		for (VirtualMachine vm: activeVMsOnCloud) {
			MessageHandler.debug("verifyVMs(): comparing VM on CLOUD:" + vm.id + ", " + vm.location + ", " + vm.aggregator_location + ", " + vm.site + "."); // DEBUG REMOVE
			
			Boolean foundVM = false;
			for (int i = 0 ; i < activeVMsInDB.size() ; i++ )
			{
				if  ( activeVMsInDB.get(i).location.equals(vm.location) )
					foundVM = true;
			}
			
			// If we did not find an experiment in the DB, it means that 
			//   the experiment was added in the Cloud. We should also add in the DB
			if ( foundVM == false )
			{
				URI href;
				try {
					href = new URI(vm.location);
					
					MessageHandler.print("verifyVMs(): [insertNewVM] for experiment:" + experiment.id + ".Create VM:" + href + ", experiment ID:" + experiment.id + ", PhysicalHost:" + vm.Physical_Host_location + ", Site:" + vm.site + ", timestamp:" + vm.start_time + ".");
					dbConnector.insertNewVM(experiment.id, href, vm.start_time, vm.Physical_Host_location, vm.site);
					MessageHandler.print("verifyVMs(): vm (" + experiment.id + ", location:" + vm.location + ") is now activated." );
				} catch (URISyntaxException e) {
					MessageHandler.error("verifyVMs():.. " + vm.location + " is not a valid URI. Did not manage to activate!!!! Exiting....");
					continue;
				}
			}	
		}
	}

	@SuppressWarnings("unused")
	private long getTimeFromExperimentEventsLogFile(String URLString, String locationPath, String eventType, int experimentId, String userName)
	{
		String Response = null;

		// Retrieve list of Experiments
		try {
			InputStream in = getURLContent(URLString, true, false, userName);
			Response = convertStreamToString(in);
			in.close();
		} catch (IOException e) {
			MessageHandler.error("getTimeFromExperimentEventsLogFile(): HTTP communication error attempting to gather Experiment from url: " + URLString + ". Exception Message: " + e.getMessage() + " Skipping.");
			return 0;
		}
		
		// Build an XML file out of the response
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc = null;
	    try
	    {
	        builder = factory.newDocumentBuilder();
	        doc = builder.parse( new InputSource( new StringReader( Response ) ) );
	    } catch (Exception e) {
			MessageHandler.error("getTimeFromExperimentEventsLogFile():XML document [" + URLString + "] is not correct: " + Response + ". Exception Message: " + e.getMessage() + " Skipping.");
			return 0;
	    }
	    
	    NodeList eventNodeList = doc.getElementsByTagName("event");
	    if ( eventNodeList == null || eventNodeList.getLength() == 0)
	    {
	    	MessageHandler.error("getTimeFromExperimentEventsLogFile():XML document [" + URLString + "] is not correct. Could NOT find event tag!! Skipping.");
			return 0;
	    }
	    
	    for (int i = 0; i < eventNodeList.getLength(); i++)
        {
	    	Node childNode = eventNodeList.item(i);
	    	
        	Node kindNode = getNode("kind", childNode.getChildNodes() );
            if (kindNode == null ) {
            	MessageHandler.error("getTimeFromExperimentEventsLogFile():XML document [" + URLString + "] is not correct. Could not find KIND tag!! Skipping.");
    			return 0;
            }
        	String kind = getNodeValue(kindNode);
        	if ( kind.length() <= 3 ) {
        		MessageHandler.error("getTimeFromExperimentEventsLogFile():XML document [" + URLString + "] is not correct. KIND tag is too short (" + kind + ")!! Skipping.");
    			return 0;
        	}
        	if ( !kind.equals("compute") ) {
            	continue;	// not a compute node!!!!        		
        	}
        	
        	Node experimentIdNode = getNode("experiment_id", childNode.getChildNodes() );
            if (experimentIdNode == null ) {
            	MessageHandler.error("getTimeFromExperimentEventsLogFile():XML document [" + URLString + "] is not correct. Could not find experiment_id tag!! Skipping.");
    			return 0;
            }
            try {
	            int experiment = Integer.parseInt(getNodeValue(experimentIdNode));
	        	if ( experiment !=  experimentId) {
	            	continue;	// not our experiment!!!!		
	        	}
            }  catch (NumberFormatException e) {
    			MessageHandler.error("getTimeFromExperimentEventsLogFile(): ExperimentId node:" + getNodeValue(experimentIdNode) + ", returned an NumberFormatException Exception [" + e.getMessage() + ". Exiting....");
    		    return 0;
    		} catch (Exception e){
    			MessageHandler.error("getTimeFromExperimentEventsLogFile(): ExperimentId node:" + getNodeValue(experimentIdNode) + ", returned a Generic Exception [" + e.getMessage() + ". Exiting....");
    		    return 0;
    		 }
        	
        	Node statusNode = getNode("status", childNode.getChildNodes() );
            if (statusNode == null ) {
            	MessageHandler.error("getTimeFromExperimentEventsLogFile():XML document [" + URLString + "] is not correct. Could not find STATUS tag!! Skipping.");
    			return 0;
            }
        	String status = getNodeValue(statusNode);
        	if ( status.length() <= 1 ) {
        		MessageHandler.error("getTimeFromExperimentEventsLogFile():XML document [" + URLString + "] is not correct. STATUS tag zero length!! Skipping.");
    			return 0;
        	}
        	if ( !status.equals(eventType) ) {
            	continue;	// not our compute node event!!!!        		
        	}
        	
        	Node pathNode = getNode("path", childNode.getChildNodes() );
            if (pathNode == null ) {
            	MessageHandler.error("getTimeFromExperimentEventsLogFile():XML document [" + URLString + "] is not correct. Could not find PATH tag!! Skipping.");
    			return 0;
            }
        	String path = getNodeValue(pathNode);
        	if ( path.length() <= 1 ) {
        		MessageHandler.error("getTimeFromExperimentEventsLogFile():XML document [" + URLString + "] is not correct. PATH tag zero length!! Skipping.");
    			return 0;
        	}
        	if ( !path.equals(locationPath) ) {
            	continue;	// not our compute node!!!!        		
        	}
        	
        	Node timestampNode = getNode("timestamp", childNode.getChildNodes() );
            if (timestampNode == null ) {
            	MessageHandler.error("getTimeFromExperimentEventsLogFile():XML document [" + URLString + "] is not correct. Could not find TIMESTAMP tag!! Skipping.");
    			return 0;
            }
        	String time = getNodeValue(timestampNode);

        	
        	SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        	Date date;
			try {
				date = sdf.parse(time);
				long timeInMillisSinceEpoch = date.getTime(); 
	        	long timeInMinutesSinceEpoch = timeInMillisSinceEpoch / (60 * 1000);
	        	
	        	return timeInMillisSinceEpoch / 1000;
			} catch (ParseException e) {
            	MessageHandler.error("getTimeFromExperimentEventsLogFile():XML document [" + URLString + "] is not correct. Could not parse timestamp:" + time + ". Exception Message: " + e.getMessage() );
    			return 0;
			} catch (Exception e){
    			MessageHandler.error("getTimeFromExperimentEventsLogFile():XML document [" + URLString + "] is not correct. Could not parse timestamp:" + time + ", returned a Generic Exception [" + e.getMessage() + ". Exiting....");
    		    return 0;
    		 }
        }
	
		return 0;
	}
	
	@SuppressWarnings("unused")
	private String getHostFromComputeXML(String URLString, String userName)
	{
		String Response = null;

		// Retrieve list of Experiments
		try {
			InputStream in = getURLContent(URLString, true, true, userName);
			Response = convertStreamToString(in);
			in.close();
		} catch (IOException e) {
			MessageHandler.error("getHostFromComputeXML(): HTTP communication error attempting to gather compute from url: " + URLString + ". Exception Message: " + e.getMessage() + " Skipping.");
			return "";
		}

		// Build an XML file out of the response
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc = null;
	    try
	    {
	        builder = factory.newDocumentBuilder();
	        doc = builder.parse( new InputSource( new StringReader( Response ) ) );
	    } catch (Exception e) {
			MessageHandler.error("getHostFromComputeXML():XML document [" + URLString + "] is not correct: " + Response + ". Exception Message: " + e.getMessage() + " Skipping.");
			return "";
	    }
	    
	    NodeList computeNodeList = doc.getElementsByTagName("compute");
	    if ( computeNodeList == null )
	    {
	    	MessageHandler.error("getHostFromComputeXML():XML document [" + URLString + "] is not correct. Could NOT find compute tag!! Skipping.");
			return "";
	    }
	    if ( computeNodeList.getLength() >= 2 )
	    {
	    	MessageHandler.error("getHostFromComputeXML():XML document [" + URLString + "] is not correct. Found more than one computes tags!! Skipping.");
			return "";
	    }
	    else if ( computeNodeList.getLength() == 0 )
	    {
	    	MessageHandler.error("getHostFromComputeXML():XML document [" + URLString + "] is not correct. Found NONE compute tags!! Skipping.");
			return "";
	    }
	    
	    NodeList contextNodeList = doc.getElementsByTagName("context");
	    if ( contextNodeList == null )
	    {
	    	MessageHandler.error("getHostFromComputeXML():XML document [" + URLString + "] is not correct. Could NOT find context tag!! Skipping.");
			return "";
	    }
	    if ( contextNodeList.getLength() >= 2 )
	    {
	    	MessageHandler.error("getHostFromComputeXML():XML document [" + URLString + "] is not correct. Found more than one context tags!! Skipping.");
			return "";
	    }
	    else if ( contextNodeList.getLength() == 0 )
	    {
	    	MessageHandler.error("getHostFromComputeXML():XML document [" + URLString + "] is not correct. Found NONE context tags!! Skipping.");
			return "";
	    }
	    
	    for (int i = 0; i < contextNodeList.getLength(); i++)
        {
        	Node childNode = contextNodeList.item(i);
        	Node hostNodeContext = getNode("host", childNode.getChildNodes() );

            if (hostNodeContext == null ) {
            	MessageHandler.debug("getHostFromComputeXML():XML document [" + URLString + "] is not correct. Could not find host tag in context!! Skipping.");
            }
            else {
	        	String host = getNodeValue(hostNodeContext);
	        	if ( host.length() == 0) {
	        		MessageHandler.error("getHostFromComputeXML():" + URLString + " returned zero length host node from context. Exiting....");
	    			return "";
	        	}
	        	MessageHandler.debug("getHostFromComputeXML():XML document [" + URLString + "], found host:" + host + ".");
	        	
				if ( host.equalsIgnoreCase("NoSuitableHost") ) {
					MessageHandler.debug("getHostFromComputeXML(): host is NoSuitableHost. Returning NULL.");
					return "";
				}
	        	return host;
            }
        }
	    
	    for (int i = 0; i < computeNodeList.getLength(); i++)
        {
        	Node childNode = computeNodeList.item(i);
	    	Node hostNodeCompute = getNode("host", childNode.getChildNodes() );
        	
            if (hostNodeCompute == null ) {
            	MessageHandler.debug("getHostFromComputeXML():XML document [" + URLString + "] is not correct. Could not find host tag in compute!! Skipping.");
            }
            else {
	        	String host = getNodeValue(hostNodeCompute);
        	if ( host.length() == 0) {
	        		MessageHandler.error("getHostFromComputeXML():" + URLString + " returned zero length host node from compute. Exiting....");
    			return "";
        	}
	        	MessageHandler.debug("getHostFromComputeXML():XML document [" + URLString + "], found host:" + host + ".");
	        	
				if ( host.equalsIgnoreCase("NoSuitableHost") ) {
					MessageHandler.debug("getHostFromComputeXML(): host is NoSuitableHost. Returning NULL.");
					return "";
				}
        	return host;
        }
        }
	    
	    MessageHandler.error("getHostFromComputeXML():XML document [" + URLString + "] is not correct!!! Skipping.");
//	    MessageHandler.print("getHostFromComputeXML():XML document [" + URLString + "] is not correct!!! Skipping.");
		return "";
	}
	
	public ArrayList<Experiment> getExperimentInTheCloud(String userName)
	{
		MessageHandler.debug("getExperimentInTheCloud(): Get experiment in the Cloud for user (" + userName + ").");
		ArrayList<Experiment> experimentsInTheCloud = new ArrayList<Experiment>();
		
		String URLString = ConfigurationValues.bonfireAPI + "/experiments";
		String Response = null;

		try {
			InputStream in = getURLContent(URLString, true, true, userName);
			Response = convertStreamToString(in);
			in.close();
		} catch (IOException e) {
			MessageHandler.error("getExperimentInTheCloud(): HTTP communication error attempting to gather Experiments from url: " + URLString + ". Exception Message: " + e.getMessage() + " Skipping.");
			return null;
		}

		// Build an XML file out of the response
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder;
	    Document doc = null;
	    try
	    {
	        builder = factory.newDocumentBuilder();
	        doc = builder.parse( new InputSource( new StringReader( Response ) ) );
	    } catch (Exception e) {
			MessageHandler.error("getExperimentInTheCloud(): XML document is not correct: " + Response + ". Exception Message: " + e.getMessage() + " Skipping.");
			return null;
	    }

		NodeList experimentNodeList = doc.getElementsByTagName("experiment");
		MessageHandler.print("getExperimentInTheCloud(): Found " + experimentNodeList.getLength() + " experiments running on the Cloud, for user:" + userName + ".");
		for (int i = 0; i < experimentNodeList.getLength(); ++i)
        {
        	Node childNode = experimentNodeList.item(i);
        	
        	Node groupsNode = getNode("groups", childNode.getChildNodes() );
            if (groupsNode == null ) {
    			MessageHandler.error("getExperimentInTheCloud():" + URLString + " returned no groups node. Exiting....");
    			return null;
            }
        	String localGroupName = getNodeValue(groupsNode);
        	
        	Node experimentIDNode = getNode("id", childNode.getChildNodes() );
            if (experimentIDNode == null ) {
    			MessageHandler.error("getExperimentInTheCloud():" + URLString + " returned no ID node. Exiting....");
    			return null;
            }
            
        	Node userNode = getNode("user_id", childNode.getChildNodes() );
            if (userNode == null ) {
    			MessageHandler.error("getExperimentInTheCloud():" + URLString + " returned no user_id node. Exiting....");
    			return null;
            }
        	String localUserName = getNodeValue(userNode);
            
            try {
        	int experimentID = Integer.parseInt(getNodeValue(experimentIDNode));
            	MessageHandler.debug("getExperimentInTheCloud(): experimentID:" + experimentID + ", localGroupName:" + localGroupName + ", localUserName:" + localUserName + ", userName:" + userName + ".");
            	
            	boolean foundMatch = false;
        		
        		for (String group: groupNames) {
        			if ( (localGroupName.toLowerCase().startsWith(group.toLowerCase())) || 
        					(localGroupName.toLowerCase().equals(group.toLowerCase())) ) {
        				foundMatch = true;
        			}
        		}
        		
            	if ( foundMatch ) {
    	        	experimentsInTheCloud.add(new Experiment(experimentID, "", localGroupName, localUserName, true));
        	}
            } catch (NumberFormatException e) {
    			MessageHandler.error("getExperimentInTheCloud(): ExperimentId node:" + getNodeValue(experimentIDNode) + ", returned an NumberFormatException Exception [" + e.getMessage() + ". Exiting....");
    		    return null;
    		} catch (Exception e) {
    			MessageHandler.error("getExperimentInTheCloud(): ExperimentId node:" + getNodeValue(experimentIDNode) + ", returned a Generic Exception [" + e.getMessage() + ". Exiting....");
    		    return null;
    		}
        }
		
		MessageHandler.print("getExperimentInTheCloud(): Found " + experimentsInTheCloud.size() + " experiments running on the Cloud after removing non relevant experiments (invalid group), for user:" + userName + ".");
		for (Experiment experiment : experimentsInTheCloud) {
			MessageHandler.print("getExperimentInTheCloud(): Experiments in the Cloud: experiment (" + experiment.id + ", userName:" + experiment.userName + ", groupName:" + experiment.groupName + ", aggro:" + experiment.aggregator_location + ")." );
    		}
		
		return experimentsInTheCloud;
        }
		
	
	public void verifyExperiments()
	{
		// Get Active List of Experiments from DB
		ArrayList<Experiment> experimentsInDB = dbConnector.getActiveExperiments();
		if ( experimentsInDB == null) {
			MessageHandler.error("verifyExperiments(): experimentsInDB is NULL.");
			return;
		}
		MessageHandler.print("verifyExperiments(): Found " + experimentsInDB.size() + " ACTIVE experiments running in the DB.");
		for (Experiment experiment : experimentsInDB) {
			for (Experiment experimentScheduler : experimentsFromScheduler) {
				if ( experiment.id == experimentScheduler.id)  {
					if ( (experiment.userName == null) || (experiment.userName.length() < 1) ) {
						experiment.userName = experimentScheduler.userName;
						MessageHandler.debug("verifyExperiments(): Active Experiments in DB: experiment (" + experiment.id + ") has now userName set to :" + experiment.userName + "." );
						dbConnector.setExperimentUserName(experiment.id, experiment.userName);
					}
					if ( (experiment.groupName == null) || (experiment.groupName.length() < 1) ) {
						experiment.groupName = experimentScheduler.groupName;
						MessageHandler.debug("verifyExperiments(): Active Experiments in DB: experiment (" + experiment.id + ") has now groupName set to :" + experiment.groupName + "." );
						dbConnector.setExperimentGroupName(experiment.id, experiment.groupName);
					}
				}
			}
			MessageHandler.print("verifyExperiments(): Active Experiments in DB: experiment (" + experiment.id + ")[aggr:" + experiment.aggregator_location + 
					", user:" + experiment.userName + ", group:" + experiment.groupName + "]." );
		}
		
		// Get List of All Experiments from DB
		ArrayList<Experiment> experimentsNonActiveInDB = dbConnector.getAllExperiments();
		if ( experimentsNonActiveInDB == null) {
			MessageHandler.error("verifyExperiments(): experimentsNonActiveInDB is NULL.");
			return;
		}
		MessageHandler.print("verifyExperiments(): Found " + experimentsNonActiveInDB.size() + " (ALL) experiments in the DB.");
		for (Experiment experiment : experimentsNonActiveInDB) {
			for (Experiment experimentScheduler : experimentsFromScheduler) {
				if ( experiment.id == experimentScheduler.id)  {
					if ( (experiment.userName == null) || (experiment.userName.length() < 1) ) {
						experiment.userName = experimentScheduler.userName;
						MessageHandler.debug("verifyExperiments(): Experiments in DB: experiment (" + experiment.id + ") has now userName set to :" + experiment.userName + "." );
					}
					if ( (experiment.groupName == null) || (experiment.groupName.length() < 1) ) {
						experiment.groupName = experimentScheduler.groupName;
						MessageHandler.debug("verifyExperiments(): Experiments in DB: experiment (" + experiment.id + ") has now groupName set to :" + experiment.groupName + "." );
					}
				}
			}
			MessageHandler.print("verifyExperiments(): Experiments in DB: experiment (" + experiment.id + ")[aggr:" + experiment.aggregator_location + 
					", user:" + experiment.userName + ", group:" + experiment.groupName + "]." );
		}
		
		// Retrieve list of Experiments from the Cloud		
		ArrayList<Experiment> experimentsInTheCloud = new ArrayList<Experiment>();

		ArrayList<Experiment> localList = new ArrayList<Experiment>();
		localList = getExperimentInTheCloud("eco2clouds");	// eco2clouds is also a username
		
		if (localList != null) {
			experimentsInTheCloud.addAll(localList);
		} else {
			MessageHandler.error("verifyExperiments(): experiments in the cloud is NULL, for user name:" + "eco2clouds" + ".");
		}
		
		for (String username: userNamesFromScheduler) {
			localList = getExperimentInTheCloud(username);
			
			if (localList != null) {
				for(Experiment experimentFoundInTheCloud: localList) {
					boolean foundMatch = false;
					for (Experiment experimentAlreadyInTheList: experimentsInTheCloud) {
						if ( experimentFoundInTheCloud.id == experimentAlreadyInTheList.id ) {
							foundMatch = true;
							break;
						}
					}
					if (foundMatch) {
						MessageHandler.debug("verifyExperiments(): Experiment:" + experimentFoundInTheCloud.id + " is already in the list. Skipping!!!");
						continue;
					}
					
					for (String groupName : groupNames) {
						if (experimentFoundInTheCloud.groupName.equals(groupName)) {
							experimentsInTheCloud.add(experimentFoundInTheCloud);
							MessageHandler.debug("verifyExperiments(): Adding experiment:" + experimentFoundInTheCloud.id + " to the list.");
							continue;
						}
					}
				}
			} else {
				MessageHandler.error("verifyExperiments(): experiments in the cloud is NULL, for user name:" + username + ".");
			}
		}

		/*
		for (String group: groupNames) {
			ArrayList<Experiment> localList = new ArrayList<Experiment>();
			
			localList = getExperimentInTheCloud(group);
			
			if (localList != null) {
				experimentsInTheCloud.addAll(localList);
			} else {
				MessageHandler.error("verifyExperiments(): experiments in the cloud is NULL, for group name:" + group + ".");
			}
		}
		*/
		
		MessageHandler.print("verifyExperiments(): Found " + experimentsInTheCloud.size() + " experiments running on the Cloud.");
		for (Experiment experiment : experimentsInTheCloud) {
			for (Experiment experimentScheduler : experimentsFromScheduler) {
				if ( experiment.id == experimentScheduler.id)  {
					if ( (experiment.userName == null) || (experiment.userName.length() < 1) ) {
						experiment.userName = experimentScheduler.userName;
						MessageHandler.debug("verifyExperiments(): Experiments in Cloud: experiment (" + experiment.id + ") has now userName set to :" + experiment.userName + "." );
					}
					if ( (experiment.groupName == null) || (experiment.groupName.length() < 1) ) {
						experiment.groupName = experimentScheduler.groupName;
						MessageHandler.debug("verifyExperiments(): Experiments in Cloud: experiment (" + experiment.id + ") has now groupName set to :" + experiment.groupName + "." );
					}
				}
			}
			MessageHandler.print("verifyExperiments(): Experiments in Cloud: experiment (" + experiment.id + ")[aggr:" + experiment.aggregator_location + 
					", user:" + experiment.userName + ", group:" + experiment.groupName + "]." );
		}
		
		// Check that the Experiments we have in the DB are still active in the Cloud.
		// If they are NOT active in the Cloud, make them inactive in the DB.
		for (Experiment experiment : experimentsInDB) {
			Boolean foundExperiment = false;
			for (int i = 0 ; i < experimentsInTheCloud.size() ; i++ )
			{
				if ( experimentsInTheCloud.get(i).id == experiment.id )
					foundExperiment = true;
			}
			
			// If we did not find an experiment in the Cloud, it means that 
			//   the experiment was deleted in the Cloud. We should also de-activate it
			if ( foundExperiment == false )	{
				MessageHandler.print("verifyExperiments(): Pass A: experiment (" + experiment.id + ") is now de-activated." );
				dbConnector.setExperimentTerminated(experiment.id);
			}			
			else {
				MessageHandler.debug("verifyExperiments(): Pass A: experiment (" + experiment.id + ") is not modified." );
			}
		}
		
		// Check that the Experiments that are active in the Cloud are also in the DB
		for (Experiment experiment : experimentsInTheCloud) {
			Boolean foundExperiment = false;
			for (int i = 0 ; i < experimentsInDB.size() ; i++ )
			{
				if ( experimentsInDB.get(i).id == experiment.id )
					foundExperiment = true;
			}
			
			// If we did not find an experiment in the DB, it means that 
			//   the experiment was added in the Cloud. We should also add in the DB
			if ( foundExperiment == false )	{
				// If however, the experiment is in the NON Active list, it means that for some reason (manually??)
				//   the experiment was set as INACTIVE in the DB. THerefore, do NOT insert a new Experiment.
				//   Rather, update the existing experiment to ACTIVE
				Boolean foundNonActiveExperiment = false;
				for (int i = 0 ; i < experimentsNonActiveInDB.size() ; i++ )
				{
					if ( experimentsNonActiveInDB.get(i).id == experiment.id )
						foundNonActiveExperiment = true;
				}
				
				if (foundNonActiveExperiment) {
				MessageHandler.print("verifyExperiments(): Pass B: experiment (" + experiment.id + ") is now active." );
					dbConnector.setExperimentActive(experiment.id);
				} else {
					MessageHandler.print("verifyExperiments(): Pass B: experiment (" + experiment.id + ") is now active and ADDED." );
					dbConnector.insertNewExperiment(experiment.id, experiment.groupName, experiment.userName);
				}
			} else {
				MessageHandler.debug("verifyExperiments(): Pass B: experiment (" + experiment.id + ") is not modified." );
			}						
		}
	}
	
	/**
	 * Stops this collector and shuts down. Expected to be called from a thread other than that of the collector.
	 */
	public void stop() {
		stopCollecting = true;
	}
	
	// ---Infrastructure layer metrics---
	/**
	 * Updates both site and host listings in the database - functionally calls <code>updateDatabaseSitesStatus()</code> followed by <code>updateDatabaseHostsStatus()</code>.
	 */
	public void updateDatabaseInfrastructureStatus() {
		MessageHandler.debug("Updating Database Infrastructure Status." );
		updateDatabaseSitesStatus();
		updateDatabaseHostsStatus();
		MessageHandler.debug("Updated Database Infrastructure Status." );
	}
	
	/**
	 * Updates the database with the existence of all hosts. (i.e. Their entries in the "metrics_sites" table in the database.):
	 *  Gets the list of sites from the accompanying .properties file (via the ConfigurationValues class).
	 *  Updates the accounting database (via the DatabaseConnector) with the supplied site names.
	 */
	public void updateDatabaseSitesStatus() {	
		String[] sites = getSites();
		
		if ( sites == null) {
			MessageHandler.error("updateDatabaseSitesStatus(): getSites() returned NULL. Exiting!!!");
		    return;
		}
		
		//MessageHandler.debug("updateDatabaseSitesStatus(): Got " + sites.length + " sites." ); // DEBUG
		//for (String temp: sites) {
			//MessageHandler.debug("updateDatabaseSitesStatus(): Inserting site:" + temp + "." ); // DEBUG
		//}
		
		dbConnector.insertNewSites(sites);
	}
	
	public boolean checkMetricsBasedOnInterval(Calendar temp, int interval) {
		
		Calendar currentTime = Calendar.getInstance();
		Date siteCollectionDate = temp.getTime();
		Date currentDate = currentTime.getTime();
		long l1 = siteCollectionDate.getTime();
		long l2 = currentDate.getTime();
		
		MessageHandler.debug("checkMetricsBasedOnInterval(): last Collection date:" + siteCollectionDate + ", current time:" + currentDate + ", interval:" + interval + ", current interval:" + (l2 - l1) + ".");
		
		if ( l2 - l1 <= interval ) {
			return false;
		}
			
		return true;
	}
	
	/**
	 * Updates the database with all sites metrics:
	 * Gets the list of sites from the accompanying .properties file (via the ConfigurationValues class).
	 * Gets the list of metric aliases for a site from the BonFIRE Zabbix API.
	 * Calls the API for the value of each metric !!!One call per metric per site. Serious QoS concerns.!!!.
	 * Repeats for all sites.
	 * Updates the accounting database (via the DatabaseConnector) with all gathered metrics.
	 */
	public void updateDatabaseSitesMetrics() {
		
		ArrayList<Site> siteResources = new ArrayList<Site>();
		
		if ( !checkMetricsBasedOnInterval(siteCollectionTime, 1800000) )
		{
			MessageHandler.debug("updateDatabaseSitesMetrics(): Skipping collection of data. Less than 30 minutes." ); // DEBUG
			return;
		}
		
		String[] sites = getSites();

		for (String site : sites) {
			// Get all metrics aliases for a site
			String aliasesURL = ConfigurationValues.bonfireAPI + "/locations/" + site + "/locationmetrics/aliases";

			MessageHandler.debug("updateDatabaseSitesMetrics(): aliasesURL:" + aliasesURL + "." ); // DEBUG

			ArrayList<String> aliases = getAliases(aliasesURL, site);
			
			MessageHandler.debug("updateDatabaseSitesMetrics(): aliases:" + aliases + "." ); // DEBUG

			// Get all site metrics
			ArrayList<Item> items = new ArrayList<Item>();
			for (String metric : aliases) {
				// Get all values of this metric recorded in Zabbix while the Collector was asleep.
				// Call is for history_metrics/(pollingRate + processingTime).
				// Since history_metrics precision is only available in seconds, rounding up may result in duplicate metrics being gathered.
				// The SQL statements for inserting Items data ensure any duplicates are not recorded in the metrics database.
				long processingTime = System.currentTimeMillis() - startTime;
				@SuppressWarnings("unused")
				long time = ConfigurationValues.pollingRate + (long)Math.ceil( (processingTime / 1000.0) );
				
				//String metricURL = aliasesURL + "/" + metric + "/history_metrics/" + time;
				String metricURL = aliasesURL + "/" + metric; // Only gets single latest value, chance for a metric to be missed 
				
				MessageHandler.debug("updateDatabaseSitesMetrics(): \t metricURL:" + metricURL +", metric:" + metric + "." ); // DEBUG
							
				ArrayList<Item> itemHistory = getMetric(metricURL, metric);
				
				if ( ConfigurationValues.loggingLevel == Level.DEBUG )
					for( Item localItem : itemHistory )
							MessageHandler.debug("updateDatabaseSitesMetrics(): \t\t metric:" + metric + ", \tItem(name:" + localItem.name + ", zabbix_itemid:" + localItem.zabbix_itemid + ", clock:" + localItem.clock + ", value:" + localItem.value + ", unity:" + localItem.unity); // DEBUG
				
				items.addAll(itemHistory);
			}
			
			double GreenTotal = -1.0, Total = -1.0, GEC = -1.0;
			long clock = -1;
			String unity = "";
			
			for(int i=0;i<items.size();i++)	{
		        if( items.get(i).name.equals("TotalGreen"))	{
		        	GreenTotal = items.get(i).value;
		        }
		        else if( items.get(i).name.equals("Total"))	{
		        	Total = items.get(i).value;
		        	clock = items.get(i).clock;
		        	unity = items.get(i).unity;
		        } 
			}

			if ( GreenTotal >= 0.0 && Total >= 0.0 )
			{
				if ( Total > 0.0)
					GEC = GreenTotal / Total;
				else
					GEC = 0.0;
			
				NumberFormat number = NumberFormat.getNumberInstance();
				number.setMaximumFractionDigits(5);
				String snum = number.format(GEC);
				GEC = Double.parseDouble(snum);
			
				Item derivedItem = new Item("GEC", 1, clock, GEC, unity );
				items.add(derivedItem);
				MessageHandler.debug("updateDatabaseSitesMetrics(): adding GEC:" + GEC + "." ); // DEBUG
			}
			
			Site newSite = new Site(site);
			newSite.items = items;
			siteResources.add(newSite);
			
			siteCollectionTime = Calendar.getInstance();	// Set the new siteCollectionTime time
		}
		
		dbConnector.updateMetrics(siteResources,"sites");
	}
		
	/**
	 * Updates the database with the existence of all hosts (i.e. Their entries in the "metrics_physical_hosts" table in the database) assuming associated site record already exists:
	 * Calls the one-status URL defined in the accompanying .properties file for the current list of physical host machines at each site.
	 * Updates the database with the gathered host names.
	 */
	public void updateDatabaseHostsStatus() {
				
		String[][] hostInfo = ConfigurationValues.hostInfo;
		for (int i=0; i<hostInfo.length; i++) {
			String siteName = hostInfo[i][0];
			String oneStatusURL = hostInfo[i][1];
			
			// Get all hosts associated with each site by querying the site's OpenNebula status page.
			String[] hosts = getOneStatus(oneStatusURL);
			
			if ( hosts == null) {
				MessageHandler.error("updateDatabaseHostsStatus(): getOneStatus() returned NULL. Exiting!!!");
			    return;
			}
			
			//MessageHandler.debug("updateDatabaseHostsStatus(): Got " + hosts.length + " hosts." ); // DEBUG
			//for (String temp: hosts) {
			//	MessageHandler.debug("updateDatabaseHostsStatus(): Inserting host:" + temp + "." ); // DEBUG
			//}
			
			if (hosts != null) {
				dbConnector.insertNewPhysicalHosts(hosts, siteName);
			}
		}
		
	}
		
	private ArrayList<String> fixAliases(ArrayList<String> inputAliases, 
			List<String> highPriorityList, List<String> mediumPriorityList, List<String> lowPriorityList,
			boolean checkMediumPriority, boolean checkLowPriority, boolean isPH) {
		ArrayList<String> aliases = new ArrayList<String>();
		
		for (String alias : inputAliases) {
			if (highPriorityList.contains(alias)) {
				MessageHandler.debug("fixAliases(): Adding alias :" + alias + " to list. Found in HIGH Priority List."); // DEBUG
				aliases.add(alias);
			}
			else if ( mediumPriorityList.contains(alias) && checkMediumPriority ) {
				MessageHandler.debug("fixAliases(): Adding alias :" + alias + " to list. Found in Medium Priority List and it is time."); // DEBUG
				aliases.add(alias);
			}
			else if ( lowPriorityList.contains(alias) && checkLowPriority ) {
				MessageHandler.debug("fixAliases(): Adding alias :" + alias + " to list. Found in low Priority List and it is time."); // DEBUG
				aliases.add(alias);
			}
			
			if (!highPriorityList.contains(alias) && !mediumPriorityList.contains(alias) && !lowPriorityList.contains(alias)) {
				MessageHandler.debug("fixAliases(): Adding alias :" + alias + " to list. Not found in ANY LIST. Adding to the HIGH Priority list."); // DEBUG
				MessageHandler.error("fixAliases(): Adding alias :" + alias + " to list. Not found in ANY LIST. Adding to the HIGH Priority list."); // DEBUG
				aliases.add(alias);
				if (isPH)
					ConfigurationValues.highPriorityMetricsPH.add(new String(alias));
				else
					ConfigurationValues.highPriorityMetricsVM.add(new String(alias));
			}
		}
		
		return aliases;
	}
	/**
	 * Update the database with all gathered (physical) hosts metrics:
	 * Gets the list of host records from the accounting database (via the DatabaseConnector).
	 * API http://wiki.bonfire-project.eu/index.php/Monitoring_Ecometrics_specs for collecting metrics for an individual physical machine.
	 * Updates the accounting database (via the DatabaseConnector) with all gathered metrics.
	 */
	public void updateDatabaseHostsMetrics() {
		
		ArrayList<PhysicalHost> hosts = dbConnector.getPhysicalHosts();
		
		if ( ConfigurationValues.loggingLevel == Level.DEBUG )
			for( PhysicalHost localPhysicalHostItem : hosts )
				MessageHandler.debug("updateDatabaseHostsMetrics(): host.location:" + localPhysicalHostItem.location + ", site:" + localPhysicalHostItem.site + ", id:" + localPhysicalHostItem.id + "." ); // DEBUG

		boolean checkMediumPriority = checkMetricsBasedOnInterval(phCollectionTimeMedium, ConfigurationValues.mediumPriorityPHInterval);
		if (checkMediumPriority)
			phCollectionTimeMedium = Calendar.getInstance();
		boolean checkLowPriority = checkMetricsBasedOnInterval(phCollectionTimeLow, ConfigurationValues.lowPriorityPHInterval);
		if (checkLowPriority)
			phCollectionTimeLow = Calendar.getInstance();
		
		MessageHandler.debug("updateDatabaseHostsMetrics(): checkMediumPriority:" + checkMediumPriority + ", checkLowPriority:" + checkLowPriority + "." );
		
		int i = 0;
		for (PhysicalHost host : hosts) {
			String aliasesURLString = ConfigurationValues.bonfireAPI + "/locations/" + host.site + "/hostmetrics";
			
			MessageHandler.debug("updateDatabaseHostsMetrics(): \thost:" + host.location + ", aliasesURLString:" + aliasesURLString + "." ); // DEBUG
			
			ArrayList<String> aliases = getAliasesMetric(aliasesURLString, host.site);

			MessageHandler.debug("updateDatabaseHostsMetrics(): aliases:" + aliases + "." ); // DEBUG
			aliases = fixAliases(aliases, ConfigurationValues.highPriorityMetricsPH, ConfigurationValues.mediumPriorityMetricsPH, ConfigurationValues.lowPriorityMetricsPH, checkMediumPriority, checkLowPriority, true);
			MessageHandler.debug("updateDatabaseHostsMetrics(): aliases:" + aliases + "." ); // DEBUG

			// Hack to find the "short name" of a host.
			String hostLocation = "";
			if ( host.location.contains(".") )
			{
				String[] parts = host.location.split("\\.");
				hostLocation = parts[0];
			}
			else
				hostLocation = host.location;
			
			String metricURL = ConfigurationValues.bonfireAPI + "/locations/" + host.site + "/hosts/" + hostLocation + "/hostmetrics/aliases";  // Only gets single latest value, chance for a metric to be missed
			
			workers[i] = new MetricsCollectorThread(aliases, i, sslSocketFactory, startTime, metricURL, metricURL, System.currentTimeMillis());
			futures[i] = tpes.submit(workers[i]);
			i++;

			/*
			ArrayList<Item> items = new ArrayList<Item>();
			for (String metric : aliases) {
				// Get all values of this metric recorded in Zabbix while the Collector was asleep.
				// Call is for <URL>/history_metrics/(pollingRate + processingTime).
				// Since history_metrics precision is only available in seconds, rounding up may result in duplicate metrics being gathered.
				// The SQL statements for inserting Items data ensure any duplicates are not recorded in the metrics database.  
				long processingTime = System.currentTimeMillis() - startTime;
				@SuppressWarnings("unused")
				long time = ConfigurationValues.pollingRate + (long)Math.ceil( (processingTime / 1000.0) );

				// Hack to find the "short name" of a host.
				String hostLocation = "";
				if ( host.location.contains(".") )
				{
					String[] parts = host.location.split("\\.");
					hostLocation = parts[0];
				}
				else
					hostLocation = host.location;
				
				String metricURL = ConfigurationValues.bonfireAPI + "/locations/" + host.site + "/hosts/" + hostLocation + "/hostmetrics/aliases/" + metric;  // Only gets single latest value, chance for a metric to be missed
				
				MessageHandler.debug("updateDatabaseHostsMetrics(): \t\thost:" + host.location + ",metricURL:" + metricURL +", metric:" + metric + "." ); // DEBUG
				
				ArrayList<Item> itemHistory = getMetric(metricURL, metric);
				
				if ( ConfigurationValues.loggingLevel == Level.DEBUG )
					for( Item localItem : itemHistory )
							MessageHandler.debug("updateDatabaseHostsMetrics(): \t\t\t metric:" + metric + ", \tItem(name:" + localItem.name + ", zabbix_itemid:" + localItem.zabbix_itemid + ", clock:" + localItem.clock + ", value:" + localItem.value + ", unity:" + localItem.unity); // DEBUG

				items.addAll(itemHistory);
			}
			host.items = items;
			*/
		}
		
		i = 0;
		for (PhysicalHost host : hosts) {
			try {
				ArrayList<Item> itemHistory = (ArrayList<Item>) futures[i].get();
				if ( itemHistory == null ) {
					// Hack to find the "short name" of a host.
					String hostLocation = "";
					if ( host.location.contains(".") )
					{
						String[] parts = host.location.split("\\.");
						hostLocation = parts[0];
					}
					else
						hostLocation = host.location;
					
					MessageHandler.error("updateDatabaseHostsMetrics(): " + i + "[" + ConfigurationValues.bonfireAPI + "/locations/" + host.site + "/hosts/" + hostLocation + "/hostmetrics/aliases" + "], returned itemHistory is NULL!!!"); // DEBUG
				}
			}
			catch (Exception e) {
				MessageHandler.error("updateDatabaseHostsMetrics(): " + i + "[" + ConfigurationValues.bonfireAPI + "/locations/" + host.site + "/hostmetrics" + "], returned itemHistory is NULL!!!"); // DEBUG
			}
			i++;
		}
		
		i = 0;
		for (PhysicalHost host : hosts) {
            try {
            	ArrayList<Item> items = new ArrayList<Item>();
            	
            	// Hack to find the "short name" of a host.
    			String hostLocation = "";
    			if ( host.location.contains(".") )
    			{
    				String[] parts = host.location.split("\\.");
    				hostLocation = parts[0];
    			}
    			else
    				hostLocation = host.location;
    			
            	String aliasesURLString = ConfigurationValues.bonfireAPI + "/locations/" + host.site + "/hosts/" + hostLocation + "/hostmetrics/aliases";
            	MessageHandler.debug("updateDatabaseHostsMetrics(): " + i + ", URLString: " + aliasesURLString + "."); // DEBUG
            	
            	ArrayList<Item> itemHistory = (ArrayList<Item>) futures[i].get();
				if ( ConfigurationValues.loggingLevel == Level.DEBUG )
					for( Item localItem : itemHistory )
							MessageHandler.debug("updateDatabaseHostsMetrics(): \t\t\t PH_ID:" + host.id + ", metric:" + ", \tItem(name:" + localItem.name + ", zabbix_itemid:" + localItem.zabbix_itemid + ", clock:" + localItem.clock + ", value:" + localItem.value + ", unity:" + localItem.unity); // DEBUG

            	items.addAll(itemHistory);
            	host.items = items;
            } catch (Exception e) {
            	MessageHandler.error("updateDatabaseHostsMetrics(): " + i + "[" + ConfigurationValues.bonfireAPI + "/locations/" + host.site + "/hostmetrics" + "], returned ERROR!!!"); // DEBUG
            }
            
            workers[i] = null;	// Help GC
			futures[i] = null;	// Help GC
            i++;
            phCollectionTime = Calendar.getInstance();	// Set the new phCollectionTime time
		}

		dbConnector.updateMetrics(hosts, "physical_hosts");
	}
	
	// ---Virtual Machine layer metrics---	
	/**
	 * Update the database with all gathered virtual machine metrics:
	 * Gets the list of VMs associated with an active experiment from the accounting database (via the DatabaseConnector).
	 * Gets the list of metric aliases for a VM from the BonFIRE Zabbix API.
	 * Calls the API for the value of each metric !!!One call per metric per site. Serious QoS concerns.!!!.
	 * Repeats for all VMs.
	 * Updates the accounting database (via the DatabaseConnector) with all gathered metrics.
	 */
	public void updateDatabaseVMMetrics() {
		ArrayList<VirtualMachine> vms = dbConnector.getActiveVirtualMachines();
		
		MessageHandler.debug("updateDatabaseVMMetrics(): Found " + vms.size() + " VMs." ); // DEBUG
			for( VirtualMachine vm : vms )
				MessageHandler.debug("updateDatabaseVMMetrics(): vms.location:" + vm.location + ", vms.site:" + vm.site + ", vms.aggregator_location:" + vm.aggregator_location +"." ); // DEBUG
		
		boolean checkMediumPriority = checkMetricsBasedOnInterval(vmCollectionTimeMedium, ConfigurationValues.mediumPriorityVMInterval);
		if (checkMediumPriority)
			vmCollectionTimeMedium = Calendar.getInstance();	// Set the new vmCollectionTimeMedium time
		boolean checkLowPriority = checkMetricsBasedOnInterval(vmCollectionTimeLow, ConfigurationValues.lowPriorityVMInterval);
		if (checkLowPriority)
			vmCollectionTimeLow = Calendar.getInstance();	// Set the new vmCollectionTimeMedium time
		
		MessageHandler.debug("updateDatabaseHostsMetrics(): checkMediumPriority:" + checkMediumPriority + ", checkLowPriority:" + checkLowPriority + "." );
		
		int i = 0;
		for (VirtualMachine vm : vms) {

			String aliasesURLString = ConfigurationValues.bonfireAPI + vm.location + "/vmmetrics/aliases";
			ArrayList<String> aliases = getAliases(aliasesURLString, vm.site);
			
			MessageHandler.debug("updateDatabaseVMMetrics(): URLString: " + aliasesURLString + ", aliases:" + aliases + "." ); // DEBUG
			MessageHandler.debug("updateDatabaseVMMetrics(): aliases:" + aliases + "." ); // DEBUG
			aliases = fixAliases(aliases, ConfigurationValues.highPriorityMetricsVM, ConfigurationValues.mediumPriorityMetricsVM, ConfigurationValues.lowPriorityMetricsVM, checkMediumPriority, checkLowPriority, false);
			MessageHandler.debug("updateDatabaseVMMetrics(): aliases:" + aliases + "." ); // DEBUG			
			aliases.add("applicationmetric_1");
			
			workers[i] = new MetricsCollectorThread(aliases, i, sslSocketFactory, startTime, aliasesURLString, ConfigurationValues.bonfireAPI + vm.location + "/vmmetrics/raws", System.currentTimeMillis());
			futures[i] = tpes.submit(workers[i]);
			i++;
			
				// TODO:
/*
			ArrayList<Item> items = new ArrayList<Item>();
			for (String metric : aliases) {	
				
				// Get all values of this metric recorded in Zabbix while the Collector was asleep.
				// Call is for <URL>/history_metrics/(pollingRate + processingTime).
				// Since history_metrics precision is only available in seconds, rounding up may result in duplicate metrics being gathered.
				// The SQL statements for inserting Items data ensure any duplicates are not recorded in the metrics database.  
				long processingTime = System.currentTimeMillis() - startTime;
				@SuppressWarnings("unused")
				long time = ConfigurationValues.pollingRate + (long)Math.ceil( (processingTime / 1000.0) );

				//String metricURL = aliasesURLString + "/" + metric + "/history_metrics/" + time;
				String metricURL = aliasesURLString + "/" + metric;  // Only gets single latest value, chance for a metric to be missed
				
				MessageHandler.debug("updateDatabaseVMMetrics(): \tmetric:" + metric + ", path:" +  metricURL); // DEBUG
				
				ArrayList<Item> itemHistory = getMetric(metricURL, metric);
				
				if ( ConfigurationValues.loggingLevel == Level.DEBUG )
					for( Item localItem : itemHistory )
							MessageHandler.debug("updateDatabaseVMMetrics(): \t\t\t metric:" + metric + ", \tItem(name:" + localItem.name + ", zabbix_itemid:" + localItem.zabbix_itemid + ", clock:" + localItem.clock + ", value:" + localItem.value + ", unity:" + localItem.unity); // DEBUG
				
				items.addAll(itemHistory);
			}
			
			vm.items = items;			
*/
		}
		
		i = 0;
		for (VirtualMachine vm : vms) {
			try {
				ArrayList<Item> itemHistory = (ArrayList<Item>) futures[i].get();
				if ( itemHistory == null ) {
					MessageHandler.error("updateDatabaseVMMetrics(): " + i + "[" + ConfigurationValues.bonfireAPI + vm.location + "/vmmetrics/aliases" + "], returned itemHistory is NULL!!!"); // DEBUG
				}
			}
			catch (Exception e) {
				MessageHandler.error("MetricsCollector:updateDatabaseVMMetrics(): Got Exception. Exception Message: " + e.getMessage() + ".");
			}
			i++;
		}
		
		i = 0;
		for (VirtualMachine vm : vms) {
            try {
            	ArrayList<Item> items = new ArrayList<Item>();
            	String aliasesURLString = ConfigurationValues.bonfireAPI + vm.location + "/vmmetrics/aliases";
            	MessageHandler.debug("updateDatabaseVMMetrics(): " + i + ", URLString: " + aliasesURLString + "."); // DEBUG
            	
            	ArrayList<Item> itemHistory = (ArrayList<Item>) futures[i].get();
					for( Item localItem : itemHistory )
							MessageHandler.debug("updateDatabaseVMMetrics(): \t\t\t VM_ID:" + vm.id + ", metric:" + ", \tItem(name:" + localItem.name + ", zabbix_itemid:" + localItem.zabbix_itemid + ", clock:" + localItem.clock + ", value:" + localItem.value + ", unity:" + localItem.unity); // DEBUG

				// Application specific metrics
				// applicationmetric_1,  applicationmetric_2,  applicationmetric_3,  applicationmetric_4,  applicationmetric_5 
				// GET /locations/<location_id>/computes/<compute_id>/hostmetrics/raw/url_encode('Energy consumption')
				/*
				for (String applicationMetric : applicationMetrics) {
					String applicationURL = ConfigurationValues.bonfireAPI + vm.location + "/vmmetrics/raws/" + applicationMetric;
					MessageHandler.debug("updateDatabaseVMMetrics(): " + i + ", applicationURL: " + applicationURL + "."); // DEBUG
					
					ArrayList<Item> itemHistoryApplicationMetric = getMetric(applicationURL, applicationMetric, "rawmetric");
					
					if ( itemHistoryApplicationMetric == null )  {
						MessageHandler.debug("updateDatabaseVMMetrics(): \t\t\t VM_ID:" + vm.id + ", " + applicationMetric + ", is NULL. Breaking!!!!"); // DEBUG
						break;
					}
					else if ( itemHistoryApplicationMetric.size() == 0 )  {
						MessageHandler.debug("updateDatabaseVMMetrics(): \t\t\t VM_ID:" + vm.id + ", " + applicationMetric + ", is NULL. Breaking 1!!!!"); // DEBUG
						break;
					} else {
						for( Item localItem : itemHistoryApplicationMetric )
							MessageHandler.debug("updateDatabaseVMMetrics(): \t\t\t VM_ID:" + vm.id + ", metric:" + ", \tItem(name:" + localItem.name + ", zabbix_itemid:" + localItem.zabbix_itemid + ", clock:" + localItem.clock + ", value:" + localItem.value + ", unity:" + localItem.unity); // DEBUG
					}
						
					itemHistory.addAll(itemHistoryApplicationMetric);
				}
				*/
				
            	items.addAll(itemHistory);
            	vm.items = items;
            } catch (Exception e) {
            	MessageHandler.error("MetricsCollector:updateDatabaseVMMetrics(): Got Exception. Exception Message: " + e.getMessage() + ".");
            }
            
            workers[i] = null;
			futures[i] = null;
            i++;
            vmCollectionTime = Calendar.getInstance();	// Set the new vmCollectionTime time
		}
		
		dbConnector.updateMetrics(vms, "virtual_machines");
	}
	
	// ---Application layer metrics---
		

	// ---Network interaction---
	// ***API PERFORMANCE ISSUES: Maybe do each request in a parallel thread?***
	/**
	 * Gets an InputStream of the provided URL.
	 * All external network transactions are performed through this function ensuring that user-agents and any required authentication are set correctly.
	 * Sets all connections requiring authentication to the custom port of 444 (required for BonFIRE client certificate authentication).
	 * 
	 * @param urlString the string representation of the url to contact
	 * @param authRequired set true if the connection requires authentication (e.g. a BonFIRE API call) or false otherwise (e.g. a one-status call).
	 * @return an InputStream of the provided URL. Must be closed external to this function.
	 * @throws IOException Pushes exceptions up the chain on a communication error.
	 */
	public InputStream getURLContent(String urlString, boolean authRequired) throws IOException {
		return getURLContent(urlString, authRequired, true, ConfigurationValues.apiUser); //"eco2clouds");
	}
	public InputStream getURLContent(String urlString, boolean authRequired, boolean useBonFIREHeader) throws IOException {
		return getURLContent(urlString, authRequired, true, ConfigurationValues.apiUser); //"eco2clouds");
	}
	public InputStream getURLContent(String urlString, boolean authRequired, boolean useBonFIREHeader, String userName) throws IOException {
		
		// Ensure credentials have been established.
		establishSecureContext();
			
		URL url = null;
		URLConnection con = null;
		InputStream in = null;
		
		// Parse the provided full url string (e.g. https://api.integration.bonfire.grid5000.fr:444/locations/fr-inria/hostmetrics/aliases)
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			MessageHandler.error("URL: " + urlString + " is malformed. Failed to contact remote resource.");
			//e.printStackTrace();
			throw new IOException(e);
		}
		
		try {
			con = url.openConnection();
		} catch (IOException e) {
			MessageHandler.error("Networking error. Unable to open connection to URL: " + urlString + ". Exception: " + e.getMessage());
			//e.printStackTrace();
			throw new IOException(e);
		}
        
		if ( userName == null || userName.length() < 2 )
			userName = ConfigurationValues.apiUser;
		
		// Set up authentication info: username to assert and client certificate ssl context
		MessageHandler.debug("getURLContent(): Using userName:" + userName + ".");
		if (authRequired) {
			((HttpsURLConnection)con).setSSLSocketFactory(sslSocketFactory);
			//con.setRequestProperty("X-Bonfire-Asserted-Selected-Groups", groupName);
			//con.setRequestProperty("X-Bonfire-Asserted-Group", groupName);
			//con.setRequestProperty("X-Bonfire-Asserted-Id", ConfigurationValues.apiUser);
			con.setRequestProperty("X-Bonfire-Asserted-Id", userName);
		}
		
		if (useBonFIREHeader)
		con.setRequestProperty("Accept", "application/vnd.bonfire+xml");
		else
			con.setRequestProperty("Accept", "*/*");
		con.setRequestProperty("User-Agent", ConfigurationValues.userAgent);

		in = con.getInputStream();
	
		return in;
	}

	/**
	 * Gets the names of all physical host machines from the one-status page for a site.
	 * URL must be set in the accompanying .properties file.
	 *
	 * @param urlString the url of the one-status page
	 * @return the host names for all physical machines reported by the site
	 */
	public String[] getOneStatus(String urlString) {

		// Parsing
		InputStream in = null;
		DocumentBuilder builder = null;
		DocumentBuilderFactory factory = null;
		Document xmlDoc = null;
		XPathFactory xpathFactory = null;
		XPath xpath = null;
		NodeList nodes = null;

		// Results
		ArrayList<String> hosts = new ArrayList<String>();

		try {
			in = getURLContent(urlString, false);
		} catch (IOException e) {
			MessageHandler.print("getOneStatus(): HTTP communication error attempting to gather machine host names from one-status resource: " + urlString + ". Exception Message: " + e.getMessage() + ". Skipping.");
			MessageHandler.error("getOneStatus(): HTTP communication error attempting to gather machine host names from one-status resource: " + urlString + ". Exception Message: " + e.getMessage() + ". Skipping.");
			//e.printStackTrace();
			return null;
		}

		// Parse the returned XML using DOM
		factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
			xmlDoc = builder.parse(in);
		} catch (ParserConfigurationException e) {
			MessageHandler.error("getOneStatus(): Failed to parse host status from " + urlString + ". Exception Message: " + e.getMessage());
			return null;
		} catch (SAXException e) {
			MessageHandler.error("getOneStatus(): Failed to parse host status from " + urlString + ". Exception Message: " + e.getMessage());
			return null;
		} catch (IOException e) {
			MessageHandler.error("getOneStatus(): Failed to parse host status from " + urlString + ". Exception Message: " + e.getMessage());
			return null;
		}

		xpathFactory = XPathFactory.newInstance();
		xpath = xpathFactory.newXPath();

		try {
			nodes = (NodeList) xpath.evaluate("/HOST_POOL/HOST/NAME/text()", xmlDoc, XPathConstants.NODESET);
		} catch (XPathExpressionException e1) {
			MessageHandler.error("getOneStatus(): Failed to parse host status from " + urlString + ". Exception Message: " + e1.getMessage());
			return null;
		}

		// Gather results for returning
		for (int i = 0; i < nodes.getLength(); i++) {
			hosts.add(nodes.item(i).getNodeValue()); 
		}

		// Clean-up
		try {
			in.close();
		} catch (IOException e) {
			MessageHandler.error("MetricsCollector:getOneStatus(): Got Exception. Exception Message: " + e.getMessage() + ".");
			e.printStackTrace();
		}

		return hosts.toArray(new String[hosts.size()]);
	}

	/**
	 * Gets the list of available metrics (aka the metric aliases) from the BonFIRE Zabbix API abstraction as per http://wiki.bonfire-project.eu/index.php/Monitoring_Ecometrics_specs
	 *
	 * @param aliasesURLString the url of the resource's aliases listing
	 * @param siteLocation the location of the site (uk-epcc, fr-inria, etc)
	 * @return a list with each alias name
	 */
	private ArrayList<String> getAliasesMetric(String aliasesURLString, String siteLocation) {
		ArrayList<String> aliases = new ArrayList<String>();
		String aliasesResponse = null;

		// Retrieve list of metric aliases
		try {
			InputStream in = getURLContent(aliasesURLString, true);
			aliasesResponse = convertStreamToString(in);
			in.close();
		} catch (IOException e) {
			MessageHandler.error("getAliasesMetric(): HTTP communication error attempting to gather aliases from url: " + aliasesURLString + ". Exception Message: " + e.getMessage() + " Skipping.");
			//e.printStackTrace();
			return aliases;
		}

		// Build an XML file out of the response
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder;
	    Document doc = null;
	    try
	    {
	        builder = factory.newDocumentBuilder();
	        doc = builder.parse( new InputSource( new StringReader( aliasesResponse ) ) );
	    } catch (Exception e) {
			MessageHandler.error("getAliasesMetric(): XML document is not correct: " + aliasesResponse + ". Exception Message: " + e.getMessage() + " Skipping.");
			//e.printStackTrace();
			return aliases;
	    }

		NodeList items = doc.getElementsByTagName("items");
		if (items == null || items.getLength() == 0) {
			MessageHandler.error("getAliasesMetric(): Error in XML file (" + aliasesURLString + "). Items is empty. Exiting....");
			return aliases;
		}
		Element itemsElement = (Element) items.item(0);

		NodeList metrics = itemsElement.getElementsByTagName("metric");
		if (metrics == null || metrics.getLength() == 0) {
			
			metrics = itemsElement.getElementsByTagName("availablehostmetric");	// Check for OLD API implementation
			if (metrics == null || metrics.getLength() == 0) {
			MessageHandler.error("getAliasesMetric(): Error in XML file (" + aliasesURLString + "). Metrics is empty. Exiting....");
			return aliases;
		}
		}
		
        for (int i = 0; i < metrics.getLength(); ++i)
        {
        	Node childNode = metrics.item(i);
        	if ( childNode.getNodeType() == Node.ELEMENT_NODE )
        	{
        		String temp = getNodeAttr("href", childNode);
        		String[] bits = temp.split("/");
        		temp = bits[bits.length-1];
            	aliases.add(temp);
        	}
        }

		return aliases;
	}

	/**
	 * Gets the list of available Site metrics (aka the metric aliases) from the BonFIRE Zabbix API abstraction as per http://wiki.bonfire-project.eu/index.php/Monitoring_Ecometrics_specs
	 *
	 * @param aliasesURLString the url of the resource's aliases listing
	 * @param siteLocation the location of the site (uk-epcc, fr-inria, etc)
	 * @return a list with each alias name
	 */
	private ArrayList<String> getAliases(String aliasesURLString, String siteLocation) {

		ArrayList<String> aliases = new ArrayList<String>();
		String aliasesResponse = null;

		// Retrieve list of metric aliases
		try {
			InputStream in = getURLContent(aliasesURLString, true);
			aliasesResponse = convertStreamToString(in);
			in.close();
		} catch (IOException e) {
			MessageHandler.error("getAliases(): HTTP communication error attempting to gather aliases from url: " + aliasesURLString + ". Exception Message: " + e.getMessage() + " Skipping.");
			//e.printStackTrace();
			return aliases;
		}

		// Build an XML file out of the response
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc = null;
		try
		{
			builder = factory.newDocumentBuilder();
			doc = builder.parse( new InputSource( new StringReader( aliasesResponse ) ) );
		} catch (Exception e) {
			MessageHandler.error("getAliases(): XML document is not correct: " + aliasesResponse + ". Exception Message: " + e.getMessage() + " Skipping.");
			//e.printStackTrace();
			return aliases;
		}

	    NodeList items = doc.getElementsByTagName("items");
		if (items == null || items.getLength() == 0) {
			MessageHandler.error("getAliases(): Error in XML file (" + aliasesURLString + "). Items is empty. Exiting....");
			return aliases;
		}
		Element itemsElement = (Element) items.item(0);
		
		NodeList metrics = itemsElement.getElementsByTagName("metric");
		if (metrics == null || metrics.getLength() == 0) {
			metrics = itemsElement.getElementsByTagName("locationmetric");	// Check for OLD API implementation for location
			
			if (metrics == null || metrics.getLength() == 0) {
				metrics = itemsElement.getElementsByTagName("hostmetric");	// Check for OLD API implementation for hostmetric (VM)

				if (metrics == null || metrics.getLength() == 0) {
					MessageHandler.error("getAliases(): Error in XML file (" + aliasesURLString + "). Metrics is empty. Exiting....");
					return aliases;
				}
			}
		}
		
		for (int i = 0; i < metrics.getLength(); ++i)
        {
        	Node childNode = metrics.item(i);
        	if ( childNode.getNodeType() == Node.ELEMENT_NODE )
        	{
        		String temp = getNodeAttr("href", childNode);
        		String[] bits = temp.split("/");
        		temp = bits[bits.length-1];
            	aliases.add(temp);
        	}
        }
		
		return aliases;
	}

	/**
	 * Gets the metric values and other item information from the BonFIRE Zabbix API abstraction as per http://wiki.bonfire-project.eu/index.php/Monitoring_Ecometrics_specs.
	 * Can be used either for a single call or with the history_metrics function.
	 * !!!Currently one API call per metric. Serious QoS issues!!!
	 *
	 * @param metricURLString the url of the metric query
	 * @param metric the alias name of the metric
	 * @return a list of Item instances containing the metric information returned by the api.
	 */
	private ArrayList<Item> getMetric(String metricURLString, String metric) {
		return getMetric(metricURLString, metric, "metric");
	}
				        
	private ArrayList<Item> getMetric(String metricURLString, String metric, String tagName) {	        
		ArrayList<Item> items = new ArrayList<Item>();
		String metricResponse = null;
        
		try {
			InputStream in = getURLContent(metricURLString, true);
			metricResponse = convertStreamToString(in);
			in.close();
		} catch (IOException e) {
			MessageHandler.error("getMetric(): Communication error attempting to gather metric from url: " + metricURLString + ". Exception Message: " + e.getMessage() + ". Skipping.");
			//e.printStackTrace();
			return items;
		}			
		
		// Build an XML file out of the response
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
	    Document doc = null;
	    try
	    {
	        builder = factory.newDocumentBuilder();
	        doc = builder.parse( new InputSource( new StringReader( metricResponse ) ) );
	    } catch (Exception e) {
			MessageHandler.error("getMetric(): XML document is not correct: " + metricResponse + ". Exception Message: " + e.getMessage() + " Skipping.");
			//e.printStackTrace();
			return items;
	    }

		// Get the document's root XML node
        NodeList root = doc.getChildNodes();

        // Navigate down the hierarchy to get to the Site node
        Node nodeMetric = getNode(tagName, root);
        if ( nodeMetric == null || !nodeMetric.hasChildNodes()) {
        	if ( tagName.equalsIgnoreCase("metric")) {
			MessageHandler.error("getMetric(): Error in XML file (" + metricURLString + "). Metric is empty. Exiting....");
			return items;
        }
        	nodeMetric = getNode("metric", root);
        	if ( nodeMetric == null || !nodeMetric.hasChildNodes()) {
        		MessageHandler.error("getMetric(): Error in XML file (" + metricURLString + "). Both Metric and " + tagName + " tags are empty. Exiting....");
    			return items;
        	}
        }

		String name;
		int zabbix_itemid;
		long clock;
		double value;
		String unity;

		try {
		Node itemidNode = getNode("itemid", nodeMetric.getChildNodes() );
        if (itemidNode == null ) {
			MessageHandler.error("getMetric():" + metricURLString + " returned no itemid node. Exiting....");
			return items;
        }
        zabbix_itemid = Integer.parseInt(getNodeValue(itemidNode));

		Node valueNode = getNode("value", nodeMetric.getChildNodes() );
        if (valueNode == null ) {
			MessageHandler.error("getMetric():" + metricURLString + " returned no value node. Exiting....");
			return items;
        }
        if (getNodeValue(valueNode).length() > 60 )
        {
			MessageHandler.error("getMetric():" + metricURLString + " returned value node LONGER than 60. Exiting....");
			return items;
        }
        value = Double.parseDouble(getNodeValue(valueNode));
	        // The following code causes NumberFormatException exceptions for large values....
	        /*
	        NumberFormat number = NumberFormat.getNumberInstance();
			number.setMaximumFractionDigits(10);
			number.setMaximumIntegerDigits(14);
			String snum = number.format(value);
			value = Double.parseDouble(snum);
			*/

		Node clockNode = getNode("clock", nodeMetric.getChildNodes() );
        if (clockNode == null ) {
			MessageHandler.error("getMetric():" + metricURLString + " returned no clock node. Exiting....");
			return items;
        }
        clock = Long.parseLong(getNodeValue(clockNode));

		Node keynameNode = getNode("key", nodeMetric.getChildNodes() );
        if (keynameNode == null ) {
			MessageHandler.error("getMetric():" + metricURLString + " returned no key node. Exiting....");
			return items;
        }
        name = getNodeValue(keynameNode);

        Node keynameName = getNode("name", nodeMetric.getChildNodes() );
        if (keynameName == null ) {
			MessageHandler.error("getMetric():" + metricURLString + " returned no name node. Exiting....");
			return items;
        }
        //name = getNodeValue(keynameName);

		Node unityNode = getNode("unit", nodeMetric.getChildNodes() );
        if (unityNode == null ) {
			MessageHandler.error("getMetric():" + metricURLString + " returned no unit node. Exiting....");
			return items;
        }
        unity = getNodeValue(unityNode);
		} catch (NumberFormatException e) {
			MessageHandler.error("getMetric():" + metricURLString + " returned an NumberFormatException Exception [" + e.getMessage() + ". Exiting....");
		    return items;
		} catch (Exception e){
			MessageHandler.error("getMetric():" + metricURLString + " returned a Generic Exception [" + e.getMessage() + ". Exiting....");
		    return items;
		 }

		items.add(new Item(name, zabbix_itemid, clock, value, unity));

		return items;
	}

	/**
	 * Gather all site names from the {siteName, statusURL} pairs as supplied in the accompanying .properties file.
	 *
	 * @return the siteNames
	 */
	private String[] getSites() {
	 
		String[][] hostInfo = ConfigurationValues.hostInfo;
		String[] sites = new String[hostInfo.length];
		for (int i=0; i<hostInfo.length; i++) {
			sites[i] = hostInfo[i][0];
		}
		
		return sites;
	}
	
	/**
	 * Inserts randomly generated metrics Items into the supplied Resource objects.
	 * Used as a temporary measure for Experiment/Application and host metrics where there is no API provision for gathering their metrics.
	 *
	 * @param resources the resources to populate with dummy data.
	 * @param numItems the number of items to generate
	 */
	@SuppressWarnings("unused")
	private void populateResourceDummyMetrics(ArrayList<? extends Resource> resources, int numItems) {

		// Populate items lists with arbitrary data
		int id = 0;
		for (Resource resource : resources) {
			
			// Experiment metrics
			ArrayList<Item> items = new ArrayList<Item>();
			for (int i=0; i<numItems; i++) {
				// Time has to be in SECONDS
				Item item = new Item("DummyEnergyMetric", id++, (System.currentTimeMillis() / 1000), 100.0+id, "kW");
				items.add(item);
			}
			resource.items = items;
		}
	}
	
	/**
	 * Loads the Java keystore specified in the properties file with the BonFIRE API certificate and this application's certificate/private key pair for use in mutual authentication.
	 */
	public void establishSecureContext() {
		
		// Only establish the context once if it's not already been established.
		if (sslSocketFactory == null) {

			try {
				// Load store of trusted certs and client private key.
				InputStream keyInput = new FileInputStream(ConfigurationValues.keyStorePath);
				KeyStore ks = KeyStore.getInstance("JKS");
				ks.load(keyInput, ConfigurationValues.keyStorePassword.toCharArray());
				keyInput.close();

				// Use store to set private key.
				KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
				kmf.init(ks, ConfigurationValues.keyStorePassword.toCharArray());
				KeyManager[] kms = kmf.getKeyManagers();

				// Use store to set trusted certificates.
				TrustManagerFactory trustFactory = TrustManagerFactory.getInstance("PKIX");
				trustFactory.init(ks);
				TrustManager[] tms = trustFactory.getTrustManagers();		

				// Set future connections requiring authentication to use these credentials.
				SSLContext sc = SSLContext.getInstance("TLS");
				sc.init(kms, tms, null);
				this.sslSocketFactory = sc.getSocketFactory();

			} catch (FileNotFoundException e) {
				MessageHandler.error("Monitoring Collector cannot find certificate keystore file " + ConfigurationValues.keyStorePath + ". Fatal.");
				throw new RuntimeException(e);
			} catch (KeyStoreException e) {
				MessageHandler.error("Monitoring Collector could not initialise certificate keystore using file " + ConfigurationValues.keyStorePath + ". Fatal.");
				throw new RuntimeException(e);
			} catch (NoSuchAlgorithmException e) {
				MessageHandler.error("Monitoring Collector could not initialise certificate keystore using file " + ConfigurationValues.keyStorePath + ". Current platform does not support TLSv1 using X.509 certificates. Fatal.");
				throw new RuntimeException(e);
			} catch (CertificateException e) {
				MessageHandler.error("Monitoring Collector could not load all certificates from keystore file " + ConfigurationValues.keyStorePath + ". Fatal.");
				throw new RuntimeException(e);
			} catch (IOException e) {
				MessageHandler.error("Monitoring Collector IO error reading certificate keystore file " + ConfigurationValues.keyStorePath + ". Fatal.");
				throw new RuntimeException(e);
			} catch (UnrecoverableKeyException e) {
				MessageHandler.error("Monitoring Collector could not load all certificates from keystore file " + ConfigurationValues.keyStorePath + ". Password may be incorrect. Fatal.");
				throw new RuntimeException(e);
			} catch (KeyManagementException e) {
				MessageHandler.error("Monitoring Collector could not establish an SSL context using the given trusted certificates from keystore file " + ConfigurationValues.keyStorePath + ". Fatal.");
				throw new RuntimeException(e);
			}		

		}
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

				MessageHandler.print("readConfigAndPopulateListOfGroups(): Adding group:" + splited[0] + ", to list.");
				groupNames.add(splited[0]);
			}
			br.close();
			
			return;
			
		} catch (FileNotFoundException e) {
			MessageHandler.error("readConfigAndPopulateListOfGroups(): config.txt could not be found. Adding default entry.");
			//groupNames.add("eco2");
		}  catch (IOException e) {
			MessageHandler.error("readConfigAndPopulateListOfGroups(): Parsing config.txt threw an IOException. Adding default entry.");
			//groupNames.add("eco2");
		} catch (Exception e) {
			MessageHandler.error("readConfigAndPopulateListOfGroups(): Generic Exception. Exception Message: " + e.getMessage() + " Skipping.");
			return;
	    }
		
		return;
	}
	
	public void addGroupToGroupNames(String groupName)
	{
		groupNames.add(groupName);
	}
	
	/**
	 *  Helper function to convert the given InputStream to a string.
	 *
	 * @param is the InputStream to convert to a string
	 * @return the string representation of the InputStream
	 */
	@SuppressWarnings("resource")
	private static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}

	/**
	 *  Helper function to return an XML Node from a NodeList
	 *
	 * @param tagName the name of the XML node that we want to retrieve
	 * @param nodes the NodeList that we are searching
	 * @return the Node (null if not found)
	 */
	protected static Node getNode(String tagName, NodeList nodes) {
		for (int x = 0; x < nodes.getLength(); x++) {
			Node node = nodes.item(x);
			if (node.getNodeName().equalsIgnoreCase(tagName)) {
				return node;
			}
		}

		return null;
	}

	/**
	 *  Helper function to return the value of a Node
	 *
	 * @param node the input XML Node
	 * @return the value using type String (null if not found)
	 */
	protected static String getNodeValue(Node node) {
		NodeList childNodes = node.getChildNodes();
		for (int x = 0; x < childNodes.getLength(); x++) {
			Node data = childNodes.item(x);
			if (data.getNodeType() == Node.TEXT_NODE)
				return data.getNodeValue();
		}
		return "";
	}

	/**
	 *  Helper function to return the value of a Node in a NodeList
	 *
	 * @param tagName the name of the XML node that we want to retrieve
	 * @param nodes the XML NodeList that we are searching
	 * @return the value using type String (null if not found)
	 */
	protected static String getNodeValue(String tagName, NodeList nodes) {
		for (int x = 0; x < nodes.getLength(); x++) {
			Node node = nodes.item(x);
			if (node.getNodeName().equalsIgnoreCase(tagName)) {
				NodeList childNodes = node.getChildNodes();
				for (int y = 0; y < childNodes.getLength(); y++) {
					Node data = childNodes.item(y);
					if (data.getNodeType() == Node.TEXT_NODE)
						return data.getNodeValue();
				}
			}
		}
		return "";
	}

	/**
	 *  Helper function to return the attributes of a Node
	 *
	 * @param attrName the name of the XML attribute that we want to retrieve
	 * @param node the XML Node that we are searching
	 * @return the value using type String (null if not found)
	 */
	protected static String getNodeAttr(String attrName, Node node) {
		NamedNodeMap attrs = node.getAttributes();
		for (int y = 0; y < attrs.getLength(); y++) {
			Node attr = attrs.item(y);
			if (attr.getNodeName().equalsIgnoreCase(attrName)) {
				return attr.getNodeValue();
			}
		}
		return "";
	}

	/**
	 *  Helper function to return the attributes of a Node in a NodeList
	 *
	 * @param tagName the name of the XML node that we want to retrieve
	 * @param attrName the name of the XML attribute that we want to retrieve
	 * @param nodes the XML NodeList that we are searching
	 * @return the value using type String (null if not found)
	 */
	protected static String getNodeAttr(String tagName, String attrName, NodeList nodes) {
		for (int x = 0; x < nodes.getLength(); x++) {
			Node node = nodes.item(x);
			if (node.getNodeName().equalsIgnoreCase(tagName)) {
				NodeList childNodes = node.getChildNodes();
				for (int y = 0; y < childNodes.getLength(); y++) {
					Node data = childNodes.item(y);
					if (data.getNodeType() == Node.ATTRIBUTE_NODE) {
						if (data.getNodeName().equalsIgnoreCase(attrName))
							return data.getNodeValue();
					}
				}
			}
		}

		return "";
	}
	
	/*
	 * Debug Functions
	private static String convertDocumentToString(Document doc) {
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer;
	    try {
	        transformer = tf.newTransformer();
	        // below code to remove XML declaration
	        // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	            StringWriter writer = new StringWriter();
	            transformer.transform(new DOMSource(doc), new StreamResult(writer));
	            String output = writer.getBuffer().toString();
	            return output;
	        } catch (TransformerException e) {
	            e.printStackTrace();
	        }
	        
	        return null;
	    }
		
		
	public static void listAllAttributes(Element element) {
			
			MessageHandler.debug("List attributes for node: " + element.getNodeName());
		
		// get a map containing the attributes of this node 
		NamedNodeMap attributes = element.getAttributes();
	
		// get the number of nodes in this map
		int numAttrs = attributes.getLength();
	
		for (int i = 0; i < numAttrs; i++) {
			Attr attr = (Attr) attributes.item(i);
			
			String attrName = attr.getNodeName();
			String attrValue = attr.getNodeValue();
			
			//System.out.println("Found attribute: " + attrName + " with value: " + attrValue);
			MessageHandler.debug("\t\tFound attribute: " + attrName + " with value: " + attrValue); // DEBUG
		}
	}
	*/

}