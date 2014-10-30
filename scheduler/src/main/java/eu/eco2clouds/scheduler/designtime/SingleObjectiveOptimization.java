package eu.eco2clouds.scheduler.designtime;

import java.util.HashMap;
import java.util.List;

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
 * Abstract class that provides the internal logic for single objective optimization.
 * @author Adonis Adoni
 *
 */
public abstract class SingleObjectiveOptimization
{
	protected final static double cpuConvertionUnit = 100.0;
	protected final static double memConvertionUnit = 1024.0;
	
	public String findHost(InstanceTypeRequirements instanceType, String specifiedLocation)
	{
		AccountingClient client = new AccountingClientHC(Configuration.accountingServiceUrl);
		
		Testbed testbed = new Testbed();
		testbed.setName(specifiedLocation);
		
		List<HostXml> hosts = client.getHostStatusForATestbed(testbed).getHosts();
		
		if (hosts == null || hosts.size() == 0)
		{
			return "NoSuitableHost";
		} // if
		
		HashMap<String, HostXml> changesToHostsBeforeDeployment =
				HostsChangesBeforeDeployment.getCurrentInstance().getHostsChanges();
		
		for (HostXml host : hosts)
		{
			if (changesToHostsBeforeDeployment.get(host.getName()) != null)
			{
				HostXml updatesForCurrent = changesToHostsBeforeDeployment.get(host.getName());
				
				long newCpuUsage = host.getHostShare().getCpuUsage() + updatesForCurrent.getHostShare().getCpuUsage();
				int newMemUsage = host.getHostShare().getMemUsage() + updatesForCurrent.getHostShare().getMemUsage();
				
				host.getHostShare().setCpuUsage(newCpuUsage);
				host.getHostShare().setMemUsage(newMemUsage);
			} // if
		} // for
		return findSuitableHost(hosts, testbed, instanceType, client, false, null);
	} // findHost
	
	public abstract String findSuitableHost(List<HostXml> hosts, Testbed testbed, InstanceTypeRequirements instanceType,
			AccountingClient client, boolean isTesting, HashMap<String, Double> energyTestValues);
	
	public HashMap<String, HostXml> findHostsWithSuitableState(List<HostXml> hosts, Testbed testbed)
	{
		HashMap<String, HostXml> map = new HashMap<String, HostXml>();
		for (HostXml host : hosts)
		{
			//check whether suitable hosts have more than 10% of available resources - OpenNebula wont create VM if condition not met
			boolean reserveCheck = false; 
			boolean powerCheck = false;
			long maxMem = host.getHostShare().getMaxMem();			
			long availableMem = host.getHostShare().getMaxMem() - host.getHostShare().getMemUsage();
            availableMem = availableMem / 1024;
            
			long tenPercentOfMaxMem = (long) (maxMem*10/100);
			tenPercentOfMaxMem = tenPercentOfMaxMem / 1024;
			
			if(availableMem > tenPercentOfMaxMem)
			    reserveCheck = true;
			
			//Power Check to validate that host has power values available - otherwise host not suitable for deployment
			AccountingClient client = new AccountingClientHC(Configuration.accountingServiceUrl);
			if(client.getHostMonitoringStatus(testbed, host.getName()).getPowerConsumption() != null)
			powerCheck = true;
			//power check
			
			boolean statusCheck = false;			
			if (host.getState() == 1 || host.getState() == 2) 
				statusCheck = true;
			
			if	(statusCheck == true && reserveCheck == true && powerCheck == true)
			{
		//		FileOutput.outputToFile("#"+host.getName()+ "/n");
				map.put(host.getName(), host);
			} // if
		} // for
		
		return map;
	} // findSuitableHosts	
	
	public String findBestHostBasedOnInstanceType(HashMap<String, HostXml> suitableHosts, String[] hostNames,
			InstanceTypeRequirements instanceType)
	{
		for (int i = suitableHosts.size() - 1; i >= 0; i--)
		{
			// Available CPU and MEM are calculated by subtracting usage from max
			long availableCpu = suitableHosts.get(hostNames[i]).getHostShare().getMaxCpu() -
					suitableHosts.get(hostNames[i]).getHostShare().getCpuUsage();
			long availableMem = suitableHosts.get(hostNames[i]).getHostShare().getMaxMem() -
					suitableHosts.get(hostNames[i]).getHostShare().getMemUsage();
			
			//start- double reserve check - there should be more than 10% AvailableMem after deploying instance type
			boolean doubleReserveCheck = false;
			long maxMem = suitableHosts.get(hostNames[i]).getHostShare().getMaxMem();			
			long tenPercentOfMaxMem = (long) (maxMem*10/100);  
			tenPercentOfMaxMem = (long) (tenPercentOfMaxMem / memConvertionUnit); 
			long availableMemWithConversion = (long) (availableMem / memConvertionUnit);
			if (availableMemWithConversion - instanceType.getMemRequirement() > tenPercentOfMaxMem)
				doubleReserveCheck = true;				
			//end- double reserve check
			
			//enough resources check
			boolean enoughResourceCheck = false;
			if (availableCpu / cpuConvertionUnit >= instanceType.getCpuRequirement() &&
					(availableMem / memConvertionUnit) >= instanceType.getMemRequirement())
				enoughResourceCheck = true;
			
			if (doubleReserveCheck == true && enoughResourceCheck == true)
			{
				HostXml selectedHost = suitableHosts.get(hostNames[i]);
				
				long newCpuUsage = (long) (instanceType.getCpuRequirement() * cpuConvertionUnit);
				int newMemUsage = (int) (instanceType.getMemRequirement() * memConvertionUnit);
				
				HostsChangesBeforeDeployment.getCurrentInstance().addHostChange(selectedHost, newCpuUsage, newMemUsage);
				
				return hostNames[i];
			} // if
		} // for
		
		return "NoSuitableHost";
	} // findBestHostBasedOnInstanceType
	
	public double[] calculateWeightedAggregate(double[] normalizedCpu, double[] normalizedMem,
			double[] normalizedVm, double[] normalizedEng, int suitableHosts)
	{
		double[] weightedSum = new double[suitableHosts];
		for (int i = 0; i < suitableHosts; i++)
		{
			weightedSum[i]= 0.10 * normalizedCpu[i] + 0.10 * normalizedMem[i] + 0.31 * normalizedVm[i]
					+ 0.49 * normalizedEng[i];
			weightedSum[i] = roundUpToThreeDecimals(weightedSum[i]);
		} // for
		
		return weightedSum;
	} // calculateWeightedAggregate
	
	public void inverseWeighting(double[] array)
	{
		for (int i = 0; i < array.length; i++)
		{
			if (array[i] == 0)
			{
				array[i] = 1;
			} // if
			
			else if (array[i] == 1)
			{
				array[i] = 0;
			} // else if
			
			else
			{
				array[i] = roundUpToThreeDecimals(1.0 - array[i]);
			} // else
		} // for
	} // inverseWeighting
	
	public void quickSort(double weightedSum[], String[] hostNames, int low, int high)
	{
		int i = low;
		int j = high;
		double y = 0;
		
		/* compare value */
		double z = weightedSum[(low + high) / 2];
		
		/* partition */
		do
		{
			/* find member above ... */
			while (weightedSum[i] < z) i++;
			
			/* find element below ... */
			while (weightedSum[j] > z) j--;
			
			if(i <= j)
			{
				/* swap two elements */
				y = weightedSum[i];
				weightedSum[i] = weightedSum[j]; 
				weightedSum[j] = y;
				
				String tempHostName = hostNames[i];
				hostNames[i] = hostNames[j];
				hostNames[j] = tempHostName;
				
				i++; 
				j--;
			} // if
		} while (i <= j);

		/* recurse */
		if (low < j)
		{
			quickSort(weightedSum, hostNames, low, j);
		} // if
		
		if (i < high)
		{
			quickSort(weightedSum, hostNames, i, high);
		} // if
	} // quicksort
	
	public double[] normalize(double[] param)
	{
		double high=param[0], low=param[0];
		double[] normalized = new double[param.length];
		
		for(int i = 0; i < param.length; i++)
		{	
			if (param[i] < low)
			{
				low = param[i];
			} // if
			
			if (param[i] > high)
			{
				high = param[i];
			} // if
		} // for
		
		for (int i=0; i < param.length; i++)
		{
			double v1 = param[i]-low;
			double v2 = high-low;
			double normalizedValue = v1/v2;
			
			if (Double.isInfinite(normalizedValue) || Double.isNaN(normalizedValue))
			{
				normalizedValue = 0;
			} // if
			
			normalized[i] = roundUpToThreeDecimals(normalizedValue);
		} // for
		
		return normalized;
	 } // normalize
	
	public double roundUpToThreeDecimals(double d)
	{
		return Math.round(d * 1000.0) / 1000.0;
	} // roundUpToThreeDecimals
} // class