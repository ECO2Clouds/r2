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
package eu.eco2clouds.portal.scheduler;

import java.util.HashMap;

/**
 *
 *  
 */
public class MonitoringManager {

    double em_fr;
    double em_uk;
    double em_de;

    HashMap<String, Double> cpuload_fr;
    HashMap<String, Double> cpuload_uk;
    HashMap<String, Double> cpuload_de;

    SchedulerManager sm = SchedulerManagerFactory.getInstance();

    public MonitoringManager() {
        
        super();
        this.refreshValues();

    }

    public double getEmissionFactor(String location) {

        if (location.equals("fr-inria")) {
            return this.em_fr;
        } else if (location.equals("uk-epcc")) {
            return this.em_uk;
        } else if (location.equals("de-hlrs")) {
            return this.em_de;
        }
        return -1;
    }

    public HashMap<String, Double> getCpuLoads(String location) {

        if (location.equals("fr-inria")) {
            return this.cpuload_fr;
        } else if (location.equals("uk-epcc")) {
            return this.cpuload_uk;
        } else if (location.equals("de-hlrs")) {
            return this.cpuload_de;
        }
        return null;
    }

    public void refreshValues() {

        this.em_fr = sm.getCurrentEnergyMixEmissions("fr-inria");
        this.em_uk = sm.getCurrentEnergyMixEmissions("uk-epcc");
        this.em_de = sm.getCurrentEnergyMixEmissions("de-hlrs");

        cpuload_fr = sm.getCpuLoadLocation("fr-inria");
        cpuload_uk = sm.getCpuLoadLocation("uk-epcc");
        cpuload_de = sm.getCpuLoadLocation("de-hlrs");

    }

}
