package eu.eco2clouds.accounting.service.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.eco2clouds.accounting.datamodel.Host;
import eu.eco2clouds.accounting.datamodel.HostData;
import eu.eco2clouds.accounting.service.HostDAO;
import eu.eco2clouds.accounting.service.HostDataDAO;

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
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/scheduler-db-JPA-test-context.xml")
public class HostDataDAOJpaTest extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	private HostDataDAO hostDataDAO;
	@Autowired
	private HostDAO hostDAO;

	
	@Test
	public void notNull() {
		if(hostDataDAO == null || hostDAO == null) fail();
	}
	
	@Test
	public void saveGetAll() {
		int size = hostDataDAO.getAll().size();
		
		HostData hostData = new HostData();
		hostData.setCpuUsage(1);
		hostData.setDiskUsage(2);
		hostData.setFreeCpu(3);
		hostData.setFreeDisk(4);
		hostData.setFreeMen(5);
		hostData.setMaxCpu(7);
		hostData.setMaxDisk(8);
		hostData.setMaxMem(9);
		hostData.setRunningVms(10);
		hostData.setUsedCpu(11);
		hostData.setUsedDisk(12);
		hostData.setUsedMem(13);
				
		boolean saved = hostDataDAO.save(hostData);
		assertTrue(saved);
		
		List<HostData> hostDatas = hostDataDAO.getAll();
		size = size + 1;
		assertEquals(size, hostDatas.size());
		
		HostData hostDataFromDatabase = hostDatas.get(size - 1);
		assertEquals(1, hostDataFromDatabase.getCpuUsage());
		assertEquals(2, hostDataFromDatabase.getDiskUsage());
		assertEquals(3, hostDataFromDatabase.getFreeCpu());
		assertEquals(4, hostDataFromDatabase.getFreeDisk());
		assertEquals(5, hostDataFromDatabase.getFreeMen());
		assertEquals(7, hostDataFromDatabase.getMaxCpu());
		assertEquals(8, hostDataFromDatabase.getMaxDisk());
		assertEquals(9, hostDataFromDatabase.getMaxMem());
		assertEquals(10, hostDataFromDatabase.getRunningVms());
		assertEquals(11, hostDataFromDatabase.getUsedCpu());
		assertEquals(12, hostDataFromDatabase.getUsedDisk());
		assertEquals(13, hostDataFromDatabase.getUsedMem());
	}
	
	@Test
	public void getById() {
		int size = hostDataDAO.getAll().size();
		
		HostData hostData = new HostData();
		hostData.setCpuUsage(1);
		hostData.setDiskUsage(2);
		hostData.setFreeCpu(3);
		hostData.setFreeDisk(4);
		hostData.setFreeMen(5);
		hostData.setMaxCpu(7);
		hostData.setMaxDisk(8);
		hostData.setMaxMem(9);
		hostData.setRunningVms(10);
		hostData.setUsedCpu(11);
		hostData.setUsedDisk(12);
		hostData.setUsedMem(13);
		
		boolean saved = hostDataDAO.save(hostData);
		assertTrue(saved);
			
		HostData hostDataFromDatabase = hostDataDAO.getAll().get(size);
		hostDataFromDatabase = hostDataDAO.getById(hostDataFromDatabase.getId());
		assertEquals(1, hostDataFromDatabase.getCpuUsage());
		assertEquals(2, hostDataFromDatabase.getDiskUsage());
		assertEquals(3, hostDataFromDatabase.getFreeCpu());
		assertEquals(4, hostDataFromDatabase.getFreeDisk());
		assertEquals(5, hostDataFromDatabase.getFreeMen());
		assertEquals(7, hostDataFromDatabase.getMaxCpu());
		assertEquals(8, hostDataFromDatabase.getMaxDisk());
		assertEquals(9, hostDataFromDatabase.getMaxMem());
		assertEquals(10, hostDataFromDatabase.getRunningVms());
		assertEquals(11, hostDataFromDatabase.getUsedCpu());
		assertEquals(12, hostDataFromDatabase.getUsedDisk());
		assertEquals(13, hostDataFromDatabase.getUsedMem());
		
		HostData nullHostData = hostDataDAO.getById(30000);
		assertEquals(null, nullHostData);
	}
	
	@Test
	public void delete() {
		int size = hostDataDAO.getAll().size();
		
		HostData hostData = new HostData();
		hostData.setCpuUsage(1);
		hostData.setDiskUsage(2);
		hostData.setFreeCpu(3);
		hostData.setFreeDisk(4);
		hostData.setFreeMen(5);
		hostData.setMaxCpu(7);
		hostData.setMaxDisk(8);
		hostData.setMaxMem(9);
		hostData.setRunningVms(10);
		hostData.setUsedCpu(11);
		hostData.setUsedDisk(12);
		hostData.setUsedMem(13);
		
		boolean saved = hostDataDAO.save(hostData);
		assertTrue(saved);
			
		HostData hostDataFromDatabase = hostDataDAO.getAll().get(size);
		int id = hostDataFromDatabase.getId();
		
		boolean deleted = hostDataDAO.delete(hostDataFromDatabase);
		assertTrue(deleted);
		
		deleted = hostDataDAO.delete(hostDataFromDatabase);
		assertTrue(!deleted);
		
		hostDataFromDatabase = hostDataDAO.getById(id);
		assertEquals(null, hostDataFromDatabase);
	}
	
	@Test
	public void update() {
		int size = hostDataDAO.getAll().size();
		
		HostData hostData = new HostData();
		hostData.setCpuUsage(1);
		hostData.setDiskUsage(2);
		hostData.setFreeCpu(3);
		hostData.setFreeDisk(4);
		hostData.setFreeMen(5);
		hostData.setMaxCpu(7);
		hostData.setMaxDisk(8);
		hostData.setMaxMem(9);
		hostData.setRunningVms(10);
		hostData.setUsedCpu(11);
		hostData.setUsedDisk(12);
		hostData.setUsedMem(13);
		
		boolean saved = hostDataDAO.save(hostData);
		assertTrue(saved);
		
		HostData hostDataFromDatabase = hostDataDAO.getAll().get(size);
		assertEquals(1, hostDataFromDatabase.getCpuUsage());
		assertEquals(2, hostDataFromDatabase.getDiskUsage());
		assertEquals(3, hostDataFromDatabase.getFreeCpu());
		assertEquals(4, hostDataFromDatabase.getFreeDisk());
		assertEquals(5, hostDataFromDatabase.getFreeMen());
		assertEquals(7, hostDataFromDatabase.getMaxCpu());
		assertEquals(8, hostDataFromDatabase.getMaxDisk());
		assertEquals(9, hostDataFromDatabase.getMaxMem());
		assertEquals(10, hostDataFromDatabase.getRunningVms());
		assertEquals(11, hostDataFromDatabase.getUsedCpu());
		assertEquals(12, hostDataFromDatabase.getUsedDisk());
		assertEquals(13, hostDataFromDatabase.getUsedMem());
				
		hostDataFromDatabase.setRunningVms(2222);
		hostDataFromDatabase.setUsedMem(3333);
		boolean updated = hostDataDAO.update(hostDataFromDatabase);
		assertTrue(updated);
		
		hostDataFromDatabase = hostDataDAO.getAll().get(0);
		assertEquals(1, hostDataFromDatabase.getCpuUsage());
		assertEquals(2, hostDataFromDatabase.getDiskUsage());
		assertEquals(3, hostDataFromDatabase.getFreeCpu());
		assertEquals(4, hostDataFromDatabase.getFreeDisk());
		assertEquals(5, hostDataFromDatabase.getFreeMen());
		assertEquals(7, hostDataFromDatabase.getMaxCpu());
		assertEquals(8, hostDataFromDatabase.getMaxDisk());
		assertEquals(9, hostDataFromDatabase.getMaxMem());
		assertEquals(2222, hostDataFromDatabase.getRunningVms());
		assertEquals(11, hostDataFromDatabase.getUsedCpu());
		assertEquals(12, hostDataFromDatabase.getUsedDisk());
		assertEquals(3333, hostDataFromDatabase.getUsedMem());
	}
	
	@Test
	public void getLastEntryTest() {
		Host host = new Host();
		host.setConnected(true);
		host.setName("host1");
		host.setState(1);
		
		boolean saved = hostDAO.save(host);
		assertTrue(saved);
		
		HostData hostData1 = new HostData();
		hostData1.setCpuUsage(1);
		hostData1.setDiskUsage(2);
		hostData1.setFreeCpu(3);
		hostData1.setFreeDisk(4);
		hostData1.setFreeMen(5);
		hostData1.setMaxCpu(7);
		hostData1.setMaxDisk(8);
		hostData1.setMaxMem(9);
		hostData1.setRunningVms(10);
		hostData1.setUsedCpu(11);
		hostData1.setUsedDisk(12);
		hostData1.setUsedMem(13);
		hostData1.setHost(host);
		
		HostData hostData2 = new HostData();
		hostData2.setCpuUsage(11);
		hostData2.setDiskUsage(22);
		hostData2.setFreeCpu(33);
		hostData2.setFreeDisk(44);
		hostData2.setFreeMen(55);
		hostData2.setMaxCpu(77);
		hostData2.setMaxDisk(8);
		hostData2.setMaxMem(9);
		hostData2.setRunningVms(10);
		hostData2.setUsedCpu(11);
		hostData2.setUsedDisk(12);
		hostData2.setUsedMem(13);
		hostData2.setHost(host);
		
		saved = hostDataDAO.save(hostData1);
		assertTrue(saved);
		saved = hostDataDAO.save(hostData2);
		assertTrue(saved);
		
		List<HostData> hostDatas = hostDataDAO.getLastEntry(host);
		assertEquals(1, hostDatas.size());
		assertEquals(11, hostDatas.get(0).getCpuUsage());
	}
	
	@Test
	public void manyToOneHost() {
		Host host = new Host();
		host.setConnected(true);
		host.setName("host1");
		host.setState(1);
		
		boolean saved = hostDAO.save(host);
		assertTrue(saved);
		
		HostData hostData1 = new HostData();
		hostData1.setCpuUsage(1);
		hostData1.setDiskUsage(2);
		hostData1.setFreeCpu(3);
		hostData1.setFreeDisk(4);
		hostData1.setFreeMen(5);
		hostData1.setMaxCpu(7);
		hostData1.setMaxDisk(8);
		hostData1.setMaxMem(9);
		hostData1.setRunningVms(10);
		hostData1.setUsedCpu(11);
		hostData1.setUsedDisk(12);
		hostData1.setUsedMem(13);
		hostData1.setHost(host);
		
		HostData hostData2 = new HostData();
		hostData2.setCpuUsage(1);
		hostData2.setDiskUsage(2);
		hostData2.setFreeCpu(3);
		hostData2.setFreeDisk(4);
		hostData2.setFreeMen(5);
		hostData2.setMaxCpu(7);
		hostData2.setMaxDisk(8);
		hostData2.setMaxMem(9);
		hostData2.setRunningVms(10);
		hostData2.setUsedCpu(11);
		hostData2.setUsedDisk(12);
		hostData2.setUsedMem(13);
		hostData2.setHost(host);
		
		saved = hostDataDAO.save(hostData1);
		assertTrue(saved);
		saved = hostDataDAO.save(hostData2);
		assertTrue(saved);
		
		List<HostData> hostDatas = hostDataDAO.getAll();
		assertEquals(2, hostDatas.size());
		
		HostData hostData1FromDatabase = hostDatas.get(0);
		HostData hostData2FromDatabase = hostDatas.get(1);		
		assertEquals("host1", hostData1FromDatabase.getHost().getName());
		assertEquals("host1", hostData2FromDatabase.getHost().getName());
		
		boolean deleted = hostDataDAO.delete(hostData1FromDatabase);
		assertTrue(deleted);
		
		List<Host> hosts = hostDAO.getAll();
		assertEquals(1, hosts.size());
		assertEquals("host1", hosts.get(0).getName());
		
		deleted = hostDataDAO.delete(hostData2FromDatabase);
		assertTrue(deleted);
		
		hosts = hostDAO.getAll();
		assertEquals(1, hosts.size());
		assertEquals("host1", hosts.get(0).getName());
	}
}
