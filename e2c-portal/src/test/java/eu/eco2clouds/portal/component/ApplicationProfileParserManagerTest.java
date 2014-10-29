/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eco2clouds.portal.component;

import eu.eco2clouds.applicationProfile.datamodel.ApplicationProfile;
import eu.eco2clouds.portal.scheduler.ApplicationProfileParserManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 *  
 */
public class ApplicationProfileParserManagerTest {
    
    ApplicationProfileParserManager apm = new ApplicationProfileParserManager();
    
    public ApplicationProfileParserManagerTest() {        
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
    public void testParse() {
        try {
            ApplicationProfile ap = apm.parse(ApplicationProfileParserManager.APTEST);
            
            assert(ap.getFlow() != null);
            assert(ap.getFlow().getSequence()!=null);
            assert(ap.getFlow().getSequence().size() == 5);
            assert(ap.getFlow().getSequence().get(0).getTask().equals("task1"));
            assert(ap.getFlow().getSequence().get(1).getTask().equals("task2"));
            assert(ap.getFlow().getSequence().get(2).getBranches() != null);
            assert(ap.getFlow().getSequence().get(2).getBranches().size() == 2);
            assert(ap.getFlow().getSequence().get(3).getTask().equals("task5"));
            assert(ap.getFlow().getSequence().get(4).getLoop().size() == 5);
            
            String aps = apm.deserialize(ap);
            String initial = "{\"applicationprofile\":{\"flow\":{\"sequence\":[{\"task\":\"task1\"";
            
            assert(aps.startsWith(initial));
            System.out.println(aps);
        } catch (JsonMappingException ex) {
            Logger.getLogger(ApplicationProfileParserManagerTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(ApplicationProfileParserManagerTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.getMessage());
        }
        
    }
}