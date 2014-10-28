package eu.eco2clouds.api.bonfire.occi.datamodel;

import static eu.eco2clouds.api.bonfire.occi.Dictionary.NAMESPACE;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
 * Maps the InstanceType/Configurations of the BonFIRE OCCI to Java objects...
 * 
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "configuration", namespace = NAMESPACE)
public class Configuration {
	@XmlAttribute
	private String href;
	@XmlElement(namespace = NAMESPACE)
	private String name;
	@XmlElement(namespace = NAMESPACE)
	private double cpu;
	@XmlElement(namespace = NAMESPACE)
	private long memory;
	@XmlElement(name="link", namespace = NAMESPACE)
	private ArrayList<Link> links;
	
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public double getCpu() {
		return cpu;
	}
	public void setCpu(double cpu) {
		this.cpu = cpu;
	}
	
	public long getMemory() {
		return memory;
	}
	public void setMemory(long memory) {
		this.memory = memory;
	}
	
	public ArrayList<Link> getLinks() {
		return links;
	}
	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}
}
