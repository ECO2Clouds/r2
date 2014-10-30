package eu.eco2clouds.scheduler.designtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import eu.eco2clouds.accounting.datamodel.parser.Host;
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
 */
public class BiObjectiveOptimizationDdm extends SingleObjectiveOptimization
{
	public String findHost(InstanceTypeRequirements instanceType)
	{
		AccountingClient client = new AccountingClientHC(Configuration.accountingServiceUrl);
		
		List<Testbed> testbeds = new ArrayList<Testbed>();
		if (instanceType.getUserSuggestedLocations().isEmpty())
		{
			testbeds = client.getListOfTestbeds();
		} // if
		
		else
		{
			for (String userSuggestedTestbeds : instanceType.getUserSuggestedLocations())
			{
				testbeds.add(client.getTestbed(userSuggestedTestbeds));
			} // for
		} // else
		
		testbeds = filterTestbedsAccordingToInstanceType(testbeds, instanceType);
		
		List<HostXml> hosts = new ArrayList<HostXml>();
		for (Testbed testbed : testbeds)
		{
			hosts.addAll(client.getHostStatusForATestbed(testbed).getHosts());
		} // for
		
		if (hosts == null || hosts.size() == 0)
		{
			return "NoSuitableHost";
		} // if
		
		return findSuitableHost(hosts, null, instanceType, client, false, null);
	} // findHost
	
	public static List<Testbed> filterTestbedsAccordingToInstanceType(
			List<Testbed> suitableTestbeds, InstanceTypeRequirements instanceType)
	{
		Iterator<String> notSuitableLocations = instanceType.getNotSuitableLocations().iterator();
		while (notSuitableLocations.hasNext())
		{
			String notSuitableLocation = notSuitableLocations.next();
			
			Iterator<Testbed> suitableTestbedsIterator = suitableTestbeds.iterator();
			while (suitableTestbedsIterator.hasNext())
			{
				Testbed candidateTestbedForRemoval = suitableTestbedsIterator.next();
				if (candidateTestbedForRemoval.getName().equals(notSuitableLocation))
				{
					suitableTestbeds.remove(candidateTestbedForRemoval);
				} // if
			} // while
		} // while
		
		return suitableTestbeds;
	} // filterTestbedsAccordingToInstanceType
	
	@Override
	public String findSuitableHost(List<HostXml> hosts, Testbed testbed, InstanceTypeRequirements instanceType,
			AccountingClient client, boolean isTesting,	HashMap<String, Double> energyTestValues)
	{
		// create hash map to store suitable hosts, hosts with state 1 or 2
		HashMap<String, HostXml> suitableHosts = findHostsWithSuitableState(hosts, testbed);
		Iterator<Map.Entry<String, HostXml>> suitableHostsIterator = suitableHosts.entrySet().iterator();
		
		// if there are no suitable hosts return noSuitableHost and exit
		if (suitableHosts.size() == 0)
		{
			return "NoSuitableHost";
		} // if
		
		// create arrays for data and populate them
		String[] hostNames = new String[suitableHosts.size()];
		double[] cpu = new double[suitableHosts.size()];		
		double[] mem = new double[suitableHosts.size()];
		double[] vm = new double[suitableHosts.size()];
		double[] energy = new double[suitableHosts.size()];
		
		int index = 0;
		while (suitableHostsIterator.hasNext())
		{
			HostXml host = suitableHostsIterator.next().getValue();
			hostNames[index] = host.getName();
			
			// Available CPU is Max CPU minus (-) CPU usage.
			cpu[index] = (host.getHostShare().getMaxCpu() - host.getHostShare().getCpuUsage()) / cpuConvertionUnit;
			
			// Available MEM is Max MEM minus (-) MEM usage.
			mem[index] = (host.getHostShare().getMaxMem() - host.getHostShare().getMemUsage()) / memConvertionUnit;
			
			vm[index] = host.getHostShare().getRunningVms();
			
			if (isTesting)
			{
				energy[index] = energyTestValues.get(hostNames[index]);
			} // if
			
			else
			{
				energy[index] = client.getHostMonitoringStatus(
						TestbedUtilities.findTestbedOfHost(host.getName()), host.getName()
						).getPowerConsumption().getValue();
			} // else
			
			index++;
		} // while
		
		// if only one suitable host found check its status first and then select it
		if (suitableHosts.size() == 1)
	    {
			return findBestHostBasedOnInstanceType(suitableHosts, hostNames, instanceType);
	    } // if
		
		//normalize data
		double[] normalizedCpu = normalize(cpu);
		double[] normalizedMem = normalize(mem);
		double[] normalizedVm = normalize(vm);
		double[] normalizedEng = normalize(energy);
		
		//inverse Available CPU weighting
		inverseWeighting(normalizedCpu);
		
		// get the sum of the weights
		double[] weightedSum = calculateWeightedAggregate(normalizedCpu, normalizedMem, normalizedVm, normalizedEng, suitableHosts.size());
		
		//WARNING: AFTER QUICKSORT ONLY THE ARRAYS "WEIGTHEDSUM" AND "HOSTNAMES" ARE UPDATED
		quickSort(weightedSum, hostNames, 0, hostNames.length - 1); // sort the suitable hosts from low weight to high
		
		return findBestHostBasedOnInstanceType(suitableHosts, hostNames, instanceType);
	} // findSuitableHost
} // class