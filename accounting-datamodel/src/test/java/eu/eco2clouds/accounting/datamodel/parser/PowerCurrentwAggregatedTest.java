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

import eu.eco2clouds.accounting.datamodel.parser.Co2;

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
public class PowerCurrentwAggregatedTest {

	@Test 
	public void pojo() {
		PowerCurrentwAggregated power = new PowerCurrentwAggregated();
		power.setClock(10l);
		power.setName("name");
		power.setUnity("units");
		power.setValue(1.0d);
		
		assertEquals(10l, power.getClock().longValue());
		assertEquals(1.0d, power.getValue().doubleValue(), 0.0001);
		assertEquals("name", power.getName());
		assertEquals("units", power.getUnity());
	}
	
	@Test
	public void testXML() throws Exception {
		String co2XML = "<power_current_w_aggregated xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\">"
							+ "<value>94.0</value>"
							+ "<clock>1381512236</clock>"
							+ "<unity>g\\/kWh</unity>"
							+ "<name>Co2 producted per kWh</name>"
						+ "</power_current_w_aggregated>";
		
		JAXBContext jaxbContext = JAXBContext.newInstance(PowerCurrentwAggregated.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		PowerCurrentwAggregated co2 = (PowerCurrentwAggregated) jaxbUnmarshaller.unmarshal(new StringReader(co2XML));
		
		assertEquals(1381512236l, co2.getClock().longValue());
		assertEquals(94.0d, co2.getValue().doubleValue(), 0.0001);
		assertEquals("Co2 producted per kWh", co2.getName());
		assertEquals("g\\/kWh", co2.getUnity());
	}
	
	@Test
	public void objectToXml() throws Exception {
		PowerCurrentwAggregated co2 = new PowerCurrentwAggregated();
		co2.setClock(10l);
		co2.setName("name");
		co2.setUnity("units");
		co2.setValue(1.0d);
		
		JAXBContext jaxbContext = JAXBContext.newInstance(PowerCurrentwAggregated.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(co2, out);
		String output = out.toString();
		System.out.println(output);
			
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(output));
		XPath xpath = XPath.newInstance("//bnf:power_current_w_aggregated");
		xpath.addNamespace("bnf", E2C_NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		
		
		XPath xpathName = XPath.newInstance("//bnf:value");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		List listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		Element element = (Element) listxpathName.get(0);
		assertEquals("1.0", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:clock");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("10", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:unity");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("units", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:name");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("name", element.getValue());
	}
}
