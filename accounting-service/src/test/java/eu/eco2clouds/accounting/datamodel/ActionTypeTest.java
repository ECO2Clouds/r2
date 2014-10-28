package eu.eco2clouds.accounting.datamodel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
 *
 * Unit test for the ActionType Class
 */
public class ActionTypeTest {
	
	@Test
	public void pojo() {
		ActionType actionType = new ActionType();
		actionType.setId(3);
		actionType.setName("pepito");
		
		assertEquals(3, actionType.getId());
		assertEquals("pepito", actionType.getName());
		
		ActionType actionType2 = new ActionType(4,"juanito");
		assertEquals(4, actionType2.getId());
		assertEquals("juanito", actionType2.getName());
		
	}
}
