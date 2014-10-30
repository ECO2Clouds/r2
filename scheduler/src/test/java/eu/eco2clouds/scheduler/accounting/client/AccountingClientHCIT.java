package eu.eco2clouds.scheduler.accounting.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import eu.eco2clouds.accounting.datamodel.parser.Co2;
import eu.eco2clouds.accounting.datamodel.parser.Experiment;
import eu.eco2clouds.accounting.datamodel.parser.HostMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.VM;
import eu.eco2clouds.accounting.datamodel.parser.VMMonitoring;
import eu.eco2clouds.accounting.datamodel.xml.HostPool;
import eu.eco2clouds.accounting.datamodel.parser.Testbed;
import eu.eco2clouds.accounting.datamodel.parser.TestbedMonitoring;
import eu.eco2clouds.scheduler.conf.Configuration;

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
public class AccountingClientHCIT {

//	//@Test
//	public void getListTestbeds() {
//		AccountingClient client = new AccountingClientHC(Configuration.accountingServiceUrl);
//		
//		// Get a list of availables testbeds
//		List<Testbed> testbeds = client.getListOfTestbeds();
//		assertEquals(2, testbeds.size());
//		
//		HostPool hostPool = client.getHostStatusForATestbed(testbeds.get(0));
//		assertEquals(1, hostPool.getHosts().size());
//		
//		List<Testbed> allTestbeds = client.getHostStatusForAllTestbeds();
//		assertEquals(2, allTestbeds.size());
//		assertEquals(3, allTestbeds.get(1).getHosts().size());
//	}
	
	//@Test
	public void getTestbedMonitoring() {
		Testbed testbed = new Testbed();
		testbed.setName("fr-inria");
		
		AccountingClient client = new AccountingClientHC(Configuration.accountingServiceUrl);
		TestbedMonitoring testbedMonitoring = client.getTestbedMonitoringStatus(testbed);
		
		assertTrue(!(null == testbedMonitoring.getCo2()));
	}
	
	@Test
	public void getVMMonitoring() {
		AccountingClient client = new AccountingClientHC(Configuration.accountingServiceUrl);
		List<VM> vms = client.getListOfVMsOfExperiment(135, "uwajid", "eco2clouds");
		for (VM vm : vms) {
			VMMonitoring vmMonitoring = client.getVMMonitoringStatus(vm);

			System.out.println("VM Name: " + vm.getName() + ", VM Bonfire ID: " + vm.getBonfireId() + ", VM ID: " + vm.getId() + "\n");
			System.out.println("VM Host: " + vm.getHost() + "\n");
			System.out.println("VM Power: " + vmMonitoring.getPower() + "\n");
			System.out.println("VM CPU Util: " + vmMonitoring.getCpuutil() + "\n\n");
		}
	}
	
	//@Test
	public void getCo2PerHourProducedByAHostAtThisMoment() {
		Testbed testbed = new Testbed();
		testbed.setName("uk-epcc");
		AccountingClient client = new AccountingClientHC(Configuration.accountingServiceUrl);
		
		double co2PerHour = client.getCo2PerHourProducedByAHostAtThisMoment(testbed, "crockett0");
		
		System.out.println("##### Co2 per hour " + co2PerHour );
		assertTrue((co2PerHour != 0.0));
	}
	
	//@Test
	public void getCo2IntervalTestbed() {
		Testbed testbed = new Testbed();
		testbed.setName("uk-epcc");
		AccountingClient client = new AccountingClientHC(Configuration.accountingServiceUrl);
		
		List<Co2> co2s = client.getCo2OfTestbedForInterval(testbed, 1381841695l, 1381851747l);
		assertTrue(co2s.size() > 0);
	}
	
	//@Test
	public void getCollectionHostMonitoring() {
		Testbed testbed = new Testbed();
		testbed.setName("uk-epcc");
		
		AccountingClient client = new AccountingClientHC(Configuration.accountingServiceUrl);
		List<HostMonitoring> hostMonitorings= client.getHostMonitoringStatus(testbed, "crockett0", 1381841695l, 1381841747l);
		
		assertEquals("Total memory", hostMonitorings.get(0).getTotalMemory().getName());
	}
	
	//@Test
	public void listExperiments() {
		AccountingClient client = new AccountingClientHC("http://localhost:8080/e2c-accounting");
		
		List<Experiment> experiments = client.getListOfExperiments("userId1", "groupId1");
		assertEquals(12, experiments.size());
		
		Experiment experiment = client.getExperiment(17, "userId1", "groupId1");
		assertEquals("bbb", experiment.getApplicationProfile());
	}
	
	//@Test
	public void postPutExperiment() {
		AccountingClient client = new AccountingClientHC("http://localhost:8080/e2c-accounting");
		
		Experiment experiment = new Experiment();
		experiment.setApplicationProfile("aaa");
		experiment.setBonfireGroupId("groupId1");
		experiment.setBonfireUserId("userId1");
		experiment.setEndTime(2l);
		experiment.setStartTime(1l);
		experiment.setBonfireExperimentId(223l);
		experiment.setManagedExperimentId(22l);
		experiment.setSubmittedExperimentDescriptor("...");
		
		experiment = client.createExperiment(experiment);
		
		assertEquals("aaa", experiment.getApplicationProfile());
		
		experiment.setApplicationProfile("bbb");
		experiment.setManagedExperimentId(23l);
		
		experiment = client.updateExperiment(experiment);
		assertEquals("bbb", experiment.getApplicationProfile());
		assertEquals(23l, experiment.getManagedExperimentId().longValue());
	}
}
