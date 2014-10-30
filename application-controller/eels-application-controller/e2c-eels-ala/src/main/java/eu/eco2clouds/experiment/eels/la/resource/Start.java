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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 *
 *  
 */
@Path("/start")
public class Start {

    @POST
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"
    public void startTask() {
        try {

            FileOutputStream writer = new FileOutputStream("/home/zabbix/eels/responsetime.log");

            long start_time = System.currentTimeMillis();

            InitDat initdat = InitParser.readFile(eu.eco2clouds.experiment.eels.la.Configuration.getProperties().getProperty(eu.eco2clouds.experiment.eels.la.Configuration.INITDATDIT_PROPNAME) + File.separator + "init.dat");

            EelsTaskStatus eelsTaskStatus = EelsTaskStatusFactory.getInstance();

            eelsTaskStatus.setFinish(false);
            int startyear = eelsTaskStatus.getStart_year();
            int stopyear = eelsTaskStatus.getStop_year();

            System.out.println("Task started [" + startyear + "-" + stopyear + "]");

            for (int i = startyear; i <= stopyear; i++) {

                long start_time_year = System.currentTimeMillis();
                eelsTaskStatus.setCurrent_year(i);

                System.out.println("Iteration " + (i));

                String ipAddressOD = eelsTaskStatus.getOd_ip();
                
                initdat.setYear_start(i);
                initdat.setYear_stop(i + 1);

                InitParser.writeFile(eu.eco2clouds.experiment.eels.la.Configuration.getProperties().getProperty(eu.eco2clouds.experiment.eels.la.Configuration.INITDATDIT_PROPNAME) + File.separator + "init.dat", initdat);

                Runtime rt = Runtime.getRuntime();

                System.out.println("Executing command: " + eu.eco2clouds.experiment.eels.la.Configuration.getProperties().getProperty(eu.eco2clouds.experiment.eels.la.Configuration.SIMULATIONDIR_PROPNAME) + File.separator + "javarun.sh " + ipAddressOD + " " + initdat.getYear_start() + " " + initdat.getNeel() + " " + 1 + " " + "/home/zabbix/eels");

                Process pr = rt.exec(eu.eco2clouds.experiment.eels.la.Configuration.getProperties().getProperty(eu.eco2clouds.experiment.eels.la.Configuration.SIMULATIONDIR_PROPNAME) + File.separator + "javarun.sh " + ipAddressOD + " " + initdat.getYear_start() + " " + initdat.getNeel() + " " + 1 + " " + "/home/zabbix/eels");

                pr.waitFor();

                System.out.println("End Iteration " + (i - startyear));

                long response_time_year = System.currentTimeMillis() - start_time_year;

                if (writer != null) {
                    synchronized (writer) {
                        writer.write((Long.toString(response_time_year) + "\n").getBytes());
                        writer.flush();
                    }
                } else {
                    Logger.getLogger(Start.class.getName()).log(Level.INFO, "TP file not available");

                }


            }

            System.out.println("Task ended");

            eelsTaskStatus.setResponse_time(System.currentTimeMillis() - start_time);

            if (writer != null) {
                writer.close();
            }

            eelsTaskStatus.setFinish(true);

        } catch (InterruptedException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }


    }
}
