package eu.eco2clouds.accounting.datamodel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.eco2clouds.accounting.datamodel.HostData;

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
		hostData.setCpuUsage(1);
		hostData.setDiskUsage(2);
		hostData.setFreeCpu(3);
		hostData.setFreeDisk(4);
		hostData.setFreeMen(5);
		hostData.setId(6);
		hostData.setMaxCpu(7);
		hostData.setMaxDisk(8);
		hostData.setMaxMem(9);
		hostData.setRunningVms(10);
		hostData.setUsedCpu(11);
		hostData.setUsedDisk(12);
		hostData.setUsedMem(13);
		
		assertEquals(1, hostData.getCpuUsage());
		assertEquals(2, hostData.getDiskUsage());
		assertEquals(3, hostData.getFreeCpu());
		assertEquals(4, hostData.getFreeDisk());
		assertEquals(5, hostData.getFreeMen());
		assertEquals(6, hostData.getId());
		assertEquals(7, hostData.getMaxCpu());
		assertEquals(8, hostData.getMaxDisk());
		assertEquals(9, hostData.getMaxMem());
		assertEquals(10, hostData.getRunningVms());
		assertEquals(11, hostData.getUsedCpu());
		assertEquals(12, hostData.getUsedDisk());
		assertEquals(13, hostData.getUsedMem());
		
		
		HostData hostData2 = new HostData(2,50,50,70,80,90,100,110,120,130,140,150,160,170);

	
		assertEquals(2, hostData2.getId());
		
		
	}
}
