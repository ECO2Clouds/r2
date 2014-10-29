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
package eu.eco2clouds.component;

import eu.eco2clouds.ac.ApplicationController;
import eu.eco2clouds.ac.Configuration;
import eu.eco2clouds.ac.monitor.AppStatus;
import eu.eco2clouds.ac.monitor.VM;
import eu.eco2clouds.ac.monitor.Item;
import eu.eco2clouds.ac.monitor.ItemValue;
import eu.eco2clouds.ac.monitor.PM;
import eu.eco2clouds.ac.monitor.PMStatus;
import eu.eco2clouds.ac.monitor.Report;
import eu.eco2clouds.ac.monitor.VMStatus;
import eu.eco2clouds.app.ApplicationMetric;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 *
 *  
 */
public class MonitoringManager {

    private final static Logger logger
            = Logger.getLogger(MonitoringManager.class.getName());

    private ApplicationMetric applicationMetric;

    protected List<VMStatus> vmStatusList = new LinkedList<VMStatus>();
    protected List<PMStatus> pmStatusList = new LinkedList<PMStatus>();
    protected AppStatus appStatus;

    public MonitoringManager() {

        SchedulerManager sm = SchedulerManagerFactory.getInstance();

        // read the metrics to be monitored
        this.initVmStatusList(sm.getExperimentVMs());
        this.initPmStatusList(sm.getExperimentPMs());

        logger.log(Level.INFO, "MonitoringManager initialized.");

    }

    public ApplicationMetric getApplicationMetric() {
        return applicationMetric;
    }

    public void setApplicationMetric(ApplicationMetric applicationMetric) {
        this.applicationMetric = applicationMetric;
        if (this.applicationMetric != null) {
            this.initAppStatus(this.applicationMetric.initAppStatus());
        }
    }

//    public ApplicationPerformance getAppPerformance() {
//        return appPerformance;
//    }
//
//    public void setAppPerformance(ApplicationPerformance appPerformance) {
//        this.appPerformance = appPerformance;
//    }
    private void initVmStatusList(List<VM> vms) {

        String propVMS = Configuration.getProperties().getProperty(Configuration.VMS);
        List<String> vmList = Arrays.asList(propVMS.split(","));

        System.out.println("vms " + propVMS);

        SchedulerManager sm = SchedulerManagerFactory.getInstance();
        sm.getExperimentVMs();

        for (VM vm : vms) {

            //System.out.println("compare " + vm.getAlaServer());
            if (vmList.contains(vm.getAlaServer())) {
                List<Item> items = new LinkedList<Item>();

                //I think we need to restrict to the set of agreed metrics in D3.4
                items.add(new Item(Metric.CPU_LOAD, Metric.CPU_LOAD));
                items.add(new Item(Metric.CPU_UTIL, Metric.CPU_UTIL));
                items.add(new Item(Metric.DISK_FREE, Metric.DISK_FREE));
                items.add(new Item(Metric.DISK_TOTAL, Metric.DISK_TOTAL));
                items.add(new Item(Metric.DISK_USAGE, Metric.DISK_USAGE));
                items.add(new Item(Metric.IOPS, Metric.IOPS));
                items.add(new Item(Metric.IO_UTIL, Metric.IO_UTIL));
                items.add(new Item(Metric.MEM_FREE, Metric.MEM_FREE));
                items.add(new Item(Metric.MEM_TOTAL, Metric.MEM_TOTAL));
                items.add(new Item(Metric.MEM_USED, Metric.MEM_USED));
                items.add(new Item(Metric.NETIF_IN, Metric.NETIF_IN));
                items.add(new Item(Metric.NETIF_OUT, Metric.NETIF_OUT));
                items.add(new Item(Metric.PROC_NUM, Metric.PROC_NUM));
                items.add(new Item(Metric.POWER, Metric.POWER));
                items.add(new Item(Metric.SWAP_FREE, Metric.SWAP_FREE));
                items.add(new Item(Metric.SWAP_TOTAL, Metric.SWAP_TOTAL));

                VMStatus vmstatus = new VMStatus(vm, items);
                this.vmStatusList.add(vmstatus);
            }

        }

    }

    private void initPmStatusList(List<PM> pms) {

        for (PM pm : pms) {

            List<Item> items = new LinkedList<Item>();

            //I think we need to restrict to the set of agreed metrics in D3.4
            items.add(new Item(Metric.PM_AVAILABILITY, Metric.PM_AVAILABILITY));
            items.add(new Item(Metric.PM_CPU_UTILIZATION, Metric.PM_CPU_UTILIZATION));
            items.add(new Item(Metric.PM_DISK_IOPS, Metric.PM_DISK_IOPS));
            items.add(new Item(Metric.PM_POWER_CONSUMPTION, Metric.PM_POWER_CONSUMPTION));
            items.add(new Item(Metric.PM_NO_VM, Metric.PM_NO_VM));

            PMStatus pmstatus = new PMStatus(pm, items);
            this.pmStatusList.add(pmstatus);

        }

    }

    private void initAppStatus(AppStatus appStatus) {
        this.appStatus = appStatus;
    }

    public List<VMStatus> updateVmStatusList() {

        SchedulerManager sm = SchedulerManagerFactory.getInstance();

        for (VMStatus vmStatus : vmStatusList) {

            vmStatus = sm.getVMMonitor(vmStatus);

        }
        return vmStatusList;
    }

    public List<PMStatus> updatePmStatusList() {
        SchedulerManager sm = SchedulerManagerFactory.getInstance();

        for (PMStatus pmStatus : pmStatusList) {

            pmStatus = sm.getPMMonitor(pmStatus);

        }
        return pmStatusList;
    }

    public AppStatus updateAppStatus() {

        for (Item item : this.appStatus.getItems()) {
            ItemValue value = this.applicationMetric.calculate(item, pmStatusList, vmStatusList, appStatus);
            appStatus.addValueOfItem(item, value);
        }

        return appStatus;
    }

    public List<VMStatus> getVmStatusList() {
        return vmStatusList;
    }

    public List<PMStatus> getPmStatusList() {
        return pmStatusList;
    }

    public AppStatus getAppStatus() {
        return appStatus;
    }

    public void updateStatus() {
        this.updatePmStatusList();
        this.updateVmStatusList();
        this.updateAppStatus();
    }

    public void printLastStatus(Logger log) {

        this.updateStatus();

        for (PMStatus pmStatus : pmStatusList) {
            pmStatus.printLastStatus(log);
        }

        for (VMStatus vmStatus : vmStatusList) {
            vmStatus.printLastStatus(log);
        }

        appStatus.printLastStatus(log);
    }

    public void printLastStatus(PrintStream out) {

        this.updateStatus();

        for (PMStatus pmStatus : pmStatusList) {
            pmStatus.printLastStatus(out);
        }

        for (VMStatus vmStatus : vmStatusList) {
            vmStatus.printLastStatus(out);
        }

        appStatus.printLastStatus(out);
    }

    public void printTotalStatus(PrintStream out) {

        this.updateStatus();

        for (VMStatus vmStatus : vmStatusList) {
            vmStatus.printVMStatus(out);
        }

    }

    public void printStatusXML(PrintStream out) {

        this.updateStatus();

        try {
            Report report = new Report(new Date(), Configuration.getProperties().getProperty(Configuration.EXPERIMENT_NO), this.getVmStatusList(), this.getAppStatus());

            JAXBContext jaxbContext = JAXBContext.newInstance(Report.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                    true);
            jaxbMarshaller.marshal(report, out);
        } catch (JAXBException ex) {
            Logger.getLogger(ApplicationController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String args[]) throws Exception {

        SchedulerManager scheduler = SchedulerManagerFactory.getInstance();
        MonitoringManager mm = MonitoringManagerFactory.getInstance();
        List<VMStatus> list = mm.updateVmStatusList();
        for (VMStatus vm : list) {
            System.out.println(vm.getVM().getAlaServer());
            System.out.println(vm.getVM().getId());
            System.out.println(vm.getVM().getName());
            vm.printVMStatus(System.out);
        }

        Thread.sleep(10000);
        list = mm.updateVmStatusList();
        for (VMStatus vm : list) {
            vm.printVMStatus(System.out);
        }
    }

}
