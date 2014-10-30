package eu.eco2clouds.api.bonfire.occi.datamodel;

import static eu.eco2clouds.api.bonfire.occi.Dictionary.NAMESPACE;

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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "network", namespace = NAMESPACE)
public class Network implements Resource {
	@XmlAttribute
	private String href;
	@XmlAttribute
	private String name;
	@XmlElement(namespace = NAMESPACE)
	private String id;
	@XmlElement(name="name", namespace = NAMESPACE)
	private String networkName;
	@XmlElement(namespace = NAMESPACE)
	private String groups;
	@XmlElement(namespace = NAMESPACE)
	private UserId uname;
	@XmlElement(name="public", namespace = NAMESPACE)
	private String publicNetwork;
	
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	
	public String getName() {
		if(name == null) {
			this.name = networkName;
		}
		
		return name;
	}
	public void setName(String name) {
		this.networkName = name;
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getNetworkName() {
		if(networkName == null) {
			this.networkName = name;
		}
		
		return networkName;
	}
	public void setNetworkName(String networkName) {
		this.name = networkName;
		this.networkName = networkName;
	}
	
	public String getGroups() {
		return groups;
	}
	public void setGroups(String groups) {
		this.groups = groups;
	}
	
	public UserId getUname() {
		return uname;
	}
	public void setUname(UserId uname) {
		this.uname = uname;
	}
	
	public String getPublicNetwork() {
		return publicNetwork;
	}
	public void setPublicNetwork(String publicNetwork) {
		this.publicNetwork = publicNetwork;
	}
}
