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

import eu.eco2clouds.accounting.datamodel.Host;
import eu.eco2clouds.accounting.datamodel.HostData;
import eu.eco2clouds.accounting.service.HostDataDAO;

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
@Service("HostDataService")
public class HostDataDAOJpa implements HostDataDAO {
	private static Logger logger = Logger.getLogger(HostDataDAOJpa.class);
	private EntityManager entityManager;

	@PersistenceContext(unitName = "schedulerDB")
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public HostData getById(int id) {
		return entityManager.find(HostData.class, id);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<HostData> getAll() {
		Query query = entityManager.createNamedQuery("HostData.findAll");
		List<HostData> hostDatas = null;
		hostDatas = query.getResultList();
		return hostDatas;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean save(HostData hostData) {
		entityManager.persist(hostData);
		entityManager.flush();
		return true;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean update(HostData hostData) {
		entityManager.merge(hostData);
		entityManager.flush();
		return true;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean delete(HostData hostData) {
		try {
			hostData = entityManager.getReference(HostData.class,
					hostData.getId());
			entityManager.remove(entityManager.merge(hostData));
			entityManager.flush();
			return true;
		} catch (EntityNotFoundException e) {
			logger.debug(e);
			return false;
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean saveHostData (HostData hostData) {
		
		try {
			
				
			Query query = entityManager.createNativeQuery("INSERT INTO host_data " +
					"(id,host_id,action_id,disk_usage,mem_usage,cpu_usage,max_disk," +
					"max_mem,max_cpu,free_disk,free_mem,free_cpu,used_disk,used_mem,"+
					"used_cpu,running_vms)" +
					" VALUES ("+ hostData.getId() +"," + hostData.getHost().getId() +"," + 
					 hostData.getAction().getId() +","+ hostData.getDiskUsage() +","+ hostData.getMemUsage()+","+
				     hostData.getCpuUsage() +","+ hostData.getMaxDisk() +","+ hostData.getMaxMem() +"," +
				     hostData.getMaxCpu() +","+hostData.getFreeDisk() +","+ hostData.getFreeMen() +"," +
				     hostData.getFreeCpu() +","+ hostData.getUsedDisk() +","+hostData.getUsedMem() +"," +
				     hostData.getUsedCpu() +"," + hostData.getRunningVms()+ ")"); 
		    
			query.executeUpdate();
		    return true;

		} catch (NoResultException e) {
			logger.debug("No Result found: " + e);
			return false;
		}
	}

	@Override
	public List<HostData> getLastEntry(Host host) {
		try {
			String hql = "FROM HostData HD WHERE HD.host = :host " +
                         "ORDER BY HD.id DESC ";
			Query query = entityManager.createQuery(hql);
			query.setParameter("host", host);
			query.setMaxResults(1);
			
			List<HostData> results = (List<HostData>) query.getResultList();
			return results;
		} catch (NoResultException e) {
			logger.debug("No result found: " + e);
			return null;
		}
	}
}
