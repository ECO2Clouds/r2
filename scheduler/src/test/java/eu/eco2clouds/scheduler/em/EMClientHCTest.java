package eu.eco2clouds.scheduler.em;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.eco2clouds.applicationProfile.datamodel.ExperimentDescriptor;
import eu.eco2clouds.scheduler.SchedulerDictionary;
import eu.eco2clouds.scheduler.accounting.client.AccountingClientHCTest;
import eu.eco2clouds.scheduler.conf.Configuration;
import eu.eco2clouds.scheduler.em.datamodel.ManagedExperiment;
import eu.eco2clouds.scheduler.util.HttpHeadersImpl;
import eu.eco2clouds.scheduler.util.MockWebServer;

import com.sun.net.httpserver.Headers;

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
public class EMClientHCTest {
	private static Logger logger = Logger.getLogger(AccountingClientHCTest.class);
	private MockWebServer mServer;
	private String mBaseURL = "http://localhost:";
	private String emUrl = "";
	
	@Before
	public void before() throws IOException {
		mServer = new MockWebServer();
		mServer.start();
		mBaseURL = mBaseURL + mServer.getPort();
		logger.debug("Web server started: " + mBaseURL);
		setMockServerUrl();
	}

	@Test
	public void checkUrlUserAndGroupsAreSetted() {
		EMClientHC client = new EMClientHC();
		
		assertEquals(Configuration.bonfireApiGroup, client.groupId);
		assertEquals(Configuration.bonfireEMUrl, client.url);
	}
	
	@Test
	public void getListManagedExperiments() {
			
		// Normal case... 
		String xmlCollection = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<collection>"
					+ "<items>"
						+ "<managed_experiment href=\"/managed_experiments/247\">"
							+ "<name>ReviewAmazonExperiment</name>"
							+ "<description>Horizontal scalability demo</description>"
							+ "<status>DEPLOYED</status>"
							+ "<link rel=\"experiment\" href=\"https://api.bonfire-project.eu/experiments/21272\"/>"
							+ "<link rel=\"log\" href=\"/managed_experiments/247/log\"/>"
						+ "</managed_experiment>"
						+ "<managed_experiment href=\"/managed_experiments/250\">"
							+ "<name>ReviewAmazonExperiment</name>"
							+ "<description>Horizontal scalability demo</description>"
							+ "<status>DEPLOYED</status>"
							+ "<link rel=\"experiment\" href=\"https://api.bonfire-project.eu/experiments/21277\"/>"
							+ "<link rel=\"log\" href=\"/managed_experiments/250/log\"/>"
						+ "</managed_experiment>"
					+ "</items>"
				+ "</collection>";
		
		mServer.addPath("/", xmlCollection);
		
		EMClientHC client = new EMClientHC();
		List<ManagedExperiment> managedExperiments = client.listExperiments("eco2clouds2");
		assertEquals("eco2clouds2", client.userId);
		
		assertEquals(2, managedExperiments.size());
		assertEquals("/managed_experiments/247", managedExperiments.get(0).getHref());
		assertEquals("/managed_experiments/250", managedExperiments.get(1).getHref());
		
		// We verify that the right headers were sent
		Headers headers = mServer.getHeaders();
		assertEquals("eco2clouds2", headers.getFirst(SchedulerDictionary.X_BONFIRE_ASSERTED_ID));
		assertEquals("eco2clouds", headers.getFirst(SchedulerDictionary.X_BONFIRE_GROUPS_ID));
		assertEquals(SchedulerDictionary.ACCEPT, headers.getFirst("Accept"));
		assertEquals(SchedulerDictionary.USER_AGENT, headers.getFirst("User-Agent"));
		
		
	}
	
	@Test
	public void getListManagedExperimentsEmpty() {
		
		// Empty list since the returned message is wrong...
		mServer.addPath("/", "error!!!");
		EMClientHC client = new EMClientHC();
		List<ManagedExperiment> managedExperiments = client.listExperiments("eco2clouds2");
		
		assertEquals(0, managedExperiments.size());
		
		// We verify that the right headers were sent
		Headers headers = mServer.getHeaders();
		assertEquals("eco2clouds2", headers.getFirst(SchedulerDictionary.X_BONFIRE_ASSERTED_ID));
		assertEquals("eco2clouds", headers.getFirst(SchedulerDictionary.X_BONFIRE_GROUPS_ID));
		assertEquals(SchedulerDictionary.ACCEPT, headers.getFirst("Accept"));
		assertEquals(SchedulerDictionary.USER_AGENT, headers.getFirst("User-Agent"));
	}
	
	@Test
	public void getManagedExperiment() {
	
		// Normal case... 
		String xmlExperiment = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
								+ "<managed_experiment href=\"/managed_experiments/247\">"
									+ "<name>ReviewAmazonExperiment</name>"
									+ "<description>Horizontal scalability demo</description>"
									+ "<status>DEPLOYED</status>"
									+ "<link rel=\"experiment\" href=\"https://api.bonfire-project.eu/experiments/21272\"/>"
									+ "<link rel=\"log\" href=\"/managed_experiments/247/log\"/>"
								+ "</managed_experiment>";
		
		mServer.addPath("/247", xmlExperiment);
		
		EMClientHC client = new EMClientHC();
		ManagedExperiment me = client.getExperiment("eco2clouds2", 247);
		assertEquals("eco2clouds2", client.userId);
		
		assertEquals("/managed_experiments/247", me.getHref());
		assertEquals("ReviewAmazonExperiment", me.getName());
		assertEquals("Horizontal scalability demo", me.getDescription());
		assertEquals("DEPLOYED", me.getStatus());
		assertEquals(2, me.getLinks().size());
		
		// We verify that the right headers were sent
		Headers headers = mServer.getHeaders();
		assertEquals("eco2clouds2", headers.getFirst(SchedulerDictionary.X_BONFIRE_ASSERTED_ID));
		assertEquals("eco2clouds", headers.getFirst(SchedulerDictionary.X_BONFIRE_GROUPS_ID));
		assertEquals(SchedulerDictionary.ACCEPT, headers.getFirst("Accept"));
		assertEquals(SchedulerDictionary.USER_AGENT, headers.getFirst("User-Agent"));
	}
	
	@Test
	public void postExperiment() {
		String xmlExperiment = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<managed_experiment href=\"/managed_experiments/247\">"
					+ "<name>ReviewAmazonExperiment</name>"
					+ "<description>Horizontal scalability demo</description>"
					+ "<status>DEPLOYED</status>"
					+ "<link rel=\"experiment\" href=\"https://api.bonfire-project.eu/experiments/21272\"/>"
					+ "<link rel=\"log\" href=\"/managed_experiments/247/log\"/>"
				+ "</managed_experiment>";
		
		ExperimentDescriptor ed = new ExperimentDescriptor();
		ed.setDescription("description");
		ed.setDuration(120);
		ed.setName("experiment name");
		
		mServer.addPath("/", xmlExperiment , 200);
		EMClientHC client = new EMClientHC();
		ManagedExperiment me = client.submitExperiment("eco2clouds2", ed, 0l);
		assertEquals("eco2clouds2", client.userId);
		
		assertEquals("/managed_experiments/247", me.getHref());
		assertEquals("ReviewAmazonExperiment", me.getName());
		assertEquals("Horizontal scalability demo", me.getDescription());
		assertEquals("DEPLOYED", me.getStatus());
		assertEquals(2, me.getLinks().size());
		
		// We verify that the right headers were sent
		Headers headers = mServer.getHeaders();
		assertEquals("eco2clouds2", headers.getFirst(SchedulerDictionary.X_BONFIRE_ASSERTED_ID));
		assertEquals("eco2clouds", headers.getFirst(SchedulerDictionary.X_BONFIRE_GROUPS_ID));
		assertEquals(SchedulerDictionary.ACCEPT, headers.getFirst("Accept"));
		assertEquals(SchedulerDictionary.USER_AGENT, headers.getFirst("User-Agent"));
		assertEquals(SchedulerDictionary.CONTENT_TYPE_JSON, headers.getFirst("Content-Type").toString());
		assertEquals("eco2clouds", headers.getFirst(SchedulerDictionary.X_BONFIRE_ASSERTED_SELECTED_GROUP));
	}
	
	@Test
	public void getManagedExperimentEmpty() {
			
		// Empty list since the returned message is wrong...
		mServer.addPath("/", "error!!!");
		EMClientHC client = new EMClientHC();
		ManagedExperiment me = client.getExperiment("eco2clouds2", 247);
		assertEquals("eco2clouds2", client.userId);
		assertEquals(null, me);
		
		// We verify that the right headers were sent
		Headers headers = mServer.getHeaders();
		assertEquals("eco2clouds2", headers.getFirst(SchedulerDictionary.X_BONFIRE_ASSERTED_ID));
		assertEquals("eco2clouds", headers.getFirst(SchedulerDictionary.X_BONFIRE_GROUPS_ID));
		assertEquals(SchedulerDictionary.ACCEPT, headers.getFirst("Accept"));
		assertEquals(SchedulerDictionary.USER_AGENT, headers.getFirst("User-Agent"));

	}
	
	private void resetConfigurationUrl() {
		// We clean everything for other tests... just in case!
		Configuration.bonfireEMUrl = emUrl;
	}
	
	private void setMockServerUrl() {
		// Just to leave everything clean after the restart...
		emUrl = Configuration.bonfireEMUrl;
		Configuration.bonfireEMUrl = mBaseURL;
	}
	
	@After
	public void after() {
		resetConfigurationUrl();
		mServer.stop();
	}
}
