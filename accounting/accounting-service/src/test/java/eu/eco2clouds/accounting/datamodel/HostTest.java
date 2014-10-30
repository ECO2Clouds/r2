package eu.eco2clouds.accounting.datamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import eu.eco2clouds.accounting.datamodel.Host;

public class HostTest {

	@Test
	public void pojo() {
		Host host = new Host();
		host.setId(11);
		host.setName("hostname");
		host.setState(1);
		host.setConnected(true);

		assertEquals(11, host.getId());
		assertEquals("hostname", host.getName());
		assertEquals(1, host.getState());
		assertTrue(host.isConnected());
		
        Host host2 = new Host(2,0,"host2",false);
		
		assertEquals(2, host2.getId());
		assertEquals("host2", host2.getName());
		assertEquals(0, host2.getState());
		assertEquals(false, host2.isConnected());
	}
}
