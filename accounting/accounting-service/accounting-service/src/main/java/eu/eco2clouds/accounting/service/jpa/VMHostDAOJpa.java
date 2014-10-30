package eu.eco2clouds.accounting.service.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.eco2clouds.accounting.datamodel.Experiment;
import eu.eco2clouds.accounting.datamodel.VMHost;
import eu.eco2clouds.accounting.service.VMHostDAO;

@Service("VMHostDAOService")
public class VMHostDAOJpa implements VMHostDAO {
	private static Logger logger = Logger.getLogger(VMHostDAOJpa.class);
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
	public VMHost getById(int id) {
		return entityManager.find(VMHost.class, id);
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public List<VMHost> getAll() {
		Query query = entityManager.createNamedQuery("VMHost.findAll");
		List<VMHost> vMHosts = null;
		vMHosts = query.getResultList();
		return vMHosts;
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean save(VMHost vMHost) {
		entityManager.persist(vMHost);
		entityManager.flush();
		return true;
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean update(VMHost vMHost) {
		entityManager.merge(vMHost);
		entityManager.flush();
		return true;
	}

	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean delete(VMHost vMHost) {
		try {
			vMHost = entityManager.getReference(VMHost.class, vMHost.getId());
			entityManager.remove(vMHost);
			entityManager.flush();
			return true;
		} catch(EntityNotFoundException e) {
			logger.debug(e);
			return false;
		}
	}
	
	@Override
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public VMHost getByVmId(int vmId) {
		Query query = entityManager.createNamedQuery("VMHost.getByVmId");
		query.setParameter("vmId", vmId);
		VMHost vMHost = null;
		//VMHost = (VMHost) query.getSingleResult();
		
		List results =  query.getResultList();
		
		if(!results.isEmpty()){
		    // ignores multiple results
			vMHost =  (VMHost) results.get(0);
		}
		
		return vMHost;
	}
}
