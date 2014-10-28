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

import eu.eco2clouds.accounting.datamodel.Action;
import eu.eco2clouds.accounting.datamodel.ActionType;
import eu.eco2clouds.accounting.datamodel.HostData;
import eu.eco2clouds.accounting.service.ActionDAO;
import eu.eco2clouds.accounting.service.ActionTypeDAO;
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
public class ActionDAOJpaTest extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	protected ActionDAO actionDAO;
	@Autowired
	protected ActionTypeDAO actionTypeDAO;
	@Autowired
	protected HostDataDAO hostDataDAO;
	
	@Test
	public void notNull() {
		if(actionDAO == null || actionTypeDAO == null || hostDataDAO == null) fail();
	}
	
	@Test
	public void saveGetAll() {
		int size = actionDAO.getAll().size();
		
		Action action = new Action();
		action.setActionLog("log...");
		action.setTimestamp(1l);
		
		boolean saved = actionDAO.save(action);
		assertTrue(saved);
		
		List<Action> actions = actionDAO.getAll();
		size = size + 1;
		assertEquals(size, actions.size());
		
		Action actionFromDatabase = actions.get(size - 1);
		assertEquals("log...", actionFromDatabase.getActionLog());
		assertEquals(1l, actionFromDatabase.getTimestamp());
	}
	
	@Test
	public void getById() {
		int size = actionDAO.getAll().size();
		
		Action action = new Action();
		action.setActionLog("log...");
		action.setTimestamp(1l);
		
		boolean saved = actionDAO.save(action);
		assertTrue(saved);
			
		Action actionFromDatabase = actionDAO.getAll().get(size);
		int id = actionFromDatabase.getId();
		actionFromDatabase = actionDAO.getById(id);
		assertEquals("log...", actionFromDatabase.getActionLog());
		assertEquals(1l, actionFromDatabase.getTimestamp());
		
		Action nullAction = actionDAO.getById(30000);
		assertEquals(null, nullAction);
	}

	@Test
	public void delete() {
		int size = actionDAO.getAll().size();
		
		Action action = new Action();
		action.setActionLog("log...");
		action.setTimestamp(1l);
		
		boolean saved = actionDAO.save(action);
		assertTrue(saved);
			
		Action actionFromDatabase = actionDAO.getAll().get(size);
		int id = actionFromDatabase.getId();
		
		boolean deleted = actionDAO.delete(actionFromDatabase);
		assertTrue(deleted);
		
		deleted = actionDAO.delete(actionFromDatabase);
		assertTrue(!deleted);
		
		actionFromDatabase = actionDAO.getById(id);
		assertEquals(null, actionFromDatabase);
	}
	
	@Test
	public void update() {
		int size = actionDAO.getAll().size();
		
		Action action = new Action();
		action.setActionLog("log...");
		action.setTimestamp(1l);
		
		boolean saved = actionDAO.save(action);
		assertTrue(saved);
		
		Action actionFromDatabase = actionDAO.getAll().get(size);
		int id = actionFromDatabase.getId();
		assertEquals("log...", actionFromDatabase.getActionLog());
		assertEquals(1l, actionFromDatabase.getTimestamp());
				
		actionFromDatabase.setActionLog("log2...");
		actionFromDatabase.setTimestamp(2l);
		boolean updated = actionDAO.update(actionFromDatabase);
		assertTrue(updated);
		
		actionFromDatabase = actionDAO.getById(id);
		assertEquals("log2...", actionFromDatabase.getActionLog());
		assertEquals(2l, actionFromDatabase.getTimestamp());
	}
	
	@Test
	public void manyToOneActionType() {
		ActionType actionType = new ActionType();
		actionType.setName("MyActionType");
		
		boolean saved = actionTypeDAO.save(actionType);
		assertTrue(saved);
		
		Action action1 = new Action();
		action1.setActionLog("log...");
		action1.setTimestamp(1l);
		action1.setActionType(actionType);
		
		Action action2 = new Action();
		action2.setActionLog("log2...");
		action2.setTimestamp(2l);
		action2.setActionType(actionType);
		
		saved = actionDAO.save(action1);
		assertTrue(saved);
		saved = actionDAO.save(action2);
		assertTrue(saved);
		
		List<Action> actions = actionDAO.getAll();
		assertEquals(2, actions.size());
		
		Action action1FromDatabase = actions.get(0);
		Action action2FromDatabase = actions.get(1);
		assertEquals("log...", action1FromDatabase.getActionLog());
		assertEquals(1l, action1FromDatabase.getTimestamp());
		assertEquals("log2...", action2FromDatabase.getActionLog());
		assertEquals(2l, action2FromDatabase.getTimestamp());
		
		assertEquals("MyActionType", action1FromDatabase.getActionType().getName());
		assertEquals("MyActionType", action2FromDatabase.getActionType().getName());
		
		boolean deleted = actionDAO.delete(action1FromDatabase);
		assertTrue(deleted);
		
		List<ActionType> actionTypes = actionTypeDAO.getAll();
		assertEquals(1, actionTypes.size());
		assertEquals("MyActionType", actionTypes.get(0).getName());
	}
	
	@Test
	public void cascadeHostData() {
		int size = actionDAO.getAll().size();
		
		Action action = new Action();
		action.setActionLog("log...");
		action.setTimestamp(1l);
		
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
		
		Set<HostData> hostDatas = new HashSet<HostData>();
		hostDatas.add(hostData2);
		hostDatas.add(hostData1);
		action.setHostDatas(hostDatas);
		
		boolean saved = actionDAO.save(action);
		assertTrue(saved);
		
		Action actionFromDatabase = actionDAO.getAll().get(size);
		assertEquals("log...", actionFromDatabase.getActionLog());
		assertEquals(1l, actionFromDatabase.getTimestamp());
		int id = actionFromDatabase.getId();
		
		List<HostData> hostDatasFromDatabase = hostDataDAO.getAll();
		assertEquals(2, hostDatasFromDatabase.size());
		
		actionFromDatabase = actionDAO.deleteHostData(actionFromDatabase, hostDatasFromDatabase.get(0));
		
		actionFromDatabase = actionDAO.getById(id);
		assertEquals(1, actionFromDatabase.getHostDatas().size());
		
		hostDatasFromDatabase = hostDataDAO.getAll();
		assertEquals(1, hostDatasFromDatabase.size());
		
		boolean deleted = actionDAO.delete(actionFromDatabase);
		assertTrue(deleted);
		
		List<Action> actions = actionDAO.getAll();
		assertEquals(0, actions.size());
		
		hostDatasFromDatabase = hostDataDAO.getAll();
		assertEquals(0, hostDatasFromDatabase.size());
	}
}
