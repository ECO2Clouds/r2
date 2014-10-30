package eu.eco2clouds.scheduler.bonfire;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

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
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

import eu.eco2clouds.accounting.datamodel.parser.VM;
import eu.eco2clouds.api.bonfire.occi.datamodel.Compute;
import eu.eco2clouds.api.bonfire.occi.datamodel.Experiment;
import eu.eco2clouds.api.bonfire.occi.datamodel.Collection;
import eu.eco2clouds.scheduler.SchedulerDictionary;
import eu.eco2clouds.scheduler.conf.Configuration;
import eu.eco2clouds.scheduler.http.HCClientFactory;

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
public class BFClientSchedulerImpl implements BFClientScheduler {
	private static Logger logger = Logger.getLogger(BFClientSchedulerImpl.class);
	protected String url;
	protected String userId;
	protected String groupId;
	private HttpClient httpClient;
	
	public BFClientSchedulerImpl() {
		url = Configuration.bonfireAPIUrl;
		groupId = Configuration.bonfireApiGroup;
	}

	@Override
	public Experiment getExperiment(String userId, long experimentId) {
		this.userId = userId;
		Boolean exception = false;
		String experimentUrl = url + "/experiments/" + experimentId;
					
		String response = getMethod(experimentUrl, exception);
		logger.debug(response);

		Experiment experiment = new Experiment();

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Experiment.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			experiment = (Experiment) jaxbUnmarshaller.unmarshal(new StringReader(response));	
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned status of hosts: " + experimentUrl + " Exception: " + e.getMessage());
			exception = true;
		}

		if(exception) return new Experiment();
		return experiment;
	}
	
	private HttpClient getHttpClient() {
		if(httpClient == null) {
			httpClient = HCClientFactory.getHttpClient();
		}
		
		return httpClient;
	}
	
	private void setHeaders(HttpMethod method) {		
		method.addRequestHeader(SchedulerDictionary.X_BONFIRE_ASSERTED_ID, userId);
		method.addRequestHeader(SchedulerDictionary.X_BONFIRE_GROUPS_ID, groupId);
		method.addRequestHeader("User-Agent", SchedulerDictionary.USER_AGENT);
		method.addRequestHeader("Accept", SchedulerDictionary.ACCEPT);	
	}
	
	private String putMethod(String url, String payload, Boolean exception) {
		// Create an instance of HttpClient.
		HttpClient client = getHttpClient();

		logger.debug("Connecting to: " + url);
		// Create a method instance.
		PutMethod method = new PutMethod(url);
		setHeaders(method);
		
		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		String response = "";

		try {
			RequestEntity entity = new StringRequestEntity(payload, SchedulerDictionary.CONTENT_TYPE_BONFIRE_XML, "UTF-8");
	        method.setRequestEntity(entity);
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_ACCEPTED) { //TODO test for this case... 
				logger.warn("get managed experiments information... : " + url + " failed: " + method.getStatusLine());
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
	
	private String getMethod(String url, Boolean exception) {
		// Create an instance of HttpClient.
		HttpClient client = getHttpClient();

		logger.debug("Connecting to: " + url);
		// Create a method instance.
		GetMethod method = new GetMethod(url);
		setHeaders(method);
		
		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		String response = "";

		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) { //TODO test for this case... 
				logger.warn("get managed experiments information... : " + url + " failed: " + method.getStatusLine());
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
	public List<Compute> getVMsOfExperiment(String userId, long experimentId) {
		this.userId = userId;
		Boolean exception = false;
		String experimentUrl = url + "/experiments/" + experimentId + "/computes";
					
		String response = getMethod(experimentUrl, exception);
		logger.debug(response);

		Collection collection = new Collection();

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			collection = (Collection) jaxbUnmarshaller.unmarshal(new StringReader(response));	
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned status of hosts: " + experimentUrl + " Exception: " + e.getMessage());
			exception = true;
		}

		if(exception) return new ArrayList<Compute>();
		return collection.getItems().getComputes();
	}

	@Override
	public Compute getVM(String userId, String href) {
		this.userId = userId;
		Boolean exception = false;
		String computeURL = url + href;
		
		String response = getMethod(computeURL, exception);
		logger.debug(response);
		
		Compute compute = new Compute();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Compute.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			compute = (Compute) jaxbUnmarshaller.unmarshal(new StringReader(response));	
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned status of hosts: " + computeURL + " Exception: " + e.getMessage());
			exception = true;
		}
			
		if(exception) return new Compute();
		return compute;
	}
	
	private Compute changeStateCompute(String userId, Compute compute, String state) {
		this.userId = userId;
		Boolean exception = false;
		String computeURL = url + compute.getHref();
		
		Compute computeMessage = new Compute();
		computeMessage.setHref(compute.getHref());
		computeMessage.setState(state);
		computeMessage.setLinks(compute.getLinks());
		
		String payload = "";
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Compute.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			marshaller.marshal(computeMessage, out);
			payload = out.toString();
		
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned status of hosts: " + computeURL + " Exception: " + e.getMessage());
			exception = true;
		}
		
		String response = putMethod(computeURL, payload, exception);
		logger.debug(response);
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Compute.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			compute = (Compute) jaxbUnmarshaller.unmarshal(new StringReader(response));	
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned status of hosts: " + computeURL + " Exception: " + e.getMessage());
			exception = true;
		}
			
		if(exception) return new Compute();
		return compute;
	}

	@Override
	public Compute shutdownVM(String userId, Compute compute) {
		return changeStateCompute(userId, compute, "shutdown");
	}

	@Override
	public Compute suspendVM(String userId, Compute compute) {
		return changeStateCompute(userId, compute, "suspended");
	}

	@Override
	public Compute cancelVM(String userId, Compute compute) {
		return changeStateCompute(userId, compute, "cancel");
	}

	@Override
	public Compute resumenVM(String userId, Compute compute) {
		return changeStateCompute(userId, compute, "resume");
	}

	@Override
	public Compute stopVM(String userId, Compute compute) {
		return changeStateCompute(userId, compute, "stopped");
	}

	@Override
	public VM stopVM(VM vm) {
		Compute compute = new Compute();
		compute.setHref(vm.getBonfireUrl());
		stopVM("dperez", compute);
		
		return vm;
	}

	@Override
	public VM resumeVM(VM vm) {
		Compute compute = new Compute();
		compute.setHref(vm.getBonfireUrl());
		resumenVM("dperez", compute);
		
		return vm;
	}

	@Override
	public VM cancelVM(VM vm) {
		Compute compute = new Compute();
		compute.setHref(vm.getBonfireUrl());
		cancelVM("dperez", compute);
		
		return vm;
	}

	@Override
	public VM suspendVM(VM vm) {
		Compute compute = new Compute();
		compute.setHref(vm.getBonfireUrl());
		suspendVM("dperez", compute);
		
		return vm;
	}

	@Override
	public VM shutdownVM(VM vm) {
		Compute compute = new Compute();
		compute.setHref(vm.getBonfireUrl());
		shutdownVM("dperez", compute);
		
		return vm;
	}
}
