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
@XmlRootElement(name = "metric", namespace = Report.E2C_NAMESPACE)
public class ReportMetric {
    
    @XmlAttribute(namespace = Report.E2C_NAMESPACE)
    private String name;
    @XmlElement(name = "value", namespace = Report.E2C_NAMESPACE)
    private List<ItemValue> value;

    public ReportMetric() {
    }

    
    
    public ReportMetric(String metric, List<ItemValue> value) {
        this.name = metric;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ItemValue> getValue() {
        return value;
    }

    public void setValue(List<ItemValue> value) {
        this.value = value;
    }
    
    
    
}
