package eu.eco2clouds.accounting.datamodel.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.eco2clouds.accounting.datamodel.parser.VMHost;

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
public class VMHostTest {

	@Test
	public void pojo() {
		
		
		VMHost vmHost = new VMHost();
		vmHost.setId(1);
	    vmHost.setHost(null);
	    vmHost.setTimestamp(45000);
		
		
		
		assertEquals(1, vmHost.getId());
		assertEquals(null, vmHost.getHost());
		assertEquals(45000, vmHost.getTimestamp());
		
		
		
		VMHost vmHost2 = new VMHost(2,4000);
		
		assertEquals(2, vmHost2.getId());
		assertEquals(4000, vmHost2.getTimestamp());

	}
	

}
