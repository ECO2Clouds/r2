package eu.eco2clouds.scheduler.em.datamodel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LinkTest {

	@Test
	public void gettersAndSettersTest() {
		Link link = new Link();
		link.setHref("http://something.com");
		link.setRel("/");
		
		assertEquals("http://something.com", link.getHref());
		assertEquals("/", link.getRel());
	}
}
