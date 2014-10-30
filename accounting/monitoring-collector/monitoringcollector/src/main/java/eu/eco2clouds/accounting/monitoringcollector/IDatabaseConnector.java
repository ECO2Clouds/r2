////////////////////////////////////////////////////////////////////////
//
// Copyright (c) The University of Edinburgh, 2013
//
// This software may not be used, sold, licensed, transferred, copied
// or reproduced in whole or in part in any manner or form or in or
// on any media by any person other than in accordance with the terms
// of the Licence Agreement supplied with the software, or otherwise
// without the prior written consent of the copyright owners.
//
// This software is distributed WITHOUT ANY WARRANTY, without even the
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
// PURPOSE, except where stated in the Licence Agreement supplied with
// the software.
//
//	Created By :			Dominic Sloan-Murphy, Iakovos Panourgias
//	Last Updated Date :		28 Aug 2014
//	Created for Project :	ECO2Clouds
//
////////////////////////////////////////////////////////////////////////
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.   
//
////////////////////////////////////////////////////////////////////////

package eu.eco2clouds.accounting.monitoringcollector;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to generalise database access and facilitate testing.
 */
public interface IDatabaseConnector {

	/**
	 * Connect to the current database host.
	 */
	public boolean connect();
	
	/**
	 * Disconnect from the current database host.
	 */
	public void disconnect();
	
	/**
	 * Insert a new experiment record into the database.
	 *
	 * @param experimentId the experiment id
	 */
	public void insertNewExperiment(int experimentId);
	
	void insertNewExperiment(int experimentId, String groupName, String userName);
	
	/**
	 * Sets the active value on the record with the specified experiment id to 0.
	 *
	 * @param experimentId the experiment to set inactive
	 */
	public void setExperimentTerminated(int experimentId);
	
	public void setExperimentActive(int experimentId);
	
	/**
	 * Sets the aggregator on the the record with the specified experiment id.
	 *
	 * @param experimentId the experiment id
	 * @param objectId the indicator of the aggregator experiment resource
	 */
	public void setExperimentAggregator(int experimentId, URI objectId);
	
	/**
	 * Sets the user_name on the the record with the specified experiment id.
	 *
	 * @param experimentId the experiment id
	 * @param objectId the indicator of the user_name experiment resource
	 */
	public void setExperimentUserName(int experimentId, String userName);
	
	/**
	 * Sets the group_name on the the record with the specified experiment id.
	 *
	 * @param experimentId the experiment id
	 * @param objectId the indicator of the group_name experiment resource
	 */
	public void setExperimentGroupName(int experimentId, String groupName);
	/**
	 * Insert a new virtual machine record into the database.
	 * Both host and site are required as a physical host name cannot be used as a unique identifier.
	 * e.g. Both fr-inria and uk-epcc could name a host "BonFIRE-Host-1".   
	 *
	 * @param experimentId the id of the experiment with which the virtual machine is to be associated 
	 * @param objectId the indicator of the compute resource
	 * @param timestamp the timestamp of the compute creation event that produced the virtual machine
	 * @param host the physical host machine which the virtual machine is running on 
	 * @param site the site/testbed where the physical host machine is located.
	 */
	public void insertNewVM(int experimentId, URI objectId, long timestamp, String host, String site);
	
	public void updateVM(int experimentId, URI location, long timeStamp, String host, String site);
	
	public void updateVMPhysicalHost(int VMid, long timeStamp, String host);
	
	void terminateVM(int experimentId, URI location, long timeStamp, String host, String site);
	
	/**
	 * Insert a new site record for each element of the supplied array.
	 *
	 * @param locations the location names of each site. e.g. { "uk-epcc", "fr-inria", "de-hlrs", etc. }
	 */
	public void insertNewSites(String[] locations);
	
	/**
	 * Insert a new physical hosts record for each element of the supplied hosts array and associate that record with the supplied site name.
	 * It is assumed that site names can be used as unique identifiers. e.g. It will never be the case that two distinct partners share the name uk-epcc.
	 *
	 * @param hosts the host names of each physical machine
	 * @param siteName the name of the site where the specified physical machines are housed.
	 */
	public void insertNewPhysicalHosts(String[] hosts, String siteName);
	
	/**
	 * Queries the database for all experiments where active == 1.
	 *
	 * @return a collection containing the active experiments
	 */
	public ArrayList<Experiment> getActiveExperiments();
	
	public Experiment getExperiment(int experimentId);
	
	public ArrayList<Experiment> getAllExperiments();
	
	public Boolean updateExperimentGroup(int experimentID, String groupName);
	
	public	Boolean updateExperimentUser(int experimentId, String userName);
	
	/**
	 * Queries the database for all virtual machines where the associated experiment has field active == 1.
	 *
	 * @return a collection containing the active virtual machines
	 */
	public ArrayList<VirtualMachine> getActiveVirtualMachines();

	/**
	 * Queries the database for all virtual machines where the associated experiment has field active == 1 and they belong to a specific Experiment.
	 *
	 * @return a collection containing the active virtual machines
	 */
	public ArrayList<VirtualMachine> getActiveVirtualMachinesOfExperiment(int experimentId);
	
	ArrayList<VirtualMachine> getAllVirtualMachinesOfExperiment(int experimentId);
	
	/**
	 * Queries the database for all physical machine records.
	 * 
	 * @return  a collection containing the physical host machines records
	 */
	public ArrayList<PhysicalHost> getPhysicalHosts();
	
	/**
	 * Updates the database records associated with the supplied resources with the collection of Items (metrics data) on each resource.
	 * Only inserts an Item record so long as it is not a duplicate. It is classified as a duplicate if there already exists an association 
	 * with the current resource in the "resource_items" table for an "items" record that has the same zabbix_itemid and timestamp.   
	 *
	 * @param resources the collection of resources subject to update. Each resource object has a List of Items containing the metrics gathered.
	 * @param itemsTable the items table which is associated with the resources being updated. e.g. When the resources are virtual machines, the itemsTable string must be "virtual_machines".
	 */
	public void updateMetrics(List<? extends Resource> resources, String itemsTable);

	ArrayList<VirtualMachine> getVirtualMachinesWithoutPhysicalHost();
		
	// Not used in this iteration of the software
	//public void setVMDone(int experimentId, URI objectId);
	
}
