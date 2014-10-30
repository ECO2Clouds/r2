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

import eu.eco2clouds.portal.component.NewResourceTableBean;
import eu.eco2clouds.portal.exception.E2CPortalException;
import eu.eco2clouds.portal.scheduler.SchedulerManager;
import eu.eco2clouds.portal.scheduler.SchedulerManagerFactory;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 *  
 */
public class DeploymentUtils {


    SchedulerManager sm;

    public DeploymentUtils() {
        super();

    }

   
    
    public List<String> getCombinations(List<NewResourceTableBean> resourceList) {
        LinkedList<String> result = new LinkedList<String>();

        if (resourceList.size() == 1) {

            String[] locationList = resourceList.get(0).getLocation().split(" ");

            for (int i = 0; i < locationList.length; i++) {
                result.add(resourceList.get(0).getName() + "@" + locationList[i]);
            }

        } else {

            List<NewResourceTableBean> subList = resourceList.subList(1, resourceList.size());
            List<String> combinations = getCombinations(subList);

            String[] locationList = resourceList.get(0).getLocation().split(" ");

            for (int i = 0; i < locationList.length; i++) {
                for (String s : combinations) {
                    result.add(resourceList.get(0).getName() + "@" + locationList[i] + " " + s);
                }
            }

        }

        return result;

    }

}
