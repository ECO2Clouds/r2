/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eco2clouds.portal.component;

import eu.eco2clouds.portal.scheduler.SchedulerManager;
import eu.eco2clouds.portal.scheduler.SchedulerManagerFactory;
import eu.eco2clouds.portal.service.data.Notification;
import eu.eco2clouds.portal.service.data.NotificationList;
import eu.eco2clouds.portal.service.data.NotificationListFactory;
import java.util.Date;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 *  
 */
public class NotificationListTest {

    NotificationList notificationList;

    public NotificationListTest() {

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
    public void testNotificationListInstantiation() {
        NotificationList notificationList = NotificationListFactory.getInstance();
        
        if (notificationList == null) {
            fail("notificationList should be instantiated");
        } else {
            assert(true);
        } 
            
    }
    
    @Test
    public void testChangedFlag() {
        
        assert(!notificationList.isChanged());
        
        Notification n = new Notification();
        n.setTimestamp(new Date());
        n.setLevel(0);
        n.setTitle("title");
        n.setDescription("description");
        n.setSource("source");
        
        notificationList.addNotification(n);
        
        assert(notificationList.isChanged());
        
        notificationList.getList();
        
        assert(!notificationList.isChanged());
        
    }
}