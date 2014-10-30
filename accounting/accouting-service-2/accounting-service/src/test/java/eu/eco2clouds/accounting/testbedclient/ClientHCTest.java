package eu.eco2clouds.accounting.testbedclient;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import eu.eco2clouds.accounting.datamodel.Testbed;
import eu.eco2clouds.accounting.util.MockWebServer;
import eu.eco2clouds.accounting.util.ReadFile;

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
public class ClientHCTest {
	@Rule
	public ExpectedException exception = ExpectedException.none();
	private MockWebServer mServer;
	private String mBaseURL = "http://localhost:";
	private String hostInfo;

	@Before
	public void before() throws IOException {
		hostInfo = ReadFile.readFile("src/test/resources/host.example");
		mServer = new MockWebServer();
		mServer.start();
		mBaseURL = mBaseURL + mServer.getPort();
	}
	
	@Test
	public void clientTest() {
		mServer.addPath("/", hostInfo);
		
		Testbed testbed = new Testbed();
		testbed.setName("fr-inria");
		testbed.setUrl(mBaseURL);
		
		Client client = new ClientHC();
		String returnedHostInfo = client.getHostInfo(testbed);
		System.out.println(returnedHostInfo);
		
		assertEquals("","");
	}
	
	@Test
	public void clientTest404() {
		Testbed testbed = new Testbed();
		testbed.setName("fr-inria");
		testbed.setUrl(mBaseURL + "/...1111");
		
		Client client = new ClientHC();
		String returnedHostInfo = client.getHostInfo(testbed);
		System.out.println(returnedHostInfo);
		
		assertEquals("", returnedHostInfo);
	}
	
	@Test
	public void clientTestInvalidURL() {
		Testbed testbed = new Testbed();
		testbed.setName("fr-inria");
		testbed.setUrl("http://pepito");
		
		Client client = new ClientHC();
		String returnedHostInfo = client.getHostInfo(testbed);
		System.out.println(returnedHostInfo);
		
		assertEquals("", returnedHostInfo);
	}
	
	@After
	public void after() {
		mServer.stop();
	}
}
