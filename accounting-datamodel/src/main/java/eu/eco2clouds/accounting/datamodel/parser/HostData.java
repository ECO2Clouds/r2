package eu.eco2clouds.accounting.datamodel.parser;

import static eu.eco2clouds.accounting.Dictionary.E2C_NAMESPACE;

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
 * A POJO Object that stores all the information from a HostData
 * 
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="host-data", namespace = E2C_NAMESPACE)
public class HostData {

	@XmlElement(name="id", namespace = E2C_NAMESPACE)
	private int id;
	
	@XmlElement(name="disk-Usage", namespace = E2C_NAMESPACE)
	private long diskUsage;
	
	@XmlElement(name="mem-usage", namespace = E2C_NAMESPACE)
	private long memUsage;
	
	@XmlElement(name="cpu-usage", namespace = E2C_NAMESPACE)
	private long cpuUsage;
	
	@XmlElement(name="max-disk", namespace = E2C_NAMESPACE)
	private long maxDisk;
	
	@XmlElement(name="max-mem", namespace = E2C_NAMESPACE)
	private long maxMem;
	
	@XmlElement(name="max-cpu", namespace = E2C_NAMESPACE)
	private long maxCpu;
	
	@XmlElement(name="free-disk", namespace = E2C_NAMESPACE)
	private long freeDisk;
	
	@XmlElement(name="free-men", namespace = E2C_NAMESPACE)
	private long freeMen;
	
	@XmlElement(name="free-cpu", namespace = E2C_NAMESPACE)
	private long freeCpu;
	
	@XmlElement(name="used-disk", namespace = E2C_NAMESPACE)
	private long usedDisk;
	
	@XmlElement(name="used-mem", namespace = E2C_NAMESPACE)
	private long usedMem;
	
	@XmlElement(name="used-cpu", namespace = E2C_NAMESPACE)
	private long usedCpu;
	
	@XmlElement(name="running-vms", namespace = E2C_NAMESPACE)
	private int runningVms;
	
//	@XmlElement(name="host", namespace = E2C_NAMESPACE)
//	private HostXml host;
	
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


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public long getDiskUsage() {
		return diskUsage;
	}

	public void setDiskUsage(long diskUsage) {
		this.diskUsage = diskUsage;
	}

	
	public long getMemUsage() {
		return memUsage;
	}

	public void setMemUsage(long memUsage) {
		this.memUsage = memUsage;
	}

	
	public long getCpuUsage() {
		return cpuUsage;
	}

	public void setCpuUsage(long cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	
	public long getMaxDisk() {
		return maxDisk;
	}

	public void setMaxDisk(long maxDisk) {
		this.maxDisk = maxDisk;
	}

	
	public long getMaxMem() {
		return maxMem;
	}

	public void setMaxMem(long maxMem) {
		this.maxMem = maxMem;
	}

	
	public long getMaxCpu() {
		return maxCpu;
	}

	public void setMaxCpu(long maxCpu) {
		this.maxCpu = maxCpu;
	}

	
	public long getFreeDisk() {
		return freeDisk;
	}

	public void setFreeDisk(long freeDisk) {
		this.freeDisk = freeDisk;
	}

	
	public long getFreeMen() {
		return freeMen;
	}

	public void setFreeMen(long freeMen) {
		this.freeMen = freeMen;
	}

	
	public long getFreeCpu() {
		return freeCpu;
	}

	public void setFreeCpu(long freeCpu) {
		this.freeCpu = freeCpu;
	}

	
	public long getUsedDisk() {
		return usedDisk;
	}

	public void setUsedDisk(long usedDisk) {
		this.usedDisk = usedDisk;
	}


	public long getUsedMem() {
		return usedMem;
	}

	public void setUsedMem(long usedMem) {
		this.usedMem = usedMem;
	}

	
	public long getUsedCpu() {
		return usedCpu;
	}

	public void setUsedCpu(long usedCpu) {
		this.usedCpu = usedCpu;
	}

	
	public int getRunningVms() {
		return runningVms;
	}

	public void setRunningVms(int runningVms) {
		this.runningVms = runningVms;
	}

//	public HostXml getHost() {
//		return host;
//	}
//
//	public void setHost(HostXml host) {
//		this.host = host;
//	}
//
//	public HostXml getAction() {
//		// TODO Auto-generated method stub
//		return null;
//	}
}

