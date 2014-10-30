/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.eco2clouds.ac.monitor;

import junit.framework.Test;
import junit.framework.TestCase;

/**
 *
 *  
 */
public class VMTest extends TestCase {
    
    public VMTest(String testName) {
        super(testName);
    }

    public void testPojo() {
        
        VM vm = new VM();
        assertNull(vm.getAlaServer());
        assertNull(vm.getId());
        assertNull(vm.getName());
        
        vm.setAlaServer("alaserver");
        vm.setName("name");
        vm.setId("id");
                
        assertEquals("alaserver", vm.getAlaServer());
        assertEquals("id", vm.getId());
        assertEquals("name", vm.getName());

        VM vm1 = new VM("alaserver");
        assertEquals("alaserver", vm1.getAlaServer());
        assertNull(vm1.getId());
        assertNull(vm1.getName());
        
        VM vm2 = new VM("alaserver", "id", "name");
        assertEquals("alaserver", vm2.getAlaServer());
        assertEquals("id", vm2.getId());
        assertEquals("name", vm2.getName());
    }
    
}
