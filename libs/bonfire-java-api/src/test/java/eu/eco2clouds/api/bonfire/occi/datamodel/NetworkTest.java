package eu.eco2clouds.api.bonfire.occi.datamodel;

import static eu.eco2clouds.api.bonfire.occi.Dictionary.NAMESPACE;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
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
public class NetworkTest {

	@Test
	public void testPojo() {
		Network network = new Network();
		network.setHref("adfas");
		network.setName("BonFIRE WAN");
		
		assertEquals("adfas", network.getHref());
		assertEquals("BonFIRE WAN", network.getName());
		
		Network network1 = new Network();
		network1.setName("pepe");
		assertEquals("pepe", network1.getNetworkName());
		
		Network network2 = new Network();
		network2.setNetworkName("pepe");
		assertEquals("pepe", network2.getName());
	}
	
	@Test
	public void xmlToObject() throws Exception {
		String xmlNetwork = "<network xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"/locations/fr-inria/networks/1\">"
								+ "<id>1</id>"
								+ "<name>BonFIRE WAN</name>"
								+ "<uname href=\"/locations/fr-inria/users/0\">admin</uname>"
								+ "<groups>users</groups>"
								+ "<public>NO</public>"
								+ "<vlan />"
							+ "</network>";
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Network.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Network network = (Network) jaxbUnmarshaller.unmarshal(new StringReader(xmlNetwork));
		
		assertEquals("1", network.getId());
		assertEquals("BonFIRE WAN", network.getNetworkName());
		assertEquals("users", network.getGroups());
		assertEquals("/locations/fr-inria/users/0", network.getUname().getHref());
		assertEquals("admin", network.getUname().getValue());
		assertEquals("NO", network.getPublicNetwork());
	}
	
	@Test
	public void objectToXml() throws Exception {
		Network network = new Network();
		network.setName("pepe");
		network.setGroups("pepito");
		network.setHref("/asdasd/asdadf");
		network.setId("11");
		network.setNetworkName("pepe2");
		network.setPublicNetwork("YES");
		UserId user = new UserId();
		user.setHref("adsfasdf");
		user.setValue("dperez");
		network.setUname(user);
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Network.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(network, out);
		String output = out.toString();
		System.out.println(output);
		
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(output));
		XPath xpath = XPath.newInstance("//bnf:network");
		xpath.addNamespace("bnf", NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element storageElement = (Element) listxpath.get(0);
		assertEquals("/asdasd/asdadf", storageElement.getAttributeValue("href"));
		assertEquals("pepe2", storageElement.getAttributeValue("name"));
		
		XPath xpathName = XPath.newInstance("//bnf:name");
		xpathName.addNamespace("bnf", NAMESPACE);
		List listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		Element name = (Element) listxpathName.get(0);
		assertEquals("pepe2", name.getValue());
		
		xpathName = XPath.newInstance("//bnf:groups");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element element = (Element) listxpath.get(0);
		assertEquals("pepito", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:id");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("11", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:public");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("YES", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:uname");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("dperez", element.getValue());
		assertEquals("adsfasdf", element.getAttributeValue("href"));
	}
}
