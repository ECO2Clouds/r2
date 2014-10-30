package eu.eco2clouds.accounting.service.jpa;

import java.util.List;
import java.util.Set;

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
import eu.eco2clouds.accounting.datamodel.Experiment;
import eu.eco2clouds.accounting.datamodel.HostData;
import eu.eco2clouds.accounting.service.ActionDAO;

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
@Service("ActionService")
public class ActionDAOJpa implements ActionDAO {
	private static Logger logger = Logger.getLogger(ActionDAOJpa.class);
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
	public Action getById(int id) {
		return entityManager.find(Action.class, id);
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public List<Action> getAll() {
		Query query = entityManager.createNamedQuery("Action.findAll");
		List<Action> actions = null;
		actions = query.getResultList();
		return actions;
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean save(Action action) {
		entityManager.persist(action);
		entityManager.flush();
		return true;
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean update(Action action) {
		entityManager.merge(action);
		entityManager.flush();
		return true;
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean delete(Action action) {
		try {
			action = entityManager.getReference(Action.class, action.getId());
			entityManager.remove(action);
			entityManager.flush();
			return true;
		} catch(EntityNotFoundException e) {
			logger.debug(e);
			return false;
		} 
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public Action deleteHostData(Action action, HostData hostData) {
		Set<HostData> hostDatas = action.getHostDatas();
		
		for(HostData hostDataFromAction : hostDatas) {
			if(hostDataFromAction.getId() == hostData.getId()) {
				entityManager.remove(entityManager.find(HostData.class, hostData.getId()));
				action.getHostDatas().remove(hostDataFromAction);
				break;
			}
		}
		
		return action;
	}
	
}
