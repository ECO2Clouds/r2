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
import eu.eco2clouds.accounting.datamodel.ActionType;
import eu.eco2clouds.accounting.datamodel.Experiment;
import eu.eco2clouds.accounting.datamodel.VM;
import eu.eco2clouds.accounting.service.ExperimentDAO;

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
 *
 * The Data Access Object for the location objects stored in the database,
 * relying on the hibernate library. Spring injects this automatically when
 * asked for LocationDAO
 */
@Service("ExperimentService")
public class ExperimentDAOJpa implements ExperimentDAO {
	private static Logger logger = Logger.getLogger(ExperimentDAOJpa.class);
	private EntityManager entityManager;

	@PersistenceContext(unitName = "schedulerDB")
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Experiment getById(int id) {
		return entityManager.find(Experiment.class, id);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<Experiment> getAll() {
		Query query = entityManager.createNamedQuery("Experiment.findAll");
		List<Experiment> experiments = null;
		experiments = query.getResultList();
		return experiments;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Experiment getExperimentId(long experimentId) {

		try {
			Query query = entityManager
					.createNamedQuery("Experiment.findExperimentId");
			query.setParameter("experimentId", experimentId);
			Experiment experiment = null;
			experiment = (Experiment) query.getSingleResult();
			return experiment;

		} catch (NoResultException e) {
			logger.debug("No Result found: " + e);
			return null;
		}

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Experiment getVMId(long experimentId, int vmId) {

		try {
			Query query = entityManager.createNamedQuery("Experiment.findVMId");
			query.setParameter("experimentId", experimentId);
			query.setParameter("vmId", vmId);
			Experiment experiment = null;
			experiment = (Experiment) query.getSingleResult();
			return experiment;

		} catch (NoResultException e) {
			logger.debug("No Result found: " + e);
			return null;
		}

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean save(Experiment experiment) {
		entityManager.persist(experiment);
		entityManager.flush();
		return true;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean update(Experiment experiment) {
		entityManager.merge(experiment);
		entityManager.flush();
		return true;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean delete(Experiment experiment) {
		try {
			experiment = entityManager.getReference(Experiment.class,
					experiment.getId());
			entityManager.remove(experiment);
			entityManager.flush();
			return true;
		} catch (EntityNotFoundException e) {
			logger.debug(e);
			return false;
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Experiment deleteVM(Experiment experiment, VM vm) {
		Set<VM> vMs = experiment.getvMs();

		for (VM vM : vMs) {
			if (vM.getId() == vm.getId()) {
				entityManager.remove(entityManager.find(VM.class, vm.getId()));
				experiment.getvMs().remove(vM);
				break;
			}
		}

		return experiment;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<Experiment> getListExperimentByGroupId(List<String> groups) {

		try {
			Query query = entityManager
					.createNamedQuery("Experiment.findListExperimentByGroups");
			query.setParameter("groups", groups);
			List<Experiment> experiments = null;
			experiments = query.getResultList();
			return experiments;

		} catch (NoResultException e) {
			logger.debug("No Result found: " + e);
			return null;
		}

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Experiment getExperimentByGroupId(int id, List<String> groups) {

		try {
			Query query = entityManager
					.createNamedQuery("Experiment.findExperimentByGroups");
			query.setParameter("id", id);
			query.setParameter("groups", groups);

			Experiment experiment = null;
			experiment = (Experiment) query.getSingleResult();
			return experiment;

		} catch (NoResultException e) {
			logger.debug("No Result found: " + e);
			return null;
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Experiment getListVMByGroupId(long experimentId, List<String> groups) {

		try {
			Query query = entityManager
					.createNamedQuery("Experiment.findListVMByGroups");
			query.setParameter("experimentId", experimentId);
			query.setParameter("groups", groups);
			Experiment exp = null;
			exp = (Experiment) query.getSingleResult();
			return exp;

		} catch (NoResultException e) {
			logger.debug("No Result found: " + e);
			return null;
		}

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Experiment getExperimentByVmBonfireUrl(long experimentId,
			String vmBonfireUrl) {

		try {
			Query query = entityManager
					.createNamedQuery("Experiment.findVMByBonfireUrl");
			query.setParameter("experimentId", experimentId);
			query.setParameter("vmBonfireUrl", vmBonfireUrl);
			Experiment experiment = null;
			experiment = (Experiment) query.getSingleResult();
			return experiment;

		} catch (NoResultException e) {
			logger.debug("No Result found: " + e);
			return null;
		}

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int addExperiment(Experiment experiment) {

		try {

			Query query = entityManager
					.createNativeQuery("INSERT INTO experiments (bonfire_group_id, bonfire_user_id, bonfire_experiment_id, start_time,end_time,application_profile, submitted_experiment_descriptor) "
							+ " VALUES('"
							+ experiment.getBonfireGroupId()
							+ "','"
							+ experiment.getBonfireUserId()
							+ "',"
							+ experiment.getBonfireExperimentId()
							+ ","
							+ experiment.getStartTime()
							+ ","
							+ experiment.getEndTime()
							+ ",'"
							+ experiment.getApplicationProfile()
							+ "','"
							+ experiment.getSubmittedExperimentDescriptor()
							+ "')");

			int result = query.executeUpdate();

			return result;

		} catch (NoResultException e) {
			logger.debug("No Result found: " + e);
			return -1;
		}

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int updateExperiment(Experiment experiment) {

		try {

			Query query = entityManager
					.createNativeQuery("UPDATE experiments p  "
							+ " SET p.bonfire_group_id='" + experiment.getBonfireGroupId()+ "', " 
							+ "p.bonfire_user_id='" + experiment.getBonfireUserId()+ "', " 
							+ "p.bonfire_experiment_id=" + experiment.getBonfireExperimentId()+ ", " 
							+ "p.start_time=" + experiment.getStartTime()+ ", " 
							+ "p.end_time=" + experiment.getEndTime()+ ", " 
							+ "p.application_profile='" + experiment.getApplicationProfile()+ "', "  
							+ "p.submitted_experiment_descriptor='" + experiment.getSubmittedExperimentDescriptor()+ "' "
							+" WHERE p.id= " + experiment.getId() + "");
												
		    return query.executeUpdate();

		} catch (NoResultException e) {
			logger.debug("No Result found: " + e);
			return -1;
		}

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int addVM(VM vm, int experimentId) {

		try {

			Query query = entityManager
					.createNativeQuery("INSERT INTO vm (experiment_id, bonfire_url) "
							+ " VALUES("
							+ experimentId + ",'"
							+ vm.getBonfireUrl() + "')");

			int result = query.executeUpdate();

			return result;

		} catch (NoResultException e) {
			logger.debug("No Result found: " + e);
			return -1;
		}

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int updateVM(VM vm, int experimentId) {

		try {

			Query query = entityManager.createNativeQuery("UPDATE vm p  "
					+ " SET p.experiment_id=" + experimentId + ","
					+ "p.bonfire_url='" + vm.getBonfireUrl() + "'"
					+ " WHERE p.id= " + vm.getId() + "");

			return query.executeUpdate();

		} catch (NoResultException e) {
			logger.debug("No Result found: " + e);
			return -1;
		}

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int addAction(Action action, int vmId) {

		try {

			System.out.println("####" + action.getActionType().getId() + ","
					+ vmId + "," + action.getTimestamp() + ","
					+ action.getActionLog());

			Query query = entityManager
					.createNativeQuery("INSERT INTO action (action_type_id, vm_id, timestamp, action_log) "
							+ " VALUES ("
							+ action.getActionType().getId()
							+ ","
							+ vmId
							+ ","
							+ action.getTimestamp()
							+ ",'"
							+ action.getActionLog() + "')");

			int result = query.executeUpdate();

			return result;

		} catch (NoResultException e) {
			logger.debug("No Result found: " + e);
			return -1;
		}

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int updateAction(Action action) {

		try {

			StringBuilder queryString = new StringBuilder();

			queryString.append("UPDATE action p ");
			queryString.append(" SET ");
			if (action.getTimestamp() != 0)
				queryString.append(" p.timeStamp=" + action.getTimestamp());
			if (action.getActionLog() != null)
				queryString.append(" p.actionLog='" + action.getActionLog()
						+ "'");
			queryString.append(" WHERE p.id=" + action.getId() + "");

			Query query = entityManager.createNativeQuery(queryString
					.toString());

			return query.executeUpdate();

		} catch (NoResultException e) {
			logger.debug("No Result found: " + e);
			return -1;
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int insertVmHost(int vmId, int hostId) {

		try {

			Query query = entityManager.createNativeQuery("INSERT INTO vm_host (host_id, vm_id, timestamp) " +
					  " VALUES(" + hostId + "," +vmId +  ",  'SELECT UNIX_TIMESTAMP()')");
			
		    return query.executeUpdate();


		} catch (NoResultException e) {
			logger.debug("No Result found: " + e);
			return -1;
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Experiment getLastExperiment() {
		Query query = entityManager.createQuery("SELECT p FROM Experiment p order by id DESC");
		query.setMaxResults(1);
		return (Experiment) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<Experiment> getListOfRunningExperiments(long actualDate) {

		try {
			
			
			Query query = entityManager
					.createNamedQuery("Experiment.getRunningExperiments");
			query.setParameter("actualDate", actualDate);

			List<Experiment> experiments = null;
			experiments = (List<Experiment>) query.getResultList();
			return experiments;
			
		} catch (NoResultException e) {
			logger.debug("No Result found: " + e);
			return null;
		}
	}

	
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Experiment getExperimentWithVmIP(long actualDate, String vmIp) {

		try {
			
			
			Query query = entityManager.createNamedQuery("Experiment.getExperimentWithVmIP");
			query.setParameter("actualDate", actualDate);
			query.setParameter("vmIp", vmIp);

			Experiment experiment = null;
			experiment = (Experiment) query.getSingleResult();
			return experiment;
			
		} catch (NoResultException e) {
			logger.debug("No Result found: " + e);
			return null;
		}
	}
}
