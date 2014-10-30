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
package eu.eco2clouds.experiment.eels.task;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import eu.eco2clouds.ac.Configuration;
import eu.eco2clouds.experiment.eels.EelsApplication;
import eu.eco2clouds.experiment.eels.EelsDecisor;
import eu.eco2clouds.experiment.eels.task.status.EelsTaskStatus;
import eu.eco2clouds.experiment.eels.task.conf.InitDat;
import eu.eco2clouds.portal.service.data.NotificationSenderFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;

/**
 *
 *  
 */
public class EelsSimulationTask implements Runnable {

    private final static Logger LOGGER = Logger.getLogger(EelsDecisor.class.getName());
    private String server;
    private EelsTaskStatus taskStatus;
    private EelsApplication app;

    public EelsSimulationTask(String server, EelsTaskStatus taskStatus, EelsApplication app) {

        this.server = server;
        this.taskStatus = taskStatus;
        this.app = app;

    }

    private void informApplication() {

        Logger.getLogger(EelsSimulationTask.class.getName()).log(Level.INFO, "I am " + this.server + " and I ended to compute year " + taskStatus.getStart_year());
        NotificationSenderFactory.getInstance().send("Compute", "ALA server " + this.server + " computed "  + taskStatus.getStart_year());

        app.setFinisher(this.server);
    }

    public void run() {

        Logger.getLogger(EelsSimulationTask.class.getName()).log(Level.INFO, "starting ALA server at " + this.server);
        ManageALAServer.startALAServer(this.server);
        try {
            Thread.sleep(20000); // to be sure the server is up and running
        } catch (InterruptedException ex) {
            Logger.getLogger(EelsSimulationTask.class.getName()).log(Level.SEVERE, "error in sleep when starting ala server at " + this.server + " "
                    + ex.getMessage(), ex);
        }
        Logger.getLogger(EelsSimulationTask.class.getName()).log(Level.INFO, "started ALA server at " + this.server);
        NotificationSenderFactory.getInstance().send("Start", "Start ALA Server " + this.server + " to compute year " + this.taskStatus.getStart_year());

        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource service = client.resource("http://" + this.server + ":" + Configuration.getProperties().getProperty(Configuration.ALA_SERVER_PORT));

        InitDat initdat = new InitDat();
        initdat.setNeel(this.taskStatus.getNeels());
        initdat.setYear_start(this.taskStatus.getStart_year());
        initdat.setYear_stop(this.taskStatus.getStop_year());
        initdat.setIp_code(this.taskStatus.getOd_ip());

        System.out.println("connection to http://" + this.server + ":" + Configuration.getProperties().getProperty(Configuration.ALA_SERVER_PORT));
        service.path("setup").type(MediaType.APPLICATION_XML).post(String.class, initdat);

        service.path("start").type(MediaType.APPLICATION_XML).post();

        while (!taskStatus.isFinish()) {
            try {
                taskStatus = service.path("status").get(EelsTaskStatus.class);
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(EelsSimulationTask.class.getName()).log(Level.SEVERE, "error in sleep when getting data at " + this.server + " "
                        + ex.getMessage(), ex);
            }
        }

        client.destroy();
        try {
            Thread.sleep(30000); //waiting foe sending all the info to the AC
        } catch (InterruptedException ex) {
            Logger.getLogger(EelsSimulationTask.class.getName()).log(Level.SEVERE, "error in sleep when stopping ala server at " + this.server + " "
                    + ex.getMessage(), ex);
        }
        Logger.getLogger(EelsSimulationTask.class.getName()).log(Level.INFO, "stopping ALA server at " + this.server);
        ManageALAServer.stopALAServer(this.server);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(EelsSimulationTask.class.getName()).log(Level.SEVERE, null, ex);
        }
        NotificationSenderFactory.getInstance().send("Stop", "Stop ALA Server " + this.server);

        this.informApplication();

    }

    public EelsTaskStatus getTaskStatus() {
        return taskStatus;
    }

}
