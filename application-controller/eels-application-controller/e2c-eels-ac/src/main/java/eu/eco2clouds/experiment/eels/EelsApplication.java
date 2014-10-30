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
package eu.eco2clouds.experiment.eels;

import eu.eco2clouds.ac.ACException;
import eu.eco2clouds.ac.monitor.VMStatus;
import eu.eco2clouds.experiment.eels.task.EelsSimulationTask;
import eu.eco2clouds.app.Application;
import eu.eco2clouds.component.MonitoringManager;
import eu.eco2clouds.component.MonitoringManagerFactory;
import eu.eco2clouds.component.SchedulerManager;
import eu.eco2clouds.component.SchedulerManagerFactory;
import eu.eco2clouds.experiment.eels.task.status.EelsTaskStatus;
import eu.eco2clouds.experiment.eels.task.conf.InitParser;
import eu.eco2clouds.portal.service.data.NotificationSenderFactory;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *  
 */
public class EelsApplication extends Application {

    private final static Logger LOGGER = Logger.getLogger(EelsApplication.class.getName());
    InitParser parser = new InitParser();

    //static String finisher = "";
    boolean someoneFinish = false;

    FileOutputStream fResults;
    HashMap<String, ArrayList<Integer>> workload;
    ArrayList<Integer> years;
    HashMap<String, Thread> threads;
    HashMap<String, Integer> currentAssignement;

    public EelsApplication() {

        try {
            //load ac properties
            eu.eco2clouds.ac.Configuration.loadProperties();

            // load application properties
            Configuration.loadProperties();

            fResults = new FileOutputStream("/home/zabbix/eels/eelsadaptation" + eu.eco2clouds.ac.Configuration.getProperties().getProperty(eu.eco2clouds.ac.Configuration.EXPERIMENT_NO) + ".res");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EelsApplication.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // call by the threads to inform who finish
    public synchronized void setFinisher(String finisher) {

        try {
            Logger.getLogger(EelsApplication.class.getName()).log(Level.INFO, finisher + " finish");

            Logger.getLogger(EelsApplication.class.getName()).log(Level.INFO, "server " + finisher + " ends");

            fResults.write("server ".getBytes());
            fResults.write(finisher.getBytes());
            fResults.write(" ends\n".getBytes());

            ((EelsDecisor) this.getApplicationController().getDecisor()).setYears(years);
            workload = ((EelsDecisor) this.getApplicationController().getDecisor()).distributeWorkload();

            fResults.write("Calculate new workload\n".getBytes());
            StringBuffer s = new StringBuffer();
            for (String vms : workload.keySet()) {
                s.append(vms);
                s.append("->{");
                for (Integer y : workload.get(vms)) {
                    s.append(y.toString());
                    s.append(" ");
                }
                s.append("}\n");
            }
            fResults.write(s.toString().getBytes());

            if (workload.get(finisher) != null && workload.get(finisher).size() > 0) {

                EelsTaskStatus task = new EelsTaskStatus();
                task.setOd_ip(Configuration.getProperties().getProperty(Configuration.OCEANOGRAPHIC_DATA_IP));
                task.setStart_year(workload.get(finisher).get(0));
                task.setStop_year(workload.get(finisher).get(0));

                years.remove(workload.get(finisher).get(0));

                fResults.write(finisher.getBytes());
                fResults.write(" is starting year ".getBytes());
                fResults.write(Integer.toString(workload.get(finisher).get(0)).getBytes());
                fResults.write("\n".getBytes());

                currentAssignement.put(finisher, workload.get(finisher).get(0));

                task.setNeels(Long.parseLong(Configuration.getProperties().getProperty(Configuration.NEELS)));

                EelsSimulationTask experiment = new EelsSimulationTask(finisher, task, this);

                threads.put(finisher, new Thread(experiment));
                threads.get(finisher).start();
            } else {

                if (!Configuration.getProperties().getProperty(Configuration.OPTIMIZATION).equals(Configuration.OPT_OFF)) {

                    MonitoringManager mm = MonitoringManagerFactory.getInstance();
                    List<VMStatus> vmStatusList = mm.getVmStatusList();

                    for (VMStatus vmStatus : vmStatusList) {
                        if (vmStatus.getVM().getAlaServer().equals(finisher)) {
                            Logger.getLogger(EelsApplication.class.getName()).log(Level.INFO, "stopping VM " + finisher);
                            NotificationSenderFactory.getInstance().send("Stop", "Stop VM " + finisher);
                            SchedulerManager scheduler = SchedulerManagerFactory.getInstance();
                            scheduler.stopVM(vmStatus);
                            ((EelsDecisor) this.getApplicationController().getDecisor()).removeAlaServer(finisher);
                            fResults.write("stopping VM ".getBytes());
                            fResults.write(finisher.getBytes());
                            fResults.write("\n".getBytes());
                        }
                    }
                }
            }
            finisher = "";
            Logger.getLogger("years remained to assign " + years.size());
        } catch (IOException ex) {
            Logger.getLogger(EelsApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ACException ex) {
            Logger.getLogger(EelsApplication.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /*public String getFinisher() {

     return this.finisher;

     }*/
    public void run() {

        try {

            NotificationSenderFactory.getInstance().send("Start", "Start eels application ");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");

            //fResults = new FileOutputStream("/home/zabbix/eels/eelsadaptation" + sdf.format(new Date()) + ".res");
            this.status = Application.RUNNING;

            //      long startTime = System.currentTimeMillis();
            int start_year = Integer.parseInt(Configuration.getProperties().getProperty(Configuration.START_YEAR));
            int stop_year = Integer.parseInt(Configuration.getProperties().getProperty(Configuration.STOP_YEAR));

            years = new ArrayList<Integer>();

            for (int i = start_year; i <= stop_year; i++) {
                years.add(i);
            }

            ((EelsDecisor) this.getApplicationController().getDecisor()).initialSetup(years);
            this.workload = ((EelsDecisor) this.getApplicationController().getDecisor()).distributeWorkload();
            
            fResults.write("Initial new workload\n".getBytes());

            StringBuffer s = new StringBuffer();
            for (String vms : workload.keySet()) {
                s.append(vms);
                s.append("->{");
                for (Integer y : workload.get(vms)) {
                    s.append(y.toString());
                    s.append(" ");
                }
                s.append("}\n");
            }
            fResults.write(s.toString().getBytes());

            threads = new HashMap<String, Thread>();

            currentAssignement = new HashMap<String, Integer>();

            //prepare the task for the initial run
            for (String node : workload.keySet()) {

                if (workload.get(node) != null && workload.get(node).size() > 0) {
                    EelsTaskStatus task = new EelsTaskStatus();
                    task.setOd_ip(Configuration.getProperties().getProperty(Configuration.OCEANOGRAPHIC_DATA_IP));
                    task.setStart_year(workload.get(node).get(0));
                    task.setStop_year(workload.get(node).get(0));

                    currentAssignement.put(node, workload.get(node).get(0));

                    // remove the year from the assignable years
                    years.remove(workload.get(node).get(0));

                    fResults.write(node.getBytes());
                    fResults.write(" is starting year ".getBytes());
                    fResults.write(Integer.toString(workload.get(node).get(0)).getBytes());
                    fResults.write("\n".getBytes());

                    ((EelsDecisor) this.getApplicationController().getDecisor()).setYears(years);

                    task.setNeels(Long.parseLong(Configuration.getProperties().getProperty(Configuration.NEELS)));
                    EelsSimulationTask experiment = new EelsSimulationTask(node, task, this);

                    threads.put(node, new Thread(experiment));
                    threads.get(node).start();
                }

            }


            while (years.size() > 0) {
                try {
                    Thread.sleep(5000);
                    Logger.getLogger(EelsApplication.class.getName()).log(Level.INFO, years.size() + " years remained to be assigned");

                } catch (InterruptedException ex) {
                    Logger.getLogger(EelsApplication.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }// wait until all the years have been assigned

            for (String node : threads.keySet()) {
                if (threads.get(node) != null) {
                    try {
                        threads.get(node).join();

                    } catch (InterruptedException ex) {
                        Logger.getLogger(EelsApplication.class
                                .getName()).log(Level.SEVERE, "error in waiting for threads on server " + node + " to finish.", ex);
                    }
                }
            }

            fResults.write("application terminated\n".getBytes());
            fResults.close();
            Logger
                    .getLogger(EelsApplication.class
                            .getName()).log(Level.INFO, "Application ends!!!!");
            NotificationSenderFactory.getInstance().send("Stop", "Stop eels application ");

            // waiting for the last measurements in applicationdecisor
            //Thread.sleep(180000);
            
            Logger.getLogger(EelsApplication.class.getName()).log(Level.INFO, "/home/root/copyresults.sh " + eu.eco2clouds.ac.Configuration.getProperties().getProperty(eu.eco2clouds.ac.Configuration.EXPERIMENT_NO) + " " + Configuration.getProperties().getProperty(Configuration.OCEANOGRAPHIC_DATA_IP));
            Runtime.getRuntime().exec("/root/eelsAC/copyresults.sh " + eu.eco2clouds.ac.Configuration.getProperties().getProperty(eu.eco2clouds.ac.Configuration.EXPERIMENT_NO) + " " + Configuration.getProperties().getProperty(Configuration.OCEANOGRAPHIC_DATA_IP));

            
            this.status = Application.STOPPED;

            
            this.getApplicationController()
                    .notifyApplicationStatus(this.status);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EelsApplication.class
                    .getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } catch (IOException ex) {
            Logger.getLogger(EelsApplication.class
                    .getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public int getStatus() {

        return this.status;
    }

}
