package eu.eco2clouds.accounting.datamodel.parser;

import static eu.eco2clouds.accounting.Dictionary.E2C_NAMESPACE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.eco2clouds.accounting.datamodel.parser.Action;
import eu.eco2clouds.accounting.datamodel.parser.Link;
import eu.eco2clouds.accounting.datamodel.parser.VMHost;

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
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "vm", namespace = E2C_NAMESPACE)
public class VM {
	@XmlAttribute
	String href;
	@XmlElement(name = "id", namespace = E2C_NAMESPACE)
	private Integer id;
	@XmlElement(name = "host", namespace = E2C_NAMESPACE)
	private String host;
	@XmlElement(name = "ip", namespace = E2C_NAMESPACE)
	private String ip;
	@XmlElement(name = "name", namespace = E2C_NAMESPACE)
	private String name;
	@XmlElement(name = "bonfire-id", namespace = E2C_NAMESPACE)
	private String bonfireId;
	@XmlElement(name = "bonfire-url", namespace = E2C_NAMESPACE)
	private String bonfireUrl;
	@XmlElement(name = "nic", namespace = E2C_NAMESPACE)
	private List<Nic> nics;
	
	@XmlElement(name = "actions", namespace = E2C_NAMESPACE)
	private Set<Action> actions;

	@XmlElement(name = "vMhosts", namespace = E2C_NAMESPACE)
	private Set<VMHost> vMhosts;

	@XmlElement(name = "link", namespace = E2C_NAMESPACE)
	private List<Link> links;

	public VM() {
	}

	public VM(int id, String bonfireUrl) {
		this.id = id;
		this.bonfireUrl = bonfireUrl;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBonfireUrl() {
		return bonfireUrl;
	}

	public void setBonfireUrl(String bonfireUrl) {
		this.bonfireUrl = bonfireUrl;
	}

	public Set<Action> getActions() {
		return actions;
	}

	public void setActions(Set<Action> actions) {
		this.actions = actions;
	}

	public Set<VMHost> getvMhosts() {
		return vMhosts;
	}

	public void setvMhosts(Set<VMHost> vMhosts) {
		this.vMhosts = vMhosts;
	}

	public String getHost() {
		return host;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setHost(String host) {
		this.host = host;
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
	
	public List<Nic> getNics() {
		return nics;
	}

	public void setNics(List<Nic> nics) {
		this.nics = nics;
	}
	
	public void addNic(String ip, String mac, String network) {
		if(nics == null)
			nics = new ArrayList<Nic>();
		Nic nic = new Nic();
		nic.setIp(ip);
		nic.setMac(mac);
		nic.setNetwork(network);
		nics.add(nic);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBonfireId() {
		return bonfireId;
	}

	public void setBonfireId(String bonfireId) {
		this.bonfireId = bonfireId;
	}
}
