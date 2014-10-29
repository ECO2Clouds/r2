/* 
 * Copyright 2014 Politecnico di Milano.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

 *  @author: Pierluigi Plebani, Politecnico di Milano, Italy
 *  e-mail pierluigi.plebani@polimi.it

 */
package eu.eco2clouds.portal;

import eu.eco2clouds.accounting.datamodel.parser.Experiment;
import eu.eco2clouds.applicationProfile.datamodel.ApplicationProfile;
import eu.eco2clouds.applicationProfile.datamodel.Data;
import eu.eco2clouds.applicationProfile.datamodel.ExperimentDescriptor;
import eu.eco2clouds.applicationProfile.datamodel.Flow;
import eu.eco2clouds.applicationProfile.datamodel.Requirement;

/**
 *
 *  
 */
public class SessionStatus {

    public static final int UNLOGGED = 0;
    public static final int HOME = 1;
    public static final int SUBMITAP = 2;
    public static final int EXPERIMENT = 3;
    public static final int NOTIFICATIONS = 4;
 

    private int status;
    private String loggedUser;
    private String loggedGroup;
    
    private ApplicationProfile applicationProfile = new ApplicationProfile();
    
    private Experiment selectedExperiment = null;
    
    public SessionStatus() {
        applicationProfile.setExperimentDescriptor(new ExperimentDescriptor());
        applicationProfile.setFlow(new Flow());
        applicationProfile.setRequirement(new Requirement());
        applicationProfile.setData(new Data());
        
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(String loggedUser) {
        this.loggedUser = loggedUser;
    }

    public String getLoggedGroup() {
        return loggedGroup;
    }

    public void setLoggedGroup(String loggedGroup) {
        this.loggedGroup = loggedGroup;
    }
  
    public Experiment getSelectedExperiment() {
        return selectedExperiment;
    }

    public void setSelectedExperiment(Experiment selectedExperiment) {
        this.selectedExperiment = selectedExperiment;
    }
    
    public ApplicationProfile getApplicationProfile() {
        return applicationProfile;
    }

    public void setApplicationProfile(ApplicationProfile applicationProfile) {
        this.applicationProfile = applicationProfile;
    }
    
    
}
