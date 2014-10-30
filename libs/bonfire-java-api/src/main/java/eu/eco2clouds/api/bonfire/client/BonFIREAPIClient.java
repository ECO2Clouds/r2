package eu.eco2clouds.api.bonfire.client;

import java.util.ArrayList;

import eu.eco2clouds.api.bonfire.client.exceptions.NoHrefSetException;
import eu.eco2clouds.api.bonfire.occi.datamodel.Compute;
import eu.eco2clouds.api.bonfire.occi.datamodel.Configuration;
import eu.eco2clouds.api.bonfire.occi.datamodel.Experiment;
import eu.eco2clouds.api.bonfire.occi.datamodel.Location;
import eu.eco2clouds.api.bonfire.occi.datamodel.Network;
import eu.eco2clouds.api.bonfire.occi.datamodel.Resource;
import eu.eco2clouds.api.bonfire.occi.datamodel.Storage;

/**
 * 
 * Copyright 2014 ATOS SPAIN S.A. 
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
 * @author: David Garcia Perez. Atos Research and Innovation, Atos SPAIN SA
 * e-mail david.garciaperez@atos.net
 */
public interface BonFIREAPIClient {

	public void setUrl(String url);
	public String getUrl();
	public void setUserName(String userName);
	public String getUserName();
	public void setPassword(String password);
	public String getPassword();
	
	public ArrayList<Location> getLocations();
	public ArrayList<Experiment> getExperiments();
	public Experiment getExperiment(int id);
	
	public ArrayList<Storage> getStoragesOfALocation(Location location);
	public ArrayList<Network> getNetworksOfALocation(Location location);
	public ArrayList<Configuration> getConfigurationsOfALocation(Location location);
	
	public Experiment createExperiment(Experiment experiment);
	public Storage createStorage(Location location, Experiment experiment, Storage storage);
	public Compute createCompute(Location location, Experiment experiment, Compute compute);
	public Compute createCompute(Location location, String experimentId, Compute compute);
	public Compute createComputeAtBonFIREWAN(Location location, Experiment experiment, Compute compute);
	public Compute createComputeAtBonFIREWAN(Location location, String experimentId, Compute compute);
	public Compute suspendCompute(Compute compute);
	public Compute refreshCompute(Compute compute);
	public Compute resumeCompute(Compute compute);
	public Compute stopCompute(Compute compute);
	public Compute shutdownCompute(Compute compute);
	public Compute cancelCompute(Compute compute);
	
	public Storage makeStoragePublic(Storage storage);
	public Storage unMakeStoragePublic(Storage storage);
//	public Storage makeStoragePersistent(Storage storage);
//	public Storage unMakeStoragePersistent(Storage storage);
	
	public Resource updateResourceInfo(Resource resource) throws NoHrefSetException;
	
	public boolean deleteResource(Resource resource);
}
