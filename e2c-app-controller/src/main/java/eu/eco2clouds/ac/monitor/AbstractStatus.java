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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *  
 */
public class AbstractStatus {

    protected HashMap<Item, List<ItemValue>> metrics = new HashMap<Item, List<ItemValue>>();

    public Set<Item> getItems() {
        return metrics.keySet();
    }

    public HashMap<Item, List<ItemValue>> getMetrics() {
        return metrics;
    }

    public void setMetrics(HashMap<Item, List<ItemValue>> metrics) {
        this.metrics = metrics;
    }

    public void addValueOfItem(Item item, ItemValue value) {
        this.metrics.get(item).add(value);
    }

    public double getItemAverage(Item item) {
        double avg = 0;
        int remove = 0;
        if (this.getMetrics().get(item) == null) {
            //Logger.getLogger(AbstractStatus.class.getName()).log(Level.INFO, "no items");
            return 0;
        } else {
            double size = this.getMetrics().get(item).size();
            //Logger.getLogger(AbstractStatus.class.getName()).log(Level.INFO, "number of items " + size);
            if (size > 0) {
                for (ItemValue value : this.getMetrics().get(item)) {
                    if (value == null) {
                        remove++;
                    } else {
                        avg = avg + value.getValue();
                    }
                }
                //Logger.getLogger(AbstractStatus.class.getName()).log(Level.INFO, "average of items " + avg /(double)(size-remove));
                return avg / (double)(size - remove);

            } else {
                return 0;
            }
        }
    }

    public ItemValue getLastItemValue(Item item) {
        if (this.getMetrics().get(item).size() > 0) {
            return this.getMetrics().get(item).get(this.getMetrics().get(item).size() - 1);
        } else {
            return null;
        }
    }

    public List<ItemValue> getLastNItemValues(Item item, int n) {

        List<ItemValue> values = new LinkedList<ItemValue>();

        if (this.getMetrics().get(item).size() > 0) {
            for (int i = 0; i < n; i++) {
                try {
                    values.add(this.getMetrics().get(item).get(this.getMetrics().get(item).size() - i - 1));
                } catch (IndexOutOfBoundsException e) {
                    Logger.getLogger(AbstractStatus.class.getName()).log(Level.FINER, "index out of bound for metric " + item.getName());
                }
            }
            return values;
        } else {
            return null;
        }
    }

    public Item findItemById(String id) {
        for (Item item : this.getMetrics().keySet()) {
            if (item.getId().equalsIgnoreCase(id)) {
                return item;
            }
        }
        return null;
    }

    public ItemValue getItemValueById(String id, int index) {
        List<ItemValue> iV = this.getMetrics().get(findItemById(id));
        return iV.get(index);
    }

}
