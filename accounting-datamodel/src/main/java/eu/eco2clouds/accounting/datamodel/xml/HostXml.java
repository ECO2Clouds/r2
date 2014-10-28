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
@XmlRootElement(name = "HOST")
public class HostXml {

	@XmlElement(name = "ID")
	private int id;
	@XmlElement(name = "NAME")
	private String name;
	@XmlElement(name = "STATE")
	private int state;
	@XmlElement(name = "IM_MAD")
	private String imMad;
	@XmlElement(name = "VM_MAD")
	private String vmMad;
	@XmlElement(name = "VN_MAD")
	private String vnMad;
	@XmlElement(name = "LAST_MON_TIME")
	private long lastMonTime;
	@XmlElement(name = "CLUSTER_ID")
	private int clusterId;
	@XmlElement(name = "CLUSTER")
	private String cluster;
	@XmlElement(name = "HOST_SHARE")
	private HostShare hostShare;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getImMad() {
		return imMad;
	}

	public void setImMad(String imMad) {
		this.imMad = imMad;
	}

	public String getVmMad() {
		return vmMad;
	}

	public void setVmMad(String vmMad) {
		this.vmMad = vmMad;
	}

	public String getVnMad() {
		return vnMad;
	}

	public void setVnMad(String vnMad) {
		this.vnMad = vnMad;
	}

	public long getLastMonTime() {
		return lastMonTime;
	}

	public void setLastMonTime(long lastMonTime) {
		this.lastMonTime = lastMonTime;
	}

	public int getClusterId() {
		return clusterId;
	}

	public void setClusterId(int clusterId) {
		this.clusterId = clusterId;
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	public HostShare getHostShare() {
		return hostShare;
	}

	public void setHostShare(HostShare hostShare) {
		this.hostShare = hostShare;
	}

}
