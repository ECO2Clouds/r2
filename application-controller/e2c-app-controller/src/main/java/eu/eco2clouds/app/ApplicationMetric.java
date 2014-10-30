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
package eu.eco2clouds.app;

import eu.eco2clouds.ac.monitor.AppStatus;
import eu.eco2clouds.ac.monitor.Item;
import eu.eco2clouds.ac.monitor.ItemValue;
import eu.eco2clouds.ac.monitor.PMStatus;
import eu.eco2clouds.ac.monitor.VMStatus;
import java.util.List;

/**
 *
 *  
 */
public abstract class ApplicationMetric {
    
    // how to calculate the app metrics
    public abstract ItemValue calculate(Item item, List<PMStatus> pmStatusList, List<VMStatus> vmStatusList, AppStatus appStatus);
       
    // to feed the monitoringmanager with the list of the metrics need to be calculated
    public abstract AppStatus initAppStatus();
}
