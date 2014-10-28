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
import eu.eco2clouds.accounting.datamodel.Testbed;
import eu.eco2clouds.accounting.service.TestbedDAO;

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
@Service("TestbedService")
public class TestbedDAOJpa implements TestbedDAO {
	private static Logger logger = Logger.getLogger(TestbedDAOJpa.class);
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
	public Testbed getByName(String name) {
		try {
			Query query = entityManager.createNamedQuery("Testbed.findTestbedByName");
			query.setParameter("name", name);
			Testbed testbed = (Testbed) query.getSingleResult();
			return testbed;
		} catch(NoResultException e) {
			logger.debug("No Result found: " + e);
			return null;
		}
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public Testbed getById(int id) {
		return entityManager.find(Testbed.class, id);
	}

	@Override
	public List<Testbed> getAll() {
		Query query = entityManager.createNamedQuery("Testbed.findAll");
		List<Testbed> testbeds = null;
		testbeds = query.getResultList();
		return testbeds;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public boolean save(Testbed testbed) {
		entityManager.persist(testbed);
		entityManager.flush();
		return true;
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean update(Testbed testbed) {
		entityManager.merge(testbed);
		entityManager.flush();
		return true;
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean delete(Testbed testbed) {
		try {
			testbed = entityManager.getReference(Testbed.class, testbed.getId());
			entityManager.remove(testbed);
			entityManager.flush();
			return true;
		} catch(EntityNotFoundException e) {
			logger.debug(e);
			return false;
		} 
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public Testbed find(Testbed testbed) {
		if(testbed == null) 
			return null;
		else if(testbed.getName() == null)
			return null;
		else 
			return getByName(testbed.getName());
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public Testbed deleteHost(Testbed testbed, Host host) {
		List<Host> hosts = testbed.getHosts();
		
		for(Host hostInTestbed : hosts) {
			if(hostInTestbed.getId() == host.getId()) {
				entityManager.remove(entityManager.find(Host.class, host.getId()));
				testbed.getHosts().remove(hostInTestbed);
				break;
			}
		}
		
		return testbed;
	}
	
}
