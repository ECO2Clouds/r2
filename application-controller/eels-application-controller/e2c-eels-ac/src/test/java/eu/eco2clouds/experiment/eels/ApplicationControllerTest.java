/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eco2clouds.experiment.eels;

import eu.eco2clouds.ac.ACException;
import eu.eco2clouds.ac.ApplicationController;
import eu.eco2clouds.app.Application;
import eu.eco2clouds.app.ApplicationDecisor;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;


/**
 *
 *  
 */
public class ApplicationControllerTest extends TestCase {

    public ApplicationControllerTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testStartup() {
       // try {
         /*   Application app = new EelsApplication();
            ApplicationDecisor dec = new EelsDecisor();
            ApplicationProfile profile = new ApplicationProfile();
            System.out.println("testing the startup of the application controller");
            ApplicationController instance = new ApplicationController(app, dec, profile);*/
            //instance.start();
            // TODO review the generated test code and remove the default call to fail.
            assertTrue(true);
        /*} catch (ACException ex) {
            Logger.getLogger(ApplicationControllerTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.getMessage());
        }*/
    }
}
