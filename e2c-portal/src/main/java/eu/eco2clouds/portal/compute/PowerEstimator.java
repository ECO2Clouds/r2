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
package eu.eco2clouds.portal.compute;

import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import dk.ange.octave.type.Octave;
import dk.ange.octave.type.OctaveDouble;
import dk.ange.octave.type.OctaveString;
import eu.eco2clouds.accounting.datamodel.parser.Host;
import eu.eco2clouds.accounting.datamodel.parser.HostMonitoring;
import eu.eco2clouds.portal.exception.E2CPortalException;
import eu.eco2clouds.portal.scheduler.SchedulerManager;
import eu.eco2clouds.portal.scheduler.SchedulerManagerFactory;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 *  
 */
public class PowerEstimator {

    private String[] hostInria = new String[]{"bonfire"};
    private String[] hostEpcc = new String[]{"vmhost1", "vmhost6"};
    private String[] hostHlrs = new String[]{"floccus16", "floccus6", "node0104"};

    private HashMap<String, List<String>> hosttypes = new HashMap<String, List<String>>();

    public PowerEstimator() {
        super();

        //INRIA
        LinkedList<String> typesBf = new LinkedList<String>();
        for (int i = 1; i <= 4; i++) {
            typesBf.add("bonfire-blade-" + i + ".bonfire.grid5000.fr");
        }

        hosttypes.put("bonfire", typesBf);

        //HLRS
        LinkedList<String> typesFl6 = new LinkedList<String>();
        for (int i = 1; i <= 6; i++) {
            typesFl6.add("floccus0" + i);
        }

        hosttypes.put("floccus6", typesFl6);

        LinkedList<String> typesFl16 = new LinkedList<String>();
        for (int i = 15; i <= 16; i++) {
            typesFl6.add("floccus" + i);
        }

        hosttypes.put("floccus16", typesFl16);

        LinkedList<String> typesNode = new LinkedList<String>();
        for (int i = 1; i <= 14; i++) {
            if (i < 10) {
                typesNode.add("node010" + i);
            } else {
                typesNode.add("node01" + i);
            }
        }
        for (int i = 1; i <= 14; i++) {
            if (i < 10) {
                typesNode.add("node020" + i);
            } else {
                typesNode.add("node02" + i);
            }
        }

        hosttypes.put("node0104", typesNode);

        //EPCC
        LinkedList<String> vmhost1 = new LinkedList<String>();
        for (int i = 0; i <= 1; i++) {
            vmhost1.add("vmhost" + i);
        }

        hosttypes.put("vmhost1", vmhost1);

        LinkedList<String> vmhost6 = new LinkedList<String>();
        for (int i = 2; i <= 6; i++) {
            vmhost6.add("vmhost" + i);
        }

        hosttypes.put("vmhost6", vmhost6);

    }

    public PowerItem estimatePower(String location, double no_cpu, double cpuload) throws E2CPortalException {

        //double currentCpuLoad = sm.getAverageCpuLocation(location); TODO WHEN SCHEDULER WILL WORK
        if (cpuload == 0 || no_cpu == 0) {
            return new PowerItem(0.0, 0.0, 0.0);
        } else if (cpuload < 0 || no_cpu < 0) {
            if (cpuload < 0) {
                throw new E2CPortalException("cpuload cannot be negative");
            }
            if (no_cpu < 0) {
                throw new E2CPortalException("no_cpu cannot be negative");
            }
            return null;

        } else {

            //CPUItem currentCpuLoad = new CPUItem (300.00, 300.00, 300.00);
            CPUItem currentCpuLoad = this.getCurrentCpuLoad(location);

            //PowerItem currentPower = new PowerItem(this.getMinPower(location, currentCpuLoad.getMin()), this.getMinPower(location, currentCpuLoad.getMin()), this.getMinPower(location, currentCpuLoad.getMin()));
            PowerItem currentPower = this.getCurrentPower(location);

            CPUItem finalCpuLoad = new CPUItem(currentCpuLoad.getMin() + (cpuload * no_cpu), currentCpuLoad.getMean() + (cpuload * no_cpu), currentCpuLoad.getMax() + (cpuload * no_cpu));

            double finalMinPower = this.getMinPower(location, finalCpuLoad.getMin());
            double finalMaxPower = this.getMaxPower(location, finalCpuLoad.getMax());

            /*double finalCpuLoad = 0;
             if (currentCpuLoad + cpuload > 2400) {
             finalCpuLoad = 2400;
             } else {
             finalCpuLoad = currentCpuLoad + (cpuload * no_cpu);
             }*/
            PowerItem result = new PowerItem();

            result.setMin(((finalMinPower - currentPower.getMin()) * 100.00) / 100.00);
            result.setMax(((finalMaxPower - currentPower.getMax()) * 100.00) / 100.00);
            return result;
        }

    }

    private double getMinPower(String location, double cpuload) {

        double min = -1;

        if (location.equals("fr-inria")) {

            for (int i = 0; i < this.hostInria.length; i++) {
                double a = this.powerModelHost(location, this.hostInria[i], cpuload);
                if (a < min || min == -1) {
                    min = a;
                }
            }

        }

        if (location.equals("de-hlrs")) {

            for (int i = 0; i < this.hostHlrs.length; i++) {
                double a = this.powerModelHost(location, this.hostHlrs[i], cpuload);
                if (a < min || min == -1) {
                    min = a;
                }
            }

        }

        if (location.equals("uk-epcc")) {

            for (int i = 0; i < this.hostEpcc.length; i++) {
                double a = this.powerModelHost(location, this.hostEpcc[i], cpuload);
                if (a < min || min == -1) {
                    min = a;
                }
            }

        }

        return min;

    }

    private double getMaxPower(String location, double cpuload) {

        double max = -1;

        if (location.equals("fr-inria")) {

            for (int i = 0; i < this.hostInria.length; i++) {
                double a = this.powerModelHost(location, this.hostInria[i], cpuload);
                if (a > max) {
                    max = a;
                }
            }

        }

        if (location.equals("de-hlrs")) {

            for (int i = 0; i < this.hostHlrs.length; i++) {
                double a = this.powerModelHost(location, this.hostHlrs[i], cpuload);
                if (a > max) {
                    max = a;
                }
            }

        }

        if (location.equals("uk-epcc")) {

            for (int i = 0; i < this.hostEpcc.length; i++) {
                double a = this.powerModelHost(location, this.hostEpcc[i], cpuload);
                if (a > max) {
                    max = a;
                }
            }

        }

        return max;

    }

    private double powerModelHost(String location, String type, double cpuload) {

        if (location.equals("fr-inria")) {
            if (type.equals("bonfire")) {
                return -61.64 * Math.pow(cpuload, 2) + 131 * cpuload + 139.3;
            }
        } else if (location.equals("uk-epcc")) {
            if (type.equals("vmhost1")) { //equals to floccus16 so far
                return -156.2 * Math.pow(cpuload, 2) + 439 * cpuload + 364.3;
            }
            if (type.equals("vmhost6")) {
                return -113 * Math.pow(cpuload, 2) + 208.9 * cpuload + 106.4;
            }
        } else if (location.equals("de-hlrs")) {
            if (type.equals("floccus16")) {
                return -156.2 * Math.pow(cpuload, 2) + 439 * cpuload + 364.3;
            }
            if (type.equals("floccus6")) {
                return -7.08 * Math.pow(cpuload, 2) + 101.3 * cpuload + 154.8;
            }
            if (type.equals("node0104")) {
                return -8.896 * Math.pow(cpuload, 2) + 114.3 * cpuload + 155.3;
            }
        }
        return -1;
    }

    public CPUItem getCurrentCpuLoad(String location) {

        CPUItem result = new CPUItem(0.0, 0.0, 0.0);

        SchedulerManager sm = SchedulerManagerFactory.getInstance();

        List<Host> hosts = sm.getHosts(location);

        for (Host host : hosts) {
            HostMonitoring hm = sm.getHostMonitoring(location, host.getName());

            double cpuload = hm.getCpuUtilization().getValue() !=null ? hm.getCpuUtilization().getValue()/ 100.00: 0.00; //now the latest, when available we will have the last n samples
            if (result.getMin() == 0 || result.getMin() > cpuload) {
                result.setMin(cpuload);
            }
            if (result.getMax() < cpuload) {
                result.setMax(cpuload);
            }
        }
        return result;
    }

    private PowerItem getCurrentPower(String location) {

        PowerItem result = new PowerItem(0.0, 0.0, 0.0);

        SchedulerManager sm = SchedulerManagerFactory.getInstance();

        List<Host> hosts = sm.getHosts(location);

        for (Host host : hosts) {

            HostMonitoring hm = sm.getHostMonitoring(location, host.getName());
            double power = hm.getPowerConsumption().getValue(); //now the latest, when available we will have the last n samples
            if (result.getMin() == 0 || result.getMin() > power) {
                result.setMin(power);
            }
            if (result.getMax() < power) {
                result.setMax(power);
            }
        }
        return result;
    }

    public void checkMetrics(String location) {

        SchedulerManager sm = SchedulerManagerFactory.getInstance();

        List<Host> hosts = sm.getHosts(location);

        System.out.println("hosts " + hosts.size());

        for (Host host : hosts) {

            HostMonitoring hm = sm.getHostMonitoring(location, host.getName());
            System.out.println("host " + hm.getHref());
            System.out.println("CpuUtilization = " + hm.getCpuUtilization().getValue() + hm.getCpuUtilization().getUnity());
            System.out.println("Availability = " + hm.getAvailability().getValue() + hm.getAvailability().getUnity());
            System.out.println("PowerConsumption = " + hm.getPowerConsumption().getValue() + hm.getPowerConsumption().getUnity());
            System.out.println("DiskIOPS = " + hm.getDiskIOPS().getValue() + hm.getDiskIOPS().getUnity());
            System.out.println("");
        }
    }

    public static void oldmain(String args[]) throws Exception {

        SchedulerManagerFactory.configure("https://scheduler.eco2clouds.eu/scheduler", "keystore.jks", "xxx");

        PowerEstimator estimator = new PowerEstimator();
        //estimator.estimatePower("de-hlrs", 1, 1).getMin();
        estimator.checkMetrics("fr-inria");
        estimator.checkMetrics("de-hlrs");
        estimator.checkMetrics("uk-epcc");

    }

   
}
