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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 *  
 */
@Path("/status")
public class Status {

    private final static Logger LOGGER = Logger.getLogger(Status.class.getName());

    @GET
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"
    @Produces(MediaType.APPLICATION_XML)
    public EelsTaskStatus getActions() {

        EelsTaskStatus status = EelsTaskStatusFactory.getInstance();

        try {
            status.setThroughput(this.readThroughput());
        } catch (IOException ex) {
            Logger.getLogger(Status.class.getName()).log(Level.SEVERE, "file tp4ALA.log does not exist. return 0.0", ex);
            status.setThroughput(0.0);
        }

        try {
            status.setResponse_time(this.readResponseTime());
        } catch (IOException ex) {
            Logger.getLogger(Status.class.getName()).log(Level.SEVERE, "file rt4ALA.log does not exist. return 0.0", ex);
            status.setResponse_time(0);
        }

        try {
            status.setCpuutilization(this.readCpuUtilization());
        } catch (IOException ex) {
            Logger.getLogger(Status.class.getName()).log(Level.SEVERE, "file cpuutil4ALA.log does not exist. return 0.0", ex);
            status.setCpuutilization(0.0);
        }

        return EelsTaskStatusFactory.getInstance();
    }

    private double readThroughput() throws FileNotFoundException, IOException {
        BufferedReader br;
        br = new BufferedReader(new FileReader("/home/zabbix/eels/tp4ALA.log"));
        String line = br.readLine(); //file supposes to have 1 line
        br.close();

        if (line == null) {
            return 0.0;
        } else {
            return Double.parseDouble(line);
        }
    }

    private long readResponseTime() throws FileNotFoundException, IOException {
        BufferedReader br;
        br = new BufferedReader(new FileReader("/home/zabbix/eels/rt4ALA.log"));
        String line = br.readLine(); //file supposes to have 1 line
        br.close();

        if (line == null) {
            return 0;
        } else {
            return Long.parseLong(line);
        }
    }

    private double readCpuUtilization() throws FileNotFoundException, IOException {
        BufferedReader br;
        br = new BufferedReader(new FileReader("/home/zabbix/eels/cpuutil4ALA.log"));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine(); //file supposes to have 1 line
        br.close();

        if (line == null) {
            return 0.0;
        } else {
            return Double.parseDouble(line);
        }
    }

}
