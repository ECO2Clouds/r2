package eu.eco2clouds.accounting.rest.service;

import static eu.eco2clouds.accounting.Dictionary.CONTENT_TYPE_ECO2CLOUDS_XML;
import static eu.eco2clouds.accounting.Dictionary.CONTENT_TYPE_JSON;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import eu.eco2clouds.accounting.conf.Configuration;
import eu.eco2clouds.accounting.datamodel.Action;
import eu.eco2clouds.accounting.datamodel.ActionType;
import eu.eco2clouds.accounting.datamodel.Experiment;
import eu.eco2clouds.accounting.datamodel.Host;
import eu.eco2clouds.accounting.datamodel.Testbed;
import eu.eco2clouds.accounting.datamodel.VM;
import eu.eco2clouds.accounting.datamodel.VMHost;
import eu.eco2clouds.accounting.datamodel.parser.Collection;
import eu.eco2clouds.accounting.datamodel.parser.Items;
import eu.eco2clouds.accounting.datamodel.parser.Link;
import eu.eco2clouds.accounting.datamodel.util.ModelConversion;
import eu.eco2clouds.accounting.monitoringcollector.DatabaseConnector;
import eu.eco2clouds.accounting.monitoringcollector.MessageHandler;
import eu.eco2clouds.accounting.service.ExperimentDAO;
import eu.eco2clouds.accounting.service.HostDAO;
import eu.eco2clouds.accounting.service.HostDataDAO;
import eu.eco2clouds.accounting.service.TestbedDAO;
import eu.eco2clouds.accounting.service.VMHostDAO;
import eu.eco2clouds.accounting.testbedclient.Client;
import eu.eco2clouds.accounting.testbedclient.ClientHC;

/**
 * 
 * Copyright 2014 ATOS SPAIN S.A. 
 * 
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author: David Garcia Perez. Atos Research and Innovation, Atos SPAIN SA
 * e-mail david.garciaperez@atos.net
 */

public class AccountingServiceExperiment extends AccountingServiceAbstractImpl {
	private static Logger logger = Logger.getLogger(AccountingServiceExperiment.class);
	
	public DatabaseConnector dbConnector = new DatabaseConnector(
			Configuration.mysqlCollectorHost,
			Configuration.mysqlCollectorUsername,
			Configuration.mysqlCollectorPassword);
	private static long PERIOD = 9600000l;

	protected enum Field {
		application_profile, submitted_experiment_descriptor
	};

	public AccountingServiceExperiment(ExperimentDAO experimentDAO) {
		this.experimentDAO = experimentDAO;
	}

	public AccountingServiceExperiment(ExperimentDAO experimentDAO,
			TestbedDAO testbedDAO, HostDAO hostDAO, HostDataDAO hostDataDAO) {
		this.experimentDAO = experimentDAO;
		this.testbedDAO = testbedDAO;
		this.hostDAO = hostDAO;
		this.hostDataDAO = hostDataDAO;
	}

	public AccountingServiceExperiment(TestbedDAO testbedDAO, HostDAO hostDAO,
			HostDataDAO hostDataDAO) {
		this.testbedDAO = testbedDAO;
		this.hostDAO = hostDAO;
		this.hostDataDAO = hostDataDAO;
	}

	public AccountingServiceExperiment(ExperimentDAO experimentDAO,
			HostDAO hostDAO, VMHostDAO vmHostDAO) {
		this.experimentDAO = experimentDAO;
		this.hostDAO = hostDAO;
		this.vmHostDAO = vmHostDAO;
	}

	public boolean doesElementExist(int id) {

		if (this.experimentDAO.getById(id) != null)
			return true;
		else
			return false;
	}

	public StringBuilder printElement(Experiment experiment) {

		StringBuilder xmlResponse = new StringBuilder();

		xmlResponse.append("<id>" + experiment.getId() + "</id>\n");
		xmlResponse.append("<bonfire-experiment-id>"
				+ experiment.getBonfireExperimentId()
				+ "</bonfire-experiment-id>\n");
		xmlResponse.append("<bonfire-user-id>" + experiment.getBonfireUserId()
				+ "</bonfire-user-id>\n");
		xmlResponse.append("<bonfire-groupd-id>"
				+ experiment.getBonfireGroupId() + "</bonfire-group-id>\n");
		xmlResponse.append("<start-time>" + experiment.getStartTime()
				+ "</start-time>\n");
		xmlResponse.append("<end-time>" + experiment.getEndTime()
				+ "</end-time>\n");
		xmlResponse.append("<link rel=\"parent\" href=\"/experiments\"/>\n");
		xmlResponse
				.append("<link rel=\"application-profile\" href=\"/experiments/"
						+ experiment.getId() + "/application-profile\"/>\n");
		xmlResponse
				.append("<link rel=\"submitted-experiment-descriptor\" href=\"/experiments/"
						+ experiment.getId()
						+ "/submitted-experimen-descriptor\"/>\n");
		xmlResponse.append("<link rel=\"vms\" href=\"/experiments/"
				+ experiment.getId() + "/vms\"/>\n");
		xmlResponse.append("</experiment>\n");

		return xmlResponse;
	}

	public String printInformation(String verb, int experimentId, List<String> groups, String field) throws IOException, JAXBException {

		List<Experiment> experiments = new ArrayList<Experiment>();
		Experiment experiment = null;
		String xmlResponse = "";
		// StringBuilder xmlResponse = new StringBuilder();
		Field fieldExperiment = null;

		if (groups != null && groups.size() != 0) {
			if (experimentId == -1) {
				if(groups.size() == 1 && groups.get(0).equals("ALL")) {
					experiments = this.experimentDAO.getAll();
				} else {
					experiments = this.experimentDAO.getListExperimentByGroupId(groups);
				}
				if (experiments.size() == 0)
					return null;
			} else {
				experiment = this.experimentDAO.getExperimentByGroupId(experimentId, groups);
				if (experiment == null)
					return null;
			}

		}

		if (verb != "GET") {
			if (experimentId == -1) {
				experiments = this.experimentDAO.getAll();
				if (experiments.size() == 0)
					return null;
			} else {
				experiment = this.experimentDAO.getExperimentId(experimentId);
				if (experiment == null)
					return null;
			}
		}

		if (experimentId == -1) {
			/** @Path("/experiments/") */
			Collection collection = new Collection();
			collection.setHref("/experiments");
			Link link = new Link("parent", "/", CONTENT_TYPE_ECO2CLOUDS_XML);
			collection.addLink(link);

			Items items = new Items();
			items.setOffset(0);
			items.setTotal(experiments.size());
			collection.setItems(items);

			for (Experiment experimentFromDB : experiments)
				items.addExperiment(ModelConversion
						.getExperimentXML(experimentFromDB));

			JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			marshaller.marshal(collection, out);

			xmlResponse = out.toString();
		} else {
			/** @Path("/experiments/{id ") */

			if (experiment != null)
				xmlResponse = getExperimentXMLRepresentation(experiment);
			else
				return null;

			if (field != null)
				fieldExperiment = Field.valueOf(field);

			if (fieldExperiment != null) {
				StringBuilder xmlResponseBuilder = new StringBuilder();

				switch (fieldExperiment) {
				/** @Path("/experiments/{id /application-profile") */
				case application_profile:
					xmlResponseBuilder.append(experiment
							.getApplicationProfile() + "\n");
					break;
				/** @Path("/experiments/{id /submitted_experiment_descriptor") */
				case submitted_experiment_descriptor:
					xmlResponseBuilder.append(experiment
							.getSubmittedExperimentDescriptor() + "\n");
					break;
				}

				xmlResponse = xmlResponseBuilder.toString();
			}
		}

		return xmlResponse;
	}

	public StringBuilder printVMElement(long experiment_id, String hostname,
			VM vm) {

		StringBuilder xmlResponse = new StringBuilder();
		int vmId = vm.getId();

		xmlResponse.append("<vm href=\"/experiments/" + experiment_id + "/vms/"
				+ vmId + "\">\n");
		xmlResponse.append("<id>" + vmId + "</id>\n");
		if (hostname != null) {
			xmlResponse.append("<hostname>" + hostname + "</hostname>\n");
		}
		xmlResponse.append("<bonfire_url>" + vm.getBonfireUrl()
				+ "</bonfire_url>\n");

		List<Action> vmActions = vm.getActions();

		xmlResponse.append("<actions>\n");

		Iterator<Action> it = vmActions.iterator();

		while (it.hasNext()) {

			// Get elements Action
			Action vmAction = (Action) it.next();

			xmlResponse.append(printAction(experiment_id, vmId, vmAction));
		}

		xmlResponse.append("</actions>\n");

		xmlResponse.append("<link rel=\"parent\" href=\"/experiments/"
				+ experiment_id + "\">\n");
		xmlResponse.append("<link rel=\"actions\" href=\"/experiments/"
				+ experiment_id + "/vms/" + vm.getId() + "/actions\">\n");
		xmlResponse.append("</vm>\n");

		return xmlResponse;
	}

	public String printAction(long experiment_id, int vmId, Action vmAction) {

		StringBuilder xmlResponse = new StringBuilder();

		// Get elements Action_Types
		ActionType vmActionType = vmAction.getActionType();
		int vmActionId = vmAction.getId();
		int vmActionTypeId = vmActionType.getId();

		xmlResponse.append("<action href=\"/experiments/" + experiment_id
				+ "/vms/" + vmId + "/actions/" + vmActionId + "\">\n");
		xmlResponse.append("<id>" + vmActionId + "</id>\n");
		xmlResponse.append("<timestamp>" + vmAction.getTimestamp()
				+ "</timestamp>\n");
		xmlResponse.append("<action_type href=\"/action_types/"
				+ vmActionTypeId + "\">\n");
		xmlResponse.append("<id>" + vmActionTypeId + "</id>\n");
		xmlResponse.append("<name>" + vmActionType.getName() + "</name>\n");
		xmlResponse.append("</action_type>\n");
		xmlResponse.append("</action>\n");

		return xmlResponse.toString();

	}

	public String printVMInformation(long experiment_id, int id,
			String hostname, List<String> groups) {

		Experiment experiment = null;
		Set<VM> vms = null;
		VM vm = null;
		StringBuilder xmlResponse = new StringBuilder();

		if (groups.size() != 0) {
			experiment = this.experimentDAO.getListVMByGroupId(experiment_id,
					groups);

			if (experiment == null)
				return null;
		}

		xmlResponse.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

		if (id == -1) {

			// @Path("/experiments/{experiment_id}/vms")

			experiment = this.experimentDAO.getListVMByGroupId(experiment_id,
					groups);

			vms = (Set<VM>) experiment.getvMs();

			/** @Path("/experiment/{experiment_id /vms") */

			xmlResponse
					.append("<collection xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/occi\" href=\"/experiments/"
							+ experiment_id + "/vms\">\n");
			xmlResponse.append("<items offset=\"0\" total=\"" + vms.size()
					+ "\">\n");

			for (VM vm2 : vms) {

				xmlResponse.append(printVMElement(experiment_id, null, vm2));
			}

			xmlResponse.append("</items>\n");
			xmlResponse
					.append("<link href=\"/experiments/"
							+ experiment_id
							+ "\" rel=\"parent\" type=\"application/vnd.eco2clouds+xml\"/>\n");
			xmlResponse.append("</collection>\n");
		} else {

			// @Path("/experiments/{experiment_id}/vms/{vm_id}")

			// ) experiment = this.experimentDAO.getVMByGroupId(experiment_id,
			// id, groups);
			experiment = this.experimentDAO.getVMId(experiment_id, id);

			if (experiment == null)
				return null;

			vms = (Set<VM>) experiment.getvMs();

			for (VM vmElem : vms) {

				if (id == vmElem.getId()) {
					vm = vmElem;
				}
			}

			if (vm == null)
				return null;
			else {
				xmlResponse
						.append("<action_type xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/occi\" href=\"/action_types/"
								+ vm.getId() + "\">\n");
				xmlResponse.append(printVMElement(experiment_id, null, vm));
			}

		}
		return xmlResponse.toString();

	}

	public boolean doesExperimentIdExist(long experimentId) {

		if (this.experimentDAO.getExperimentId(experimentId) != null)
			return true;
		else
			return false;
	}

	public boolean doesElementIdExist(int id) {

		if (this.experimentDAO.getById(id) != null)
			return true;
		else
			return false;
	}

	private boolean doesVMIdExist(int experimentId, int id) {

		if (this.experimentDAO.getVMId(experimentId, id) != null)
			return true;
		else
			return false;
	}

	public Response serviceVM(String verb, int experimentId, int vmId, HttpHeaders hh, String payload) throws IOException, JAXBException {

		String xmlResponse = null;
		List<String> groups = getGroupHeader(hh);

		if (verb == "GET") {
			if (groups.size() == 0)
				return buildResponse(400, "Missing GroupId");

			if (experimentId != -1 && vmId != -1) {
				if (!doesVMIdExist(experimentId, vmId))
					return buildResponse(
							404,
							"VmId "
									+ vmId
									+ " from experimentId "
									+ experimentId
									+ " does not exist in the Accounting Service Scheduler Database");
			} else if (experimentId != -1) {
				if (!doesExperimentIdExist(experimentId))
					return buildResponse(
							404,
							"ExperimentId "
									+ experimentId
									+ " does not exist in the Accounting Service Scheduler Database");
			}

			xmlResponse = new AccountingServiceExperiment(this.experimentDAO).printVMInformation(experimentId, vmId, null, groups);

			if (xmlResponse == null)
				return buildResponse(403, "Not authorized to access to the VM information");
		} else if (verb == "POST") {

			if (experimentId != -1 && vmId != -1) {
				System.out.println("experimentId, vmId: " + experimentId + " "
						+ vmId);
				if (!doesVMIdExist(experimentId, vmId))
					return buildResponse(
							404,
							"VmId "
									+ vmId
									+ " from experimentId "
									+ experimentId
									+ " does not exist in the Accounting Service Scheduler Database");
			} else if (experimentId != -1) {
				if (!doesExperimentIdExist(experimentId))
					return buildResponse(
							404,
							"ExperimentId "
									+ experimentId
									+ " does not exist in the Accounting Service Scheduler Database");
			}

			if (experimentId != -1) {

				if (vmId != -1)
					xmlResponse = new AccountingServiceExperiment(
							this.experimentDAO, this.testbedDAO, this.hostDAO,
							this.hostDataDAO).addAction(experimentId, vmId,
							payload);
				else
					xmlResponse = new AccountingServiceExperiment(
							this.experimentDAO, this.hostDAO, this.vmHostDAO)
							.addVM(experimentId, payload, groups);
			}
		} else if (verb == "PUT") {

			if (experimentId != -1 && vmId != -1) {
				if (!doesVMIdExist(experimentId, vmId))
					return buildResponse(
							404,
							"VmId "
									+ vmId
									+ " from experimentId "
									+ experimentId
									+ " does not exist in the Accounting Service Scheduler Database");
			} else if (experimentId != -1) {
				if (!doesExperimentIdExist(experimentId))
					return buildResponse(
							404,
							"ExperimentId "
									+ experimentId
									+ " does not exist in the Accounting Service Scheduler Database");
			}
			if (experimentId != -1) {
				if (vmId != -1)
					xmlResponse = new AccountingServiceExperiment(
							this.experimentDAO).updateAction(experimentId,
							vmId, payload);
				else
					xmlResponse = new AccountingServiceExperiment(
							this.experimentDAO, this.hostDAO, this.vmHostDAO)
							.updateVM(experimentId, payload, groups);
			}
		}

		if (xmlResponse == null) {
			if (verb == "GET")
				return buildResponse(403,
						"Not authorized to access to the Experiment information");
			else if (verb == "POST")
				return buildResponse(500, "Not inserted Experiment information");
			else if (verb == "PUT")
				return buildResponse(500, "Not updated Experiment information");
		} else if (verb == "POST" || verb == "PUT") {
			return buildResponse(202, xmlResponse);
		}
		return buildResponse(200, xmlResponse);
	}

	public Response service(String verb, int id, HttpHeaders hh, String field,
			String payload) throws IOException, JAXBException {

		String xmlResponse = null;
		List<String> groups = getGroupHeader(hh);

		if (verb == "GET") {

			if (id != -1) {
				if (groups.size() == 0)
					return buildResponse(400, "Missing GroupId");
				if (!doesElementIdExist(id))
					return buildResponse(
							404,
							"Experiment "
									+ id
									+ " does not exist in the Accounting Service Scheduler Database");
			}

			xmlResponse = new AccountingServiceExperiment(this.experimentDAO)
					.printInformation(verb, id, groups, field);

			if (xmlResponse == null)
				return buildResponse(403,
						"Not authorized to access to the experiment information");

		} else if (verb == "POST") {
			if (id != -1) {

				if (!doesElementExist(id))
					return buildResponse(
							404,
							"Experiment  "
									+ id
									+ " does not exist in the Accounting Service Scheduler Database");
			}

			xmlResponse = new AccountingServiceExperiment(this.experimentDAO)
					.addExperiment(verb, payload);

		} else if (verb == "PUT") {
			if (id != -1) {

				if (!doesElementExist(id))
					return buildResponse(
							404,
							"Experiment "
									+ id
									+ " does not exist in the Accounting Service Scheduler Database");
			}

			xmlResponse = new AccountingServiceExperiment(this.experimentDAO)
					.updateExperiment(verb, payload);

		}

		if (xmlResponse == null) {
			if (verb == "GET")
				return buildResponse(403,
						"Not authorized to access to the Experiment information");
			else
				return buildResponse(500, "Not inserted Experiment information");
		} else if (verb == "POST") {
			return buildResponse(202, xmlResponse);
		}
		return buildResponse(200, xmlResponse);
	}

	private Experiment getExperimentForDB(eu.eco2clouds.accounting.datamodel.parser.Experiment experimentXML) {
		Experiment experimentDB = new Experiment();

		if (experimentXML.getId() != null) {
			experimentDB.setId(experimentXML.getId().intValue());
			logger.debug("                     id:" + experimentDB.getId());
		}
		if (experimentXML.getBonfireGroupId() != null) {
			experimentDB.setBonfireGroupId(experimentXML.getBonfireGroupId());
			logger.debug("        BonFIRE Group id:"
					+ experimentDB.getBonfireGroupId());
		}
		if (experimentXML.getBonfireUserId() != null) {
			experimentDB.setBonfireUserId(experimentXML.getBonfireUserId());
			logger.debug("        BonFIRE user id:"
					+ experimentDB.getBonfireUserId());
		}
		if (experimentXML.getBonfireExperimentId() != null) {
			experimentDB.setBonfireExperimentId(experimentXML
					.getBonfireExperimentId().longValue());
			logger.debug("  BonFIRE Experiment Id:"
					+ experimentDB.getBonfireExperimentId());
		}
		if (experimentXML.getStartTime() != null) {
			experimentDB.setStartTime(experimentXML.getStartTime().longValue());
			logger.debug("             Start Time:"
					+ experimentDB.getStartTime());
		}
		if (experimentXML.getEndTime() != null) {
			experimentDB.setEndTime(experimentXML.getEndTime().longValue());
			logger.debug("               End Time:" + experimentDB.getEndTime());
		}
		if (experimentXML.getApplicationProfile() != null) {
			experimentDB.setApplicationProfile(experimentXML
					.getApplicationProfile());
			logger.debug("    Application Profile:"
					+ experimentDB.getApplicationProfile());
		}
		if (experimentXML.getSubmittedExperimentDescriptor() != null) {
			experimentDB.setSubmittedExperimentDescriptor(experimentXML
					.getSubmittedExperimentDescriptor());
			logger.debug("  Experiment Descriptor:"
					+ experimentDB.getSubmittedExperimentDescriptor());
		}
		if (experimentXML.getManagedExperimentId() != null) {
			experimentDB.setManagedExperimentId(experimentXML
					.getManagedExperimentId().longValue());
			logger.debug("  Managed Experiment id:"
					+ experimentDB.getManagedExperimentId());
		}

		return experimentDB;
	}

	private Experiment getExperimentFromPayload(String payload)
			throws IOException, JAXBException {
		JAXBContext jaxbContext = JAXBContext
				.newInstance(eu.eco2clouds.accounting.datamodel.parser.Experiment.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		eu.eco2clouds.accounting.datamodel.parser.Experiment experimentXML = (eu.eco2clouds.accounting.datamodel.parser.Experiment) jaxbUnmarshaller
				.unmarshal(new StringReader(payload));

		return getExperimentForDB(experimentXML);
	}

	public String getExperimentXMLRepresentation(Experiment experiment)
			throws IOException, JAXBException {
		eu.eco2clouds.accounting.datamodel.parser.Experiment experimentXML = ModelConversion
				.getExperimentXML(experiment);

		JAXBContext jaxbContext = JAXBContext
				.newInstance(eu.eco2clouds.accounting.datamodel.parser.Experiment.class);
		Marshaller marshaller = jaxbContext.createMarshaller();

		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(experimentXML, out);

		return out.toString();
	}

	public String addExperiment(String verb, String payload) throws IOException, JAXBException {
		String experimentString = null;
		Experiment experimentOut = new Experiment();

		Experiment experiment = getExperimentFromPayload(payload);
		logger.debug("Experiment from payload (bonfire experiment id): " + experiment.getBonfireExperimentId());
		logger.debug("Experiment from payload (experiment id): " + experiment.getId());
		
		experimentOut = this.experimentDAO.getById(experiment.getId());
		logger.debug("Experiment OUT : " + experimentOut);
		
		if (experimentOut == null) {
			// Insert experiment in the case it doesn't exist in database
			boolean saved = this.experimentDAO.save(experiment);
			logger.debug(" Saved? : " + saved);

			if (saved) {
				// If element has been inserted, it is printed
				experimentOut = this.experimentDAO.getLastExperiment();
				logger.debug("Created experiment with ID: " + experiment.getId());
				experimentString = getExperimentXMLRepresentation(experimentOut);
				logger.debug("STORED EXPERIMENT " + experimentString);
			}
		}

		return experimentString;
	}

	public String updateExperiment(String verb, String payload)
			throws IOException, JAXBException {
		String experimentString = null;
		Experiment experimentOut = new Experiment();

		Experiment experiment = getExperimentFromPayload(payload);

		experimentOut = this.experimentDAO.getById(experiment.getId());

		if (experimentOut != null) {
			// Update experiment in the case it exists in database
			boolean isUpdated = this.experimentDAO.update(experiment);

			if (isUpdated) {
				// If element has been updated, it is printed
				experimentOut = this.experimentDAO.getById(experiment.getId());
				experimentString = getExperimentXMLRepresentation(experimentOut);
				logger.debug("UPDATED EXPERIMENT " + experimentString);
			}
		}

		return experimentString;
	}

	public String addVM(int experimentId, String payload, List<String> groups) throws IOException, JAXBException {

		VMHost vmHost = new VMHost();

		String vmString = null;
		int result = 0;
		// int resultVmHost = 0;

		int vmId = -1;
		Experiment experiment = new Experiment();

		JAXBContext jaxbContext = JAXBContext
				.newInstance(eu.eco2clouds.accounting.datamodel.parser.VM.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		eu.eco2clouds.accounting.datamodel.parser.VM vmParser = (eu.eco2clouds.accounting.datamodel.parser.VM) jaxbUnmarshaller
				.unmarshal(new StringReader(payload));

		VM vmMessage = new VM(0, vmParser.getBonfireUrl());

		experiment = this.experimentDAO.getExperimentId(experimentId);

		result = this.experimentDAO.addVM(vmMessage, experiment.getId());

		if (result == 1) {

			// experiment =
			// this.experimentDAO.getExperimentByVmBonfireUrl(experimentId,
			// vmMessage.getBonfireUrl());

			experiment = this.experimentDAO.getById(experiment.getId());

			vmId = getVmId(experiment, vmMessage);

			if (vmId == -1)
				return null;

			vmString = new AccountingServiceExperiment(this.experimentDAO)
					.printVMInformation(experimentId, vmId, vmParser.getHost(),
							groups);

			// Obtain host from
			eu.eco2clouds.accounting.datamodel.Host host = this.hostDAO
					.getByName(vmParser.getHost());

			vmHost.setHost(host);

			vmMessage.setId(vmId);

			java.util.Date date = new java.util.Date();
			java.sql.Timestamp ts1 = new Timestamp(date.getTime());

			long tsTime1 = ts1.getTime();

			vmHost.setTimestamp(tsTime1);
			vmHost.setVm(vmMessage);

			this.vmHostDAO.save(vmHost);

		}

		return vmString;
	}

	public int getVmId(Experiment experimentOut, VM vmMessage) {

		VM vm = new VM();

		Set<VM> vms;
		if (experimentOut == null)
			return -1;

		vms = (Set<VM>) experimentOut.getvMs();

		for (VM vmElem : vms) {

			if (vmMessage.getBonfireUrl().equals(vmElem.getBonfireUrl())) {

				vm = vmElem;
			}
		}
		return vm.getId();
	}

	public int getActionId(Experiment experiment, int vmId, Action action) {

		VM vm = new VM();
		Action actionRes = new Action();
		// int actionId;
		Set<VM> vms = new HashSet<VM>();
		Set<Action> actions = new HashSet<Action>();

		if (experiment == null)
			return -1;

		vms = (Set<VM>) experiment.getvMs();

		for (VM vmElem : vms) {
			System.out.println("vmElem.getId()" + vmElem.getId());
			if (vmId == vmElem.getId()) {
				vm = vmElem;
			}
		}
		System.out.println("#####GetActionId - vmId " + vmId);
		System.out.println("#####GetActionId - vm " + vm.getId());
		actions = (Set<Action>) vm.getActions();

		for (Action actionElem : actions) {
			if (action.getTimestamp() == actionElem.getTimestamp()) {
				actionRes = actionElem;
			}
		}

		return actionRes.getId();
	}

	public String updateVM(int experimentId, String payload, List<String> groups)
			throws IOException, JAXBException {

		String vmString = null;
		int result = 0;
		Host host = new Host();
		VMHost vmHost = new VMHost();
		VMHost vmHostDB = new VMHost();
		Experiment experiment = new Experiment();
		java.util.Date date = null;
		java.sql.Timestamp ts1 = null;
		long tsTime1 = 0;

		JAXBContext jaxbContext = JAXBContext
				.newInstance(eu.eco2clouds.accounting.datamodel.parser.VM.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		eu.eco2clouds.accounting.datamodel.parser.VM vmParser = (eu.eco2clouds.accounting.datamodel.parser.VM) jaxbUnmarshaller
				.unmarshal(new StringReader(payload));

		VM vm = new VM(vmParser.getId(), vmParser.getBonfireUrl());

		experiment = this.experimentDAO.getExperimentId(experimentId);

		if (experiment != null) {

			// Update experiment in the case it exists in database
			result = this.experimentDAO.updateVM(vm, experiment.getId());

			// Check if the host of the VM has changed

			System.out.println("vmParser.getHost()" + vmParser.getHost());
			// Obtain host in the database
			if (vmParser.getHost() != null) {
				host = this.hostDAO.getByName(vmParser.getHost());
			}

			// Obtain vm_host record of that vm
			vmHostDB = this.vmHostDAO.getByVmId(vmParser.getId());

			System.out.println("vmHostDB.getHost()"
					+ vmHostDB.getHost().getId());
			System.out.println("host.getId()" + host.getId());

			// Host has change. The new Host is stored in vm_host table
			// associated to that VM.
			if (vmHostDB.getHost().getId() != host.getId()) {

				vmHost.setHost(host);

				// Obtain timeStamp
				date = new java.util.Date();
				ts1 = new Timestamp(date.getTime());
				tsTime1 = ts1.getTime();

				vmHost.setVm(vm);
				vmHost.setTimestamp(tsTime1);

				this.vmHostDAO.save(vmHost);

			}

			if (result == 1) {

				// If element has been updated, it is printed
				experiment = this.experimentDAO.getById(experiment.getId());

				vmString = new AccountingServiceExperiment(this.experimentDAO)
						.printVMInformation(experimentId, vm.getId(), null,
								groups);

			}
		}

		return vmString;

	}

	public String addAction(int experimentId, int vmId, String payload)
			throws IOException, JAXBException {

		String actionString = null;
		int result = 0;
		int actionId;
		Experiment experiment = new Experiment();
		java.util.Date date = null;
		java.sql.Timestamp ts1 = null;
		long tsTime1 = 0;
		List testbeds = new ArrayList<Testbed>();
		String xmlPayload = null;
		String name = null;
		Testbed testbed = new Testbed();

		Client client = new ClientHC();

		JAXBContext jaxbContext = JAXBContext
				.newInstance(eu.eco2clouds.accounting.datamodel.parser.Action.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		eu.eco2clouds.accounting.datamodel.parser.Action actionParser = (eu.eco2clouds.accounting.datamodel.parser.Action) jaxbUnmarshaller
				.unmarshal(new StringReader(payload));

		Action actionMessage = new Action(0, actionParser.getTimestamp(),
				actionParser.getActionLog());
		ActionType actionType = new ActionType(actionParser.getActionType()
				.getId(), actionParser.getActionType().getName());

		actionMessage.setActionType(actionType);

		// Obtain timeStamp
		date = new java.util.Date();
		ts1 = new Timestamp(date.getTime());
		tsTime1 = ts1.getTime();

		actionMessage.setTimestamp(tsTime1);

		result = this.experimentDAO.addAction(actionMessage, vmId);

		if (result == 1) {

			// If Action has been inserted, it is printed
			experiment = this.experimentDAO.getExperimentId(experimentId);

			System.out.println("experiment" + experiment.toString());
			System.out.println("vmId" + vmId);
			System.out.println("actionMessage" + actionMessage);

			Set<VM> vms = (Set<VM>) experiment.getvMs();

			for (VM vmElem : vms) {

				System.out.println("####vm:" + vmElem.getId());
				System.out.println("####Actions:" + vmElem.getActions());
			}

			actionId = getActionId(experiment, vmId, actionMessage);

			System.out.println("actionId" + actionId);

			if (actionId == -1)
				return null;
			actionMessage.setId(actionId);
			actionString = new AccountingServiceExperiment(this.experimentDAO)
					.printAction(experimentId, vmId, actionMessage);

			// HostData info belonging to all the host of every testbed is
			// stored and related to that action
			AccountingServiceTestbed accountingService = new AccountingServiceTestbed(
					testbedDAO, hostDAO, hostDataDAO);
			testbeds = this.testbedDAO.getAll();

			xmlPayload = accountingService.getTestbedsHostsStatusInfoService(
					testbeds, actionMessage);

		}

		return actionString;

	}

	public String updateAction(int experimentId, int vmId, String payload)
			throws IOException, JAXBException {

		String actionString = null;
		int result = 0;
		JAXBContext jaxbContext = JAXBContext
				.newInstance(eu.eco2clouds.accounting.datamodel.parser.Action.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		eu.eco2clouds.accounting.datamodel.parser.Action actionParser = (eu.eco2clouds.accounting.datamodel.parser.Action) jaxbUnmarshaller
				.unmarshal(new StringReader(payload));

		Action actionMessage = new Action(actionParser.getId(),
				actionParser.getTimestamp(), actionParser.getActionLog());
		ActionType actionType = new ActionType(actionParser.getActionType()
				.getId(), actionParser.getActionType().getName());

		actionMessage.setActionType(actionType);

		Experiment experiment = new Experiment();

		experiment = this.experimentDAO.getExperimentId(experimentId);

		if (experiment != null) {

			// Update experiment in the case it exists in database
			result = this.experimentDAO.updateAction(actionMessage);

			if (result == 1) {
				// If element has been inserted, it is printed
				experiment = this.experimentDAO.getExperimentId(experimentId);
				actionString = new AccountingServiceExperiment(
						this.experimentDAO).printAction(experimentId, vmId,
						actionMessage);
			}
		}

		return actionString;

	}

	public String getListOfRunningExperiments() {

		List<Experiment> experiments = new ArrayList<Experiment>();
		StringBuilder xmlResponse = new StringBuilder();

		Date date = new Date();
		long actualDate = date.getTime();

		experiments = this.experimentDAO
				.getListOfRunningExperiments(actualDate);

		xmlResponse.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		xmlResponse
				.append("<collection xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/testbeds\">\n");
		xmlResponse.append("<items offset=\"0\" total=\"" + experiments.size()
				+ "\">\n");

		for (Experiment experiment : experiments) {

			xmlResponse.append("<experiment href=\"/experiments/"
					+ experiment.getId() + "\">\n");
			xmlResponse.append(printElement(experiment));
		}
		xmlResponse.append("</items>\n");
		xmlResponse
				.append("<link href=\"/\" rel=\"parent\" type=\"application/vnd.eco2clouds+xml\"/>\n");
		xmlResponse.append("</collection>\n");

		return xmlResponse.toString();
	}

	public String getExperimentWithVmIP(String vmIp) {

		Experiment experiment = new Experiment();

		StringBuilder xmlResponse = new StringBuilder();

		Date date = new Date();
		long actualDate = date.getTime();

		experiment = this.experimentDAO.getExperimentWithVmIP(actualDate, vmIp);

		if (experiment != null) {

			xmlResponse.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

			xmlResponse.append("<experiment href=\"/experiments/"
					+ experiment.getId() + "\">\n");
			xmlResponse.append(printElement(experiment));
			
			return xmlResponse.toString();
		} else {
			return null;
		}

	}
	
	private void setLinksCollectionParentSelf(Collection collection, String parent, String self) {
		Link linkParent = new Link();
		linkParent.setHref(parent);
		linkParent.setRel("parent");
		linkParent.setType(CONTENT_TYPE_ECO2CLOUDS_XML);
		Link linkSelf = new Link();
		linkSelf.setHref(self);
		linkSelf.setRel("self");
		linkSelf.setType(CONTENT_TYPE_ECO2CLOUDS_XML);
		ArrayList<Link> links = new ArrayList<Link>();
		links.add(linkParent);
		links.add(linkSelf);
		collection.setLinks(links);
	}

	public Response getVMsOfExperiment(int experimentId) {
		Experiment experiment = experimentDAO.getById(experimentId);
		
		if(experiment == null) return buildResponse(400, "No experiment under the ID=" + experimentId);
		
		Set<VM> vMsFromDB = experiment.getvMs();
		List<eu.eco2clouds.accounting.datamodel.parser.VM> vms = new ArrayList<eu.eco2clouds.accounting.datamodel.parser.VM>();
		
		for(VM vm : vMsFromDB) {
			vms.add(ModelConversion.getVMXML(vm, true, experimentId));
		}
		
		Collection collection = new Collection();
		Items items = new Items();
		items.setOffset(0);
		items.setTotal(vms.size());
		items.setvMs(vms);
		collection.setItems(items);
		setLinksCollectionParentSelf(collection, "/experiments/" + experimentId, "/experiments/" + experimentId + "/vms");
		
		String vmsXML = null;
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Collection .class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			marshaller.marshal(collection, out);
			vmsXML = out.toString();

			logger.debug("Testbed Monitoring data: " + vmsXML);

		} catch (JAXBException jaxbExpcetion) {
			logger.debug("Error trying to marshall VM Collection XML " + jaxbExpcetion.getStackTrace());
		}
		
		return buildResponse(200, vmsXML);
	}

	public Response addVMToExperiment(int experimentId, HttpHeaders hh, String payload) {
		Experiment experiment = experimentDAO.getById(experimentId);
		if(experiment == null) return buildResponse(400, "No experiment under the ID=" + experimentId);
		
		boolean allowed = verifyHeaders(experiment, hh);
		
		if(!allowed) return buildResponse(403, "Not allowed to see the experiment");
		
		return null;
	}
	
	private boolean verifyHeaders(Experiment experiment, HttpHeaders hh) {
		List<String> groups = getGroupHeader(hh);
		
		if(groups.size() == 0) return false;
		
		String experimentGroup = experiment.getBonfireGroupId();
		
		if(experimentGroup == null) return false;
		
		for(String group : groups) {
			if(experimentGroup.equals(group)) return true;
		}
		
		return false;
	}
	
	/**
	 * Retrieves the last monitoring value for a metric associated to an experiment
	 * @param bonfireExperimentId BonFIRE Experiment ID
	 * @param metricName 
	 * @return the metric value
	 */
	public Response getExperimentLastMetricValue(long bonfireExperimentId, String metricName) {

		String xmlResponse = null;
		dbConnector.connect();
		long currentTime = System.currentTimeMillis();
		currentTime = (currentTime - PERIOD)/1000l;
		ResultSet results = null;
				
		try {
			PreparedStatement statement = dbConnector.connection
					.prepareStatement("SELECT items_1.* " +
									  "FROM items AS items_1 " +
									  "INNER JOIN ( " +
									  	"SELECT MAX(items_2.id_items) AS max_id " +
									  	"FROM items AS items_2, " + 
									  	      "metrics_experiments AS experiments, " +
									  	      "experiments_items AS st " +
									  	"WHERE experiments.id_metrics_experiments=? " + 
									  	"AND experiments.id_metrics_experiments = st.fk_metrics_experiments " +
									  	"AND items_2.id_items = st.fk_items AND items_2.name=?) " + 
									  	"AS max_items on max_items.max_id = items_1.id_items;");

			// Parameters index from 1
			statement.setString(1, "" + bonfireExperimentId);
			statement.setString(2, metricName);

			logger.debug("Query statement for application metric: " + statement.toString());

			results = statement.executeQuery();

			// Convert to XML //TODO xmlResponse =  printHostMonitoring(results, "testbed", name);

		} catch (SQLException e) {
			MessageHandler.error("Error setting application metric " + metricName + " for experiment: " + bonfireExperimentId
					             + " Exception: " + e.getMessage() + " Skipping.");
		}

		dbConnector.disconnect(); // Close and clean up all JDBC resources

		if (xmlResponse != null)
			return buildResponse(200, xmlResponse);
		else
			return buildResponse(404, "No items for the application metric " + metricName);
	}
	
//	protected String getApplicationMetricXMLRepresentationFromSQLQuery(ResultSet results) {
//		
//	}
}
