/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eco2clouds.ac;

import eu.eco2clouds.ac.monitor.AppStatus;
import eu.eco2clouds.ac.monitor.Item;
import eu.eco2clouds.ac.monitor.ItemValue;
import eu.eco2clouds.ac.monitor.PMStatus;
import eu.eco2clouds.ac.monitor.VMStatus;
import eu.eco2clouds.app.Application;
import eu.eco2clouds.app.ApplicationDecisor;
import eu.eco2clouds.app.ApplicationMetric;
import eu.eco2clouds.component.MonitoringManager;
import eu.eco2clouds.component.MonitoringManagerFactory;
import java.util.LinkedList;
import java.util.List;
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

    /**
     * Test of start method, of class ApplicationController.
     */
    public void testInitializations() throws Exception {
        System.out.println("Initialization");
        ApplicationController instance = null;
        try {
            instance = new ApplicationController(null, null, null);
        } catch (ACException ex) {
            assert (instance == null);

        }
        try {
            instance = new ApplicationController(new FakeApplication(), null, null);
        } catch (ACException ex) {
            assert (instance == null);
        }
        try {
            instance = new ApplicationController(new FakeApplication(), new FakeDecisor(), null);
        } catch (ACException ex) {
            assert (instance == null);
        }
        try {
            instance = new ApplicationController(new FakeApplication(), new FakeDecisor(), null);
        } catch (ACException ex) {
            assert (instance == null);
        }
        try {
            instance = new ApplicationController(new FakeApplication(), new FakeDecisor(), new FakePerformance());
        } catch (ACException ex) {
            assert (instance == null);
        }
    }

    /**
     * Test of start method, of class ApplicationController.
     */
    public void testStart() throws Exception {
        try {
        ApplicationController instance = new ApplicationController(new FakeApplication(), new FakeDecisor(), new FakePerformance());
        instance.start();
        } catch (ACException ex) {
            System.out.println(ex.getMessage());
            fail();
        }
        assert (true);
    }

    /**
     * Test of notifyApplicationStatus method, of class ApplicationController.
     */
    public void testNotifyApplicationStatus() {
        try {
            System.out.println("notifyApplicationStatus");
            int status = Application.STOPPED;
            FakeDecisor fakeDecisor = new FakeDecisor();
            ApplicationController instance = new ApplicationController(new FakeApplication(), fakeDecisor, new FakePerformance());
            
            instance.notifyApplicationStatus(status);
            if (fakeDecisor.getStatus() == Application.STOPPED) {
                assert(true);
            } else {
                fail();
            }
            // TODO review the generated test code and remove the default call to fail.
        } catch (ACException ex) {
            Logger.getLogger(ApplicationControllerTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }

    /**
     * Test of decide method, of class ApplicationController.
     */
    public void testDecide() {
        try {
            System.out.println("decide");

            FakeDecisor fakeDecisor = new FakeDecisor();
            ApplicationController instance = new ApplicationController(new FakeApplication(), fakeDecisor, new FakePerformance());
            instance.decide();
            
            if (fakeDecisor.decisionState.equals("decided")) {
                assert(true);
            } else {
                fail();
            }
                
            // TODO review the generated test code and remove the default call to fail.
        } catch (ACException ex) {
            Logger.getLogger(ApplicationControllerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

class FakeApplication extends Application {

    int status;
    
    public void run() {
        status = Application.RUNNING;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(FakeApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        status = Application.STOPPED;
    }
    
    public int getStatus() {

        return this.status;
    }    
    
}

class FakeDecisor extends ApplicationDecisor {
    
    public String decisionState = "undecided";

    @Override
    public void decide() {
        
        this.decisionState = "decided";
        
    }

    public void run() {
        
    }
    
}

class FakeMM extends MonitoringManager {

    public List<VMStatus> getVmStatusList() {
        return new LinkedList<VMStatus>();
    }

    public List<PMStatus> getPmStatusList() {
        return new LinkedList<PMStatus>();
    }

    public AppStatus getAppStatus() {
        return new AppStatus("experiment", new LinkedList<Item>());
    }
    
}

class FakePerformance extends ApplicationMetric {

    @Override
    public ItemValue calculate(Item item, List<PMStatus> pmStatusList, List<VMStatus> vmStatusList, AppStatus appStatus) {
        System.out.println("fake computation of applicatrion level metrics");
        return new ItemValue(System.currentTimeMillis(), -100);
    }

    @Override
    public AppStatus initAppStatus() {
       LinkedList<Item> items = new LinkedList<Item>();

            items.add(new Item("FakeMetric", "FakeMetric"));

            AppStatus appStatus = new AppStatus("eelsApplication", items);

            return appStatus;

    }
    
}