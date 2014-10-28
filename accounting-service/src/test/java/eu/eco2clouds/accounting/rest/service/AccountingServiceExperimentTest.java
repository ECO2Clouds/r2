package eu.eco2clouds.accounting.rest.service;

import static eu.eco2clouds.accounting.Dictionary.E2C_NAMESPACE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.junit.Test;

import com.mockrunner.mock.jdbc.MockResultSet;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import eu.eco2clouds.accounting.datamodel.Experiment;
import eu.eco2clouds.accounting.datamodel.VM;
import eu.eco2clouds.accounting.datamodel.parser.Collection;
import eu.eco2clouds.accounting.datamodel.parser.Testbed;
import eu.eco2clouds.accounting.datamodel.parser.TestbedMonitoring;
import eu.eco2clouds.accounting.rest.service.AccountingServiceExperiment.Field;
import eu.eco2clouds.accounting.service.ExperimentDAO;
import eu.eco2clouds.accounting.util.HttpHeadersImpl;

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
public class AccountingServiceExperimentTest {
	
	@Test
	public void getListOfExperimentsTest() throws Exception {
		// We prepare the objects that we expect to be retrieved by the database.
		Experiment experiment1 = new Experiment();
		experiment1.setId(1);
		experiment1.setBonfireExperimentId(101L);
		experiment1.setBonfireUserId("A100");
		experiment1.setBonfireGroupId("ATOS");
		experiment1.setStartTime(15000L);
		experiment1.setEndTime(18000L);

		Experiment experiment2 = new Experiment();
		experiment2.setId(2);
		experiment2.setBonfireExperimentId(102L);
		experiment2.setBonfireUserId("E102");
		experiment2.setBonfireGroupId("EPCC");
		experiment2.setStartTime(25000L);
		experiment2.setEndTime(28000L);

			
		
		List<Experiment> experiments = new ArrayList<Experiment>();
		experiments.add(experiment1);
		experiments.add(experiment2);
		
		List<String> groups = new ArrayList<String>();
		
		groups.add("EPCC");
		groups.add("ATOS");
		
		ExperimentDAO experimentDAO = mock(ExperimentDAO.class);
		when(experimentDAO.getListExperimentByGroupId(groups)).thenReturn(experiments); // When it is called it also returns this list
		
		// We create the service and we connect it to the mocked experimentDAO
		AccountingServiceExperiment accountingService = new AccountingServiceExperiment(experimentDAO);
       		
		String experimentString = accountingService.printInformation("GET", -1, groups,null); 

		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(experimentString));
		XPath xpath = XPath.newInstance("//bnf:collection");
		xpath.addNamespace("bnf", E2C_NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element storageElement = (Element) listxpath.get(0);
		assertEquals("/experiments", storageElement.getAttributeValue("href"));
		
		XPath xpathName = XPath.newInstance("//bnf:items");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		List listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		Element element = (Element) listxpathName.get(0);
		assertEquals("0", element.getAttributeValue("offset"));
		assertEquals("2", element.getAttributeValue("total"));
		
		xpathName = XPath.newInstance("//bnf:link");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		System.out.println(listxpath.get(0).toString());
		assertEquals(7, listxpath.size());
		element = (Element) listxpath.get(6);
		assertEquals("parent", element.getAttributeValue("rel"));
		assertEquals("/", element.getAttributeValue("href"));
		
		xpathName = XPath.newInstance("//bnf:experiment");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(2, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("/experiments/1", element.getAttributeValue("href"));
		
		xpathName = XPath.newInstance("//bnf:bonfire-experiment-id");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(2, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("101", element.getValue());

	}
	

	//@Test
	public void getExperimentTest() throws IOException {
		
		StringBuilder xmlResponse = new StringBuilder();
		Field fieldExperiment = null;
		String field = "application_profile";
		
		// We prepare the objects that we expect to be retrieved by the database.
		Experiment experiment1 = new Experiment();
		experiment1.setId(1);
		experiment1.setBonfireExperimentId(101L);
		experiment1.setBonfireUserId("A100");
		experiment1.setBonfireGroupId("ATOS");
		experiment1.setStartTime(15000L);
		experiment1.setEndTime(18000L);
			
		
		List<String> groups = new ArrayList<String>();
		groups.add("ATOS");

			
		ExperimentDAO experimentDAO = mock(ExperimentDAO.class);
		when(experimentDAO.getExperimentByGroupId(1, groups)).thenReturn(experiment1); // When it is called it also returns this list	
		
		// We create the service and we connect it to the mocked experimentDAO		
		AccountingServiceExperiment accountingService = new AccountingServiceExperiment(experimentDAO);
		
		
		if(!accountingService.doesElementIdExist(1)) System.out.println(accountingService.buildResponse(404,"Experiment " + 1  + " does not exist in the Accounting Service Scheduler Database"));	
	
   		
		Experiment experiment = experimentDAO.getExperimentByGroupId(experiment1.getId(), groups);
		
		System.out.println("experiment:" + experiment);
				
		if (experiment!= null)
		{ 
			xmlResponse.append("<experiment xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/occi\" href=\"/experiments/"+ experiment.getId() + "\">\n");
			xmlResponse.append(accountingService.printElement(experiment));
			
			
		}
		BufferedReader reader = new BufferedReader(new StringReader(xmlResponse.toString()));
		
		assertEquals("<experiment xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/occi\" href=\"/experiments/1\">", reader.readLine());
		assertEquals("<id>1</id>",reader.readLine());
		assertEquals("<bonfire-experiment-id>101</bonfire-experiment-id>",reader.readLine());
		assertEquals("<bonfire-user-id>A100</bonfire-user-id>",reader.readLine());
		assertEquals("<bonfire-groupd-id>ATOS</bonfire-group-id>",reader.readLine());
		assertEquals("<start-time>15000</start-time>",reader.readLine());
		assertEquals("<end-time>18000</end-time>",reader.readLine());
		assertEquals("<link rel=\"parent\" href=\"/experiments\"/>",reader.readLine());
		assertEquals("<link rel=\"application-profile\" href=\"/experiments/1/application-profile\"/>",reader.readLine());
		assertEquals("<link rel=\"submitted-experiment-descriptor\" href=\"/experiments/1/submitted-experimen-descriptor\"/>",reader.readLine());
		assertEquals("<link rel=\"vms\" href=\"/experiments/1/vms\"/>",reader.readLine());  
		assertEquals("</experiment>",reader.readLine());
		
		
		

		if (field != null) fieldExperiment= Field.valueOf(field);
		
		if (fieldExperiment != null) {
			xmlResponse = new StringBuilder();
			
			switch(fieldExperiment) {
			    /**@Path("/experiments/{id}/application-profile")*/
			    case application_profile:
			    	xmlResponse.append(experiment.getApplicationProfile() + "\n");
			        break;
			    /**@Path("/experiments/{id}/submitted_experiment_descriptor")*/
			    case submitted_experiment_descriptor:
			    	xmlResponse.append(experiment.getSubmittedExperimentDescriptor() + "\n");
			        break;
			}
		}
	
	}
	
	@Test
	public void getVMsOfExperimentTest() {
		ExperimentDAO experimentDAO = mock(ExperimentDAO.class);
		AccountingServiceExperiment aSExperiment = new AccountingServiceExperiment(experimentDAO);
		
		Experiment experiment = new Experiment();
		experiment.setId(1);
		experiment.setBonfireExperimentId(101L);
		experiment.setBonfireUserId("A100");
		experiment.setBonfireGroupId("ATOS");
		experiment.setStartTime(15000L);
		experiment.setEndTime(18000L);
		
		VM vm = new VM();
		vm.setId(1);
		vm.setIp("0.0.0.0");
		vm.setBonfireUrl("url");
		
		Set<VM> vms = new HashSet<VM>();
		vms.add(vm);
		experiment.setvMs(vms);
		
		when(experimentDAO.getById(1)).thenReturn(experiment);
		
		Response response = aSExperiment.getVMsOfExperiment(1);
		assertEquals(200, response.getStatus());
		assertTrue(response.getEntity() != null);
		
		Collection collection = null;
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			collection = (Collection) jaxbUnmarshaller.unmarshal(new StringReader((String) response.getEntity()));
		} catch(JAXBException e) {
			System.out.println("Error trying to parse returned list of testbed: /testbeds Exception: " + e.getMessage());
		}
		
		assertEquals(1, collection.getItems().getvMs().size());
		assertEquals("0.0.0.0", collection.getItems().getvMs().get(0).getIp());
	}
	
	@Test
	public void addVMToExperimentNullExperiment() {
		ExperimentDAO experimentDAO = mock(ExperimentDAO.class);
		AccountingServiceExperiment aSExperiment = new AccountingServiceExperiment(experimentDAO);
		when(experimentDAO.getById(1)).thenReturn(null);
		
		Response response = aSExperiment.addVMToExperiment(1, null, null);
		assertEquals(400, response.getStatus());		
	}
	
	@Test
	public void addVMToExperimentWrongGroup() {
		ExperimentDAO experimentDAO = mock(ExperimentDAO.class);
		AccountingServiceExperiment aSExperiment = new AccountingServiceExperiment(experimentDAO);
		Experiment experiment = new Experiment();
		experiment.setBonfireGroupId("eco2clouds");
		when(experimentDAO.getById(1)).thenReturn(experiment);
		
		HttpHeadersImpl hh = new HttpHeadersImpl();
		MultivaluedMap<String, String> map = new MultivaluedMapImpl();
		map.add("x-bonfire-asserted-id", "pepito");
		map.add("xxxx","yyyy");
		map.add("x_bonfire_groups_id", "pepito,manolito,pedrito");
		hh.setRequestHeaders(map);
				
		Response response = aSExperiment.addVMToExperiment(1, hh, null);
		assertEquals(403, response.getStatus());		
	}
	
	@Test
	public void addVMToExperiment() {
		ExperimentDAO experimentDAO = mock(ExperimentDAO.class);
		AccountingServiceExperiment aSExperiment = new AccountingServiceExperiment(experimentDAO);
		Experiment experiment = new Experiment();
		experiment.setBonfireGroupId("eco2clouds");
		when(experimentDAO.getById(1)).thenReturn(experiment);

		HttpHeadersImpl hh = new HttpHeadersImpl();
		MultivaluedMap<String, String> map = new MultivaluedMapImpl();
		map.add("x-bonfire-asserted-id", "pepito");
		map.add("xxxx","yyyy");
		map.add("x_bonfire_groups_id", "eco2clouds,manolito,pedrito");
		hh.setRequestHeaders(map);
				
		Response response = aSExperiment.addVMToExperiment(1, hh, null);
		assertEquals(response, null);		
	}
	
	@Test
	public void getVMsOfExperimentWrongIdTest() {
		ExperimentDAO experimentDAO = mock(ExperimentDAO.class);
		AccountingServiceExperiment aSExperiment = new AccountingServiceExperiment(experimentDAO);
		
		when(experimentDAO.getById(1)).thenReturn(null);
		
		Response response = aSExperiment.getVMsOfExperiment(1);
		assertEquals(400, response.getStatus());
		assertTrue(response.getEntity() != null);
		assertEquals("No experiment under the ID=1", (String) response.getEntity());
	}
	
//	@Test
//	public void getApplicationMetricXMLRepresentationFromSQLQueryTest() throws Exception {
//		
////		+----------+---------------------+---------------+------------+-------+-------+
////		| id_items | name                | zabbix_itemid | clock      | value | unity |
////		+----------+---------------------+---------------+------------+-------+-------+
////		| 20383055 | applicationmetric_1 |         23058 | 1405070449 |    17 |       |
////		+----------+---------------------+---------------+------------+-------+-------+
//
//
//		MockResultSet result = new MockResultSet("myMock");
//		result.addColumn("id_items");
//		result.addColumn("name");
//		result.addColumn("zabbix_itemid");
//		result.addColumn("clock");
//		result.addColumn("value");
//		result.addColumn("unity");
//
//		result.addRow(new Object[] {"20383055", "applicationmetric_1", "23058", "1405070449", "17", "MW" });
//		
//		AccountingServiceExperiment service = new AccountingServiceExperiment(null);
//
//		String output = service.getApplicationMetricXMLRepresentationFromSQLQuery((ResultSet) result);
//		System.out.println(output);
//		
//		JAXBContext jaxbContext = JAXBContext.newInstance(TestbedMonitoring.class);
//		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//		TestbedMonitoring testbedMonitoring = (TestbedMonitoring) jaxbUnmarshaller.unmarshal(new StringReader(output));
//		
//		assertEquals("/testbeds/fr-inria/monitoring", testbedMonitoring.getHref());
//		
//		assertEquals("Hydraulic", testbedMonitoring.getHydraulic().getName().toString());
//		assertEquals("MW", testbedMonitoring.getHydraulic().getUnity().toString());
//		assertEquals("13508.0", testbedMonitoring.getHydraulic().getValue().toString());
//		assertEquals("1392975360", testbedMonitoring.getHydraulic().getClock().toString());
//	}
}
