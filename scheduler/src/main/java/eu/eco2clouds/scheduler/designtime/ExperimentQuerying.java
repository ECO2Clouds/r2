package eu.eco2clouds.scheduler.designtime;

import java.util.List;

import eu.eco2clouds.accounting.datamodel.parser.Co2;
import eu.eco2clouds.accounting.datamodel.parser.Experiment;
import eu.eco2clouds.accounting.datamodel.parser.HostMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.Testbed;
import eu.eco2clouds.accounting.datamodel.parser.TestbedMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.VM;
import eu.eco2clouds.accounting.datamodel.xml.HostShare;
import eu.eco2clouds.accounting.datamodel.xml.HostXml;
import eu.eco2clouds.scheduler.accounting.client.AccountingClientHC;
import eu.eco2clouds.scheduler.conf.Configuration;
import eu.eco2clouds.scheduler.em.EMClientHC;
import eu.eco2clouds.scheduler.em.datamodel.ManagedExperiment;

/**
 * 
 * Copyright 2014 University of Manchester 
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
public class ExperimentQuerying
{
	public static void zappa()
	{
//		EMClientHC emClient = new EMClientHC();
		AccountingClientHC accountingclient = new AccountingClientHC(Configuration.accountingServiceUrl);
		
//		List<ManagedExperiment> managedExperiments = emClient.listExperiments("dperez");
//		FileOutput.outputToFile("Managed");
//		for (ManagedExperiment managedExperiment : managedExperiments)
//		{
//			FileOutput.outputToFile(managedExperiment.getName() + " " + managedExperiment.getStatus() +  " " + managedExperiment.getDescription() +
//					" " + managedExperiment.getBonFIREExperimentId() + " " +
//					accountingclient.getCo2Consumption(managedExperiment.getBonFIREExperimentId()));
//		}
		
		List<Experiment> experiments = accountingclient.getListOfExperiments("uwajid", "eco2clouds");
		for (Experiment experiment : experiments)
		{
			if (experiment.getStatus().equals("running") && experiment.getBonfireExperimentId() != -1)
			{
				FileOutput.outputToFile("Bonfire ID: " + experiment.getBonfireExperimentId());
				FileOutput.outputToFile("CO2: " + accountingclient.getCo2Consumption(safeLongToInt(experiment.getBonfireExperimentId())));
				FileOutput.outputToFile("Status: " + experiment.getStatus());
				FileOutput.outputToFile("VMs: " + experiment.getvMs());
//				FileOutput.outputToFile("VMs from accounting: " + accountingclient.getListOfVMsOfExperiment(experiment.getBonfireExperimentId(),
//						experiment.getBonfireUserId(),
//						experiment.getBonfireGroupId()));
//				
//				for (VM vm : experiment.getvMs())
//				{
//					FileOutput.outputToFile(vm.getBonfireUrl() + " " + vm.getHost());
//				} // for
			} // if
		} // for
	} // zappa
	
	private static int safeLongToInt(long l)
	{
	    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE)
	    {
	        throw new IllegalArgumentException
	            (l + " cannot be cast to int without changing its value.");
	    } // if
	    
	    return (int) l;
	} // safeLongToInt
	
	public static void alpha()
	{
		AccountingClientHC accountingclient = new AccountingClientHC(Configuration.accountingServiceUrl);
		
		List<Testbed> testbeds = accountingclient.getListOfTestbeds();
		FileOutput.outputToFile(testbeds.size() +  "\n");
		for (Testbed testbed : testbeds)
		{
			if (testbed == null)
				throw new RuntimeException("Testbed Null");
			
			TestbedMonitoring testbedMonitoring = accountingclient.getTestbedMonitoringStatus(testbed);
			
			if (testbedMonitoring == null)
				throw new RuntimeException("Testbed Monitoring Null");
			
			FileOutput.outputToFile("Testbed Name: " + testbed.getName() + ""); 
			FileOutput.outputToFile("Co2: " + testbedMonitoring.getCo2() + "");
			FileOutput.outputToFile("Coal: " + testbedMonitoring.getCoal() + "");
			FileOutput.outputToFile("Cost: " + testbedMonitoring.getCost() + "");
			FileOutput.outputToFile("Gaz: " + testbedMonitoring.getGaz() + "");
			FileOutput.outputToFile("Hydraulic: " + testbedMonitoring.getHydraulic() + "");
			FileOutput.outputToFile("Nuclear: " + testbedMonitoring.getNuclear() + "");
			FileOutput.outputToFile("Oil: " + testbedMonitoring.getOil() + "");
			FileOutput.outputToFile("pDUFr: " + testbedMonitoring.getpDUFr() + "");
			FileOutput.outputToFile("Total: " + testbedMonitoring.getTotal() + "");
			FileOutput.outputToFile("Wind: " + testbedMonitoring.getWind() + "\n");
			
//			FileOutput.outputToFile("Co2");
//			for (Co2 co2 : accountingclient.getCo2OfTestbedForInterval(testbed, 0, System.currentTimeMillis()))
//			{
//				FileOutput.outputToFile("Co2 for interval from accounting client: " + co2.getValue());
//			}
			
			for (HostXml host : accountingclient.getHostStatusForATestbed(testbed).getHosts())
			{
				FileOutput.outputToFile("Host Name: " + host.getName() + " State: " + host.getState() + "");
				
				HostMonitoring hostMonitoring = accountingclient.getHostMonitoringStatus(testbed, host.getName());
				FileOutput.outputToFile("Aggregate Energy: " + hostMonitoring.getAggregateEnergy() + "");
				FileOutput.outputToFile("Aggregate Energy Usage: " + hostMonitoring.getAggregateEnergyUsage().getValue() + "");
				FileOutput.outputToFile("Apparent Power: " + hostMonitoring.getApparentPower() + "");
				FileOutput.outputToFile("Apparent Power Usage: " + hostMonitoring.getApparentPowerUsage() + "");
				FileOutput.outputToFile("Co2 Genration Rate: " + hostMonitoring.getCo2GenerationRate() + "");
				FileOutput.outputToFile("Co2 Produced: " + hostMonitoring.getCo2Producted() + "");
				FileOutput.outputToFile("Free Memory: " + hostMonitoring.getFreeMemory().getValue() + "");
				FileOutput.outputToFile("Number of Processes: " + hostMonitoring.getNumberOfProcesses().getValue() + "");
				FileOutput.outputToFile("Number of Running VMs: " + hostMonitoring.getNumberOfVMsRunning() + "");
				FileOutput.outputToFile("Processor Load: " + hostMonitoring.getProcessorLoad().getValue() + "");
				FileOutput.outputToFile("Real Power: " + hostMonitoring.getRealPower().getValue() + "");
				FileOutput.outputToFile("Real Power Usage: " + hostMonitoring.getRealPowerUsage() + "");
				FileOutput.outputToFile("Running VMs: " + hostMonitoring.getRunningVMs().getValue() + "");
				FileOutput.outputToFile("Total Memory: " + hostMonitoring.getTotalMemory().getValue() + "\n");
				
				HostShare hostShare = host.getHostShare();
				FileOutput.outputToFile("Cpu Usage: " + hostShare.getCpuUsage() + "");
				FileOutput.outputToFile("Disk Usage: " + hostShare.getDiskUsage() + "");
				FileOutput.outputToFile("Free Cpu: " + hostShare.getFreeCpu() + "");
				FileOutput.outputToFile("Free Disk: " + hostShare.getFreeDisk() + "");
				FileOutput.outputToFile("Free Mem: " + hostShare.getFreeMem() + "");
				FileOutput.outputToFile("Max Cpu: " + hostShare.getMaxCpu() + "");
				FileOutput.outputToFile("Max Disk: " + hostShare.getMaxDisk() + "");
				FileOutput.outputToFile("Max Mem: " + hostShare.getMaxMem() + "");
				FileOutput.outputToFile("Mem Usage: " + hostShare.getMemUsage() + "");
				FileOutput.outputToFile("Running VMs: " + hostShare.getRunningVms() + "");
				FileOutput.outputToFile("Used Cpu: " + hostShare.getUsedCpu() + "");
				FileOutput.outputToFile("Used Disk: " + hostShare.getUsedDisk() + "");
				FileOutput.outputToFile("Used Mem: " + hostShare.getUsedMem() + "\n");
			}
		}
	}
} // class