package eu.eco2clouds.accounting.datamodel.parser;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

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
public class HostMonitoringTest {

	@Test
	public void pojo() {
		HostMonitoring hostMonitoring = new HostMonitoring();

		CO2GenerationRate co2GenerateRate = new CO2GenerationRate();
		ProcessorLoad processorLoad = new ProcessorLoad();
		FreeMemory freeMemory = new FreeMemory();
		TotalMemory totalMemory = new TotalMemory();
		FreeSwapSpace freeSwapSpace = new FreeSwapSpace();
		NumberOfVMsRunning numberOfVMsRunning = new NumberOfVMsRunning();
		AggregateEnergy aggregateEnergy = new AggregateEnergy();
		ApparentPower apparentPower = new ApparentPower();
		RealPower realPower = new RealPower();
		NumberOfProcesses numberOfProcesses = new NumberOfProcesses();
		CPUUserTimeAvg1 cpuUserTimeAvg1 = new CPUUserTimeAvg1();
		RunningVMs runningVMs = new RunningVMs();
		Co2Producted co2Producted = new Co2Producted();
		AggregateEnergyUsage aggregateEnergyUsage = new AggregateEnergyUsage();
		ApparentPowerUsage apparentPowerUsage = new ApparentPowerUsage();
		RealPowerUsage realPowerUsage = new RealPowerUsage();
		Co2Raw co2Raw = new Co2Raw();
		DiskReadWrite dRW = new DiskReadWrite();
		HostAvailability ha = new HostAvailability();
		PowerTotalAggregated powerTotal = new PowerTotalAggregated(); 
		hostMonitoring.setHostAvailability(ha);
		hostMonitoring.setPowerTotalAggregated(powerTotal);
		hostMonitoring.setCo2GenerationRate(co2GenerateRate);
		hostMonitoring.setProcessorLoad(processorLoad);
		hostMonitoring.setFreeMemory(freeMemory);
		hostMonitoring.setTotalMemory(totalMemory);
		hostMonitoring.setFreeSwapSpace(freeSwapSpace);
		hostMonitoring.setNumberOfVMsRunning(numberOfVMsRunning);
		hostMonitoring.setAggregateEnergy(aggregateEnergy);
		hostMonitoring.setApparentPower(apparentPower);
		hostMonitoring.setRealPower(realPower);
		hostMonitoring.setNumberOfProcesses(numberOfProcesses);
		hostMonitoring.setCpuUserTimeAvg1(cpuUserTimeAvg1);
		hostMonitoring.setRunningVMs(runningVMs);
		hostMonitoring.setCo2Producted(co2Producted);
		hostMonitoring.setAggregateEnergyUsage(aggregateEnergyUsage);
		hostMonitoring.setApparentPowerUsage(apparentPowerUsage);
		hostMonitoring.setRealPowerUsage(realPowerUsage);
		hostMonitoring.setCo2Raw(co2Raw);
		hostMonitoring.setDiskReadWrite(dRW);
		
		List<Link> links = new ArrayList<Link>();
		hostMonitoring.setLinks(links);
		hostMonitoring.setHref("href");

		assertEquals("href", hostMonitoring.getHref());
		assertEquals(dRW, hostMonitoring.getDiskReadWrite());
		assertEquals(powerTotal, hostMonitoring.getPowerTotalAggregated());
		assertEquals(co2GenerateRate, hostMonitoring.getCo2GenerationRate());
		assertEquals(processorLoad, hostMonitoring.getProcessorLoad());
		assertEquals(freeMemory, hostMonitoring.getFreeMemory());
		assertEquals(totalMemory, hostMonitoring.getTotalMemory());
		assertEquals(freeSwapSpace, hostMonitoring.getFreeSwapSpace());
		assertEquals(numberOfVMsRunning, hostMonitoring.getNumberOfVMsRunning());
		assertEquals(aggregateEnergy, hostMonitoring.getAggregateEnergy());
		assertEquals(apparentPower, hostMonitoring.getApparentPower());
		assertEquals(realPower, hostMonitoring.getRealPower());
		assertEquals(numberOfProcesses, hostMonitoring.getNumberOfProcesses());
		assertEquals(cpuUserTimeAvg1, hostMonitoring.getCpuUserTimeAvg1());
		assertEquals(runningVMs, hostMonitoring.getRunningVMs());
		assertEquals(co2Producted, hostMonitoring.getCo2Producted());
		assertEquals(ha, hostMonitoring.getHostAvailability());
		assertEquals(aggregateEnergyUsage,
				hostMonitoring.getAggregateEnergyUsage());
		assertEquals(apparentPowerUsage,
				hostMonitoring.getApparentPowerUsage());
		assertEquals(realPowerUsage, hostMonitoring.getRealPowerUsage());

		// assertEquals(dummy, hostMonitoring.getDummy());

		assertEquals(links, hostMonitoring.getLinks());
		assertEquals(co2Raw, hostMonitoring.getCo2Raw());
	}

	@Test
	public void xmlToObject() throws Exception {
		String testbedMonitoringXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<host_monitoring xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/testbeds/fr-inria/hosts/crocket0/monitoring\">"
				+ "<co2_generation_rate>"
				+ "<value>1.0</value>"
				+ "<clock>10</clock>"
				+ "<unity>units</unity>"
				+ "<name>name</name>"
				+ "</co2_generation_rate>"

				+ "<processor_load>"
				+ "<value>2.0</value>"
				+ "<clock>20</clock>"
				+ "<unity>units</unity>"
				+ "<name>name</name>"
				+ "</processor_load>"

				+ "<free_memory>"
				+ "<value>3.0</value>"
				+ "<clock>30</clock>"
				+ "<unity>units</unity>"
				+ "<name>name</name>"
				+ "</free_memory>"

				+ "<total_memory>"
				+ "<value>4.0</value>"
				+ "<clock>40</clock>"
				+ "<unity>units</unity>"
				+ "<name>name</name>"
				+ "</total_memory>"

				+ "<free_swap_space>"
				+ "<value>5.0</value>"
				+ "<clock>50</clock>"
				+ "<unity>units</unity>"
				+ "<name>name</name>"
				+ "</free_swap_space>"

				+ "<number_of_vms_running>"
				+ "<value>6.0</value>"
				+ "<clock>60</clock>"
				+ "<unity>units</unity>"
				+ "<name>name</name>"
				+ "</number_of_vms_running>"

				+ "<aggregate_energy>"
				+ "<value>7.0</value>"
				+ "<clock>70</clock>"
				+ "<unity>units</unity>"
				+ "<name>name</name>"
				+ "</aggregate_energy>"

				+ "<apparent_power>"
				+ "<value>8.0</value>"
				+ "<clock>80</clock>"
				+ "<unity>units</unity>"
				+ "<name>name</name>"
				+ "</apparent_power>"

				+ "<real_power>"
				+ "<value>9.0</value>"
				+ "<clock>90</clock>"
				+ "<unity>units</unity>"
				+ "<name>name</name>"
				+ "</real_power>"

				+ "<number_of_processes>"
				+ "<value>10.0</value>"
				+ "<clock>100</clock>"
				+ "<unity>units</unity>"
				+ "<name>name</name>"
				+ "</number_of_processes>"

				+ "<cpu_user_time_avg1>"
				+ "<value>11.0</value>"
				+ "<clock>110</clock>"
				+ "<unity>units</unity>"
				+ "<name>name</name>"
				+ "</cpu_user_time_avg1>"

				+ "<running_vms>"
				+ "<value>12.0</value>"
				+ "<clock>120</clock>"
				+ "<unity>units</unity>"
				+ "<name>name</name>"
				+ "</running_vms>"

				+ "<co2_producted>"
				+ "<value>13.0</value>"
				+ "<clock>130</clock>"
				+ "<unity>units</unity>"
				+ "<name>name</name>"
				+ "</co2_producted>"

				+ "<aggregate_energy_usage>"
				+ "<value>14.0</value>"
				+ "<clock>140</clock>"
				+ "<unity>units</unity>"
				+ "<name>name</name>"
				+ "</aggregate_energy_usage>"

				+ "<apparent_power_usage>"
				+ "<value>15.0</value>"
				+ "<clock>150</clock>"
				+ "<unity>units</unity>"
				+ "<name>name</name>"
				+ "</apparent_power_usage>"

				+ "<real_power_usage>"
				+ "<value>16.0</value>"
				+ "<clock>160</clock>"
				+ "<unity>units</unity>"
				+ "<name>name</name>"
				+ "</real_power_usage>"

				+ "<link rel=\"testbed\" href=\"/testbeds/fr-inria\"/>"
				+ "</host_monitoring>";

		JAXBContext jaxbContext = JAXBContext.newInstance(HostMonitoring.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		HostMonitoring hostMonitoring = (HostMonitoring) jaxbUnmarshaller
				.unmarshal(new StringReader(testbedMonitoringXML));

		assertEquals("/testbeds/fr-inria/hosts/crocket0/monitoring",
				hostMonitoring.getHref());
		assertEquals(10l, hostMonitoring.getCo2GenerationRate().getClock()
				.longValue());
		assertEquals(20l, hostMonitoring.getProcessorLoad().getClock()
				.longValue());
		assertEquals(30l, hostMonitoring.getFreeMemory().getClock().longValue());
		assertEquals(40l, hostMonitoring.getTotalMemory().getClock().longValue());
		assertEquals(50l, hostMonitoring.getFreeSwapSpace().getClock().longValue());
		assertEquals(60l, hostMonitoring.getNumberOfVMsRunning().getClock().longValue());
		assertEquals(70l, hostMonitoring.getAggregateEnergy().getClock().longValue());
		assertEquals(80l, hostMonitoring.getApparentPower().getClock().longValue());
		assertEquals(90l, hostMonitoring.getRealPower().getClock().longValue());
		assertEquals(100l, hostMonitoring.getNumberOfProcesses().getClock().longValue());
		assertEquals(110l, hostMonitoring.getCpuUserTimeAvg1().getClock().longValue());
		assertEquals(120l, hostMonitoring.getRunningVMs().getClock().longValue());
		assertEquals(130l, hostMonitoring.getCo2Producted().getClock().longValue());
		assertEquals(140l, hostMonitoring.getAggregateEnergyUsage().getClock().longValue());
		assertEquals(150l, hostMonitoring.getApparentPowerUsage().getClock().longValue());
		assertEquals(160l, hostMonitoring.getRealPowerUsage().getClock().longValue());

		assertEquals("/testbeds/fr-inria", hostMonitoring.getLinks().get(0)
				.getHref());
		assertEquals("testbed", hostMonitoring.getLinks().get(0).getRel());
	}
}
