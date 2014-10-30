package eu.eco2clouds.scheduler.conf;

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
 */
public class Configuration {
	private static Logger logger = Logger.getLogger(Configuration.class);
	private static final String etcConfiguration = "/etc/e2c-scheduler/scheduler.properties";
	//private static final String etcConfiguration = "D://scheduler.properties";
	public static String bonfireAPIUrl = "https://api.qualification.bonfire-project.i2cat.net:444";
	public static String bonfireEMUrl = "no-valid";
	public static String accountingServiceUrl = "http://localhost:4040/ECO2Clouds-accounting";
	public static String bonfireApiGroup = "no-valid";
	public static String keystore = "no-valid";
	public static String keystoreKey = "no-valid";
	
	static {
        try {
        	String propertiesFile = "scheduler.properties";
        	
        	File f = new File(etcConfiguration);
        	if(f.exists()) { 
        		propertiesFile = etcConfiguration; 
        	}
        	
        	org.apache.commons.configuration.Configuration config = new PropertiesConfiguration(propertiesFile);
            bonfireAPIUrl = config.getString("bonfire.api.url");
            bonfireEMUrl = config.getString("bonfire.em.url");
            bonfireApiGroup = config.getString("bonfire.api.group");
            accountingServiceUrl = config.getString("accounting.service.url");
            keystore = config.getString("keystore");
            keystoreKey = config.getString("keystore.key");
        }
        catch (Exception e) {
            logger.info("Error loading the configuration of the scheduler");
            logger.info("Expcetion " + e);
        }
    }
}
