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

import eu.eco2clouds.accounting.datamodel.ActionType;
import eu.eco2clouds.accounting.service.ActionTypeDAO;

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
public class ActionTypeDAOJpaTest extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	private ActionTypeDAO actionTypeDAO;
	
	@Test
	public void notNull() {
		if(actionTypeDAO == null) fail();
	}
	
	@Test
	public void saveGetAll() {
		int size = actionTypeDAO.getAll().size();
		
		ActionType actionType = new ActionType();
		actionType.setName("pepito");
		
		boolean saved = actionTypeDAO.save(actionType);
		assertTrue(saved);
		
		List<ActionType> actionTypes = actionTypeDAO.getAll();
		size = size + 1;
		assertEquals(size, actionTypes.size());
		
		ActionType actionTypeFromDatabase = actionTypes.get(size - 1);
		assertEquals("pepito", actionTypeFromDatabase.getName());
	}
	
	@Test
	public void getById() {
		int size = actionTypeDAO.getAll().size();
		
		ActionType actionType = new ActionType();
		actionType.setName("name");
		
		boolean saved = actionTypeDAO.save(actionType);
		assertTrue(saved);
		
		ActionType actionTypeFromDatabase = actionTypeDAO.getAll().get(size);
		int id = actionTypeFromDatabase.getId();
		actionTypeFromDatabase = actionTypeDAO.getById(id);
		assertEquals("name", actionTypeFromDatabase.getName());
		
		ActionType nullActionType = actionTypeDAO.getById(30000);
		assertEquals(null, nullActionType);
	}
	
	@Test
	public void getByName() {
		ActionType actionType = new ActionType();
		actionType.setName("name");
		
		boolean saved = actionTypeDAO.save(actionType);
		assertTrue(saved);
			
		ActionType actionTypeFromDatabase = actionTypeDAO.getByName("name");
		assertEquals("name", actionTypeFromDatabase.getName());
		
		ActionType nullActionType = actionTypeDAO.getByName("xxxxx");
		assertEquals(null, nullActionType);
	}
	
	@Test
	public void find() {
		ActionType actionType = new ActionType();
		actionType.setName("name");
		
		boolean saved = actionTypeDAO.save(actionType);
		assertTrue(saved);
			
		ActionType actionTypeFromDatabase = actionTypeDAO.find(actionType);
		assertEquals("name", actionTypeFromDatabase.getName());
		
		ActionType notInDatabase = new ActionType();
		notInDatabase.setName("xxxxx");
		
		ActionType nullActionType = actionTypeDAO.find(notInDatabase);
		assertEquals(null, nullActionType);
		
		nullActionType = actionTypeDAO.find(null);
		assertEquals(null, nullActionType);
		
		ActionType nullName = new ActionType();
		nullName.setName(null);
		
		nullActionType = actionTypeDAO.find(nullName);
		assertEquals(null, nullActionType);
	}
	
	@Test
	public void delete() {
		int size = actionTypeDAO.getAll().size();
		
		ActionType actionType = new ActionType();
		actionType.setName("name");
		
		boolean saved = actionTypeDAO.save(actionType);
		assertTrue(saved);
			
		ActionType actionTypeFromDatabase = actionTypeDAO.getAll().get(size);
		int id = actionTypeFromDatabase.getId();
		boolean deleted = actionTypeDAO.delete(actionTypeFromDatabase);
		assertTrue(deleted);
		
		deleted = actionTypeDAO.delete(actionTypeFromDatabase);
		assertTrue(!deleted);
		
		actionTypeFromDatabase = actionTypeDAO.getById(id);
		assertEquals(null, actionTypeFromDatabase);
	}
	
	@Test
	public void update() {
		int size = actionTypeDAO.getAll().size();
		
		ActionType actionType = new ActionType();
		actionType.setName("name");
		
		boolean saved = actionTypeDAO.save(actionType);
		assertTrue(saved);
		
		ActionType actionTypeFromDatabase = actionTypeDAO.getAll().get(size);
		int id = actionTypeFromDatabase.getId();
		actionTypeFromDatabase = actionTypeDAO.getById(id);
		assertEquals("name", actionTypeFromDatabase.getName());
				
		actionTypeFromDatabase.setName("pepito");
		boolean updated = actionTypeDAO.update(actionTypeFromDatabase);
		assertTrue(updated);
		
		actionTypeFromDatabase = actionTypeDAO.getById(id);
		assertEquals("pepito", actionTypeFromDatabase.getName());
	}
}
