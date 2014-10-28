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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "storage", namespace = NAMESPACE)
public class Storage implements Resource {
	@XmlAttribute
	private String href;
	@XmlAttribute
	private String name;
	@XmlElement(namespace = NAMESPACE)
	private String id;
	@XmlElement(name="name", namespace = NAMESPACE)
	private String storageName;
	@XmlElement(name="user_id", namespace = NAMESPACE)
	private UserId userId;
	@XmlElement(namespace = NAMESPACE)
	private String groups;
	@XmlElement(namespace = NAMESPACE)
	private String state;
	@XmlElement(namespace = NAMESPACE)
	private String type;
	@XmlElement(namespace = NAMESPACE)
	private String description;
	@XmlElement(namespace = NAMESPACE)
	private String size;
	@XmlElement(namespace = NAMESPACE)
	private String fstype;
	@XmlElement(name="public", namespace = NAMESPACE)
	private String publicStorage;
	@XmlElement(namespace = NAMESPACE)
	private String persistent;
	@XmlElement(name="link", namespace = NAMESPACE)
	private ArrayList<Link> links;
	
	public String getName() {
		if(name == null) {
			this.name = storageName;
		}
		
		return name;
	}
	public void setName(String name) {
		this.storageName = name;
		this.name = name;
	}
	
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getStorageName() {
		if(storageName == null) {
			this.storageName = name;
		}
		
		return storageName;
	}
	public void setStorageName(String storageName) {
		this.name = storageName;
		this.storageName = storageName;
	}
	
	public UserId getUserId() {
		return userId;
	}
	public void setUserId(UserId userId) {
		this.userId = userId;
	}
	
	public String getGroups() {
		return groups;
	}
	public void setGroups(String groups) {
		this.groups = groups;
	}
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	
	public String getFstype() {
		return fstype;
	}
	public void setFstype(String fstype) {
		this.fstype = fstype;
	}
	
	public String getPublicStorage() {
		return publicStorage;
	}
	public void setPublicStorage(String publicStorage) {
		this.publicStorage = publicStorage;
	}
	
	public String getPersistent() {
		return persistent;
	}
	public void setPersistent(String persistent) {
		this.persistent = persistent;
	}
	
	public ArrayList<Link> getLinks() {
		return links;
	}
	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void addLink(Link link) {
		if(links == null) links = new ArrayList<Link>();
		links.add(link);
	}
}
