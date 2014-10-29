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
import eu.eco2clouds.ac.monitor.AppStatus;
import eu.eco2clouds.ac.monitor.Item;
import eu.eco2clouds.ac.monitor.ItemValue;
import eu.eco2clouds.ac.monitor.PMStatus;
import eu.eco2clouds.ac.monitor.VM;
import eu.eco2clouds.ac.monitor.VMStatus;
import eu.eco2clouds.app.Application;
import eu.eco2clouds.app.ApplicationDecisor;
import eu.eco2clouds.component.Metric;
import eu.eco2clouds.component.MonitoringManager;
import eu.eco2clouds.component.MonitoringManagerFactory;
import eu.eco2clouds.component.SchedulerManager;
import eu.eco2clouds.component.SchedulerManagerFactory;
import eu.eco2clouds.experiment.eels.task.status.EelsTaskStatus;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *  
 */
public class EelsDecisor extends ApplicationDecisor {

    private final static Logger LOGGER = Logger.getLogger(EelsDecisor.class.getName());
    //private final static int SAMPLE_TIME = 30000;

    FileOutputStream fResults;

    VMStatus acStatus;
    PMStatus pmStatus;

    ArrayList<Integer> years = new ArrayList<Integer>();
    HashMap<String, Double> performances = new HashMap<String, Double>();

    public void setYears(ArrayList<Integer> years) {
        this.years = years;

    }

    /* initial setup assigns one year each */
    public void initialSetup(ArrayList<Integer> years) {

        this.setYears(years);
        this.initialSetup();
    }

    public void initialSetup() {

        try {

            MonitoringManager mm = MonitoringManagerFactory.getInstance();
            List<VMStatus> vmStatusList = mm.getVmStatusList();

            for (VMStatus vmStatus : vmStatusList) {

                performances.put(vmStatus.getVM().getAlaServer(), 0.000001); // in the beginning all the servers are equally not that good
            }

        } catch (ACException ex) {
            Logger.getLogger(EelsApplication.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void removeAlaServer(String alaserver) {
        this.performances.remove(alaserver);

        Logger.getLogger(EelsDecisor.class.getName()).log(Level.INFO, "server " + alaserver + " removed from the list of servers");

    }
    /* takes the years to be assigned and distribut the work among he nodes based on the vm performances */

    public HashMap<String, ArrayList<Integer>> distributeWorkload() {

        HashMap<String, Double> weights = new HashMap<String, Double>();
        HashMap<String, ArrayList<Integer>> assignedYears = new HashMap<String, ArrayList<Integer>>();

        int no_years = years.size();
        Logger.getLogger(EelsDecisor.class.getName()).log(Level.INFO, "years to assign " + no_years);

        if (years.size() > 0) {

            double tot_performances = 0;
            for (String vms : performances.keySet()) {
                Logger.getLogger(EelsDecisor.class.getName()).log(Level.INFO, "performance of " + vms + " is " + performances.get(vms));
                tot_performances = tot_performances + performances.get(vms);

            }

            if (tot_performances == 0) { // make them equal
                for (String vms : performances.keySet()) {
                    performances.put(vms, 0.001);
                }
                tot_performances = 0.003;
            }

            String bestVM = "";
            double bestValue = -1;

            for (String vms : performances.keySet()) {
                double w = performances.get(vms) / tot_performances;
                weights.put(vms, w);
                if (w >= bestValue) {
                    bestVM = vms;
                    bestValue = w;
                }

                Logger.getLogger(EelsDecisor.class.getName()).log(Level.INFO, "weight for " + vms + " = " + w);

            }

            Logger.getLogger(EelsDecisor.class.getName()).log(Level.INFO, "best is " + bestVM);

            HashMap<String, Integer> noAssignedYears = new HashMap<String, Integer>();

            int checkyears = 0;
            for (String vms : performances.keySet()) {
                noAssignedYears.put(vms, (int) Math.round(weights.get(vms) * no_years));
                checkyears = checkyears + (int) Math.round(weights.get(vms) * no_years);
                Logger.getLogger(EelsDecisor.class.getName()).log(Level.INFO, "assign " + (int) Math.round(weights.get(vms) * no_years) + " to " + vms);
            }

            if (checkyears > no_years) {
                noAssignedYears.put(bestVM, noAssignedYears.get(bestVM) - (checkyears - no_years));
                Logger.getLogger(EelsDecisor.class.getName()).log(Level.INFO, "removed " + (checkyears - no_years) + " from " + bestVM);

            }
            if (checkyears < no_years) {
                noAssignedYears.put(bestVM, noAssignedYears.get(bestVM) + (no_years - checkyears));
                Logger.getLogger(EelsDecisor.class.getName()).log(Level.INFO, "added " + (no_years - checkyears) + " to " + bestVM);

            }

            ArrayList<Integer> all = new ArrayList<Integer>();
            for (Integer y : years) {
                all.add(y);
            }

            for (String vms : noAssignedYears.keySet()) {

                assignedYears.put(vms, new ArrayList<Integer>());

                for (int k = 0; k < noAssignedYears.get(vms); k++) {

                    assignedYears.get(vms).add(all.get(0));
                    all.remove(0);
                }
            }

            for (String vms : assignedYears.keySet()) {
                Logger.getLogger(EelsDecisor.class.getName()).log(Level.INFO, "years assigned to " + vms);
                for (Integer year : assignedYears.get(vms)) {
                    Logger.getLogger(EelsDecisor.class.getName()).log(Level.INFO, "year " + year);
                }
            }

        } else {
            Logger.getLogger(EelsDecisor.class.getName()).log(Level.INFO, "nothing to distribute");
        }

        return assignedYears;
    }

    public void decide() {

        try {
            /*
             * here the decisor reacts from a request for adaptation asked
             * by the application
             * The application simply says that an adaptation is possible
             * since the application is in a safe state
             * The applicationController redirect the request to the decisor
             *
             */

            /*
             * analise the data from the vm and tasks ...
             */
            AppStatus appStatus = MonitoringManagerFactory.getInstance().getAppStatus();
            MonitoringManager mm = MonitoringManagerFactory.getInstance();

            List<VMStatus> vms = mm.getVmStatusList();
            int i = 0;

            for (VMStatus vm : vms) {

                if (this.performances.containsKey(vm.getVM().getAlaServer())) { //only for active VMs

                    double value = 0;
                //ItemValue throughput = appStatus.getLastItemValue(appStatus.findItemById(EelsApplicationMetric.TASK_THROUGHPUT + "#" + vm.getVM().getId()));
                    //ItemValue power = appStatus.getLastItemValue(appStatus.findItemById(EelsApplicationMetric.TASK_POWER + "#" + vm.getVM().getId()));
                    //if (throughput != null && throughput.getValue() != 0 && power != null && power.getValue() != 0) {
                    //value = (throughput.getValue() * power.getValue());

                    double throughput = appStatus.getItemAverage(appStatus.findItemById(EelsApplicationMetric.TASK_THROUGHPUT + "#" + vm.getVM().getId()));
                    double power = appStatus.getItemAverage(appStatus.findItemById(EelsApplicationMetric.TASK_POWER + "#" + vm.getVM().getId()));

                    if (Configuration.getProperties().getProperty(Configuration.OPTIMIZATION).equals(Configuration.OPT_THROUGHPUT)) {
                        value = throughput;
                    }

                    if (Configuration.getProperties().getProperty(Configuration.OPTIMIZATION).equals(Configuration.OPT_POWER)) {

                        if (power == 0) {
                            value = 0.0000001;
                        } else {
                            value = 1 / power;
                        }
                    }

                    if (Configuration.getProperties().getProperty(Configuration.OPTIMIZATION).equals(Configuration.OPT_EFFICIENCY)) {

                        if (power == 0) {
                            value = 0.0000001;
                        } else {
                            value = throughput / power;
                        }
                    }

                    if (Configuration.getProperties().getProperty(Configuration.OPTIMIZATION).equals(Configuration.OPT_CO2)) {

                        double energymix = 0;
                        if (vm.getVM().getId().contains("fr-inria")) {
                            energymix = SchedulerManagerFactory.getInstance().getCurrentEnergyMixEmissions("fr-inria");
                        } else if (vm.getVM().getId().contains("uk-epcc")) {
                            energymix = SchedulerManagerFactory.getInstance().getCurrentEnergyMixEmissions("uk-epcc");
                        } else if (vm.getVM().getId().contains("de-hlrs")) {
                            energymix = SchedulerManagerFactory.getInstance().getCurrentEnergyMixEmissions("de-hlrs");
                        }

                        if (power == 0 || energymix == 0 || throughput == 0) {
                            value = 0.0000001;
                        } else {
                            value = throughput / (power * energymix);
                        }
                    }

                    if (Configuration.getProperties().getProperty(Configuration.OPTIMIZATION).equals(Configuration.OPT_OFF)) {
                        value = 0.0000001;
                    }
                    performances.put(vm.getVM().getAlaServer(), value); //update the value

                    Logger.getLogger(EelsDecisor.class.getName()).log(Level.INFO, "server " + vm.getVM().getAlaServer() + " has performance = " + value + "(" + Configuration.getProperties().getProperty(Configuration.OPTIMIZATION) + ")");
                }
            }

        } catch (ACException ex) {
            Logger.getLogger(EelsDecisor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void run() {

        try {
            this.openFiles();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EelsDecisor.class.getName()).log(Level.SEVERE, "not able to open files to store the results", ex);
        } catch (IOException ex) {
            Logger.getLogger(EelsDecisor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ACException ex) {
            Logger.getLogger(EelsDecisor.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.status = Application.RUNNING;

        while (this.getStatus() == Application.RUNNING) {
            try {
                /* here the decisor can do continuous monitoring and/or enactment 
                 * regardless of the status of the application
                 */
                Thread.sleep(20000);
                decide();

            } catch (InterruptedException ex) {
                LOGGER.severe("Error in thread sleeping");
            }

            LOGGER.info("Time to compute application metrics");

            try {
                this.storeResult();
                MonitoringManagerFactory.getInstance().printLastStatus(Logger.getLogger(EelsDecisor.class.getName()));
            } catch (ACException ex) {
                Logger.getLogger(EelsDecisor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

       /* for (int i = 0; i < 12; i++) { // some idle cycles to make it sure all the metrics go down
            try {
                Thread.sleep(10000);

                this.storeResult();
                MonitoringManagerFactory.getInstance().printLastStatus(Logger.getLogger(EelsDecisor.class.getName()));
            } catch (ACException ex) {
                Logger.getLogger(EelsDecisor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(EelsDecisor.class.getName()).log(Level.SEVERE, null, ex);
            }

        }*/
        try {
            this.closeFiles();
        } catch (IOException ex) {
            Logger.getLogger(EelsDecisor.class.getName()).log(Level.SEVERE, "not able to close files where results are stored", ex);
        }
    }

    private void openFiles() throws FileNotFoundException, IOException, ACException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");

        //fResults = new FileOutputStream("/home/zabbix/eels/eelsresults" + sdf.format(new Date()) + ".res");
        fResults = new FileOutputStream("/home/zabbix/eels/eelsresults" + eu.eco2clouds.ac.Configuration.getProperties().getProperty(eu.eco2clouds.ac.Configuration.EXPERIMENT_NO) + ".res");

        StringBuffer header = new StringBuffer();
        header.append("clock" + "\t");
        header.append(EelsApplicationMetric.APP_ENERGY_PRODUCTIVITY_NAME + "\t");
        header.append(EelsApplicationMetric.APP_POWER_NAME + "\t");
        header.append(EelsApplicationMetric.APP_PUE_NAME + "\t");
        header.append(EelsApplicationMetric.APP_RESPONSETIME_NAME + "\t");
        header.append(EelsApplicationMetric.APP_THROUGHPUT_NAME + "\t");

        MonitoringManager mm = MonitoringManagerFactory.getInstance();

        List<VMStatus> vms = mm.getVmStatusList();
        for (VMStatus vm : vms) {

            header.append(EelsApplicationMetric.TASK_POWER_NAME + "#" + vm.getVM().getId() + "\t");
            header.append(EelsApplicationMetric.TASK_CPUUTILIZATION_NAME + "#" + vm.getVM().getId() + "\t");
            header.append(EelsApplicationMetric.TASK_RESPONSETIME_NAME + "#" + vm.getVM().getId() + "\t");
            header.append(EelsApplicationMetric.TASK_THROUGHPUT_NAME + "#" + vm.getVM().getId() + "\t");
        }

        header.append("ACPower\t");

        List<PMStatus> pms = mm.getPmStatusList();
        for (PMStatus pm : pms) {

            header.append("HostPower#" + pm.getPM().getName() + "@" + pm.getPM().getLocation() + "\t");
            header.append("HostCPUUtilization#" + pm.getPM().getName() + "@" + pm.getPM().getLocation() + "\t");
            header.append("HostNOVM#" + pm.getPM().getName() + "@" + pm.getPM().getLocation() + "\t");

        }

        header.append("Mix@fr-inria\t");
        header.append("Mix@uk-epcc\t");
        header.append("Mix@de-hlrs\n");

        fResults.write(header.toString().getBytes());

        SchedulerManager sm = SchedulerManagerFactory.getInstance();
        List<VM> vmss = sm.getExperimentVMs();
        for (VM vm : vmss) {
            if (vm.getAlaServer().equals(eu.eco2clouds.ac.Configuration.getProperties().getProperty(eu.eco2clouds.ac.Configuration.AC_IP))) {
                List<Item> items = new LinkedList<Item>();
                items.add(new Item(Metric.POWER, Metric.POWER));
                this.acStatus = new VMStatus(vm, items);
            }
        }

    }

    private void storeResult() throws ACException {

        AppStatus appStatus = MonitoringManagerFactory.getInstance().getAppStatus();
        StringBuilder data = new StringBuilder();

        data.append(System.currentTimeMillis()).append("\t");
        ItemValue item = appStatus.getLastItemValue(appStatus.findItemById(EelsApplicationMetric.APP_ENERGY_PRODUCTIVITY));
        if (item != null) {
            data.append(Math.round(item.getValue() * 1000.00) / 1000.00).append("\t");
        } else {
            data.append("NaN\t");
        }
        item = appStatus.getLastItemValue(appStatus.findItemById(EelsApplicationMetric.APP_POWER));
        if (item != null) {
            data.append(Math.round(item.getValue() * 1000.00) / 1000.00).append("\t");
        } else {
            data.append("NaN\t");
        }

        item = appStatus.getLastItemValue(appStatus.findItemById(EelsApplicationMetric.APP_PUE));
        if (item != null) {
            data.append(Math.round(item.getValue() * 1000.00) / 1000.00).append("\t");
        } else {
            data.append("NaN\t");
        }

        item = appStatus.getLastItemValue(appStatus.findItemById(EelsApplicationMetric.APP_RESPONSETIME));
        if (item != null) {
            data.append(Math.round(item.getValue() * 1000.00) / 1000.00).append("\t");
        } else {
            data.append("NaN\t");
        }

        item = appStatus.getLastItemValue(appStatus.findItemById(EelsApplicationMetric.APP_THROUGHPUT));
        if (item != null) {
            data.append(Math.round(item.getValue() * 1000.00) / 1000.00).append("\t");
        } else {
            data.append("NaN\t");
        }

        MonitoringManager mm = MonitoringManagerFactory.getInstance();

        List<VMStatus> vms = mm.getVmStatusList();
        for (VMStatus vm : vms) {

            item = appStatus.getLastItemValue(appStatus.findItemById(EelsApplicationMetric.TASK_POWER + "#" + vm.getVM().getId()));
            if (item != null) {
                data.append(Math.round(item.getValue() * 1000.00) / 1000.00).append("\t");
            } else {
                data.append("NaN\t");
            }

            item = appStatus.getLastItemValue(appStatus.findItemById(EelsApplicationMetric.TASK_CPUUTILIZATION + "#" + vm.getVM().getId()));
            if (item != null) {
                data.append(Math.round(item.getValue() * 1000.00) / 1000.00).append("\t");
            } else {
                data.append("NaN\t");
            }

            item = appStatus.getLastItemValue(appStatus.findItemById(EelsApplicationMetric.TASK_RESPONSETIME + "#" + vm.getVM().getId()));
            if (item != null) {
                data.append(Math.round(item.getValue() * 1000.00) / 1000.00).append("\t");
            } else {
                data.append("NaN\t");
            }

            item = appStatus.getLastItemValue(appStatus.findItemById(EelsApplicationMetric.TASK_THROUGHPUT + "#" + vm.getVM().getId()));
            if (item != null) {
                data.append(Math.round(item.getValue() * 1000.00) / 1000.00).append("\t");
            } else {
                data.append("NaN\t");
            }
        }

        SchedulerManager sm = SchedulerManagerFactory.getInstance();

        this.acStatus = sm.getVMMonitor(acStatus);

        item = acStatus.getLastItemValue(acStatus.findItemById(Metric.POWER));
        if (item != null) {
            data.append(Math.round(item.getValue() * 1000.00) / 1000.00).append("\t");
        } else {
            data.append("NaN\t");
        }

        List<PMStatus> pms = mm.getPmStatusList();
        for (PMStatus pm : pms) {

            item = pm.getLastItemValue(pm.findItemById(Metric.PM_POWER_CONSUMPTION));
            if (item != null) {
                data.append(Math.round(item.getValue() * 1000.00) / 1000.00).append("\t");
            } else {
                data.append("NaN\t");
            }

            item = pm.getLastItemValue(pm.findItemById(Metric.PM_CPU_UTILIZATION));
            if (item != null) {
                data.append(Math.round(item.getValue() * 1000.00) / 1000.00).append("\t");
            } else {
                data.append("NaN\t");
            }

            item = pm.getLastItemValue(pm.findItemById(Metric.PM_NO_VM));
            if (item != null) {
                data.append(Math.round(item.getValue() * 1000.00) / 1000.00).append("\t");
            } else {
                data.append("NaN\t");
            }

        }

        data.append(sm.getCurrentEnergyMixEmissions("fr-inria")).append("\t");
        data.append(sm.getCurrentEnergyMixEmissions("uk-epcc")).append("\t");
        data.append(sm.getCurrentEnergyMixEmissions("de-hlrs")).append("\n");

        try {
            this.fResults.write(data.toString().getBytes());
        } catch (IOException ex) {
            Logger.getLogger(EelsDecisor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void closeFiles() throws IOException {
        if (fResults != null) {
            fResults.close();
        }

    }

}
