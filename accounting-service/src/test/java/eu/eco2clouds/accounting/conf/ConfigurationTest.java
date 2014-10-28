package eu.eco2clouds.accounting.conf;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
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
 */
public class ConfigurationTest {
	
	private static final String etcConfiguration = "/etc/e2c-accounting/MonitoringCollector.properties";

	@Test
	public void loadConfigurationTest() {
		assertEquals("root", Configuration.mysqlCollectorUsername);
		assertEquals("1234", Configuration.mysqlCollectorPassword);
		assertEquals("localhost/metricsdb", Configuration.mysqlCollectorHost);
	}
	
	@Test
	public void loadMonitoringPropertiesFile () {
		
		String propertiesFile = "MonitoringCollector.properties";
    	
    	File f = new File(etcConfiguration);
    	if(f.exists()) { 
    		propertiesFile = etcConfiguration; 
    	}
    	
    	org.apache.commons.configuration.Configuration config;
		try {
			config = new PropertiesConfiguration(propertiesFile);
			
	    	assertEquals ("root", config.getString("mysql.username"));
	    	assertEquals("1234", config.getString("mysql.password"));  
	    	assertEquals("localhost/metricsdb", config.getString("mysql.host")); 
				    				
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
}
