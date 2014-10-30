package eu.eco2clouds.accounting.datamodel.parser;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import eu.eco2clouds.accounting.datamodel.parser.Testbed;

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
public class TestbedTest {

	@Test
	public void pojo() {
		
		
		Testbed testbed = new Testbed();
		testbed.setId(1);
		testbed.setName("testbed A");
		//testbed.setHosts(null);
		testbed.setUrl("http://testbedA:8080");
		ArrayList<Link> links = new ArrayList<Link>();
		testbed.setLinks(links);
				
		
		assertEquals(1, testbed.getId());
		assertEquals("testbed A", testbed.getName());
		//assertEquals(null, testbed.getHosts());
		assertEquals("http://testbedA:8080", testbed.getUrl());
		assertEquals(links, testbed.getLinks());
		
		
				
		Testbed testbed2 = new Testbed(2,"testbed B", "http://testbedB:8080");
		
		assertEquals(2, testbed2.getId());
		
	}

}
