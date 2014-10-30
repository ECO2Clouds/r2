package eu.eco2clouds.api.bonfire.client.rest;

import static eu.eco2clouds.api.bonfire.occi.Dictionary.BONFIRE_ASSERTED_ID_HEADER;
import static eu.eco2clouds.api.bonfire.occi.Dictionary.BONFIRE_XML;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;

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
 * Implements the nasty HTTP clients connections to be able to connect to BonFIRE
 */
public class RestClient {
	private static final int port = 443;

	private static void enableSSLUnsecureTrustStore() {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
		    ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
		    SSLContext.setDefault(ctx);
		} catch(NoSuchAlgorithmException exception) {
			System.out.println("ERROR TRYING TO DISSABLE JAVA SSL SECURITY");
			System.out.println("NO TLS ALGORITHM EXCEPTION");
			System.out.println("EXCEPTÇION" +  exception.getMessage());
		} catch(KeyManagementException exception) {
			System.out.println("ERROR TRYING TO DISSABLE JAVA SSL SECURITY");
			System.out.println("KEY MANAGEMENT CREATION EXCEPTION");
			System.out.println("EXCEPTÇION" +  exception.getMessage());
		}
      }
	
	private static HttpClient getClient(String url, String username, String password) {
		// Disabling strict JAVA SSL security... 
		enableSSLUnsecureTrustStore();
		
		HttpClient client = new HttpClient();
		// We set the credentials
		client.getState().setCredentials(
                new AuthScope(stripURL(url), port, AuthScope.ANY_REALM),
                new UsernamePasswordCredentials(username, password)
        );
       client.getParams().setAuthenticationPreemptive(true);
       
       return client;
	}
	
	private static Response executeMethod(HttpMethod httpMethod, String type, String username, String password, String url) {
		HttpClient client = getClient(url, username, password);
		Response response = null;
		
		// We set the Heathers
		httpMethod.setDoAuthentication(true);
		httpMethod.addRequestHeader(BONFIRE_ASSERTED_ID_HEADER, username);
		//httpMethod.addRequestHeader(BONFIRE_ASSERTED_ID_HEADER, groups);
		httpMethod.addRequestHeader("Accept", type);
		httpMethod.addRequestHeader("Content-Type", type);
		
		try {
			int statusCode = client.executeMethod(httpMethod);
			String responsePayload = httpMethod.getResponseBodyAsString();
			response = new Response(statusCode, responsePayload);
		} catch (IOException iOException) {
			System.out.println("ERROR TRYING TO PERFORM GET TO: " + httpMethod.getPath());
			System.out.println("ERROR: " + iOException.getMessage());
			System.out.println("ERROR: " + iOException.getStackTrace());
		} finally {
			httpMethod.releaseConnection();
		}
		
		return response;
	}
	
	/**
	 * Performs a POST method against the API
	 * @param url -> This URL is going to be converted to API_URL:BONFIRE_PORT/url
	 * @param payload message sent in the post method
	 * @return Response object encapsulation all the HTTP information, it returns <code>null</code> if there was an error.
	 */
	public static Response executePutMethod(String url, String payload, String username, String password) {
		Response response = null;
		
		try {
			PutMethod put = new PutMethod(url);

			// We define the request entity
			RequestEntity requestEntity = new StringRequestEntity(payload, BONFIRE_XML, null);
			put.setRequestEntity(requestEntity);

			response = executeMethod(put, BONFIRE_XML, username, password, url);
			
		} catch (UnsupportedEncodingException exception) {
			System.out.println("THE PAYLOAD ENCODING IS NOT SUPPORTED");
			System.out.println("ERROR: " + exception.getMessage());
			System.out.println("ERROR: " + exception.getStackTrace());
		}
		
		return response;
	}
	
	/**
	 * Performs a POST method against the API
	 * @param url -> This URL is going to be converted to API_URL:BONFIRE_PORT/url
	 * @param payload message sent in the post method
	 * @param type specifies the content type of the request
	 * @return Response object encapsulation all the HTTP information, it returns <code>null</code> if there was an error.
	 */
	public static Response executePostMethod(String url, String username, String password, String payload, String type) {
		Response response = null;
		
		try {
			PostMethod post = new PostMethod(url);

			// We define the request entity
			RequestEntity requestEntity = new StringRequestEntity(payload, type, null);
			post.setRequestEntity(requestEntity);

			response = executeMethod(post, BONFIRE_XML, username, password, url);
			
		} catch (UnsupportedEncodingException exception) {
			System.out.println("THE PAYLOAD ENCODING IS NOT SUPPORTED");
			System.out.println("ERROR: " + exception.getMessage());
			System.out.println("ERROR: " + exception.getStackTrace());
		}
		
		return response;
	}
//	
//	/**
//	 * Performs a POST method against the API using BONFIRE_XML as type by default
//	 * @param url -> This URL is going to be converted to API_URL:BONFIRE_PORT/url
//	 * @param payload message sent in the post method
//	 * @return Response object encapsulation all the HTTP information, it returns <code>null</code> if there was an error.
//	 */
//	public static Response executePostMethod(String url, String payload) {
//		
//		return executePostMethod(url, payload, BONFIRE_XML);
//	}
	
	/**
	 * @param url 
	 * @param id of the resource to be deleted
	 * @return Response object encapsulation all the HTTP information, it returns <code>null</code> if there was an error.
	 */
	public static Response executeDeleteMethod(String url, String username, String password) {

		DeleteMethod delete = new DeleteMethod(url);
		
		return executeMethod(delete, BONFIRE_XML, username, password, url); 
	}
//	
//	/**
//	 * @param url -> This URL is going to be converted to API_URL:BONFIRE_PORT/url
//	 * @return Response object encapsulation all the HTTP information, it returns <code>null</code> if there was an error.
//	 */
//	public static Response executeGetMethod(String url) {
//
//		GetMethod get = new GetMethod("https://" + API_URL + ":" + API_PORT + "/" + url);
//		
//		return executeMethod(get, BONFIRE_XML); 
//	}
	
	/**
	 * @param url 
	 * @param id of the resource 
	 * @return Response object encapsulation all the HTTP information, it returns <code>null</code> if there was an error.
	 */
	public static Response executeGetMethod(String url, String username, String password) {

		GetMethod get = new GetMethod(url);
		
		return executeMethod(get, BONFIRE_XML, username, password, url); 
	}
	
	protected static String stripURL(String url) {
		String[] strippedURL = url.split("/");
		
		return strippedURL[2];
	}
}

