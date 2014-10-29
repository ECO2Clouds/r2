/* 
 * Copyright 2014 Politecnico di Milano.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

 *  @author: Pierluigi Plebani, Politecnico di Milano, Italy
 *  e-mail pierluigi.plebani@polimi.it

 */
package eu.eco2clouds.experiment.eels.la;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 *
 *  
 */
public class Configuration {
     private final static Logger LOGGER = Logger.getLogger(Configuration.class.getName());
    
    private static Properties prop = new Properties();

    public static final String INITDATDIT_PROPNAME = "initdat_dir";
    
    public static final String SIMULATIONDIR_PROPNAME = "simulation_dir";
        
    public static final String APPLICATION_CONTROLLER_IP = "application_controller_ip";
    
    public static final String OCEANOGRAPHIC_DATA_IP = "oceanographic_data_ip";

        
    public static Properties loadProperties() {
    	try {
               //load a properties file
    		Configuration.prop.load(new FileInputStream("eelslocalagent.properties"));
 
               //get the property value and print it out
                LOGGER.info(Configuration.prop.getProperty(Configuration.INITDATDIT_PROPNAME));
    		LOGGER.info(Configuration.prop.getProperty(Configuration.SIMULATIONDIR_PROPNAME));
    		LOGGER.info(Configuration.prop.getProperty(Configuration.APPLICATION_CONTROLLER_IP));
    		LOGGER.info(Configuration.prop.getProperty(Configuration.OCEANOGRAPHIC_DATA_IP));
 
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
 
        return Configuration.prop;
        
    }
    
    public static Properties getProperties() {
        
        return Configuration.prop;
        
    }
    
    
}
