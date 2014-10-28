package eu.eco2clouds.accounting.datamodel.parser;

import static eu.eco2clouds.accounting.Dictionary.E2C_NAMESPACE;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import eu.eco2clouds.accounting.datamodel.parser.Co2;


/**
 * 
 * Copyright 2014 ATOS SPAIN S.A. 
 * 
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author: David Garcia Perez. Atos Research and Innovation, Atos SPAIN SA
 * e-mail david.garciaperez@atos.net
 */
 * Implements all methods a monitoring item is going to content...
 * @author David Garcia Perez - AtoS
 */
@XmlTransient
@XmlSeeAlso({Co2.class})
public class AbstractMonitoringItem {
	@XmlElement(namespace = E2C_NAMESPACE)
	private Double value;
	@XmlElement(namespace = E2C_NAMESPACE)
	private Long clock;
	@XmlElement(namespace = E2C_NAMESPACE)
	private String unity;
	@XmlElement(namespace = E2C_NAMESPACE)
	private String name;
	
	public void setValue(Double value) {
		this.value = value;
	}
	public Double getValue() {
		return value;
	}
	
	public void setClock(Long clock) {
		this.clock = clock;
	}
	public Long getClock() {
		return clock;
	}
	
	public void setUnity(String unity) {
		this.unity = unity;
	}
	public String getUnity() {
		return unity;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
}
