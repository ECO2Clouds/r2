package eu.eco2clouds.api.bonfire.occi.datamodel;

import static eu.eco2clouds.api.bonfire.occi.Dictionary.NAMESPACE;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

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
@XmlRootElement(name = "compute", namespace = NAMESPACE)
public class Compute implements Resource {
	@XmlAttribute
	private String href;
	@XmlAttribute
	private String name;
	@XmlElement(namespace = NAMESPACE)
	private String id;
	@XmlElement(namespace = NAMESPACE)
	private String cpu;
	@XmlElement(namespace = NAMESPACE)
	private String memory;
	@XmlElement(namespace = NAMESPACE)
	private UserId uname;
	@XmlElement(namespace = NAMESPACE)
	private String groups;
	@XmlElement(name="name", namespace = NAMESPACE)
	private String computeName;
	@XmlElement(name="instance_type", namespace = NAMESPACE)
	private ComputeInstanceType computeInstanceType;
	// This is not mapped in the XML compute
	@XmlTransient
	private Configuration configuration;
	@XmlElement(namespace = NAMESPACE)
	private String state;
	@XmlElement(name="disk", namespace = NAMESPACE)
	private ArrayList<Disk> disks;;
	@XmlElement(name="nic", namespace = NAMESPACE)
	private ArrayList<Nic> nics;
	//private Context context;
	@XmlElement(namespace = NAMESPACE)
	private String host;
	@XmlElement(name="link", namespace = NAMESPACE)
	private ArrayList<Link> links;
	
	
	public String getName() {
		if(name == null) this.name = computeName;
		return name;
	}
	public void setName(String name) {
		this.computeName = name;
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
	
	public String getCpu() {
		return cpu;
	}
	public void setCpu(String cpu) {
		this.cpu = cpu;
	}
	
	public String getMemory() {
		return memory;
	}
	public void setMemory(String memory) {
		this.memory = memory;
	}
	
	public UserId getUname() {
		return uname;
	}
	public void setUname(UserId uname) {
		this.uname = uname;
	}
	
	public String getGroups() {
		return groups;
	}
	public void setGroups(String groups) {
		this.groups = groups;
	}
	
	public String getComputeName() {
		if(computeName == null) this.computeName = name;
		return computeName;
	}
	public void setComputeName(String computeName) {
		this.name = computeName;
		this.computeName = computeName;
	}
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	
	public ArrayList<Link> getLinks() {
		return links;
	}
	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}
	
	public ArrayList<Disk> getDisks() {
		return disks;
	}
	public void setDisks(ArrayList<Disk> disks) {
		this.disks = disks;
	}
	
	public ArrayList<Nic> getNics() {
		return nics;
	}
	public void setNics(ArrayList<Nic> nics) {
		this.nics = nics;
	}
	
	public Configuration getConfiguration() {
		if(computeInstanceType != null) {
			// We create a new configuration, this needs to get updated from the API to get
			// correct values of CPU, Memory, etc... this need a bit more of thought!!!
			Configuration configuration = new Configuration();
			configuration.setName(computeInstanceType.getValue());
			configuration.setHref(computeInstanceType.getHref());
			
			this.configuration = configuration;
		}
		
		return this.configuration;
	}
	public void setConfiguration(Configuration configuration) {
		ComputeInstanceType computeInstanceType = new ComputeInstanceType();
		computeInstanceType.setHref(configuration.getHref());
		computeInstanceType.setValue(configuration.getName());
		
		this.computeInstanceType = computeInstanceType;
		this.configuration = configuration;
	}
	
	public ComputeInstanceType getComputeInstanceType() {
		if(configuration != null) {
			// We create a new configuration, this needs to get updated from the API to get
			// correct values of CPU, Memory, etc... this need a bit more of thought!!!
			ComputeInstanceType computeInstanceType = new ComputeInstanceType();
			computeInstanceType.setHref(configuration.getHref());
			computeInstanceType.setValue(configuration.getName());
			
			this.computeInstanceType = computeInstanceType;
		}
		
		return this.computeInstanceType;
	}
	public void setComputeInstanceType(ComputeInstanceType computeInstanceType) {	
		Configuration configuration = new Configuration();
		configuration.setName(computeInstanceType.getValue());
		configuration.setHref(computeInstanceType.getHref());
		
		this.configuration = configuration;
		this.computeInstanceType = computeInstanceType;
	}
	
	public void addNic(Nic nic) {
		if(nics == null) nics = new ArrayList<Nic>();
		nics.add(nic);
	}
	
	public void addDisk(Disk disk) {
		if(disks == null) disks = new ArrayList<Disk>();
		disks.add(disk);
	}
	
	public void addLink(Link link) {
		if(links == null) links = new ArrayList<Link>();
		links.add(link);
	}
}
