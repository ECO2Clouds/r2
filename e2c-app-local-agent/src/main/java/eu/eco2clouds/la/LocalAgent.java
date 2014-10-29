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
package eu.eco2clouds.la;

import java.util.logging.Logger;

/**
 *
 *  
 */
public class LocalAgent extends Thread {

    public final static int ALA_SERVER_PORT = 9998; // if modified this value then also modify the constant ApplicationController.ALA_SERVER_PORT
    
    private final static Logger LOGGER = Logger.getLogger(LocalAgent.class.getName());
    ServerManager server;
    private String resourcePkg;

    public LocalAgent(String resourcePkg, int port) {
        
        server = new ServerManager(port);

        this.resourcePkg = resourcePkg;
    }

    @Override
    public void start() {

        this.server.startServer(this.resourcePkg);

    }

    @Override
    public void run() {


        LOGGER.info("Stopping Grizzly...");

        this.server.stopServer();
    }

    public ServerManager getServer() {
        return server;
    }

    public void setServer(ServerManager server) {
        this.server = server;
    }

    protected String getResourcePkg() {
        return resourcePkg;
    }

    protected void setResourcePkg(String resourcePkg) {
        this.resourcePkg = resourcePkg;
    }
    
    public static void main(String[] args) {

        Configuration.loadProperties();

        String resources_package = Configuration.getProperties().getProperty(Configuration.RESOURCES_PACKAGE);
        
        LocalAgent localagent = new LocalAgent(resources_package, LocalAgent.ALA_SERVER_PORT);

        Runtime.getRuntime().addShutdownHook(localagent);

        localagent.start();

    }
    
   
}
