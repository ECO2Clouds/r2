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
package eu.eco2clouds.experiment.eels;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 *
 *  
 */
public class Configuration {

    private final static Logger LOGGER = Logger.getLogger(eu.eco2clouds.experiment.eels.Configuration.class.getName());
    private static Properties prop = new Properties();
    public static final String START_YEAR = "start_year";
    public static final String STOP_YEAR = "stop_year";
    public static final String NEELS = "neels";
    public static final String OCEANOGRAPHIC_DATA_IP = "oceanographic_data_ip";
    public static final String OPTIMIZATION = "optimization";
    
    public static final String OPT_THROUGHPUT = "throughput";
    public static final String OPT_POWER = "power";
    public static final String OPT_EFFICIENCY = "efficiency";
    public static final String OPT_CO2 = "co2";
    public static final String OPT_OFF = "off";
    

    public static Properties loadProperties() {
        try {
            //load a properties file
            prop.load(new FileInputStream("eelsapplication.properties"));

            //get the property value and print it out

            LOGGER.info(Configuration.START_YEAR + ":" + Configuration.prop.getProperty(Configuration.START_YEAR));
            LOGGER.info(Configuration.STOP_YEAR + ":" + Configuration.prop.getProperty(Configuration.STOP_YEAR));
            LOGGER.info(Configuration.NEELS + ":" + Configuration.prop.getProperty(Configuration.NEELS));
            LOGGER.info(Configuration.OCEANOGRAPHIC_DATA_IP + ":" + Configuration.prop.getProperty(Configuration.OCEANOGRAPHIC_DATA_IP));
            LOGGER.info(Configuration.OPTIMIZATION + ":" + Configuration.prop.getProperty(Configuration.OPTIMIZATION));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return prop;

    }

    public static Properties getProperties() {

        return prop;

    }

}
