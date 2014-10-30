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

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;

/**
 *
 *  
 */

public class ServerManager {
    
    private final static Logger LOGGER = Logger.getLogger(ServerManager.class.getName()); 

    
    private HttpServer httpServer;
    private int port = 9998;
    
    public static final int STOPPED = 1;
    public static final int RUNNING = 2;
    
    private int status = ServerManager.STOPPED;
    
    public ServerManager(int port) {
        this.port = port;
    }
   
    private URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost").port(this.port).build();
    }
    
    public  final URI BASE_URI = getBaseURI();

    private void deployResource() {
        
    }
    
    public void startServer(String resourcePkg) {
        try {
            LOGGER.info("Starting Grizzly");
            ResourceConfig rc = new PackagesResourceConfig(resourcePkg);
            this.httpServer = GrizzlyServerFactory.createHttpServer("http://0.0.0.0:" + this.port, rc);
            
            while (this.status == ServerManager.STOPPED) {
                
            }
            
            
        } catch (IOException ex) {
            LOGGER.severe(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOGGER.severe(ex.getMessage());
        } catch (NullPointerException ex) {
            LOGGER.severe(ex.getMessage());
        }
    }

    public void stopServer() {
        this.setStatus(ServerManager.STOPPED);
        this.httpServer.stop();
        LOGGER.info("Stopping Grizzly");

    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    
    
}
