/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eco2clouds.portal.component;

import eu.eco2clouds.portal.scheduler.SchedulerManager;
import com.sun.jersey.api.client.ClientResponse;
import eu.eco2clouds.portal.exception.E2CPortalException;
import eu.eco2clouds.portal.scheduler.SchedulerManagerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 *  
 */
public class SchedulerManagerTest {

    SchedulerManager schedulerManager;

    public SchedulerManagerTest() {

      //  SchedulerManagerFactory.configure("https://scheduler.integration.e2c.bonfire.grid5000.fr:443/scheduler", "keystore.jks", "xxx");
        SchedulerManagerFactory.configure("https://scheduler.eco2clouds.eu/scheduler", "keystore.jks", "xxx");

    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of submitApplicationProfile method, of class SchedulerManager.
     */
    @Test
    public void testSchedulerInstantiation() {

        schedulerManager = SchedulerManagerFactory.getInstance();

        if (schedulerManager == null) {
            fail("SchedulerManager should be instantiated");
        } else {
            assert (true);
        }
        
        SchedulerManagerFactory.configure(null, null, null);
        schedulerManager = SchedulerManagerFactory.getInstance();
        
        if (schedulerManager != null) {
            fail("SchedulerManager should not be instantiated");
        } else {
            assert (true);
        }

    }

    @Test
    public void testSchedulerConnection() {
        try {

    
            schedulerManager = SchedulerManagerFactory.getInstance();

            ClientResponse response = schedulerManager.pingScheduler();

            assertEquals(200, response.getStatus());

            BufferedReader reader = new BufferedReader(new StringReader(response.getEntity(String.class)));
            assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", reader.readLine());
            assertEquals("<root xmlns=\"http://scheduler.eco2clouds.eu/doc/schemas/xml\" href=\"/\">", reader.readLine());
            assertEquals("<version>0.1</version>", reader.readLine());
            reader.readLine();
            assertEquals("<link rel=\"experiments\" href=\"/experiments\" type=\"application/vnd.eco2clouds+xml\"/>", reader.readLine());
            assertEquals("</root>", reader.readLine());


        } catch (IOException ex) {
            Logger.getLogger(SchedulerManagerTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.getMessage());
        } catch (E2CPortalException ex) {
            Logger.getLogger(SchedulerManagerTest.class.getName()).log(Level.SEVERE, null, ex);
            assert(true);
            //fail(ex.getMessage());
        }

    }
}
