package eu.eco2clouds.accounting.datamodel.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.eco2clouds.accounting.datamodel.parser.Host;

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
public class HostTest {

	@Test
	public void pojo() {
		
		
		Host host = new Host();
		host.setId(1);
		host.setName("host1");
		host.setState(1);
		host.setConnected(true);
		HostData hostData = new HostData();
		host.setHostData(hostData);
		
		
		assertEquals(1, host.getId());
		assertEquals("host1", host.getName());
		assertEquals(1, host.getState());
		assertEquals(true, host.isConnected());
		assertEquals(hostData, host.getHostData());
		
		
		Host host2 = new Host(2,0,"host2",false);
		
		assertEquals(2, host2.getId());
		assertEquals("host2", host2.getName());
		assertEquals(0, host2.getState());
		assertEquals(false, host2.isConnected());
	}
	

}
