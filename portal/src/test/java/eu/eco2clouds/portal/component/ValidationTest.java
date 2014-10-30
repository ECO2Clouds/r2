/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eco2clouds.portal.component;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 *
 *  
 */
public class ValidationTest {

    WebDriverBackedSelenium selenium;
    WebDriver driver;

    public ValidationTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        //driver = new FirefoxDriver();

        //selenium = new WebDriverBackedSelenium(driver,
               // "http://localhost:8084");
    }

    @After
    public void tearDown() {
        //driver.quit();
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //

    //@Test
    //public void testSubmit() {
        //selenium.open("/portal/gui");
        //selenium.click("btnValidate");
        //assert (true);
    //}
}