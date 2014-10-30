package eu.eco2clouds.scheduler.designtime;

import java.util.List;

import org.apache.log4j.Logger;

import eu.eco2clouds.accounting.datamodel.parser.Host;
import eu.eco2clouds.accounting.datamodel.parser.HostMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.Testbed;
import eu.eco2clouds.accounting.datamodel.xml.HostXml;
import eu.eco2clouds.scheduler.accounting.client.AccountingClient;
import eu.eco2clouds.scheduler.accounting.client.AccountingClientHC;
import eu.eco2clouds.scheduler.conf.Configuration;

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
 *
 * 
 * @author Adonis Adonis
 *
 */
public class TestbedUtilities
{
	private static Logger logger = Logger.getLogger(TestbedUtilities.class);
	
	public static Testbed findTestbedOfHost(String requiredHostName)
	{
		AccountingClient client = new AccountingClientHC(Configuration.accountingServiceUrl);
		for (Testbed testbed : client.getListOfTestbeds())
		{
			for (Host host : client.getHostsOfTesbed(testbed.getName()))
			{
				if (host.getName().equals(requiredHostName))
				{
					return testbed;
				}
			} // for
		} // for
		
		return null;
	} // findTestbedOfHost
	
	public static double getTotalCpu(Testbed testbed)
	{
		AccountingClientHC client = new AccountingClientHC(Configuration.accountingServiceUrl);
		
		List<HostXml> hosts = client.getHostStatusForATestbed(testbed).getHosts();
		
		double totalMaxCpu = 0;
		for (HostXml host : hosts)
		{
			if (isHostStateSuitable(host))
			{
				totalMaxCpu += host.getHostShare().getMaxCpu();
			} // if
		} // for
		
		return totalMaxCpu;
	} // getTotalCpu
	
	public static double getCpuAvailability(Testbed testbed)
	{
		AccountingClientHC client = new AccountingClientHC(Configuration.accountingServiceUrl);
		
		List<HostXml> hosts = client.getHostStatusForATestbed(testbed).getHosts();
		
		double totalCpuAvailability = 0, totalMaxCpu = 0, totalCpuUsage = 0;
		for (HostXml host : hosts)
		{
			if (isHostStateSuitable(host))
			{
				totalMaxCpu += host.getHostShare().getMaxCpu();
				totalCpuUsage += host.getHostShare().getCpuUsage();
			} // if
		} // for
		
		totalCpuAvailability = totalMaxCpu - totalCpuUsage;
		return totalCpuAvailability / totalMaxCpu * 100;
	} // getCpuAvailabilityForATestbed
	
	public static double getTotalMem(Testbed testbed)
	{
		AccountingClientHC client = new AccountingClientHC(Configuration.accountingServiceUrl);
		
		List<HostXml> hosts = client.getHostStatusForATestbed(testbed).getHosts();
		
		double totalMaxMem = 0;
		for (HostXml host : hosts)
		{
			if (isHostStateSuitable(host))
			{
				totalMaxMem += host.getHostShare().getMaxMem();
			} // if
		} // for
		
		return totalMaxMem;
	} // getTotalMem
	
	public static double getMemAvailability(Testbed testbed)
	{
		AccountingClientHC client = new AccountingClientHC(Configuration.accountingServiceUrl);
		
		List<HostXml> hosts = client.getHostStatusForATestbed(testbed).getHosts();
		
		double totalMemAvailability = 0, totalMaxMem = 0, totalMemUsage = 0;
		for (HostXml host : hosts)
		{
			if (isHostStateSuitable(host))
			{
				totalMaxMem += host.getHostShare().getMaxMem();
				totalMemUsage += host.getHostShare().getMemUsage();
			} // if
		} // for
		
		totalMemAvailability = totalMaxMem - totalMemUsage;
		return totalMemAvailability / totalMaxMem * 100;
	} // getMemAvailabilityForATestbed
	
	public static int getRunningVms(Testbed testbed)
	{
		AccountingClientHC client = new AccountingClientHC(Configuration.accountingServiceUrl);
		
		List<HostXml> hosts = client.getHostStatusForATestbed(testbed).getHosts();
		
		int totalRunningVms = 0;
		for (HostXml host : hosts)
		{
			if (isHostStateSuitable(host))
			{
				totalRunningVms += host.getHostShare().getRunningVms();
			} // if
		} // for
		
		return totalRunningVms;
	} // getMemAvailabilityForATestbed

	public static double getAllHostsPowerConsumptionofTestbed(Testbed testbed)
	{
		AccountingClientHC client = new AccountingClientHC(Configuration.accountingServiceUrl);
		
		double allHostsPowerConsumption = 0.0;
		
		List<Host> hosts = client.getHostsOfTesbed(testbed.getName());
		for (Host host : hosts)
		{
			if (isHostStateSuitable(host))
			{
				HostMonitoring hostMonitoring = client.getHostMonitoringStatus(testbed, host.getName());
			//Check to ignore hosts that do not provide power values
			
			//	logger.debug(" HostMonitoring Power: " + hostMonitoring.getPowerConsumption());
				if(hostMonitoring.getPowerConsumption() != null)
				{
			//		logger.debug(" It should have not been null: " + host.getName());
					allHostsPowerConsumption += hostMonitoring.getPowerConsumption().getValue();
				}
				
			}
		}
		
		return allHostsPowerConsumption;
	} // getAllHostsPowerConsumptionofTestbed
	
	public static boolean isHostStateSuitable (HostXml host)
	{
		if (host.getState() == 1 || host.getState() == 2)
		{
			return true;
		} // if
		
		return false;
	} // hostHasSuitableState
	
	public static boolean isHostStateSuitable(Host host)
	{
		if (host.getState() == 1 || host.getState() == 2)
		{
			return true;
		} // if
		
		return false;
	} // hostHasSuitableState
} // class