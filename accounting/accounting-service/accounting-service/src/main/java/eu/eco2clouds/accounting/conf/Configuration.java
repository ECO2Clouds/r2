package eu.eco2clouds.accounting.conf;

import java.io.File;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

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
public class Configuration {
	
	private static Logger logger = Logger.getLogger(Configuration.class);
	private static final String etcConfiguration = "/etc/e2c-accounting/MonitoringCollector.properties";
	public static String mysqlCollectorUsername = "root";
	public static String mysqlCollectorPassword = "admin";
	public static String mysqlCollectorHost = "localhost/metricsdb";
	
	public static String keystore = "no-valid";
	public static String keystoreKey = "no-valid";
	public static String bonfireAPIUrl = "https://test:444";
	public static String bonfireApiGroup = "no-valid";
	
	private static final String e2cAccountingConfiguration = "/etc/e2c-accounting/e2c-accounting.properties";
	
	static {
        try {
        	String propertiesFile = "MonitoringCollector.properties";
        	
        	File f = new File(etcConfiguration);
        	if(f.exists()) { 
        		propertiesFile = etcConfiguration; 
        	}
        	
        	org.apache.commons.configuration.Configuration config = new PropertiesConfiguration(propertiesFile);
        	mysqlCollectorUsername = config.getString("mysql.username");
        	mysqlCollectorPassword = config.getString("mysql.password");
        	mysqlCollectorHost = config.getString("mysql.host");
        }
        catch (Exception e) {
            logger.info("Error loading the configuration of the scheduler");
            logger.info("Expcetion " + e);
        }  
    }
	
	static {
		try {
        	String propsFile = "e2c-accounting.properties";
        	
        	File cfgFile = new File(e2cAccountingConfiguration);
        	if(cfgFile.exists()) { 
        		propsFile = e2cAccountingConfiguration; 
        	}
        	
        	org.apache.commons.configuration.Configuration e2cAccountingProps = new PropertiesConfiguration(propsFile);
        	bonfireAPIUrl = e2cAccountingProps.getString("bonfire.api.url");
        	bonfireApiGroup = e2cAccountingProps.getString("bonfire.api.group");
        	keystore = e2cAccountingProps.getString("keystore");
        	keystoreKey = e2cAccountingProps.getString("keystore.key");
        }
        catch (Exception e) {
            logger.info("Error loading the configuration of the e2c accounting from configuration file");
            logger.info("Exception " + e);
        }
	}
}
