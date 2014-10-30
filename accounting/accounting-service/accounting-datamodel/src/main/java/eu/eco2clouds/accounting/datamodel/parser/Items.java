package eu.eco2clouds.accounting.datamodel.parser;

import static eu.eco2clouds.accounting.Dictionary.E2C_NAMESPACE;


import java.util.ArrayList;
import java.util.List;

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
 *
 * POJO representation of the items inside a ECO2Clouds Collection
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "items", namespace = E2C_NAMESPACE)
public class Items {
	@XmlAttribute
	private int offset;
	@XmlAttribute
	private int total;
	
    @XmlElement(name="testbed", namespace = E2C_NAMESPACE)
    private List<Testbed> testbeds;
	
	@XmlElement(name="experiment", namespace = E2C_NAMESPACE)
	private List<Experiment> experiments;
	
	@XmlElement(name="vm", namespace = E2C_NAMESPACE)
	private List<VM> vMs;
	
	@XmlElement(name="host_monitoring", namespace = E2C_NAMESPACE)
	private List<HostMonitoring> hostMonitorings;
	
	@XmlElement(name="testbed_monitoring", namespace = E2C_NAMESPACE)
	private List<TestbedMonitoring> testbedMonitorings;
	
	@XmlElement(name="host", namespace = E2C_NAMESPACE)
	private List<Host> hosts;
	
	public List<Host> getHosts() {
		return hosts;
	}
	public void setHosts(List<Host> hosts) {
		this.hosts = hosts;
	}
	public List<VM> getvMs() {
		return vMs;
	}
	public void setvMs(List<VM> vMs) {
		this.vMs = vMs;
	}
	public List<Experiment> getExperiments() {
		return experiments;
	}
	public void setExperiments(List<Experiment> experiments) {
		this.experiments = experiments;
	}
	
	public void addExperiment(Experiment experiment) {
		if(experiments == null) experiments = new ArrayList<Experiment>();
		experiments.add(experiment);
	}
	
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	
	public List<HostMonitoring> getHostMonitorings() {
		return hostMonitorings;
	}
	public void setHostMonitorings(List<HostMonitoring> hostMonitorings) {
		this.hostMonitorings = hostMonitorings;
	}
	public void addHostMonitoring(HostMonitoring hostMonitoring) {
		if(hostMonitorings == null) hostMonitorings = new ArrayList<HostMonitoring>();
		hostMonitorings.add(hostMonitoring);
	}
	
	public List<TestbedMonitoring> getTestbedMonitorings() {
		return testbedMonitorings;
	}
	public void setTestbedMonitorings(List<TestbedMonitoring> testbedMonitorings) {
		this.testbedMonitorings = testbedMonitorings;
	}
	public void addTestbedMonitoring(TestbedMonitoring testbedMonitoring) {
		if(testbedMonitorings == null) testbedMonitorings = new ArrayList<TestbedMonitoring>();
		testbedMonitorings.add(testbedMonitoring);
	}
	public List<Testbed> getTestbeds() {
		return testbeds;
	}
	public void setTestbeds(List<Testbed> testbeds) {
		this.testbeds = testbeds;
	}
}
