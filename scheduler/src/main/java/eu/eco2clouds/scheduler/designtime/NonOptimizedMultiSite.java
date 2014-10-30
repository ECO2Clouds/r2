package eu.eco2clouds.scheduler.designtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import eu.eco2clouds.accounting.datamodel.parser.Testbed;
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
public class NonOptimizedMultiSite
{
	private static AccountingClientHC client;
	
	public static String findLocation(InstanceTypeRequirements instanceType)
	{
		client = new AccountingClientHC();
		List<Testbed> testbeds = client.getListOfTestbeds();
		
		if (client == null || testbeds == null || testbeds.size() == 0)
		{
			throw new RuntimeException(BiObjectiveOptimizationIdm.class + ": NO TESTBEDS"); 
		} // if
		
		return findRandomLocation(testbeds, instanceType);
	} // findLocation

	public static String findRandomLocation(List<Testbed> testbeds,	InstanceTypeRequirements instanceType)
	{
		HashMap<String, Testbed> suitableTestbeds = new HashMap<String, Testbed>();
		for (Testbed testbed : testbeds)
		{
			suitableTestbeds.put(testbed.getName(), testbed);
		} // for
		
		filterTestbedsAccordingToInstanceType(suitableTestbeds, instanceType);
		
		if (suitableTestbeds.size() == 0)
		{
			throw new RuntimeException(BiObjectiveOptimizationIdm.class + ": NO SUITABLE TESTBEDS");
		} // if
		
		if (suitableTestbeds.size() == 1)
		{
			return suitableTestbeds.values().iterator().next().getName();
		} // if
		
		return randomTestbed(suitableTestbeds);
	} // findSuitableLocation
	
	public static void filterTestbedsAccordingToInstanceType(
			HashMap<String,Testbed> suitableTestbeds, InstanceTypeRequirements instanceType)
	{
		Iterator<String> notSuitableLocations = instanceType.getNotSuitableLocations().iterator();
		while (notSuitableLocations.hasNext())
		{
			suitableTestbeds.remove(notSuitableLocations.next());
		} // while
	} // filterTestbedsAccordingToInstanceType
	
	private static String randomTestbed(HashMap<String, Testbed> suitableTestbeds)
	{
		ArrayList<String> testbedNames = new ArrayList<String>(suitableTestbeds.keySet());
		return testbedNames.get((int) (Math.random() * testbedNames.size()));
	} //randomTestbed
} // NonOptimizedMultiSite