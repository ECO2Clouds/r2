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
package eu.eco2clouds.experiment.eels.la.resource;

import eu.eco2clouds.experiment.eels.task.status.EelsTaskStatus;
import eu.eco2clouds.experiment.eels.task.status.EelsTaskStatusFactory;
import eu.eco2clouds.experiment.eels.task.conf.InitDat;
import eu.eco2clouds.experiment.eels.task.conf.InitParser;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 *  
 */
@Path("/setup")
public class Setup {
    
    private final static Logger LOGGER = Logger.getLogger(Setup.class.getName());

    @POST
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"
    @Produces(MediaType.TEXT_PLAIN)  
    @Consumes(MediaType.APPLICATION_XML)
    public String getActions(InitDat conf) {
        try {
            InitParser.writeFile(eu.eco2clouds.experiment.eels.la.Configuration.getProperties().getProperty(eu.eco2clouds.experiment.eels.la.Configuration.INITDATDIT_PROPNAME) + File.separator + "init.dat", conf);
            
            EelsTaskStatus eelsTaskStatus = EelsTaskStatusFactory.getInstance();
            eelsTaskStatus.setStart_year(conf.getYear_start());
            eelsTaskStatus.setStop_year(conf.getYear_stop());
            eelsTaskStatus.setOd_ip(conf.getIp_code());
            
            // Return some cliched textual content
            System.out.append("OK\n");
            return Long.toString(conf.getNeel());
        } catch (IOException ex) {
            LOGGER.severe(ex.getMessage());
            return "Error in storing configuration";
        }
    }
}
