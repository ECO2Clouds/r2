package eu.eco2clouds.accounting.service.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.eco2clouds.accounting.datamodel.Action;
import eu.eco2clouds.accounting.datamodel.ActionType;
import eu.eco2clouds.accounting.datamodel.Experiment;
import eu.eco2clouds.accounting.service.ActionTypeDAO;

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
@Service("ActionTypeService")
public class ActionTypeDAOJpa implements ActionTypeDAO {
	private static Logger logger = Logger.getLogger(ActionTypeDAOJpa.class);
	private EntityManager entityManager;

	@PersistenceContext (unitName = "schedulerDB")
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public ActionType getByName(String name) {
		try {
			Query query = entityManager.createNamedQuery("ActionType.findActionTypeByName");
			query.setParameter("name", name);
			ActionType actionType = (ActionType) query.getSingleResult();
			return actionType;
		} catch(NoResultException e) {
			logger.debug("No Result found: " + e);
			return null;
		}
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public ActionType getById(int id) {
		return entityManager.find(ActionType.class, id);
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public List<ActionType> getAll() {
		Query query = entityManager.createNamedQuery("ActionType.findAll");
		List<ActionType> actionTypes = null;
		actionTypes = query.getResultList();
		return actionTypes;
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean save(ActionType actionType) {
		entityManager.persist(actionType);
		entityManager.flush();
		return true;
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean update(ActionType actionType) {
		entityManager.merge(actionType);
		entityManager.flush();
		return true;
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean delete(ActionType actionType) {
		try {
			actionType = entityManager.getReference(ActionType.class, actionType.getId());
			entityManager.remove(actionType);
			entityManager.flush();
			return true;
		} catch(EntityNotFoundException e) {
			logger.debug(e);
			return false;
		} 
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public ActionType find(ActionType actionType) {
		if(actionType == null) 
			return null;
		else if(actionType.getName() == null)
			return null;
		else 
			return getByName(actionType.getName());
	}
	

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public ActionType getActionTypeById(int id) {

		try {
			Query query = entityManager
					.createNamedQuery("ActionType.findActionTypeById");
			query.setParameter("id", id);

			
			ActionType actionType = null;
			actionType = (ActionType) query.getSingleResult();
			return actionType;

		}  catch(NoResultException e) {
			logger.debug("No Result found: " + e);
			return null;
		}

	}
	
	

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int addActionType (ActionType actionType){
		
		try {
			
			
			Query query = entityManager.createNativeQuery("INSERT INTO action_type (name) " +
					    " VALUES('" + actionType.getName() +  "')");
				  
			int result = query.executeUpdate();
		
			return result;

		}  catch(NoResultException e) {
			logger.debug("No Result found: " + e);
			return -1;
		}
		
	}
	
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int updateActionType (ActionType actionType) {
		
		try {
			
			
			Query query = entityManager.createNativeQuery("UPDATE action_type p " +
					    " SET p.name='" +  actionType.getName() + "' WHERE p.id= " + actionType.getId() + "");
			
		  
			int result = query.executeUpdate();
		
			return result;

		}  catch(NoResultException e) {
			logger.debug("No Result found: " + e);
			return -1;
		}
		
	}
	
		
}
