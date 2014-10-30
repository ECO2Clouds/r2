package eu.eco2clouds.scheduler.em.datamodel;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

public class ManagedExperimentTest {
	
	@Test
	public void pojo() {
		ManagedExperiment me = new ManagedExperiment();
		me.setName("name");
		me.setDescription("description");
		me.setStatus("DEPLOYED");
		ArrayList<Link> links = new ArrayList<Link>();
		me.setLinks(links);
		
		assertEquals("DEPLOYED", me.getStatus());
		assertEquals("description", me.getDescription());
		assertEquals("name", me.getName());
		assertEquals(links, me.getLinks());
	}
	
	@Test
	public void parseTest() throws Exception {
		String managedExperimentString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
										 + "<managed_experiment href=\"/managed_experiments/247\">"
										 		+ "<name>ReviewAmazonExperiment</name>"
										 		+ "<description>Horizontal scalability demo</description>"
										 		+ "<status>DEPLOYED</status>"
										 		+ "<link rel=\"experiment\" href=\"https://api.bonfire-project.eu/experiments/21272\"/>"
										 		+ "<link rel=\"log\" href=\"/managed_experiments/247/log\"/>"
										 + "</managed_experiment>";
		
		JAXBContext jaxbContext = JAXBContext.newInstance(ManagedExperiment.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		ManagedExperiment me = (ManagedExperiment) jaxbUnmarshaller.unmarshal(new StringReader(managedExperimentString));
		
		assertEquals("/managed_experiments/247", me.getHref());
		assertEquals("ReviewAmazonExperiment", me.getName());
		assertEquals("Horizontal scalability demo", me.getDescription());
		assertEquals("DEPLOYED", me.getStatus());
		assertEquals(2, me.getLinks().size());
		assertEquals("experiment", me.getLinks().get(0).getRel());
		assertEquals("https://api.bonfire-project.eu/experiments/21272", me.getLinks().get(0).getHref());
		assertEquals("log", me.getLinks().get(1).getRel());
		assertEquals("/managed_experiments/247/log", me.getLinks().get(1).getHref());
	}
	
	@Test
	public void parseManagedExperimentId() {
		ManagedExperiment me = new ManagedExperiment();
		assertEquals(-1, me.getManagedExperimentId());
		
		me.setHref("/managed_experiments/247");
		assertEquals(247, me.getManagedExperimentId());
	}
	
	@Test
	public void parseGetBonFIREExperimentId() throws Exception {
		String managedExperimentString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
										 + "<managed_experiment href=\"/managed_experiments/247\">"
										 		+ "<name>ReviewAmazonExperiment</name>"
										 		+ "<description>Horizontal scalability demo</description>"
										 		+ "<status>DEPLOYED</status>"
										 		+ "<link rel=\"experiment\" href=\"https://api.bonfire-project.eu:444/experiments/21272\"/>"
										 		+ "<link rel=\"log\" href=\"/managed_experiments/247/log\"/>"
										 + "</managed_experiment>";
		
		JAXBContext jaxbContext = JAXBContext.newInstance(ManagedExperiment.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		ManagedExperiment me = (ManagedExperiment) jaxbUnmarshaller.unmarshal(new StringReader(managedExperimentString));
		
		assertEquals(21272, me.getBonFIREExperimentId());
		
		managedExperimentString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				 + "<managed_experiment href=\"/managed_experiments/247\">"
				 		+ "<name>ReviewAmazonExperiment</name>"
				 		+ "<description>Horizontal scalability demo</description>"
				 		+ "<status>RUNNING</status>"
				 		+ "<link rel=\"experiment\" href=\"https://api.bonfire-project.eu:444/experiments/21272\"/>"
				 		+ "<link rel=\"log\" href=\"/managed_experiments/247/log\"/>"
				 + "</managed_experiment>";
		
		me = (ManagedExperiment) jaxbUnmarshaller.unmarshal(new StringReader(managedExperimentString));

		assertEquals(21272, me.getBonFIREExperimentId());
		
		managedExperimentString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				 + "<managed_experiment href=\"/managed_experiments/247\">"
				 		+ "<name>ReviewAmazonExperiment</name>"
				 		+ "<description>Horizontal scalability demo</description>"
				 		+ "<status>DEPLOYED</status>"
				 		+ "<link rel=\"experiment\" href=\"https://api.bonfire-project.eu:444/ex\"/>"
				 		+ "<link rel=\"log\" href=\"/managed_experiments/247/log\"/>"
				 + "</managed_experiment>";

		jaxbContext = JAXBContext.newInstance(ManagedExperiment.class);
		jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		me = (ManagedExperiment) jaxbUnmarshaller.unmarshal(new StringReader(managedExperimentString));

		assertEquals(-1, me.getBonFIREExperimentId());
		
		managedExperimentString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				 + "<managed_experiment href=\"/managed_experiments/247\">"
				 		+ "<name>ReviewAmazonExperiment</name>"
				 		+ "<description>Horizontal scalability demo</description>"
				 		+ "<status>DEPLOYED</status>"
				 		+ "<link rel=\"experiment\" href=\"https://api.bonfire-project.eu:444/experiments/aaaa\"/>"
				 		+ "<link rel=\"log\" href=\"/managed_experiments/247/log\"/>"
				 + "</managed_experiment>";

		jaxbContext = JAXBContext.newInstance(ManagedExperiment.class);
		jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		me = (ManagedExperiment) jaxbUnmarshaller.unmarshal(new StringReader(managedExperimentString));

		assertEquals(-1, me.getBonFIREExperimentId());
		
		managedExperimentString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				 + "<managed_experiment href=\"/managed_experiments/247\">"
				 		+ "<name>ReviewAmazonExperiment</name>"
				 		+ "<description>Horizontal scalability demo</description>"
				 		+ "<status>xxxx</status>"
				 		+ "<link rel=\"experiment\" href=\"https://api.bonfire-project.eu:444/experiments/21272\"/>"
				 		+ "<link rel=\"log\" href=\"/managed_experiments/247/log\"/>"
				 + "</managed_experiment>";

		jaxbContext = JAXBContext.newInstance(ManagedExperiment.class);
		jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		me = (ManagedExperiment) jaxbUnmarshaller.unmarshal(new StringReader(managedExperimentString));
		
		assertEquals(-1, me.getBonFIREExperimentId());
	}
}
