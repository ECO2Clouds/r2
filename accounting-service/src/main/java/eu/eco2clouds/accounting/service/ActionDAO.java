package eu.eco2clouds.accounting.service;

import java.util.List;

import eu.eco2clouds.accounting.datamodel.Action;
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
public interface ActionDAO extends DAO<Action> {

	/**
	 * Returns the Action from the database by its Id
	 * 
	 * @param id of the Action
	 * @return the corresponding Action from the database
	 */
	public Action getById(int id);

	/**
	 * Returns all the Action stored in the database
	 * 
	 * @return all the Action stored in the database
	 */
	public List<Action> getAll();

	/**
	 * Stores a Action into the database
	 * 
	 * @param Action
	 *            Action to be saved.
	 * @return <code>true</code> if the ActionType was saved correctly
	 */
	public boolean save(Action action);

	/**
	 * Updates a Action in the database
	 * 
	 * @param Action
	 *            Action to be updated
	 * @return <code>true</code> if the Action was saved correctly
	 */
	public boolean update(Action action);

	/**
	 * Deletes a Action from the database
	 * 
	 * @param Action
	 *            to be deleted
	 * @return <code>true</code> if the Action was deleted correctly
	 */
	public boolean delete(Action action);

	public Action deleteHostData(Action action, HostData hostData);
	
	
}
