package eu.eco2clouds.api.bonfire.occi.datamodel;

import static eu.eco2clouds.api.bonfire.occi.Dictionary.NAMESPACE;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.junit.Test;

/**
 * 
 * Copyright 2014 ATOS SPAIN S.A. 
 * 
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author: David Garcia Perez. Atos Research and Innovation, Atos SPAIN SA
 * e-mail david.garciaperez@atos.net
 */
public class CollectionTest {

	@Test
	public void pojoTest() {
		Collection collection = new Collection();
		collection.setHref("/href...");
		ArrayList<Link> links = new ArrayList<Link>();
		collection.setLinks(links);
		Items items = new Items();
		collection.setItems(items);
		
		assertEquals(links, collection.getLinks());
		assertEquals(items, collection.getItems());
		assertEquals("/href...", collection.getHref());
	}
	
	@Test
	public void xmlToObjecWithDifferentCommas() throws Exception {
		String xmlCollectionStorages = "<?xml version='1.0' encoding='UTF-8'?>"
				   + "<collection xmlns='http://api.bonfire-project.eu/doc/schemas/occi'>"
				   		+ "<items>"
				   			+ "<storage href=\"/locations/fr-inria/storages/190\" name=\"Enactor Integration Test Save As\">"
				   				+ "<name>Enactor Integration Test Save As</name>"
				   				+ "<user_id>dperez</user_id>"
				   				+ "<state>READY</state>"
				   				+ "<type>OS</type>"
				   				+ "<size>700</size>"
				   				+ "<persistent>NO</persistent>"
				   				+ "<link href=\"/locations/fr-inria\" rel=\"location\"/>"
				   				+ "<link rel=\"experiment\" type=\"application/vnd.bonfire+xml\"/>"
				   			+ "</storage>"
				   			+ "<storage href=\"/locations/fr-inria/storages/340\" name=\"testdone\">"
				   				+ "<name>testdone</name>"
				   				+ "<user_id>yliang</user_id>"
				   				+ "<state>READY</state>"
				   				+ "<type>DATABLOCK</type>"
				   				+ "<size>1024</size>"
				   				+ "<persistent>NO</persistent>"
				   				+ "<link href=\"/locations/fr-inria\" rel=\"location\"/>"
				   				+ "<link rel=\"experiment\" type=\"application/vnd.bonfire+xml\"/>"
				   			+ "</storage>"
				   		+ "</items>"
				   	+ "</collection>";
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Collection collection = (Collection) jaxbUnmarshaller.unmarshal(new StringReader(xmlCollectionStorages));
		
		assertEquals(2, collection.getItems().getStorages().size());
	}
	
	@Test
	public void xmlToObject() throws Exception {
		String xmlCollection = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
								+ "<collection xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"testa\">"
									+ "<items offset=\"0\" total=\"4\">"
										+ "<configuration href=\"/locations/fr-inria/configurations/custom\">"
											+ "<name>custom</name>"
											+ "<cpu/>"
											+ "<memory/>"
										+ "</configuration>"
										+ "<configuration href=\"/locations/fr-inria/configurations/lite\">"
											+ "<name>lite</name>"
											+ "<cpu>0.5</cpu>"
											+ "<memory>256</memory>"
										+ "</configuration>"
										+ "<configuration href=\"/locations/fr-inria/configurations/medium\">"
											+ "<name>medium</name>"
											+ "<cpu>2</cpu>"
											+ "<memory>2048</memory>"
										+ "</configuration>"
										+ "<configuration href=\"/locations/fr-inria/configurations/small\">"
											+ "<name>small</name>"
											+ "<cpu>1</cpu>"
											+ "<memory>1024</memory>"
										+ "</configuration>"
										+ "<storage href=\"/locations/fr-inria/storages/190\" name=\"Enactor Integration Test Save As\">"
											+ "<name>Enactor Integration Test Save As</name>"
											+ "<user_id>dperez</user_id>"
											+ "<state>READY</state>"
											+ "<type>OS</type>"
											+ "<size>700</size>"
											+ "<persistent>NO</persistent>"
											+ "<link href=\"/locations/fr-inria\" rel=\"location\"/>"
										+ "</storage>"
										+ "<storage href=\"/locations/fr-inria/storages/340\" name=\"testdone\">"
											+ "<name>testdone</name>"
											+ "<user_id>yliang</user_id>"
											+ "<state>READY</state>"
											+ "<type>DATABLOCK</type>"
											+ "<size>1024</size>"
											+ "<persistent>NO</persistent>"
											+ "<link href=\"/locations/fr-inria\" rel=\"location\"/>"
											+ "<link rel=\"experiment\" type=\"application/vnd.bonfire+xml\"/>"
										+ "</storage>"
										+ "<network href=\"/locations/fr-inria/networks/1\" name=\"BonFIRE WAN\">"
											  + "<name>BonFIRE WAN</name>"
											  + "<uname>admin</uname>"
											  + "<link href=\"/locations/fr-inria\" rel=\"location\"/>"
											  + "<link rel=\"experiment\" type=\"application/vnd.bonfire+xml\"/>"
										+ "</network>"
									+ "</items>"
									+ "<link rel=\"parent\" href=\"/locations/fr-inria\" type=\"application/vnd.bonfire+xml\"/>"
									+ "<link rel=\"self\" href=\"/locations/fr-inria/configurations\" type=\"application/vnd.bonfire+xml\"/>" 
									+ "<link rel=\"top\" href=\"/locations/fr-inria/configurations\" type=\"application/vnd.bonfire+xml\"/>" 
								+ "</collection>";

		JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Collection collection = (Collection) jaxbUnmarshaller.unmarshal(new StringReader(xmlCollection));
		
		assertEquals("testa", collection.getHref());
		assertEquals(3, collection.getLinks().size());
		assertEquals("parent", collection.getLinks().get(0).getRel());
		assertEquals("/locations/fr-inria", collection.getLinks().get(0).getHref());
		assertEquals("application/vnd.bonfire+xml", collection.getLinks().get(0).getType());
		assertEquals("self", collection.getLinks().get(1).getRel());
		assertEquals("/locations/fr-inria/configurations", collection.getLinks().get(1).getHref());
		assertEquals("application/vnd.bonfire+xml", collection.getLinks().get(1).getType());
		assertEquals("top", collection.getLinks().get(2).getRel());
		assertEquals("/locations/fr-inria/configurations", collection.getLinks().get(2).getHref());
		assertEquals("application/vnd.bonfire+xml", collection.getLinks().get(2).getType());
		assertEquals(4, collection.getItems().getConfigurations().size());
		assertEquals(0, collection.getItems().getOffset());
		assertEquals(4, collection.getItems().getTotal());
		assertEquals("/locations/fr-inria/configurations/custom", collection.getItems().getConfigurations().get(0).getHref());
		assertEquals("custom", collection.getItems().getConfigurations().get(0).getName());
		assertEquals("/locations/fr-inria/configurations/lite", collection.getItems().getConfigurations().get(1).getHref());
		assertEquals("lite", collection.getItems().getConfigurations().get(1).getName());
		assertEquals(0.5, collection.getItems().getConfigurations().get(1).getCpu(), 0.001);
		assertEquals(256, collection.getItems().getConfigurations().get(1).getMemory());
		assertEquals("/locations/fr-inria/configurations/medium", collection.getItems().getConfigurations().get(2).getHref());
		assertEquals("medium", collection.getItems().getConfigurations().get(2).getName());
		assertEquals(2, collection.getItems().getConfigurations().get(2).getCpu(), 0.001);
		assertEquals(2048, collection.getItems().getConfigurations().get(2).getMemory());
		assertEquals("/locations/fr-inria/configurations/small", collection.getItems().getConfigurations().get(3).getHref());
		assertEquals("small", collection.getItems().getConfigurations().get(3).getName());
		assertEquals(1, collection.getItems().getConfigurations().get(3).getCpu(), 0.001);
		assertEquals(1024, collection.getItems().getConfigurations().get(3).getMemory());
		assertEquals(2, collection.getItems().getStorages().size());
		assertEquals("Enactor Integration Test Save As", collection.getItems().getStorages().get(0).getStorageName());
		assertEquals("1024", collection.getItems().getStorages().get(1).getSize());
		assertEquals(1, collection.getItems().getNetworks().size());
		assertEquals("BonFIRE WAN", collection.getItems().getNetworks().get(0).getNetworkName());
	}
	
	@Test
	public void objectToXml() throws Exception {
		Collection collection = new Collection();
		ArrayList<Link> links = new ArrayList<Link>();
		Link link = new Link();
		link.setHref("/");
		link.setRel("parent");
		links.add(link);
		collection.setLinks(links);
		collection.setHref("href...");
		Items items = new Items();
		items.setOffset(0);
		items.setTotal(4);
		collection.setItems(items);
		
		Storage storage1 = new Storage();
		storage1.setStorageName("storage1");
		
		Storage storage2 = new Storage();
		storage2.setStorageName("storage2");
		
		ArrayList<Storage> storages = new ArrayList<Storage>();
		storages.add(storage1);
		storages.add(storage2);
		
		items.setStorages(storages);
		
		Compute compute = new Compute();
		compute.setGroups("pepe");
		ArrayList<Compute> computes = new ArrayList<Compute>();
		computes.add(compute);
		items.setComputes(computes);
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(collection, out);
		String output = out.toString();
		System.out.println(output);
		
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(output));
		XPath xpath = XPath.newInstance("//bnf:collection");
		xpath.addNamespace("bnf", NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element storageElement = (Element) listxpath.get(0);
		assertEquals("href...", storageElement.getAttributeValue("href"));
		
		XPath xpathName = XPath.newInstance("//bnf:items");
		xpathName.addNamespace("bnf", NAMESPACE);
		List listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		Element element = (Element) listxpathName.get(0);
		assertEquals("0", element.getAttributeValue("offset"));
		assertEquals("4", element.getAttributeValue("total"));
		
		xpathName = XPath.newInstance("//bnf:link");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("parent", element.getAttributeValue("rel"));
		assertEquals("/", element.getAttributeValue("href"));
		
		xpathName = XPath.newInstance("//bnf:storage//bnf:name");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(2, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("storage1", element.getValue());
		element = (Element) listxpath.get(1);
		assertEquals("storage2", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:compute//bnf:groups");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("pepe", element.getValue());
	}
}
