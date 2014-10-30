package eu.eco2clouds.scheduler.em;


import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import eu.eco2clouds.applicationProfile.datamodel.Compute;
import eu.eco2clouds.applicationProfile.datamodel.Contexts;
import eu.eco2clouds.applicationProfile.datamodel.ExperimentDescriptor;
import eu.eco2clouds.applicationProfile.datamodel.ResourceCompute;
import eu.eco2clouds.scheduler.SchedulerDictionary;
import eu.eco2clouds.scheduler.conf.Configuration;
import eu.eco2clouds.scheduler.em.datamodel.Collection;
import eu.eco2clouds.scheduler.em.datamodel.ManagedExperiment;
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
public class EMClientHC implements EMClient {
	private static Logger logger = Logger.getLogger(EMClientHC.class);
	protected String url;
	protected String userId;
	protected String groupId;
	private HttpClient httpClient;
	
	public EMClientHC() {
		url = Configuration.bonfireEMUrl;
		groupId = Configuration.bonfireApiGroup;
	}

	@Override
	public List<ManagedExperiment> listExperiments(String userId) {
		this.userId = userId;
		Boolean exception = false;
		String testbedsUrl = url;
					
		String response = getMethod(testbedsUrl, exception);

		List<ManagedExperiment> managedExperiments = new ArrayList<ManagedExperiment>();

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Collection collection = (Collection) jaxbUnmarshaller.unmarshal(new StringReader(response));
			managedExperiments = collection.getItems().getManagedExperiments();
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned status of hosts: " + url + testbedsUrl + " Exception: " + e.getMessage());
			exception = true;
		}

		if(exception) return new ArrayList<ManagedExperiment>();
		return managedExperiments;
	}
	
	private HttpClient getHttpClient() {
		if(httpClient == null) {
			httpClient = HCClientFactory.getHttpClient();
		}
		
		return httpClient;
	}
	
	private void setHeaders(HttpMethod method, String group) {
		method.addRequestHeader(SchedulerDictionary.X_BONFIRE_ASSERTED_SELECTED_GROUP, group);
		setHeaders(method);
	}
	
	private void setHeaders(HttpMethod method) {		
		method.addRequestHeader(SchedulerDictionary.X_BONFIRE_ASSERTED_ID, userId);
		method.addRequestHeader(SchedulerDictionary.X_BONFIRE_GROUPS_ID, groupId);
		method.addRequestHeader("User-Agent", SchedulerDictionary.USER_AGENT);
		method.addRequestHeader("Accept", SchedulerDictionary.ACCEPT);	
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
	
	private String postMethod(String url, String payload, String contentType, Boolean exception) {
		// Create an instance of HttpClient.
		HttpClient client = getHttpClient();

		logger.debug("Connecting to: " + url);
		// Create a method instance.
		PostMethod method = new PostMethod(url);
		//setHeaders(method, contentType);
		setHeaders(method, Configuration.bonfireApiGroup);
		
		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		String response = "";

		try {
			RequestEntity requestEntity = new StringRequestEntity(payload, contentType, null);
			method.setRequestEntity(requestEntity);
			
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode >= 200 && statusCode > 300) { //TODO test for this case... 
				logger.warn("post managed experiments information... : " + url + " failed: " + method.getStatusLine());
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
	public ManagedExperiment getExperiment(String userId, int experimentId) {
		this.userId = userId;
		Boolean exception = false;
		String testbedsUrl = url + "/" + experimentId;
					
		String response = getMethod(testbedsUrl, exception);

		ManagedExperiment me = null;

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ManagedExperiment.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			me = (ManagedExperiment) jaxbUnmarshaller.unmarshal(new StringReader(response));
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned status of hosts: " + url + testbedsUrl + " Exception: " + e.getMessage());
			exception = true;
		}

		if(exception) return null;
		return me;
	}
	
	private void addExoerimentIdToComputes(ExperimentDescriptor ed, long experimentId) {
		ArrayList<ResourceCompute> resourcesComputes = ed.getResourcesCompute();
		if(resourcesComputes != null) {
			for(ResourceCompute resourceCompute: resourcesComputes) {
				Compute compute = resourceCompute.getCompute();
				
				if(compute != null) {
					
					ArrayList<Contexts> contextses = compute.getContexts();
					if(contextses == null) {
						contextses = new ArrayList<Contexts>();
					}

					Contexts contexts = new Contexts();
					contexts.setContextThings("eco2clouds_experiment_id", "" + experimentId);
					contextses.add(contexts);
					compute.setContexts(contextses);
				}
			}
		}
	}

	@Override
	public ManagedExperiment submitExperiment(String userId, ExperimentDescriptor ed, long experimentId) {
		
		addExoerimentIdToComputes(ed, experimentId);
		
		this.userId = userId;
		Boolean exception = false;
		ManagedExperiment me = null;
		String testbedsUrl = url;
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, false);
			mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
			mapper.setSerializationInclusion(Inclusion.NON_NULL);

			String payload = mapper.writeValueAsString(ed);
			logger.debug("payload: " + payload);
			
			String response = postMethod(testbedsUrl, payload, SchedulerDictionary.CONTENT_TYPE_JSON, exception);
			logger.debug("Response from Experiment Manager: " + response);
			
			JAXBContext jaxbContext = JAXBContext.newInstance(ManagedExperiment.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			me = (ManagedExperiment) jaxbUnmarshaller.unmarshal(new StringReader(response));
		}  catch(Exception e) {
			logger.warn("Error trying to parse returned status of hosts: " + url + testbedsUrl + " Exception: " + e.getMessage());
			exception = true;
		}
		
		if(exception) return null;
		return me;
	}
}
