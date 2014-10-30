package eu.eco2clouds.scheduler.em.datamodel;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

public class CollectionTest {

	@Test
	public void pojo() {
		Collection collection = new Collection();
		Items items = new Items();
		collection.setItems(items);
		
		assertEquals(items, collection.getItems());
	}
	
	@Test
	public void parseCollection() throws Exception {
		String xmlCollection = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
								+ "<collection>"
									+ "<items>"
										+ "<managed_experiment href=\"/managed_experiments/247\">"
											+ "<name>ReviewAmazonExperiment</name>"
											+ "<description>Horizontal scalability demo</description>"
											+ "<status>DEPLOYED</status>"
											+ "<link rel=\"experiment\" href=\"https://api.bonfire-project.eu/experiments/21272\"/>"
											+ "<link rel=\"log\" href=\"/managed_experiments/247/log\"/>"
										+ "</managed_experiment>"
										+ "<managed_experiment href=\"/managed_experiments/250\">"
											+ "<name>ReviewAmazonExperiment</name>"
											+ "<description>Horizontal scalability demo</description>"
											+ "<status>DEPLOYED</status>"
											+ "<link rel=\"experiment\" href=\"https://api.bonfire-project.eu/experiments/21277\"/>"
											+ "<link rel=\"log\" href=\"/managed_experiments/250/log\"/>"
										+ "</managed_experiment>"
									+ "</items>"
								+ "</collection>";
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Collection collection = (Collection) jaxbUnmarshaller.unmarshal(new StringReader(xmlCollection));
		
		assertEquals(2, collection.getItems().getManagedExperiments().size());
		assertEquals("/managed_experiments/247", collection.getItems().getManagedExperiments().get(0).getHref());
		assertEquals("/managed_experiments/250", collection.getItems().getManagedExperiments().get(1).getHref());
	}
}
