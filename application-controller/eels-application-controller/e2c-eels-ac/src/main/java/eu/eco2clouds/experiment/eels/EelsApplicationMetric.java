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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import eu.eco2clouds.ac.ACException;
import eu.eco2clouds.ac.monitor.AppStatus;
import eu.eco2clouds.ac.monitor.Item;
import eu.eco2clouds.ac.monitor.ItemValue;
import eu.eco2clouds.ac.monitor.PMStatus;
import eu.eco2clouds.ac.monitor.VMStatus;
import eu.eco2clouds.app.ApplicationMetric;
import eu.eco2clouds.component.Metric;
import eu.eco2clouds.component.MonitoringManager;
import eu.eco2clouds.component.MonitoringManagerFactory;
import eu.eco2clouds.experiment.eels.task.status.EelsTaskStatus;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *  
 */
public class EelsApplicationMetric extends ApplicationMetric {

    public static String APP_THROUGHPUT = "applicationmetric_1";
    public static String APP_POWER = "applicationmetric_2";
    public static String APP_RESPONSETIME = "applicationmetric_3";
    public static String APP_PUE = "applicationmetric_4";
    public static String APP_ENERGY_PRODUCTIVITY = "applicationmetric_5";

    public static String APP_THROUGHPUT_NAME = "app_throughput";
    public static String APP_POWER_NAME = "app_power";
    public static String APP_RESPONSETIME_NAME = "app_responsetime";
    public static String APP_PUE_NAME = "app_pue";
    public static String APP_ENERGY_PRODUCTIVITY_NAME = "app_energyproductivity";

    public static String TASK_THROUGHPUT = "applicationmetric_1";
    public static String TASK_RESPONSETIME = "applicationmetric_2";
    public static String TASK_CPUUTILIZATION = "applicationmetric_3";
    public static String TASK_POWER = "vm_power";

    public static String TASK_THROUGHPUT_NAME = "task_throughput";
    public static String TASK_RESPONSETIME_NAME = "task_responsetime";
    public static String TASK_CPUUTILIZATION_NAME = "task_cpuutilization";
    public static String TASK_POWER_NAME = "task_power";

    private long commonClock = System.currentTimeMillis();

    private long appResponseTime = -1; // updated by the application to store the app responstime -1 means app not yet finished

    public long getAppResponseTime() {
        return appResponseTime;
    }

    public void setAppResponseTime(long appResponseTime) {
        this.appResponseTime = appResponseTime;
    }

    @Override
    public AppStatus initAppStatus() {

        try {
            LinkedList<Item> items = new LinkedList<Item>();

            items.add(new Item(EelsApplicationMetric.APP_POWER, EelsApplicationMetric.APP_POWER_NAME));
            items.add(new Item(EelsApplicationMetric.APP_THROUGHPUT, EelsApplicationMetric.APP_THROUGHPUT_NAME));
            items.add(new Item(EelsApplicationMetric.APP_ENERGY_PRODUCTIVITY, EelsApplicationMetric.APP_ENERGY_PRODUCTIVITY_NAME));
            items.add(new Item(EelsApplicationMetric.APP_RESPONSETIME, EelsApplicationMetric.APP_RESPONSETIME_NAME));
            items.add(new Item(EelsApplicationMetric.APP_PUE, EelsApplicationMetric.APP_PUE_NAME));

            MonitoringManager mm = MonitoringManagerFactory.getInstance();

            List<VMStatus> vms = mm.getVmStatusList();
            for (VMStatus vm : vms) {

                items.add(new Item(EelsApplicationMetric.TASK_THROUGHPUT + "#" + vm.getVM().getId(), EelsApplicationMetric.TASK_THROUGHPUT_NAME + "#" + vm.getVM().getId()));
                items.add(new Item(EelsApplicationMetric.TASK_RESPONSETIME + "#" + vm.getVM().getId(), EelsApplicationMetric.TASK_RESPONSETIME_NAME + "#" + vm.getVM().getId()));
                items.add(new Item(EelsApplicationMetric.TASK_CPUUTILIZATION + "#" + vm.getVM().getId(), EelsApplicationMetric.TASK_CPUUTILIZATION_NAME + "#" + vm.getVM().getId()));
                items.add(new Item(EelsApplicationMetric.TASK_POWER + "#" + vm.getVM().getId(), EelsApplicationMetric.TASK_POWER_NAME + "#" + vm.getVM().getId()));

            }

            AppStatus appStatus = new AppStatus("eelsApplication", items);

            return appStatus;

        } catch (ACException ex) {
            Logger.getLogger(EelsApplicationMetric.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    @Override
    public ItemValue calculate(Item item, List<PMStatus> pmStatusList, List<VMStatus> vmStatusList, AppStatus appStatus) {

        this.commonClock = System.currentTimeMillis();

        if (item.getId().equals(EelsApplicationMetric.APP_POWER)) {
            return this.computeAppPower(vmStatusList);
        } else if (item.getId().equals(EelsApplicationMetric.APP_THROUGHPUT)) {
            return this.computeAppThroughput(vmStatusList, appStatus);
        } else if (item.getId().equals(EelsApplicationMetric.APP_ENERGY_PRODUCTIVITY)) {
            return this.computeAppEnergyProductivity(vmStatusList, appStatus);
        } else if (item.getId().equals(EelsApplicationMetric.APP_RESPONSETIME)) {
            return this.computeAppResponseTime();
        } else if (item.getId().equals(EelsApplicationMetric.APP_PUE)) {
            return this.computeAppPUE(vmStatusList, appStatus);
        } else if (item.getId().startsWith(EelsApplicationMetric.TASK_POWER)) { //remeber starts with
            return this.computeTaskPower(vmStatusList, item);
        } else if (item.getId().startsWith(EelsApplicationMetric.TASK_THROUGHPUT)) { //remeber starts with
            return this.computeTaskThroughput(vmStatusList, item);
        } else if (item.getId().startsWith(EelsApplicationMetric.TASK_RESPONSETIME)) { //remeber starts with
            return this.computeTaskResponseTime(vmStatusList, item);
        } else if (item.getId().startsWith(EelsApplicationMetric.TASK_CPUUTILIZATION)) { //remeber starts with
            return this.computeTaskCpuUtilization(vmStatusList, item);
        } else {
            return null;
        }

    }

    private ItemValue computeAppPower(List<VMStatus> vmStatusList) {
        double val = 0;
        for (VMStatus v : vmStatusList) {
            Item item = v.findItemById(Metric.POWER);

            ItemValue value = v.getLastItemValue(item);
            if (value != null) {
                val += v.getLastItemValue(item).getValue();
            }
        }
        try {
            FileOutputStream fResults = new FileOutputStream("/home/zabbix/eels/apppower.log");
            fResults.write(Double.toString(val).getBytes());
            fResults.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EelsApplicationMetric.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EelsApplicationMetric.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ItemValue(this.commonClock, val);
    }

    private ItemValue computeAppThroughput(List<VMStatus> vmStatusList, AppStatus appStatus) {

        double totalTp = 0;

        for (VMStatus v : vmStatusList) {
            ItemValue tp = appStatus.getLastItemValue(appStatus.findItemById(EelsApplicationMetric.TASK_THROUGHPUT + "#" + v.getVM().getId()));
            if (tp != null) {
                totalTp += tp.getValue();
            }
        }
        try {
            FileOutputStream fResults = new FileOutputStream("/home/zabbix/eels/appthroughput.log");
            fResults.write(Double.toString(totalTp).getBytes());
            fResults.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EelsApplicationMetric.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EelsApplicationMetric.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new ItemValue(this.commonClock, totalTp);

    }

    private ItemValue computeAppEnergyProductivity(List<VMStatus> vmStatusList, AppStatus appStatus) {

        //we assume that the power from the last sample to now remains constant
        // (tp/s * 3600s ) = power*3600s --> t / Wh
        double power = 0;
        double taskPerf = 0;
        double appPerf = 0;
        for (VMStatus v : vmStatusList) {

            ItemValue powervm = v.getLastItemValue(v.findItemById(Metric.POWER));

            if (powervm != null) {
                power += powervm.getValue();
            }
            ItemValue tp = appStatus.getLastItemValue(appStatus.findItemById(EelsApplicationMetric.TASK_THROUGHPUT + "#" + v.getVM().getId()));
            if (tp != null) {
                taskPerf += tp.getValue();
            }
        }
        if (power != 0) {

            appPerf = taskPerf / power;
        }
        try {
            FileOutputStream fResults = new FileOutputStream("/home/zabbix/eels/appenergyproductivity.log");
            fResults.write(Double.toString(appPerf).getBytes());
            fResults.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EelsApplicationMetric.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EelsApplicationMetric.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new ItemValue(this.commonClock, appPerf);
    }

    private ItemValue computeAppResponseTime() {

        if (this.appResponseTime == -1) {
            try {
                FileOutputStream fResults = new FileOutputStream("/home/zabbix/eels/appresponsetime.log");
                fResults.write("0".getBytes());
                fResults.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(EelsApplicationMetric.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(EelsApplicationMetric.class.getName()).log(Level.SEVERE, null, ex);
            }

            return new ItemValue(this.commonClock, 0);
        } else {
            try {
                FileOutputStream fResults = new FileOutputStream("/home/zabbix/eels/appresponsetime.log");
                fResults.write(Double.toString(this.appResponseTime).getBytes());
                fResults.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(EelsApplicationMetric.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(EelsApplicationMetric.class.getName()).log(Level.SEVERE, null, ex);
            }
            return new ItemValue(this.commonClock, this.appResponseTime);
        }

    }

    private ItemValue computeAppPUE(List<VMStatus> vmStatusList, AppStatus appStatus) {
        double powervmtotal = 0;
        double powerproctotal = 0;
        for (VMStatus v : vmStatusList) {
            ItemValue powervm = v.getLastItemValue(v.findItemById(Metric.POWER));
            ItemValue processcpuutil = appStatus.getLastItemValue(appStatus.findItemById(EelsApplicationMetric.TASK_CPUUTILIZATION + "#" + v.getVM().getId()));

            //Logger.getLogger(EelsApplicationMetric.class.getName()).log(Level.INFO, "for server " + v.getVM().getAlaServer() + " power (" + powervm.getValue() + ") and cpuutil (" + processcpuutil.getValue() + ")");
            if (powervm != null && processcpuutil != null && powervm.getValue() != 0.0) {

                double powerprocess = processcpuutil.getValue() / 100.00 * powervm.getValue();
                powerproctotal += powerprocess;

            }
            if (powervm != null && powervm.getValue() != 0.0) {
                powervmtotal += powervm.getValue();
            }

        }
        try {
            FileOutputStream fResults = new FileOutputStream("/home/zabbix/eels/appresponsetime.log");
            fResults.write(Double.toString(powervmtotal / powerproctotal).getBytes());
            fResults.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EelsApplicationMetric.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EelsApplicationMetric.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ItemValue(this.commonClock, (powervmtotal / powerproctotal));
    }

    private ItemValue computeTaskPower(List<VMStatus> vmStatusList, Item item) {

        String[] ids = item.getId().split("#");
        String id = ids[ids.length - 1];

        try {
            for (VMStatus vm : vmStatusList) {
                if (vm.getVM().getId().equals(id)) {
                    ItemValue powervm = vm.getLastItemValue(vm.findItemById(Metric.POWER));
                    if (powervm == null) {
                        return new ItemValue(this.commonClock, 0.0);
                    } else {
                        return powervm;
                    }
                }

            }
        } catch (Exception e) {
            Logger.getLogger(EelsApplicationMetric.class.getName()).log(Level.WARNING, "Not able to compute metric " + item.getId() + " assuming the power is 0.0");
            return new ItemValue(this.commonClock, 0.0);
        }
        return null;
    }

    private ItemValue computeTaskThroughput(List<VMStatus> vmStatusList, Item item) {

        String[] ids = item.getId().split("#");
        String id = ids[ids.length - 1];

        try {
            for (VMStatus vm : vmStatusList) {
                if (vm.getVM().getId().equals(id)) {
                    ClientConfig config = new DefaultClientConfig();
                    Client client = Client.create(config);
                    WebResource service = client.resource("http://" + vm.getVM().getAlaServer() + ":" + eu.eco2clouds.ac.Configuration.getProperties().getProperty(eu.eco2clouds.ac.Configuration.ALA_SERVER_PORT));
                    EelsTaskStatus status = service.path("status").get(EelsTaskStatus.class);
                    return new ItemValue(this.commonClock, status.getThroughput());
                }

            }
        } catch (Exception e) {
            Logger.getLogger(EelsApplicationMetric.class.getName()).log(Level.WARNING, "Not able to compute metric " + item.getId() + " assuming the throughput is 0.0");
            return new ItemValue(this.commonClock, 0.0);
        }
        return null;
    }

    private ItemValue computeTaskResponseTime(List<VMStatus> vmStatusList, Item item) {

        String[] ids = item.getId().split("#");
        String id = ids[ids.length - 1];

        try {
            for (VMStatus vm : vmStatusList) {
                if (vm.getVM().getId().equals(id)) {
                    ClientConfig config = new DefaultClientConfig();
                    Client client = Client.create(config);
                    WebResource service = client.resource("http://" + vm.getVM().getAlaServer() + ":" + eu.eco2clouds.ac.Configuration.getProperties().getProperty(eu.eco2clouds.ac.Configuration.ALA_SERVER_PORT));
                    EelsTaskStatus status = service.path("status").get(EelsTaskStatus.class);
                    return new ItemValue(this.commonClock, status.getResponse_time());
                }

            }
        } catch (Exception e) {
            Logger.getLogger(EelsApplicationMetric.class.getName()).log(Level.WARNING, "Not able to compute metric " + item.getId());
        }
        return null;
    }

    private ItemValue computeTaskCpuUtilization(List<VMStatus> vmStatusList, Item item) {

        String[] ids = item.getId().split("#");
        String id = ids[ids.length - 1];

        try {
            for (VMStatus vm : vmStatusList) {
                if (vm.getVM().getId().equals(id)) {
                    ClientConfig config = new DefaultClientConfig();
                    Client client = Client.create(config);
                    WebResource service = client.resource("http://" + vm.getVM().getAlaServer() + ":" + eu.eco2clouds.ac.Configuration.getProperties().getProperty(eu.eco2clouds.ac.Configuration.ALA_SERVER_PORT));
                    EelsTaskStatus status = service.path("status").get(EelsTaskStatus.class);
                    return new ItemValue(this.commonClock, status.getCpuutilization());
                }

            }
        } catch (Exception e) {
            Logger.getLogger(EelsApplicationMetric.class.getName()).log(Level.WARNING, "Not able to compute metric " + item.getId() + " assuming cpu is 0");
            return new ItemValue(this.commonClock, 0.0);

        }
        return null;
    }
}
