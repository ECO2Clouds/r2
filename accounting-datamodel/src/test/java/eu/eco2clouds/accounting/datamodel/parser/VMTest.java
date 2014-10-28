package eu.eco2clouds.accounting.datamodel.parser;

import static eu.eco2clouds.accounting.Dictionary.E2C_NAMESPACE;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.junit.Test;

import eu.eco2clouds.accounting.datamodel.parser.Action;
import eu.eco2clouds.accounting.datamodel.parser.Link;
import eu.eco2clouds.accounting.datamodel.parser.VM;
import eu.eco2clouds.accounting.datamodel.parser.VMHost;

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
public class VMTest {

	@Test
	public void pojo() {
		
		VM vm = new VM();
		vm.setHref("href");
		vm.setBonfireUrl("https:/...");
		vm.setId(222);
		
		String host = "hostnameA";
		
		vm.setHost(host);
		
		Set<Action> actions = new HashSet<Action>();
		vm.setActions(actions);
		Set<VMHost> vMHosts = new HashSet<VMHost>();
		vm.setvMhosts(vMHosts);
		List<Link> links = new ArrayList<Link>();
		vm.setLinks(links);

		assertEquals("href", vm.getHref());
		assertEquals(222, vm.getId().intValue());
		assertEquals("https:/...", vm.getBonfireUrl());
		assertEquals("hostnameA", vm.getHost());
		assertEquals(vMHosts, vm.getvMhosts());
		assertEquals(actions, vm.getActions());
		
		VM vm2 = new VM(333,"https:/vm2");
		assertEquals(333, vm2.getId().intValue());
		assertEquals("https:/vm2", vm2.getBonfireUrl());
		assertEquals(links, vm.getLinks());
	}
	
	@Test
	public void addNics() {
		VM vm = new VM();
		assertEquals(null, vm.getNics());
		
		vm.addNic("ip", "mac", "network");
		Nic nic = vm.getNics().get(0);
		assertEquals("ip", nic.getIp());
		assertEquals("mac", nic.getMac());
		assertEquals("network", nic.getNetwork());
	}
	
	@Test
	public void addLinks() {	
		VM vm = new VM();
		
		vm.addLink("aa", "bb", "cc");
		
		assertEquals(1, vm.getLinks().size());
		assertEquals("aa", vm.getLinks().get(0).getRel());
		assertEquals("bb", vm.getLinks().get(0).getHref());
		assertEquals("cc", vm.getLinks().get(0).getType());
		
		vm.addLink("dd", "ee", "ff");
		
		assertEquals("dd", vm.getLinks().get(1).getRel());
		assertEquals("ee", vm.getLinks().get(1).getHref());
		assertEquals("ff", vm.getLinks().get(1).getType());

	}
	
	@Test
	public void fromXmlToObject() throws Exception {
		String vMXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
					   + "<vm xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/experiments/222/vms/111\">"
					   	+ "<id>2222</id>"
					    + "<host>hostname</host>"
					    + "<bonfire-url>http://something...</bonfire-url>"
					    + "<link rel=\"experiment\" href=\"/experiments/1\"/>"  
					   + "</vm>";
		
		JAXBContext jaxbContext = JAXBContext.newInstance(VM.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		VM vm = (VM) jaxbUnmarshaller.unmarshal(new StringReader(vMXml));
		
		assertEquals("/experiments/222/vms/111", vm.getHref());
		assertEquals(2222, vm.getId().intValue());
		assertEquals("hostname", vm.getHost());
		assertEquals("http://something...", vm.getBonfireUrl());
		assertEquals(1, vm.getLinks().size());
	}
	
	@Test
	public void fromObjectToXml() throws Exception {
		VM vm = new VM();
		vm.setBonfireUrl("http://something...");
		vm.setId(2222);
		vm.setHref("/experiments/222/vms/111");
		vm.setHost("hostname");
		
		List<Link> links = new ArrayList<Link>();
		Link link = new Link();
		link.setHref("/experiments/1");
		link.setRel("experiment");
		links.add(link);
		
		vm.setLinks(links);
		vm.addNic("10.10.10.10", "mac", "BonFIRE WAN");
		
		JAXBContext jaxbContext = JAXBContext.newInstance(VM.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(vm, out);
		String output = out.toString();
		System.out.println(output);
		
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(output));
		XPath xpath = XPath.newInstance("//bnf:vm");
		xpath.addNamespace("bnf", E2C_NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element element = (Element) listxpath.get(0);
		assertEquals("/experiments/222/vms/111", element.getAttributeValue("href"));
		
		XPath xpathName = XPath.newInstance("//bnf:id");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		List listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("2222", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:bonfire-url");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("http://something...", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:host");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("hostname", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:ip");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("10.10.10.10", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:link");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("experiment", element.getAttributeValue("rel"));
		assertEquals("/experiments/1", element.getAttributeValue("href"));
	}
}