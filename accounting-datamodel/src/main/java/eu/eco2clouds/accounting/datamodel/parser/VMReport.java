package eu.eco2clouds.accounting.datamodel.parser;

import static eu.eco2clouds.accounting.Dictionary.E2C_NAMESPACE;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.eco2clouds.accounting.datamodel.parser.Link;

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
 *
 * A POJO Object that stores all the information from a VM
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "vm_report", namespace = E2C_NAMESPACE)
public class VMReport {
	@XmlAttribute
	String href;
	
	@XmlElement(name = "power_consumption", namespace = E2C_NAMESPACE)
	private PowerConsumption powerConsumption;
	
	@XmlElement(name = "co2_generated", namespace = E2C_NAMESPACE)
	private CO2Generated co2Generated;

	@XmlElement(name = "link", namespace = E2C_NAMESPACE)
	private List<Link> links;

	public VMReport() {
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public void addLink(String rel, String href, String type) {
		if (links == null)
			links = new ArrayList<Link>();
		links.add(new Link(rel, href, type));
	}
	
	public PowerConsumption getPowerConsumption() {
		return powerConsumption;
	}

	public void setPowerConsumption(PowerConsumption powerConsumption) {
		this.powerConsumption = powerConsumption;
	}

	public CO2Generated getCo2Generated() {
		return co2Generated;
	}

	public void setCo2Generated(CO2Generated co2Generated) {
		this.co2Generated = co2Generated;
	}
}
