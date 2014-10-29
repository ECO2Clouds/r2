/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eco2clouds.portal.component;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import eu.eco2clouds.portal.component.ApplicationProfileLayout;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 *  
 */
public class PortalGUITest {
    
    
    public PortalGUITest() {
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
    //@Test
    public void testApplicationProfileLayout() {
        
        ApplicationProfileLayout apl = new ApplicationProfileLayout();
        
        int check = 2;
        int count = 0;
        
        for (int i = 0; i < apl.getComponentCount(); i++) {
            
            Component component = apl.getComponent(i);
            
            if (component instanceof TextArea) {
                if (component.getCaption().equals("Application profile")) {
                    count ++;
                }
            }
            if (component instanceof VerticalLayout) {
                VerticalLayout buttonset = (VerticalLayout)component;
                if (buttonset.getComponentCount() == 3) {
                    count++;
                }
            }
        }
        
        assert(count == check);
    
    }
    
    //@Test
    public void testButtonSet() {
        
        int check = 3;
        int count = 0;
        
        ApplicationProfileLayout apl = new ApplicationProfileLayout();
        for (int i = 0; i < apl.getComponentCount(); i++) {
            
            Component component = apl.getComponent(i);
            
            if (component instanceof VerticalLayout) {
                VerticalLayout buttonset = (VerticalLayout)component;
                for (int j=0; j<buttonset.getComponentCount(); j++) {
                    Button b = (Button)buttonset.getComponent(j);
                    
                    if (b.getCaption().equals("Submit")) {
                        count++;
                    }
                    
                    if (b.getCaption().equals("Validate")) {
                        count++;
                    }
                    if (b.getCaption().equals("see Process")) {
                        count++;
                    }
                    
                }
                
            }
        }
        
        assert(check == count);
    }
    
    //@Test
    public void testBtnValidate() {
        
        ApplicationProfileLayout apl = new ApplicationProfileLayout();
        for (int i = 0; i < apl.getComponentCount(); i++) {
            
            Component component = apl.getComponent(i);
            
            if (component instanceof VerticalLayout) {
                VerticalLayout buttonset = (VerticalLayout)component;
                for (int j=0; j<buttonset.getComponentCount(); j++) {
                    Button b = (Button)buttonset.getComponent(j);
                    
                    if (b instanceof ApplicationProfileLayout.BtnSubmit) {
                        
                        assert(((ApplicationProfileLayout.BtnSubmit)b).getScheduler()!= null);
                        assert(((ApplicationProfileLayout.BtnSubmit)b).getParser()!= null);
                    }
                }
            }
        }
    }
}