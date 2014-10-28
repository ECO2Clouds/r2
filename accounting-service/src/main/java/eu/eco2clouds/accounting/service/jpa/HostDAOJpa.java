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

import eu.eco2clouds.accounting.datamodel.Host;
import eu.eco2clouds.accounting.datamodel.HostData;
import eu.eco2clouds.accounting.service.HostDAO;

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
@Service("HostService")
public class HostDAOJpa implements HostDAO {
	private static Logger logger = Logger.getLogger(HostDAOJpa.class);
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
	public Host getByName(String name) {
		try {
			Query query = entityManager.createNamedQuery("Host.findHostByName");
			query.setParameter("name", name);
			Host host = (Host) query.getSingleResult();
			return host;
		} catch(NoResultException e) {
			logger.debug("No Result found: " + e);
			return null;
		}
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public Host getById(int id) {
		return entityManager.find(Host.class, id);
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public List<Host> getAll() {
		Query query = entityManager.createNamedQuery("Host.findAll");
		List<Host> hosts = null;
		hosts = query.getResultList();
		return hosts;
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean save(Host host) {
		entityManager.persist(host);
		entityManager.flush();
		return true;
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean update(Host host) {
		entityManager.merge(host);
		entityManager.flush();
		return true;
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean delete(Host host) {
		try {
			host = entityManager.getReference(Host.class, host.getId());
			entityManager.remove(entityManager.merge(host));
			entityManager.flush();
			return true;
		} catch(EntityNotFoundException e) {
			logger.debug(e);
			return false;
		} 
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public Host find(Host host) {
		if(host == null) 
			return null;
		else if(host.getName() == null)
			return null;
		else 
			return getByName(host.getName());
	}
	
	
	
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean saveHost (Host host, int testbed_id) {
		
		try {
			
			System.out.println("@@@@@@@@@testbed_id" + testbed_id);
				
			Query query = entityManager.createNativeQuery("INSERT INTO host (id,testbed_id,state,name,connected) " +
					"VALUES (" + host.getId() + "," + testbed_id + "," + host.getState()+ ",'" +host.getName() + "'," + host.isConnected()+ ")"); 
		    query.executeUpdate();
		    return true;

		} catch (NoResultException e) {
			logger.debug("No Result found: " + e);
			return false;
		}
	}
}
