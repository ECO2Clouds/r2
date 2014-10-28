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
import eu.eco2clouds.accounting.datamodel.VMHost;
import eu.eco2clouds.accounting.service.HostDAO;
import eu.eco2clouds.accounting.service.VMHostDAO;

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
public class VMHostDAOJpaTest extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	private VMHostDAO vMHostDAO;
	@Autowired
	private HostDAO hostDAO;
	
	@Test
	public void notNull() {
		if(vMHostDAO == null || hostDAO == null) fail();
	}
	
	@Test
	public void saveGetAll() {
		int size = vMHostDAO.getAll().size();
		
		VMHost vMHost = new VMHost();
		vMHost.setTimestamp(2l);
		
		boolean saved = vMHostDAO.save(vMHost);
		assertTrue(saved);
		
		List<VMHost> vMHosts = vMHostDAO.getAll();
		size = size + 1;
		assertEquals(size, vMHosts.size());
		
		VMHost vMHostFromDatabase = vMHosts.get(size - 1);
		assertEquals(2l, vMHostFromDatabase.getTimestamp());
	}
	
	@Test
	public void getById() {
		int size = vMHostDAO.getAll().size();
		
		VMHost vMHost = new VMHost();
		vMHost.setTimestamp(2l);
		
		boolean saved = vMHostDAO.save(vMHost);
		assertTrue(saved);
			
		VMHost vMHostFromDatabase = vMHostDAO.getAll().get(size);
		int id = vMHostFromDatabase.getId();
		vMHostFromDatabase = vMHostDAO.getById(id);
		assertEquals(2l, vMHostFromDatabase.getTimestamp());
		
		VMHost nullvMhost = vMHostDAO.getById(30000);
		assertEquals(null, nullvMhost);
	}

	@Test
	public void delete() {
		int size = vMHostDAO.getAll().size();
		
		VMHost vMHost = new VMHost();
		vMHost.setTimestamp(2l);
		
		boolean saved = vMHostDAO.save(vMHost);
		assertTrue(saved);
			
		VMHost vMHostFromDatabase = vMHostDAO.getAll().get(size);
		int id = vMHostFromDatabase.getId();
		
		boolean deleted = vMHostDAO.delete(vMHostFromDatabase);
		assertTrue(deleted);
		
		deleted = vMHostDAO.delete(vMHostFromDatabase);
		assertTrue(!deleted);
		
		vMHostFromDatabase = vMHostDAO.getById(id);
		assertEquals(null, vMHostFromDatabase);
	}
	
	@Test
	public void update() {
		int size = vMHostDAO.getAll().size();
		
		VMHost vMHost = new VMHost();
		vMHost.setTimestamp(2l);
		
		boolean saved = vMHostDAO.save(vMHost);
		assertTrue(saved);
		
		VMHost vMHostFromDatabase = vMHostDAO.getAll().get(size);
		int id = vMHostFromDatabase.getId();
		assertEquals(2l, vMHostFromDatabase.getTimestamp());

		vMHostFromDatabase.setTimestamp(3l);
		boolean updated = vMHostDAO.update(vMHostFromDatabase);
		assertTrue(updated);
		
		vMHostFromDatabase = vMHostDAO.getById(id);
		assertEquals(3l, vMHostFromDatabase.getTimestamp());
	}
	
	@Test
	public void manyToOneHost() {
		Host host = new Host();
		host.setConnected(true);
		host.setName("host1");
		host.setState(1);
		
		boolean saved = hostDAO.save(host);
		assertTrue(saved);
		
		VMHost vMHost1 = new VMHost();
		vMHost1.setTimestamp(2l);
		vMHost1.setHost(host);
		
		VMHost vMHost2 = new VMHost();
		vMHost2.setTimestamp(2l);
		vMHost2.setHost(host);
		
		saved = vMHostDAO.save(vMHost1);
		assertTrue(saved);
		saved = vMHostDAO.save(vMHost2);
		assertTrue(saved);
		
		List<VMHost> vMHosts = vMHostDAO.getAll();
		assertEquals(2, vMHosts.size());
		
		VMHost vMHost1FromDatabase = vMHosts.get(0);
		VMHost vMHost2FromDatabase = vMHosts.get(1);		
		assertEquals("host1", vMHost1FromDatabase.getHost().getName());
		assertEquals("host1", vMHost2FromDatabase.getHost().getName());
		
		boolean deleted = vMHostDAO.delete(vMHost1FromDatabase);
		assertTrue(deleted);
		
		List<Host> hosts = hostDAO.getAll();
		assertEquals(1, hosts.size());
		assertEquals("host1", hosts.get(0).getName());
	}
}
