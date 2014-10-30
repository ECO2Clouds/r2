package eu.eco2clouds.accounting.rest.service;

import javax.ws.rs.core.Response;

import org.junit.Test;

import eu.eco2clouds.accounting.conf.Configuration;

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
public class AccountingServiceTestIT {
	
	@Test
	public void mysqlTestbedMonitoringTest() {
		
		Configuration.mysqlCollectorUsername = "pepe";
		Configuration.mysqlCollectorPassword = "password";
		Configuration.mysqlCollectorHost = "localhost/e2c_collector";
		
		AccountingServiceTestbed acTestbed = new AccountingServiceTestbed();
		
		Response response = acTestbed.getHostMonitoring("fr-inria");
		
		System.out.println("RESPNSE: " + response.getEntity().toString());
	}

}
