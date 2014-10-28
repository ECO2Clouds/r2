package eu.eco2clouds.accounting.datamodel.parser;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import eu.eco2clouds.accounting.datamodel.parser.Co2;
import eu.eco2clouds.accounting.datamodel.parser.Coal;
import eu.eco2clouds.accounting.datamodel.parser.Cost;
import eu.eco2clouds.accounting.datamodel.parser.Gaz;
import eu.eco2clouds.accounting.datamodel.parser.Hydraulic;
import eu.eco2clouds.accounting.datamodel.parser.Link;
import eu.eco2clouds.accounting.datamodel.parser.Nuclear;
import eu.eco2clouds.accounting.datamodel.parser.Oil;
import eu.eco2clouds.accounting.datamodel.parser.Other;
import eu.eco2clouds.accounting.datamodel.parser.PDUFr;
import eu.eco2clouds.accounting.datamodel.parser.TestbedMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.Total;
import eu.eco2clouds.accounting.datamodel.parser.Wind;

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
public class VMMonitoringTest {

	@Test
	public void pojo() {
		VMMonitoring vmMonitoring = new VMMonitoring();
		vmMonitoring.setHref("href");
		CPULoad cpuload = new CPULoad();
		vmMonitoring.setCpuload(cpuload);
		CPUUtil cpuutil = new CPUUtil();
		vmMonitoring.setCpuutil(cpuutil);
		DiskFree diskfree = new DiskFree();
		vmMonitoring.setDiskfree(diskfree);
		DiskTotal disktotal = new DiskTotal();
		vmMonitoring.setDisktotal(disktotal);
		DiskUsage diskusage = new DiskUsage();
		vmMonitoring.setDiskusage(diskusage);
		Iops iops = new Iops();
		vmMonitoring.setIops(iops);
		IoUtil ioutil = new IoUtil();
		vmMonitoring.setIoutil(ioutil);
		MemFree memfree = new MemFree();
		vmMonitoring.setMemfree(memfree);
		MemTotal memtotal = new MemTotal();
		vmMonitoring.setMemtotal(memtotal);
		MemUsed memused = new MemUsed();
		vmMonitoring.setMemused(memused);
		NetIfIn netifin = new NetIfIn();
		vmMonitoring.setNetifin(netifin);
		NetIfOut netifout = new NetIfOut();
		vmMonitoring.setNetifout(netifout);
		Power power = new Power();
		vmMonitoring.setPower(power);
		ProcNum procNum = new ProcNum();
		vmMonitoring.setProcNum(procNum);
		SwapFree swapfree = new SwapFree();
		vmMonitoring.setSwapfree(swapfree);
		SwapTotal swaptotal = new SwapTotal();
		vmMonitoring.setSwaptotal(swaptotal);

		List<Link> links = new ArrayList<Link>();
		vmMonitoring.setLinks(links);
		
		assertEquals("href", vmMonitoring.getHref());
		assertEquals(cpuload, vmMonitoring.getCpuload());
		assertEquals(cpuutil, vmMonitoring.getCpuutil());
		assertEquals(diskfree, vmMonitoring.getDiskfree());
		assertEquals(disktotal, vmMonitoring.getDisktotal());
		assertEquals(diskusage, vmMonitoring.getDiskusage());
		assertEquals(iops, vmMonitoring.getIops());
		assertEquals(ioutil, vmMonitoring.getIoutil());
		assertEquals(memfree, vmMonitoring.getMemfree());
		assertEquals(memtotal, vmMonitoring.getMemtotal());
		assertEquals(memused, vmMonitoring.getMemused());
		assertEquals(netifin, vmMonitoring.getNetifin());
		assertEquals(netifout, vmMonitoring.getNetifout());
		assertEquals(power, vmMonitoring.getPower());
		assertEquals(procNum, vmMonitoring.getProcNum());
		assertEquals(swapfree, vmMonitoring.getSwapfree());
		assertEquals(swaptotal, vmMonitoring.getSwaptotal());
	}
}
