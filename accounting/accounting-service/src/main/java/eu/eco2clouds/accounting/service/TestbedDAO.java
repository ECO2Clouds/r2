package eu.eco2clouds.accounting.service;

import java.util.List;

import eu.eco2clouds.accounting.datamodel.Host;
import eu.eco2clouds.accounting.datamodel.HostData;
import eu.eco2clouds.accounting.datamodel.Testbed;

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
 * The Data Access Object for the location objects stored in the database,
 * relying on the hibernate library.
 * Spring injects this automatically when asked for TestbedDAO
 *
 */
public interface TestbedDAO extends DAO<Testbed> {

	/**
	 * Returns a Testbed from the database if it matches its name, <code>null</code> otherwise
	 * @param name
	 * @return
	 */
	public Testbed getByName(String name);
	
	/**
	 * Returns the Testbed from the database by its Id
	 * @param id of the Testbed
	 * @return the corresponding Testbed from the database
	 */
	public Testbed getById(int id);
	
	/**
	 * Returns all the Testbeds stored in the database
	 * @return all the Testbeds stored in the database
	 */
	public List<Testbed> getAll();
	
	/**
	 * Stores a Testbed into the database
	 * @param Testbed Testbed to be saved.
	 * @return <code>true</code> if the Testbed was saved correctly
	 */
	public boolean save(Testbed testbed);
	
	/**
	 * Updates a Testbed in the database
	 * @param Testbed Testbed to be updated
	 * @return <code>true</code> if the Testbed was saved correctly
	 */
	public boolean update(Testbed testbed);
	
	/**
	 * Deletes a Testbed from the database
	 * @param Testbed to be deleted
	 * @return <code>true</code> if the Testbed was deleted correctly
	 */
	public boolean delete(Testbed testbed);
	
	/**
	 * Finds a specific Testbed in the database
	 * @param Testbed Testbed to be found in the database
	 * @return the Testbed object extracted from the databese
	 */
	public Testbed find(Testbed testbed);
	
	/**
	 * Deletes a Host from the Database and the Testbed
	 * @param testbed
	 * @param host
	 * @return
	 */
	public Testbed deleteHost(Testbed testbed, Host host);

}
