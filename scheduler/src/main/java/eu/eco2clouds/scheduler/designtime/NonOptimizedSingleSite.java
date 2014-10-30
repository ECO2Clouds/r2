package eu.eco2clouds.scheduler.designtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import eu.eco2clouds.accounting.datamodel.parser.Testbed;
import eu.eco2clouds.accounting.datamodel.xml.HostXml;
import eu.eco2clouds.scheduler.accounting.client.AccountingClient;

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
public class NonOptimizedSingleSite extends SingleObjectiveOptimization
{
	@Override
	public String findSuitableHost(List<HostXml> hosts, Testbed testbed,
			InstanceTypeRequirements instanceType, AccountingClient client, boolean isTesting, HashMap<String, Double> energyTestValues)
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
				energy[index] = client.getHostMonitoringStatus(testbed, host.getName()).getPowerConsumption().getValue();
			} // else
			
			index++;
		} // while
		
		// if only one suitable host found check its status first and then select it
		if (suitableHosts.size() == 1)
	    {
			return findBestHostBasedOnInstanceType(suitableHosts, hostNames, instanceType);
	    } // if
		
		hostNames = randomizeHostNames(hostNames);
		
		return findBestHostBasedOnInstanceType(suitableHosts, hostNames, instanceType);
	} // findSuitableHost

	private String[] randomizeHostNames(String[] hostNames)
	{
		ArrayList<String> hostNamesAsArrayList = new ArrayList<String>();
        for (int i = 0; i < hostNames.length; i++)
        {
            hostNamesAsArrayList.add(hostNames[i]);
        } // for

        String[] randomHostNames = new String[hostNames.length];
        for (int i = 0; i < randomHostNames.length; i++)
        {
                int randomIndex = (int) (Math.random() * hostNamesAsArrayList.size());
                randomHostNames[i] = hostNamesAsArrayList.remove(randomIndex);
        } // for

        return randomHostNames;
	} // randomizeHostNames
} // NonOptimizedSingleSite