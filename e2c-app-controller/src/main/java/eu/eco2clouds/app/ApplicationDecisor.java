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
package eu.eco2clouds.app;

import eu.eco2clouds.ac.LocalAgentsManager;
import eu.eco2clouds.component.MonitoringManager;
import eu.eco2clouds.component.SchedulerManager;

/**
 *
 *  
 */
public abstract class ApplicationDecisor extends Application {
    
    
    /* it extends the application to resuse the link to the controller and the status management */

    /**
	 * @uml.property  name="localAgentsManager"
	 * @uml.associationEnd  
	 */
    private LocalAgentsManager localAgentsManager;
    
    /**
	 * @uml.property  name="schedulerManager"
	 * @uml.associationEnd  
	 */
    private SchedulerManager schedulerManager;
    
    /**
	 * @uml.property  name="monitoringManager"
	 * @uml.associationEnd  
	 */
    private MonitoringManager monitoringManager;
    
    /**
	 * @return
	 * @uml.property  name="localAgentsManager"
	 */
    public LocalAgentsManager getLocalAgentsManager() {
        return localAgentsManager;
    }

    /**
	 * @param  localAgentsManager
	 * @uml.property  name="localAgentsManager"
	 */
    public void setLocalAgentsManager(LocalAgentsManager localAgentsManager) {
        this.localAgentsManager = localAgentsManager;
    }

    /**
	 * @return
	 * @uml.property  name="schedulerManager"
	 */
    public SchedulerManager getSchedulerManager() {
        return schedulerManager;
    }

    /**
	 * @param  schedulerManager
	 * @uml.property  name="schedulerManager"
	 */
    public void setSchedulerManager(SchedulerManager schedulerManager) {
        this.schedulerManager = schedulerManager;
    }

    /**
	 * @return
	 * @uml.property  name="monitoringManager"
	 */
    public MonitoringManager getMonitoringManager() {
        return monitoringManager;
    }

    /**
	 * @param  monitoringManager
	 * @uml.property  name="monitoringManager"
	 */
    public void setMonitoringManager(MonitoringManager monitoringManager) {
        this.monitoringManager = monitoringManager;
    }

    
    public abstract void decide();

}
