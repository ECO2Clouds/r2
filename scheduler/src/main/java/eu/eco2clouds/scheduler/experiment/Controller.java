package eu.eco2clouds.scheduler.experiment;

import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import eu.eco2clouds.applicationProfile.datamodel.ExperimentDescriptor;
import eu.eco2clouds.scheduler.accounting.client.AccountingClient;
import eu.eco2clouds.scheduler.accounting.client.AccountingClientHC;
import eu.eco2clouds.accounting.datamodel.parser.Experiment;
import eu.eco2clouds.scheduler.em.EMClient;
import eu.eco2clouds.scheduler.em.EMClientHC;
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
 * 
 * Controls the different actions and experiment can have in BonfIRE. From submitting an experiment descriptor to store experiment details in the database
 * Employs Experiment Manager and BonFIRE APIs interfaces.
 * @author David Garcia Perez - AtoS
 *
 */
public class Controller {
	private static Logger logger = Logger.getLogger(Controller.class);
	protected EMClient emClient = new EMClientHC();
	protected AccountingClient acClient = new AccountingClientHC();
	protected long waitingTime = 5000l;
	
	public Controller() {}

	/**
	 * Submits an Experiment Descriptor
	 * @param userId of the user that submits the experiment descriptor
	 * @param ed the experimetn descriptor itself
	 * @return the Mannaged Experiment object with the just created experiment descriptor
	 */
	public ManagedExperiment submitExperimentDescriptor(String userId, ExperimentDescriptor ed) {
		ManagedExperiment me = emClient.submitExperiment(userId, ed, 0); 
		logger.info("Submitted Experiment Descriptor: " + me.getHref());
		return me;
	}

	/**
	 * Deploys an experiment descriptor in BonFIRe and waits until it is RUNNING and returns the BonFIRE experiment Id
	 * @param userId user that submits the experiment
	 * @param groupId groupId that submitted the experimetn
	 * @param ed the experiment descriptor
	 * @param aplicationProfile application profile that was initially submitted
	 * @return the BonFIRE experiment Id, returns -1 if the experiment was not successfully deployed... 
	 */
	public Experiment deployExperimentDescritor(String userId, String groupId, ExperimentDescriptor ed, String aplicationProfile) throws InterruptedException {
		Experiment experiment = new Experiment();
		experiment.setBonfireUserId(userId);
		experiment.setBonfireGroupId(groupId);
		experiment.setApplicationProfile(aplicationProfile);
		experiment.setBonfireExperimentId(-1l);
		experiment.setSubmittedExperimentDescriptor("");
		experiment.setEndTime(0l);
		experiment.setStartTime(0l);
		experiment.setManagedExperimentId(0l);
		experiment.setStatus("INIT");
		experiment = acClient.createExperiment(experiment);
		
		logger.debug("Created experiment in DB with id: " + experiment.getId());
		// First we create the experiment
		ManagedExperiment me = emClient.submitExperiment(userId, ed, experiment.getId());
		logger.debug("Created managed experiment: " + me);
		
		//We store in the accounting the system the submitted experiment
		experiment = acClient.updateExperiment(convertExperiment(experiment, me, ed));
		
		//TODO Add a timeout
		while(me.getStatus().equals("QUEUED") || me.getStatus().equals("DEPLOYABLE") || me.getStatus().equals("DEPLOYING")) {
			Thread.sleep(waitingTime);
			
			// We retrieve again the experiment from the Experiment Manager
			me = emClient.getExperiment(userId, me.getManagedExperimentId());
			logger.info("Submitted experiment still in status: "  + me.getStatus());
		}

		experiment.setBonfireExperimentId(new Long(me.getBonFIREExperimentId()));
		acClient.updateExperiment(experiment);
		
		return experiment;
	}

	protected Experiment convertExperiment(Experiment experiment, ManagedExperiment me, ExperimentDescriptor ed) {
		experiment.setBonfireExperimentId(new Long(me.getBonFIREExperimentId()));
		experiment.setManagedExperimentId(new Long(me.getManagedExperimentId()));
		
		if(ed != null) {
			try { 
				Date date = new Date();
				experiment.setStartTime(date.getTime());
				experiment.setEndTime(getEndTime(ed.getDuration()));
								
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, true);
				mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);

				String expDescString = mapper.writeValueAsString(ed);
				experiment.setSubmittedExperimentDescriptor(expDescString);
				
			} catch(JsonGenerationException e) {
				logger.warn(" Error parsing ED: " + e.toString());
			} catch(JsonMappingException e) {
				logger.warn(" Error parsing ED: " + e.toString());
			} catch(IOException e) {
				logger.warn(" Error parsing ED: " + e.toString());
			}
		}
		
		return experiment;
	}
	
	protected long getEndTime(long minutes) {
		Date date = new Date();
		
		return date.getTime() + minutes * 60 * 1000;
	}
}
