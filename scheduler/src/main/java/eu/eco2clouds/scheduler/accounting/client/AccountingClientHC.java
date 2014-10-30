package eu.eco2clouds.scheduler.accounting.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import eu.eco2clouds.api.bonfire.occi.datamodel.Compute;
import eu.eco2clouds.api.bonfire.occi.datamodel.Nic;
import eu.eco2clouds.applicationProfile.datamodel.ExperimentDescriptor;
import eu.eco2clouds.scheduler.SchedulerDictionary;
import eu.eco2clouds.accounting.datamodel.parser.AggregateEnergy;
import eu.eco2clouds.accounting.datamodel.parser.Co2;
import eu.eco2clouds.accounting.datamodel.parser.Collection;
import eu.eco2clouds.accounting.datamodel.parser.Experiment;
import eu.eco2clouds.accounting.datamodel.parser.ExperimentReport;
import eu.eco2clouds.accounting.datamodel.parser.Host;
import eu.eco2clouds.accounting.datamodel.parser.VM;
import eu.eco2clouds.accounting.datamodel.parser.HostMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.VMMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.VMReport;
import eu.eco2clouds.accounting.datamodel.xml.HostPool;
import eu.eco2clouds.accounting.datamodel.parser.Testbed;
import eu.eco2clouds.accounting.datamodel.parser.TestbedMonitoring;
import eu.eco2clouds.scheduler.bonfire.BFClientScheduler;
import eu.eco2clouds.scheduler.bonfire.BFClientSchedulerImpl;
import eu.eco2clouds.scheduler.conf.Configuration;
import eu.eco2clouds.scheduler.http.HCClientFactory;
import eu.eco2clouds.scheduler.util.EDUtil;
import eu.eco2clouds.scheduler.util.EnergyCalculations;
import eu.eco2clouds.scheduler.util.TimeCalculations;

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
 * Implementation of the Client interface using Appache HTTP Client libraries
 * @author David Garcia Perez - AtoS
 *
 */
public class AccountingClientHC implements AccountingClient {
	private static Logger logger = Logger.getLogger(AccountingClientHC.class);
	protected String url;
	protected HttpClient httpClient;
	protected BFClientScheduler bfClient = new BFClientSchedulerImpl();
	
	public AccountingClientHC() {
		this.url = Configuration.accountingServiceUrl;
	}
	
	public AccountingClientHC(String url) {
		this.url = url;
	}

	@Override
	public List<Testbed> getListOfTestbeds() {
		Boolean exception = false;
		String testbedsUrl = url + "/testbeds";
		
		logger.debug("CONNECTING TO: " + url);
		
		String response = getMethod(testbedsUrl, null, null, exception);
		logger.debug("PAYLOAD: " + response);
		
		Collection collection = new Collection();
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			collection = (Collection) jaxbUnmarshaller.unmarshal(new StringReader(response));
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned list of testbed: " + url + "/testbeds" + " Exception: " + e.getMessage());
			exception = true;
		}
		
		if(exception) return new ArrayList<Testbed>();

		return collection.getItems().getTestbeds();
	}

	@Override
	public String getURL() {
		return url;
	}

	@Override
	public void setURL(String url) {
		this.url = url;
	}

	@Override
	public HostPool getHostStatusForATestbed(Testbed testbed) {
		Boolean exception = false;
		String testbedsUrl = url + "/testbeds/" + testbed.getName() + "/status";
					
		String response = getMethod(testbedsUrl, null, null, exception);

		HostPool hostPool = new HostPool();

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(HostPool.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			hostPool = (HostPool) jaxbUnmarshaller.unmarshal(new StringReader(response));
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned status of hosts: " + url + testbedsUrl + " Exception: " + e.getMessage());
			exception = true;
		}

		if(exception) return new HostPool();
		return hostPool;
	}
	
	private HttpClient getHttpClient() {
		if(httpClient == null) {
			httpClient = HCClientFactory.getHttpClient();
		}
		
		return httpClient;
	}
	
	protected void setStatusExperiment(Experiment experiment) {
		long endTime = experiment.getEndTime();
		
		if(TimeCalculations.isStopped(endTime)) experiment.setStatus("stopped");
		else if(TimeCalculations.isTermnated(endTime)) experiment.setStatus("terminated");
		else {
			eu.eco2clouds.api.bonfire.occi.datamodel.Experiment bfExperiment = bfClient.getExperiment(experiment.getBonfireUserId(), experiment.getBonfireExperimentId());
			
			if(bfExperiment == null) experiment.setStatus("unknown");
			else experiment.setStatus(bfExperiment.getStatus());
		}
	}
	
	private void setHeaders(HttpMethod method, String groupId, String userId) {
		if(userId != null)
			if(!userId.equals(""))
				method.addRequestHeader(SchedulerDictionary.X_BONFIRE_ASSERTED_ID, userId);
		
		if(groupId != null)
			if(!groupId.equals(""))
				method.addRequestHeader(SchedulerDictionary.X_BONFIRE_GROUPS_ID, groupId);
		
		method.addRequestHeader("User-Agent", SchedulerDictionary.USER_AGENT);
		method.addRequestHeader("Accept", SchedulerDictionary.ACCEPT);	
	}
	
	private String getMethod(String url, String userId, String groupId, Boolean exception) {
		// Create an instance of HttpClient.
		HttpClient client = getHttpClient();
				
		logger.debug("Connecting to: " + url);
		// Create a method instance.
		GetMethod method = new GetMethod(url);
		setHeaders(method, groupId, userId);

		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		String response = "";

		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) { //TODO test for this case... 
				logger.warn("Get host information of testbeds: " + url + " failed: " + method.getStatusLine());
			} else {
				// Read the response body.
				byte[] responseBody = method.getResponseBody();
				response = new String(responseBody);
			}	

		} catch(HttpException e) {
			logger.warn("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
			exception = true;
		} catch(IOException e) {
			logger.warn("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
			exception = true;
		} finally {
			// Release the connection.
			method.releaseConnection();
		}
		
		return response;
	}

//	@Override
//	public List<Testbed> getHostStatusForAllTestbeds() {
//		List<Testbed> testbeds = getListOfTestbeds();
//		
//		for(Testbed testbed : testbeds) {
//			List<HostXml> hosts = getHostStatusForATestbed(testbed).getHosts();
//			testbed.setHosts(hosts);
//		}
//		
//		return testbeds;
//	}

	@Override
	public List<Experiment> getListOfExperiments(String bonfireUser, String bonfireGroup) {
		Boolean exception = false;
		String experimentsUrl = url + "/experiments";
		
		String response = getMethod(experimentsUrl, bonfireUser, bonfireGroup, exception);
		logger.debug("PAYLOAD: " + response);
		
		Collection collection = new Collection();
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			collection = (Collection) jaxbUnmarshaller.unmarshal(new StringReader(response));
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned list of testbed: " + url + "/experiments" + " Exception: " + e.getMessage());
			exception = true;
		}
		
		if(exception) return new ArrayList<Experiment>();
		
		List<Experiment> experiments = collection.getItems().getExperiments();
		
		if(experiments != null) {
			for(Experiment experiment : collection.getItems().getExperiments()) {
				setStatusExperiment(experiment);
			}
		}
		
		return collection.getItems().getExperiments();
	}

	@Override
	public Experiment getExperiment(Experiment experiment, String bonfireUser, String bonfireGroup) {
		String experimentUrl = url + experiment.getHref();
		experiment = getExperiment(experimentUrl, bonfireUser, bonfireGroup);
		return experiment;
	}

	@Override
	public Experiment getExperiment(int id, String bonfireUser, String bonfireGroup) {
		String experimentUrl = url + "/experiments/" + id;
		return getExperiment(experimentUrl, bonfireUser, bonfireGroup);
	}
	
	private Experiment getExperiment(String experimentUrl, String bonfireUser, String bonfireGroup) {
		Boolean exception = false;
		
		String response = getMethod(experimentUrl, bonfireUser, bonfireGroup, exception);
		logger.debug("PAYLOAD: " + response);
		
		Experiment experiment = new Experiment();
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Experiment.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			experiment = (Experiment) jaxbUnmarshaller.unmarshal(new StringReader(response));
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned list of testbed: " + url + "/experiments" + " Exception: " + e.getMessage());
			exception = true;
		}
			
		if(exception) return new Experiment();
		setStatusExperiment(experiment);
		
		return experiment;
	}

	@Override
	public Experiment createExperiment(Experiment experiment) {
		Boolean exception = false;
		String experimentUrl = url + "/experiments/";
		logger.debug("URL build: " + experimentUrl);
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Experiment.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			marshaller.marshal(experiment, out);
			String payload = out.toString();
			
			String response = postMethod(experimentUrl, payload, experiment.getBonfireUserId(), experiment.getBonfireGroupId(), exception);
			logger.debug("PAYLOAD: " + response);
			
			try {
				jaxbContext = JAXBContext.newInstance(Experiment.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				experiment = (Experiment) jaxbUnmarshaller.unmarshal(new StringReader(response));
			} catch(JAXBException e) {
				logger.warn("Error trying incoming experiment to object. Exception: " + e.getMessage());
				exception = true;
			}
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned experiment from database: " + url + "/experiments" + " Exception: " + e.getMessage());
			exception = true;
		}
		
		if(exception) return new Experiment();
		return experiment;
	}

	private String postMethod(String url, String payload, String bonfireUserId, String bonfireGroupId, Boolean exception) {
		// Create an instance of HttpClient.
		HttpClient client = getHttpClient();

		logger.debug("Connecting to: " + url);
		// Create a method instance.
		PostMethod method = new PostMethod(url);
		setHeaders(method, bonfireGroupId, bonfireUserId);
		//method.addRequestHeader("Content-Type", SchedulerDictionary.CONTENT_TYPE_ECO2CLOUDS_XML);
		
		
		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		String response = "";

		try {
			// We set the payload
			StringRequestEntity payloadEntity = new StringRequestEntity(payload,  SchedulerDictionary.CONTENT_TYPE_ECO2CLOUDS_XML, "UTF-8");
			method.setRequestEntity(payloadEntity);
			
			// Execute the method.
			int statusCode = client.executeMethod(method);
			logger.debug("Status Code: " + statusCode );

			if (statusCode >= 200 && statusCode > 300) { //TODO test for this case... 
				logger.warn("Get host information of testbeds: " + url + " failed: " + method.getStatusLine());
			} else {
				// Read the response body.
				byte[] responseBody = method.getResponseBody();
				response = new String(responseBody);
			}	

		} catch(HttpException e) {
			logger.warn("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
			exception = true;
		} catch(IOException e) {
			logger.warn("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
			exception = true;
		} finally {
			// Release the connection.
			method.releaseConnection();
		}

		return response;
	}

	@Override
	public Experiment updateExperiment(Experiment experiment) {
		Boolean exception = false;
		String experimentUrl = url + "/experiments/" + experiment.getId().intValue();
		logger.debug("URL build: " + experimentUrl);
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Experiment.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			marshaller.marshal(experiment, out);
			String payload = out.toString();
			
			String response = putMethod(experimentUrl, payload, experiment.getBonfireUserId(), experiment.getBonfireGroupId(), exception);
			logger.debug("PAYLOAD: " + response);
			
			try {
				jaxbContext = JAXBContext.newInstance(Experiment.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				experiment = (Experiment) jaxbUnmarshaller.unmarshal(new StringReader(response));
			} catch(JAXBException e) {
				logger.warn("Error trying incoming experiment to object. Exception: " + e.getMessage());
				exception = true;
			}
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned experiment from database: " + url + "/experiments" + " Exception: " + e.getMessage());
			exception = true;
		}
		
		if(exception) return new Experiment();
		return experiment;
	}
	
	private String putMethod(String url, String payload, String bonfireUserId, String bonfireGroupId, Boolean exception) {
		// Create an instance of HttpClient.
		HttpClient client = getHttpClient();

		logger.debug("Connecting to: " + url);
		// Create a method instance.
		PutMethod method = new PutMethod(url);
		setHeaders(method, bonfireGroupId, bonfireUserId);
		//method.addRequestHeader("Content-Type", SchedulerDictionary.CONTENT_TYPE_ECO2CLOUDS_XML);
		
		
		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		String response = "";

		try {
			// We set the payload
			StringRequestEntity payloadEntity = new StringRequestEntity(payload,  SchedulerDictionary.CONTENT_TYPE_ECO2CLOUDS_XML, "UTF-8");
			method.setRequestEntity(payloadEntity);
			
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) { //TODO test for this case... 
				logger.warn("Get host information of testbeds: " + url + " failed: " + method.getStatusLine());
			} else {
				// Read the response body.
				byte[] responseBody = method.getResponseBody();
				response = new String(responseBody);
			}	

		} catch(HttpException e) {
			logger.warn("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
			exception = true;
		} catch(IOException e) {
			logger.warn("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
			exception = true;
		} finally {
			// Release the connection.
			method.releaseConnection();
		}

		return response;
	}

	@Override
	public TestbedMonitoring getTestbedMonitoringStatus(Testbed testbed) {
		Boolean exception = false;
		String testbedsUrl = url + "/testbeds/" + testbed.getName() + "/monitoring";
		
		String response = getMethod(testbedsUrl, null, null, exception);
		logger.debug("PAYLOAD: " + response);
		
		TestbedMonitoring testbedMonitoring = new TestbedMonitoring();
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(TestbedMonitoring.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			testbedMonitoring = (TestbedMonitoring) jaxbUnmarshaller.unmarshal(new StringReader(response));
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned list of testbed: " + url + "/testbeds/" + testbed.getName() + "/monitoring" + " Exception: " + e.getMessage());
			exception = true;
		}
		
		if(exception) return new TestbedMonitoring();
		return testbedMonitoring;
	}

	@Override
	public HostMonitoring getHostMonitoringStatus(Testbed testbed, String hostname) {
		Boolean exception = false;
		String testbedsUrl = url + "/testbeds/" + testbed.getName() + "/hosts/" + hostname + "/monitoring";
		
		String response = getMethod(testbedsUrl, null, null, exception);
		logger.debug("PAYLOAD: " + response);
		
		HostMonitoring hostMonitoring = new HostMonitoring();
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(HostMonitoring.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			hostMonitoring = (HostMonitoring) jaxbUnmarshaller.unmarshal(new StringReader(response));
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned list of testbed: " + url + "/testbeds/" + testbed.getName() + "/hosts/" + hostname + "/monitoring" + " Exception: " + e.getMessage());
			exception = true;
		}
		
		if(exception) return new HostMonitoring();
		return hostMonitoring;
	}

	@Override
	public List<HostMonitoring> getHostMonitoringStatus(Testbed testbed, String hostname, long startTime, long endTime) {
		Boolean exception = false;
		String testbedsUrl = url + "/testbeds/" + testbed.getName() + "/hosts/" + hostname + "/monitoring?startTime=" + startTime + "&endTime=" + endTime;
		
		String response = getMethod(testbedsUrl, null, null, exception);
		logger.debug("PAYLOAD: " + response);
		
		Collection collection = new Collection();
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			collection = (Collection) jaxbUnmarshaller.unmarshal(new StringReader(response));
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned list of testbed: " + url + "/testbeds/" + testbed.getName() + "/hosts/" + hostname + "/monitoring?startTime=" + startTime + "&endTime=" + endTime+ " Exception: " + e.getMessage());
			exception = true;
		}
		
		if(exception) return new ArrayList<HostMonitoring>();
		return collection.getItems().getHostMonitorings();
	}

	@Override
	public double getCo2PerHourProducedByAHostAtThisMoment(Testbed testbed, String hostname) {
		HostMonitoring hostMonitoring = getHostMonitoringStatus(testbed, hostname);
		TestbedMonitoring testbedMonitoring = getTestbedMonitoringStatus(testbed);
		
		return EnergyCalculations.gPerHour(testbedMonitoring.getCo2(), hostMonitoring.getCo2Producted());
	}

	@Override
	public double getCo2Consumption(int experimentId) {
		//First it is necessary to get the Experiment from the Database 
		//TODO this group things needs to be improved... 
		Experiment experiment = getExperiment(experimentId, "user", "eco2clouds");
		
		
		// Now I can retrieve the SubmittedExperimentDescriptor
		ExperimentDescriptor ed = getSubmittedExperimentDescriptor(experiment);
		
		// Now I can get a mapper list of testbeds and hosts... 
		double totalCo2 = 0;
		Map<String, List<String>> testbedAndHosts = EDUtil.getListHostsTestbeds(ed);
		for(Map.Entry<String, List<String>> entry : testbedAndHosts.entrySet()) {
			Testbed testbed = new Testbed();
			testbed.setName(entry.getKey());
			long startTime = experiment.getStartTime() / 1000;
			long endTime = experiment.getEndTime() / 1000;
			List<Co2> co2s = getCo2OfTestbedForInterval(testbed, startTime, endTime); 
			Co2 meanCo2 = EnergyCalculations.getMeanCo2Value(co2s);
			
			for(String hostname : entry.getValue()) {
				List<HostMonitoring> hostMonitorings = getHostMonitoringStatus(testbed, hostname, startTime, endTime);
				List<AggregateEnergy> aggregateEnergies = new ArrayList<AggregateEnergy>();
				
				for(HostMonitoring hostMonitoring : hostMonitorings) {
					aggregateEnergies.add(hostMonitoring.getAggregateEnergy());
				}
				
				AggregateEnergy aggregateEnergy = EnergyCalculations.getMeanEnergy(aggregateEnergies, AggregateEnergy.class);
				
				double totalCo2Period = EnergyCalculations.getTotalCo2ForPeriodOfTime(meanCo2, aggregateEnergy, startTime, endTime);
				
				totalCo2 = totalCo2 + totalCo2Period;
			}
		}
		
		
		return totalCo2;
	}

	@Override
	public List<Co2> getCo2OfTestbedForInterval(Testbed testbed, long startTime, long endTime) {
		Boolean exception = false;
		String testbedsUrl = url + "/testbeds/" + testbed.getName() + "/monitoring/co2?startTime=" + startTime + "&endTime=" + endTime;
		
		String response = getMethod(testbedsUrl, null, null, exception);
		logger.debug("PAYLOAD: " + response);
		
		Collection collection = new Collection();
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			collection = (Collection) jaxbUnmarshaller.unmarshal(new StringReader(response));
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned list of testbed: " + url + "/testbeds/" + testbed.getName() + "/monitoring/co2?startTime=" + startTime + "&endTime=" + endTime + " Exception: " + e.getMessage());
			exception = true;
		}
		
		if(exception) return new ArrayList<Co2>();
		
		List<Co2> co2s = new ArrayList<Co2>();
		
		for(TestbedMonitoring testbedMonitoring : collection.getItems().getTestbedMonitorings()) 
			co2s.add(testbedMonitoring.getCo2());
		
		return co2s;
	}

	@Override
	public ExperimentDescriptor getSubmittedExperimentDescriptor(Experiment experiment) {
		Boolean exception = false;
		String submittedExperimentUrl = url + "/experiments/" + experiment.getId() + "/submitted-experiment-descriptor";
		
		String response = getMethod(submittedExperimentUrl, experiment.getBonfireUserId(), experiment.getBonfireGroupId(), exception);
		logger.debug("PAYLOAD: " + response);
		
		ExperimentDescriptor ed = new ExperimentDescriptor();
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, false);
			mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
			
			ed = mapper.readValue(response, ExperimentDescriptor.class);

		}  catch(Exception e) {
			logger.warn("Error trying to parse experiment descriptor: " + submittedExperimentUrl + " Exception: " + e.getMessage());
			exception = true;
		}
		
		if(exception) new ExperimentDescriptor();
		
		return ed;
	}
	
	@SuppressWarnings("unchecked")
	public List<Experiment> getListOfRunningExperiments(String bonfireUserId, String bonfireGroupId){
		Boolean exception = false;
		String testbedsUrl = url + "/experiments/status/" +"?status=\"running\"";
		
		String response = getMethod(testbedsUrl, null, null, exception);
		logger.debug("PAYLOAD: " + response);
		
		List<Experiment> experiments = new ArrayList<Experiment>();
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			experiments = (List<Experiment>) jaxbUnmarshaller.unmarshal(new StringReader(response));
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned list of testbed: " + url + "/experiments/status/?status=\"running\"" + " Exception: " + e.getMessage());
			exception = true;
		}
		
		return experiments;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Experiment> getListOfRunningExperiments(String bonfireUserId, String bonfireGroupId, String status){
		Boolean exception = false;
		String testbedsUrl = url + "/experiments//" +"?status=\""+ status +"\"";
		
		String response = getMethod(testbedsUrl, null, null, exception);
		logger.debug("PAYLOAD: " + response);
		
		List<Experiment> experiments = new ArrayList<Experiment>();
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			experiments = (List<Experiment>) jaxbUnmarshaller.unmarshal(new StringReader(response));
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned list of testbed: " + url + "/experiments/status/?status=\""+ status +"\"" + " Exception: " + e.getMessage());
			exception = true;
		}
		
		return experiments;
	}

	@Override
	public Testbed getTestbed(String location) {
		Boolean exception = false;
		String testbedUrl = url + "/testbeds/" + location;
		
		String response = getMethod(testbedUrl, null, null, exception);
		logger.debug("PAYLOAD: " + response);
		
		Testbed testbed = new Testbed();
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Testbed.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			testbed = (Testbed) jaxbUnmarshaller.unmarshal(new StringReader(response));
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned list of testbed: " + url + "/testbeds/" + location + " Exception: " + e.getMessage());
			exception = true;
		}
		
		return testbed;
	}

	@Override
	public List<Host> getHostsOfTesbed(String location) {
		Boolean exception = false;
		String hostsUrl = url + "/testbeds/" + location + "/hosts";
		
		String response = getMethod(hostsUrl, null, null, exception);
		logger.debug("PAYLOAD: " + response);
		
		Collection collection = null;
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			collection = (Collection) jaxbUnmarshaller.unmarshal(new StringReader(response));
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned list of testbed: " + url + "/testbeds/" + location + " Exception: " + e.getMessage());
			exception = true;
		}
		
		if(collection == null) return null;
		
		return collection.getItems().getHosts();
	}

	@Override
	public Host getHostOfTestbed(String location, String hostName) {
		Boolean exception = false;
		String hostUrl = url + "/testbeds/" + location + "/hosts/" + hostName;
		
		String response = getMethod(hostUrl, null, null, exception);
		logger.debug("PAYLOAD: " + response);
		
		Host host = null;
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Host.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			host = (Host) jaxbUnmarshaller.unmarshal(new StringReader(response));
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned list of testbed: " + url + "/testbeds/" + location + "/hosts/" + hostName + " Exception: " + e.getMessage());
			exception = true;
		}
		
		if(host == null) return null;
		
		return host;
	}

	@Override
	public List<VM> getListOfVMsOfExperiment(long bonfireExperimentId, String bonfireUser, String bonfireGroup) {
		Experiment experiment = getExperiment((int) bonfireExperimentId, bonfireUser,bonfireGroup);
		setStatusExperiment(experiment);
		
		List<VM> vms = null;
		
		if(!experiment.getStatus().equals("terminated") || !experiment.getStatus().equals("stopped")) {
			List<Compute> computes = bfClient.getVMsOfExperiment(experiment.getBonfireUserId(), experiment.getBonfireExperimentId());
			
			if(computes != null) {
				vms = new ArrayList<VM>();
				for(Compute compute : computes) {
					compute = bfClient.getVM(experiment.getBonfireUserId(), compute.getHref());
					if(compute!= null) {
						VM vm = new VM();
						vm.setHref(compute.getHref());
						vm.setBonfireId(compute.getId());
						vm.setBonfireUrl(Configuration.bonfireAPIUrl + compute.getHref());
						vm.setHost(compute.getHost());
						vm.setName(compute.getName());
						
						for(Nic nic : compute.getNics()) {
							vm.addNic(nic.getIp(), nic.getMac(), nic.getNetwork().getNetworkName());
						}
						
						vm.addLink("bonfire", compute.getHref(), SchedulerDictionary.CONTENT_TYPE_ECO2CLOUDS_XML);
						vms.add(vm);
					}
				}
			}
		}
		
		return vms;
	}
	
	
	protected String getVMUrl(VM vm) {
		String bonFIREUrl = vm.getBonfireUrl();
		
		if(bonFIREUrl.subSequence(0, 4).equals("http")) {
			String[] parts = bonFIREUrl.split("/");
			bonFIREUrl = "/" + parts[3] + "/" + parts[4] + "/" + parts[5] + "/" + parts[6];
		}
		
		return bonFIREUrl;
	}

	@Override
	public VMMonitoring getVMMonitoringStatus(VM vm) {
		Boolean exception = false;
		String vmUrl = url + getVMUrl(vm) + "/monitoring";
		logger.debug(" Constructed VM Monitoring URL: " + vmUrl);
		
		String response = getMethod(vmUrl, null, null, exception);
		logger.debug("PAYLOAD: " + response);
		
		VMMonitoring vmMonitoring = new VMMonitoring();
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(VMMonitoring.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			vmMonitoring = (VMMonitoring) jaxbUnmarshaller.unmarshal(new StringReader(response));
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned vm monitoring: " + url + vm.getBonfireUrl() + "/monitoring" + " Exception: " + e.getMessage());
			exception = true;
		}
		
		if(exception) return new VMMonitoring();
		return vmMonitoring;
	}
	
	@Override
	public VMReport getVMReport(VM vm) {
		Boolean exception = false;
		String vmUrl = url + vm.getBonfireUrl() + "/report";
		
		String response = getMethod(vmUrl, null, null, exception);
		logger.debug("PAYLOAD: " + response);
		
		VMReport vmReport = new VMReport();
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(VMReport.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			vmReport = (VMReport) jaxbUnmarshaller.unmarshal(new StringReader(response));
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned vm monitoring: " + url + vm.getBonfireUrl() + "/report" + " Exception: " + e.getMessage());
			exception = true;
		}
		
		if(exception) return new VMReport();
		return vmReport;
	}

	@Override
	public ExperimentReport getExperimentReport(Experiment experiment, String bonfireUser, String bonfireGroup) {
		Boolean exception = false;
		String experimentUrl = url + "/experiments/" + experiment.getId() + "/report";
		
		String response = getMethod(experimentUrl, bonfireUser, bonfireGroup, exception);
		logger.debug("PAYLOAD: " + response);
		
		ExperimentReport experimentReport = new ExperimentReport();
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ExperimentReport.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			experimentReport = (ExperimentReport) jaxbUnmarshaller.unmarshal(new StringReader(response));
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned list of testbed: " + url + "/experiments/" + experiment.getId() + "/report" + " Exception: " + e.getMessage());
			exception = true;
		}
			
		if(exception) return new ExperimentReport();
		return experimentReport;
	}
}
