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

import eu.eco2clouds.accounting.datamodel.parser.Oil;

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
public class SystemCpuUtilTest {

	@Test 
	public void pojo() {
		SystemCpuUtil systemCPULoad = new SystemCpuUtil();
		systemCPULoad.setClock(10l);
		systemCPULoad.setName("name");
		systemCPULoad.setUnity("units");
		systemCPULoad.setValue(1.0d);
		
		assertEquals(10l, systemCPULoad.getClock().longValue());
		assertEquals(1.0d, systemCPULoad.getValue().doubleValue(), 0.0001);
		assertEquals("name", systemCPULoad.getName());
		assertEquals("units", systemCPULoad.getUnity());
	}
	
	@Test
	public void testXML() throws Exception {
		String oilXML = "<system_cpu_util xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\">"
							+ "<value>94.0</value>"
							+ "<clock>1381512236</clock>"
							+ "<unity>g\\/kWh</unity>"
							+ "<name>Co2 producted per kWh</name>"
						+ "</system_cpu_util>";
		
		JAXBContext jaxbContext = JAXBContext.newInstance(SystemCpuUtil.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		SystemCpuUtil oil = (SystemCpuUtil) jaxbUnmarshaller.unmarshal(new StringReader(oilXML));
		
		assertEquals(1381512236l, oil.getClock().longValue());
		assertEquals(94.0d, oil.getValue().doubleValue(), 0.0001);
		assertEquals("Co2 producted per kWh", oil.getName());
		assertEquals("g\\/kWh", oil.getUnity());
	}
	
	@Test
	public void objectToXml() throws Exception {
		SystemCpuUtil oil = new SystemCpuUtil();
		oil.setClock(10l);
		oil.setName("name");
		oil.setUnity("units");
		oil.setValue(1.0d);
		
		JAXBContext jaxbContext = JAXBContext.newInstance(SystemCpuUtil.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(oil, out);
		String output = out.toString();
		System.out.println(output);
			
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(output));
		XPath xpath = XPath.newInstance("//bnf:system_cpu_util");
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
