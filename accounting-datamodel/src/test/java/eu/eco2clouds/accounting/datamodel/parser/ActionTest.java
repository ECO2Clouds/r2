package eu.eco2clouds.accounting.datamodel.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.eco2clouds.accounting.datamodel.parser.Action;

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
public class ActionTest {

	@Test
	public void pojo() {

		Action action = new Action();
		action.setId(1);
		action.setActionLog("actionlog1");
		action.setActionType(null);
		action.setHostDatas(null);
		action.setTimestamp(28000);
		action.setVm_id(10);

		assertEquals(1, action.getId());
		assertEquals("actionlog1", action.getActionLog());
		assertEquals(null, action.getActionType());
		assertEquals(null, action.getHostDatas());
		assertEquals(28000, action.getTimestamp());
		assertEquals(10, action.getVm_id());

		Action action2 = new Action(2, 29000, "action_log_2");

		assertEquals(2, action2.getId());
		assertEquals(29000, action2.getTimestamp());
		assertEquals("action_log_2", action2.getActionLog());
	}

}
