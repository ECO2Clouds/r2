package eu.eco2clouds.accounting.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringReader;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.mockrunner.mock.jdbc.MockResultSet;

import eu.eco2clouds.accounting.datamodel.Testbed;
import eu.eco2clouds.accounting.datamodel.parser.Collection;
import eu.eco2clouds.accounting.datamodel.parser.HostMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.TestbedMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.VMMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.VMReport;
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
public class AccountingServiceTestbedTest {

	private static Logger logger = Logger
			.getLogger(AccountingServiceTestbedTest.class);
	
	@Test
	public void getListOfTestbedsTest() throws IOException {
		// We prepare the objects that we expect to be retrieved by the
		// database.
		Testbed testbed1 = new Testbed();
		testbed1.setId(1);
		testbed1.setName("testbed1");
		testbed1.setUrl("http://testbed1.com");
		Testbed testbed2 = new Testbed();
		testbed2.setId(2);
		testbed2.setName("testbed2");
		testbed2.setUrl("http://testbed2.com");

		List<Testbed> testbeds = new ArrayList<Testbed>();
		testbeds.add(testbed1);
		testbeds.add(testbed2);

		TestbedDAO testbedDAO = mock(TestbedDAO.class);
		when(testbedDAO.getAll()).thenReturn(testbeds); // When it is called it
														// also returns this
														// list

		// We create the service and we connect it to the mocked testbedDAO
		AccountingServiceTestbed service = new AccountingServiceTestbed(testbedDAO);

		String testbedsString = service.getListOfTesbeds();
		
		Collection collection = null;
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			collection = (Collection) jaxbUnmarshaller.unmarshal(new StringReader((String) testbedsString));
		} catch(JAXBException e) {
			System.out.println("Error trying to parse returned list of testbed: /testbeds Exception: " + e.getMessage());
		}
		
		assertEquals(2, collection.getItems().getTestbeds().size());
	}

	@Test
	public void getTestbedTest() throws IOException {
		Testbed testbed = new Testbed();
		testbed.setId(1);
		testbed.setName("uk-epcc");
		testbed.setUrl("https://bonfire.epcc.ed.ac.uk:8443");

		TestbedDAO testbedDAO = mock(TestbedDAO.class);
		when(testbedDAO.getByName("uk-epcc")).thenReturn(testbed);

		AccountingServiceTestbed service = new AccountingServiceTestbed(testbedDAO);

		String testbedXML = service.getTestbedByName("uk-epcc");
		eu.eco2clouds.accounting.datamodel.parser.Testbed testbedOut = null;
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(eu.eco2clouds.accounting.datamodel.parser.Testbed.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			testbedOut = (eu.eco2clouds.accounting.datamodel.parser.Testbed) jaxbUnmarshaller.unmarshal(new StringReader((String) testbedXML));
		} catch(JAXBException e) {
			System.out.println("Error trying to parse returned list of testbed: /testbeds Exception: " + e.getMessage());
		}
		
		assertEquals("uk-epcc", testbedOut.getName());
	}

	@Test
	public void getTestbedNull() {
		TestbedDAO testbedDAO = mock(TestbedDAO.class);
		when(testbedDAO.getByName("uk-epcc")).thenReturn(null);

		AccountingServiceTestbed service = new AccountingServiceTestbed(
				testbedDAO);

		assertEquals(null, service.getTestbedByName("uk-epcc"));
	}

	@Test
	public void getTestbedHostsStatusInfoTest() {
		Testbed testbed = new Testbed();
		testbed.setName("pepe");
		testbed.setUrl("http://localhost...");

		TestbedDAO testbedDAO = mock(TestbedDAO.class);
		when(testbedDAO.getByName(testbed.getName())).thenReturn(testbed);

		Client client = mock(Client.class);
		when(client.getHostInfo(testbed)).thenReturn("beatiful text...");

		AccountingServiceTestbed service = new AccountingServiceTestbed(testbedDAO, client);

		assertEquals("beatiful text...",service.getTestbedHostsStatusInfoService(testbed.getName(),null));
	}

	@Test
	public void getTestbedHostsStatusInfoTestTestbedNull() {
		TestbedDAO testbedDAO = mock(TestbedDAO.class);
		when(testbedDAO.getByName("uk-epcc")).thenReturn(null);

		AccountingServiceTestbed service = new AccountingServiceTestbed(
				testbedDAO);

		assertEquals(null,service.getTestbedHostsStatusInfoService("uk-epcc", null));
	}
	
	@Test
	public void vmMonitoringTest() throws Exception {
		MockResultSet result = new MockResultSet("myMock");
		result.addColumn("id_items");
		result.addColumn("name");
		result.addColumn("zabbix_itemid");
		result.addColumn("clock");
		result.addColumn("value");
		result.addColumn("unity");
		
		result.addRow(new Object[] {"4600", "cpuload", "1", "2", "3", "4" });
		result.addRow(new Object[] {"4606", "cpuutil", "5", "6", "7", "8" });
		result.addRow(new Object[] {"4612", "diskfree", "9", "10", "11", "12" });
		result.addRow(new Object[] {"4612", "disktotal", "13", "14", "15", "16" });
		result.addRow(new Object[] {"4612", "diskusage", "17", "18", "19", "20" });
		result.addRow(new Object[] {"4612", "iops", "21", "22", "23", "24" });
		result.addRow(new Object[] {"4612", "ioutil", "25", "26", "27", "28" });
		result.addRow(new Object[] {"4612", "memfree", "29", "30", "31", "32" });
		result.addRow(new Object[] {"4612", "memtotal", "33", "34", "35", "36" });
		result.addRow(new Object[] {"4612", "memused", "37", "38", "39", "40" });
		result.addRow(new Object[] {"4612", "netifin", "41", "42", "43", "44" });
		result.addRow(new Object[] {"4612", "netifout", "45", "46", "47", "48" });
		result.addRow(new Object[] {"4612", "power", "49", "50", "51", "52" });
		result.addRow(new Object[] {"4612", "procnum", "53", "54", "55", "56" });
		result.addRow(new Object[] {"4612", "swapfree", "57", "58", "60", "61" });
		result.addRow(new Object[] {"4612", "swaptotal", "62", "63", "64", "65" });	
		result.addRow(new Object[] {"4612", "applicationmetric_1", "66", "67", "68", "69" });	
		result.addRow(new Object[] {"4612", "applicationmetric_2", "71", "72", "73", "74" });	
		result.addRow(new Object[] {"4612", "applicationmetric_3", "81", "82", "83", "84" });
		result.addRow(new Object[] {"4612", "applicationmetric_4", "91", "92", "93", "94" });
		
		AccountingServiceTestbed service = new AccountingServiceTestbed(null);
		String output = service.printVMMonitoring((ResultSet) result, "/locations/fr-inria/computes/333");
		System.out.println(output);
		
		JAXBContext jaxbContext = JAXBContext.newInstance(VMMonitoring.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		VMMonitoring vmMMonitoring = (VMMonitoring) jaxbUnmarshaller.unmarshal(new StringReader(output));
		
		assertEquals("/locations/fr-inria/computes/333/monitoring", vmMMonitoring.getHref());
		
		assertEquals("applicationmetric_1", vmMMonitoring.getApplicationMetric1().getName());
		assertEquals("68.0", vmMMonitoring.getApplicationMetric1().getValue().toString());
		assertEquals("67", vmMMonitoring.getApplicationMetric1().getClock().toString());
		assertEquals("69", vmMMonitoring.getApplicationMetric1().getUnity());
		
		assertEquals("applicationmetric_2", vmMMonitoring.getApplicationMetric2().getName());
		assertEquals("73.0", vmMMonitoring.getApplicationMetric2().getValue().toString());
		assertEquals("72", vmMMonitoring.getApplicationMetric2().getClock().toString());
		assertEquals("74", vmMMonitoring.getApplicationMetric2().getUnity());
		
		assertEquals("applicationmetric_3", vmMMonitoring.getApplicationMetric3().getName());
		assertEquals("83.0", vmMMonitoring.getApplicationMetric3().getValue().toString());
		assertEquals("82", vmMMonitoring.getApplicationMetric3().getClock().toString());
		assertEquals("84", vmMMonitoring.getApplicationMetric3().getUnity());
		
		assertEquals("applicationmetric_4", vmMMonitoring.getApplicationMetric4().getName());
		assertEquals("93.0", vmMMonitoring.getApplicationMetric4().getValue().toString());
		assertEquals("92", vmMMonitoring.getApplicationMetric4().getClock().toString());
		assertEquals("94", vmMMonitoring.getApplicationMetric4().getUnity());
		
		assertEquals("cpuload", vmMMonitoring.getCpuload().getName());
		assertEquals("3.0", vmMMonitoring.getCpuload().getValue().toString());
		assertEquals("2", vmMMonitoring.getCpuload().getClock().toString());
		assertEquals("4", vmMMonitoring.getCpuload().getUnity());
		
		assertEquals("cpuutil", vmMMonitoring.getCpuutil().getName());
		assertEquals("7.0", vmMMonitoring.getCpuutil().getValue().toString());
		assertEquals("6", vmMMonitoring.getCpuutil().getClock().toString());
		assertEquals("8", vmMMonitoring.getCpuutil().getUnity());
		
		assertEquals("diskfree", vmMMonitoring.getDiskfree().getName());
		assertEquals("11.0", vmMMonitoring.getDiskfree().getValue().toString());
		assertEquals("10", vmMMonitoring.getDiskfree().getClock().toString());
		assertEquals("12", vmMMonitoring.getDiskfree().getUnity());
		
		assertEquals("disktotal", vmMMonitoring.getDisktotal().getName());
		assertEquals("15.0", vmMMonitoring.getDisktotal().getValue().toString());
		assertEquals("14", vmMMonitoring.getDisktotal().getClock().toString());
		assertEquals("16", vmMMonitoring.getDisktotal().getUnity());
		
		assertEquals("diskusage", vmMMonitoring.getDiskusage().getName());
		assertEquals("19.0", vmMMonitoring.getDiskusage().getValue().toString());
		assertEquals("18", vmMMonitoring.getDiskusage().getClock().toString());
		assertEquals("20", vmMMonitoring.getDiskusage().getUnity());
		
		assertEquals("iops", vmMMonitoring.getIops().getName());
		assertEquals("23.0", vmMMonitoring.getIops().getValue().toString());
		assertEquals("22", vmMMonitoring.getIops().getClock().toString());
		assertEquals("24", vmMMonitoring.getIops().getUnity());
		
		assertEquals("ioutil", vmMMonitoring.getIoutil().getName());
		assertEquals("27.0", vmMMonitoring.getIoutil().getValue().toString());
		assertEquals("26", vmMMonitoring.getIoutil().getClock().toString());
		assertEquals("28", vmMMonitoring.getIoutil().getUnity());
		
		assertEquals("memfree", vmMMonitoring.getMemfree().getName());
		assertEquals("31.0", vmMMonitoring.getMemfree().getValue().toString());
		assertEquals("30", vmMMonitoring.getMemfree().getClock().toString());
		assertEquals("32", vmMMonitoring.getMemfree().getUnity());
		
		assertEquals("memtotal", vmMMonitoring.getMemtotal().getName());
		assertEquals("35.0", vmMMonitoring.getMemtotal().getValue().toString());
		assertEquals("34", vmMMonitoring.getMemtotal().getClock().toString());
		assertEquals("36", vmMMonitoring.getMemtotal().getUnity());
		
		assertEquals("memused", vmMMonitoring.getMemused().getName());
		assertEquals("39.0", vmMMonitoring.getMemused().getValue().toString());
		assertEquals("38", vmMMonitoring.getMemused().getClock().toString());
		assertEquals("40", vmMMonitoring.getMemused().getUnity());
		
		assertEquals("netifin", vmMMonitoring.getNetifin().getName());
		assertEquals("43.0", vmMMonitoring.getNetifin().getValue().toString());
		assertEquals("42", vmMMonitoring.getNetifin().getClock().toString());
		assertEquals("44", vmMMonitoring.getNetifin().getUnity());
		
		assertEquals("netifout", vmMMonitoring.getNetifout().getName());
		assertEquals("47.0", vmMMonitoring.getNetifout().getValue().toString());
		assertEquals("46", vmMMonitoring.getNetifout().getClock().toString());
		assertEquals("48", vmMMonitoring.getNetifout().getUnity());
		
		assertEquals("power", vmMMonitoring.getPower().getName());
		assertEquals("51.0", vmMMonitoring.getPower().getValue().toString());
		assertEquals("50", vmMMonitoring.getPower().getClock().toString());
		assertEquals("52", vmMMonitoring.getPower().getUnity());
		
		assertEquals("procnum", vmMMonitoring.getProcNum().getName());
		assertEquals("55.0", vmMMonitoring.getProcNum().getValue().toString());
		assertEquals("54", vmMMonitoring.getProcNum().getClock().toString());
		assertEquals("56", vmMMonitoring.getProcNum().getUnity());
		
		assertEquals("swapfree", vmMMonitoring.getSwapfree().getName());
		assertEquals("60.0", vmMMonitoring.getSwapfree().getValue().toString());
		assertEquals("58", vmMMonitoring.getSwapfree().getClock().toString());
		assertEquals("61", vmMMonitoring.getSwapfree().getUnity());
		
		assertEquals("swaptotal", vmMMonitoring.getSwaptotal().getName());
		assertEquals("64.0", vmMMonitoring.getSwaptotal().getValue().toString());
		assertEquals("63", vmMMonitoring.getSwaptotal().getClock().toString());
		assertEquals("65", vmMMonitoring.getSwaptotal().getUnity());
	}

	@Test
	public void testbedMonitoringTest() throws Exception {

		MockResultSet result = new MockResultSet("myMock");
		result.addColumn("id_items");
		result.addColumn("name");
		result.addColumn("zabbix_itemid");
		result.addColumn("clock");
		result.addColumn("value");
		result.addColumn("unity");

		result.addRow(new Object[] {"4600", "Hydraulic", "103440", "1392975360", "13508", "MW" });
		result.addRow(new Object[] {"4606", "Other", "103434", "1392975354", "0", "MW" });
		result.addRow(new Object[] {"4612", "Wind", "103442", "1392975362", "3195", "MW" });

		AccountingServiceTestbed service = new AccountingServiceTestbed(null);
		String output = service.printTestbedMonitoring((ResultSet) result, "fr-inria");
		System.out.println(output);
		
		JAXBContext jaxbContext = JAXBContext.newInstance(TestbedMonitoring.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		TestbedMonitoring testbedMonitoring = (TestbedMonitoring) jaxbUnmarshaller.unmarshal(new StringReader(output));
		
		assertEquals("/testbeds/fr-inria/monitoring", testbedMonitoring.getHref());
		
		assertEquals("Hydraulic", testbedMonitoring.getHydraulic().getName().toString());
		assertEquals("MW", testbedMonitoring.getHydraulic().getUnity().toString());
		assertEquals("13508.0", testbedMonitoring.getHydraulic().getValue().toString());
		assertEquals("1392975360", testbedMonitoring.getHydraulic().getClock().toString());
		
		assertEquals("Other", testbedMonitoring.getOther().getName().toString());
		assertEquals("MW", testbedMonitoring.getOther().getUnity().toString());
		assertEquals("0.0", testbedMonitoring.getOther().getValue().toString());
		assertEquals("1392975354", testbedMonitoring.getOther().getClock().toString());
		
		assertEquals("Wind", testbedMonitoring.getWind().getName().toString());
		assertEquals("MW", testbedMonitoring.getWind().getUnity().toString());
		assertEquals("3195.0", testbedMonitoring.getWind().getValue().toString());
		assertEquals("1392975362", testbedMonitoring.getWind().getClock().toString());
		
		
		assertEquals("/testbeds/fr-inria", testbedMonitoring.getLinks().get(0).getHref());
	}

	@Test
	public void printHostMonitoringTestWithTestBed() throws Exception {

		MockResultSet result = new MockResultSet("myMock");
		result.addColumn("id_items");
		result.addColumn("name");
		result.addColumn("zabbix_itemid");
		result.addColumn("clock");
		result.addColumn("value");
		result.addColumn("unity");

		result.addRow(new Object[] {"19577052", "Availability", "106950", "1404400473", "1", "" });
		result.addRow(new Object[] {"19618358", "co2g", "106506", "1404846486", "0.021377", "g" });
		result.addRow(new Object[] {"19618360", "consva", "105795", "1404846495", "144.569632", "VA" });
		result.addRow(new Object[] {"19618361", "consw", "105796", "1404846496", "140.84909" , "W" });
		result.addRow(new Object[] {"19618359", "conswh", "105797", "1404846497", "0.531506", "Wh" });
		result.addRow(new Object[] {"19618352", "cpuload", "100896", "1404846481", "0.18", ""});
		result.addRow(new Object[] {"19618353", "cpuutil", "101015", "1404846481", "0.188009", "" });
		result.addRow(new Object[] {"19618365", "cpuUtilization", "107392", "1404846472", "4", "%" });
		result.addRow(new Object[] {"19618362", "freespacesrv", "100879", "1404846481", "217679134720", ""});
		result.addRow(new Object[] {"19618364", "IOPS", "107223", "1404846483", "6", "Ops/s"});
		result.addRow(new Object[] {"19618354", "memfree", "103227", "1404846481", "278568960", ""});
		result.addRow(new Object[] {"19618355", "memtotal", "103268", "1404846481", "963440640", ""});
		result.addRow(new Object[] {"19618366", "PowerConsumption", "107054", "1404846496", "139", "W" });
		result.addRow(new Object[] {"19618351", "procnum", "101008", "1404846481", "296", "" });
		result.addRow(new Object[] {"19618357", "runningvm", "100891", "404846481", "15", "vms" });
		result.addRow(new Object[] {"19618356", "swapfree", "100897", "1404846481", "1003270144", "B" });
		
		AccountingServiceTestbed service = new AccountingServiceTestbed(null);
		String output = service.printHostMonitoring((ResultSet) result, "fr-inria", "node-1.integration.bonfire.grid5000.fr");
		logger.debug(output);
		

		JAXBContext jaxbContext = JAXBContext.newInstance(HostMonitoring.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		HostMonitoring hostMonitoring = (HostMonitoring) jaxbUnmarshaller.unmarshal(new StringReader(output));
		// swapfree
		assertEquals(1003270144, hostMonitoring.getFreeSwapSpace().getValue().longValue());
		// runningvm
		assertEquals(15.0, hostMonitoring.getRunningVMs().getValue(), 0.0001);
		// procnum
		assertEquals(296, hostMonitoring.getProcNum().getValue(), 0.0001);
		// Power Comsumption
		assertEquals(139, hostMonitoring.getPowerConsumption().getValue(), 0.0001);
		// MemTotal
		assertEquals(963440640, hostMonitoring.getTotalMemory().getValue().longValue());
		// MemFree
		assertEquals(278568960, hostMonitoring.getFreeMemory().getValue().longValue());
		// IOPS
		assertEquals(6.0, hostMonitoring.getDiskIOPS().getValue(), 0.00001);
		// freespacesrv
		assertEquals(217679134720l, hostMonitoring.getFreeSpaceOnSrv().getValue().longValue());
		//cpuUtilization
		assertEquals(4.0, hostMonitoring.getCpuUtilization().getValue(), 0.001);
		//cpuutil
		assertEquals(0.188009, hostMonitoring.getSystemCpuUtil().getValue(), 0.001);
		//cpuload
		assertEquals(0.18, hostMonitoring.getSystemCPULoad().getValue(), 0.001);
		//conswh
		assertEquals(0.531506, hostMonitoring.getPowerCurrentwhReal().getValue(), 0.001);
		//consw
		assertEquals(140.84909, hostMonitoring.getPowerCurrentwAggregated().getValue(), 0.001);
		//consva
		assertEquals(144.569632, hostMonitoring.getPowerCurrentvaAggregated().getValue(), 0.001);
		//Availability
		assertEquals(1404400473, hostMonitoring.getAvailability().getClock().longValue());
		//co2g
		assertEquals(0.021377, hostMonitoring.getCo2Producted().getValue(), 0.001);
	}
	
	@Test
	public void printVMPowerReportTest() throws Exception {
		MockResultSet result = new MockResultSet("myMock");
		result.addColumn("name");
		result.addColumn("value");
		result.addColumn("clock");
		result.addColumn("unity");
	}
	
	@Test
	public void printHostMonitoringTest() throws Exception {

		MockResultSet result = new MockResultSet("myMock");
		result.addColumn("location");
		result.addColumn("name");
		result.addColumn("zabbix_itemid");
		result.addColumn("clock");
		result.addColumn("value");
		result.addColumn("unity");


		result.addRow(new Object[] {"1", "power", "22781", "36000000", "10" , "W"});
		result.addRow(new Object[] {"1", "power", "22781", "72000000", "20" , "W"});
		result.addRow(new Object[] {"1", "power", "22781", "108000000", "30" , "W"});
		result.addRow(new Object[] {"1", "power", "22781", "144000000", "40" , "W"});
		result.addRow(new Object[] {"1", "power", "22781", "180000000", "50" , "W"});
		
		MockResultSet co2Result = new MockResultSet("myMock");
		co2Result.addColumn("location");
		co2Result.addColumn("name");
		co2Result.addColumn("zabbix_itemid");
		co2Result.addColumn("clock");
		co2Result.addColumn("value");
		co2Result.addColumn("unity");

		co2Result.addRow(new Object[] {"1", "co2", "103436", "71000000", "10" , "g/kWh"});
		co2Result.addRow(new Object[] {"1", "co2", "103436", "105000000", "20" , "g/kWh"});
		co2Result.addRow(new Object[] {"1", "co2", "103436", "180000000", "30" , "g/kWh"});		

		AccountingServiceTestbed service = new AccountingServiceTestbed(null);
		String output = service.printVMPowerReport(result, "/locations/fr-inria/computes/111", co2Result);
		System.out.println(output);

		JAXBContext jaxbContext = JAXBContext.newInstance(VMReport.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		VMReport vmReport = (VMReport) jaxbUnmarshaller.unmarshal(new StringReader(output));

		assertEquals("/locations/fr-inria/computes/111", vmReport.getHref());
		assertEquals(1200.0, vmReport.getPowerConsumption().getValue().doubleValue(), 0.001);
		assertEquals("Power consumed by the VM", vmReport.getPowerConsumption().getName());
		assertEquals("Wh", vmReport.getPowerConsumption().getUnity());
		assertEquals(180000000, vmReport.getPowerConsumption().getClock().longValue());
		assertEquals(30.5, vmReport.getCo2Generated().getValue().doubleValue(), 0.0001);
		assertEquals("g", vmReport.getCo2Generated().getUnity());
		assertEquals(180000000, vmReport.getCo2Generated().getClock().longValue());
		assertEquals("CO2 Generated by the VM", vmReport.getCo2Generated().getName());
	}

	@Test
	public void printCollectionOfHostMonitoringTest() throws Exception {

		MockResultSet result = new MockResultSet("myMock");
		result.addColumn("C.location");
		result.addColumn("A.name");
		result.addColumn("A.value");
		result.addColumn("A.clock");
		result.addColumn("A.unity");

		result.addRow(new Object[] { "crockett0", "Co2", "1984.0", "1381512234", "g/h" });
		result.addRow(new Object[] { "crockett0", "Processor load", "1985.0","1381512235", "%" });
		result.addRow(new Object[] { "crockett0", "Free memory", "1987.0","1381512237", "MB" });

		AccountingServiceTestbed service = new AccountingServiceTestbed(null);
		String output = service.printCollectionOfHostMonitoring("uk-epcc", (ResultSet) result);
		System.out.println(output);

		JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Collection collection = (Collection) jaxbUnmarshaller.unmarshal(new StringReader(output));

		assertEquals(3, collection.getItems().getTotal());
		assertEquals(3, collection.getItems().getHostMonitorings().size());

		//assertEquals(1984.0, collection.getItems().getHostMonitorings().get(0).getCo2GenerationRate().getValue().doubleValue(), 0.0001);
		assertEquals(1985.0, collection.getItems().getHostMonitorings().get(1).getProcessorLoad().getValue().doubleValue(), 0.0001);
		assertEquals(1987.0, collection.getItems().getHostMonitorings().get(2).getFreeMemory().getValue().doubleValue(), 0.0001);

	}

	@Test
	public void printTestbedCo2Test() throws Exception {

		MockResultSet result = new MockResultSet("myMock");
		result.addColumn("C.location");
		result.addColumn("A.name");
		result.addColumn("A.value");
		result.addColumn("A.clock");
		result.addColumn("A.unity");

		result.addRow(new Object[] { "uk-epcc", "Co2","1984.0", "1381512234", "MW" });
		result.addRow(new Object[] { "uk-epcc", "Co2","1985.0", "1381512235", "MW" });
		result.addRow(new Object[] { "uk-epcc", "Co2","1987.0", "1381512237", "MW" });

		AccountingServiceTestbed service = new AccountingServiceTestbed(null);
		String output = service.printTestbedCo2((ResultSet) result);
		System.out.println(output);

		JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Collection collection = (Collection) jaxbUnmarshaller.unmarshal(new StringReader(output));

		assertEquals(3, collection.getItems().getTotal());
		assertEquals(3, collection.getItems().getTestbedMonitorings().size());
		assertEquals(1984.0,collection.getItems().getTestbedMonitorings().get(0).getCo2().getValue().doubleValue(), 0.0001);
		assertEquals(1985.0,collection.getItems().getTestbedMonitorings().get(1).getCo2().getValue().doubleValue(), 0.0001);
		assertEquals(1987.0,collection.getItems().getTestbedMonitorings().get(2).getCo2().getValue().doubleValue(), 0.0001);
	}
	
	@Test
	public void getListOfLocationsMappingTest() {
		List<String> hrefsComputes = new ArrayList<String>();
		Set<String> uniqueLocations = new HashSet<String>();
		
		hrefsComputes.add("/locations/uk-epcc/computes/11212");
		hrefsComputes.add("/locations/de-hlrs/computes/11111212");
		hrefsComputes.add("/locations/uk-epcc/computes/11312");
		hrefsComputes.add("/locations/uk-epcc/computes/11211212");
		
		AccountingServiceTestbed service = new AccountingServiceTestbed(null);
		Map<String,String> mapOfVMsAndLocations = service.getListOfLocationsMapping(hrefsComputes, uniqueLocations);
		
		assertEquals(2, uniqueLocations.size());
		assertTrue(uniqueLocations.contains("uk-epcc"));
		assertTrue(uniqueLocations.contains("de-hlrs"));
		
		assertEquals(4, mapOfVMsAndLocations.size());
		assertEquals("uk-epcc", mapOfVMsAndLocations.get("/locations/uk-epcc/computes/11212"));
		assertEquals("de-hlrs", mapOfVMsAndLocations.get("/locations/de-hlrs/computes/11111212"));
		assertEquals("uk-epcc", mapOfVMsAndLocations.get("/locations/uk-epcc/computes/11312"));
		assertEquals("uk-epcc", mapOfVMsAndLocations.get("/locations/uk-epcc/computes/11211212"));
	}
}
