package eu.eco2clouds.accounting.datamodel.xml;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import org.junit.Before;
import org.junit.Test;

import eu.eco2clouds.accounting.datamodel.xml.HostXml;
import eu.eco2clouds.accounting.datamodel.xml.HostPool;
import eu.eco2clouds.accounting.datamodel.xml.HostShare;
import eu.eco2clouds.accounting.util.ReadFile;

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
public class HostPoolTest {
	private String hostInfo = "";
	
	@Before
	public void before() throws IOException {
		hostInfo = ReadFile.readFile("src/test/resources/host.example");
	}
	
	@Test
	public void pojoTest() {
		HostPool hostPool = new HostPool();
		List<HostXml> hosts = new ArrayList<HostXml>();
		hostPool.setHosts(hosts);
		
		assertEquals(hosts, hostPool.getHosts());
	}
	
	@Test
	public void xmlToObject() throws Exception {
		JAXBContext jaxbContext = JAXBContext.newInstance(HostPool.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		HostPool hostPool = (HostPool) jaxbUnmarshaller.unmarshal(new StringReader(hostInfo));
		
		assertEquals(4, hostPool.getHosts().size());
		HostXml host1 = hostPool.getHosts().get(0);
		assertEquals("bonfire-blade-4", host1.getName());
		assertEquals(24379392, host1.getHostShare().getMemUsage());
		HostXml host3 = hostPool.getHosts().get(3);
		assertEquals("bonfire-blade-1", host3.getName());
		assertEquals(16, host3.getHostShare().getRunningVms());
	}
	
	@Test
	public void objectToXml() throws Exception {
		HostXml host1 = new HostXml();
		host1.setId(448);
		host1.setName("bonfire-blade-4");
		host1.setState(2);
		host1.setImMad("im_xen");
		host1.setVmMad("vmm_xen");
		host1.setVnMad("dummy");
		host1.setLastMonTime(1372835028);
		host1.setClusterId(-1);
		
		HostShare hostShare1 = new HostShare();
		host1.setHostShare(hostShare1);
		hostShare1.setDiskUsage(0);
		hostShare1.setMemUsage(21233664);
		hostShare1.setCpuUsage(2050);
		hostShare1.setMaxDisk(0);
		hostShare1.setMaxMem(66778144);
		hostShare1.setMaxCpu(2400);
		hostShare1.setFreeDisk(0);
		hostShare1.setFreeMem(44122112);
		hostShare1.setFreeCpu(2377);
		hostShare1.setUsedMem(22656032);
		hostShare1.setUsedCpu(23);
		hostShare1.setRunningVms(12);
		
		HostXml host2 = new HostXml();
		host2.setId(440);
		host2.setName("bonfire-blade-4");
		host2.setState(2);
		host2.setImMad("im_xen");
		host2.setVmMad("vmm_xen");
		host2.setVnMad("dummy");
		host2.setLastMonTime(1372835028);
		host2.setClusterId(-1);
		
		HostShare hostShare2 = new HostShare();
		host2.setHostShare(hostShare2);
		hostShare2.setDiskUsage(0);
		hostShare2.setMemUsage(21233664);
		hostShare2.setCpuUsage(2050);
		hostShare2.setMaxDisk(0);
		hostShare2.setMaxMem(66778144);
		hostShare2.setMaxCpu(2400);
		hostShare2.setFreeDisk(0);
		hostShare2.setFreeMem(44122112);
		hostShare2.setFreeCpu(2377);
		hostShare2.setUsedMem(22656032);
		hostShare2.setUsedCpu(23);
		hostShare2.setRunningVms(17);
		
		List<HostXml> hosts = new ArrayList<HostXml>();
		hosts.add(host1);
		hosts.add(host2);
		HostPool hostPool = new HostPool();
		hostPool.setHosts(hosts);
		
		JAXBContext jaxbContext = JAXBContext.newInstance(HostPool.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(hostPool, out);
		String output = out.toString();
		System.out.println(output);
		
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(output));
		XPath xpath = XPath.newInstance("//HOST_POOL");
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
				
		XPath xpathName = XPath.newInstance("//HOST");
		List listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(2, listxpathName.size());
		
		xpathName = XPath.newInstance("//ID");
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(2, listxpathName.size());
		Element element = (Element) listxpathName.get(0);
		assertEquals("448", element.getValue());
		element = (Element) listxpathName.get(1);
		assertEquals("440", element.getValue());
	}
}
