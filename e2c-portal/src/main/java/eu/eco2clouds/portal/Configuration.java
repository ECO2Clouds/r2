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
package eu.eco2clouds.portal;

import com.vaadin.server.VaadinServlet;
import eu.eco2clouds.portal.scheduler.ApplicationProfileParserManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *  
 */
public class Configuration {

    public static String[] locations = new String[]{"fr-inria", "de-hrls", "uk-epcc"};    
    
    public static String propertiesDir;
    static {
        if (VaadinServlet.getCurrent() != null) {   
             propertiesDir =  VaadinServlet.getCurrent().getServletContext().getInitParameter("properties.dir");
        } else {
            propertiesDir = "src/main/resources";
        }
    }
    public static String schedulerUrl = Configuration.getProperty("scheduler.url");
    public static String keystoreFilename = Configuration.getProperty("keystore.filename");
    public static String keystorePwd = Configuration.getProperty("keystore.pwd");
    public static long refreshMonitorInterval = Long.parseLong(Configuration.getProperty("refresh.monitor.interval"));
    public static String octaveDir = Configuration.getProperty("octave.dir");
    public static String octavePath = Configuration.getProperty("octave.path");
    //public static boolean refreshUIActive = Boolean.parseBoolean(Configuration.getProperty("refresh.ui.active"));
    public static String applicationProfileSample = Configuration.getApplicationProfileSample();
    
    private static Properties prop = null;
    
    public static String getProperty(String propertyName) {

        if (Configuration.prop == null) {
            try {
                String fileName = Configuration.propertiesDir + File.separatorChar + "e2c-portal.properties";
                Logger.getLogger(Configuration.class.getName()).log(Level.INFO, "Reading properties in " + fileName);
                InputStream input = new FileInputStream(fileName);
                prop = new Properties();
                prop.load(input);

                Set<Object> names = prop.keySet();
                for (Object name : names) {
                    Logger.getLogger(Configuration.class.getName()).log(Level.INFO, (String) name + "=" + prop.getProperty((String) name));
                }
            } catch (IOException ex) {
                Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }

        }

        return prop.getProperty(propertyName);

    }

    
    public static String getApplicationProfileSample() {

        StringBuilder sb = new StringBuilder();
        sb.append(" ");
         
        BufferedReader file = null;
        try {
            file = new BufferedReader(new FileReader(Configuration.propertiesDir + File.separatorChar + "eels_applicationprofile.json"));

            String line = file.readLine();

            while (line != null) {
                sb.append(line + "\n");
                line = file.readLine();
            }



        } catch (FileNotFoundException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return ApplicationProfileParserManager.APTEST;
        } catch (IOException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return ApplicationProfileParserManager.APTEST;
        } finally {

            if (file != null) {
                try {
                    file.close();
                } catch (IOException ex) {
                    Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
            return sb.toString();
        }

    }
}
