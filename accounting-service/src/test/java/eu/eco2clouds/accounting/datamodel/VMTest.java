package eu.eco2clouds.accounting.datamodel;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import eu.eco2clouds.accounting.datamodel.Action;
import eu.eco2clouds.accounting.datamodel.VM;
import eu.eco2clouds.accounting.datamodel.VMHost;

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
public class VMTest {

	@Test
	public void pojo() {
		VM vm = new VM();
		vm.setBonfireUrl("https:/...");
		vm.setId(222);
		List<Action> actions = new ArrayList<Action>();
		vm.setActions(actions);
		Set<VMHost> vMHosts = new HashSet<VMHost>();
		vm.setvMhosts(vMHosts);

		assertEquals(222, vm.getId());
		assertEquals("https:/...", vm.getBonfireUrl());
		assertEquals(vMHosts, vm.getvMhosts());
		assertEquals(actions, vm.getActions());

		VM vm2 = new VM(333, "https:/vm2");
		assertEquals(333, vm2.getId());
		assertEquals("https:/vm2", vm2.getBonfireUrl());

		VMHost vmHost2 = new VMHost(2, 4000);

		assertEquals(2, vmHost2.getId());
		assertEquals(4000, vmHost2.getTimestamp());
	}
}
