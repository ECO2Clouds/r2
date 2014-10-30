package eu.eco2clouds.api.bonfire.occi.datamodel;

import static eu.eco2clouds.api.bonfire.occi.Dictionary.NAMESPACE;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
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
@XmlRootElement(name = "experiment", namespace = NAMESPACE)
public class Experiment implements Resource {
	@XmlAttribute
	private String href;
	@XmlElement(namespace = NAMESPACE)
	private String id;
	@XmlElement(namespace = NAMESPACE)
	private String name;
	@XmlElement(namespace = NAMESPACE)
	private String description;
	@XmlElement(namespace = NAMESPACE)
	private long walltime;
	@XmlElement(name="user_id", namespace = NAMESPACE)
	private String userId;
	@XmlElement(namespace = NAMESPACE)
	private String groups;
	@XmlElement(namespace = NAMESPACE)
	private String status;
	@XmlElement(name="routing_key", namespace = NAMESPACE)
	private String routingKey;
	@XmlElement(name="aggregator_password", namespace = NAMESPACE)
	private String aggregatorPassword;
	@XmlElement(name="created_at", namespace = NAMESPACE)
	private String createdAt;
	@XmlElement(name="updated_at", namespace = NAMESPACE)
	private String updatedAt;
	@XmlElementWrapper(name="computes", namespace = NAMESPACE)
	@XmlElement(name="compute", namespace = NAMESPACE)
	private ArrayList<Compute> computes;
	@XmlElementWrapper(name="storages", namespace = NAMESPACE)
	@XmlElement(name="storage", namespace = NAMESPACE)
	private ArrayList<Storage> storages;
	@XmlElement(name="link", namespace = NAMESPACE)
	private ArrayList<Link> links;
	
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
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public long getWalltime() {
		return walltime;
	}
	public void setWalltime(long walltime) {
		this.walltime = walltime;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getGroups() {
		return groups;
	}
	public void setGroups(String groups) {
		this.groups = groups;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getRoutingKey() {
		return routingKey;
	}
	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}
	
	public String getAggregatorPassword() {
		return aggregatorPassword;
	}
	public void setAggregatorPassword(String aggregatorPassword) {
		this.aggregatorPassword = aggregatorPassword;
	}
	
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	
	public String getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	public ArrayList<Link> getLinks() {
		return links;
	}
	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}
	
	public ArrayList<Compute> getComputes() {
		return computes;
	}
	public void setComputes(ArrayList<Compute> computes) {
		this.computes = computes;
	}
	
	public ArrayList<Storage> getStorages() {
		return storages;
	}
	public void setStorages(ArrayList<Storage> storages) {
		this.storages = storages;
	}
}
