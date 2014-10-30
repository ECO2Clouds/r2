package eu.eco2clouds.accounting.service.jpa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import eu.eco2clouds.accounting.datamodel.Action;
import eu.eco2clouds.accounting.datamodel.ActionType;
import eu.eco2clouds.accounting.datamodel.Experiment;
import eu.eco2clouds.accounting.datamodel.Host;
import eu.eco2clouds.accounting.datamodel.HostData;
import eu.eco2clouds.accounting.datamodel.Testbed;
import eu.eco2clouds.accounting.datamodel.VM;
import eu.eco2clouds.accounting.datamodel.VMHost;
import eu.eco2clouds.accounting.service.ActionDAO;
import eu.eco2clouds.accounting.service.ActionTypeDAO;
import eu.eco2clouds.accounting.service.ExperimentDAO;
import eu.eco2clouds.accounting.service.HostDAO;
import eu.eco2clouds.accounting.service.HostDataDAO;
import eu.eco2clouds.accounting.service.TestbedDAO;
import eu.eco2clouds.accounting.service.VMDAO;
import eu.eco2clouds.accounting.service.VMHostDAO;

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
public class MysqlJpaIT {
	
	public static void main(String args[]) throws InterruptedException {
		// Load Spring configuration
		ApplicationContext context = new ClassPathXmlApplicationContext("/scheduler-db-JPA-test-MySQLIT-context.xml");
		ExperimentDAO experimentDAO = (ExperimentDAO) context.getBean("ExperimentService");
		TestbedDAO testbedDAO = (TestbedDAO) context.getBean("TestbedService");
		HostDAO hostDAO = (HostDAO) context.getBean("HostService");
		VMDAO vMDAO = (VMDAO) context.getBean("VMDAOService");
		ActionTypeDAO actionTypeDAO = (ActionTypeDAO) context.getBean("ActionTypeService");
		ActionDAO actionDAO = (ActionDAO) context.getBean("ActionService");
		VMHostDAO vMHostDAO = (VMHostDAO) context.getBean("VMHostDAOService");
		HostDataDAO hostDataDAO = (HostDataDAO) context.getBean("HostDataService");
		
		int size = experimentDAO.getAll().size();
		
		// We create an experiment
		Experiment experiment = new Experiment();
		experiment.setBonfireExperimentId(2l);
		experiment.setBonfireGroupId("groups");
		experiment.setBonfireUserId("pepito");
		experiment.setSubmittedExperimentDescriptor("submitted");
		experiment.setApplicationProfile("applicationProfile");
		
		// We store the experiment
		boolean saved = experimentDAO.save(experiment);
		Experiment experimentFromDatabase = experimentDAO.getAll().get(size);
		
		if(saved == false) {
			System.out.println("IMPOSIBLE TO SAVE EXPERIMENT!!!!");
			Thread.sleep(600000l);
		}
		
		// We store a testbed
		Testbed testbed = new Testbed();
		testbed.setName("fr-inria");
		testbed.setUrl("https://...");
		
		saved = testbedDAO.save(testbed);
		
		if(saved == false) {
			System.out.println("IMPOSIBLE TO SAVE TESTBED!!!!");
			Thread.sleep(600000l);
		}
		
		int numberOfHosts = hostDAO.getAll().size();
		
		// We store a host
		Host host1 = new Host();
		host1.setConnected(true);
		host1.setName("host1");
		host1.setState(1);
		Host host2 = new Host();
		host2.setConnected(true);
		host2.setName("host2");
		host2.setState(1);
		List<Host> hosts = new ArrayList<Host>();
		hosts.add(host1);
		hosts.add(host2);
		
		Testbed testbedFromDatabase = testbedDAO.getByName("fr-inria");
		testbedFromDatabase.setHosts(hosts);
		
		boolean updated = testbedDAO.update(testbedFromDatabase);
		numberOfHosts = hostDAO.getAll().size() - numberOfHosts; 
				
		if(updated == false || numberOfHosts != 2) {
			System.out.println("IMPOSIBLE TO UPDATE TESTBED!!!!");
			Thread.sleep(600000l);
		}
		
		testbedFromDatabase = testbedDAO.getByName("fr-inria");
		host1 = hostDAO.getByName("host1");
		System.out.println("######## HOST 1 ID: " + host1.getId());
		
		testbedFromDatabase = testbedDAO.deleteHost(testbedFromDatabase, host1);
		numberOfHosts = numberOfHosts - hostDAO.getAll().size();
		if(numberOfHosts != 1) {
			System.out.println("IMPOSIBLE TO DELETE A HOST FROM A TESTBED!!!!");
			Thread.sleep(600000l);
		}
		
		int numberOfVMs = vMDAO.getAll().size();
		// We add VMs to the Experiment
		VM vM1 = new VM();
		vM1.setBonfireUrl("url...");
		VM vM2 = new VM();
		vM2.setBonfireUrl("url2...");
		Set<VM> vMs = new HashSet<VM>();
		vMs.add(vM1);
		vMs.add(vM2);
		experimentFromDatabase.setvMs(vMs);
		
		updated = experimentDAO.update(experimentFromDatabase);
		experimentFromDatabase = experimentDAO.getById(experimentFromDatabase.getId());
		numberOfVMs = vMDAO.getAll().size() - numberOfVMs;
		if(updated == false || numberOfVMs != 2) {
			System.out.println("IMPOSIBLE TO UPDATE TESTBED!!!!");
			Thread.sleep(600000l);
		}
		
		vM1 = vMDAO.getAll().get(0);
		experimentFromDatabase = experimentDAO.deleteVM(experimentFromDatabase, vM1);
		numberOfVMs = numberOfVMs - vMDAO.getAll().size();
		if(numberOfVMs != 1) {
			System.out.println("IMPOSIBLE TO DELETE A VM FROM A EXPERIMENT!!!!");
			Thread.sleep(600000l);
		}
		
		ActionType actionType = new ActionType();
		actionType.setName("action type 1");
		
		saved = actionTypeDAO.save(actionType);
		if(saved != true) {
			System.out.println("IMPOSIBLE TO STORE ACTION TYPE!!!!");
			Thread.sleep(600000l);
		}
		
		ActionType actionTypeFromDatabase = actionTypeDAO.getByName("action type 1");
		if(!actionTypeFromDatabase.getName().equals("action type 1")) {
			System.out.println("IMPOSIBLE TO STORE ACTION TYPE!!!!");
			Thread.sleep(600000l);
		}
		
		int numberOfActions = actionDAO.getAll().size();
		
		Action action1 = new Action();
		action1.setActionLog("log...");
		action1.setActionType(actionTypeFromDatabase);
		action1.setTimestamp(1l);
		
		Action action2 = new Action();
		action2.setActionLog("log2...");
		action2.setActionType(actionTypeFromDatabase);
		action2.setTimestamp(1l);
		
		List<Action> actions = new ArrayList<Action>();
		actions.add(action1);
		actions.add(action2);
		
		vM2 = vMDAO.getAll().get(0);
		vM2.setActions(actions);
		
		updated = vMDAO.update(vM2);
		numberOfActions = actionDAO.getAll().size() - numberOfActions;
		if(updated == false && numberOfActions == 2) {
			System.out.println("IMPOSIBLE TO UPDATE VM!!!!");
			Thread.sleep(600000l);
		}
		
		vM2 = vMDAO.getById(vM2.getId());
		action1 = actionDAO.getAll().get(0);
		
		vM2 = vMDAO.deleteAction(vM2, action1);
		numberOfActions = actionDAO.getAll().size();
		System.out.println("################### - " + numberOfActions);
		if(numberOfActions != 1) {
			System.out.println("IMPOSIBLE TO DELETE AN ACTION FROM A VM!!!!");
			Thread.sleep(600000l);
		}
		
		host1 = hostDAO.getAll().get(0);
		
		VMHost vMHost = new VMHost();
		vMHost.setHost(host1);
		vMHost.setTimestamp(2l);
		
		Set<VMHost> vMHosts = new HashSet<VMHost>();
		vMHosts.add(vMHost);
		
		// Not sure why I need to reload this... 
		vM2 = vMDAO.getById(vM2.getId());
		vM2.setvMhosts(vMHosts);
		updated = vMDAO.update(vM2);
		int numberOfVMHosts = vMHostDAO.getAll().size();
		if(numberOfVMHosts != 1 && updated == false) {
			System.out.println("IMPOSIBLE TO ADD AN VMHOST TO A VM!!!!");
			Thread.sleep(600000l);
		}
		
		vMHost = vMHostDAO.getAll().get(0);
		vM2 = vMDAO.getById(vM2.getId());
		vM2 = vMDAO.deleteVMHost(vM2, vMHost);
		numberOfVMHosts = vMHostDAO.getAll().size();
		if(numberOfVMHosts != 0) {
			System.out.println("IMPOSIBLE TO DELETE AN VMHOST FROM A VM!!!!");
			Thread.sleep(600000l);
		}
		
		HostData hostData1 = new HostData();
		hostData1.setCpuUsage(1);
		hostData1.setDiskUsage(2);
		hostData1.setFreeCpu(3);
		hostData1.setFreeDisk(4);
		hostData1.setFreeMen(5);
		hostData1.setId(6);
		hostData1.setMaxCpu(7);
		hostData1.setMaxDisk(8);
		hostData1.setMaxMem(9);
		hostData1.setRunningVms(10);
		hostData1.setUsedCpu(11);
		hostData1.setUsedDisk(12);
		hostData1.setUsedMem(13);
		
		HostData hostData2 = new HostData();
		hostData2.setCpuUsage(1);
		hostData2.setDiskUsage(2);
		hostData2.setFreeCpu(3);
		hostData2.setFreeDisk(4);
		hostData2.setFreeMen(5);
		hostData2.setId(6);
		hostData2.setMaxCpu(7);
		hostData2.setMaxDisk(8);
		hostData2.setMaxMem(9);
		hostData2.setRunningVms(10);
		hostData2.setUsedCpu(11);
		hostData2.setUsedDisk(12);
		hostData2.setUsedMem(13);
		
		Set<HostData> hostDatas = new HashSet<HostData>();
		hostDatas.add(hostData1);
		hostDatas.add(hostData2);
		
		action1 = actionDAO.getAll().get(0);
		host1 = hostDAO.getAll().get(0);
		
		action1.setHostDatas(hostDatas);
		hostData1.setHost(host1);
		hostData2.setHost(host1);
		updated = actionDAO.update(action1);
		
		int numberOfHostDatas = hostDataDAO.getAll().size();
		if(numberOfHostDatas != 2 && updated == false) {
			System.out.println("IMPOSIBLE TO ADD AN HOSTDATA TO A ACTION!!!!");
			Thread.sleep(600000l);
		}
		
		action1 = actionDAO.getAll().get(0);
		hostData1 = hostDataDAO.getAll().get(0);
		
		action1 = actionDAO.deleteHostData(action1, hostData1);
		numberOfHostDatas = hostDataDAO.getAll().size();
		if(numberOfHostDatas != 1) {
			System.out.println("IMPOSIBLE TO DELETE AN HOSTDATA FROM A ACTION!!!!");
			Thread.sleep(600000l);
		}
		
		Thread.sleep(30000l);
		// Cleaning
		boolean deleted = experimentDAO.delete(experimentFromDatabase);
		if(deleted == false) {
			System.out.println("IMPOSIBLE TO DELETE EXPERIMENT!!!!");
			Thread.sleep(600000l);
		}
		
		deleted = testbedDAO.delete(testbedFromDatabase);
		if(deleted == false) {
			System.out.println("IMPOSIBLE TO DELETE TESTBED!!!!");
			Thread.sleep(600000l);
		}
		
		deleted = actionTypeDAO.delete(actionTypeFromDatabase);
		if(deleted == false) {
			System.out.println("IMPOSIBLE TO DELETE ACTION TYPE!!!!");
			Thread.sleep(600000l);
		}
	}
	

}
