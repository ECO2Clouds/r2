package eu.eco2clouds.scheduler.rest;

import static eu.eco2clouds.scheduler.SchedulerDictionary.CONTENT_TYPE_ECO2CLOUDS_XML;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.sun.jersey.core.util.MultivaluedMapImpl;

import eu.eco2clouds.accounting.datamodel.parser.AggregateEnergyUsage;
import eu.eco2clouds.accounting.datamodel.parser.Coal;
import eu.eco2clouds.accounting.datamodel.parser.Collection;
import eu.eco2clouds.accounting.datamodel.parser.DiskIOPS;
import eu.eco2clouds.accounting.datamodel.parser.Experiment;
import eu.eco2clouds.accounting.datamodel.parser.Host;
import eu.eco2clouds.accounting.datamodel.parser.HostMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.Items;
import eu.eco2clouds.accounting.datamodel.parser.Link;
import eu.eco2clouds.accounting.datamodel.parser.Other;
import eu.eco2clouds.accounting.datamodel.parser.Testbed;
import eu.eco2clouds.accounting.datamodel.parser.TestbedMonitoring;
import eu.eco2clouds.scheduler.accounting.client.AccountingClient;
import eu.eco2clouds.scheduler.util.HttpHeadersImpl;

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
public class RestSchedulerTest {
	private static Logger logger = Logger.getLogger(RestSchedulerTest.class);
	
	@Test
	public void listRootTest() throws IOException {
		RestScheduler restScheduler = new RestScheduler();
		String rootList = restScheduler.listRoot();
		
		BufferedReader reader = new BufferedReader(new StringReader(rootList));
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", reader.readLine());
		assertEquals("<root xmlns=\"http://scheduler.eco2clouds.eu/doc/schemas/xml\" href=\"/\">", reader.readLine());
		assertEquals("<version>0.1</version>", reader.readLine());
		reader.readLine();
		assertEquals("<link rel=\"experiments\" href=\"/experiments\" type=\"application/vnd.eco2clouds+xml\"/>", reader.readLine());
		assertEquals("</root>", reader.readLine());
	}
	
//	@Test
//	public void submitApplicationProfileTest() {
//		String  validApplicationProfile= "{ \"applicationprofile\": {"
//				+ "\"flow\": {"
//						+"          \"sequence\": ["
//						+"              {\"task\":\"task1\"},"
//						+"              {\"task\":\"task2\"},"
//						+"                {"
//						+"                  \"branches\": ["
//						+"                        {"
//						+"                          \"branch\": ["
//						+"                                {"
//						+"                                  \"prob\": 0.3"
//						+"                                },"
//						+"                              {\"task\":\"task3\"}"
//						+"                            ]"
//						+"                        },"
//						+"                        {"
//						+"                          \"branch\": ["
//						+"                                {"
//						+"                                  \"prob\": 0.7"
//						+"                                },"
//						+"                              {\"task\":\"task4\"}"
//						+"                            ]"
//						+"                        }"
//						+"                    ]"
//						+"                },"
//						+"              {\"task\": \"task5\"},"
//						+"                {"
//						+"                  \"loop\": ["
//						+"                        {"
//						+"                          \"sequence\": ["
//						+"                              {\"task\":\"task6\"},"
//						+"                              {\"task\":\"task7\"}"
//						+"                            ]"
//						+"                        },"
//						+"                        {"
//						+"                          \"iteration\": {"
//						+"                              \"num\": 1,"
//						+"                              \"prob\": 0.7"
//						+"                            }"
//						+"                        },"
//						+"                        {"
//						+"                          \"iteration\": {"
//						+"                              \"num\": 2,"
//						+"                              \"prob\": 0.3"
//						+"                            }"
//						+"                        },"
//						+"                        {"
//						+"                          \"iteration\": {"
//						+"                              \"num\": 3,"
//						+"                              \"prob\": 0.1"
//						+"                            }"
//						+"                        },"
//						+"                        {"
//						+"                          \"iteration\": {"
//						+"                              \"num\": 4,"
//						+"                              \"prob\": 0.01"
//						+"                            }"
//						+"                        }"
//						+"                    ]"
//						+"                }"
//						+"            ]"
//						+"        },"
//				+"      \"requirements\": {"
//				+"          \"constraints\": ["
//				+"                {"
//				+"                  \"indicator\":\"A-PUE\","
//				+"                  \"element\":\"VM1\","
//				+"                  \"operator\":\"<\","
//				+"                  \"value\":\"1.4\""
//				+"                },"
//				+"                {"
//				+"                  \"indicator\":\"Responsetime\","
//				+"                  \"element\":\"Task1\","
//				+"                  \"operator\":\"<\","
//				+"                  \"value\":\"1ms\""
//				+"                },"
//				+"                {"
//				+"                  \"indicator\":\"Responsetime\","
//				+"                  \"element\":\"Application\","
//				+"                  \"operator\":\"<\","
//				+"                  \"value\":\"10ms\""
//				+"                },"
//				+"                {"
//				+"                  \"indicator\":\"CPUUsage\","
//				+"                  \"element\":\"VM1\","
//				+"                  \"operator\":\"><\","
//				+"                  \"values\": ["
//				+"                      \"60\","
//				+"                      \"90\""
//				+"                    ]"
//				+"                }"
//				+"            ]"
//				+"        },"
//				+"      \"resources\": {"
//				+"          \"name\":\"MyExperiment\","
//				+"          \"description\":\"Experimentdescription\","
//				+"          \"duration\": 120,"
//				+"          \"resources\": ["
//				+"                {"
//				+"                  \"compute\": {"
//				+"                      \"name\":\"Client\","
//				+"                      \"description\":\"A description of the client\","
//				+"                      \"instanceType\":\"small\","
//				+"                      \"locations\": ["
//				+"                          \"fr-inria\""
//				+"                        ],"
//				+"                      \"resources\": ["
//				+"                            {"
//				+"                              \"storage\":\"@BonFIREDebianSqueezev5\""
//				+"                            },"
//				+"                            {"
//				+"                              \"network\":\"@BonFIREWAN\""
//				+"                            }"
//				+"                        ]"
//				+"                    }"
//				+"                },"
//				+"                {"
//				+"                  \"compute\": {"
//				+"                      \"name\":\"Server\","
//				+"                      \"description\":\"A description of the server\","
//				+"                      \"instanceType\":\"small\","
//				+"                      \"locations\": ["
//				+"                          \"uk-epcc\""
//				+"                        ],"
//				+"                      \"resources\": ["
//				+"                            {"
//				+"                              \"storage\":\"@BonFIREDebianSqueezev3\""
//				+"                            },"
//				+"                            {"
//				+"                              \"network\":\"@BonFIREWAN\""
//				+"                            }"
//				+"                        ]"
//				+"                    }"
//				+"                }"
//				+"            ]"
//				+"        },"
//				+"      \"data\": {"
//				+"          \"datadependency\": ["
//				+"              {\"task\":\"task1\"},"
//				+"              {\"task\":\"task2\"}"
//				+"            ]"
//				+"        }"
//				+"    }"
//				+"}";
//		
//		// This first test should return submitted experiment descriptor
//		RestScheduler restScheduler = new RestScheduler();
//		Response response = restScheduler.submitApplicationProfile(getHeaders(), validApplicationProfile);
//		
//		verifyHeaders(restScheduler);
//		
//		assertEquals(201, response.getStatus());
//		assertEquals("Experiment submitted.", (String) response.getEntity());
//		
//		// This should return an 400 error code with parsing error
//		String invalidJson = "<sdasd> ...";
//		response = restScheduler.submitApplicationProfile(getHeaders(), invalidJson);
//		
//		verifyHeaders(restScheduler);
//		
//		assertEquals(400, response.getStatus());
//		assertEquals("Error parsing Application Profile.", (String) response.getEntity());
//	}
	
	private HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeadersImpl();
		MultivaluedMap<String, String> map = new MultivaluedMapImpl();
		map.add("x-bonfire-asserted-id", "pepito");
		map.add("xxxx","yyyy");
		map.add("x_bonfire_groups_id", "pepito,manolito,pedrito");
		((HttpHeadersImpl) headers).setRequestHeaders(map);
		
		return headers;
	}
	
	private void verifyHeaders(RestScheduler restScheduler) {
		assertEquals("pepito", restScheduler.userId);
		assertEquals("pepito", restScheduler.groups.get(0));
		assertEquals("manolito", restScheduler.groups.get(1));
		assertEquals("pedrito", restScheduler.groups.get(2));
	}
	
	@Test
	public void parsingHeaders() {
		RestScheduler scheduler = new RestScheduler();
		scheduler.parsingHeaders(getHeaders());
		
		verifyHeaders(scheduler);
	}
	
	@Test
	public void parsingUserId() {
		MultivaluedMap<String, String> map = new MultivaluedMapImpl();
		map.add("x-bonfire-asserted-id", "pepito");
		map.add("xxxx","yyyy");
		RestScheduler restScheduler = new RestScheduler();
		assertEquals("pepito", restScheduler.getUserId(map));
		
		map = new MultivaluedMapImpl();
		map.add("xxxx","yyyy");
		map.add("x_bonfire_asserted_id", "pepito2");
		assertEquals("pepito2", restScheduler.getUserId(map));
		
		map = new MultivaluedMapImpl();
		map.add("X-BONFIRE-ASSERTED-ID", "pepito3");
		map.add("xxxx","yyyy");
		assertEquals("pepito3", restScheduler.getUserId(map));
		
		map = new MultivaluedMapImpl();
		map.add("X_BONFIRE_ASSERTED_ID", "pepito4");
		map.add("xxxx","yyyy");
		assertEquals("pepito4", restScheduler.getUserId(map));
		
		map = new MultivaluedMapImpl();
		map.add("xxxx", "xxxx");
		assertEquals("", restScheduler.getUserId(map));
	}
	
	@Test
	public void parsingGroupsId() {
		MultivaluedMap<String, String> map = new MultivaluedMapImpl();
		map.add("x-bonfire-groups-id", "pepito,manolito");
		map.add("xxxx","yyyy");
		RestScheduler restScheduler = new RestScheduler();
		List<String> groups =  restScheduler.getGroupsIds(map);
		assertEquals("pepito", groups.get(0));
		assertEquals("manolito", groups.get(1));
		
		map = new MultivaluedMapImpl();
		map.add("xxxx","yyyy");
		map.add("x_bonfire_groups_id", "pepito,manolito,pedrito");
		groups =  restScheduler.getGroupsIds(map);
		assertEquals("pepito", groups.get(0));
		assertEquals("manolito", groups.get(1));
		assertEquals("pedrito", groups.get(2));
		
		map = new MultivaluedMapImpl();
		map.add("X-BONFIRE-GROUPS-ID", "pepito,manolito");
		map.add("xxxx","yyyy");
		groups =  restScheduler.getGroupsIds(map);
		assertEquals("pepito", groups.get(0));
		assertEquals("manolito", groups.get(1));
		
		map = new MultivaluedMapImpl();
		map.add("X_BONFIRE_GROUPS_ID", "pepito,manolito,pedrito");
		map.add("xxxx","yyyy");
		groups =  restScheduler.getGroupsIds(map);
		assertEquals("pepito", groups.get(0));
		assertEquals("manolito", groups.get(1));
		assertEquals("pedrito", groups.get(2));
		
		map = new MultivaluedMapImpl();
		map.add("xxxx", "xxxx");
		groups =  restScheduler.getGroupsIds(map);
		assertEquals(0, groups.size());
	}
	
	@Test
	public void getExperiments() throws Exception {
		AccountingClient ac = mock(AccountingClient.class);
		RestService service = new RestService();
		service.ac = ac;
		
		Experiment experiment1 = new Experiment();
		experiment1.setId(1);
		Experiment experiment2 = new Experiment();
		experiment2.setId(2);
		
		List<Experiment> experiments = new ArrayList<Experiment>();
		experiments.add(experiment1);
		experiments.add(experiment2);
		
		when(ac.getListOfExperiments("", "eco2clouds")).thenReturn(experiments);
		
		Response response = service.getExperimentsFromAccountingService();
		assertEquals(200, response.getStatus());
		assertTrue(response.getEntity() != null);
	}
	
	@Test
	public void getExperimentFromAccountingService() throws Exception {
		AccountingClient ac = mock(AccountingClient.class);
		RestService service = new RestService();
		service.ac = ac;
		
		Experiment experiment1 = new Experiment();
		experiment1.setId(1);
				
		when(ac.getExperiment(experiment1.getId(), "", "eco2clouds")).thenReturn(experiment1);
		
		Response response = service.getExperimentFromAccountingService(1);
		assertEquals(200, response.getStatus());
		assertTrue(response.getEntity() != null);
	}
	
	@Test
	public void getTotalCo2FromExperiment() throws Exception {
		AccountingClient ac = mock(AccountingClient.class);
		RestService service = new RestService();
		service.ac = ac;
		
		Experiment experiment1 = new Experiment();
		experiment1.setId(1);
				
		when(ac.getCo2Consumption(1)).thenReturn(24.0);
		
		Response response = service.getTotalCo2FromExperiment(1);
		assertEquals(200, response.getStatus());
		assertTrue(response.getEntity() != null);
	}
	
	@Test
	public void getTestbed() throws Exception {
		AccountingClient ac = mock(AccountingClient.class);
		RestService service = new RestService();
		service.ac = ac;
		
		Testbed testbed = new Testbed();
		testbed.setId(0);
		testbed.setName("pepito");
		testbed.setUrl("pepito");
		
		when(ac.getTestbed("pepito")).thenReturn(testbed);
		
		Response response = service.getTestbedFromAccountingService("pepito");
		assertEquals(200, response.getStatus());
		assertTrue(response.getEntity() != null);
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Testbed.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			testbed = (Testbed) jaxbUnmarshaller.unmarshal(new StringReader((String) response.getEntity()));
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned list of testbed: /testbeds Exception: " + e.getMessage());
		}
		
		assertEquals("pepito", testbed.getName());
	}
	
	@Test
	public void getHostsFromTestbed() throws Exception {
		AccountingClient ac = mock(AccountingClient.class);
		RestService service = new RestService();
		service.ac = ac;
		
		List<Host> hosts = new ArrayList<Host>();
		Host host1 = new Host();
		host1.setName("pepito1");
		Host host2 = new Host();
		host2.setName("pepito2");
		hosts.add(host1);
		hosts.add(host2);
		
		when(ac.getHostsOfTesbed("uk-epcc")).thenReturn(hosts);
		
		Response response = service.getHostsOfTestbedsFromAccounting("uk-epcc");
		assertEquals(200, response.getStatus());
		assertTrue(response.getEntity() != null);
		
		Collection collection = new Collection();
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			collection = (Collection) jaxbUnmarshaller.unmarshal(new StringReader((String) response.getEntity()));
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned list of testbed: /testbeds Exception: " + e.getMessage());
		}
		
		ArrayList<Link> links = collection.getLinks();
		assertEquals(1, links.size());
		assertEquals("parent", links.get(0).getRel());
		assertEquals("/testbeds/uk-epcc", links.get(0).getHref());
		assertEquals(CONTENT_TYPE_ECO2CLOUDS_XML, links.get(0).getType());
		
		Items items = collection.getItems();
		assertEquals(0, items.getOffset());
		assertEquals(2, items.getTotal());
		
		hosts = collection.getItems().getHosts();
		assertEquals(2, hosts.size());
		assertEquals("pepito1", hosts.get(0).getName());
	}
	
	@Test
	public void getHostFromTestbed() throws Exception {
		AccountingClient ac = mock(AccountingClient.class);
		RestService service = new RestService();
		service.ac = ac;
		
		Host host = new Host();
		host.setName("pepito");
		
		when(ac.getHostOfTestbed("uk-epcc", "pepito")).thenReturn(host);
		
		Response response = service.getHostOfTestbedFromAccountin("uk-epcc", "pepito");
		assertEquals(200, response.getStatus());
		assertTrue(response.getEntity() != null);

		Host host2 = null;
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Host.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			host2 = (Host) jaxbUnmarshaller.unmarshal(new StringReader((String) response.getEntity()));
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned list of testbed: /testbeds Exception: " + e.getMessage());
		}
		
		assertEquals("pepito", host2.getName());
	}
	
	@Test
	public void getTestbeds() throws Exception {
		AccountingClient ac = mock(AccountingClient.class);
		RestService service = new RestService();
		service.ac = ac;
		
		Testbed testbed = new Testbed();
		testbed.setName("pepito");
		
		List<Testbed> testbeds = new ArrayList<Testbed>();
		testbeds.add(testbed);
		
		when(ac.getListOfTestbeds()).thenReturn(testbeds);
		
		Response response = service.getTestbedsFromAccountingService();
		assertEquals(200, response.getStatus());
		assertTrue(response.getEntity() != null);
		
		Collection collection = new Collection();
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			collection = (Collection) jaxbUnmarshaller.unmarshal(new StringReader((String) response.getEntity()));
		} catch(JAXBException e) {
			logger.warn("Error trying to parse returned list of testbed: /testbeds Exception: " + e.getMessage());
		}
		
		ArrayList<Link> links = collection.getLinks();
		assertEquals(1, links.size());
		assertEquals("parent", links.get(0).getRel());
		assertEquals("/", links.get(0).getHref());
		assertEquals(CONTENT_TYPE_ECO2CLOUDS_XML, links.get(0).getType());
		
		Items items = collection.getItems();
		assertEquals(0, items.getOffset());
		assertEquals(1, items.getTotal());
		
		testbeds = collection.getItems().getTestbeds();
		assertEquals(1, testbeds.size());
		assertEquals("pepito", testbeds.get(0).getName());
	}
	
	
	@Test
	public void getTestbedMonitoringFromAccountingService() throws Exception {
		AccountingClient ac = mock(AccountingClient.class);
		RestService service = new RestService();
		service.ac = ac;
		
		String testbedName = "uk-epcc";			
		TestbedMonitoring tbm = new TestbedMonitoring();
		
		Other other = new Other();
		other.setName("Other power");
		other.setClock((long) 1381512234);
		other.setUnity("MW");
		other.setValue(1984.0);
		tbm.setOther(other);
		
		Coal coal = new Coal();
		coal.setName("Coal power");
		coal.setClock((long) 1381512235);
		coal.setUnity("MW");
		coal.setValue(3949.0);
		tbm.setCoal(coal);		
		
		Testbed tb = new Testbed();
		tb.setName(testbedName);
		when(ac.getTestbedMonitoringStatus((Testbed) any())).thenReturn(tbm);
		
		Response response = service.getTestbedMonitoringFromAccountingService(testbedName);
		assertEquals(200, response.getStatus());
		assertTrue(response.getEntity() != null);
	}
	
	@Test
	public void getHostMonitoringFromAccountingService() throws Exception {
		AccountingClient ac = mock(AccountingClient.class);
		RestService service = new RestService();
		service.ac = ac;
		
		String testbedName = "uk-epcc";
		String hostName = "crockett0";
		
		HostMonitoring hm = new HostMonitoring();
		
		AggregateEnergyUsage aeu = new AggregateEnergyUsage();
		aeu.setName("Aggregate energy usage");
		aeu.setClock((long) 1389731620);
		aeu.setUnity("Wh");
		aeu.setValue((double) 2);
		hm.setAggregateEnergyUsage(aeu);
		
		DiskIOPS diops = new DiskIOPS();
		diops.setName("Disk IOPS");
		diops.setClock((long) 1389731620);
		diops.setUnity("Wh");
		diops.setValue((double) 2);
		hm.setDiskIOPS(diops);

		Testbed tb = new Testbed();
		tb.setName(testbedName);
		when(ac.getHostMonitoringStatus((Testbed) any(), (String) any())).thenReturn(hm);
		
		Response response = service.getHostMonitoringFromAccountingService(testbedName, hostName);
		assertEquals(200, response.getStatus());
		assertTrue(response.getEntity() != null);
	}
}
