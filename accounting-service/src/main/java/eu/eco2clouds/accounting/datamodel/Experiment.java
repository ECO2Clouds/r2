package eu.eco2clouds.accounting.datamodel;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
@Entity
@Table(name = "experiments")
@NamedQueries( { 
@NamedQuery(name = "Experiment.findAll", query = "SELECT p FROM Experiment p"),
@NamedQuery(name = "Experiment.findListExperimentByGroups", query = "SELECT p FROM Experiment p where p.bonfireGroupId in :groups"),
@NamedQuery(name = "Experiment.findExperimentByGroups", query = "SELECT p FROM Experiment p where p.id = :id and p.bonfireGroupId in :groups"),
@NamedQuery(name = "Experiment.findVMId", query = "SELECT p FROM Experiment p "
		+ "where p.bonfireExperimentId=:experimentId "
		+ "and  :vmId in ( SELECT id FROM p.vMs  )"),
@NamedQuery(name = "Experiment.findVMByBonfireUrl", query = "SELECT p FROM Experiment p "
				+ "where p.bonfireExperimentId=:experimentId "
				+ "and  :vmBonfireUrl in ( SELECT bonfireUrl FROM p.vMs ) "),
@NamedQuery(name = "Experiment.findExperimentId", query = "SELECT p FROM Experiment p "
				+ "where p.bonfireExperimentId=:experimentId "),
@NamedQuery(name = "Experiment.findListVMByGroups", query = "SELECT p FROM Experiment p "
		+ "where p.bonfireExperimentId=:experimentId "
		+ "and p.bonfireGroupId in :groups"),
@NamedQuery(name = "Experiment.getRunningExperiments", query = "SELECT p FROM Experiment p "
				+ "where p.startTime < :actualDate "
				+ "and p.endTime > :actualDate"),
@NamedQuery(name = "Experiment.getExperimentWithVmIP", query = "SELECT p FROM Experiment p "
                  + "where p.startTime < :actualDate "
                  + "and p.endTime > :actualDate " 
				  + "and :vmIp in ( SELECT ip FROM p.vMs)" )})
public class Experiment implements Serializable {
	private static final long serialVersionUID = 6789355327199983922L;
	private int id;
	private String bonfireGroupId;
	private String bonfireUserId;
	private long bonfireExperimentId;
	private Set<VM> vMs;
	private long startTime;
	private long endTime;
	private String applicationProfile;
	private String submittedExperimentDescriptor;
	private long managedExperimentId;
	private String status;

	public Experiment() {}

	public Experiment(int id, String bonfireGroupId, String bonfireUserId,
			long bonfireExperimentId, long startTime,
			long endTime, String applicationProfile,
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

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "bonfire_group_id")
	public String getBonfireGroupId() {
		return bonfireGroupId;
	}

	public void setBonfireGroupId(String bonfireGroupId) {
		this.bonfireGroupId = bonfireGroupId;
	}

	@Column(name = "bonfire_user_id")
	public String getBonfireUserId() {
		return bonfireUserId;
	}

	public void setBonfireUserId(String bonfireUserId) {
		this.bonfireUserId = bonfireUserId;
	}

	@Column(name = "bonfire_experiment_id")
	public long getBonfireExperimentId() {
		return bonfireExperimentId;
	}

	public void setBonfireExperimentId(long bonfireExperimentId) {
		this.bonfireExperimentId = bonfireExperimentId;
	}

	@Column(name = "start_time")
	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	@Column(name = "end_time")
	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	@Column(name = "application_profile", length=900000)
	public String getApplicationProfile() {
		return applicationProfile;
	}

	public void setApplicationProfile(String applicationProfile) {
		this.applicationProfile = applicationProfile;
	}

	@Column(name = "submitted_experiment_descriptor", length=900000)
	public String getSubmittedExperimentDescriptor() {
		return submittedExperimentDescriptor;
	}

	public void setSubmittedExperimentDescriptor(String submittedExperimentDescriptor) {
		this.submittedExperimentDescriptor = submittedExperimentDescriptor;
	}

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "experiment_id", referencedColumnName="id", nullable = true)
	public Set<VM> getvMs() {
		return vMs;
	}

	public void setvMs(Set<VM> vMs) {
		this.vMs = vMs;
	}

	@Column(name = "managed_experiment_id")
	public long getManagedExperimentId() {
		return managedExperimentId;
	}

	public void setManagedExperimentId(long managedExperimentId) {
		this.managedExperimentId = managedExperimentId;
	}
	
	@Column(name = "status")
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
}
