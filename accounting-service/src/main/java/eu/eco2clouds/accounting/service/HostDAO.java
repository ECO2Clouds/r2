package eu.eco2clouds.accounting.service;

import java.util.List;

import eu.eco2clouds.accounting.datamodel.Host;
import eu.eco2clouds.accounting.datamodel.HostData;

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
 *
 * DAO interface to access to the Host information
 *
 */
public interface HostDAO extends DAO<Host> { 
	
	/**
	 * Returns a Host by its name
	 * @param name Name of the Host
	 * @return The Host from the database
	 */
	public Host getByName(String name);
	
	/**
	 * Returns the Host from the database by its Id
	 * @param id of the Host
	 * @return the corresponding Host from the database
	 */
	public Host getById(int id);
	
	/**
	 * Returns all the Hosts stored in the database
	 * @return all the Hosts stored in the database
	 */
	public List<Host> getAll();
	
	/**
	 * Stores a Host into the database
	 * @param HostXml Host to be saved.
	 * @return <code>true</code> if the Host was saved correctly
	 */
	public boolean save(Host host);
	
	/**
	 * Updates a Host in the database
	 * @param HostXml Host to be updated
	 * @return <code>true</code> if the Host was saved correctly
	 */
	public boolean update(Host host);
	
	/**
	 * Deletes a Host from the database
	 * @param HostXml to be deleted
	 * @return <code>true</code> if the Host was deleted correctly
	 */
	public boolean delete(Host host);
	
	/**
	 * Finds a specific Host in the database
	 * @param HostXml Host to be found in the database
	 * @return the Host object extracted from the databese
	 */
	public Host find(Host host);
	
	/**
	 * Stores a Host into the database with the relation with the testbed
	 * @param HostXml Host to be saved.
	 * @return <code>true</code> if the Host was saved correctly
	 */
	public boolean saveHost(Host host, int testbed_id);
	
}
