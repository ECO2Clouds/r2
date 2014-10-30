package eu.eco2clouds.scheduler.em.datamodel;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

public class ItemsTest {

	@Test
	public void pojo() {
		ArrayList<ManagedExperiment> managedExperiments = new ArrayList<ManagedExperiment>();
		Items items = new Items();
		items.setManagedExperiments(managedExperiments);
		
		assertEquals(managedExperiments, items.getManagedExperiments());
	}
}
