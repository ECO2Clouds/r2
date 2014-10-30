package eu.eco2clouds.accounting.rest;

import static eu.eco2clouds.accounting.Dictionary.CONTENT_TYPE_ECO2CLOUDS_XML;
import static eu.eco2clouds.accounting.Dictionary.CONTENT_TYPE_JSON;
import static eu.eco2clouds.accounting.Dictionary.CONTENT_TYPE_XML;

import java.io.IOException;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import eu.eco2clouds.accounting.datamodel.Experiment;
import eu.eco2clouds.accounting.rest.service.AccountingServiceAbstractImpl;
import eu.eco2clouds.accounting.rest.service.AccountingServiceActionType;
import eu.eco2clouds.accounting.rest.service.AccountingServiceExperiment;
import eu.eco2clouds.accounting.rest.service.AccountingServiceTestbed;
import eu.eco2clouds.accounting.service.ExperimentDAO;
import eu.eco2clouds.accounting.service.TestbedDAO;
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
 *
 * Rest Service that exposes all the stored information of the ECO2Clouds
 * Accounting Service to the ECO2Clouds Scheduler
 */
@Path("/")
@Component
@Scope("request")
public class AccountingServiceRest extends AccountingServiceAbstractImpl {
	private static Logger logger = Logger.getLogger(AccountingServiceRest.class);
	protected Client client = new ClientHC();

	public AccountingServiceRest() {
	}

	public AccountingServiceRest(TestbedDAO testbedDAO) {
		this.testbedDAO = testbedDAO;
	}

	public AccountingServiceRest(TestbedDAO testbedDAO, Client client) {
		this.testbedDAO = testbedDAO;
		this.client = client;
	}

	public AccountingServiceRest(ExperimentDAO experimentDAO) {
		this.experimentDAO = experimentDAO;
	}

	/**
	 * Returns in ECO2Clouds XML format all the links from the root path that
	 * can be query for the Accounting Service
	 * 
	 * @return the ECO2Clouds Rest link path document...
	 */
	@GET
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public String list() {
		logger.info("REQUEST for listing /");

		return getList();
	}

	/**
	 * Returns the list of available testbeds from where we can get metrics,
	 * host information, etc...
	 * 
	 * @return XML Document with the list of testbeds
	 */
	@GET
	@Path("testbeds")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public String getTestbeds() {
		logger.info("REQUEST for /testbeds");

		AccountingServiceTestbed accountingService = new AccountingServiceTestbed(testbedDAO, client);
		return accountingService.getListOfTesbeds();
	}

	/**
	 * Returns the infomration of an specific testbed If the testbed it is not
	 * in the database, it returns 404 with empty payload
	 * 
	 * @param name
	 *            of the testbed
	 * @return XML information with the different details and urls of the
	 *         testbed
	 */
	@GET
	@Path("testbeds/{tesbed_name}")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getTestbed(@PathParam("tesbed_name") String name) {
		logger.info("REQUEST for /testbeds/" + name);

		AccountingServiceTestbed accountingService = new AccountingServiceTestbed(testbedDAO, client);
		String xmlPayload = accountingService.getTestbedByName(name);

		if (xmlPayload != null)
			return buildResponse(200, xmlPayload);
		else
			return buildResponse(404,
					"Testbed does not exist in the Accounting Service Scheduler Database");
	}

	/**
	 * Returns the host status info for a site in XML format... It returns 404
	 * and empty payload if it is not possible to find the site in the database
	 * 
	 * @param name
	 *            of the testbed
	 * @return XML information about the host status at tha time...
	 */
	@GET
	@Path("testbeds/{tesbed_name}/status")
	@Produces(CONTENT_TYPE_XML)
	public Response getTestbedHostsStatusInfo(@PathParam("tesbed_name") String name) {
		logger.info("REQUEST for /testbeds/" + name + "/status");

		AccountingServiceTestbed accountingService = new AccountingServiceTestbed(testbedDAO, hostDAO, hostDataDAO, client);
		
		String xmlPayload = accountingService.getTestbedHostsStatusInfoService(name, null);

		if (xmlPayload != null)
			return buildResponse(200, xmlPayload);
		else
			return buildResponse(404, "Testbed does not exist in the Accounting Service Scheduler Database");
	}
	
	/**
	 * Returns the hosts details of an give testbed. It returns 404 and empty payload if it is not possible to find the site in the database
	 * @param name
	 * @return
	 */
	@GET
	@Path("testbeds/{testbed_name}/hosts")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getHostsOfTestbed(@PathParam("testbed_name") String name) {
		logger.info("REQUEST for /testbeds/" + name + "/hosts");
		
		AccountingServiceTestbed accountingService = new AccountingServiceTestbed(testbedDAO, hostDAO, hostDataDAO, client);
		
		String xmlPayload = accountingService.getHostsOfTestbedFromDatabase(name);

		if (xmlPayload != null)
			return buildResponse(200, xmlPayload);
		else
			return buildResponse(404, "Testbed does not exist in the Accounting Service Scheduler Database");
	}
	
	/**
	 * Returns the specific host details of an give testbed. It returns 404 and empty payload if it is not possible to find the site in the database
	 * @param name
	 * @return
	 */
	@GET
	@Path("testbeds/{testbed_name}/hosts/{host_name}")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getHostOfTestbed(@PathParam("testbed_name") String testbedName, @PathParam("host_name") String hostName) {
		logger.info("REQUEST for /testbeds/" + testbedName + "/hosts/" + hostName);
		
		AccountingServiceTestbed accountingService = new AccountingServiceTestbed(testbedDAO, hostDAO, hostDataDAO, client);
		
		String xmlPayload = accountingService.getHostOfTestbedFromDatabase(testbedName, hostName);

		if (xmlPayload != null)
			return buildResponse(200, xmlPayload);
		else
			return buildResponse(404, "Testbed does not exist in the Accounting Service Scheduler Database");
	}

	/**
	 * Returns the list of available experiments with a particular set of
	 * groupIds
	 * 
	 * @return XML Document with the list of Experiments
	 * @throws JAXBException
	 * @throws IOException
	 */
	@GET
	@Path("/experiments/")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getExperiments(@Context HttpHeaders hh) throws IOException, 
			JAXBException {
		logger.info("REQUEST for /experiments");

		AccountingServiceExperiment accountingService = new AccountingServiceExperiment(experimentDAO);
		return accountingService.service("GET", -1, hh, null, null);

	}

	/**
	 * Returns experiment with a certain Id and groupId. It returns 403 if user
	 * does not belong to the group of that experiment and returns 404 if there
	 * is no experiment with that id
	 * 
	 * @param id
	 *            - id of the experiment
	 * @return XML Document with the list of Experiments
	 * @throws JAXBException
	 * @throws IOException
	 */
	@GET
	@Path("/experiments/{id}")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getExperiment(@PathParam("id") int id,
			@Context HttpHeaders hh) throws IOException, JAXBException {
		logger.info("REQUEST for /experiment for the experiment:" + id);

		AccountingServiceExperiment accountingService = new AccountingServiceExperiment(
				experimentDAO);
		return accountingService.service("GET", id, hh, null, null);

	}
	
	@GET
	@Path("/experiments/{id}/report")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getExperimentReport(@PathParam("id") int id,
			@Context HttpHeaders hh) throws IOException, JAXBException {
		logger.info("/experiments/" + id + "/report");

		Experiment experiment = this.experimentDAO.getById(id);
		
		AccountingServiceTestbed accountingService = new AccountingServiceTestbed();
		return accountingService.getPowerUsageOfAnExperiment("" + experiment.getBonfireExperimentId());
	}
	
	

	/**
	 * Returns application-profile from experiment with a certain id and
	 * groupId. It returns 403 if user does not belong to the group of that
	 * experiment and returns 404 if there is no experiment with that id
	 * 
	 * @param id
	 *            - id of the experiment
	 * @return JSON Document of application-profile
	 * @throws JAXBException
	 * @throws IOException
	 */
	@GET
	@Path("/experiments/{id}/application-profile")
	@Produces(CONTENT_TYPE_JSON)
	public Response getApplicationProfileFromExperiment(
			@PathParam("id") int id, @Context HttpHeaders hh)
			throws IOException, JAXBException {
		logger.info("REQUEST for /experiment/" + id + "/application-profile");

		AccountingServiceExperiment accountingService = new AccountingServiceExperiment(
				experimentDAO);
		return accountingService.service("GET", id, hh, "application_profile",
				null);
	}

	/**
	 * Returns submitted-experiment-descriptor from experiment with a certain id
	 * and groupId. It returns 403 if user does not belong to the group of that
	 * experiment and returns 404 if there is no experiment with that id
	 * 
	 * @param id
	 *            - id of the experiment
	 * @return JSON Document of submitted-experiment-descriptor
	 * @throws JAXBException
	 * @throws IOException
	 */
	@GET
	@Path("/experiments/{id}/submitted-experiment-descriptor")
	@Produces(CONTENT_TYPE_JSON)
	public Response getSubmittedExperimentDescriptorFromExperiment(
			@PathParam("id") int id, @Context HttpHeaders hh)
			throws IOException, JAXBException {
		logger.info("REQUEST for /experiment/" + id
				+ "/submitted-experiment-descriptor");

		AccountingServiceExperiment accountingService = new AccountingServiceExperiment(
				experimentDAO);
		return accountingService.service("GET", id, hh, "submitted_experiment_descriptor", null);
	}

	/**
	 * Updates database with the experiments received Returns the list of
	 * experiments updated in the database
	 * 
	 * @return XML Document with the list of Experiments
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws JAXBException
	 */
	@POST
	@Path("/experiments/")
	@Consumes(CONTENT_TYPE_ECO2CLOUDS_XML)
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response addExperiment(@Context HttpHeaders hh, String payload)
			throws IOException, JAXBException {
		logger.info("Insert /experiments");

		AccountingServiceExperiment accountingService = new AccountingServiceExperiment(
				experimentDAO);
		return accountingService.service("POST", -1, null, null, payload);
	}

	@PUT
	@Path("/experiments/{id}")
	@Consumes(CONTENT_TYPE_ECO2CLOUDS_XML)
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response updateExperiment(@PathParam("id") int id,
			@Context HttpHeaders hh, String payload) throws IOException,
			JAXBException {
		logger.info("UPDATE /experiments");

		AccountingServiceExperiment accountingService = new AccountingServiceExperiment(experimentDAO);
		return accountingService.service("PUT", id, null, null, payload);
	}

	@GET
	@Path("/action_types/")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getActionTypes() throws IOException, JAXBException {
		logger.info("REQUEST for /action_types/");

		AccountingServiceActionType accountingService = new AccountingServiceActionType(
				actionTypeDAO);
		return accountingService.service("GET", -1, null, null);
	}

	@GET
	@Path("/action_types/{id}")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getActionTypes(@PathParam("id") int id) throws IOException,
			JAXBException {
		logger.info("REQUEST for /action_types/+ id");

		AccountingServiceActionType accountingService = new AccountingServiceActionType(
				actionTypeDAO);
		return accountingService.service("GET", id, null, null);
	}

	@POST
	@Path("/action_types/")
	@Consumes(CONTENT_TYPE_ECO2CLOUDS_XML)
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response addActionTypes(@Context HttpHeaders hh, String payload)
			throws IOException, JAXBException {
		logger.info("ADD /action_types");

		AccountingServiceActionType accountingService = new AccountingServiceActionType(
				actionTypeDAO);
		return accountingService.service("POST", -1, hh, payload);
	}

	@PUT
	@Path("/action_types/")
	@Consumes(CONTENT_TYPE_ECO2CLOUDS_XML)
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response updateActionTypes(@Context HttpHeaders hh, String payload)
			throws IOException, JAXBException {
		logger.info("UPDATE /action_types");

		AccountingServiceActionType accountingService = new AccountingServiceActionType(
				actionTypeDAO);
		return accountingService.service("PUT", -1, hh, payload);
	}

	/**
	 * Returns vms from experiments with a certain experiment_id and groupId. It
	 * returns 403 if user does not belong to the group of that experiment and
	 * returns 404 if there is no experiment with that experiment_id
	 * 
	 * @param id
	 *            - id of the experiment
	 * @return XML Document with vms a that experiment
	 * @throws JAXBException
	 * @throws IOException
	 */
	@GET
	@Path("/experiments/{experiment_id}/vms")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getVMSFromExperiment(@PathParam("experiment_id") int experimentId,
			                             @Context HttpHeaders hh) throws IOException, JAXBException {
		logger.info("REQUEST for /experiment/" + experimentId + "/vms");

		AccountingServiceExperiment accountingService = new AccountingServiceExperiment(experimentDAO);
		return accountingService.getVMsOfExperiment(experimentId);
	}

	@POST
	@Path("/experiments/{experiment_id}/vms")
	@Consumes(CONTENT_TYPE_ECO2CLOUDS_XML)
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response addVMFromExperiment(@PathParam("experiment_id") int experimentId, 
			                            @Context HttpHeaders hh, 
			                            String payload) throws IOException, JAXBException {
		logger.info("ADD for /experiment/" + experimentId + "/vms");

		AccountingServiceExperiment accountingService = new AccountingServiceExperiment(experimentDAO, hostDAO, vmHostDAO);
		return accountingService.addVMToExperiment(experimentId, hh, payload);
	}

	@PUT
	@Path("/experiments/{experiment_id}/vms")
	@Consumes(CONTENT_TYPE_ECO2CLOUDS_XML)
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response updateVMFromExperiment(
			@PathParam("experiment_id") int experiment_id,
			@Context HttpHeaders hh, String payload) throws IOException,
			JAXBException {
		logger.info("UPDATE for /experiment/" + experiment_id + "/vms");

		AccountingServiceExperiment accountingService = new AccountingServiceExperiment(
				experimentDAO, hostDAO, vmHostDAO);
		return accountingService.serviceVM("PUT", experiment_id, -1, hh,
				payload);
	}

	@GET
	@Path("/experiments/{experiment_id}/vms/{vm_id}")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getVMIDFromExperiment(
			@PathParam("experiment_id") int experiment_id,
			@PathParam("vm_id") int vm_id, @Context HttpHeaders hh)
			throws IOException, JAXBException {
		logger.info("REQUEST for /experiment/" + experiment_id + "/vms/"
				+ vm_id);

		AccountingServiceExperiment accountingService = new AccountingServiceExperiment(
				experimentDAO);
		return accountingService.serviceVM("GET", experiment_id, vm_id, hh,
				null);
	}

	@POST
	@Path("/experiments/{experiment_id}/vms/{vm_id}/actions")
	@Consumes(CONTENT_TYPE_ECO2CLOUDS_XML)
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response addActionFromVmId(
			@PathParam("experiment_id") int experiment_id,
			@PathParam("vm_id") int vm_id, @Context HttpHeaders hh,
			String payload) throws IOException, JAXBException {
		logger.info("ADD for /experiment/" + experiment_id + "/vms/" + vm_id);

		AccountingServiceExperiment accountingService = new AccountingServiceExperiment(
				experimentDAO, testbedDAO, hostDAO, hostDataDAO);
		return accountingService.serviceVM("POST", experiment_id, vm_id, hh,
				payload);
	}

	@PUT
	@Path("/experiments/{experiment_id}/vms/{vm_id}/actions")
	@Consumes(CONTENT_TYPE_ECO2CLOUDS_XML)
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response updateActionFromVmId(
			@PathParam("experiment_id") int experiment_id,
			@PathParam("vm_id") int vm_id, @Context HttpHeaders hh,
			String payload) throws IOException, JAXBException {
		logger.info("UPDATE for /experiment/" + experiment_id + "/vms/"
				+ vm_id);

		AccountingServiceExperiment accountingService = new AccountingServiceExperiment(
				experimentDAO);
		return accountingService.serviceVM("PUT", experiment_id, vm_id, hh,
				payload);
	}

	/**
	 * Returns the list of running experiments
	 * 
	 * @return XML Document with the list of running experiments
	 */
	@GET
	@Path("/experiments/status")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getListofRunningExperiments(
			@QueryParam("status") String status) {
		logger.info("REQUEST status for /experiments");

		String xmlResponse = null;

		AccountingServiceExperiment accountingService = new AccountingServiceExperiment(
				experimentDAO);

		if (status.equals("running")) {

			xmlResponse = accountingService.getListOfRunningExperiments();

		}

		if (xmlResponse == null) {
			return buildResponse(500, "No experiments");
		} else {
			return buildResponse(200, xmlResponse.toString());
		}
	}

	/**
	 * Returns the list of running experiments
	 * 
	 * @return XML Document with the list of running experiments
	 */
	@GET
	@Path("/experiments/vmip")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getExperimentWithVmIP(@QueryParam("vm_ip") String vmIp) {
		logger.info("REQUEST for /experiments/ip/?vm-ip=\"192.1..");

		String xmlResponse = null;

		AccountingServiceExperiment accountingService = new AccountingServiceExperiment(
				experimentDAO);

		xmlResponse = accountingService.getExperimentWithVmIP(vmIp);

		if (xmlResponse == null) {
			return buildResponse(500, "No experiments with vm with ip : " + vmIp);
		} else {
			return buildResponse(200, xmlResponse.toString());
		}
	}

	/**
	 * Returns the list of available testbeds from where we can get metrics,
	 * host information, etc...
	 * 
	 * @return XML Document with the list of testbeds
	 */
	@GET
	@Path("/testbeds/{location}/{hostname}/monitoring")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getHostMonitoring(@PathParam("hostname") String hostname) {
		logger.info("REQUEST for /testbeds");

		AccountingServiceTestbed accountingService = new AccountingServiceTestbed();
		return accountingService.getHostMonitoring(hostname);
	}
	
	/**
	 * Returns the list of available testbeds from where we can get metrics,
	 * host information, etc...
	 * 
	 * @return XML Document with the list of testbeds
	 */
	@GET
	@Path("/locations/{location}/computes/{vm_id}/monitoring")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getVMMonitoring(@PathParam("location") String location, @PathParam("vm_id") String vmId) {
		logger.info("REQUEST for /locations/" + location + "/computes/" + vmId + "/monitoring");

		AccountingServiceTestbed accountingService = new AccountingServiceTestbed();
		return accountingService.getVMMonitoring(location, vmId);
	}
	
	/**
	 * Returns a report of the energy consumed by a VM.
	 * 
	 * @return XML Report of the VM
	 */
	@GET
	@Path("/locations/{location}/computes/{vm_id}/report")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getVMReport(@PathParam("location") String location, @PathParam("vm_id") String vmId) {
		logger.info("REQUEST for /locations/" + location + "/computes/" + vmId + "/monitoring");

		AccountingServiceTestbed accountingService = new AccountingServiceTestbed();
		return accountingService.getPowerUsageOfAVM(location, vmId);
	}

	/**
	 * Returns the list of available testbeds from where we can get metrics,
	 * host information, etc...
	 * 
	 * @return XML Document with the list of testbeds
	 */
	@GET
	@Path("/testbeds/{location}/hosts/{hostname}/monitoring")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getHostMonitoring(@PathParam("location") String testbed,
			@PathParam("hostname") String hostname,
			@QueryParam("startTime") String startTime,
			@QueryParam("endTime") String endTime) {
		logger.info("REQUEST for /testbeds/" + testbed + "/hosts/" + hostname
				+ "/monitoring");

		AccountingServiceTestbed accountingService = new AccountingServiceTestbed();

		if (startTime != null && endTime != null) {
			return accountingService.getHostMonitoring(testbed, hostname,
					startTime, endTime);
		} else {
			return accountingService.getHostMonitoring(testbed, hostname);
		}
	}

	/**
	 * Returns the list of consumed Co2 from from two dates
	 * 
	 * @return XML Document with the list of testbeds
	 */
	@GET
	@Path("/testbeds/{location}/monitoring/co2")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getTestbedCo2Monitoring(
			@PathParam("location") String testbed,
			@QueryParam("startTime") String startTime,
			@QueryParam("endTime") String endTime) {
		logger.info("REQUEST for /testbeds/" + testbed + "/monitoring/co2");

		AccountingServiceTestbed accountingService = new AccountingServiceTestbed();

		return accountingService.getTestbedMonitoringCo2(testbed, startTime,
				endTime);
	}

	/**
	 * Returns the list of available testbeds from where we can get metrics,
	 * host information, etc...
	 * 
	 * @return XML Document with the list of testbeds
	 */
	@GET
	@Path("/testbeds/{location}/monitoring")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getTestbedMonitoring(@PathParam("location") String location) {
		logger.info("REQUEST for /testbeds");

		AccountingServiceTestbed accountingService = new AccountingServiceTestbed();
		return accountingService.getTestbedMonitoring(location);
	}
	

}
