package eu.eco2clouds.accounting.datamodel.parser;

import static eu.eco2clouds.accounting.Dictionary.E2C_NAMESPACE;
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

import eu.eco2clouds.accounting.datamodel.parser.Collection;
import eu.eco2clouds.accounting.datamodel.parser.Items;
import eu.eco2clouds.accounting.datamodel.parser.Link;

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
	public void collectionOfExperiments() throws Exception {
		String testbedsXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<collection xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/testbeds\">"
				+ "<items offset=\"0\" total=\"2\">"
				+ "<experiment href=\"/experiments/101\">"
				+ "<id>101</id>"
				+ "<bonfire-experiment-id>101</bonfire-experiment-id>"
				+ "<bonfire-user-id>A100</bonfire-user-id>"
				+ "<bonfire-group-id>ATOS</bonfire-group-id>"
				+ "<managed-experiment-id>55</managed-experiment-id>"
				+ "<start-time>15000</start-time>"
				+ "<end-time>18000</end-time>"
				+ "<link rel=\"parent\" href=\"/experiments\"/>"
				+ "<link rel=\"application-profile\" href=\"/experiments/101/application-profile\"/>"
				+ "<link rel=\"submitted-experiment-descriptor\" href=\"/experiments/101/submitted-experimen-descriptor\"/>"
				+ "<link rel=\"vms\" href=\"/experiments/1/vms\"/>"
				+ "</experiment>"
				+ "<experiment href=\"/experiments/102\">"
				+ "<id>102</id>"
				+ "<bonfire-experiment-id>101</bonfire-experiment-id>"
				+ "<bonfire-user-id>A100</bonfire-user-id>"
				+ "<bonfire-group-id>ATOS</bonfire-group-id>"
				+ "<managed-experiment-id>55</managed-experiment-id>"
				+ "<start-time>15000</start-time>"
				+ "<end-time>18000</end-time>"
				+ "<link rel=\"parent\" href=\"/experiments\"/>"
				+ "<link rel=\"application-profile\" href=\"/experiments/101/application-profile\"/>"
				+ "<link rel=\"submitted-experiment-descriptor\" href=\"/experiments/101/submitted-experimen-descriptor\"/>"
				+ "<link rel=\"vms\" href=\"/experiments/1/vms\"/>"
				+ "</experiment>"
				+ "</items>"
				+ "<link href=\"/\" rel=\"parent\" type=\"application/vnd.eco2clouds+xml\"/>"
				+ "</collection>";

		JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Collection collection = (Collection) jaxbUnmarshaller
				.unmarshal(new StringReader(testbedsXML));

		assertEquals(2, collection.getItems().getExperiments().size());
		assertEquals(4, collection.getItems().getExperiments().get(0)
				.getLinks().size());
		assertEquals(102, collection.getItems().getExperiments().get(1).getId()
				.intValue());
	}

	@Test
	public void collectionOfHostMonitoring() throws Exception {
		String hostMonitoringXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<collection xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/testbeds\">"
				+ "<items offset=\"0\" total=\"2\">"
				+ "<host_monitoring href=\"/testbeds/fr-inria/hosts/crocket0/monitoring\">"
				+ "<co2_generation_rate>"
				+ "<value>1.0</value>"
				+ "<clock>10</clock>"
				+ "<unity>units</unity>"
				+ "<name>name</name>"
				+ "</co2_generation_rate>"
				+ "<link rel=\"parent\" href=\"/testbeds/fr-inria\"/>"
				+ "</host_monitoring>"
				+ "<host_monitoring href=\"/testbeds/fr-inria/hosts/crocket0/monitoring\">"
				+ "<processor_load>"
				+ "<value>1.0</value>"
				+ "<clock>11</clock>"
				+ "<unity>units</unity>"
				+ "<name>name</name>"
				+ "</processor_load>"
				+ "<link rel=\"parent\" href=\"/testbeds/fr-inria\"/>"
				+ "</host_monitoring>"
				+ "</items>"
				+ "<link href=\"/\" rel=\"parent\" type=\"application/vnd.eco2clouds+xml\"/>"
				+ "</collection>";

		JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Collection collection = (Collection) jaxbUnmarshaller
				.unmarshal(new StringReader(hostMonitoringXML));

		assertEquals(2, collection.getItems().getHostMonitorings().size());
		assertEquals(11l, collection.getItems().getHostMonitorings().get(1)
				.getProcessorLoad().getClock().longValue());
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
		items.setTotal(2);
		collection.setItems(items);

		JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(collection, out);
		String output = out.toString();

		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(output));
		XPath xpath = XPath.newInstance("//bnf:collection");
		xpath.addNamespace("bnf", E2C_NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element storageElement = (Element) listxpath.get(0);
		assertEquals("href...", storageElement.getAttributeValue("href"));

		XPath xpathName = XPath.newInstance("//bnf:items");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		List listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		Element element = (Element) listxpathName.get(0);
		assertEquals("0", element.getAttributeValue("offset"));
		assertEquals("2", element.getAttributeValue("total"));

		xpathName = XPath.newInstance("//bnf:link");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("parent", element.getAttributeValue("rel"));
		assertEquals("/", element.getAttributeValue("href"));
	}

	@Test
	public void xmlToObject() throws Exception {
		String testbedsXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<collection xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/testbeds\">"
				+ "<items offset=\"0\" total=\"2\">"
				+ "<testbed href=\"/testbeds/fr-inria\">"
				+ "<name>fr-inria</name>"
				+ "<url>http://frontend.bonfire.grid5000.fr/one-status.xml</url>"
				+ "<link rel=\"parent\" href=\"/testbeds\" type=\"application/vnd.eco2clouds+xml\"/>"
				+ "<link rel=\"status\" href=\"/testbed/fr-inria/status\" type=\"application/eco2clouds+xml\"/>"
				+ "</testbed>"
				+ "<testbed href=\"/testbeds/uk-epcc\">"
				+ "<name>uk-epcc</name>"
				+ "<url>http://bonfire.epcc.ed.ac.uk/logs/one-status.xml</url>"
				+ "<link rel=\"parent\" href=\"/testbeds\" type=\"application/vnd.eco2clouds+xml\"/>"
				+ "<link rel=\"status\" href=\"/testbed/uk-epcc/status\" type=\"application/eco2clouds+xml\"/>"
				+ "</testbed>"
				+ "</items>"
				+ "<link href=\"/\" rel=\"parent\" type=\"application/vnd.eco2clouds+xml\"/>"
				+ "</collection>";

		JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Collection collection = (Collection) jaxbUnmarshaller
				.unmarshal(new StringReader(testbedsXML));

		// assertEquals(2, collection.getItems().getTestbeds().size());
	}

}
