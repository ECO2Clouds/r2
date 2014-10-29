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
package eu.eco2clouds.ac;

import eu.eco2clouds.ac.monitor.AppStatus;
import eu.eco2clouds.ac.monitor.VM;
import eu.eco2clouds.ac.monitor.Item;
import eu.eco2clouds.ac.monitor.PMStatus;
import eu.eco2clouds.ac.monitor.VMStatus;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 *  
 */
public class Configuration {

    private final static Logger LOGGER = Logger.getLogger(Configuration.class.getName());
    
    
    private static Properties prop = new Properties();
    public static final String SCHEDULER_URL = "scheduler_url";
    public static final String KEYSTORE = "keystore";
    public static final String PASSWORD = "password";
    public static final String PORTAL_USERNAME = "portal_username";
    public static final String PORTAL_PASSWORD = "portal_password";
    public static final String EXPERIMENT_NO = "experiment_no";
    public static final String VMS = "vms";
    public static final String MONITORING_STATE = "monitoring_state";
    public static final String SCHEDULER_STATE = "scheduler_state";
    public final static String ALA_SERVER_PORT = "ala_server_port";
    public final static String AC_IP = "ac_ip";



    public static Properties loadProperties() {
        try {

            if (prop.isEmpty()) {
                //load a properties file
                Configuration.prop.load(new FileInputStream("applicationcontroller.properties"));
                Configuration.prop.load(new FileInputStream("applicationcontroller_infra.properties"));

                //get the property value and print it out
                LOGGER.info(Configuration.SCHEDULER_URL + ":" + Configuration.prop.getProperty(Configuration.PORTAL_USERNAME));
                LOGGER.info(Configuration.KEYSTORE + ":" + Configuration.prop.getProperty(Configuration.KEYSTORE));
                LOGGER.info(Configuration.PASSWORD + ":" + Configuration.prop.getProperty(Configuration.PASSWORD));
                LOGGER.info(Configuration.MONITORING_STATE + ":" + Configuration.prop.getProperty(Configuration.MONITORING_STATE));
                LOGGER.info(Configuration.SCHEDULER_STATE + ":" + Configuration.prop.getProperty(Configuration.SCHEDULER_STATE));

                LOGGER.info(Configuration.PORTAL_USERNAME + ":" + Configuration.prop.getProperty(Configuration.PORTAL_USERNAME));
                LOGGER.info(Configuration.PORTAL_PASSWORD + ":" + Configuration.prop.getProperty(Configuration.PORTAL_PASSWORD));
                LOGGER.info(Configuration.EXPERIMENT_NO + ":" + Configuration.prop.getProperty(Configuration.EXPERIMENT_NO));
                LOGGER.info(Configuration.VMS + ":" + Configuration.prop.getProperty(Configuration.VMS));
                LOGGER.info(Configuration.ALA_SERVER_PORT + ":" + Configuration.prop.getProperty(Configuration.ALA_SERVER_PORT));
                LOGGER.info(Configuration.AC_IP + ":" + Configuration.prop.getProperty(Configuration.AC_IP));
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return Configuration.prop;

    }

    public static Properties getProperties() {

        if (prop.isEmpty()) {
            Configuration.loadProperties();
        }
        return Configuration.prop;

    }

    public static List<PMStatus> getPmStatus() {

        return new LinkedList<PMStatus>();
    }

}
