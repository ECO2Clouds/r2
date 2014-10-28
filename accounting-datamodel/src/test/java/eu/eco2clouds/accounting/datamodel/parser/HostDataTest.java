package eu.eco2clouds.accounting.datamodel.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.eco2clouds.accounting.datamodel.parser.HostData;

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
public class HostDataTest {

	@Test
	public void pojo() {
		
		
		HostData hostData = new HostData();
		hostData.setId(1);
		hostData.setCpuUsage(1000);
		hostData.setDiskUsage(12000);
		hostData.setFreeCpu(15000);
		hostData.setFreeMen(14000);
		hostData.setFreeDisk(150);
		//hostData.setHost(null);
		hostData.setMaxCpu(1200);
		hostData.setMaxDisk(20000);
		hostData.setMaxMem(190);
		hostData.setMemUsage(25000);
		hostData.setRunningVms(15);
		hostData.setUsedCpu(50);
		hostData.setUsedDisk(65);
		hostData.setUsedMem(85);
		
		
		assertEquals(1, hostData.getId());
		assertEquals(1000, hostData.getCpuUsage());
		assertEquals(12000, hostData.getDiskUsage());
		assertEquals(15000, hostData.getFreeCpu());
		assertEquals(14000, hostData.getFreeMen());
		assertEquals(150, hostData.getFreeDisk());
		//assertEquals(null, hostData.getHost());
		assertEquals(1200, hostData.getMaxCpu());
		assertEquals(20000, hostData.getMaxDisk());
		assertEquals(190, hostData.getMaxMem());
		assertEquals(25000, hostData.getMemUsage());
		assertEquals(15, hostData.getRunningVms());
		assertEquals(50, hostData.getUsedCpu());
		assertEquals(65, hostData.getUsedDisk());
		assertEquals(85, hostData.getUsedMem());
		
		
		
		HostData hostData2 = new HostData(2,50,50,70,80,90,100,110,120,130,140,150,160,170);

	
		assertEquals(2, hostData2.getId());
		
	}
	

}
