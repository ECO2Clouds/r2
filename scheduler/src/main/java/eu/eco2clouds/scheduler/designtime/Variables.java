package eu.eco2clouds.scheduler.designtime;

import java.util.List;

import eu.eco2clouds.accounting.datamodel.parser.Experiment;
import eu.eco2clouds.accounting.datamodel.parser.Host;
import eu.eco2clouds.accounting.datamodel.parser.HostMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.Testbed;
import eu.eco2clouds.accounting.datamodel.parser.TestbedMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.VM;
import eu.eco2clouds.accounting.datamodel.parser.VMMonitoring;
import eu.eco2clouds.scheduler.accounting.client.AccountingClientHC;

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
public class Variables
{
	public static void hostVariables(String location)
	{
		AccountingClientHC client = new AccountingClientHC();
		Testbed testbed = new Testbed();
		testbed.setName(location);
		
		TestbedMonitoring testbedMonitoring = client.getTestbedMonitoringStatus(testbed);
		
		double loadCapacitor =
				TestbedUtilities.getCpuAvailability(testbed) + TestbedUtilities.getMemAvailability(testbed) / 2.0;
		FileOutput.outputToFile("Testbed: " + testbed.getName() + "\n" +
				"Energy, " + TestbedUtilities.getAllHostsPowerConsumptionofTestbed(testbed) + "\n" +
				/*"Availability, " + testbedMonitoring.getAvailability() + "\n" +
				"Biomass, " + testbedMonitoring.getBiomass() + "\n" +
				"Ccgt, " + testbedMonitoring.getCcgt() + "\n" +
				"Center, " + testbedMonitoring.getCenter() + "\n" +*/
				"Co2, " + testbedMonitoring.getCo2().getValue() + "\n" +
				/*"Coal, " + testbedMonitoring.getCoal() + "\n" +
				"Cogeneration, " + testbedMonitoring.getCogeneration() + "\n" +
				"Cost, " + testbedMonitoring.getCost() + "\n" +
				"Exported, " + testbedMonitoring.getExported() + "\n" +
				"Fossil, " + testbedMonitoring.getFossil() + "\n" +
				"Gaz, " + testbedMonitoring.getGaz() + "\n" +
				"Geothermal, " + testbedMonitoring.getGeothermal() + "\n" +
				"Hydraulic, " + testbedMonitoring.getHydraulic() + "\n" +
				"Imported, " + testbedMonitoring.getImported() + "\n" +
				"NpsHydro, " + testbedMonitoring.getNpsHydro() + "\n" +
				"Nuclear, " + testbedMonitoring.getNuclear() + "\n" +
				"Ocgt, " + testbedMonitoring.getOcgt() + "\n" +
				"Oil, " + testbedMonitoring.getOil() + "\n" +
				"One Utilization Cpu, " + testbedMonitoring.getOneUtlizationCpu() + "\n" +
				"Other, " + testbedMonitoring.getOther() + "\n" +
				"DUFr, " + testbedMonitoring.getpDUFr() + "\n" +
				"Ps, " + testbedMonitoring.getPs() + "\n" +*/
				"Pue, " + testbedMonitoring.getPue().getValue() + "\n" +
				"Load Capacitor, " + loadCapacitor + "\n" +
				/*"Pumped Storage, " + testbedMonitoring.getPumpedStorage() + "\n" +
				"Site Utilization, " + testbedMonitoring.getSiteUtilization() + "\n" +
				"Solar, " + testbedMonitoring.getSolar() + "\n" +
				"Storage Utilization, " + testbedMonitoring.getStorageUtilization() + "\n" +
				"Total, " + testbedMonitoring.getTotal() + "\n" +
				"Total Green, " + testbedMonitoring.getTotalGreen() + "\n" +
				"Water, " + testbedMonitoring.getWater() + "\n" +
				"Wind, " + testbedMonitoring.getWind() +*/ "\n");
		
		/*List<Host> hosts = client.getHostsOfTesbed(location);
		double hostTotalPower = 0;
		for (Host host : hosts)
		{
			HostMonitoring hostMonitoring = client.getHostMonitoringStatus(testbed, host.getName());		
			FileOutput.outputToFile("Host, " + host.getName() + "\n" +
					/*"Aggregate Energy, " + hostMonitoring.getAggregateEnergy() + "\n" +
					"Aggregate energy Usage, " + hostMonitoring.getAggregateEnergyUsage() + "\n" +
					"Apparent Power, " + hostMonitoring.getApparentPower() + "\n" +
					"Apparent Power Usage, " + hostMonitoring.getApparentPowerUsage() + "\n" +
					"Availability, " + hostMonitoring.getAvailability() + "\n" +
					"Co2 Generation Per 30s, " + hostMonitoring.getCo2GenerationPer30s() + "\n" +
					"Co2 Generation Rate, " + hostMonitoring.getCo2GenerationRate() + "\n" +
					"Co2 Producted, " + hostMonitoring.getCo2Producted() + "\n" +
					"Co2 Raw, " + hostMonitoring.getCo2Raw() + "\n" +
					"Cpu User Time Avg 1, " + hostMonitoring.getCpuUserTimeAvg1() + "\n" +
					"Cpu Utilization, " + hostMonitoring.getCpuUtilization() + "\n" +
					"Custom Cpu Utilization, " + hostMonitoring.getCustomCpuUtilization() + "\n" +
					"Custom Vfs Iops, " + hostMonitoring.getCustomVfsIops() + "\n" +
					"Custom VMs Running, " + hostMonitoring.getCustomVMsRunning() + "\n" +
					"Disk IOPS, " + hostMonitoring.getDiskIOPS() + "\n" +
					"Disk Read Write, " + hostMonitoring.getDiskReadWrite() + "\n" +
					"Free Memory, " + hostMonitoring.getFreeMemory() + "\n" +
					"Free Space On Srv, " + hostMonitoring.getFreeSpaceOnSrv() + "\n" +
					"Free Swap Space, " + hostMonitoring.getFreeSwapSpace() + "\n" +
					"Host Availability, " + hostMonitoring.getHostAvailability() + "\n" +
					"Number of Processes, " + hostMonitoring.getNumberOfProcesses() + "\n" +
					"Number of VMs Running, " + hostMonitoring.getNumberOfVMsRunning() + "\n" +
					"One Availability, " + hostMonitoring.getOneAvailability() + "\n" +
					"Power Co2 Generation, " + hostMonitoring.getPowerCo2Generated() + "\n" +
					"Power Consumption, " + hostMonitoring.getPowerConsumption().getValue() + "\n" +
					/*"Power Current va Aggregated, " + hostMonitoring.getPowerCurrentvaAggregated() + "\n" +
					"Power Current w Aggregated, " + hostMonitoring.getPowerCurrentwAggregated() + "\n" +
					"Power Current Real, " + hostMonitoring.getPowerCurrentwhReal() + "\n" +
					"Power Total Aggregated, " + hostMonitoring.getPowerTotalAggregated() + "\n" +
					"Processor Load, " + hostMonitoring.getProcessorLoad() + "\n" +
					"Proc Num, " + hostMonitoring.getProcNum() + "\n" +
					"Real Power, " + hostMonitoring.getRealPower() + "\n" +
					"Real Power Usage, " + hostMonitoring.getRealPowerUsage() + "\n" +
					"Running VMs, " + hostMonitoring.getRunningVMs() + "\n" +
					"System CPU Load, " + hostMonitoring.getSystemCPULoad() + "\n" +
					"System Cpu Util, " + hostMonitoring.getSystemCpuUtil() + "\n" +
					"System CPU Util Perc, " + hostMonitoring.getSystemCPUUtilPerc() + "\n" +
					"System Power Consumption, " + hostMonitoring.getSystemPowerConsumption() + "\n" +
					"System Swap Size, " + hostMonitoring.getSystemSwapSize() + "\n" +
					"Total Memory, " + hostMonitoring.getTotalMemory() + "\n" +
					"vM Memory Size Free, " + hostMonitoring.getvMMemorySizeFree() + "\n" +
					"vM Memory Size Total, " + hostMonitoring.getvMMemorySizeTotal() + "\n");
			//hostTotalPower += hostMonitoring.getPowerConsumption().getValue();
		}
		
		//FileOutput.outputToFile("Testbed: " + testbed.getName() + ", Testbed Total Power: " + testbedMonitoring.getTotal().getValue() + ", Hosts Total Power: " + hostTotalPower);
		/*
		List<Experiment> experiments = client.getListOfExperiments("uwajid", "eco2clouds");
		for (int i = 0; i < experiments.size(); i++)
		{
			if (experiments.get(i).getId().toString().equals("140"))
			{
				try
				{
					FileOutput.outputToFile("SIZE: "+ client.getListOfVMsOfExperiment(experiments.get(i).getId(), "dperez", "eco2clouds").size() + "\n");
					List<VM> vms = client.getListOfVMsOfExperiment(experiments.get(i).getId(), "dperez", "eco2clouds");
					for (VM vm : vms)
					{
						VMMonitoring vmMonitoring = client.getVMMonitoringStatus(vm);
						
						FileOutput.outputToFile("VM Name: " + vm.getName() + ", VM Bonfire ID: " + vm.getBonfireId() + ", VM ID: " + vm.getId() + "\n");
						FileOutput.outputToFile("VM Host: " + vm.getHost() + "\n");
						FileOutput.outputToFile("VM Monitoring: " + vmMonitoring + "\n");
						FileOutput.outputToFile("VM Power: " + vmMonitoring.getPower() + "\n");
						FileOutput.outputToFile("VM CPU Util: " + vmMonitoring.getCpuutil() + "\n\n");
					}
				}
				
				catch (NullPointerException e)
				{
					FileOutput.outputToFile(e.getMessage() + "\n");
				}
			}
		} */
	}
}
