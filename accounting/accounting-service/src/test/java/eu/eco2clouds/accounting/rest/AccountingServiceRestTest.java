package eu.eco2clouds.accounting.rest;

import static eu.eco2clouds.accounting.Dictionary.CONTENT_TYPE_ECO2CLOUDS_XML;
import static eu.eco2clouds.accounting.Dictionary.VERSION;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import eu.eco2clouds.accounting.datamodel.Experiment;
import eu.eco2clouds.accounting.datamodel.Testbed;
import eu.eco2clouds.accounting.datamodel.VM;
import eu.eco2clouds.accounting.datamodel.parser.Collection;
import eu.eco2clouds.accounting.datamodel.parser.Items;
import eu.eco2clouds.accounting.datamodel.parser.Link;
import eu.eco2clouds.accounting.rest.service.AccountingServiceExperiment;
import eu.eco2clouds.accounting.service.ExperimentDAO;
import eu.eco2clouds.accounting.service.TestbedDAO;
import eu.eco2clouds.accounting.testbedclient.Client;

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
public class AccountingServiceRestTest {
	
	@Test
	public void testList() throws IOException {
		AccountingServiceRest accountingServiceRest = new AccountingServiceRest();
		
		String list = accountingServiceRest.list();
		
		BufferedReader reader = new BufferedReader(new StringReader(list));
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", reader.readLine());
		assertEquals("<root xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/\">", reader.readLine());
		assertEquals("<version>" + VERSION + "</version>", reader.readLine());
		reader.readLine();
		assertEquals("<link rel=\"testbeds\" href=\"/testbeds\" type=\"application/vnd.eco2clouds+xml\"/>", reader.readLine());
		assertEquals("</root>", reader.readLine());
	}
	
	//@Test
	public void testGetTestbeds() throws IOException {
		// We prepare the objects that we expect to be retrieved by the database.
		Testbed testbed1 = new Testbed();
		testbed1.setName("fr-inria");
		testbed1.setUrl("http://frontend.bonfire.grid5000.fr/one-status.xml");
		Testbed testbed2 = new Testbed();
		testbed2.setName("uk-epcc");
		testbed2.setUrl("http://bonfire.epcc.ed.ac.uk/logs/one-status.xml");
				
		List<Testbed> testbeds = new ArrayList<Testbed>();
		testbeds.add(testbed1);
		testbeds.add(testbed2);
				
		TestbedDAO testbedDAO = mock(TestbedDAO.class);
		when(testbedDAO.getAll()).thenReturn(testbeds); // When it is called it also returns this list
				
		// We create the service and we connect it to the mocked testbedDAO
		AccountingServiceRest accountingServiceRest = new AccountingServiceRest(testbedDAO);
		
		String testbedsXML = accountingServiceRest.getTestbeds();
		
		BufferedReader reader = new BufferedReader(new StringReader(testbedsXML));
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", reader.readLine());
		assertEquals("<collection xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/testbeds\">", reader.readLine());
		assertEquals("<items offset=\"0\" total=\"2\">", reader.readLine());
		assertEquals("<testbed href=\"/testbeds/fr-inria\">", reader.readLine());
		assertEquals("<name>fr-inria</name>", reader.readLine());
		assertEquals("<url>http://frontend.bonfire.grid5000.fr/one-status.xml</url>", reader.readLine());
		assertEquals("<link rel=\"parent\" href=\"/testbeds\" type=\"application/vnd.eco2clouds+xml\"/>", reader.readLine());
		assertEquals("<link rel=\"status\" href=\"/testbed/fr-inria/status\" type=\"application/eco2clouds+xml\"/>", reader.readLine());
		assertEquals("</testbed>", reader.readLine());
		assertEquals("<testbed href=\"/testbeds/uk-epcc\">", reader.readLine());
		assertEquals("<name>uk-epcc</name>", reader.readLine());
		assertEquals("<url>http://bonfire.epcc.ed.ac.uk/logs/one-status.xml</url>", reader.readLine());
		assertEquals("<link rel=\"parent\" href=\"/testbeds\" type=\"application/vnd.eco2clouds+xml\"/>", reader.readLine());
		assertEquals("<link rel=\"status\" href=\"/testbed/uk-epcc/status\" type=\"application/eco2clouds+xml\"/>", reader.readLine());
		assertEquals("</testbed>", reader.readLine());
		assertEquals("</items>", reader.readLine());
		assertEquals("<link href=\"/\" rel=\"parent\" type=\"application/vnd.eco2clouds+xml\"/>", reader.readLine());
		assertEquals("</collection>", reader.readLine());
	}

	@Test
	public void testGetTestbed() throws IOException {
		Testbed testbed = new Testbed();
		testbed.setId(1);
		testbed.setName("uk-epcc");
		testbed.setUrl("https://bonfire.epcc.ed.ac.uk:8443");
		
		TestbedDAO testbedDAO = mock(TestbedDAO.class);
		when(testbedDAO.getByName("uk-epcc")).thenReturn(testbed);
		
		AccountingServiceRest accountingServiceRest = new AccountingServiceRest(testbedDAO);
	
		
		Response response = accountingServiceRest.getTestbed("uk-epcc");
		assertEquals(200, response.getStatus());
		
		eu.eco2clouds.accounting.datamodel.parser.Testbed testbedOut = null;
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(eu.eco2clouds.accounting.datamodel.parser.Testbed.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			testbedOut = (eu.eco2clouds.accounting.datamodel.parser.Testbed) jaxbUnmarshaller.unmarshal(new StringReader((String) response.getEntity()));
		} catch(JAXBException e) {
			System.out.println("Error trying to parse returned list of testbed: /testbeds Exception: " + e.getMessage());
		}
		
		assertEquals("uk-epcc", testbedOut.getName());
	}
	
	@Test
	public void testGetTestbedTest404() {
		TestbedDAO testbedDAO = mock(TestbedDAO.class);
		when(testbedDAO.getByName("uk-epcc")).thenReturn(null);
			
		AccountingServiceRest accountingServiceRest = new AccountingServiceRest(testbedDAO);
		
		
		Response response = accountingServiceRest.getTestbed("uk-epcc");
		assertEquals(404, response.getStatus());
		String testbedXML = (String) response.getEntity();
		assertEquals("Testbed does not exist in the Accounting Service Scheduler Database", testbedXML);
	}
	
	@Test
	public void testGetTestbedHostsStatusInfo() {
		Testbed testbed = new Testbed();
		testbed.setName("pepe");
		testbed.setUrl("http://localhost...");
		
		TestbedDAO testbedDAO = mock(TestbedDAO.class);
		when(testbedDAO.getByName(testbed.getName())).thenReturn(testbed);

		Client client = mock(Client.class);
		when(client.getHostInfo(testbed)).thenReturn("beatiful text...");
		
		AccountingServiceRest accountingServiceRest = new AccountingServiceRest(testbedDAO,client);
		accountingServiceRest.testbedDAO = testbedDAO;
		accountingServiceRest.client = client;
		
		Response response = accountingServiceRest.getTestbedHostsStatusInfo(testbed.getName());
		assertEquals(200, response.getStatus());
		assertEquals("beatiful text...", (String) response.getEntity());
	}
	
	@Test
	public void testGetTestbedHostsStatusInfoTestTestbedNull() {
		TestbedDAO testbedDAO = mock(TestbedDAO.class);
		when(testbedDAO.getByName("uk-epcc")).thenReturn(null);
		
		AccountingServiceRest accountingServiceRest = new AccountingServiceRest(testbedDAO);
		
		Response response = accountingServiceRest.getTestbedHostsStatusInfo("uk-epcc");
		assertEquals(404, response.getStatus());
		assertEquals("Testbed does not exist in the Accounting Service Scheduler Database", (String) response.getEntity());
	}

	
	//@Test
	public void testGetExperiment () throws IOException, JAXBException {
		
		Experiment experiment = new Experiment();
		experiment.setBonfireExperimentId(2);
		experiment.setBonfireGroupId("ATOS");
		experiment.setBonfireUserId("ATOS");
		experiment.setId(4);
		experiment.setApplicationProfile("AP");
		experiment.setSubmittedExperimentDescriptor("ED");
		experiment.setStartTime(1l);
		experiment.setEndTime(2l);
		experiment.setManagedExperimentId(55l);
		Set<VM> vMs = new HashSet<VM>();
		experiment.setvMs(vMs);
		
		ExperimentDAO experimentDAO = mock(ExperimentDAO.class);
		when(experimentDAO.getLastExperiment()).thenReturn(experiment);
		
		AccountingServiceExperiment accountingServiceExperiment = new AccountingServiceExperiment(experimentDAO);
	
		List<String> groups = new ArrayList<String>();
    	groups.add("ATOS");
		
		
		String experimentXML = accountingServiceExperiment.getExperimentXMLRepresentation(experiment);				
		 				
		BufferedReader reader = new BufferedReader(new StringReader(experimentXML));
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", reader.readLine());
		assertEquals("<experiment xmlns:ns2=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/experiments/4\">", reader.readLine());
		assertEquals("    <id>4</id>", reader.readLine());
		assertEquals("    <bonfire-group-id>ATOS</bonfire-group-id>", reader.readLine());
		assertEquals("    <bonfire-user-id>ATOS</bonfire-user-id>", reader.readLine());
		assertEquals("    <bonfire-experiment-id>2</bonfire-experiment-id>",reader.readLine());
		assertEquals("    <managed-experiment-id>55</managed-experiment-id>",reader.readLine());
	   	assertEquals("    <start-time>1</start-time>", reader.readLine());
		assertEquals("    <end-time>2</end-time>", reader.readLine());
		assertEquals("    <application-profile>AP</application-profile>", reader.readLine());
		assertEquals("    <submitted-experiment-descriptor>ED</submitted-experiment-descriptor>",reader.readLine());
		reader.readLine();reader.readLine();reader.readLine();
//	    assertEquals("    <ns2:link rel=\"parent\" href=\"/experiments\" type=\"application/vnd.eco2clouds+xml\"/>",reader.readLine());
//	    assertEquals("    <ns2:link rel=\"submitted-experiment-descriptor\" href=\"/experiments/4/submitted-experiment-descriptor\" type=\"application/json\"/>", reader.readLine());
//		assertEquals("    <ns2:link rel=\"application-profile\" href=\"/experiments/4/application-profile\" type=\"application/json\"/>", reader.readLine());  
    	assertEquals("</experiment>", reader.readLine());
	
	}
	
	
	//@Test
	public void testGetExperiments () {
		
		List<Experiment> experiments= new ArrayList<Experiment>();
		//eu.eco2clouds.accounting.datamodel.parser.Experiment experimentXML = new eu.eco2clouds.accounting.datamodel.parser.Experiment();
		//eu.eco2clouds.accounting.datamodel.parser.Experiment experiment2XML = new eu.eco2clouds.accounting.datamodel.parser.Experiment();
		
		Collection collection = new Collection();
		collection.setHref("/experiments");
		Link link = new Link("parent", "/", CONTENT_TYPE_ECO2CLOUDS_XML);
		collection.addLink(link);
		
		Items items = new Items();
		items.setOffset(0);
		items.setTotal(experiments.size());
		collection.setItems(items);
		
		Experiment experiment = new Experiment();
		experiment.setBonfireExperimentId(2);
		experiment.setBonfireGroupId("users");
		experiment.setBonfireUserId("user");
		experiment.setId(4);
		experiment.setApplicationProfile("AP");
		experiment.setSubmittedExperimentDescriptor("ED");
		experiment.setStartTime(1l);
		experiment.setEndTime(2l);
		experiment.setManagedExperimentId(55l);
		Set<VM> vMs = new HashSet<VM>();
		experiment.setvMs(vMs);
		
				
		Experiment experiment2 = new Experiment(5, "ATOS", "A100", 100, 25000, 35000, "application-profile-A100", "submittedExperimentDescriptor-A100");

		ExperimentDAO experimentDAO = mock(ExperimentDAO.class);
		when(experimentDAO.getAll()).thenReturn(experiments);
		
				
//		items.addExperiment(experiment);
//		items.addExperiment(experiment2XML);
		
		assertEquals(2,experiments.size() );
//        assertEquals("users",items.getExperiments().get(0).getBonfireGroupId());
//		assertEquals("user",items.getExperiments().get(0).getBonfireUserId());
//		assertEquals("AP", items.getExperiments().get(0).getApplicationProfile());
//		assertEquals("ED", items.getExperiments().get(0).getSubmittedExperimentDescriptor());
//
//
//	    assertEquals("ATOS",items.getExperiments().get(1).getBonfireGroupId());
//		assertEquals("A100",items.getExperiments().get(1).getBonfireUserId());
//		assertEquals("application-profile-A100", items.getExperiments().get(1).getApplicationProfile());
//		assertEquals("submittedExperimentDescriptor-A100", items.getExperiments().get(1).getSubmittedExperimentDescriptor());
		
		
	}
	
	@Test
	public void testGetApplicationProfileFromExperiment () throws IOException, JAXBException{
		
		Experiment experiment = new Experiment();
		experiment.setBonfireExperimentId(2);
		experiment.setBonfireGroupId("ATOS");
		experiment.setBonfireUserId("ATOS");
		experiment.setId(4);
		experiment.setApplicationProfile("AP");
		experiment.setSubmittedExperimentDescriptor("ED");
		experiment.setStartTime(1l);
		experiment.setEndTime(2l);
		experiment.setManagedExperimentId(55l);
		Set<VM> vMs = new HashSet<VM>();
		experiment.setvMs(vMs);
		
		ExperimentDAO experimentDAO = mock(ExperimentDAO.class);
		when(experimentDAO.getLastExperiment()).thenReturn(experiment);
		
		AccountingServiceExperiment accountingServiceExperiment = new AccountingServiceExperiment(experimentDAO);
	
		List<String> groups = new ArrayList<String>();
    	groups.add("ATOS");
		
		
		//String experimentXML = accountingServiceExperiment.getExperimentXMLRepresentation(experiment);
		
   	

    	String applicationProfileString = experiment.getApplicationProfile();
    	BufferedReader reader = new BufferedReader(new StringReader(applicationProfileString));
    	assertEquals("AP", reader.readLine());
			
	}
	
    @Test
    public void testGetSubmittedExperimentDescriptorFromExperiment () throws IOException {
    	
    	
    	Experiment experiment = new Experiment();
		experiment.setBonfireExperimentId(2);
		experiment.setBonfireGroupId("ATOS");
		experiment.setBonfireUserId("ATOS");
		experiment.setId(4);
		experiment.setApplicationProfile("AP");
		experiment.setSubmittedExperimentDescriptor("ED");
		experiment.setStartTime(1l);
		experiment.setEndTime(2l);
		experiment.setManagedExperimentId(55l);
		Set<VM> vMs = new HashSet<VM>();
		experiment.setvMs(vMs);
		
		ExperimentDAO experimentDAO = mock(ExperimentDAO.class);
		when(experimentDAO.getLastExperiment()).thenReturn(experiment);
		
		AccountingServiceExperiment accountingServiceExperiment = new AccountingServiceExperiment(experimentDAO);
	
		List<String> groups = new ArrayList<String>();
    	groups.add("ATOS");
		
	   	String experimentDescriptorString = experiment.getSubmittedExperimentDescriptor();
    	BufferedReader reader = new BufferedReader(new StringReader(experimentDescriptorString));
    	assertEquals("ED", reader.readLine());
    	
    }
	
//	@Test
//	public void testPutExperiment () {
//		
		
//	}
//	
//	@Test
//	public void testGetActionTypes () {
//		
//		
//	}
//	
//	@Test
//	public void testPutActionTypes () {
//		
//		
//	}
//	
//	@Test
//	public void testGetVMSFromExperiment () {
//		
//		
//	}
//	
//	@Test
//	public void testPutVMSFromExperiment () {
//		
//		
//	}
//	
//	@Test
//	public void testGetVMIDFromExperiment() {
//		
//		
//	}
//	
//	
//	@Test
//	public void testPutVMIDFromExperiment() {
//		
//		
//	}
	
	

//	@Test
//	public void testPutExperiment () {
//		
//		
//	}
//	
//	@Test
//	public void testGetActionTypes () {
//		
//		
//	}
//	
//	@Test
//	public void testPutActionTypes () {
//		
//		
//	}
//	
//	@Test
//	public void testGetVMSFromExperiment () {
//		
//		
//	}
//	
//	@Test
//	public void testPutVMSFromExperiment () {
//		
//		
//	}
//	
//	@Test
//	public void testGetVMIDFromExperiment() {
//		
//		
//	}
//	
//	
//	@Test
//	public void testPutVMIDFromExperiment() {
//		
//		
//	}
	
	
}
