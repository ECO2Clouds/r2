package eu.eco2clouds.scheduler.designtime;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import eu.eco2clouds.accounting.datamodel.parser.Testbed;
import eu.eco2clouds.accounting.datamodel.parser.TestbedMonitoring;
import eu.eco2clouds.applicationProfile.datamodel.Compute;
import eu.eco2clouds.applicationProfile.datamodel.ExperimentDescriptor;
import eu.eco2clouds.applicationProfile.datamodel.ResourceCompute;
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
 *
 * 
 * @author Adonis Adonis
 *
 */
public class BiObjectiveOptimizationBdm
{
	private static AccountingClientHC client;
	
	private final static double cpuConvertionUnit = 100.0;
	private final static double memConvertionUnit = 1024.0;
	/**
	 * Finds a suitable location and host for a specific instance (e.g. lite, small, medium, large etc)
	 * @param instanceType as mentioned in Application Profile
	 * @return Location and Host in an ArrayList<String>
	 */
	public static String[] findLocation(ExperimentDescriptor experimentDescriptor)
	{
		client = new AccountingClientHC();
		List<Testbed> testbeds = client.getListOfTestbeds();
		
		if (client == null || testbeds == null || testbeds.size() == 0)
		{
			throw new RuntimeException(BiObjectiveOptimizationBdm.class + ": NO TESTBEDS"); 
		} // if
		
		return findSuitableLocation(testbeds, experimentDescriptor);
	} // findLocation

	public static String[] findSuitableLocation(List<Testbed> testbeds,
			ExperimentDescriptor experimentDescriptor)
	{
		HashMap<String, Testbed> suitableTestbeds = new HashMap<String, Testbed>();
		for (Testbed testbed : testbeds)
		{
			suitableTestbeds.put(testbed.getName(), testbed);
		} // for
		
		//TODO: Create MEthod
		//-------------------------------Create Method------------------------------------------------
		double totalRequiredCpu = 0, totalRequiredMem = 0;
		for (int i = 0; i < experimentDescriptor.getResourcesCompute().size(); i++)
		{
			Compute compute = experimentDescriptor.getResourcesCompute().get(i).getCompute();
			String instanceType = compute.getInstanceType();
			InstanceTypeRequirements instanceTypeRequirements = new InstanceTypeRequirements(instanceType, compute);
			
			totalRequiredCpu += instanceTypeRequirements.getCpuRequirement();
			totalRequiredMem += instanceTypeRequirements.getMemRequirement();
		} // for
		
		for (Testbed testbed : testbeds)
		{
			if (TestbedUtilities.getTotalCpu(testbed) < totalRequiredCpu * cpuConvertionUnit ||
					TestbedUtilities.getTotalMem(testbed) < totalRequiredMem * memConvertionUnit)
			{
				suitableTestbeds.remove(testbed.getName());
			} // if
		} // for
		
		//filterTestbedsAccordingToInstanceType(suitableTestbeds, instanceType);
		//-------------------------------Create Method------------------------------------------------
		
		if (suitableTestbeds.size() == 0)
		{
			throw new RuntimeException(BiObjectiveOptimizationBdm.class + ": NO SUITABLE TESTBEDS");
		} // if
		
		if (suitableTestbeds.size() == 1)
		{
			String[] testbedNames = new String[1];
			testbedNames[0] = suitableTestbeds.values().iterator().next().getName(); 
			return testbedNames;
		} // if
		
		return checkLoadBalanceThreshold(suitableTestbeds);
	} // findSuitableLocation

	public static HashMap<String, Testbed> filterTestbedsAccordingToInstanceType(
			HashMap<String,Testbed> suitableTestbeds, InstanceTypeRequirements instanceType)
	{
		Iterator<String> notSuitableLocations = instanceType.getNotSuitableLocations().iterator();
		while (notSuitableLocations.hasNext())
		{
			suitableTestbeds.remove(notSuitableLocations.next());
		} // while
		
		return suitableTestbeds;
	} // filterTestbedsAccordingToInstanceType
	
	private static String[] checkLoadBalanceThreshold(HashMap<String, Testbed> suitableTestbeds)
	{
		double loadBalanceThreshold = 40.0;
		boolean allBellowThreshold = true;
		
		String[] testbedNames = new String[suitableTestbeds.size()];
		double[] loadCapacitor = new double[suitableTestbeds.size()];
		boolean[] aboveThreshold = new boolean[suitableTestbeds.size()];
		
		int index = 0;
		Iterator<Testbed> suitableTestbedsIterator = suitableTestbeds.values().iterator();
		while(suitableTestbedsIterator.hasNext())
		{
			Testbed testbed = suitableTestbedsIterator.next();
			testbedNames[index] = testbed.getName();
			loadCapacitor[index] = (TestbedUtilities.getCpuAvailability(testbed) +
					TestbedUtilities.getMemAvailability(testbed)) / 2.0;
			
			if (loadCapacitor[index] >= loadBalanceThreshold)
			{
				aboveThreshold[index] = true;
				allBellowThreshold = false;
			} // if
			
			index++;
		} // while
		
		if (allBellowThreshold)
		{
			/*int maxLoadCapacitorIndex = 0;
			double maxLoadCapacitor = loadCapacitor[maxLoadCapacitorIndex];
			for (int i = 0; i < suitableTestbeds.size(); i++)
			{
				if (loadCapacitor[i] > maxLoadCapacitor)
				{
					maxLoadCapacitor = loadCapacitor[i];
					maxLoadCapacitorIndex = i;
				} // if
			} // for
			
			return testbedNames[maxLoadCapacitorIndex];*/
			
			return calculateBestLocation(suitableTestbeds);
		} // if
		
		for (int i = 0; i < testbedNames.length; i++)
		{
			if (!aboveThreshold[i])
			{
				suitableTestbeds.remove(testbedNames[i]);
			} // if
		} // for
		
		return calculateBestLocation(suitableTestbeds);
	} // checkLoadBalanceThreshold

	public static String[] calculateBestLocation(HashMap<String, Testbed> suitableTestbeds)
	{
		String[]  testbedNames = new String[suitableTestbeds.size()];
		
		if (suitableTestbeds.size() == 1)
		{
			testbedNames[0] = suitableTestbeds.values().iterator().next().getName();
			return testbedNames;
		} // if
		
		double[] energy = new double[suitableTestbeds.size()];
		double[] co2 = new double[suitableTestbeds.size()];
		double[] pue = new double[suitableTestbeds.size()];
		//double[] gec = new double[suitableTestbeds.size()]; gec = totalGreen * 100 / Total
		double[] loadCapacitor = new double[suitableTestbeds.size()];
		
		int index = 0;
		Iterator<Testbed> suitableTestbedsIterator = suitableTestbeds.values().iterator();
		while (suitableTestbedsIterator.hasNext())
		{
			Testbed testbed = suitableTestbedsIterator.next();
			testbedNames[index] = testbed.getName();
			
			
			TestbedMonitoring testbedMonitoring = client.getTestbedMonitoringStatus(testbed);
			
			energy[index] = TestbedUtilities.getAllHostsPowerConsumptionofTestbed(testbed);
			co2[index] = testbedMonitoring.getCo2().getValue();
			pue[index] = testbedMonitoring.getPue().getValue();
			
			loadCapacitor[index] = (TestbedUtilities.getCpuAvailability(testbed) +
					TestbedUtilities.getMemAvailability(testbed)) / 2.0;
			
			index++;
		} // while
		
		// normalize data
		double[] normalizedEnergy = normalize(energy);
		double[] normalizedCo2 = normalize(co2);
		double[] normalizedPue = normalize(pue);
		double[] normalizedLoadCapacitor = normalize(loadCapacitor);
		
		// inverse weighting on Energy and Co2
		inverseWeighting(normalizedEnergy);
		inverseWeighting(normalizedCo2);
		
		// get sum of the weights
		double[] weightedSum = calculateWeightedAggregate(
				normalizedEnergy, normalizedCo2, normalizedPue,
				normalizedLoadCapacitor, suitableTestbeds.size());
		
		//WARNING: AFTER QUICKSORT ONLY THE ARRAYS "WEIGTHEDSUM" AND "TESTBEDNAMES" ARE UPDATED
		quickSort(weightedSum, testbedNames, 0, testbedNames.length - 1);
		
		return testbedNames;
	} // calculateBestLocation
	
	public static double[] calculateWeightedAggregate(double[] normalizedEnergy,
			double[] normalizedCo2,	double[] normalizedPue,
			double[] normalizedLoadCapacitor, int suitableTestbeds)
	{
		double[] weightedSum = new double[suitableTestbeds];
		for (int i = 0; i < suitableTestbeds; i++)
		{
			weightedSum[i]= (normalizedEnergy[i] * 0.20) +
					(normalizedCo2[i] * 0.30) +
					(normalizedPue[i] * 0.15) +
					(normalizedLoadCapacitor[i] * 0.35);
			
			weightedSum[i] = roundUpToThreeDecimals(weightedSum[i]);
		} // for
		
		return weightedSum;
	} // calculateWeightedAggregate
	
	public static void quickSort(double weightedSum[], String[] testbedNames, int low, int high)
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
				
				String tempHostName = testbedNames[i];
				testbedNames[i] = testbedNames[j];
				testbedNames[j] = tempHostName;
				
				i++; 
				j--;
			} // if
		} while (i <= j);

		/* recurse */
		if (low < j)
		{
			quickSort(weightedSum, testbedNames, low, j);
		} // if
		
		if (i < high)
		{
			quickSort(weightedSum, testbedNames, i, high);
		} // if
	} // quicksort
	
	public static void inverseWeighting(double[] array)
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
	
	public static double[] normalize(double[] param)
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
	
	public static double roundUpToThreeDecimals(double d)
	{
		return Math.round(d * 1000.0) / 1000.0;
	} // roundUpToThreeDecimals
} // class