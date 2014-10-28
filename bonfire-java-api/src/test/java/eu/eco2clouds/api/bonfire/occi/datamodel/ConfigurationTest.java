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
public class ConfigurationTest {
	
	@Test
	public void pojoTest() {
		Configuration configuration = new Configuration();
		configuration.setHref("href");
		configuration.setName("name");
		configuration.setCpu(0.2d);
		configuration.setMemory(245l);
		ArrayList<Link> links = new ArrayList<Link>();
		configuration.setLinks(links);
		
		assertEquals("href", configuration.getHref());
		assertEquals(0.2d, configuration.getCpu(), 0.00001);
		assertEquals(245l, configuration.getMemory());
		assertEquals(links, configuration.getLinks());
		assertEquals("name", configuration.getName());
	}

	@Test
	public void xmlToObject() throws Exception {
		String xmlConfiguration = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
								  + "<configuration href=\"/locations/fr-inria/configurations/medium\" xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\">"
								  		+ "<name>medium</name>"
										+ "<cpu>2</cpu>"
										+ "<memory>2048</memory>"
										+ "<link rel=\"parent\" href=\"/locations/fr-inria\" type=\"application/vnd.bonfire+xml\"/>" 
										+ "<link rel=\"self\" href=\"/locations/fr-inria/configurations/medium\" type=\"application/vnd.bonfire+xml\"/>"
								   + "</configuration>";
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Configuration.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Configuration configuration = (Configuration) jaxbUnmarshaller.unmarshal(new StringReader(xmlConfiguration));
		
		assertEquals("/locations/fr-inria/configurations/medium", configuration.getHref());
		assertEquals("medium", configuration.getName());
		assertEquals(2d, configuration.getCpu(), 0.00001);
		assertEquals(2048l, configuration.getMemory());
		assertEquals(2, configuration.getLinks().size());
		Link link01 = configuration.getLinks().get(0);
		assertEquals("parent", link01.getRel());
		assertEquals("/locations/fr-inria", link01.getHref());
		assertEquals("application/vnd.bonfire+xml", link01.getType());
		Link link02 = configuration.getLinks().get(1);
		assertEquals("self", link02.getRel());
		assertEquals("/locations/fr-inria/configurations/medium", link02.getHref());
		assertEquals("application/vnd.bonfire+xml", link02.getType());
	}
	
	@Test
	public void ObjectToXml() throws Exception {
		Configuration configuration = new Configuration();
		configuration.setHref("href");
		configuration.setName("name");
		configuration.setCpu(0.2d);
		configuration.setMemory(245l);
		ArrayList<Link> links = new ArrayList<Link>();
		Link link = new Link();
		link.setRel("parent");
		link.setHref("href...");
		link.setType("asdfasd");
		links.add(link);
		configuration.setLinks(links);
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Configuration.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(configuration, out);
		String output = out.toString();
		System.out.println(output);
		
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(output));
		XPath xpath = XPath.newInstance("//bnf:configuration");
		xpath.addNamespace("bnf", NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element storageElement = (Element) listxpath.get(0);
		assertEquals("href", storageElement.getAttributeValue("href"));
		
		XPath xpathName = XPath.newInstance("//bnf:name");
		xpathName.addNamespace("bnf", NAMESPACE);
		List listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		Element name = (Element) listxpathName.get(0);
		assertEquals("name", name.getValue());
		
		xpathName = XPath.newInstance("//bnf:cpu");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element element = (Element) listxpath.get(0);
		assertEquals("0.2", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:memory");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("245", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:link");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("parent", element.getAttributeValue("rel"));
		assertEquals("href...", element.getAttributeValue("href"));
		assertEquals("asdfasd", element.getAttributeValue("type"));
	}
}

