package eu.eco2clouds.accounting.datamodel.parser;

import static eu.eco2clouds.accounting.Dictionary.E2C_NAMESPACE;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.eco2clouds.accounting.datamodel.parser.Link;

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
 * Represents the results of the energy infrastruture monitoring of an specific testbed
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="vm_monitoring", namespace = E2C_NAMESPACE)
public class VMMonitoring {
	@XmlAttribute
	private String href;

	@XmlElement(name = "cpuload", namespace = E2C_NAMESPACE)
	private CPULoad cpuload;
	
	@XmlElement(name = "cpuutil", namespace = E2C_NAMESPACE)
	private CPUUtil cpuutil;
	
	@XmlElement(name = "diskfree", namespace = E2C_NAMESPACE)
	private DiskFree diskfree;
	
	@XmlElement(name = "disktotal", namespace = E2C_NAMESPACE)
	private DiskTotal disktotal;
	
	@XmlElement(name = "diskusage", namespace = E2C_NAMESPACE)
	private DiskUsage diskusage;
	
	@XmlElement(name = "iops", namespace = E2C_NAMESPACE)
	private Iops iops;
	
	@XmlElement(name = "ioutil", namespace = E2C_NAMESPACE)
	private IoUtil ioutil;
	
	@XmlElement(name = "memfree", namespace = E2C_NAMESPACE)
	private MemFree memfree;
	
	@XmlElement(name = "memtotal", namespace = E2C_NAMESPACE)
	private MemTotal memtotal;
	
	@XmlElement(name = "memused", namespace = E2C_NAMESPACE)
	private MemUsed memused;
	
	@XmlElement(name = "netifin", namespace = E2C_NAMESPACE)
	private NetIfIn netifin;
	
	@XmlElement(name = "netifout", namespace = E2C_NAMESPACE)
	private NetIfOut netifout;
	
	@XmlElement(name = "proc_num", namespace = E2C_NAMESPACE)
	private ProcNum procNum;
	
	@XmlElement(name = "power", namespace = E2C_NAMESPACE)
	private Power power;
	
	@XmlElement(name = "swapfree", namespace = E2C_NAMESPACE)
	private SwapFree swapfree;
	
	@XmlElement(name = "swaptotal", namespace = E2C_NAMESPACE)
	private SwapTotal swaptotal;
	
	@XmlElement(name = "applicationmetric_1", namespace = E2C_NAMESPACE)
	private ApplicationMetric applicationMetric1;
	
	@XmlElement(name = "applicationmetric_2", namespace = E2C_NAMESPACE)
	private ApplicationMetric applicationMetric2;
	
	@XmlElement(name = "applicationmetric_3", namespace = E2C_NAMESPACE)
	private ApplicationMetric applicationMetric3;
	
	@XmlElement(name = "applicationmetric_4", namespace = E2C_NAMESPACE)
	private ApplicationMetric applicationMetric4;

	@XmlElement(name = "link", namespace = E2C_NAMESPACE)
	private List<Link> links;
	
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public CPULoad getCpuload() {
		return cpuload;
	}
	public void setCpuload(CPULoad cpuload) {
		this.cpuload = cpuload;
	}
	public CPUUtil getCpuutil() {
		return cpuutil;
	}
	public void setCpuutil(CPUUtil cpuutil) {
		this.cpuutil = cpuutil;
	}
	public DiskFree getDiskfree() {
		return diskfree;
	}
	public void setDiskfree(DiskFree diskfree) {
		this.diskfree = diskfree;
	}
	public DiskTotal getDisktotal() {
		return disktotal;
	}
	public void setDisktotal(DiskTotal disktotal) {
		this.disktotal = disktotal;
	}
	public DiskUsage getDiskusage() {
		return diskusage;
	}
	public void setDiskusage(DiskUsage diskusage) {
		this.diskusage = diskusage;
	}
	public Iops getIops() {
		return iops;
	}
	public void setIops(Iops iops) {
		this.iops = iops;
	}
	public IoUtil getIoutil() {
		return ioutil;
	}
	public void setIoutil(IoUtil ioutil) {
		this.ioutil = ioutil;
	}
	public MemFree getMemfree() {
		return memfree;
	}
	public void setMemfree(MemFree memfree) {
		this.memfree = memfree;
	}
	public MemTotal getMemtotal() {
		return memtotal;
	}
	public void setMemtotal(MemTotal memtotal) {
		this.memtotal = memtotal;
	}
	public MemUsed getMemused() {
		return memused;
	}
	public void setMemused(MemUsed memused) {
		this.memused = memused;
	}
	public NetIfOut getNetifout() {
		return netifout;
	}
	public void setNetifout(NetIfOut netifout) {
		this.netifout = netifout;
	}
	public ProcNum getProcNum() {
		return procNum;
	}
	public void setProcNum(ProcNum procNum) {
		this.procNum = procNum;
	}
	public Power getPower() {
		return power;
	}
	public void setPower(Power power) {
		this.power = power;
	}
	public SwapFree getSwapfree() {
		return swapfree;
	}
	public void setSwapfree(SwapFree swapfree) {
		this.swapfree = swapfree;
	}
	public SwapTotal getSwaptotal() {
		return swaptotal;
	}
	public void setSwaptotal(SwapTotal swaptotal) {
		this.swaptotal = swaptotal;
	}	
	public NetIfIn getNetifin() {
		return netifin;
	}
	public void setNetifin(NetIfIn netifin) {
		this.netifin = netifin;
	}
	
	public ApplicationMetric getApplicationMetric1() {
		return applicationMetric1;
	}
	public void setApplicationMetric1(ApplicationMetric applicationMetric1) {
		this.applicationMetric1 = applicationMetric1;
	}
	public ApplicationMetric getApplicationMetric2() {
		return applicationMetric2;
	}
	public void setApplicationMetric2(ApplicationMetric applicationMetric2) {
		this.applicationMetric2 = applicationMetric2;
	}
	public ApplicationMetric getApplicationMetric3() {
		return applicationMetric3;
	}
	public void setApplicationMetric3(ApplicationMetric applicationMetric3) {
		this.applicationMetric3 = applicationMetric3;
	}
	public ApplicationMetric getApplicationMetric4() {
		return applicationMetric4;
	}
	public void setApplicationMetric4(ApplicationMetric applicationMetric4) {
		this.applicationMetric4 = applicationMetric4;
	}
}
