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
 */
public interface HostDataDAO extends DAO<HostData> { 
		
	/**
	 * Returns the HostData from the database by its Id
	 * @param id of the HostData
	 * @return the corresponding HostData from the database
	 */
	public HostData getById(int id);
	
	/**
	 * Returns all the HostDatas stored in the database
	 * @return all the HostDatas stored in the database
	 */
	public List<HostData> getAll();
	
	/**
	 * Stores a HostData into the database
	 * @param HostData HostData to be saved.
	 * @return <code>true</code> if the HostData was saved correctly
	 */
	public boolean save(HostData hostData);
	
	/**
	 * Updates a HostData in the database
	 * @param HostData HostData to be updated
	 * @return <code>true</code> if the HostData was saved correctly
	 */
	public boolean update(HostData hostData);
	
	/**
	 * Deletes a HostData from the database
	 * @param HostData to be deleted
	 * @return <code>true</code> if the HostData was deleted correctly
	 */
	public boolean delete(HostData hostData);
	
	
	/**
	 * Stores a HostData into the database
	 * @param HostData HostData to be saved.
	 * @return <code>true</code> if the HostData was saved correctly
	 */
	boolean saveHostData (HostData hostData);

	/**
	 * Returns the last HostData entry from the database associated to that specific Host
	 * @param host from which we want to retrieve the last HostData
	 * @return the associated HostData
	 */
	public List<HostData> getLastEntry(Host host);
}