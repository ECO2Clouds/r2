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
import eu.eco2clouds.accounting.datamodel.VM;
import eu.eco2clouds.accounting.datamodel.VMHost;
import eu.eco2clouds.accounting.service.VMDAO;

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
@Service("VMDAOService")
public class VMDAOJpa implements VMDAO {
	private static Logger logger = Logger.getLogger(VMDAOJpa.class);
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
	public VM getById(int id) {
		return entityManager.find(VM.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public List<VM> getAll() {
		Query query = entityManager.createNamedQuery("VM.findAll");
		List<VM> vMs = null;
		vMs = query.getResultList();
		return vMs;
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean save(VM vM) {
		entityManager.persist(vM);
		entityManager.flush();
		return true;
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean update(VM vM) {
		entityManager.merge(vM);
		entityManager.flush();
		return true;
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean delete(VM vM) {
		try {
			vM = entityManager.getReference(VM.class, vM.getId());
			entityManager.remove(vM);
			entityManager.flush();
			return true;
		} catch(EntityNotFoundException e) {
			logger.debug(e);
			return false;
		}
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public VM deleteAction(VM vM, Action action) {
		List<Action> actions = vM.getActions();
		
		for(Action actionFromVM : actions) {
			if(actionFromVM.getId() == action.getId()) {
				entityManager.remove(entityManager.find(Action.class, action.getId()));
				vM.getActions().remove(actionFromVM);
				break;
			}
		}
		
		return vM;
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public VM deleteVMHost(VM vM, VMHost vMHost) {
		Set<VMHost> vMHosts = vM.getvMhosts();
		
		for(VMHost vMHostFromVM : vMHosts) {
			if(vMHostFromVM.getId() == vMHost.getId()) {
				entityManager.remove(entityManager.find(VMHost.class, vMHost.getId()));
				vM.getvMhosts().remove(vMHostFromVM);
				break;
			}
		}
		
		return vM;
	}
	
	
}
