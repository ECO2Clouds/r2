package eu.eco2clouds.scheduler.accounting.client;

import static eu.eco2clouds.scheduler.SchedulerDictionary.XMLNS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.net.httpserver.Headers;

import eu.eco2clouds.applicationProfile.datamodel.ExperimentDescriptor;
import eu.eco2clouds.scheduler.SchedulerDictionary;
import eu.eco2clouds.accounting.datamodel.parser.Co2;
import eu.eco2clouds.accounting.datamodel.parser.Experiment;
import eu.eco2clouds.accounting.datamodel.parser.ExperimentReport;
import eu.eco2clouds.accounting.datamodel.parser.Host;
import eu.eco2clouds.accounting.datamodel.parser.HostMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.VM;
import eu.eco2clouds.accounting.datamodel.parser.VMMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.VMReport;
import eu.eco2clouds.accounting.datamodel.xml.HostPool;
import eu.eco2clouds.accounting.datamodel.parser.Testbed;
import eu.eco2clouds.accounting.datamodel.parser.TestbedMonitoring;
import eu.eco2clouds.scheduler.bonfire.BFClientScheduler;
import eu.eco2clouds.scheduler.util.MockWebServer;
import eu.eco2clouds.scheduler.util.ReadFile;

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
public class AccountingClientHCTest {
	private static Logger logger = Logger.getLogger(AccountingClientHCTest.class);
	private MockWebServer mServer;
	private String mBaseURL = "http://localhost:";
	private String hostInfo;
	
	@Before
	public void before() throws IOException {
		mServer = new MockWebServer();
		mServer.start();
		mBaseURL = mBaseURL + mServer.getPort();
		logger.debug("Web server started: " + mBaseURL);
		hostInfo = ReadFile.readFile("src/test/resources/host.example");
	}
	
	@Test
	public void getVMUrlTest() {
		AccountingClientHC client = new AccountingClientHC();
		
		VM vm = new VM();
		vm.setBonfireUrl("https://localhost:444/locations/fr-inria/computes/60147");
		
		String vmUrl = client.getVMUrl(vm);
		assertEquals("/locations/fr-inria/computes/60147", vmUrl);
		
		vm.setBonfireUrl("hxxps://localhost:444/locations/fr-inria/computes/60147");
		vmUrl = client.getVMUrl(vm);
		assertEquals("hxxps://localhost:444/locations/fr-inria/computes/60147", vmUrl);
	}
	
	@Test
	public void constructorTest() {
		AccountingClient client = new AccountingClientHC("url...");
		assertEquals("url...", client.getURL());
		client.setURL("url2");
		assertEquals("url2", client.getURL());
	}
	
	@Test
	public void getTestbedTest() {
		String testbedXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
							+ "<testbed href=\"/testbeds/uk-epcc\" xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\">"
								+ "<name>uk-epcc</name>"
								+ "<url>http://tubbs.epcc.ed.ac.uk/one-status.xml</url>"
								+ "<link rel=\"parent\" href=\"/\" type=\"application/vnd.eco2clouds+xml\"/>"
								+ "<link rel=\"status\" href=\"/locations/uk-epcc/status\" type=\"application/vnd.eco2clouds+xml\"/>"
							+ "</testbed>";
		
		mServer.addPath("/testbeds/uk-epcc", testbedXML);
		
		AccountingClient client = new AccountingClientHC(mBaseURL);
		Testbed testbed = client.getTestbed("uk-epcc");
		assertEquals("uk-epcc", testbed.getName());
		assertEquals("http://tubbs.epcc.ed.ac.uk/one-status.xml", testbed.getUrl());
	}
	
	@Test
	public void getHostsOfTesbedTest() {
		String hostsXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
						  + "<collection xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\">"
						  	+ "<items offset=\"0\" total=\"1\">"
						    + "<host>"
						  		+ "<id>15</id>"
						        + "<state>2</state>"
						  		+ "<name>tubbs0</name>"
						        + "<connected>true</connected>"
						  		+ "<link rel=\"parent\" href=\"/testbeds/uk-epcc/hosts\" type=\"application/vnd.eco2clouds+xml\"/>"
						     + "</host>"
						     + "</items>"
						     + "<link rel=\"parent\" href=\"/testbeds/uk-epcc\" type=\"application/vnd.eco2clouds+xml\"/>"
						   + "</collection>";
		
		mServer.addPath("/testbeds/uk-epcc/hosts", hostsXML);
		
		AccountingClient client = new AccountingClientHC(mBaseURL);
		List<Host> hosts = client.getHostsOfTesbed("uk-epcc");
		assertEquals(1, hosts.size());
		assertEquals(1, hosts.get(0).getLinks().size());
		assertEquals("tubbs0", hosts.get(0).getName());
	}
	
	@Test
	public void getHostOfTestbedTest() {
		String hostXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
						+ "<host xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\">"
							+ "<id>15</id>"
							+ "<state>2</state>"
							+ "<name>tubbs0</name>"
							+ "<connected>true</connected>"
							+ "<link rel=\"parent\" href=\"/testbeds/uk-epcc/hosts\" type=\"application/vnd.eco2clouds+xml\"/>"
						+ "</host>";
		
		mServer.addPath("/testbeds/uk-epcc/hosts/tubbs0", hostXML);
		
		AccountingClient client = new AccountingClientHC(mBaseURL);
		Host host = client.getHostOfTestbed("uk-epcc", "tubbs0");
		assertEquals(15, host.getId());
		assertEquals("tubbs0", host.getName());
	}
	
//	@Test
//	public void getHostStatusForAllTestbedsTest() {
//		String testbedsXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
//				+ "<collection xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/testbeds\">"
//					+ "<items offset=\"0\" total=\"2\">"
//						+ "<testbed href=\"/testbeds/fr-inria\">"
//							+ "<name>fr-inria</name>"
//							+ "<url>http://frontend.bonfire.grid5000.fr/one-status.xml</url>"
//							+ "<link rel=\"parent\" href=\"/testbeds\" type=\"application/vnd.eco2clouds+xml\"/>"
//							+ "<link rel=\"status\" href=\"/testbed/fr-inria/status\" type=\"application/eco2clouds+xml\"/>"
//						+ "</testbed>"
//						+ "<testbed href=\"/testbeds/uk-epcc\">"
//							+ "<name>uk-epcc</name>"
//							+ "<url>http://bonfire.epcc.ed.ac.uk/logs/one-status.xml</url>"
//							+ "<link rel=\"parent\" href=\"/testbeds\" type=\"application/vnd.eco2clouds+xml\"/>"
//							+ "<link rel=\"status\" href=\"/testbed/uk-epcc/status\" type=\"application/eco2clouds+xml\"/>"
//						+ "</testbed>"
//					+ "</items>"
//					+ "<link href=\"/\" rel=\"parent\" type=\"application/vnd.eco2clouds+xml\"/>"
//				+ "</collection>";
//		
//		// First query to know the number of testbeds
//		mServer.addPath("/testbeds", testbedsXML);
//		// Second query to know the HostPool info for fr-inria
//		mServer.addPath("/testbeds/fr-inria/status", hostInfo);
//		// Third query to know the HostPool info for uk-epcc
//		mServer.addPath("/testbeds/uk-epcc/status", hostInfo2);
//		
//		AccountingClient client = new AccountingClientHC(mBaseURL);
//		List<Testbed> testbeds = client.getHostStatusForAllTestbeds();
//		
//		assertEquals(2, testbeds.size());
//		assertEquals("fr-inria", testbeds.get(0).getName());
//		assertEquals(4, testbeds.get(0).getHosts().size());
//		assertEquals(448, testbeds.get(0).getHosts().get(0).getId());
//		assertEquals("uk-epcc", testbeds.get(1).getName());
//		assertEquals(2, testbeds.get(1).getHosts().size());
//		assertEquals(4428, testbeds.get(1).getHosts().get(0).getId());
//	}

	@Test
	public void testGetListTestbeds() {
		String testbedsXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
								+ "<collection xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/testbeds\">"
									+ "<items offset=\"0\" total=\"2\">"
										+ "<testbed href=\"/testbeds/fr-inria\">"
											+ "<name>fr-inria</name>"
											+ "<url>http://frontend.bonfire.grid5000.fr/one-status.xml</url>"
											+ "<link rel=\"parent\" href=\"/testbeds\" type=\"application/vnd.eco2clouds+xml\"/>"
											+ "<link rel=\"status\" href=\"/testbed/fr-inria/status\" type=\"application/eco2clouds+xml\"/>"
										+ "</testbed>"
										+ "<testbed href=\"/testbeds/uk-epcc\">"
											+ "<name>uk-epcc</name>"
											+ "<url>http://bonfire.epcc.ed.ac.uk/logs/one-status.xml</url>"
											+ "<link rel=\"parent\" href=\"/testbeds\" type=\"application/vnd.eco2clouds+xml\"/>"
											+ "<link rel=\"status\" href=\"/testbed/uk-epcc/status\" type=\"application/eco2clouds+xml\"/>"
										+ "</testbed>"
									+ "</items>"
									+ "<link href=\"/\" rel=\"parent\" type=\"application/vnd.eco2clouds+xml\"/>"
								+ "</collection>";
		
		mServer.addPath("/testbeds", testbedsXML);
		
		AccountingClient client = new AccountingClientHC(mBaseURL);
		List<Testbed> testbeds = client.getListOfTestbeds();
		
		assertEquals("fr-inria", testbeds.get(0).getName());
		assertEquals("http://frontend.bonfire.grid5000.fr/one-status.xml", testbeds.get(0).getUrl());
		assertEquals("uk-epcc", testbeds.get(1).getName());
		assertEquals("http://bonfire.epcc.ed.ac.uk/logs/one-status.xml", testbeds.get(1).getUrl());
	}
	
	@Test
	public void testGetListTestbedsEmpty() {
		String testbedsXML = "tion>";
		
		mServer.addPath("/testbeds", testbedsXML);
		
		AccountingClient client = new AccountingClientHC(mBaseURL);
		List<Testbed> testbeds = client.getListOfTestbeds();
		assertEquals(0,testbeds.size());
	}
	
	@Test
	public void testInvalidURLTestbeds() {
	
		AccountingClient client = new AccountingClientHC("http://xxxx");
		List<Testbed> testbeds = client.getListOfTestbeds();
		assertEquals(0,testbeds.size());
	}
	
	@Test
	public void getHostStatusForATestbedTest() {
		Testbed testbed = new Testbed();
		testbed.setName("fr-inria");
		
		mServer.addPath("/testbeds/fr-inria/status", hostInfo);
		AccountingClient client = new AccountingClientHC(mBaseURL);
		HostPool inriaHostPool = client.getHostStatusForATestbed(testbed);
		
		assertEquals(4, inriaHostPool.getHosts().size());
		assertEquals(448, inriaHostPool.getHosts().get(0).getId());
		assertEquals(2350, inriaHostPool.getHosts().get(0).getHostShare().getCpuUsage());
		assertEquals(450, inriaHostPool.getHosts().get(1).getId());
		assertEquals(40729600, inriaHostPool.getHosts().get(1).getHostShare().getFreeMem());
		assertEquals(451, inriaHostPool.getHosts().get(2).getId());
		assertEquals(15, inriaHostPool.getHosts().get(2).getHostShare().getRunningVms());
		assertEquals(552, inriaHostPool.getHosts().get(3).getId());
		assertEquals(2384, inriaHostPool.getHosts().get(3).getHostShare().getFreeCpu());
	}
	
	@Test
	public void testGetListExperiments() {
		String experimentsXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<collection xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/testbeds\">"
					+ "<items offset=\"0\" total=\"2\">"
						+ "<experiment href=\"/experiments/101\">"
							+ "<id>101</id>"
							+ "<bonfire-experiment-id>101</bonfire-experiment-id>"
							+ "<bonfire-user-id>A100</bonfire-user-id>"
							+ "<bonfire-group-id>ATOS</bonfire-group-id>"
							+ "<managed-experiment-id>55</managed-experiment-id>"
							+ "<start-time>15000</start-time>"
							+ "<end-time>18000</end-time>"
							+ "<link rel=\"parent\" href=\"/experiments\"/>"
							+ "<link rel=\"application-profile\" href=\"/experiments/101/application-profile\"/>"
							+ "<link rel=\"submitted-experiment-descriptor\" href=\"/experiments/101/submitted-experimen-descriptor\"/>"
							+ "<link rel=\"vms\" href=\"/experiments/1/vms\"/>"  
						+ "</experiment>"
						+ "<experiment href=\"/experiments/102\">"
							+ "<id>102</id>"
							+ "<bonfire-experiment-id>101</bonfire-experiment-id>"
							+ "<bonfire-user-id>A100</bonfire-user-id>"
							+ "<bonfire-group-id>ATOS</bonfire-group-id>"
							+ "<managed-experiment-id>55</managed-experiment-id>"
							+ "<start-time>15000</start-time>"
							+ "<end-time>18000</end-time>"
							+ "<link rel=\"parent\" href=\"/experiments\"/>"
							+ "<link rel=\"application-profile\" href=\"/experiments/101/application-profile\"/>"
							+ "<link rel=\"submitted-experiment-descriptor\" href=\"/experiments/101/submitted-experimen-descriptor\"/>"
							+ "<link rel=\"vms\" href=\"/experiments/1/vms\"/>"  
						+ "</experiment>"
					+ "</items>"
					+ "<link href=\"/\" rel=\"parent\" type=\"application/vnd.eco2clouds+xml\"/>"
				+ "</collection>";
		
		mServer.addPath("/experiments", experimentsXML);
		
		AccountingClient client = new AccountingClientHC(mBaseURL);
		List<Experiment> experiments = client.getListOfExperiments("user1", "group1");
		
		Headers headers = mServer.getHeaders();
		assertEquals("user1", headers.getFirst(SchedulerDictionary.X_BONFIRE_ASSERTED_ID));
		assertEquals("group1", headers.getFirst(SchedulerDictionary.X_BONFIRE_GROUPS_ID));
		assertEquals(SchedulerDictionary.ACCEPT, headers.getFirst("Accept"));
		assertEquals(SchedulerDictionary.USER_AGENT, headers.getFirst("User-Agent"));
		
		assertEquals(2, experiments.size());
		assertEquals(4, experiments.get(0).getLinks().size());
		assertEquals(102, experiments.get(1).getId().intValue());
	}
	
	@Test
	public void testInvalidURLExperiments() {
		AccountingClient client = new AccountingClientHC("http://xxxx");
		List<Experiment> experiments = client.getListOfExperiments("userId", "groupId");
		assertEquals(0, experiments.size());
	}
	
	@Test
	public void testGetListExperimentsEmpty() {
		String experimentsXML = "tion>";
		
		mServer.addPath("/experiments", experimentsXML);
		
		AccountingClient client = new AccountingClientHC(mBaseURL);
		List<Experiment> experiments = client.getListOfExperiments("userId", "groupId");
		assertEquals(0, experiments.size());
	}
	
	@Test
	public void testGetListExperiment() {
		String experimentXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
								+ "<experiment xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/experiments/101\">"
									+ "<id>101</id>"
									+ "<bonfire-experiment-id>101</bonfire-experiment-id>"
									+ "<bonfire-user-id>A100</bonfire-user-id>"
									+ "<bonfire-group-id>ATOS</bonfire-group-id>"
									+ "<managed-experiment-id>55</managed-experiment-id>"
									+ "<start-time>15000</start-time>"
									+ "<end-time>18000</end-time>"
									+ "<link rel=\"parent\" href=\"/experiments\"/>"
									+ "<link rel=\"application-profile\" href=\"/experiments/101/application-profile\"/>"
									+ "<link rel=\"submitted-experiment-descriptor\" href=\"/experiments/101/submitted-experimen-descriptor\"/>"
									+ "<link rel=\"vms\" href=\"/experiments/1/vms\"/>"  
								+ "</experiment>";
		
		Experiment experiment = new Experiment();
		experiment.setHref("/experiments/101");
		
		mServer.addPath("/experiments/101", experimentXML);
		
		AccountingClient client = new AccountingClientHC(mBaseURL);
		experiment = client.getExperiment(experiment, "user1", "group1");
		
		Headers headers = mServer.getHeaders();
		assertEquals("user1", headers.getFirst(SchedulerDictionary.X_BONFIRE_ASSERTED_ID));
		assertEquals("group1", headers.getFirst(SchedulerDictionary.X_BONFIRE_GROUPS_ID));
		assertEquals(SchedulerDictionary.ACCEPT, headers.getFirst("Accept"));
		assertEquals(SchedulerDictionary.USER_AGENT, headers.getFirst("User-Agent"));
		
		assertEquals(4, experiment.getLinks().size());
		assertEquals(101, experiment.getId().intValue());
	}
	
	
	@Test
	public void testGetListExperimentById() {
		String experimentXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
								+ "<experiment xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/experiments/101\">"
									+ "<id>101</id>"
									+ "<bonfire-experiment-id>101</bonfire-experiment-id>"
									+ "<bonfire-user-id>A100</bonfire-user-id>"
									+ "<bonfire-group-id>ATOS</bonfire-group-id>"
									+ "<managed-experiment-id>55</managed-experiment-id>"
									+ "<start-time>15000</start-time>"
									+ "<end-time>18000</end-time>"
									+ "<link rel=\"parent\" href=\"/experiments\"/>"
									+ "<link rel=\"application-profile\" href=\"/experiments/101/application-profile\"/>"
									+ "<link rel=\"submitted-experiment-descriptor\" href=\"/experiments/101/submitted-experimen-descriptor\"/>"
									+ "<link rel=\"vms\" href=\"/experiments/1/vms\"/>"  
								+ "</experiment>";
		
		mServer.addPath("/experiments/101", experimentXML);
		
		AccountingClient client = new AccountingClientHC(mBaseURL);
		Experiment experiment = client.getExperiment(101, "user1", "group1");
		
		Headers headers = mServer.getHeaders();
		assertEquals("user1", headers.getFirst(SchedulerDictionary.X_BONFIRE_ASSERTED_ID));
		assertEquals("group1", headers.getFirst(SchedulerDictionary.X_BONFIRE_GROUPS_ID));
		assertEquals(SchedulerDictionary.ACCEPT, headers.getFirst("Accept"));
		assertEquals(SchedulerDictionary.USER_AGENT, headers.getFirst("User-Agent"));
		
		assertEquals(4, experiment.getLinks().size());
		assertEquals(101, experiment.getId().intValue());
	}
	
	@Test
	public void testGetVMMonitoringInformation() {
		String vmMonitoringXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
								 "<vm_monitoring xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/locations/fr-inria/computes/57718/monitoring\">" +
								    "<cpuload>" +
								        "<value>0.2</value>" +
								        "<clock>1399450908</clock>" +
								        "<unity></unity>" +
								        "<name>cpuload</name>" +
								    "</cpuload>" +
								    "<cpuutil>" +
								        "<value>2.298851</value>" +
								        "<clock>1399450908</clock>" +
								        "<unity></unity>" +
								        "<name>cpuutil</name>" +
								    "</cpuutil>" +
								    "<link rel=\"vm\" href=\"/locations/fr-inria/computes/57718\"/>" +
								"</vm_monitoring>";
		mServer.addPath("/locations/fr-inria/computes/111/monitoring", vmMonitoringXML);

		AccountingClient client = new AccountingClientHC(mBaseURL);
		VM vm = new VM();
		vm.setBonfireUrl("/locations/fr-inria/computes/111");
		VMMonitoring vmMonitoring = client.getVMMonitoringStatus(vm);
		
		assertEquals("/locations/fr-inria/computes/57718/monitoring", vmMonitoring.getHref());
		assertEquals(0.2, vmMonitoring.getCpuload().getValue(), 0.001);
		assertEquals(1399450908, vmMonitoring.getCpuutil().getClock().longValue());
		assertEquals("/locations/fr-inria/computes/57718", vmMonitoring.getLinks().get(0).getHref());
		
	}
	
	@Test
	public void testGetExperimentReport() {
		String experimentReportXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
									 "<experiment xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/experiments/64763/report\">" +
										 "<power_consumption>" +
										 	"<value>8.482233807196968</value>" +
										 	"<clock>1401728597142</clock>" +
										 	"<unity>Wh</unity>" +
										 	"<name>Power consumed by the VM</name>" +
										 "</power_consumption>" +
										 "<co2_generated>" +
										 	"<value>0.2649107117862796</value>" +
										 	"<clock>1401728597142</clock>" +
										 	"<unity>g</unity>" +
										 	"<name>CO2 Generated by the VM</name>" +
										 "</co2_generated>" +
										 "<vm_report href=\"/locations/fr-inria/computes/58718\">" +
										 	"<power_consumption>" +
										 		"<value>2.0873164717666928</value>" +
										 		"<clock>1401728090</clock>" +
										 		"<unity>Wh</unity>" +
										 		"<name>Power consumed by the VM</name>" +
										 	"</power_consumption>" +
										 	"<co2_generated>" +
										 		"<value>0.06420958991484661</value>" +
										 		"<clock>1401728090</clock>" +
										 		"<unity>g</unity>" +
										 		"<name>CO2 Generated by the VM</name>" +
										 	"</co2_generated>" +
										 "</vm_report>" +
										 "<vm_report href=\"/locations/fr-inria/computes/58831\">" +
										 	"<power_consumption>" +
										 		"<value>0.0</value>" +
										 		"<clock>0</clock>" +
										 		"<unity>Wh</unity>" +
										 		"<name>Power consumed by the VM</name>" +
										 	"</power_consumption>" +
										 	"<co2_generated>" +
										 		"<value>0.0</value>" +
										 		"<clock>0</clock>" +
										 		"<unity>g</unity>" +
										 		"<name>CO2 Generated by the VM</name>" +
										 	"</co2_generated>" +
										 "</vm_report>" +
									 "</experiment>";
		mServer.addPath("/experiments/64763/report", experimentReportXML);

		AccountingClient client = new AccountingClientHC(mBaseURL);
		Experiment experiment = new Experiment();
		experiment.setId(64763);
		ExperimentReport experimentReport = client.getExperimentReport(experiment, "dperez", "eco2clouds");
		
		assertEquals(2, experimentReport.getVmReports().size());
	}
	
	@Test
	public void testGetVMReport() {
		String vmReportXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
								 "<vm_report xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/locations/fr-inria/computes/59038\">" + 
								 	"<power_consumption>" +
								 		"<value>0.11342466529749994</value>" +
								 		"<clock>1401395231</clock>" +
								 		"<unity>Wh</unity>" +
								 		"<name>Power consumed by the VM</name>" +
								 	"</power_consumption>" +
								 	"<co2_generated>" +
								 		"<value>0.002137732371202501</value>" +
								 		"<clock>1401395231</clock>" +
								 		"<unity>g</unity>" +
								 		"<name>CO2 Generated by the VM</name>" +
								 	"</co2_generated>" +
								 "</vm_report>";
		mServer.addPath("/locations/fr-inria/computes/59038/report", vmReportXML);

		AccountingClient client = new AccountingClientHC(mBaseURL);
		VM vm = new VM();
		vm.setBonfireUrl("/locations/fr-inria/computes/59038");
		VMReport vmReport = client.getVMReport(vm);
		
		assertEquals("/locations/fr-inria/computes/59038", vmReport.getHref());
		assertEquals(0.11342466529749994, vmReport.getPowerConsumption().getValue().doubleValue(), 0.001);
		assertEquals("CO2 Generated by the VM", vmReport.getCo2Generated().getName());
		
		mServer.addPath("/locations/fr-inria/computes/59033/report", "");
		vm.setBonfireUrl("/locations/fr-inria/computes/59033");
		vmReport = client.getVMReport(vm);
		
		assertEquals(null, vmReport.getPowerConsumption());
		assertEquals(null, vmReport.getCo2Generated());
	}
	
	@Test
	public void testGetTestbedMonitoringInformation() {
		String testbedMonitoringXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<testbed_monitoring xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/testbeds/fr-inria/monitoring\">"
				+ "<Other>"
					+ "<value>1984.0</value>"
					+ "<clock>1381512234</clock>"
					+ "<unity>MW</unity>"
					+ "<name>Other power</name>"
				+ "</Other>"
				+ "<Coal>"
					+ "<value>3949.0</value>"
					+ "<clock>1381512235</clock>"
					+ "<unity>MW</unity>"
					+ "<name>Coal power</name>"
				+ "</Coal>"
				+ "<Co2>"
					+ "<value>94.0</value>"
					+ "<clock>1381512236</clock>"
					+ "<unity>g\\/kWh</unity>"
					+ "<name>Co2 producted per kWh</name>"
				+ "</Co2>"
				+ "<Wind>"
					+ "<value>648.0</value>"
					+ "<clock>1381512237</clock>"
					+ "<unity>MW</unity>"
					+ "<name>Wind power</name>"
				+ "</Wind>"
				+ "<Oil>"
					+ "<value>0.0</value>"
					+ "<clock>1381512238</clock>"
					+ "<unity>MW</unity>"
					+ "<name>Oil power</name>"
				+ "</Oil>"
				+ "<Gaz>"
					+ "<value>2667.0</value>"
					+ "<clock>1381512239</clock>"
					+ "<unity>MW</unity>"
					+ "<name>Gaz power</name>"
				+ "</Gaz>"
				+ "<Hydraulic>"
					+ "<value>7317.0</value>"
					+ "<clock>1381512240</clock>"
					+ "<unity>MW</unity>"
					+ "<name>Hydraulic power</name>"
				+ "</Hydraulic>"
				+ "<Nuclear>"
					+ "<value>42343.0</value>"
					+ "<clock>1381512241</clock>"
					+ "<unity>MW</unity>"
					+ "<name>Nuclear power</name>"
				+ "</Nuclear>"
				+ "<Total>"
					+ "<value>58908.0</value>"
					+ "<clock>1381512242</clock>"
					+ "<unity>MW</unity>"
					+ "<name>Total power</name>"
				+ "</Total>"
				+ "<Cost>"
					+ "<value>0.33</value>"
					+ "<clock>1381512630</clock>"
					+ "<unity>euros/h</unity>"
					+ "<name>Cost</name>"
				+ "</Cost>"
				+ "<link rel=\"parent\" href=\"/testbeds/fr-inria\"/>"
			+ "</testbed_monitoring>";
		
		mServer.addPath("/testbeds/fr-inria/monitoring", testbedMonitoringXML);

		AccountingClient client = new AccountingClientHC(mBaseURL);
		Testbed inria = new Testbed();
		inria.setName("fr-inria");
		TestbedMonitoring testbedMonitoring = client.getTestbedMonitoringStatus(inria);
		
		assertEquals("/testbeds/fr-inria/monitoring", testbedMonitoring.getHref());
		assertEquals(0.33, testbedMonitoring.getCost().getValue().doubleValue(), 0.0001);
		assertEquals(1381512242, testbedMonitoring.getTotal().getClock().longValue());
		assertEquals(1381512241, testbedMonitoring.getNuclear().getClock().longValue());
		assertEquals(1381512240, testbedMonitoring.getHydraulic().getClock().longValue());
		assertEquals(1381512239, testbedMonitoring.getGaz().getClock().longValue());
		assertEquals(1381512238, testbedMonitoring.getOil().getClock().longValue());
		assertEquals(1381512237, testbedMonitoring.getWind().getClock().longValue());
		assertEquals(1381512236, testbedMonitoring.getCo2().getClock().longValue());
		assertEquals(1381512234, testbedMonitoring.getOther().getClock().longValue());
		assertEquals("/testbeds/fr-inria", testbedMonitoring.getLinks().get(0).getHref());
	}
	
	@Test
	public void testCalculateCo2PerHourForAHost() {
		String testbedMonitoringXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<testbed_monitoring xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/testbeds/uk-epcc/monitoring\">"
				+ "<Co2>"
					+ "<value>100.0</value>"
					+ "<clock>1381512236</clock>"
					+ "<unity>g\\/kWh</unity>"
					+ "<name>Co2 producted per kWh</name>"
				+ "</Co2>"
				+ "<link rel=\"parent\" href=\"/testbeds/uk-epcc\"/>"
			+ "</testbed_monitoring>";
		
		mServer.addPath("/testbeds/uk-epcc/monitoring", testbedMonitoringXML);

		AccountingClient client = new AccountingClientHC(mBaseURL);
		Testbed testbed = new Testbed();
		testbed.setName("uk-epcc");
		
		String hostMonitoringXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
					+ "<host_monitoring xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/testbeds/uk-epcc/hosts/crockett0/monitoring\">"
							+ "<co2_producted>"
								+ "<value>100.0</value>"
								+ "<clock>1381858501</clock>"
								+ "<unity>kW</unity>"
								+ "<name>Co2 producted</name>"
							+ "</co2_producted>"
							+ "<link rel=\"testbed\" href=\"/testbeds/uk-epcc\"/>"
					+ "</host_monitoring>";

		mServer.addPath("/testbeds/uk-epcc/hosts/crockett0/monitoring", hostMonitoringXML);
		
		String hostname = "crockett0";
		
		double co2PerHour = client.getCo2PerHourProducedByAHostAtThisMoment(testbed, hostname);
		
		assertEquals(10000.0, co2PerHour, 0.00001);
	}
	
	@Test
	public void getHostMonitoringInformation() throws Exception {
		 String hostMonitoringXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				 					+ "<host_monitoring xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/testbeds/uk-epcc/hosts/crockett0/monitoring\">"
				 							+ "<aggregate_energy>"
				 								+ "<value>102.0</value>"
				 								+ "<clock>1381858501</clock>"
				 								+ "<unity>kW</unity>"
				 								+ "<name>Aggregate energy</name>"
				 							+ "</aggregate_energy>"
				 							+ "<link rel=\"testbed\" href=\"/testbeds/uk-epcc\"/>"
				 					+ "</host_monitoring>";
		 
		 mServer.addPath("/testbeds/uk-epcc/hosts/crockett0/monitoring", hostMonitoringXML);

		 AccountingClient client = new AccountingClientHC(mBaseURL);
		 Testbed testbed = new Testbed();
		 testbed.setName("uk-epcc");
		 HostMonitoring hostMonitoring = client.getHostMonitoringStatus(testbed, "crockett0");

		 assertEquals("/testbeds/uk-epcc/hosts/crockett0/monitoring", hostMonitoring.getHref());
		 assertEquals("Aggregate energy", hostMonitoring.getAggregateEnergy().getName());
	}
	
	//@Test
	public void getSeveralHostMonitoringInformation() throws Exception {
		String collectionXML = "<collection xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\">"
								+ "<items offset=\"0\" total=\"2\">"
									+ "<host_monitoring href=\"/testbeds/uk-epcc/hosts/crockett0/monitoring\">"
										+ "<real_power>"
											+ "<value>102.0</value>"
											+ "<clock>1381841695</clock>"
											+ "<unity>kW</unity>"
											+ "<name>Real power</name>"
										+ "</real_power>"
										+ "<link rel=\"testbed\" href=\"/testbeds/uk-epcc\"/>"
									+ "</host_monitoring>"
									+ "<host_monitoring href=\"/testbeds/uk-epcc/hosts/crockett0/monitoring\">"
										+ "<running_vms>"
											+ "<value>102.0</value>"
											+ "<clock>1381841695</clock>"
											+ "<unity>Vm</unity>"
											+ "<name>Running VMs</name>"
										+ "</running_vms>"
										+ "<link rel=\"testbed\" href=\"/testbeds/uk-epcc\"/>"
									+ "</host_monitoring>"
								+ "</collection>";
		
		mServer.addPath("/testbeds/uk-epcc/hosts/crockett0/monitoring?starTime=111&endTime=222", collectionXML);

		AccountingClient client = new AccountingClientHC(mBaseURL);
		Testbed testbed = new Testbed();
		testbed.setName("uk-epcc");
		List<HostMonitoring> hostMonitorings = client.getHostMonitoringStatus(testbed, "crockett0", 111l, 222l);

		assertEquals(2, hostMonitorings.size());
	}
	
	@Test
	public void testInvalidURLExperiment() {
		AccountingClient client = new AccountingClientHC("http://xxxx");
		Experiment experiment = client.getExperiment(new Experiment(), "user1", "group1");
		
		assertEquals(null, experiment.getId());
	}
	
	@Test
	public void testGetListExperimentEmpty() {
		String experimentXML = "tion>";
		
		Experiment experiment = new Experiment();
		experiment.setHref("/experiments/101");
		
		mServer.addPath("/experiments/101", experimentXML);
		
		AccountingClient client = new AccountingClientHC(mBaseURL);
		experiment = client.getExperiment(experiment, "user1", "group1");
		assertEquals(null, experiment.getId());
	}
	
	@Test
	public void testPostExperiment() throws Exception {
		
		String returnedXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
							+ "<experiment xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/experiments/101\">"
								+ "<id>101</id>"
								+ "<bonfire-experiment-id>101</bonfire-experiment-id>"
								+ "<bonfire-user-id>A100</bonfire-user-id>"
								+ "<bonfire-group-id>ATOS</bonfire-group-id>"
								+ "<managed-experiment-id>55</managed-experiment-id>"
								+ "<start-time>15000</start-time>"
								+ "<end-time>18000</end-time>"
								+ "<link rel=\"parent\" href=\"/experiments\"/>"
								+ "<link rel=\"application-profile\" href=\"/experiments/101/application-profile\"/>"
								+ "<link rel=\"submitted-experiment-descriptor\" href=\"/experiments/101/submitted-experimen-descriptor\"/>"
								+ "<link rel=\"vms\" href=\"/experiments/1/vms\"/>"  
							+ "</experiment>";
		
		mServer.addPath("/experiments", returnedXML);
		
		Experiment experiment = new Experiment();
		experiment.setApplicationProfile("aaa");
		experiment.setBonfireGroupId("groupId");
		experiment.setBonfireUserId("userId");
		experiment.setEndTime(2l);
		experiment.setStartTime(1l);
		experiment.setManagedExperimentId(22l);
		experiment.setSubmittedExperimentDescriptor("...");
		
		AccountingClient client = new AccountingClientHC(mBaseURL);
		Experiment experimentFromDatabase = client.createExperiment(experiment);
		assertEquals(101, experimentFromDatabase.getId().intValue());
		assertEquals(101l, experimentFromDatabase.getBonfireExperimentId().longValue());
		assertEquals("A100", experimentFromDatabase.getBonfireUserId());
		
		// We verify the headers...
		Headers headers = mServer.getHeaders();
		assertEquals("userId", headers.getFirst(SchedulerDictionary.X_BONFIRE_ASSERTED_ID));
		assertEquals("groupId", headers.getFirst(SchedulerDictionary.X_BONFIRE_GROUPS_ID));
		assertEquals(SchedulerDictionary.ACCEPT, headers.getFirst("Accept"));
		assertEquals(SchedulerDictionary.USER_AGENT, headers.getFirst("User-Agent"));
		assertEquals(SchedulerDictionary.CONTENT_TYPE_ECO2CLOUDS_XML + "; charset=UTF-8", headers.getFirst("Content-Type"));
		
		// Now we verify that we sent the right message to the server
		String messageSentToAccountingService = mServer.getRequestBody();
		
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(messageSentToAccountingService));
		XPath xpath = XPath.newInstance("//bnf:experiment");
		xpath.addNamespace("bnf", XMLNS);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		
		XPath xpathName = XPath.newInstance("//bnf:bonfire-user-id");
		xpathName.addNamespace("bnf", XMLNS);
		List listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		Element name = (Element) listxpathName.get(0);
		assertEquals("userId", name.getValue());
				
		xpath = XPath.newInstance("//bnf:bonfire-group-id");
		xpath.addNamespace("bnf", XMLNS);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element description = (Element) listxpath.get(0);
		assertEquals("groupId", description.getValue());
		
		xpath = XPath.newInstance("//bnf:managed-experiment-id");
		xpath.addNamespace("bnf", XMLNS);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element status = (Element) listxpath.get(0);
		assertEquals("22", status.getValue());

		xpath = XPath.newInstance("//bnf:start-time");
		xpath.addNamespace("bnf", XMLNS);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element walltime = (Element) listxpath.get(0);
		assertEquals("1", walltime.getValue());
	}
	
	@Test
	public void testPutExperiment() throws Exception {
		
		String returnedXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
							+ "<experiment xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/experiments/101\">"
								+ "<id>101</id>"
								+ "<bonfire-experiment-id>101</bonfire-experiment-id>"
								+ "<bonfire-user-id>A100</bonfire-user-id>"
								+ "<bonfire-group-id>ATOS</bonfire-group-id>"
								+ "<managed-experiment-id>55</managed-experiment-id>"
								+ "<start-time>15000</start-time>"
								+ "<end-time>18000</end-time>"
								+ "<link rel=\"parent\" href=\"/experiments\"/>"
								+ "<link rel=\"application-profile\" href=\"/experiments/101/application-profile\"/>"
								+ "<link rel=\"submitted-experiment-descriptor\" href=\"/experiments/101/submitted-experimen-descriptor\"/>"
								+ "<link rel=\"vms\" href=\"/experiments/1/vms\"/>"  
							+ "</experiment>";
		
		mServer.addPath("/experiments/101", returnedXML);
		
		Experiment experiment = new Experiment();
		experiment.setApplicationProfile("aaa");
		experiment.setBonfireGroupId("groupId");
		experiment.setBonfireUserId("userId");
		experiment.setEndTime(2l);
		experiment.setStartTime(1l);
		experiment.setId(101);
		experiment.setManagedExperimentId(22l);
		experiment.setSubmittedExperimentDescriptor("...");
		
		AccountingClient client = new AccountingClientHC(mBaseURL);
		Experiment experimentFromDatabase = client.updateExperiment(experiment);
		assertEquals(101, experimentFromDatabase.getId().intValue());
		assertEquals(101l, experimentFromDatabase.getBonfireExperimentId().longValue());
		assertEquals("A100", experimentFromDatabase.getBonfireUserId());
		
		// We verify the headers...
		Headers headers = mServer.getHeaders();
		assertEquals("userId", headers.getFirst(SchedulerDictionary.X_BONFIRE_ASSERTED_ID));
		assertEquals("groupId", headers.getFirst(SchedulerDictionary.X_BONFIRE_GROUPS_ID));
		assertEquals(SchedulerDictionary.ACCEPT, headers.getFirst("Accept"));
		assertEquals(SchedulerDictionary.USER_AGENT, headers.getFirst("User-Agent"));
		assertEquals(SchedulerDictionary.CONTENT_TYPE_ECO2CLOUDS_XML + "; charset=UTF-8", headers.getFirst("Content-Type"));
		
		// Now we verify that we sent the right message to the server
		String messageSentToAccountingService = mServer.getRequestBody();
		
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(messageSentToAccountingService));
		XPath xpath = XPath.newInstance("//bnf:experiment");
		xpath.addNamespace("bnf", XMLNS);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		
		XPath xpathName = XPath.newInstance("//bnf:bonfire-user-id");
		xpathName.addNamespace("bnf", XMLNS);
		List listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		Element name = (Element) listxpathName.get(0);
		assertEquals("userId", name.getValue());
				
		xpath = XPath.newInstance("//bnf:bonfire-group-id");
		xpath.addNamespace("bnf", XMLNS);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element description = (Element) listxpath.get(0);
		assertEquals("groupId", description.getValue());
		
		xpath = XPath.newInstance("//bnf:managed-experiment-id");
		xpath.addNamespace("bnf", XMLNS);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element status = (Element) listxpath.get(0);
		assertEquals("22", status.getValue());

		xpath = XPath.newInstance("//bnf:start-time");
		xpath.addNamespace("bnf", XMLNS);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element walltime = (Element) listxpath.get(0);
		assertEquals("1", walltime.getValue());
	}
	
	//@Test
	public void getCo2ConsumptionTest() {
		String experimentXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
							   + "<experiment xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/experiments/101\">"
							   		+ "<id>101</id>"
							   		+ "<bonfire-experiment-id>101</bonfire-experiment-id>"
							   		+ "<bonfire-user-id>A100</bonfire-user-id>"
							   		+ "<bonfire-group-id>ATOS</bonfire-group-id>"
							   		+ "<managed-experiment-id>55</managed-experiment-id>"
							   		+ "<start-time>150000000</start-time>"
							   		+ "<end-time>180000000</end-time>"
							   		+ "<link rel=\"parent\" href=\"/experiments\"/>"
							   		+ "<link rel=\"application-profile\" href=\"/experiments/101/application-profile\"/>"
							   		+ "<link rel=\"submitted-experiment-descriptor\" href=\"/experiments/101/submitted-experiment-descriptor\"/>"
							   		+ "<link rel=\"vms\" href=\"/experiments/1/vms\"/>"  
							   + "</experiment>";

		mServer.addPath("/experiments/101", experimentXML);

		String submittedExperimentDescriptor = "{\"resources\":{"
												 + "\"name\": \"Test1\","
												 + "\"description\": \"test1\","
												 + "\"duration\": 10,"
												 + "\"resources\": ["
												 + "{"
												 		+ "\"compute\": {"
												 			 + "\"name\": \"Compute1\","
												 			 + "\"locations\": ["
												 			 		+ "\"uk-epcc\""
												 			 	+ "],"
												 			 + "\"instanceType\": \"lite\","
												 			 + "\"min\": 1,"
												 			 + "\"host\": \"crockett0\","
												 			 + "\"resources\": ["
												 			 	+ "{"
												 			 		+ "\"storage\": \"@BonFIRE Debian Squeeze v6\""
												 			 	+ "},"
												 			 	+ "{"
												 			 		+ "\"network\": \"@BonFIRE WAN\""
												 			 	+ "}"
												 			 + "],"
												 			 + "\"contexts\": []"
      													+ "}"
    											+ "}"
    											+ "]"
    										+ "}"
    										+ "}";
		
		mServer.addPath("/experiments/101/submitted-experiment-descriptor", submittedExperimentDescriptor);
		
		String co2CollectionXML = "<collection xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\">"
								+ "<items offset=\"0\" total=\"3\">"
									+ "<testbed_monitoring href=\"/testbeds/uk-epcc/monitoring/co2\">"
										+ "<Co2>"
											+ "<value>1984.0</value>"
											+ "<clock>1381512234</clock>"
											+ "<unity>MW</unity>"
											+ "<name>Co2 producted per kWh</name>"
										+ "</Co2>"
										+ "<link rel=\"testbed\" href=\"/testbeds/uk-epcc\"/>"
									+ "</testbed_monitoring>"
									+ "<testbed_monitoring href=\"/testbeds/uk-epcc/monitoring/co2\">"
										+ "<Co2>"
											+ "<value>1985.0</value>"
											+ "<clock>1381512235</clock>"
											+ "<unity>MW</unity>"
											+ "<name>Co2 producted per kWh</name>"
										+ "</Co2>"
										+ "<link rel=\"testbed\" href=\"/testbeds/uk-epcc\"/>"
									+ "</testbed_monitoring>"
									+ "<testbed_monitoring href=\"/testbeds/uk-epcc/monitoring/co2\">"
										+ "<Co2>"
											+ "<value>1987.0</value>"
											+ "<clock>1381512237</clock>"
											+ "<unity>MW</unity>"
											+ "<name>Co2 producted per kWh</name>"
										+ "</Co2>"
									+ "<link rel=\"testbed\" href=\"/testbeds/uk-epcc\"/>"
									+ "</testbed_monitoring>"
								+ "</items>"
							+ "</collection>";
		
		mServer.addPath("/testbeds/uk-epcc/monitoring/co2", co2CollectionXML);
		
		String collectionXML = "<collection xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\">"
								+ "<items offset=\"0\" total=\"10\">"
									+ "<host_monitoring href=\"/testbeds/uk-epcc/hosts/crockett0/monitoring\">"
										+ "<co2_producted>"
											+ "<value>102.0</value>"
											+ "<clock>1381841695</clock>"
											+ "<unity>kW</unity>"
											+ "<name>Co2 producted</name>"
										+ "</co2_producted>"
									+ "<link rel=\"testbed\" href=\"/testbeds/uk-epcc\"/>"
								+ "</host_monitoring>"
								+ "<host_monitoring href=\"/testbeds/uk-epcc/hosts/crockett0/monitoring\">"
									+ "<co2_producted>"
										+ "<value>102.0</value>"
										+ "<clock>1381841695</clock>"
										+ "<unity>kW</unity>"
										+ "<name>Co2 producted</name>"
									+ "</co2_producted>"
								+ "<link rel=\"testbed\" href=\"/testbeds/uk-epcc\"/>"
								+ "</host_monitoring>"
							+ "</items>"
							+ "</collection>";

		mServer.addPath("/testbeds/uk-epcc/hosts/crockett0/monitoring", collectionXML);
		
		AccountingClient client = new AccountingClientHC(mBaseURL);
		double consumedCo2 = client.getCo2Consumption(101);
		assertEquals(1687533.3333333335, consumedCo2, 0.0001);
	}
	
	@Test
	public void getCo2ForTestbedInterval() {
		String co2CollectionXML = "<collection xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\">"
									+ "<items offset=\"0\" total=\"3\">"
										+ "<testbed_monitoring href=\"/testbeds/uk-epcc/monitoring/co2\">"
											+ "<Co2>"
												+ "<value>1984.0</value>"
												+ "<clock>1381512234</clock>"
												+ "<unity>MW</unity>"
												+ "<name>Co2 producted per kWh</name>"
											+ "</Co2>"
											+ "<link rel=\"testbed\" href=\"/testbeds/uk-epcc\"/>"
										+ "</testbed_monitoring>"
										+ "<testbed_monitoring href=\"/testbeds/uk-epcc/monitoring/co2\">"
											+ "<Co2>"
												+ "<value>1985.0</value>"
												+ "<clock>1381512235</clock>"
												+ "<unity>MW</unity>"
												+ "<name>Co2 producted per kWh</name>"
											+ "</Co2>"
											+ "<link rel=\"testbed\" href=\"/testbeds/uk-epcc\"/>"
										+ "</testbed_monitoring>"
										+ "<testbed_monitoring href=\"/testbeds/uk-epcc/monitoring/co2\">"
											+ "<Co2>"
												+ "<value>1987.0</value>"
												+ "<clock>1381512237</clock>"
												+ "<unity>MW</unity>"
												+ "<name>Co2 producted per kWh</name>"
											+ "</Co2>"
											+ "<link rel=\"testbed\" href=\"/testbeds/uk-epcc\"/>"
										+ "</testbed_monitoring>"
									+ "</items>"
								+ "</collection>";	
		
		mServer.addPath("/testbeds/uk-epcc/monitoring/co2", co2CollectionXML);
				
		AccountingClient client = new AccountingClientHC(mBaseURL);
		
		Testbed testbed = new Testbed();
		testbed.setName("uk-epcc");
		
		List<Co2> co2s = client.getCo2OfTestbedForInterval(testbed, 10l, 20l);
		assertEquals(3, co2s.size());
		assertEquals(1381512234l, co2s.get(0).getClock().longValue());
	}
	
	@Test
	public void getSubmittedExperimentDescriptorTest() {
		String submittedExperimentDescriptor = "{\"resources\":{"
				 + "\"name\": \"Test1\","
				 + "\"description\": \"test1\","
				 + "\"duration\": 10,"
				 + "\"resources\": ["
				 + "{"
				 		+ "\"compute\": {"
				 			 + "\"name\": \"Compute1\","
				 			 + "\"locations\": ["
				 			 		+ "\"uk-epcc\""
				 			 	+ "],"
				 			 + "\"instanceType\": \"lite\","
				 			 + "\"min\": 1,"
				 			 + "\"host\": \"crockett0\","
				 			 + "\"resources\": ["
				 			 	+ "{"
				 			 		+ "\"storage\": \"@BonFIRE Debian Squeeze v6\""
				 			 	+ "},"
				 			 	+ "{"
				 			 		+ "\"network\": \"@BonFIRE WAN\""
				 			 	+ "}"
				 			 + "],"
				 			 + "\"contexts\": []"
							+ "}"
				+ "}"
				+ "]"
			+ "}"
		    + "}";
		
		Experiment experiment = new Experiment();
		experiment.setId(12);
		
		mServer.addPath("/experiments/12/submitted-experiment-descriptor", submittedExperimentDescriptor);
		
		AccountingClient client = new AccountingClientHC(mBaseURL);
		ExperimentDescriptor ed = client.getSubmittedExperimentDescriptor(experiment);
		
		assertEquals("Test1", ed.getName());
	}
	
	@Test
	public void setStatusExperimentTest() throws Exception {
		AccountingClientHC ac = new AccountingClientHC();
		BFClientScheduler bfClient = mock(BFClientScheduler.class);
		ac.bfClient = bfClient;
		
		Experiment experiment = new Experiment();
		long endTime = System.currentTimeMillis();
		endTime = endTime - 18000001l;
		experiment.setEndTime(endTime);
		
		ac.setStatusExperiment(experiment);
		assertEquals("terminated", experiment.getStatus());
		
		endTime = System.currentTimeMillis();
		experiment.setEndTime(endTime);
		
		ac.setStatusExperiment(experiment);
		assertEquals("stopped", experiment.getStatus());
		
		eu.eco2clouds.api.bonfire.occi.datamodel.Experiment bfExperiment = new eu.eco2clouds.api.bonfire.occi.datamodel.Experiment();
		bfExperiment.setStatus("other");
		when(bfClient.getExperiment("dperez", 1)).thenReturn(bfExperiment);
		
		experiment.setBonfireExperimentId(1l);
		experiment.setBonfireUserId("dperez");
		experiment.setEndTime(System.currentTimeMillis() + 10009999999l);
		
		ac.setStatusExperiment(experiment);
		assertEquals("other", experiment.getStatus());
		
		bfExperiment = null;
		when(bfClient.getExperiment("dperez", 1)).thenReturn(bfExperiment);
		
		ac.setStatusExperiment(experiment);
		assertEquals("unknown", experiment.getStatus());
	}
	
	@After
	public void after() {
		mServer.stop();
	}
}
