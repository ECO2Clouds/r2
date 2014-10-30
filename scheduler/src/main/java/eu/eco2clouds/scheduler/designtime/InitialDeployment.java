package eu.eco2clouds.scheduler.designtime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import eu.eco2clouds.applicationProfile.datamodel.ApplicationProfile;
import eu.eco2clouds.applicationProfile.datamodel.Compute;
import eu.eco2clouds.applicationProfile.datamodel.ExperimentDescriptor;
import eu.eco2clouds.applicationProfile.parser.Parser;

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
public class InitialDeployment
{
	public static ExperimentDescriptor computeDeployment(String applicationProfileString)
			throws JsonParseException, JsonMappingException, IOException
	{
		ApplicationProfile applicationProfileObject = Parser.getApplicationProfile(applicationProfileString);
		
		ExperimentDescriptor ed = applicationProfileObject.getExperimentDescriptor();
		
		HostsChangesBeforeDeployment.createNewInstance();
		
		if (isBiObjectiveBulkDeploymentMode)
		{
			//FileOutput.outputToFile("is BDM\n");
			String[] locations = BiObjectiveOptimizationBdm.findLocation(ed);
			for (int i = 0; i < ed.getResourcesCompute().size(); i++)
			{
				Compute compute = ed.getResourcesCompute().get(i).getCompute();
				String instanceType = compute.getInstanceType();
				InstanceTypeRequirements instanceTypeRequirements = new InstanceTypeRequirements(instanceType, compute);
				
				if (instanceTypeRequirements.getUserSuggestedLocations().get(0).isEmpty() ||
						instanceTypeRequirements.getUserSuggestedLocations().size() > 1)
				{
					String location = locations[locations.length - 1];
					boolean isLocationFound = false;
					for (int locationsIndex = locations.length - 1; !isLocationFound && locationsIndex >= 0; locationsIndex--)
					{
						for (String userSuggestedLocation : instanceTypeRequirements.getUserSuggestedLocations())
						{
							if (locations[locationsIndex].equals(userSuggestedLocation) || userSuggestedLocation.isEmpty())
							{
								location = locations[locationsIndex];
								isLocationFound = true;
								break;
							} // if
						} // for
					} // for
					
					String newHost = optimize(instanceTypeRequirements, location); /*invoke single-objective (host only) optimization function*/
					
					ArrayList<String> newLocation = new ArrayList<String>();
					newLocation.add(location);
					
					compute.setLocations(newLocation);
					compute.setHost(newHost);
				} // if
				
				else
				{
					String newHost = optimize(instanceTypeRequirements, instanceTypeRequirements.getUserSuggestedLocations().get(0));	//invoke single-objective (host only) optimization function
					compute.setHost(newHost);
				} // else
			} // for
			
			return ed;
		} // if
		
		if (isBiObjectiveDispersalDeploymentMode)
		{
			//FileOutput.outputToFile("is DDM\n");
			BiObjectiveOptimizationDdm ddm = new BiObjectiveOptimizationDdm();
			
			for (int i = 0; i < ed.getResourcesCompute().size(); i++)
			{
				Compute compute = ed.getResourcesCompute().get(i).getCompute();
				String instanceType = compute.getInstanceType();
				InstanceTypeRequirements instanceTypeRequirements = new InstanceTypeRequirements(instanceType, compute);
				
				if (instanceTypeRequirements.getUserSuggestedLocations().get(0).isEmpty() || instanceTypeRequirements.getUserSuggestedLocations().size() > 1)
				{
					String newHost = ddm.findHost(instanceTypeRequirements);
					String location = TestbedUtilities.findTestbedOfHost(newHost).getName();
					
					ArrayList<String> newLocation = new ArrayList<String>();
					newLocation.add(location);
					
					compute.setLocations(newLocation);
					compute.setHost(newHost);
				} // if
				
				else
				{
					String newHost = optimize(instanceTypeRequirements, instanceTypeRequirements.getUserSuggestedLocations().get(0));	//invoke single-objective (host only) optimization function
					compute.setHost(newHost);
				} // else
			} // for
			
			return ed;
		} // if
		
		for (int i = 0; i < ed.getResourcesCompute().size(); i++)
		{			
			Compute compute = ed.getResourcesCompute().get(i).getCompute();
			String instanceType = compute.getInstanceType();
			ArrayList<String> userSuggestedLocations = compute.getLocations();
			
			InstanceTypeRequirements instanceTypeRequirements = new InstanceTypeRequirements(instanceType, compute);
			
			if (userSuggestedLocations.get(0).isEmpty() || userSuggestedLocations.size() > 1)
			{
				ArrayList<String> hostLocation = optimize(instanceTypeRequirements); //invoke bi-objective (host & location) optimization function 
				
				String newLoc = hostLocation.get(0);
				ArrayList<String> newLocation = new ArrayList<String>();
				newLocation.add(newLoc);
				
				compute.setLocations(newLocation);
				compute.setHost(hostLocation.get(1));
			} // if
			
			else
			{
				//new check - only perform optimization if host is null or not specified by user
				if(compute.getHost() != null && !compute.getHost().isEmpty())
				{
				 String predefinedHost = compute.getHost();
				 compute.setHost(predefinedHost); 
				}
				else
				{
					String newHost = optimize(instanceTypeRequirements, userSuggestedLocations.get(0));	//invoke single-objective (host only) optimization function
					compute.setHost(newHost);
				}
			} // else
		} // for
		
		HostsChangesBeforeDeployment.deleteCurrentInstance();
		
		return ed;
	} // computeDeployment
	
	private static SingleObjectiveOptimization soo = new SingleObjectiveOptimizationMaxUtil(); // max default
	
	public static void changeSingleObjectiveOptimization(String optimization)
	{
		if (optimization.equals("min"))
		{
			FileOutput.outputToFile("SOO: " + optimization + "\n");
			soo = new SingleObjectiveOptimizationMinUtil();
		} // if
		
		else if (optimization.equals("max"))
		{
			FileOutput.outputToFile("SOO: " + optimization + "\n");
			soo = new SingleObjectiveOptimizationMaxUtil();
		} // else if
		
		else if (optimization.equals("random"))
		{
			FileOutput.outputToFile("SOO: " + optimization + "\n");
			soo = new NonOptimizedSingleSite();
		} // else if
	} // changeOptimization
	
	//in the case that all are false then IDM mode is ON
	private static boolean isBiObjectiveBulkDeploymentMode = false; //false default
	private static boolean isBiObjectiveDispersalDeploymentMode = false; //false default
	private static boolean isBiObjectiveRandom = false; //false default
	
	public static void changeBiObjectiveMode(String mode)
	{
		if (mode.equals("idm"))
		{
			FileOutput.outputToFile("idm\n");
			isBiObjectiveDispersalDeploymentMode = false;
			isBiObjectiveBulkDeploymentMode = false;
			isBiObjectiveRandom = false;
		} // if
		
		else if (mode.equals("bdm"))
		{
			FileOutput.outputToFile("bdm\n");
			isBiObjectiveDispersalDeploymentMode = false;
			isBiObjectiveBulkDeploymentMode = true;
			isBiObjectiveRandom = false;
		} // else if
		
		else if (mode.equals("ddm"))
		{
			FileOutput.outputToFile("ddm\n");
			isBiObjectiveDispersalDeploymentMode = true;
			isBiObjectiveBulkDeploymentMode = false;
			isBiObjectiveRandom = false;
		} // else if
		
		else if (mode.equals("random"))
		{
			FileOutput.outputToFile("random\n");
			isBiObjectiveDispersalDeploymentMode = false;
			isBiObjectiveBulkDeploymentMode = false;
			isBiObjectiveRandom = true;
		} // else if
	} // changeBiObjectiveMode
	
	//single-objective optimization function i.e. select host at a particular location for specific instanceType
	public static String optimize(InstanceTypeRequirements instanceType, String designatedLocation)
	{
		String hostName = soo.findHost(instanceType, designatedLocation);
		return hostName;	
	} // optimize
	
	//bi-objective optimization function i.e. select location and host for specific instanceType
	public static ArrayList<String> optimize(InstanceTypeRequirements instanceType)
	{
		String location = "";
		
		if (isBiObjectiveRandom)
		{
			//FileOutput.outputToFile("is Random\n");
			location = NonOptimizedMultiSite.findLocation(instanceType);
		} // if
		
		else
		{
			//FileOutput.outputToFile("is IDM\n");
			location = BiObjectiveOptimizationIdm.findLocation(instanceType);
		} // else
		
		String hostName = optimize(instanceType, location);
		
		ArrayList<String> host_location = new ArrayList<String>();
		host_location.add(location);
		host_location.add(hostName);
		
		return host_location;
	} // optimize
} // class