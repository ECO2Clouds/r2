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
 * Checks that the simple Testbed class behaves as expected.
 * 
 */
public class TestbedTest {

	@Test
	public void testPojo() {
		Testbed testbed = new Testbed();
		testbed.setId(11);
		testbed.setName("name");
		testbed.setUrl("url");
		testbed.setHosts(null);

		assertEquals(11, testbed.getId());
		assertEquals("name", testbed.getName());
		assertEquals("url", testbed.getUrl());
		assertEquals(null, testbed.getHosts());

		Testbed testbed2 = new Testbed(2, "testbed B", "http://testbedB:8080");

		assertEquals(2, testbed2.getId());
	}
}
