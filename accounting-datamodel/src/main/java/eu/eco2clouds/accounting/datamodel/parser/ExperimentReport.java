package eu.eco2clouds.accounting.datamodel.parser;

import static eu.eco2clouds.accounting.Dictionary.E2C_NAMESPACE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.eco2clouds.accounting.datamodel.parser.Link;
import eu.eco2clouds.accounting.datamodel.parser.VM;


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
 * A POJO Object that stores all the information from an Experiment
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="experiment", namespace = E2C_NAMESPACE)
public class ExperimentReport {
	
	@XmlAttribute
	private String href;
	
	@XmlElement(name = "power_consumption", namespace = E2C_NAMESPACE)
	private PowerConsumption powerConsumption;
	
	@XmlElement(name = "co2_generated", namespace = E2C_NAMESPACE)
	private CO2Generated co2Generated;

	@XmlElement(name="vm_report", namespace = E2C_NAMESPACE) 
	private List<VMReport> vmReports;
	
	@XmlElement(name="link", namespace = E2C_NAMESPACE)
	private List<Link> links;

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
	
	public List<VMReport> getVmReports() {
		return vmReports;
	}

	public void setVmReports(List<VMReport> vmReports) {
		this.vmReports = vmReports;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public void addLink(String rel, String href, String type) {
		if(links == null) links = new ArrayList<Link>();
		links.add(new Link(rel, href, type));
	}
	
	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}
}
