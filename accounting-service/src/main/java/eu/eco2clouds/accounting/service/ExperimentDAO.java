package eu.eco2clouds.accounting.service;

import java.util.List;

import javax.ws.rs.core.Response;

import eu.eco2clouds.accounting.datamodel.Action;
import eu.eco2clouds.accounting.datamodel.Experiment;
import eu.eco2clouds.accounting.datamodel.VM;
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
 * Experiment DAO interface to retrieve Experiment objects from a Database
 */
public interface ExperimentDAO extends DAO<Experiment> {

	/**
	 * Returns the experiment from the database by its Id
	 * 
	 * @param id of the experiment
	 * @return the corresponding experiment from the database
	 */
	public Experiment getById(int id);

	/**
	 * Returns the experiment from the database by its experimentId
	 * 
	 * @param experiment_id of the experiment
	 * @return the corresponding experiment from the database
	 */
	public Experiment getExperimentId(long experiment_id);

	/**
	 * Returns experiments from the database by its groupId
	 * 
	 * @param groups of the experiment
	 * @return the corresponding experiment(s) with this set of groupId from the database
	 */
	public List<Experiment> getListExperimentByGroupId(List<String> groups);

	/**
	 * Returns the experiment from the database by its id and groupId
	 * 
	 * @param groups
	 *            of the experiment
	 * @return the corresponding experiment with a certain id and groupId from
	 *         the database
	 */
	public Experiment getExperimentByGroupId(int id, List<String> groups);

	public Experiment getListVMByGroupId(long experimentId, List<String> groups);

	public Experiment getVMId(long experimentId, int vmId);

	public Experiment getExperimentByVmBonfireUrl(long experimentId,String bonfire_url);

	/**
	 * Returns all the experiments stored in the database
	 * 
	 * @return all the experiments stored in the database
	 */
	public List<Experiment> getAll();

	/**
	 * @return the last experiment added to the database...
	 */
	public Experiment getLastExperiment();

	/**
	 * Stores a experiment into the database
	 * 
	 * @param experiment
	 *            experiment to be saved.
	 * @return <code>true</code> if the experiment was saved correctly
	 */
	public boolean save(Experiment experiment);

	/**
	 * Updates a experiment in the database
	 * 
	 * @param experiment
	 *            experiment to be updated
	 * @return <code>true</code> if the experiment was saved correctly
	 */
	public boolean update(Experiment experiment);

	/**
	 * Deletes a experiment from the database
	 * 
	 * @param experiment
	 *            to be deleted
	 * @return <code>true</code> if the experiment was deleted correctly
	 */
	public boolean delete(Experiment experiment);

	/**
	 * Deletes a Host from the VM from the Testbed
	 * 
	 * @param testbed
	 * @param vm
	 * @return
	 */
	public Experiment deleteVM(Experiment experiment, VM vm);

	/**
	 * Inserts action type into the database
	 * 
	 * @param groups
	 *            of the experiment
	 * @return the corresponding action type with a certain id from the database
	 */
	public int addExperiment(Experiment experiment);

	/**
	 * Update experiment from the database
	 * 
	 * @return the corresponding action type with a certain id from the database
	 */
	public int updateExperiment(Experiment experiment);

	/**
	 * Inserts action type into the database
	 * 
	 * @param groups
	 *            of the experiment
	 * @return the corresponding action type with a certain id from the database
	 */
	public int addAction(Action action, int vmId);

	/**
	 * Update experiment from the database
	 * 
	 * @return the corresponding action type with a certain id from the database
	 */
	public int updateAction(Action action);

	/**
	 * Inserts action type into the database
	 * 
	 * @param groups
	 *            of the experiment
	 * @return the corresponding action type with a certain id from the database
	 */
	public int addVM(VM vm, int experimentId);

	/**
	 * Update experiment from the database
	 * 
	 * @return the corresponding action type with a certain id from the database
	 */
	public int updateVM(VM vm, int experimentId);

	/**
	 * Inserts relation between vm and host in vm_host table
	 * 
	 * @return the corresponding vmHost from the database
	 */
	public int insertVmHost(int vmId, int hostId);

	/**
	 * Returns list of running experiments from the database
	 * 
	 * @param actual
	 *            date
	 * @return the corresponding experiment from the database
	 */
	public List<Experiment> getListOfRunningExperiments(long actualDate);

	/**
	 * Returns retrieve the experiment that it is actual running and has that VM with that IP from the database
	 * 
	 * @param actual date and VM IP
	 *         
	 * @return the corresponding experiment from the database
	 */
	public Experiment getExperimentWithVmIP(long actualDate, String vmIp);
}
