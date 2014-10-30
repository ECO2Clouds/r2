package eu.eco2clouds.scheduler.runtime;

import java.util.List;



import eu.eco2clouds.api.bonfire.client.BonFIREAPIClient;

import eu.eco2clouds.api.bonfire.client.BonFIREAPIClientImpl;

import eu.eco2clouds.api.bonfire.occi.datamodel.Compute;

import eu.eco2clouds.api.bonfire.occi.datamodel.Configuration;

import eu.eco2clouds.api.bonfire.occi.datamodel.Disk;

import eu.eco2clouds.api.bonfire.occi.datamodel.Experiment;

import eu.eco2clouds.api.bonfire.occi.datamodel.Location;

import eu.eco2clouds.api.bonfire.occi.datamodel.Storage;

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
public class RuntimeAdaptation {
	public static String newExp()
	{
		System.out.println("Creating new Experiment");

		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setUserName("usmanwajid");
		client.setPassword("Leechi07");
		client.setUrl("https://api.bonfire-project.eu");

		Experiment experiment = new Experiment();
		experiment.setDescription("Test Experiment");
		experiment.setGroups("eco2clouds");
		experiment.setStatus("ready");
		experiment.setWalltime(5000);
		experiment.setName("BonFIRE_Japi_Test");

		Experiment newExperiment = client.createExperiment(experiment);		 
		String outcome1 = newExperiment.getId()+"-"+newExperiment.getName();
		String outcome2= newVm(newExperiment, client);
		String outcome = outcome1 +" - "+ outcome2;
		return outcome;
	}

	public static String newVm(Experiment exp, BonFIREAPIClient client)
	{
		System.out.println("Creating new VM");		
		//location
		Location epcc = new Location();
		List<Location> locations = client.getLocations();		        
		for(Location location : locations) {
		   String name = location.getName();
		 if(name.equals("uk-epcc")) 
			 epcc = location;
		}
		//configuration
		List<Configuration> configurations = client.getConfigurationsOfALocation(epcc);
		Configuration lite = new Configuration();
		for(Configuration configuration : configurations) {
		  String name = configuration.getName();
		  if(name.equals("lite")) 
			  lite = configuration;
		}
		//storage		
		List<Storage> storages = client.getStoragesOfALocation(epcc);
		Storage bonfireSqueeze = new Storage();		
		for(Storage storage : storages) {
		   String name = storage.getStorageName();
		   if(name.equals("BonFIRE DebiÖ")) 
			   bonfireSqueeze = storage;
		}
		//disk
		Disk disk = new Disk();
		disk.setType("OS");
		disk.setTarget("hda");
		disk.setStorage(bonfireSqueeze);
		//network		
		/*List<Network> networks = client.getNetworksOfALocation(epcc);		
		Network bonfireWan = new Network();
		for(Network network : networks) 
			if(network.getNetworkName().equals("BonFIRE WAN"))
				bonfireWan=network;  
		Nic nic = new Nic();
		nic.setNetwork(bonfireWan);
		*/

		//compute
		Compute compute = new Compute();
		compute.setGroups("eco2clouds");
		compute.setComputeName("JavaAPICompute");
		compute.setConfiguration(lite);
		compute.addDisk(disk);		
		//compute.addNic(nic);
		Compute newCompute = client.createCompute(epcc, exp, compute);		
		return newCompute.getComputeName();
		}

	public static String newVmWithExpId(){
		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setUserName("usmanwajid");
		client.setPassword("Leechi07");
		client.setUrl("https://api.bonfire-project.eu");		
		//location
		Location epcc = new Location();
		List<Location> locations = client.getLocations();		        
		for(Location location : locations) {
		   String name = location.getName();
		 if(name.equals("uk-epcc")) 
			 epcc = location;
		}
		//configuration
		List<Configuration> configurations = client.getConfigurationsOfALocation(epcc);
		Configuration small = new Configuration();
		for(Configuration configuration : configurations) 
		{
		 String name = configuration.getName();
		 if(name.equals("small")) 
			 small = configuration;
		}
		//storage		
		List<Storage> storages = client.getStoragesOfALocation(epcc);
		Storage bonfireSqueeze = new Storage();		
		for(Storage storage : storages) {
		   String name = storage.getStorageName();
		   if(name.equals("WebServer")) 
			   bonfireSqueeze = storage;
		}

		//disk
		Disk disk = new Disk();
		disk.setType("OS");
		disk.setTarget("hda");
		disk.setStorage(bonfireSqueeze);
		//compute
		Compute compute = new Compute();
		compute.setGroups("eco2clouds");
		compute.setComputeName("JavaAPIComputeWithExpId");
		compute.setConfiguration(small);
		compute.addDisk(disk);		
		Compute newCompute = client.createCompute(epcc, "experiments/41693", compute);		
		return newCompute.getComputeName();
		}

}