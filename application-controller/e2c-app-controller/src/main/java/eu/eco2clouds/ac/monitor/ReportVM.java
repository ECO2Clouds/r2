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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 *  
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "vm", namespace = Report.E2C_NAMESPACE)
public class ReportVM {
    
    @XmlAttribute(namespace = Report.E2C_NAMESPACE)
    private String name;
    @XmlElement(name = "metric", namespace = Report.E2C_NAMESPACE)
    private List<ReportMetric> metric;

    public ReportVM() {
    }

    
    public ReportVM(String name, HashMap<Item, List<ItemValue>> metric) {
        
        this.name = name;
        this.metric = new LinkedList<ReportMetric>();
        
        for (Item item: metric.keySet()) {
            ReportMetric m = new ReportMetric(item.getName(), metric.get(item));
            this.metric.add(m);
        }
        
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<ReportMetric> getMetric() {
        return metric;
    }

    public void setMetric(List<ReportMetric> metric) {
        this.metric = metric;
    }
    
    
}
