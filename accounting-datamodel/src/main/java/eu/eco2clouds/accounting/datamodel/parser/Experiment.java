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
public class Experiment {
	
	@XmlAttribute
	private String href;

	@XmlElement(name="id", namespace = E2C_NAMESPACE)
	private Integer id;
	
	@XmlElement(name="bonfire-group-id", namespace = E2C_NAMESPACE)
	private String bonfireGroupId;
	
	@XmlElement(name="bonfire-user-id", namespace = E2C_NAMESPACE)
	private String bonfireUserId;
	
	@XmlElement(name="bonfire-experiment-id", namespace = E2C_NAMESPACE)
	private Long bonfireExperimentId;
	
	@XmlElement(name="managed-experiment-id", namespace = E2C_NAMESPACE)
	private Long managedExperimentId;
	
	@XmlElement(name="status", namespace = E2C_NAMESPACE)
	private String status;

	@XmlElement(name="vMs", namespace = E2C_NAMESPACE)
	private List<VM> vMs;
	
	@XmlElement(name="start-time", namespace = E2C_NAMESPACE)
	private Long startTime;
	
	@XmlElement(name="end-time", namespace = E2C_NAMESPACE)
	private Long endTime;
	
	@XmlElement(name="application-profile", namespace = E2C_NAMESPACE)
	private String applicationProfile;
	
	@XmlElement(name="submitted-experiment-descriptor", namespace = E2C_NAMESPACE)
	private String submittedExperimentDescriptor;
	
	@XmlElement(name="link", namespace = E2C_NAMESPACE)
	private List<Link> links;

	public Experiment() {}

	public Experiment(int id, String bonfireGroupId, String bonfireUserId,
			Long bonfireExperimentId, Long startTime,
			Long endTime, String applicationProfile,
			String submittedExperimentDescriptor) {

		this.id = id;
		this.bonfireGroupId = bonfireGroupId;
		this.bonfireUserId = bonfireUserId;
		this.bonfireExperimentId = bonfireExperimentId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.applicationProfile = applicationProfile;
		this.submittedExperimentDescriptor = submittedExperimentDescriptor;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public String getBonfireGroupId() {
		return bonfireGroupId;
	}

	public void setBonfireGroupId(String bonfireGroupId) {
		this.bonfireGroupId = bonfireGroupId;
	}

	
	public String getBonfireUserId() {
		return bonfireUserId;
	}

	public void setBonfireUserId(String bonfireUserId) {
		this.bonfireUserId = bonfireUserId;
	}


	public Long getBonfireExperimentId() {
		return bonfireExperimentId;
	}

	public void setBonfireExperimentId(Long bonfireExperimentId) {
		this.bonfireExperimentId = bonfireExperimentId;
	}

	
	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}


	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	
	public String getApplicationProfile() {
		return applicationProfile;
	}

	public void setApplicationProfile(String applicationProfile) {
		this.applicationProfile = applicationProfile;
	}


	public String getSubmittedExperimentDescriptor() {
		return submittedExperimentDescriptor;
	}

	public void setSubmittedExperimentDescriptor(String submittedExperimentDescriptor) {
		this.submittedExperimentDescriptor = submittedExperimentDescriptor;
	}
	
	public Long getManagedExperimentId() {
		return managedExperimentId;
	}

	public void setManagedExperimentId(Long managedExperimentId) {
		this.managedExperimentId = managedExperimentId;
	}

	public List<VM> getvMs() {
		return vMs;
	}

	public void setvMs(List<VM> vMs) {
		this.vMs = vMs;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
