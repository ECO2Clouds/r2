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
package eu.eco2clouds.ac.monitor;

import java.io.PrintStream;
import static java.lang.System.out;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *  
 */
public class PMStatus extends AbstractStatus{

    PM pm;

    public PMStatus(PM pm, List<Item> items) {
        this.pm = pm;

        for (Item itemId : items) {
            this.metrics.put(itemId, new LinkedList<ItemValue>());
        }
    }

    public PM getPM() {
        return this.pm;
    }

    public void setPM(PM pm) {
        this.pm = pm;
    }

    
    public void printLastStatus(PrintStream out) {

        if (this.getPM() != null) {
            out.println(this.getPM().getName());
        }

        if (this.getMetrics() != null) {
            for (Item item : this.getMetrics().keySet()) {
                ItemValue itemValue = getLastItemValue(item);
                if (itemValue != null) {
                    out.println(item + "\t" + itemValue.getTimestamp() + "\t" + itemValue.getValue());
                }
            }
        }

    }

    public void printLastStatus(Logger log) {

        if (this.getPM() != null) {
            log.log(Level.INFO, this.getPM().getName());
        }

        if (this.getMetrics() != null) {
            for (Item item : this.getMetrics().keySet()) {
                ItemValue itemValue = getLastItemValue(item);
                if (itemValue != null) {
                    log.log(Level.INFO, item + "\t" + itemValue.getTimestamp() + "\t" + itemValue.getValue());
                }
            }
        }

    }

}
