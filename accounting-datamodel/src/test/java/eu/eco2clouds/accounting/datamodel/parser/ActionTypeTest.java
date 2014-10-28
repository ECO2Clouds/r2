package eu.eco2clouds.accounting.datamodel.parser;

import static eu.eco2clouds.accounting.Dictionary.E2C_NAMESPACE;
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

import eu.eco2clouds.accounting.datamodel.parser.ActionType;

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
public class ActionTypeTest {

	@Test
	public void pojo() {
		ActionType actionType = new ActionType();
		actionType.setId(1);
		actionType.setName("name");
		
		assertEquals("name", actionType.getName());
		assertEquals(1, actionType.getId().intValue());
		
		ActionType actionType2 = new ActionType(1, "name");
		assertEquals("name", actionType2.getName());
		assertEquals(1, actionType2.getId().intValue());
	}
	
	@Test
	public void XmlToObject() throws Exception {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					 + "<action_type xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\">"
					 	+ "<name>name action type</name>"
					 + "</action_type>";
		
		JAXBContext jaxbContext = JAXBContext.newInstance(ActionType.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		ActionType actionType = (ActionType) jaxbUnmarshaller.unmarshal(new StringReader(xml));
		
		assertEquals("name action type", actionType.getName());
		assertEquals(null, actionType.getId());
		
		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			  + "<action_type xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\">"
			  		+ "<id>222</id>"
			   		+ "<name>name action type</name>"
			  + "</action_type>";
		
		actionType = (ActionType) jaxbUnmarshaller.unmarshal(new StringReader(xml));
		
		assertEquals("name action type", actionType.getName());
		assertEquals(222, actionType.getId().intValue());
	}
	
	@Test
	public void objectToXml() throws Exception {
		ActionType actionType = new ActionType(333, "name");
		
		JAXBContext jaxbContext = JAXBContext.newInstance(ActionType.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(actionType, out);
		String output = out.toString();
		System.out.println(output);
			
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(output));
		XPath xpath = XPath.newInstance("//bnf:action_type");
		xpath.addNamespace("bnf", E2C_NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		
		XPath xpathName = XPath.newInstance("//bnf:id");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		List listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		Element element = (Element) listxpathName.get(0);
		assertEquals("333", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:name");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("name", element.getValue());
		
		ActionType actionType2 = new ActionType();
		actionType2.setName("name 2");
		
		ByteArrayOutputStream out2 = new ByteArrayOutputStream();
		marshaller.marshal(actionType2, out2);
		output = out2.toString();
		System.out.println(output);
		
		xmldoc = builder.build(new StringReader(output));
		xpath = XPath.newInstance("//bnf:action_type");
		xpath.addNamespace("bnf", E2C_NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		
		xpathName = XPath.newInstance("//bnf:id");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(0, listxpathName.size());
		
		xpathName = XPath.newInstance("//bnf:name");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("name 2", element.getValue());
	}
}
