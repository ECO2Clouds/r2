/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.eco2clouds.ac.monitor;

import eu.eco2clouds.accounting.datamodel.parser.Items;
import java.util.LinkedList;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 *  
 */
public class AppStatusTest extends TestCase {
    
    public AppStatusTest(String testName) {
        super(testName);
    }

    public void testPojo() {
        
        Item item = new Item("item1", "id1");
        List<Item> items = new LinkedList<Item>();
        items.add(item);
        
        AppStatus ap = new AppStatus("application", items);
        assertEquals("application", ap.getApplication());
        assertNotNull(ap.getMetrics());
        assertEquals(1, ap.getMetrics().size());
        assertNotNull(ap.getMetrics().get(item));
    }
    
}
