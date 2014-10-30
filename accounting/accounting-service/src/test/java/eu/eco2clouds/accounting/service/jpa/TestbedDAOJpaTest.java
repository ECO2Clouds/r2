package eu.eco2clouds.accounting.service.jpa;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
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
import eu.eco2clouds.accounting.datamodel.Testbed;
import eu.eco2clouds.accounting.service.TestbedDAO;
import eu.eco2clouds.accounting.service.HostDAO;

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
public class TestbedDAOJpaTest extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	private TestbedDAO testbedDAO;
	@Autowired
	private HostDAO hostDAO;
	
	@Test
	public void notNull() {
		if(testbedDAO == null || hostDAO == null) fail();
	}
	
	@Test
	public void saveGetAll() {
		int size = testbedDAO.getAll().size();
		
		Testbed testbed = new Testbed();
		testbed.setName("fr-inria");
		testbed.setUrl("https://...");
		
		boolean saved = testbedDAO.save(testbed);
		assertTrue(saved);
		
		List<Testbed> testbeds = testbedDAO.getAll();
		size = size + 1;
		assertEquals(size, testbeds.size());
		
		Testbed testbedFromDatabase = testbeds.get(size - 1);
		assertEquals("fr-inria", testbedFromDatabase.getName());
		assertEquals("https://...", testbedFromDatabase.getUrl());
	}
	
	@Test
	public void getById() {
		Testbed testbed = new Testbed();
		testbed.setName("fr-inria");
		testbed.setUrl("https://...");
		
		boolean saved = testbedDAO.save(testbed);
		assertTrue(saved);

		Testbed testbedFromDatabase = testbedDAO.getByName(testbed.getName());
		int id = testbedFromDatabase.getId();
		testbedFromDatabase = testbedDAO.getById(id);
		assertEquals("fr-inria", testbedFromDatabase.getName());
		assertEquals("https://...", testbedFromDatabase.getUrl());
		
		Testbed nullTestbed = testbedDAO.getById(30000);
		assertEquals(null, nullTestbed);
	}
	
	@Test
	public void getByName() {
		Testbed testbed = new Testbed();
		testbed.setName("fr-inria");
		testbed.setUrl("https://...");
		
		boolean saved = testbedDAO.save(testbed);
		assertTrue(saved);
			
		Testbed testbedFromDatabase = testbedDAO.getByName("fr-inria");
		assertEquals("fr-inria", testbedFromDatabase.getName());
		assertEquals("https://...", testbedFromDatabase.getUrl());
		
		Testbed nullTestbed = testbedDAO.getByName("xxxxx");
		assertEquals(null, nullTestbed);
	}
	
	@Test
	public void find() {
		Testbed testbed = new Testbed();
		testbed.setName("fr-inria");
		testbed.setUrl("https://...");
		
		boolean saved = testbedDAO.save(testbed);
		assertTrue(saved);
			
		Testbed testbedFromDatabase = testbedDAO.find(testbed);
		assertEquals("fr-inria", testbedFromDatabase.getName());
		assertEquals("https://...", testbedFromDatabase.getUrl());
		
		Testbed notInDatabase = new Testbed();
		testbed.setName("uk-epcc");
		testbed.setUrl("http...");
		
		Testbed nullTestbed = testbedDAO.find(notInDatabase);
		assertEquals(null, nullTestbed);
		
		nullTestbed = testbedDAO.find(null);
		assertEquals(null, nullTestbed);
		
		Testbed nullName = new Testbed();
		nullName.setName(null);
		
		nullTestbed = testbedDAO.find(nullName);
		assertEquals(null, nullTestbed);
	}
	
	@Test
	public void delete() {
		Testbed testbed = new Testbed();
		testbed.setName("fr-inria");
		testbed.setUrl("https://...");
		
		boolean saved = testbedDAO.save(testbed);
		assertTrue(saved);
			
		Testbed testbedFromDatabase = testbedDAO.getByName(testbed.getName());
		int id = testbedFromDatabase.getId();
		
		boolean deleted = testbedDAO.delete(testbedFromDatabase);
		assertTrue(deleted);
		
		deleted = testbedDAO.delete(testbedFromDatabase);
		assertTrue(!deleted);
		
		testbedFromDatabase = testbedDAO.getById(id);
		assertEquals(null, testbedFromDatabase);
	}
	
	@Test
	public void update() {
		int size = testbedDAO.getAll().size();
		
		Testbed testbed = new Testbed();
		testbed.setName("fr-inria");
		testbed.setUrl("https://...");
		
		boolean saved = testbedDAO.save(testbed);
		assertTrue(saved);
		
		Testbed testbedFromDatabase = testbedDAO.getByName(testbed.getName());
		assertEquals("fr-inria", testbedFromDatabase.getName());
		assertEquals("https://...", testbedFromDatabase.getUrl());
				
		testbedFromDatabase.setName("uk-epcc");
		boolean updated = testbedDAO.update(testbedFromDatabase);
		assertTrue(updated);
		
		testbedFromDatabase = testbedDAO.getByName(testbed.getName());
		assertEquals("uk-epcc", testbedFromDatabase.getName());
		assertEquals("https://...", testbedFromDatabase.getUrl());
	}
	
	@Test
	public void cascade() {
		Testbed testbed = new Testbed();
		testbed.setName("testbed2");
		testbed.setUrl("url2");
		
		Host host1 = new Host();
		host1.setConnected(true);
		host1.setName("host1");
		host1.setState(1);
		
		Host host2 = new Host();
		host2.setConnected(true);
		host2.setName("host2");
		host2.setState(1);
		
		List<Host> hosts = new ArrayList<Host>();
		hosts.add(host1);
		hosts.add(host2);
		testbed.setHosts(hosts);
		
		boolean saved = testbedDAO.save(testbed);
		assertTrue(saved);
		
		Testbed testbedFromDatabase = testbedDAO.getByName(testbed.getName());
		assertEquals(2, testbedFromDatabase.getHosts().size());
		
		List<Host> hostsFromDatabase = hostDAO.getAll();
		assertEquals(2, hostsFromDatabase.size());
		
		testbedFromDatabase = testbedDAO.deleteHost(testbedFromDatabase, hostsFromDatabase.get(0));

		testbedFromDatabase = testbedDAO.getByName(testbed.getName());
		assertEquals(1, testbedFromDatabase.getHosts().size());
		
		hostsFromDatabase = hostDAO.getAll();
		assertEquals(1, hostsFromDatabase.size());
		
		boolean deleted = testbedDAO.delete(testbedFromDatabase);
		assertTrue(deleted);
		
		List<Testbed> testbedsFromDatabase = testbedDAO.getAll();
		assertEquals(0, testbedsFromDatabase.size());
		
		hostsFromDatabase = hostDAO.getAll();
		assertEquals(0, hostsFromDatabase.size());
	}
}
