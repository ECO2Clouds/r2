/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eco2clouds.ac;

import eu.eco2clouds.ac.monitor.AppStatus;
import eu.eco2clouds.ac.monitor.VMStatus;
import java.util.List;
import java.util.Properties;
import junit.framework.TestCase;

/**
 *
 *  
 */
public class ConfigurationTest extends TestCase {
    
    
    public void testLoadProperties() {
        System.out.println("loadProperties");
        Properties result = Configuration.loadProperties();
        assertEquals(result.getProperty(Configuration.PORTAL_USERNAME), "plebani");
        assertEquals(result.getProperty(Configuration.PORTAL_PASSWORD), "AdAmRub8");
        assertEquals(result.getProperty(Configuration.EXPERIMENT_NO), "712");
        assertEquals(result.getProperty(Configuration.SCHEDULER_URL), "https://scheduler.eco2clouds.eu/scheduler");
        assertEquals(result.getProperty(Configuration.KEYSTORE), "keystore.jks");
        assertEquals(result.getProperty(Configuration.PASSWORD), "importkey");

    }

 
}
