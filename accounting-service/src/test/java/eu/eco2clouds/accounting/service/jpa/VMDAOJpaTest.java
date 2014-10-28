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

import eu.eco2clouds.accounting.datamodel.Action;
import eu.eco2clouds.accounting.datamodel.VM;
import eu.eco2clouds.accounting.datamodel.VMHost;
import eu.eco2clouds.accounting.service.ActionDAO;
import eu.eco2clouds.accounting.service.VMDAO;
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
public class VMDAOJpaTest extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	private VMDAO vMDAO;
	@Autowired
	private ActionDAO actionDAO;
	@Autowired
	private VMHostDAO vMHostDAO;
	
	@Test
	public void notNull() {
		if(vMDAO == null || actionDAO == null || vMHostDAO == null) fail();
	}
	
	@Test
	public void saveGetAll() {
		int size = vMDAO.getAll().size();
		
		VM vM = new VM();
		vM.setBonfireUrl("url...");
		
		boolean saved = vMDAO.save(vM);
		assertTrue(saved);
		
		List<VM> vMs = vMDAO.getAll();
		size = size + 1;
		assertEquals(size, vMs.size());
		
		VM vMFromDatabase = vMs.get(size - 1);
		assertEquals("url...", vMFromDatabase.getBonfireUrl());
	}
	
	@Test
	public void getById() {
		int size = vMDAO.getAll().size();
		
		VM vM = new VM();
		vM.setBonfireUrl("url...");
		
		boolean saved = vMDAO.save(vM);
		assertTrue(saved);
			
		int id = vMDAO.getAll().get(size).getId();
		VM vMFromDatabase = vMDAO.getById(id);
		assertEquals("url...", vMFromDatabase.getBonfireUrl());
		
		VM nullVM = vMDAO.getById(444444);
		assertEquals(null, nullVM);
	}
	
	@Test
	public void delete() {
		int size = vMDAO.getAll().size();
		
		VM vM = new VM();
		vM.setBonfireUrl("url...");
		
		boolean saved = vMDAO.save(vM);
		assertTrue(saved);
			
		VM vMFromDatabase = vMDAO.getAll().get(size);
		int id = vMFromDatabase.getId();
		
		boolean deleted = vMDAO.delete(vMFromDatabase);
		assertTrue(deleted);
		
		deleted = vMDAO.delete(vMFromDatabase);
		assertTrue(!deleted);
		
		vMFromDatabase = vMDAO.getById(id);
		assertEquals(null, vMFromDatabase);
	}
	
	@Test
	public void update() {
		int size = vMDAO.getAll().size();
		
		VM vM = new VM();
		vM.setBonfireUrl("url...");
		
		boolean saved = vMDAO.save(vM);
		assertTrue(saved);
			
		VM vMFromDatabase = vMDAO.getAll().get(size);
		int id = vMFromDatabase.getId();
		assertEquals("url...", vMFromDatabase.getBonfireUrl());
				
		vMFromDatabase.setBonfireUrl("url2");
		boolean updated = vMDAO.update(vMFromDatabase);
		assertTrue(updated);
		
		vMFromDatabase = vMDAO.getById(id);
		assertEquals("url2", vMFromDatabase.getBonfireUrl());
	}
	
	@Test
	public void cascadeActionss() {
		int size = vMDAO.getAll().size();
		
		VM vM = new VM();
		vM.setBonfireUrl("url...");
		
		Action action1 = new Action();
		action1.setActionLog("log...");
		action1.setTimestamp(1l);
		
		Action action2 = new Action();
		action2.setActionLog("log2...");
		action2.setTimestamp(2l);
		
		List <Action> actions = new ArrayList<Action>();
		actions.add(action1);
		actions.add(action2);
		vM.setActions(actions);
		
		boolean saved = vMDAO.save(vM);
		assertTrue(saved);
		
		VM vMFromDatabase = vMDAO.getAll().get(size);
		int id = vMFromDatabase.getId();
		assertEquals("url...", vMFromDatabase.getBonfireUrl());
		assertEquals(2, vMFromDatabase.getActions().size());
	
		List<Action> actionsFromDatabase = actionDAO.getAll();
		assertEquals(2, actionsFromDatabase.size());

		vMFromDatabase = vMDAO.deleteAction(vMFromDatabase, action1);
		
		vMFromDatabase = vMDAO.getById(id);
		assertEquals(1, vMFromDatabase.getActions().size());
		
		actionsFromDatabase = actionDAO.getAll();
		assertEquals(1, actionsFromDatabase.size());
		
		boolean deleted = vMDAO.delete(vMFromDatabase);
		assertTrue(deleted);
		
		List<VM> vMs = vMDAO.getAll();
		assertEquals(0, vMs.size());
		
		actionsFromDatabase = actionDAO.getAll();
		assertEquals(0, actionsFromDatabase.size());
	}
	
	@Test
	public void cascadeVMHosts() {
		int size = vMDAO.getAll().size();
		
		VM vM = new VM();
		vM.setBonfireUrl("url...");
		
		VMHost vMHost1 = new VMHost();
		vMHost1.setTimestamp(2l);
		
		VMHost vMHost2 = new VMHost();
		vMHost2.setTimestamp(2l);
		
		Set<VMHost> vMHosts = new HashSet<VMHost>();
		vMHosts.add(vMHost2);
		vMHosts.add(vMHost1);
		vM.setvMhosts(vMHosts);
		
		boolean saved = vMDAO.save(vM);
		assertTrue(saved);
		
		VM vMFromDatabase = vMDAO.getAll().get(size);
		int id = vMFromDatabase.getId();
		assertEquals("url...", vMFromDatabase.getBonfireUrl());
		assertEquals(2, vMFromDatabase.getvMhosts().size());
	
		List<VMHost> vMHostsFromDatabase = vMHostDAO.getAll();
		assertEquals(2, vMHostsFromDatabase.size());

		vMFromDatabase = vMDAO.deleteVMHost(vMFromDatabase, vMHost1);
		
		vMFromDatabase = vMDAO.getById(id);
		assertEquals(1, vMFromDatabase.getvMhosts().size());
		
		vMHostsFromDatabase = vMHostDAO.getAll();
		assertEquals(1, vMHostsFromDatabase.size());
		
		boolean deleted = vMDAO.delete(vMFromDatabase);
		assertTrue(deleted);
		
		List<VM> vMs = vMDAO.getAll();
		assertEquals(0, vMs.size());
		
		vMHostsFromDatabase = vMHostDAO.getAll();
		assertEquals(0, vMHostsFromDatabase.size());
	}
}
