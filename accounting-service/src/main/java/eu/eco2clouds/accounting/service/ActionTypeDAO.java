package eu.eco2clouds.accounting.service;

import java.util.List;

import eu.eco2clouds.accounting.datamodel.ActionType;

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
public interface ActionTypeDAO extends DAO<ActionType> {

	/**
	 * Returns a ActionType by its name
	 * 
	 * @param name
	 *            Name of the ActionType
	 * @return The ActionType from the database
	 */
	public ActionType getByName(String name);

	/**
	 * Returns the ActionType from the database by its Id
	 * 
	 * @param id of the ActionType
	 * @return the corresponding ActionType from the database
	 */
	public ActionType getById(int id);

	/**
	 * Returns all the ActionTypes stored in the database
	 * 
	 * @return all the ActionTypes stored in the database
	 */
	public List<ActionType> getAll();

	/**
	 * Stores a ActionType into the database
	 * 
	 * @param ActionType
	 *            ActionType to be saved.
	 * @return <code>true</code> if the ActionType was saved correctly
	 */
	public boolean save(ActionType actionType);

	/**
	 * Updates a ActionType in the database
	 * 
	 * @param ActionType
	 *            ActionType to be updated
	 * @return <code>true</code> if the ActionType was saved correctly
	 */
	public boolean update(ActionType actionType);

	/**
	 * Deletes a ActionType from the database
	 * 
	 * @param ActionType
	 *            to be deleted
	 * @return <code>true</code> if the ActionType was deleted correctly
	 */
	public boolean delete(ActionType actionType);

	/**
	 * Finds a specific ActionType in the database
	 * 
	 * @param ActionType
	 *            ActionType to be found in the database
	 * @return the ActionType object extracted from the databese
	 */
	public ActionType find(ActionType actionType);
	
	
	/**
	 * Returns action type from the database by its id and groupId
	 * @param groups of the experiment
	 * @return the corresponding action type with a certain id from the database
	 */
	public ActionType getActionTypeById(int id);
	
	
	/**
	 * Returns action type from the database
	 * @return the corresponding action type with a certain id from the database
	 */
	public int addActionType (ActionType actionType);
	
	
	/**
	 * Updates action type from the database
	 * @return the corresponding action type updated
	 */
	public int updateActionType (ActionType actionType);
	
	
}
