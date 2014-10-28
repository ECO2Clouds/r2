package eu.eco2clouds.accounting.service.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class HostDAOJpaTest extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	private HostDAO hostDAO;
	@Autowired
	private HostDataDAO hostDataDAO;

	
	@Test
	public void notNull() {
		if(hostDAO == null || hostDataDAO == null) fail();
	}
	
	@Test
	public void saveGetAll() {
		int size = hostDAO.getAll().size();
		
		Host host = new Host();
		host.setConnected(true);
		host.setName("host1");
		host.setState(1);
		
		boolean saved = hostDAO.save(host);
		assertTrue(saved);
		
		List<Host> hosts = hostDAO.getAll();
		size = size + 1;
		assertEquals(size, hosts.size());
		
		Host hostFromDatabase = hosts.get(size - 1);
		assertTrue(hostFromDatabase.isConnected());
		assertEquals(1, hostFromDatabase.getState());
		assertEquals("host1", hostFromDatabase.getName());
	}
	
	@Test
	public void getById() {
		Host host = new Host();
		host.setConnected(true);
		host.setName("host1");
		host.setState(1);
		
		boolean saved = hostDAO.save(host);
		assertTrue(saved);
			
		Host hostFromDatabase = hostDAO.getByName(host.getName());
		hostFromDatabase = hostDAO.getById(hostFromDatabase.getId());
		assertTrue(hostFromDatabase.isConnected());
		assertEquals(1, hostFromDatabase.getState());
		assertEquals("host1", hostFromDatabase.getName());
		
		Host nullHost = hostDAO.getById(30000);
		assertEquals(null, nullHost);
	}
	
	@Test
	public void getByName() {
		Host host = new Host();
		host.setConnected(true);
		host.setName("host1");
		host.setState(1);
		
		boolean saved = hostDAO.save(host);
		assertTrue(saved);
			
		Host hostFromDatabase = hostDAO.getByName("host1");
		assertTrue(hostFromDatabase.isConnected());
		assertEquals(1, hostFromDatabase.getState());
		assertEquals("host1", hostFromDatabase.getName());
		
		Host nullHost = hostDAO.getByName("xxxxx");
		assertEquals(null, nullHost);
	}
	
	@Test
	public void find() {
		Host host = new Host();
		host.setConnected(true);
		host.setName("host1");
		host.setState(1);
		
		boolean saved = hostDAO.save(host);
		assertTrue(saved);
			
		Host hostFromDatabase = hostDAO.find(host);
		assertTrue(hostFromDatabase.isConnected());
		assertEquals(1, hostFromDatabase.getState());
		assertEquals("host1", hostFromDatabase.getName());
		
		Host notInDatabase = new Host();
		host.setConnected(true);
		host.setName("host1");
		host.setState(1);
		
		Host nullHost = hostDAO.find(notInDatabase);
		assertEquals(null, nullHost);
		
		nullHost = hostDAO.find(null);
		assertEquals(null, nullHost);
		
		Host nullName = new Host();
		nullName.setName(null);
		
		nullHost = hostDAO.find(nullName);
		assertEquals(null, nullHost);
	}
	
	@Test
	public void delete() {
		int size = hostDAO.getAll().size();
		
		Host host = new Host();
		host.setConnected(true);
		host.setName("host1");
		host.setState(1);
		
		boolean saved = hostDAO.save(host);
		assertTrue(saved);
			
		Host hostFromDatabase = hostDAO.getAll().get(size);
		
		boolean deleted = hostDAO.delete(hostFromDatabase);
		assertTrue(deleted);
		
		deleted = hostDAO.delete(hostFromDatabase);
		assertTrue(!deleted);
		
		hostFromDatabase = hostDAO.getById(size);
		assertEquals(null, hostFromDatabase);
	}
	
	@Test
	public void update() {
		int size = hostDAO.getAll().size();
		
		Host host = new Host();
		host.setConnected(true);
		host.setName("host1");
		host.setState(1);
		
		boolean saved = hostDAO.save(host);
		assertTrue(saved);
		
		Host hostFromDatabase = hostDAO.getAll().get(size);
		assertTrue(hostFromDatabase.isConnected());
		assertEquals(1, hostFromDatabase.getState());
		assertEquals("host1", hostFromDatabase.getName());
				
		hostFromDatabase.setName("host222");
		boolean updated = hostDAO.update(hostFromDatabase);
		assertTrue(updated);
		
		hostFromDatabase = hostDAO.getAll().get(0);
		assertTrue(hostFromDatabase.isConnected());
		assertEquals(1, hostFromDatabase.getState());
		assertEquals("host222", hostFromDatabase.getName());
	}
}
