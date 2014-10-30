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
package eu.eco2clouds.ac;

import eu.eco2clouds.ac.action.IAction;
import eu.eco2clouds.ac.conf.INodeConfiguration;
import eu.eco2clouds.ac.conf.LocalAgentConfiguration;
import eu.eco2clouds.ac.monitor.VM;
import eu.eco2clouds.ac.monitor.AbstractStatus;
import eu.eco2clouds.applicationProfile.datamodel.ApplicationProfile;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the set of LocalAgents that are defined in the ApplicationProfile
 * @author  plebani
 */
public class LocalAgentsManager {

    //ApplicationProfile applicationProfile;
    
    List<VM> hosts = new LinkedList<VM>();

    HashMap<String, LocalAgentClient> localAgents = new HashMap<String, LocalAgentClient>();

    HashMap<String, INodeConfiguration> nodeConfiguration = new HashMap<String, INodeConfiguration>();

    public LocalAgentsManager(List<VM> hosts) throws ACException {
        
        /* reads the applicationProfile and creates the LocalAgentConfiguration */
        
        //this.applicationProfile = applicationProfile;
        //if (this.applicationProfile != null) {
            this.hosts = hosts;
        //} else {
        //    Logger.getLogger(LocalAgentsManager.class.getName()).log(Level.SEVERE, "Application Profile not defined");
        //    throw new ACException("Application Profile not defined");
        //}
        
    }

    public void configureLocalAgent(String ipAddress, LocalAgentConfiguration configuration) {

        localAgents.get(ipAddress).setAgentConfiguration(configuration);

    }

    public LocalAgentConfiguration getLocalAgentConfiguration(String ipAddress) {

        return localAgents.get(ipAddress).getAgentConfiguration();

    }
    
    public void setNodeConfiguration(String ipAddress, INodeConfiguration nodeConfiguration) {
        
        localAgents.get(ipAddress).setNodeConfiguration(nodeConfiguration);
        
    }

    
    public INodeConfiguration getNodeConfiguration(String ipAddress) {
        
        return localAgents.get(ipAddress).getNodeConfiguration();
        
    }

    /**
	 * @param configurations
	 * @uml.property  name="nodeConfiguration"
	 */
    public void setNodeConfiguration(HashMap<String, INodeConfiguration> configurations) {
        
        for (Map.Entry<String, INodeConfiguration> configuration : configurations.entrySet()) {
         
            localAgents.get(configuration.getKey()).setNodeConfiguration(configuration.getValue());
            
        }
        
    }

    /**
	 * @return
	 * @uml.property  name="nodeConfiguration"
	 */
    public HashMap<String, INodeConfiguration> getNodeConfiguration() {
        
        HashMap<String, INodeConfiguration> configurations = new HashMap<String, INodeConfiguration>();
        
        for (Map.Entry<String, LocalAgentClient> agents : this.localAgents.entrySet()) {
         
            configurations.put(agents.getKey(), localAgents.get(agents.getKey()).getNodeConfiguration());                        
            
        }
        
        return configurations;
        
    }

    public AbstractStatus getNodeStatus(String ipAddress) {
        
        return localAgents.get(ipAddress).getNodeStatus();
        
    }


    public HashMap<String, AbstractStatus> getNodeStatus() {
        
        HashMap<String, AbstractStatus> status = new HashMap<String, AbstractStatus>();
        
        for (Map.Entry<String, LocalAgentClient> agents : this.localAgents.entrySet()) {
         
            status.put(agents.getKey(), localAgents.get(agents.getKey()).getNodeStatus());                        
            
        }
        
        return status;
        
    }

    /**
	 * @return
	 * @uml.property  name="nodes"
	 */
    public List<VM> getHosts() {
        return hosts;
    }
    
    
    public void enact(IAction action) {
        
    }


}
