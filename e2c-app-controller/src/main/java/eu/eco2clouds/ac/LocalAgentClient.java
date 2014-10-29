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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import eu.eco2clouds.ac.conf.INodeConfiguration;
import eu.eco2clouds.ac.conf.LocalAgentConfiguration;
import eu.eco2clouds.ac.monitor.AbstractStatus;
import java.net.URI;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

/**
 *
 *  
 */
public class LocalAgentClient {

    /**
	 * @uml.property  name="agentConfiguration"
	 * @uml.associationEnd  
	 */
    LocalAgentConfiguration agentConfiguration;

    /**
	 * @uml.property  name="nodeConfiguration"
	 * @uml.associationEnd  
	 */
    INodeConfiguration nodeConfiguration;
    
    public LocalAgentClient(LocalAgentConfiguration agentConfiguration) {

        this.agentConfiguration = agentConfiguration;

    }

    /**
	 * @return
	 * @uml.property  name="agentConfiguration"
	 */
    public LocalAgentConfiguration getAgentConfiguration() {
        return agentConfiguration;
    }

    /**
	 * @param  agentConfiguration
	 * @uml.property  name="agentConfiguration"
	 */
    public void setAgentConfiguration(LocalAgentConfiguration agentConfiguration) {
        this.agentConfiguration = agentConfiguration;
    }

    /**
	 * @return
	 * @uml.property  name="nodeConfiguration"
	 */
    public INodeConfiguration getNodeConfiguration() {
        return nodeConfiguration;
    }

    /**
	 * @param  nodeConfiguration
	 * @uml.property  name="nodeConfiguration"
	 */
    public void setNodeConfiguration(INodeConfiguration nodeConfiguration) {
        this.nodeConfiguration = nodeConfiguration;
    }
    
    public AbstractStatus getNodeStatus() {
        /* ask to the system the status */
        return null;
    }


    private void connect() {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource service = client.resource(getBaseURI());
        // Get plain text
        System.out.println(service.path("actions").accept(MediaType.APPLICATION_JSON).get(String.class));

    }

    private URI getBaseURI() {
        return UriBuilder.fromUri("http://" + this.agentConfiguration.getIpAddress() + ":" + Configuration.getProperties().getProperty(Configuration.ALA_SERVER_PORT)+"/").build();
    }
    
    
    public static void main(String args[]) {
        
        LocalAgentConfiguration lacConf = new LocalAgentConfiguration();
        lacConf.setIpAddress("127.0.0.1");
        
        LocalAgentClient lac = new LocalAgentClient(lacConf);
        
        lac.connect();
        
    }
}
