package eu.eco2clouds.accounting.rest.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import eu.eco2clouds.accounting.bonfire.BFClientAccounting;
import eu.eco2clouds.accounting.bonfire.BFClientAccountingImpl;
import eu.eco2clouds.accounting.datamodel.Experiment;
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
 */
public class UpdateExperimentStatusTask {

	private static Logger logger = Logger.getLogger(UpdateExperimentStatusTask.class);

	@Autowired
	private ExperimentDAO experimentDAO;
	private BFClientAccounting bfClient = new BFClientAccountingImpl();


	@Scheduled(cron = "${update.experiment.status}")
	public void updateExperimentsStatus()
	{
//		System.out.println("Method executed at every 30 seconds. Current time is :: "+ new Date());
//
//		System.out.println("UpdateExperimentsStatusJob says: Quartz works!!!");
//		logger.info("testing logger");
//		System.out.println("Current Time : " + Calendar.getInstance().getTime());
		List<Experiment> experiments = new ArrayList<Experiment>();

		//get running experiments
		Date date = new Date();
		long actualDate = date.getTime();

		experiments = this.experimentDAO
				.getListOfRunningExperiments(actualDate);

		//update status in database if required
		eu.eco2clouds.api.bonfire.occi.datamodel.Experiment bfExp;
		boolean updated = false;
		for (Experiment  e: experiments){
			Long bfireExpId = e.getBonfireExperimentId();
			String bfireUserId = e.getBonfireUserId();	
			//Get experiment information from BonFIRE
			bfExp = bfClient.getExperiment(bfireUserId, bfireExpId);			
			if (e.getStatus() == null || !bfExp.getStatus().equals(e.getStatus())){
				e.setStatus(bfExp.getStatus());
				updated = this.experimentDAO.update(e);
				if (!updated){
					logger.debug("Error updating experiment status in database. Experiment_id = " + bfireExpId); 
				}
			}			
		}		
	}
		
}
