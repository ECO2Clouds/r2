package eu.eco2clouds.scheduler.designtime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import eu.eco2clouds.accounting.datamodel.parser.Testbed;
import eu.eco2clouds.applicationProfile.datamodel.Compute;

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
 * Class that provides the requirements of CPU and Memory
 * for each instance type in the application profile.
 * @author Adonis Adoni
 *
 */
public final class InstanceTypeRequirements
{
	private final static double cpuConvertionUnit = 100.0;
	private final static double memConvertionUnit = 1024.0;
	
	private double cpu, mem;
	private HashSet<String> notSuitableLocations;
	private List<String> userSuggestedLocations;
	
	public InstanceTypeRequirements(String instanceType, Compute compute)
	{
		notSuitableLocations = new HashSet<String>();
		userSuggestedLocations = compute.getLocations();
		
		if (instanceType.equals("lite"))
		{
			cpu = 0.5;
			mem = 256;
		} // if
		
		else if (instanceType.equals("small"))
		{
			cpu = 1;
			mem = 1024;
		} // else if
		
		else if (instanceType.equals("medium"))
		{
			cpu = 2;
			mem = 2048;
		} // else if
		
		else if (instanceType.equals("large") || instanceType.equals("large+"))
		{
			cpu = 2;
			mem = 4096;
			notSuitableLocations.add("fr-inria");
		} // else if
		
		else if (instanceType.equals("xlarge") || instanceType.equals("xlarge+"))
		{
			cpu = 4;
			mem = 4096;
			notSuitableLocations.add("fr-inria");
		} // else if
		
		else if (instanceType.equals("custom"))
		{
			this.cpu = compute.getCpu() / cpuConvertionUnit;
			this.mem = compute.getMemory() / memConvertionUnit;
		} // else if
		
		else //Make medium default
		{
			cpu = 2;
			mem = 2048;
			//throw new RuntimeException("Undefined Instance Type");
		} // else
	} // constructor
	
	public List<String> getUserSuggestedLocations()
	{
		return userSuggestedLocations;
	} // getUserSuggestedLocations
	
	public double getCpuRequirement()
	{
		return cpu;
	} // getCpuRequirement
	
	public double getMemRequirement()
	{
		return mem;
	} // getMemRequirement
	
	public HashSet<String> getNotSuitableLocations()
	{
		return notSuitableLocations;
	} // getNotSuitableLocations
	
	public static String mapInstanceType(double cpu, double mem)
	{
		cpu /= cpuConvertionUnit;
		mem /= memConvertionUnit;
		
		if (cpu <= 0.5)
		{
			return "lite";
		} // if
		
		if (cpu > 0.5 && cpu <= 1.0)
		{
			return "small";
		} // if
		
		if (cpu > 1 && cpu <= 2.0)
		{
			if (mem <= 2048)
			{
				return "medium";				
			} // if
			
			else
			{
				return "large";
			} // else
		} // if
		
		if (cpu > 2.0)
		{
			return "xlarge";
		} // if
		
		return null;
	} // mapInstanceType
} // class