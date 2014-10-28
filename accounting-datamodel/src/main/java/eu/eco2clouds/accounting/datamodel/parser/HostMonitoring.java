package eu.eco2clouds.accounting.datamodel.parser;

import static eu.eco2clouds.accounting.Dictionary.E2C_NAMESPACE;

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
 * Represents the results of the energy infrastruture monitoring of an specific
 * testbed
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "host_monitoring", namespace = E2C_NAMESPACE)
public class HostMonitoring {
	@XmlAttribute
	private String href;

/*	@XmlElement(name = "dummy_energy_metric", namespace = E2C_NAMESPACE)
	private DummyEnergyMetric dummy;*/

	@XmlElement(name = "co2_generation_rate", namespace = E2C_NAMESPACE)
	private CO2GenerationRate co2GenerationRate;

	@XmlElement(name = "processor_load", namespace = E2C_NAMESPACE)
	private ProcessorLoad processorLoad;

	@XmlElement(name = "free_memory", namespace = E2C_NAMESPACE)
	private FreeMemory freeMemory;

	@XmlElement(name = "total_memory", namespace = E2C_NAMESPACE)
	private TotalMemory totalMemory;

	@XmlElement(name = "free_swap_space", namespace = E2C_NAMESPACE)
	private FreeSwapSpace freeSwapSpace;

	@XmlElement(name = "number_of_vms_running", namespace = E2C_NAMESPACE)
	private NumberOfVMsRunning numberOfVMsRunning;

	@XmlElement(name = "aggregate_energy", namespace = E2C_NAMESPACE)
	private AggregateEnergy aggregateEnergy;

	@XmlElement(name = "apparent_power", namespace = E2C_NAMESPACE)
	private ApparentPower apparentPower;

	@XmlElement(name = "real_power", namespace = E2C_NAMESPACE)
	private RealPower realPower;

	@XmlElement(name = "number_of_processes", namespace = E2C_NAMESPACE)
	private NumberOfProcesses numberOfProcesses;

	@XmlElement(name = "cpu_user_time_avg1", namespace = E2C_NAMESPACE)
	private CPUUserTimeAvg1 cpuUserTimeAvg1;

	@XmlElement(name = "running_vms", namespace = E2C_NAMESPACE)
	private RunningVMs runningVMs;

	@XmlElement(name = "co2_producted", namespace = E2C_NAMESPACE)
	private Co2Producted co2Producted;

	@XmlElement(name = "aggregate_energy_usage", namespace = E2C_NAMESPACE)
	private AggregateEnergyUsage aggregateEnergyUsage;

	@XmlElement(name = "apparent_power_usage", namespace = E2C_NAMESPACE)
	private ApparentPowerUsage apparentPowerUsage;

	@XmlElement(name = "real_power_usage", namespace = E2C_NAMESPACE)
	private RealPowerUsage realPowerUsage;

	@XmlElement(name = "link", namespace = E2C_NAMESPACE)
	private List<Link> links;

	//new metrics - David Rojo Antona
	@XmlElement(name = "Availability", namespace = E2C_NAMESPACE)
	private Availability availability;
	
	@XmlElement(name = "co2_generation_per_30s", namespace = E2C_NAMESPACE)
	private Co2GenerationPer30s co2GenerationPer30s;
	
	@XmlElement(name = "cpu_utilization", namespace = E2C_NAMESPACE)
	private CPUUtilization cpuUtilization;
	
	@XmlElement(name = "disk_iops", namespace = E2C_NAMESPACE)
	private DiskIOPS diskIOPS;
	
	@XmlElement(name = "free_space_on_srv", namespace = E2C_NAMESPACE)
	private FreeSpaceOnSrv freeSpaceOnSrv;
	
	@XmlElement(name = "power_consumption", namespace = E2C_NAMESPACE)
	private PowerConsumption powerConsumption;
	
	@XmlElement(name = "co2_raw", namespace = E2C_NAMESPACE)
	private Co2Raw co2Raw;
	
	@XmlElement(name = "disk_read_write", namespace = E2C_NAMESPACE)
	private DiskReadWrite diskReadWrite;
	
	@XmlElement(name = "host_availability", namespace = E2C_NAMESPACE)
	private HostAvailability hostAvailability;
	
	@XmlElement(name = "one_availability", namespace = E2C_NAMESPACE)
	private OneAvailability oneAvailability;
	
	@XmlElement(name = "power_current_va_aggregated", namespace = E2C_NAMESPACE)
	private PowerCurrentvaAggregated powerCurrentvaAggregated;
	
	@XmlElement(name = "power_current_w_aggregated", namespace = E2C_NAMESPACE)
	private PowerCurrentwAggregated powerCurrentwAggregated;

	@XmlElement(name = "power_total_aggregated", namespace = E2C_NAMESPACE)
	private PowerTotalAggregated powerTotalAggregated;
	
	@XmlElement(name = "system_cpu_load", namespace = E2C_NAMESPACE)
	private SystemCPULoad systemCPULoad;
	
	@XmlElement(name = "system_cpu_util_perc", namespace = E2C_NAMESPACE)
	private SystemCPUUtilPerc systemCPUUtilPerc;
	
	@XmlElement(name = "system_power_consumption", namespace = E2C_NAMESPACE)
	private SystemPowerConsumption systemPowerConsumption;
	
	@XmlElement(name = "system_swap_size", namespace = E2C_NAMESPACE)
	private SystemSwapSize systemSwapSize;
	
	@XmlElement(name = "vm_memory_size", namespace = E2C_NAMESPACE)
	private VMMemorySizeFree vMMemorySizeFree;
	
	@XmlElement(name = "vm_memory_total", namespace = E2C_NAMESPACE)
	private VMMemorySizeTotal vMMemorySizeTotal;
	
	@XmlElement(name = "custom_cpu_utilization", namespace = E2C_NAMESPACE)
	private CustomCpuUtilization customCpuUtilization;
	
	@XmlElement(name = "custom_vfs_iops", namespace = E2C_NAMESPACE)
	private CustomVfsIops customVfsIops;
	
	@XmlElement(name = "custom_vms_running", namespace = E2C_NAMESPACE)
	private CustomVMSRunning customVMsRunning;
	
	@XmlElement(name = "power_co2_generated", namespace = E2C_NAMESPACE)
	private PowerCo2Generated powerCo2Generated;
	
	@XmlElement(name = "power_currentwh_real", namespace = E2C_NAMESPACE)
	private PowerCurrentwhReal powerCurrentwhReal;
	
	@XmlElement(name = "proc_num", namespace = E2C_NAMESPACE)
	private ProcNum procNum;
	
	@XmlElement(name = "system_cpu_util", namespace = E2C_NAMESPACE)
	private SystemCpuUtil systemCpuUtil;
	
	public SystemCpuUtil getSystemCpuUtil() {
		return systemCpuUtil;
	}

	public void setSystemCpuUtil(SystemCpuUtil systemCpuUtil) {
		this.systemCpuUtil = systemCpuUtil;
	}

	public ProcNum getProcNum() {
		return procNum;
	}

	public void setProcNum(ProcNum procNum) {
		this.procNum = procNum;
	}

	public PowerCurrentwhReal getPowerCurrentwhReal() {
		return powerCurrentwhReal;
	}

	public void setPowerCurrentwhReal(PowerCurrentwhReal powerCurrentwhReal) {
		this.powerCurrentwhReal = powerCurrentwhReal;
	}

	public PowerCo2Generated getPowerCo2Generated() {
		return powerCo2Generated;
	}

	public void setPowerCo2Generated(PowerCo2Generated powerCo2Generated) {
		this.powerCo2Generated = powerCo2Generated;
	}

	public CustomVMSRunning getCustomVMsRunning() {
		return customVMsRunning;
	}

	public void setCustomVMsRunning(CustomVMSRunning customVMsRunning) {
		this.customVMsRunning = customVMsRunning;
	}

	public CustomVfsIops getCustomVfsIops() {
		return customVfsIops;
	}

	public void setCustomVfsIops(CustomVfsIops customVfsIops) {
		this.customVfsIops = customVfsIops;
	}

	public CustomCpuUtilization getCustomCpuUtilization() {
		return customCpuUtilization;
	}

	public void setCustomCpuUtilization(CustomCpuUtilization customCpuUtilization) {
		this.customCpuUtilization = customCpuUtilization;
	}

	public VMMemorySizeTotal getvMMemorySizeTotal() {
		return vMMemorySizeTotal;
	}

	public void setvMMemorySizeTotal(VMMemorySizeTotal vMMemorySizeTotal) {
		this.vMMemorySizeTotal = vMMemorySizeTotal;
	}

	public VMMemorySizeFree getvMMemorySizeFree() {
		return vMMemorySizeFree;
	}

	public void setvMMemorySizeFree(VMMemorySizeFree vMMemorySizeFree) {
		this.vMMemorySizeFree = vMMemorySizeFree;
	}

	public SystemSwapSize getSystemSwapSize() {
		return systemSwapSize;
	}

	public void setSystemSwapSize(SystemSwapSize systemSwapSize) {
		this.systemSwapSize = systemSwapSize;
	}

	public SystemPowerConsumption getSystemPowerConsumption() {
		return systemPowerConsumption;
	}

	public void setSystemPowerConsumption(
			SystemPowerConsumption systemPowerConsumption) {
		this.systemPowerConsumption = systemPowerConsumption;
	}

	public SystemCPUUtilPerc getSystemCPUUtilPerc() {
		return systemCPUUtilPerc;
	}

	public void setSystemCPUUtilPerc(SystemCPUUtilPerc systemCPUUtilPerc) {
		this.systemCPUUtilPerc = systemCPUUtilPerc;
	}

	public OneAvailability getOneAvailability() {
		return oneAvailability;
	}

	public void setOneAvailability(OneAvailability oneAvailability) {
		this.oneAvailability = oneAvailability;
	}

	public SystemCPULoad getSystemCPULoad() {
		return systemCPULoad;
	}

	public void setSystemCPULoad(SystemCPULoad systemCPULoad) {
		this.systemCPULoad = systemCPULoad;
	}

	public PowerTotalAggregated getPowerTotalAggregated() {
		return powerTotalAggregated;
	}

	public void setPowerTotalAggregated(PowerTotalAggregated powerTotalAggregated) {
		this.powerTotalAggregated = powerTotalAggregated;
	}

	public PowerCurrentwAggregated getPowerCurrentwAggregated() {
		return powerCurrentwAggregated;
	}

	public void setPowerCurrentwAggregated(
			PowerCurrentwAggregated powerCurrentwAggregated) {
		this.powerCurrentwAggregated = powerCurrentwAggregated;
	}

	public PowerCurrentvaAggregated getPowerCurrentvaAggregated() {
		return powerCurrentvaAggregated;
	}

	public void setPowerCurrentvaAggregated(
			PowerCurrentvaAggregated powerCurrentvaAggregated) {
		this.powerCurrentvaAggregated = powerCurrentvaAggregated;
	}

	public HostAvailability getHostAvailability() {
		return hostAvailability;
	}

	public void setHostAvailability(HostAvailability hostAvailability) {
		this.hostAvailability = hostAvailability;
	}

	public DiskReadWrite getDiskReadWrite() {
		return diskReadWrite;
	}

	public void setDiskReadWrite(DiskReadWrite diskReadWrite) {
		this.diskReadWrite = diskReadWrite;
	}

	public Co2Raw getCo2Raw() {
		return co2Raw;
	}

	public void setCo2Raw(Co2Raw co2Raw) {
		this.co2Raw = co2Raw;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

/*	public DummyEnergyMetric getDummy() {
		return dummy;
	}

	public void setDummy(DummyEnergyMetric dummy) {
		this.dummy = dummy;
	}*/

	public CO2GenerationRate getCo2GenerationRate() {
		return co2GenerationRate;
	}

	public void setCo2GenerationRate(CO2GenerationRate co2GenerarationRate) {
		this.co2GenerationRate = co2GenerarationRate;
	}

	public ProcessorLoad getProcessorLoad() {
		return processorLoad;
	}

	public void setProcessorLoad(ProcessorLoad processorLoad) {
		this.processorLoad = processorLoad;
	}

	public FreeMemory getFreeMemory() {
		return freeMemory;
	}

	public void setFreeMemory(FreeMemory freeMemory) {
		this.freeMemory = freeMemory;
	}

	public TotalMemory getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(TotalMemory totalMemory) {
		this.totalMemory = totalMemory;
	}

	public FreeSwapSpace getFreeSwapSpace() {
		return freeSwapSpace;
	}

	public void setFreeSwapSpace(FreeSwapSpace freeSwapSpace) {
		this.freeSwapSpace = freeSwapSpace;
	}

	public NumberOfVMsRunning getNumberOfVMsRunning() {
		return numberOfVMsRunning;
	}

	public void setNumberOfVMsRunning(NumberOfVMsRunning numberOfVMsRunning) {
		this.numberOfVMsRunning = numberOfVMsRunning;
	}

	public AggregateEnergy getAggregateEnergy() {
		return aggregateEnergy;
	}

	public void setAggregateEnergy(AggregateEnergy aggregateEnergy) {
		this.aggregateEnergy = aggregateEnergy;
	}

	public ApparentPower getApparentPower() {
		return apparentPower;
	}

	public void setApparentPower(ApparentPower apparentPower) {
		this.apparentPower = apparentPower;
	}

	public RealPower getRealPower() {
		return realPower;
	}

	public void setRealPower(RealPower realPower) {
		this.realPower = realPower;
	}

	public NumberOfProcesses getNumberOfProcesses() {
		return numberOfProcesses;
	}

	public void setNumberOfProcesses(NumberOfProcesses numberOfProcesses) {
		this.numberOfProcesses = numberOfProcesses;
	}

	public CPUUserTimeAvg1 getCpuUserTimeAvg1() {
		return cpuUserTimeAvg1;
	}

	public void setCpuUserTimeAvg1(CPUUserTimeAvg1 cpuUserTimeAvg1) {
		this.cpuUserTimeAvg1 = cpuUserTimeAvg1;
	}

	public RunningVMs getRunningVMs() {
		return runningVMs;
	}

	public void setRunningVMs(RunningVMs runningVMs) {
		this.runningVMs = runningVMs;
	}

	public Co2Producted getCo2Producted() {
		return co2Producted;
	}

	public void setCo2Producted(Co2Producted co2Producted) {
		this.co2Producted = co2Producted;
	}

	public AggregateEnergyUsage getAggregateEnergyUsage() {
		return aggregateEnergyUsage;
	}

	public void setAggregateEnergyUsage(
			AggregateEnergyUsage aggregateEnergyUsage) {
		this.aggregateEnergyUsage = aggregateEnergyUsage;
	}

	public ApparentPowerUsage getApparentPowerUsage() {
		return apparentPowerUsage;
	}

	public void setApparentPowerUsage(ApparentPowerUsage apparentPowerUsage) {
		this.apparentPowerUsage = apparentPowerUsage;
	}

	public RealPowerUsage getRealPowerUsage() {
		return realPowerUsage;
	}

	public void setRealPowerUsage(RealPowerUsage realPowerUsage) {
		this.realPowerUsage = realPowerUsage;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public Availability getAvailability() {
		return availability;
	}

	public void setAvailability(Availability availability) {
		this.availability = availability;
	}

	public Co2GenerationPer30s getCo2GenerationPer30s() {
		return co2GenerationPer30s;
	}

	public void setCo2GenerationPer30s(Co2GenerationPer30s co2GenerationPer30s) {
		this.co2GenerationPer30s = co2GenerationPer30s;
	}

	public CPUUtilization getCpuUtilization() {
		return cpuUtilization;
	}

	public void setCpuUtilization(CPUUtilization cpuUtilization) {
		this.cpuUtilization = cpuUtilization;
	}

	public DiskIOPS getDiskIOPS() {
		return diskIOPS;
	}

	public void setDiskIOPS(DiskIOPS diskIOPS) {
		this.diskIOPS = diskIOPS;
	}

	public FreeSpaceOnSrv getFreeSpaceOnSrv() {
		return freeSpaceOnSrv;
	}

	public void setFreeSpaceOnSrv(FreeSpaceOnSrv freeSpaceOnSrv) {
		this.freeSpaceOnSrv = freeSpaceOnSrv;
	}

	public PowerConsumption getPowerConsumption() {
		return powerConsumption;
	}

	public void setPowerConsumption(PowerConsumption powerConsumption) {
		this.powerConsumption = powerConsumption;
	}
	
}
