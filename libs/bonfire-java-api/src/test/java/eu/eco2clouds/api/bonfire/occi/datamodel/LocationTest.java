package eu.eco2clouds.api.bonfire.occi.datamodel;

import static org.junit.Assert.assertEquals;
import static eu.eco2clouds.api.bonfire.occi.Dictionary.NAMESPACE;

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
 *
 * Test that verifies that the Location POJO object behaves as expected
 * 
 */
public class LocationTest {
	
	@Test
	public void gettersAndSettersTest() {
		Location location = new Location();
		location.setName("location name");
		location.setUrl("https://url.com");
		location.setHref("/locations/location_name");
		
		assertEquals("location name", location.getName());
		assertEquals("https://url.com", location.getUrl());
		assertEquals("/locations/location_name", location.getHref());
	}
	
	@Test
	public void xmlToObject() throws Exception {
		
		String locationXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
							 + "<location href=\"/locations/fr-inria\" xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\">"
							 	+ "<name>fr-inria</name>"
							 	+ "<url>https://frontend.bonfire.grid5000.fr:443</url>"
							 	+ "<link rel=\"parent\" href=\"/\" type=\"application/vnd.bonfire+xml\"/>"
							 	+ "<link rel=\"computes\" href=\"/locations/fr-inria/computes\" type=\"application/vnd.bonfire+xml\"/>"
							 	+ "<link rel=\"networks\" href=\"/locations/fr-inria/networks\" type=\"application/vnd.bonfire+xml\"/>"
							 	+ "<link rel=\"storages\" href=\"/locations/fr-inria/storages\" type=\"application/vnd.bonfire+xml\"/>"
							 	+ "<link rel=\"routers\" href=\"/locations/fr-inria/routers\" type=\"application/vnd.bonfire+xml\"/>"
							 	+ "<link rel=\"site_links\" href=\"/locations/fr-inria/site_links\" type=\"application/vnd.bonfire+xml\"/>"
							 	+ "<link rel=\"configurations\" href=\"/locations/fr-inria/configurations\" type=\"application/vnd.bonfire+xml\"/>"
							 	+ "<link rel=\"services\" href=\"/locations/fr-inria/services\" type=\"application/vnd.bonfire+xml\"/>"
							 	+ "<link rel=\"account\" href=\"/locations/fr-inria/account\" type=\"application/vnd.bonfire+xml\"/>"
							 + "</location>";
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Location.class);
		
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		
		Location location = (Location) jaxbUnmarshaller.unmarshal(new StringReader(locationXML));
		
		assertEquals("/locations/fr-inria", location.getHref());
		assertEquals("fr-inria", location.getName());
		assertEquals("https://frontend.bonfire.grid5000.fr:443", location.getUrl());
		assertEquals(9, location.getLinks().size());
		
		Link link0 = location.getLinks().get(0);
		assertEquals("parent", link0.getRel());
		assertEquals("/", link0.getHref());
		assertEquals("application/vnd.bonfire+xml", link0.getType());
		
		Link link1 = location.getLinks().get(1);
		assertEquals("computes", link1.getRel());
		assertEquals("/locations/fr-inria/computes", link1.getHref());
		assertEquals("application/vnd.bonfire+xml", link1.getType());
		
		Link link2 = location.getLinks().get(2);
		assertEquals("networks", link2.getRel());
		assertEquals("/locations/fr-inria/networks", link2.getHref());
		assertEquals("application/vnd.bonfire+xml", link2.getType());
		
		Link link3 = location.getLinks().get(3);
		assertEquals("storages", link3.getRel());
		assertEquals("/locations/fr-inria/storages", link3.getHref());
		assertEquals("application/vnd.bonfire+xml", link3.getType());
		
		Link link4 = location.getLinks().get(4);
		assertEquals("routers", link4.getRel());
		assertEquals("/locations/fr-inria/routers", link4.getHref());
		assertEquals("application/vnd.bonfire+xml", link4.getType());
		
		Link link5 = location.getLinks().get(5);
		assertEquals("site_links", link5.getRel());
		assertEquals("/locations/fr-inria/site_links", link5.getHref());
		assertEquals("application/vnd.bonfire+xml", link5.getType());
		
		Link link6 = location.getLinks().get(6);
		assertEquals("configurations", link6.getRel());
		assertEquals("/locations/fr-inria/configurations", link6.getHref());
		assertEquals("application/vnd.bonfire+xml", link6.getType());
		
		Link link7 = location.getLinks().get(7);
		assertEquals("services", link7.getRel());
		assertEquals("/locations/fr-inria/services", link7.getHref());
		assertEquals("application/vnd.bonfire+xml", link7.getType());
		
		Link link8 = location.getLinks().get(8);
		assertEquals("account", link8.getRel());
		assertEquals("/locations/fr-inria/account", link8.getHref());
		assertEquals("application/vnd.bonfire+xml", link8.getType());
	}
	
	@Test
	public void objectToXML() throws Exception {
		Location location = new Location();
		location.setHref("locations/my-location");
		location.setName("my-location");
		location.setUrl("https;//api.bonfire-project.eu/locations/my-location");
		
		Link link = new Link();
		link.setHref("/");
		link.setRel("parent");
		link.setType("application/vnd.bonfire+xml");
		
		ArrayList<Link> links = new ArrayList<Link>();
		links.add(link);
		location.setLinks(links);
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Location.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(location, out);
		String output = out.toString();
		
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(output));
		XPath xpath = XPath.newInstance("//bnf:location");
		xpath.addNamespace("bnf", NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element locationElement = (Element) listxpath.get(0);
		assertEquals("locations/my-location", locationElement.getAttributeValue("href"));
		
		XPath xpathName = XPath.newInstance("//bnf:name");
		xpathName.addNamespace("bnf", NAMESPACE);
		List listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		Element name = (Element) listxpathName.get(0);
		assertEquals("my-location", name.getValue());
		
		XPath xpathURL = XPath.newInstance("//bnf:url");
		xpathURL.addNamespace("bnf", NAMESPACE);
		List listxpathURL = xpathURL.selectNodes(xmldoc);
		assertEquals(1, listxpathURL.size());
		Element url = (Element) listxpathURL.get(0);
		assertEquals("https;//api.bonfire-project.eu/locations/my-location", url.getValue());
		
		XPath xpathLink = XPath.newInstance("//bnf:link");
		xpathLink.addNamespace("bnf", NAMESPACE);
		List listxpathLink = xpathLink.selectNodes(xmldoc);
		assertEquals(1, listxpathLink.size());
		Element linkElement = (Element) listxpathLink.get(0);
		assertEquals("/", linkElement.getAttributeValue("href"));
		assertEquals("parent", linkElement.getAttributeValue("rel"));
		assertEquals("application/vnd.bonfire+xml", linkElement.getAttributeValue("type"));

		assertEquals(true, true);
	}
}
