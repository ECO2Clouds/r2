package eu.eco2clouds.accounting.datamodel;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@Table(name = "host_data")
@NamedQueries( { 
@NamedQuery(name = "HostData.findAll", query = "SELECT p FROM HostData p")})
public class HostData implements Serializable {
	private static final long serialVersionUID = -1656584744070826460L;
	private int id;
	private long diskUsage;
	private long memUsage;
	private long cpuUsage;
	private long maxDisk;
	private long maxMem;
	private long maxCpu;
	private long freeDisk;
	private long freeMen;
	private long freeCpu;
	private long usedDisk;
	private long usedMem;
	private long usedCpu;
	private int runningVms;
	private Host host;
	private Action action;
	
	public HostData() {}

	public HostData(int id, long diskUsage, long memUsage,long cpuUsage,long maxDisk,long maxMem,long maxCpu,long freeDisk,long freeMen,long freeCpu,long usedDisk,long usedMem,long usedCpu, int runningVms)
	{
		this.id = id;
		this.diskUsage = diskUsage;
		this.memUsage = memUsage;
		this.cpuUsage = cpuUsage;
		this.maxDisk = maxDisk;
		this.maxMem = maxMem;
		this.maxCpu = maxCpu;
		this.freeDisk = freeDisk;
		this.freeMen = freeMen;
		this.freeCpu = freeCpu;
		this.usedDisk = usedDisk;
		this.usedMem = usedMem;
		this.usedCpu = usedCpu;
		this.runningVms=runningVms;
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

	@Column(name = "disk_usage")
	public long getDiskUsage() {
		return diskUsage;
	}

	public void setDiskUsage(long diskUsage) {
		this.diskUsage = diskUsage;
	}

	@Column(name = "mem_usage")
	public long getMemUsage() {
		return memUsage;
	}

	public void setMemUsage(long memUsage) {
		this.memUsage = memUsage;
	}

	@Column(name = "cpu_usage")
	public long getCpuUsage() {
		return cpuUsage;
	}

	public void setCpuUsage(long cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	@Column(name = "max_disk")
	public long getMaxDisk() {
		return maxDisk;
	}

	public void setMaxDisk(long maxDisk) {
		this.maxDisk = maxDisk;
	}

	@Column(name = "max_mem")
	public long getMaxMem() {
		return maxMem;
	}

	public void setMaxMem(long maxMem) {
		this.maxMem = maxMem;
	}

	@Column(name = "max_cpu")
	public long getMaxCpu() {
		return maxCpu;
	}

	public void setMaxCpu(long maxCpu) {
		this.maxCpu = maxCpu;
	}

	@Column(name = "free_disk")
	public long getFreeDisk() {
		return freeDisk;
	}

	public void setFreeDisk(long freeDisk) {
		this.freeDisk = freeDisk;
	}

	@Column(name = "free_mem")
	public long getFreeMen() {
		return freeMen;
	}

	public void setFreeMen(long freeMen) {
		this.freeMen = freeMen;
	}

	@Column(name = "free_cpu")
	public long getFreeCpu() {
		return freeCpu;
	}

	public void setFreeCpu(long freeCpu) {
		this.freeCpu = freeCpu;
	}

	@Column(name = "used_disk")
	public long getUsedDisk() {
		return usedDisk;
	}

	public void setUsedDisk(long usedDisk) {
		this.usedDisk = usedDisk;
	}

	@Column(name = "used_mem")
	public long getUsedMem() {
		return usedMem;
	}

	public void setUsedMem(long usedMem) {
		this.usedMem = usedMem;
	}

	@Column(name = "used_cpu")
	public long getUsedCpu() {
		return usedCpu;
	}

	public void setUsedCpu(long usedCpu) {
		this.usedCpu = usedCpu;
	}

	@Column(name = "running_vms")
	public int getRunningVms() {
		return runningVms;
	}

	public void setRunningVms(int runningVms) {
		this.runningVms = runningVms;
	}

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="host_id")
	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="action_id")
	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}
}

