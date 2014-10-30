package eu.eco2clouds.scheduler.rest;

import static eu.eco2clouds.accounting.Dictionary.CONTENT_TYPE_ECO2CLOUDS_XML;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import eu.eco2clouds.accounting.datamodel.parser.Collection;
import eu.eco2clouds.accounting.datamodel.parser.Experiment;
import eu.eco2clouds.accounting.datamodel.parser.ExperimentReport;
import eu.eco2clouds.accounting.datamodel.parser.Host;
import eu.eco2clouds.accounting.datamodel.parser.HostMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.Items;
import eu.eco2clouds.accounting.datamodel.parser.Link;
import eu.eco2clouds.accounting.datamodel.parser.Testbed;
import eu.eco2clouds.accounting.datamodel.parser.TestbedMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.TotalCo2;
import eu.eco2clouds.accounting.datamodel.parser.VM;
import eu.eco2clouds.accounting.datamodel.parser.VMMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.VMReport;
import eu.eco2clouds.api.bonfire.occi.datamodel.Compute;
import eu.eco2clouds.applicationProfile.datamodel.ApplicationProfile;
import eu.eco2clouds.applicationProfile.datamodel.ExperimentDescriptor;
import eu.eco2clouds.applicationProfile.datamodel.ResourceCompute;
import eu.eco2clouds.applicationProfile.parser.Parser;
import eu.eco2clouds.scheduler.accounting.client.AccountingClient;
import eu.eco2clouds.scheduler.accounting.client.AccountingClientHC;
import eu.eco2clouds.scheduler.bonfire.BFClientScheduler;
import eu.eco2clouds.scheduler.bonfire.BFClientSchedulerImpl;
import eu.eco2clouds.scheduler.conf.Configuration;
import eu.eco2clouds.scheduler.designtime.FileOutput;
import eu.eco2clouds.scheduler.designtime.InitialDeployment;
import eu.eco2clouds.scheduler.experiment.Controller;

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
 */
public class RestService {
	private static Logger logger = Logger.getLogger(RestService.class);
	protected AccountingClient ac = new AccountingClientHC(Configuration.accountingServiceUrl);
	protected String userId;
	protected String groupEco = "eco2clouds";
	protected List<String> groups;
	protected BFClientScheduler bfClient = new BFClientSchedulerImpl();
	
	protected String getListRoot() {
		logger.debug("BUILDING PATH / MESSAGE");
		
		Date date = new Date();
		
		String message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
						 + "<root xmlns=\"http://scheduler.eco2clouds.eu/doc/schemas/xml\" href=\"/\">\n"
						 	+ "<version>0.1</version>\n"
						 	+ "<timestamp>" + date.getTime() + "</timestamp>\n"
						 	+ "<link rel=\"experiments\" href=\"/experiments\" type=\"application/vnd.eco2clouds+xml\"/>\n"
						 + "</root>";
		
		logger.debug("RETURNED MESSAGE:" + message);
		
		return message;
	}
	
	protected String getUserId(MultivaluedMap<String, String> map) {
		String userId = "";
		Set<String> keys = map.keySet();
		
		for (String key : keys) {
			String keyparsed = key.toLowerCase();
			logger.debug("parsing key" + keyparsed + " to find userId");
			
			if (keyparsed.equals("x-bonfire-asserted-id")	|| keyparsed.equals("x_bonfire_asserted_id")) {
				userId = map.get(key).get(0);
				logger.debug("user id found " + userId);
			}
		}
		return userId;
	}
	
	protected List<String> getGroupsIds(MultivaluedMap<String, String> map) {
		List<String> groups = new ArrayList<String>();
		
		Set<String> keys = map.keySet();
		
		for(String key : keys) {
			String keyparsed = key.toLowerCase();
			logger.debug("parsing key" + keyparsed + " to find groups");
			
			String groupsString = "";
			if(keyparsed.equals("x-bonfire-groups-id")	|| keyparsed.equals("x_bonfire_groups_id")) {
				groupsString = map.get(key).get(0);
				logger.debug("groups header found " + groupsString);
			}
			
			if(!groupsString.equals("")) groups = Arrays.asList(groupsString.split("\\s*,\\s*"));
		}
		
		return groups;
	}
	
	protected ExperimentDescriptor checkIfItIsNecessaryToOptimize(String applicationProfile) throws JsonParseException, JsonMappingException, IOException {
		ApplicationProfile applicationProfileObject = Parser.getApplicationProfile(applicationProfile);
		
		ExperimentDescriptor ed = null;
		
		if(applicationProfileObject.isOptimize()) {
			ed = InitialDeployment.computeDeployment(applicationProfile);
		} else {
			ed = Parser.getApplicationProfile(applicationProfile).getExperimentDescriptor();
			
			List<ResourceCompute> computes = ed.getResourcesCompute();
			for(ResourceCompute compute: computes) {
				if(compute.getCompute().getLocations() == null || compute.getCompute().getLocations().size() == 0) {
					List<String> locations = new ArrayList<String>();
					locations.add("uk-epcc");
					locations.add("fr-inria");
					locations.add("de-hlrs");
					
					Collections.shuffle(locations);
					
					String location = locations.get(0);
					
					ArrayList<String> locations2 = new ArrayList<String>();
					locations2.add(location);
					
					compute.getCompute().setLocations(locations2);
				} else if(compute.getCompute().getLocations().size() > 1) {
					List<String> locations = compute.getCompute().getLocations();
					Collections.shuffle(locations);
					
					String location = locations.get(0);
					ArrayList<String> locations2 = new ArrayList<String>();
					locations2.add(location);
					
					compute.getCompute().setLocations(locations2);
				}
			}
		}
		
		return ed;
	}
	
	protected Response processApplicationProfile(String applicationProfile) {
		long bonfireExpId=0;
		Experiment experiment = new Experiment();
		boolean expCreation=false;
		try {
			ApplicationProfile applicationProfileObject = Parser.getApplicationProfile(applicationProfile);
			logger.debug("Application profile parsed ok: " + applicationProfileObject);
			
			ExperimentDescriptor ed = checkIfItIsNecessaryToOptimize(applicationProfile);
			logger.debug("ED to be Submitted: " + ed);
		
			
			
			//TODO: enable experiment creation by removing the following comment block		
			
			if(applicationProfileObject.getExperimentDescriptor().getAggregator() != null) {
				ed.setAggregator(applicationProfileObject.getExperimentDescriptor().getAggregator());
			}
			
		
			Controller controller = new Controller();				
			try {
				experiment = controller.deployExperimentDescritor(userId, "eco2clouds", ed, applicationProfile);
				bonfireExpId = experiment.getBonfireExperimentId();
			} 
			catch (InterruptedException e){
				// TODO Auto-generated catch block
				e.printStackTrace();				
			}
			
			while(expCreation==false)
			{
				if(bonfireExpId!=0)
					expCreation=true;
				else
					try 
					  {Thread.sleep(1000);}
						 
					catch (InterruptedException e) 
						{e.printStackTrace();}							
			}
			if(bonfireExpId==-1){
				//TODO: report Error to user
				logger.debug("Error creating BonFIRE experiment");
				return buildResponse(201, "Errow in creating Experiment.");
			}			
			else{
				//TODO: return Experiment_ID to user
				logger.debug("BonFIRE experiment created with id:"+bonfireExpId);
				FileOutput.outputToFile("EXPERIMENT DEPLOYED\n");
				return buildResponse(201, getExperimentFromAccountingServiceString(experiment.getId()));
			}
					
		} catch(JsonParseException e) {
			return processApplicationProfileException(e);
		} catch(JsonMappingException e) {
			return processApplicationProfileException(e);
		} catch(IOException e) {
			return processApplicationProfileException(e);
		} catch(JAXBException e) {
			return processApplicationProfileException(e);
		}
		
		//TODO: make the following comment if removing the above comment block
		//return buildResponse(201, "Experiment submitted.");
	}
	
	private Response processApplicationProfileException(Exception e) {
		logger.info("Exception parsing application profile: " + e);
		return buildResponse(400, "Error parsing Application Profile.");
	}
	
	private Response buildResponse(int code, String payload) {
		ResponseBuilder builder = Response.status(code);
		builder.entity(payload);

		return builder.build();
	}
	
	protected void parsingHeaders(HttpHeaders headers) {
		MultivaluedMap<String, String> map = headers.getRequestHeaders();
		this.groups = getGroupsIds(map);
		this.userId = getUserId(map);
	}

	protected Response getExperimentsFromAccountingService() throws JAXBException {
		List<Experiment> experiments = ac.getListOfExperiments("", groupEco);
		Collection collection = new Collection();
		Items items = new Items();
		items.setExperiments(experiments);
		collection.setItems(items);
		items.setTotal(experiments.size());
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(collection, out);
		String output = out.toString();
		logger.debug("Retrieved colleciton of experiments: " + output);
		
		return buildResponse(200, output);
	}
	
	protected Response getExperimentFromAccountingService(int id) throws JAXBException {
		
		String output = getExperimentFromAccountingServiceString(id);
		
		return buildResponse(200, output);
	}
	
	private String getExperimentFromAccountingServiceString(int id) throws JAXBException {
		Experiment experiment= ac.getExperiment(id, "", groupEco);
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Experiment.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(experiment, out);
		String output = out.toString();
		logger.debug("Retrieved colleciton of experiments: " + output);
		
		return output;
	}
	
	protected Response getComputeOfLocation(String location, String id) throws JAXBException {
		String url = "/locations/" + location + "/computes/" + id;
		
		if(userId == null) userId = "dperez";
		else if(userId.equals("")) userId = "dperez";
		Compute compute = bfClient.getVM(userId, url);
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Compute.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(compute, out);
		String output = out.toString();
		logger.debug("Retrieved compute from bonfire-api: " + output);
		
		return buildResponse(200, output);	
	}
	
	protected Response changeStateOfCompute(String location, String id, String payload) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Compute.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Compute compute = (Compute) jaxbUnmarshaller.unmarshal(new StringReader(payload));
		compute.setHref("/locations/" + location + "/computes/" + id);
		
		if(userId == null) userId = "dperez";
		else if(userId.equals("")) userId = "dperez";
		
		if(compute == null) {
			return buildResponse(400, "Wrong compute format!!!");
		} else if(compute.getState() == null) {
			return buildResponse(400, "Compute state information missing!!!");
		} else if(compute.getState().equals("suspended")) {
			compute = bfClient.suspendVM(userId, compute);
		} else if(compute.getState().equals("shutdown")) {
			compute = bfClient.shutdownVM(userId, compute);
		} else if(compute.getState().equals("stopped")) {
			compute = bfClient.stopVM(userId, compute);
		} else if(compute.getState().equals("cancel")) {
			compute = bfClient.cancelVM(userId, compute);
		} else if(compute.getState().equals("resume")) {
			compute = bfClient.resumenVM(userId, compute);
		} else {
			return buildResponse(400, "State requested it is not valid");
		}
		
		jaxbContext = JAXBContext.newInstance(Compute.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(compute, out);
		String output = out.toString();
		logger.debug("Retrieved compute: " + output);
		
		return buildResponse(200, output);
	}
	
	protected Response getVMsOfExperiment(int id) throws JAXBException {
		List<VM> vms = ac.getListOfVMsOfExperiment(id, "", groupEco);
		
		if(vms != null) {
			Collection collection = new Collection();
			Items items = new Items();
			items.setvMs(vms);
			items.setTotal(vms.size());
			items.setOffset(0);
			collection.setItems(items);
			
			JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			marshaller.marshal(collection, out);
			String output = out.toString();
			logger.debug("Retrieved colleciton of vms: " + output);
			
			return buildResponse(200, output);
		} else {
			Collection collection = new Collection();
			Items items = new Items();
			items.setTotal(0);
			items.setOffset(0);
			collection.setItems(items);
			
			JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			marshaller.marshal(collection, out);
			String output = out.toString();
			logger.debug("Retrieved colleciton of vms: " + output);
			
			return buildResponse(200, output);
		}
	}
	
	protected Response getTotalCo2FromExperiment(int experimentId) throws JAXBException {
		double total = ac.getCo2Consumption(experimentId);
		
		TotalCo2 totalCo2 = new TotalCo2();
		totalCo2.setValue(total);
		totalCo2.setUnity("g");
		totalCo2.setName("Grams of Co2 produced in the execution of the experiment");
		
		JAXBContext jaxbContext = JAXBContext.newInstance(TotalCo2.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(totalCo2, out);
		String output = out.toString();
		logger.debug("Retrieved colleciton of experiments: " + output);
		
		return buildResponse(200, output);
	}
	
	protected Response getTestbedsFromAccountingService() throws JAXBException {
		List<Testbed> testbeds = ac.getListOfTestbeds();
		
		//We build the collection:
		Collection collection = new Collection();
		Items items = new Items();
		items.setOffset(0);
		items.setTotal(testbeds.size());
		items.setTestbeds(testbeds);
		collection.setItems(items);
		Link link = new Link();
		link.setHref("/");
		link.setRel("parent");
		link.setType(CONTENT_TYPE_ECO2CLOUDS_XML);
		ArrayList<Link> links = new ArrayList<Link>();
		links.add(link);
		collection.setLinks(links);
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(collection, out);
		String output = out.toString();
		logger.debug("Retrieved testbedMonitoring from AccountingService: " + output);
		
		return buildResponse(200, output);
	}
	
	protected Response getTestbedFromAccountingService(String location) throws JAXBException {
		Testbed testbed = ac.getTestbed(location);
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Testbed.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(testbed, out);
		String output = out.toString();
		logger.debug("Retrieved testbed from AccountingService: " + output);
		
		return buildResponse(200, output);
	}
	
	protected Response getHostOfTestbedFromAccountin(String location, String hostname) throws JAXBException {
		Host host = ac.getHostOfTestbed(location, hostname);
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Host.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(host, out);
		String output = out.toString();
		logger.debug("Retrieved host from AccountingService: " + output);
		
		return buildResponse(200, output);
	}
	
	
	protected Response getHostsOfTestbedsFromAccounting(String location) throws JAXBException {
		List<Host> hosts = ac.getHostsOfTesbed(location); 
		
		Collection collection = new Collection();
		Items items = new Items();
		items.setOffset(0);
		items.setTotal(hosts.size());
		items.setHosts(hosts);
		Link linkParent = new Link();
		linkParent.setHref("/testbeds/" + location);
		linkParent.setRel("parent");
		linkParent.setType(CONTENT_TYPE_ECO2CLOUDS_XML);
		ArrayList<Link> links = new ArrayList<Link>();
		links.add(linkParent);
		collection.setLinks(links);
		collection.setItems(items);
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(collection, out);
		String output = out.toString();
		logger.debug("Retrieved testbed from AccountingService: " + output);
		
		return buildResponse(200, output);
	}
	
	protected Response getTestbedMonitoringFromAccountingService(String testbedName) throws JAXBException {
		Testbed testbed = new Testbed();
		testbed.setName(testbedName);
		TestbedMonitoring testbedMonitoring = ac.getTestbedMonitoringStatus(testbed);
				
		JAXBContext jaxbContext = JAXBContext.newInstance(TestbedMonitoring.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(testbedMonitoring, out);
		String output = out.toString();
		logger.debug("Retrieved testbedMonitoring from AccountingService: " + output);
		
		return buildResponse(200, output);
	}
	
	protected Response getHostMonitoringFromAccountingService(String testbedName, String hostname) throws JAXBException {
		Testbed testbed = new Testbed();
		testbed.setName(testbedName);
		HostMonitoring hostMonitoring = ac.getHostMonitoringStatus(testbed, hostname);
				
		JAXBContext jaxbContext = JAXBContext.newInstance(HostMonitoring.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(hostMonitoring, out);
		String output = out.toString();
		logger.debug("Retrieved hostMonitoring from AccountingService: " + output);
		
		return buildResponse(200, output);
	}
	
	protected Response getComputeMonitoringFromAC(String location, String id) throws JAXBException {
		VM vm = new VM();
		vm.setBonfireUrl("/locations/" + location + "/computes/" + id);
		
		VMMonitoring vmMonitoring = ac.getVMMonitoringStatus(vm);
		
		JAXBContext jaxbContext = JAXBContext.newInstance(VMMonitoring.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(vmMonitoring, out);
		String output = out.toString();
		logger.debug("Retrieved hostMonitoring from AccountingService: " + output);
		
		return buildResponse(200, output);
	}
	
	protected Response getComputeReportFromAC(String location, String id) throws JAXBException {
		VM vm = new VM();
		vm.setBonfireUrl("/locations/" + location + "/computes/" + id);
		
		VMReport vmReport = ac.getVMReport(vm);
		
		JAXBContext jaxbContext = JAXBContext.newInstance(VMReport.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(vmReport, out);
		String output = out.toString();
		logger.debug("Retrieved VMReport from AccountingService: " + output);
		
		return buildResponse(200, output);
	}
	
	protected Response getExperimentReport(int id) throws JAXBException {
		Experiment experiment = new Experiment();
		experiment.setId(id);
		
		ExperimentReport experimentReport = ac.getExperimentReport(experiment, userId, groupEco);
		
		JAXBContext jaxbContext = JAXBContext.newInstance(ExperimentReport.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(experimentReport, out);
		String output = out.toString();
		logger.debug("Retrieved VMReport from AccountingService: " + output);
		
		return buildResponse(200, output);
	}
	
}

	