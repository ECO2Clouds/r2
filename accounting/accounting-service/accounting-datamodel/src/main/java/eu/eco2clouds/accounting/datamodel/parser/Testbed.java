package eu.eco2clouds.accounting.datamodel.parser;

import static eu.eco2clouds.accounting.Dictionary.E2C_NAMESPACE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.eco2clouds.accounting.datamodel.xml.HostXml;

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
 * POJO that represents a testbed object
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="testbed", namespace = E2C_NAMESPACE)
public class Testbed{

	@XmlElement(name="id", namespace = E2C_NAMESPACE)
	private int id;
	
	@XmlElement(name="name", namespace = E2C_NAMESPACE)
	private String name;
	
	@XmlElement(name="url", namespace = E2C_NAMESPACE)
	private String url;
	
//	@XmlElement(name="hosts", namespace = E2C_NAMESPACE)
//	private List<HostXml> hosts;
	
	@XmlElement(name="link", namespace = E2C_NAMESPACE)
	private ArrayList<Link> links;

	public Testbed() {}

	public Testbed(int id, String name, String url) {
		this.id = id;
		this.name = name;
		this.url = url;
	}
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * @return the url of the testbed
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * Sets the url of the testbed to get the status data
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * @return the BonFIRE name of the testbed
	 */
	public String getName() {
		return name;
	}
	/**
	 * Sets the assigned name of the Testbed in BonFIRE
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

//	public List<HostXml> getHosts() {
//		return hosts;
//	}
//
//	public void setHosts(List<HostXml> hosts) {
//		this.hosts = hosts;
//	}
	
	public ArrayList<Link> getLinks() {
		return links;
	}

	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}
}
