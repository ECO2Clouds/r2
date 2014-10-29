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

import eu.eco2clouds.ac.ApplicationController;
import eu.eco2clouds.ac.LocalAgentsManager;
import eu.eco2clouds.ac.monitor.AbstractStatus;
import eu.eco2clouds.ac.monitor.VMStatus;
import java.util.HashMap;

/**
 *
 *  
 */
public abstract class Application implements Runnable {
    
    public static final int RUNNING = 0;

    public static final int STOPPED = 1;
            
    /**
	 * @uml.property  name="status"
	 */
    protected int status;
    
    /**
	 * @uml.property  name="controller"
	 * @uml.associationEnd  
	 */
    private ApplicationController controller;
    
    private LocalAgentsManager localAgentsManager;
    

    public int getStatus() {
        
        return status;
        
    }
    
    
    public void setStatus(int status) {
        
        this.status = status;
        
    }

    
    protected ApplicationController getApplicationController() {
        
        return this.controller;
        
    }

    
    public void setApplicationController(ApplicationController controller) {
                
        this.controller = controller;
        
    }

    protected LocalAgentsManager getLocalAgentsManager() {
    
        return localAgentsManager;
    
    }

    public void setLocalAgentsManager(LocalAgentsManager localAgentsManager) {

        this.localAgentsManager = localAgentsManager;
    
    }


}
