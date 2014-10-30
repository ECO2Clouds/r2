package eu.eco2clouds.api.bonfire.occi.datamodel;

import static eu.eco2clouds.api.bonfire.occi.Dictionary.NAMESPACE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
public class StorageTest {

	@Test
	public void testPojo() {
		Storage storage = new Storage();
		storage.setName("name");
		storage.setHref("/href/xxx");
		storage.setFstype("ext3");
		storage.setGroups("atos");
		storage.setId("1");
		ArrayList<Link> links = new ArrayList<Link>();
		storage.setLinks(links);
		storage.setPersistent("NO");
		storage.setPublicStorage("NO");
		storage.setSize("10");
		storage.setState("PENDING");
		storage.setType("DATABLOCK");
		
		assertEquals("name", storage.getName());
		assertEquals("/href/xxx", storage.getHref());
		assertEquals("ext3", storage.getFstype());
		assertEquals("atos", storage.getGroups());
		assertEquals("1", storage.getId());
		assertEquals(links, storage.getLinks());
		assertEquals("NO", storage.getPersistent());
		assertEquals("NO", storage.getPublicStorage());
		assertEquals("10", storage.getSize());
		assertEquals("PENDING", storage.getState());
		assertEquals("DATABLOCK", storage.getType());
		
		Storage newStorage = new Storage();
	    Link link = new Link();
	    
	    assertNull(newStorage.getLinks());
	    newStorage.addLink(link);
	    assertEquals(1, newStorage.getLinks().size());
	    assertEquals(link, newStorage.getLinks().get(0));
	    
	    Storage storage1 = new Storage();
	    storage1.setName("pepe");
	    assertEquals("pepe", storage1.getStorageName());
	    
	    Storage storage2 = new Storage();
	    storage2.setStorageName("pepe");
	    assertEquals("pepe", storage2.getName());
	}
	
	@Test
	public void xmlToObject() throws Exception {
		String storageXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
							+ "<storage xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"/locations/pl-psnc/storages/387\">"
								+ "<id>387</id>"
								+ "<name>asdfas</name>"
								+ "<user_id href=\"/locations/pl-psnc/users/6\">dperez</user_id>"
								+ "<groups>atos</groups>"
								+ "<state>READY</state>"
								+ "<type>DATABLOCK</type>"
								+ "<description>asdfasdf</description>"
								+ "<size>10</size>"
								+ "<fstype>ext3</fstype>"
								+ "<public>NO</public>"
								+ "<persistent>NO</persistent>"
								+ "<link rel=\"experiment\" href=\"/experiments/32860\" type=\"application/vnd.bonfire+xml\"/>" 
							+"</storage>";
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Storage.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Storage storage = (Storage) jaxbUnmarshaller.unmarshal(new StringReader(storageXML));
		
		assertEquals("/locations/pl-psnc/storages/387", storage.getHref());
		assertEquals("387", storage.getId());
		assertEquals("asdfas", storage.getStorageName());
		assertEquals("atos", storage.getGroups());
		assertEquals("READY", storage.getState());
		assertEquals("DATABLOCK", storage.getType());
		assertEquals("asdfasdf", storage.getDescription());
		assertEquals("10", storage.getSize());
		assertEquals("ext3", storage.getFstype());
		assertEquals("NO", storage.getPublicStorage());
		assertEquals("NO", storage.getPersistent());
		ArrayList<Link> links = storage.getLinks();
		assertEquals("application/vnd.bonfire+xml", links.get(0).getType());
		assertEquals("/experiments/32860", links.get(0).getHref());
		assertEquals("experiment", links.get(0).getRel());
		assertEquals("/locations/pl-psnc/users/6", storage.getUserId().getHref());
		assertEquals("dperez", storage.getUserId().getValue());
	}
	
	@Test
	public void ObjectToXML() throws Exception {
		Storage storage = new Storage();
		storage.setDescription("description");
		storage.setFstype("ext3");
		storage.setGroups("atos");
		storage.setHref("http://api.bonfire-project.eu");
		storage.setId("1");
		Link link = new Link();
		link.setHref("/");
		link.setRel("parent");
		ArrayList<Link> links = new ArrayList<Link>();
		links.add(link);
		storage.setLinks(links);
		storage.setName("name");
		storage.setPersistent("NO");
		storage.setPublicStorage("No");
		storage.setSize("20");
		storage.setState("READY");
		storage.setType("DATABLOCK");
		UserId userId = new UserId();
		userId.setHref("http://...");
		userId.setValue("pepe");
		storage.setUserId(userId);
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Storage.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(storage, out);
		String output = out.toString();
		
		System.out.println(output);
		
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(output));
		XPath xpath = XPath.newInstance("//bnf:storage");
		xpath.addNamespace("bnf", NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element storageElement = (Element) listxpath.get(0);
		assertEquals("http://api.bonfire-project.eu", storageElement.getAttributeValue("href"));
		assertEquals("name", storageElement.getAttributeValue("name"));
		
		XPath xpathName = XPath.newInstance("//bnf:name");
		xpathName.addNamespace("bnf", NAMESPACE);
		List listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		Element name = (Element) listxpathName.get(0);
		assertEquals("name", name.getValue());
		
		xpathName = XPath.newInstance("//bnf:description");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element element = (Element) listxpath.get(0);
		assertEquals("description", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:groups");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("atos", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:fstype");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("ext3", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:id");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("1", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:persistent");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("NO", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:public");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("No", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:size");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("20", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:state");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("READY", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:type");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("DATABLOCK", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:type");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("DATABLOCK", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:user_id");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("pepe", element.getValue());
		assertEquals("http://...", element.getAttributeValue("href"));
		
		xpathName = XPath.newInstance("//bnf:link");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("parent", element.getAttributeValue("rel"));
		assertEquals("/", element.getAttributeValue("href"));
	}
}
