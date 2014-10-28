package eu.eco2clouds.accounting.service;

import java.util.List;

import eu.eco2clouds.accounting.datamodel.Action;
import eu.eco2clouds.accounting.datamodel.VM;
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
public interface VMDAO extends DAO<VM> { 
	
	/**
	 * Returns the VM from the database by its Id
	 * @param id of the VM
	 * @return the corresponding VM from the database
	 */
	public VM getById(int id);
	
	/**
	 * Returns all the VMs stored in the database
	 * @return all the VMs stored in the database
	 */
	public List<VM> getAll();
	
	/**
	 * Stores a VM into the database
	 * @param VM VM to be saved.
	 * @return <code>true</code> if the VM was saved correctly
	 */
	public boolean save(VM vM);
	
	/**
	 * Updates a VM in the database
	 * @param VM VM to be updated
	 * @return <code>true</code> if the VM was saved correctly
	 */
	public boolean update(VM vM);
	
	/**
	 * Deletes a VM from the database
	 * @param VM to be deleted
	 * @return <code>true</code> if the VM was deleted correctly
	 */
	public boolean delete(VM vM);

	/**
	 * Deletes an Action related to a VM from the Databae
	 * @param vM 
	 * @param action
	 * @return the vM without the action attached to it.
	 */
	public VM deleteAction(VM vM, Action action);

	public VM deleteVMHost(VM vM, VMHost vMHost);
	
}
