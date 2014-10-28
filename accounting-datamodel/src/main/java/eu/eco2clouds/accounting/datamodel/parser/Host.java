package eu.eco2clouds.accounting.datamodel.parser;

import static eu.eco2clouds.accounting.Dictionary.E2C_NAMESPACE;

import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 * A POJO Object that stores all the information from a Host
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "host", namespace = E2C_NAMESPACE)
public class Host {

	@XmlElement(name = "id", namespace = E2C_NAMESPACE)
	private int id;
	
	@XmlElement(name = "state", namespace = E2C_NAMESPACE)
	private int state;
	
	@XmlElement(name = "name", namespace = E2C_NAMESPACE)
	private String name;
	
	@XmlElement(name = "connected", namespace = E2C_NAMESPACE)
	private boolean connected;
	
	@XmlElement(name = "host-data", namespace = E2C_NAMESPACE)
	private HostData hostData;

	@XmlElement(name="link", namespace = E2C_NAMESPACE)
	private ArrayList<Link> links;

	public Host() {}

	public Host(int id, int state, String name, boolean connected) {
		this.id = id;
		this.state = state;
		this.name = name;
		this.connected = connected;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public HostData getHostData() {
		return hostData;
	}

	public void setHostData(HostData hostData) {
		this.hostData = hostData;
	}

	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	
	public ArrayList<Link> getLinks() {
		return links;
	}

	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}
}
