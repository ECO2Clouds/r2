package eu.eco2clouds.accounting.rest.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

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
public class AccountingServiceActionTypeTest {
	
	@Test
	public void getListOfActionTypesTest() throws IOException {
		// We prepare the objects that we expect to be retrieved by the database.
		ActionType actionType1 = new ActionType();
		actionType1.setId(1);
		actionType1.setName("action type 1");
		
		ActionType actionType2 = new ActionType();
		actionType2.setId(2);
		actionType2.setName("action type 2");
				
		
		List<ActionType> actionTypes = new ArrayList<ActionType>();
		actionTypes.add(actionType1);
		actionTypes.add(actionType2);
		
		List<String> groups = new ArrayList<String>();
		
		groups.add("EPCC");
		groups.add("ATOS");
		
		ActionTypeDAO actionTypeDAO = mock(ActionTypeDAO.class);
		when(actionTypeDAO.getAll()).thenReturn(actionTypes); // When it is called it also returns this list
		
		// We create the service and we connect it to the mocked actionTypeDAO
		AccountingServiceActionType accountingService = new AccountingServiceActionType(actionTypeDAO);
       		
		String actionTypeString = accountingService.printInformation(-1, groups,null); 
		
		BufferedReader reader = new BufferedReader(new StringReader(actionTypeString));
		
				
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", reader.readLine());
		assertEquals("<collection xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/occi\" href=\"/action_types\">", reader.readLine());
		assertEquals("<items offset=\"0\" total=\"2\">", reader.readLine());
		assertEquals("<action_type href=\"/action_types/1\">", reader.readLine());
		assertEquals("<id>1</id>",reader.readLine());
		assertEquals("<name>action type 1</name>",reader.readLine());
		assertEquals("<link rel=\"parent\" href=\"/action_types\"/>",reader.readLine());
		assertEquals("</action_type>",reader.readLine());
		assertEquals("<action_type href=\"/action_types/2\">", reader.readLine());
		assertEquals("<id>2</id>",reader.readLine());
		assertEquals("<name>action type 2</name>",reader.readLine());
		assertEquals("<link rel=\"parent\" href=\"/action_types\"/>",reader.readLine());
		assertEquals("</action_type>",reader.readLine());
		assertEquals("</items>", reader.readLine());
		assertEquals("<link href=\"/\" rel=\"parent\" type=\"application/vnd.eco2clouds+xml\"/>", reader.readLine());
		assertEquals("</collection>", reader.readLine());
	}
	
	@Test
	public void getActionTypeTest() throws IOException {
		
		// We prepare the objects that we expect to be retrieved by the database.
		// We prepare the objects that we expect to be retrieved by the database.
		ActionType actionType1 = new ActionType();
		actionType1.setId(1);
		actionType1.setName("action type 1");
		
				
		List<String> groups = new ArrayList<String>();
		groups.add("ATOS");

		ActionTypeDAO actionTypeDAO = mock(ActionTypeDAO.class);
		when(actionTypeDAO.getById(1)).thenReturn(actionType1); // When it is called it also returns this list	
		
	   
		// We create the service and we connect it to the mocked actionTypeDAO		
		AccountingServiceActionType accountingService = new AccountingServiceActionType(actionTypeDAO);
   		
		String actionTypeString = accountingService.printInformation(1, groups, null); 
		
		BufferedReader reader = new BufferedReader(new StringReader(actionTypeString));
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", reader.readLine());
		assertEquals("<action_type xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/occi\" href=\"/action_types/1\">", reader.readLine());
		assertEquals("<id>1</id>",reader.readLine());
		assertEquals("<name>action type 1</name>",reader.readLine());
		assertEquals("<link rel=\"parent\" href=\"/action_types\"/>",reader.readLine());
		assertEquals("</action_type>",reader.readLine());
			
	}
	
	

}
