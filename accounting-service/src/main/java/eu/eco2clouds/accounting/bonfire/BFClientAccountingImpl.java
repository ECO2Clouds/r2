package eu.eco2clouds.accounting.bonfire;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

import eu.eco2clouds.accounting.Dictionary;
import eu.eco2clouds.accounting.conf.Configuration;
import eu.eco2clouds.accounting.http.HCClientFactory;
import eu.eco2clouds.api.bonfire.occi.datamodel.Experiment;

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
public class BFClientAccountingImpl implements BFClientAccounting {
	private static Logger logger = Logger.getLogger(BFClientAccountingImpl.class);
	protected String url;
	protected String userId;
	protected String groupId;
	private HttpClient httpClient;
	
	public BFClientAccountingImpl() {
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
	
	
	private HttpClient getHttpClient() {
		if(httpClient == null) {
			httpClient = HCClientFactory.getHttpClient();
		}
		
		return httpClient;
	}
	
	private void setHeaders(HttpMethod method) {		
		method.addRequestHeader(Dictionary.X_BONFIRE_ASSERTED_ID, userId);
		method.addRequestHeader(Dictionary.X_BONFIRE_GROUPS_ID, groupId);
		method.addRequestHeader("User-Agent", Dictionary.USER_AGENT);
		method.addRequestHeader("Accept", Dictionary.ACCEPT);	
	}
	
	
}
