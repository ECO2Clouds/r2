/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eco2clouds.portal.component;

import eu.eco2clouds.accounting.datamodel.parser.Experiment;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 *  
 */
public class ExperimentTableBeanTest {

    public ExperimentTableBeanTest() {
    }

    @Test
    public void testBean() {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        long now = System.currentTimeMillis();

        Experiment e = new Experiment();
        e.setId(Integer.valueOf(1));
        e.setHref("eco2clouds_exp");
        e.setManagedExperimentId(Long.valueOf(12));
        e.setStartTime(now);
        e.setEndTime(now + 1000);
        e.setStatus("running");
        e.setBonfireGroupId("eco2clouds");
        e.setBonfireUserId("plebani");

        ExperimentTableBean etb = new ExperimentTableBean();
        etb.setId(Integer.valueOf(1));
        etb.setHref("eco2clouds_exp");
        etb.setManagedExperimentId(Long.valueOf(12));
        etb.setStartTime(sdf.format(new Date(now)));
        etb.setEndTime(sdf.format(new Date(now + 1000)));
        etb.setStatus("running");
        etb.setGroup("eco2clouds");
        etb.setUser("plebani");

        assertEquals(Integer.valueOf(1), etb.getId());
        assertEquals("eco2clouds_exp", etb.getHref());
        assertEquals(Long.valueOf(12), etb.getManagedExperimentId());
        assertEquals(sdf.format(new Date(now)), etb.getStartTime());
        assertEquals(sdf.format(new Date(now + 1000)), etb.getEndTime());
        assertEquals("running", etb.getStatus());
        assertEquals("eco2clouds", etb.getGroup());
        assertEquals("plebani", etb.getUser());
        assertNull(etb.getExperiment());

        ExperimentTableBean etb1 = new ExperimentTableBean(e);
        assertEquals(e.getId(), etb1.getId());
        assertEquals(e.getHref(), etb1.getHref());
        assertEquals(e.getManagedExperimentId(), etb1.getManagedExperimentId());
        assertEquals(sdf.format(e.getStartTime()), etb1.getStartTime());
        assertEquals(sdf.format(e.getEndTime()), etb1.getEndTime());
        assertEquals(e.getStatus(), etb1.getStatus());
        assertEquals(e.getBonfireGroupId(), etb1.getGroup());
        assertEquals(e.getBonfireUserId(), etb1.getUser());
        assertNotNull(etb1.getExperiment());

        Experiment e1 = etb1.getExperiment();
        assertEquals(e.getId(), e1.getId());
        assertEquals(e.getHref(), e1.getHref());
        assertEquals(e.getManagedExperimentId(), e1.getManagedExperimentId());
        assertEquals(e.getStartTime(), e1.getStartTime());
        assertEquals(e.getEndTime(), e1.getEndTime());
        assertEquals(e.getStatus(), e1.getStatus());
        assertEquals(e.getBonfireGroupId(), e1.getBonfireGroupId());
        assertEquals(e.getBonfireUserId(), e1.getBonfireUserId());
    }

}
