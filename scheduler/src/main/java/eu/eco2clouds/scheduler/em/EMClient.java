package eu.eco2clouds.scheduler.em;

import java.util.List;

import eu.eco2clouds.applicationProfile.datamodel.ExperimentDescriptor;
import eu.eco2clouds.scheduler.em.datamodel.ManagedExperiment;

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
 */
public interface EMClient {

	/**
	 * Returns a list of experiments for an specific user
	 * @param userId User ID to authenticate to the experiments... 
	 * @return a list of the ECO2Clouds managed experiments
	 */
	public List<ManagedExperiment> listExperiments(String userId);
	
	/**
	 * Gets from the BonFIRE Experiment Manager API the details of an specific experiment
	 * @param userId User ID to authenticate to the experiments... 	
	 * @param experimentId Experiment Id to the which we want to access the information	
	 * @return the managed experiment object
	 */
	public ManagedExperiment getExperiment(String userId, int experimentId);
	
	/**
	 * Submits an Experiment Descriptor to the BonFIRE Experiment Manager
	 * @param userId of the user submitting the Experiment Descriptor
	 * @param ed Experiment Descriptor
	 * @param experimentId ECO2Clouds Experiment Id
	 * @return message comming from the Experiment Manager
	 */
	public ManagedExperiment submitExperiment(String userId, ExperimentDescriptor ed, long experimentId);
}
