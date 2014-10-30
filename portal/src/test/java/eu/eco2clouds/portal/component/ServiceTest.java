/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eco2clouds.portal.component;

import eu.eco2clouds.portal.service.PortalService;
import eu.eco2clouds.portal.service.data.Notification;
import eu.eco2clouds.portal.service.data.NotificationList;
import eu.eco2clouds.portal.service.data.NotificationListFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 *  
 */
public class ServiceTest {

    PortalService service;
    NotificationList notificationList;

    public ServiceTest() {

        service = new PortalService();
        notificationList = NotificationListFactory.getInstance();
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //

    @Test
    public void testRoot() {

        String s = service.getXml();

        try {
            BufferedReader reader = new BufferedReader(new StringReader(s));

            assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", reader.readLine());
            assertEquals("<root xmlns=\"http://portal.eco2clouds.eu/doc/schemas/xml\" href=\"/\">", reader.readLine());
            assertEquals("<version>0.1</version>", reader.readLine());
            reader.readLine();
            assertEquals("<link rel=\"service/notifications\" href=\"/service/notifications\" type=\"application/xml\"/>", reader.readLine());
            assertEquals("</root>", reader.readLine());

        } catch (IOException ex) {
            Logger.getLogger(SchedulerManagerTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.getMessage());

        }
    }
    
    @Test
    public void testAddNotification() {
       
        Notification notification = new Notification(new Date(), 0, "title", "description", "source");
        
        notificationList.getList().clear();

        service.addNotification(notification);
        
        assertEquals(notificationList.getList().size(), 1);
        
        Notification n = notificationList.getList().get(0);
        assertEquals(n.getLevel(), notification.getLevel());
        assertEquals(n.getTitle(), notification.getTitle());
        assertEquals(n.getDescription(), notification.getDescription());
        assertEquals(n.getSource(), notification.getSource());
        
    }
}