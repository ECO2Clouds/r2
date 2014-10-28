package eu.eco2clouds.accounting.service;

import java.util.List;

import eu.eco2clouds.accounting.datamodel.VMHost;

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
public interface VMHostDAO extends DAO<VMHost> {

	/**
	 * Returns the VMHost from the database by its Id
	 * 
	 * @param id of the VMHost
	 * @return the corresponding VMHost from the database
	 */
	public VMHost getById(int id);

	/**
	 * Returns all the VMHost stored in the database
	 * 
	 * @return all the VMHost stored in the database
	 */
	public List<VMHost> getAll();

	/**
	 * Stores a VMHost into the database
	 * 
	 * @param VMHost
	 *            VMHost to be saved.
	 * @return <code>true</code> if the VMHost was saved correctly
	 */
	public boolean save(VMHost vMHost);

	/**
	 * Updates a VMHost in the database
	 * 
	 * @param VMHost
	 *            VMHost to be updated
	 * @return <code>true</code> if the VMHost was saved correctly
	 */
	public boolean update(VMHost vMHost);

	/**
	 * Deletes a VMHost from the database
	 * 
	 * @param VMHost
	 *            to be deleted
	 * @return <code>true</code> if the VMHost was deleted correctly
	 */
	public boolean delete(VMHost vMHost);
	
	/**
	 * Returns the VMHost from the database by its vmId
	 * 
	 * @param vmId of the VMHost
	 * @return the corresponding VMHost from the database
	 */
	
	public VMHost getByVmId (int vmId);
}
