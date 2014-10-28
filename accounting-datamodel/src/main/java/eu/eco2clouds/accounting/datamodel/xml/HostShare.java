package eu.eco2clouds.accounting.datamodel.xml;

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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "HOST_SHARE")
public class HostShare {

	@XmlElement(name = "DISK_USAGE")
	private long diskUsage;
	@XmlElement(name = "MEM_USAGE")
	private int memUsage;
	@XmlElement(name = "CPU_USAGE")
	private long cpuUsage;
	@XmlElement(name = "MAX_DISK")
	private long maxDisk;
	@XmlElement(name = "MAX_MEM")
	private long maxMem;
	@XmlElement(name = "MAX_CPU")
	private long maxCpu;
	@XmlElement(name = "FREE_DISK")
	private long freeDisk;
	@XmlElement(name = "FREE_MEM")
	private long freeMem;
	@XmlElement(name = "FREE_CPU")
	private long freeCpu;
	@XmlElement(name = "USED_DISK")
	private long usedDisk;
	@XmlElement(name = "USED_MEM")
	private long usedMem;
	@XmlElement(name = "USED_CPU")
	private int usedCpu;
	@XmlElement(name = "RUNNING_VMS")
	private int runningVms;

	public long getDiskUsage() {
		return diskUsage;
	}

	public void setDiskUsage(long diskUsage) {
		this.diskUsage = diskUsage;
	}

	public int getMemUsage() {
		return memUsage;
	}

	public void setMemUsage(int memUsage) {
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

	public long getFreeMem() {
		return freeMem;
	}

	public void setFreeMem(long freeMem) {
		this.freeMem = freeMem;
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

	public int getUsedCpu() {
		return usedCpu;
	}

	public void setUsedCpu(int usedCpu) {
		this.usedCpu = usedCpu;
	}

	public int getRunningVms() {
		return runningVms;
	}

	public void setRunningVms(int runningVms) {
		this.runningVms = runningVms;
	}

}
