/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eco2clouds.portal.component;

import eu.eco2clouds.accounting.datamodel.parser.Action;
import eu.eco2clouds.accounting.datamodel.parser.Experiment;
import eu.eco2clouds.accounting.datamodel.parser.VM;
import eu.eco2clouds.accounting.datamodel.parser.VMHost;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 *  
 */
public class ResourceTableBeanTest {

    public ResourceTableBeanTest() {
    }

    @Test
    public void testBean() {

        long now = System.currentTimeMillis();

        VM vm = new VM();
        vm.setActions(new HashSet<Action>());
        vm.setBonfireId("eco2clouds");
        vm.setBonfireUrl("http://eco2clouds");
        vm.setHost("Host");
        vm.setHref("eco2clouds_exp");
        vm.setId(Integer.valueOf(1));
        vm.addNic("1.1.1.1", "00:00:00:00:00:00", "type");
        vm.setLinks(null);
        vm.setName("name");
        vm.setvMhosts(new HashSet<VMHost>());

        ResourceTableBean rtb = new ResourceTableBean();
        rtb.setBonfireId("eco2clouds");
        rtb.setBonfireUrl("http://eco2clouds");
        rtb.setHost("Host");
        rtb.setHref("eco2clouds_exp");
        rtb.setId(1);
        rtb.setIp("1.1.1.1");
        rtb.setName("name");

        assertEquals("eco2clouds", rtb.getBonfireId());
        assertEquals("http://eco2clouds", rtb.getBonfireUrl());
        assertEquals("Host", rtb.getHost());
        assertEquals("eco2clouds_exp", rtb.getHref());
        assertEquals(new Integer(1), rtb.getId());
        assertEquals("1.1.1.1", rtb.getIp());
        assertEquals("name", rtb.getName());
        assertNull(rtb.getVm());
        
        ResourceTableBean rtb1 = new ResourceTableBean(vm);
        assertEquals(vm.getBonfireId(), rtb1.getBonfireId());
        assertEquals(vm.getBonfireUrl(), rtb1.getBonfireUrl());
        assertEquals(vm.getHost(), rtb1.getHost());
        assertEquals(vm.getHref(), rtb1.getHref());
        assertEquals(vm.getId(), rtb1.getId());
        assertEquals(vm.getNics().get(0).getIp(), rtb1.getIp());
        assertEquals(vm.getName(), rtb1.getName());
        assertNotNull(rtb1.getVm());

        VM vm1 = rtb1.getVm();
        assertEquals(vm.getActions(), vm1.getActions());
        assertEquals(vm.getBonfireId(), vm1.getBonfireId());
        assertEquals(vm.getBonfireUrl(), vm1.getBonfireUrl());
        assertEquals(vm.getHost(), vm1.getHost());
        assertEquals(vm.getHref(), vm1.getHref());
        assertEquals(vm.getId(), vm1.getId());
        assertEquals(vm.getIp(), vm1.getIp());
        assertEquals(vm.getLinks(), vm1.getLinks());
        assertEquals(vm.getName(), vm1.getName());
        assertEquals(vm.getvMhosts(), vm1.getvMhosts());

    }

}
