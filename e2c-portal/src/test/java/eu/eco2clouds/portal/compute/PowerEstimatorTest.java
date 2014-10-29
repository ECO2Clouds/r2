/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eco2clouds.portal.compute;

import eu.eco2clouds.portal.exception.E2CPortalException;
import eu.eco2clouds.portal.scheduler.SchedulerManagerFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 *  
 */
public class PowerEstimatorTest {

    @Test
    public void testCO2Predictor() {

        SchedulerManagerFactory.configure("https://129.69.19.70/scheduler", "/etc/e2c-portal/keystore.jks", "xxx");

        
        PowerEstimator estimator = new PowerEstimator();

        try {
            assert (estimator.estimatePower("fr-inria", 1, 0).getMin() == 0);
            assert (estimator.estimatePower("fr-inria", 0, 1).getMin() == 0);
            assert (estimator.estimatePower("uk-epcc", 1, 0).getMin() == 0);
            assert (estimator.estimatePower("uk-epcc", 0, 1).getMin() == 0);
            assert (estimator.estimatePower("de-hlrs", 1, 0).getMin() == 0);
            assert (estimator.estimatePower("de-hlrs", 0, 1).getMin() == 0);
        } catch (E2CPortalException ex) {
            Logger.getLogger(PowerEstimatorTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
        try {
            PowerItem e = estimator.estimatePower("fr-inria", 1, -1);
            fail();
        } catch (E2CPortalException ex) {
            Logger.getLogger(PowerEstimatorTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (true);
        }
        try {
            PowerItem e = estimator.estimatePower("uk-epcc", 1, -1);
            fail();
        } catch (E2CPortalException ex) {
            Logger.getLogger(PowerEstimatorTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (true);
        }
        try {
            PowerItem e = estimator.estimatePower("de-hlrs", 1, -1);
            fail();
        } catch (E2CPortalException ex) {
            Logger.getLogger(PowerEstimatorTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (true);
        }
        try {
            PowerItem e = estimator.estimatePower("fr-inria", -1, 1);
            fail();
        } catch (E2CPortalException ex) {
            Logger.getLogger(PowerEstimatorTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (true);
        }
        try {
            PowerItem e = estimator.estimatePower("uk-epcc", -1, 1);
            fail();
        } catch (E2CPortalException ex) {
            Logger.getLogger(PowerEstimatorTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (true);
        }
        try {
            PowerItem e = estimator.estimatePower("de-hlrs", -1, 1);
            fail();
        } catch (E2CPortalException ex) {
            Logger.getLogger(PowerEstimatorTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (true);
        }
        try {
            PowerItem e = estimator.estimatePower("fr-inria", 1, 1);
            assert (e.getMin() > 0);
        } catch (E2CPortalException ex) {
            Logger.getLogger(PowerEstimatorTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
        try {
            PowerItem e = estimator.estimatePower("uk-epcc", 1, 1);
            assert (e.getMin() > 0);
        } catch (E2CPortalException ex) {
            Logger.getLogger(PowerEstimatorTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
        try {
           assert (estimator.estimatePower("de-hlrs", 1, 1).getMin() > 0);
        } catch (E2CPortalException ex) {
            Logger.getLogger(PowerEstimatorTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }

}
