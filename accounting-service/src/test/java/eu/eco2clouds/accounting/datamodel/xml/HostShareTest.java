package eu.eco2clouds.accounting.datamodel.xml;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.jdom.xpath.XPath;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import org.junit.Test;

import eu.eco2clouds.accounting.datamodel.xml.HostShare;

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
public class HostShareTest {

	@Test
	public void testPojo() {

		HostShare hostShare = new HostShare();
		hostShare.setDiskUsage(0);
		hostShare.setMemUsage(21233664);
		hostShare.setCpuUsage(2050);
		hostShare.setMaxDisk(0);
		hostShare.setMaxMem(66778144);
		hostShare.setMaxCpu(2400);
		hostShare.setFreeDisk(0);
		hostShare.setFreeMem(44122112);
		hostShare.setFreeCpu(2377);
		hostShare.setUsedMem(22656032);
		hostShare.setUsedCpu(23);
		hostShare.setRunningVms(12);
		hostShare.setUsedDisk(50);
		
		assertEquals(0, hostShare.getDiskUsage());
		assertEquals(21233664, hostShare.getMemUsage());
		assertEquals(2050, hostShare.getCpuUsage());
		assertEquals(0, hostShare.getMaxDisk());
		assertEquals(66778144, hostShare.getMaxMem());
		assertEquals(2400, hostShare.getMaxCpu());
		assertEquals(0, hostShare.getFreeDisk());
		assertEquals(44122112, hostShare.getFreeMem());
		assertEquals(2377, hostShare.getFreeCpu());
		assertEquals(22656032, hostShare.getUsedMem());
		assertEquals(23, hostShare.getUsedCpu());
		assertEquals(12, hostShare.getRunningVms());
		assertEquals(50, hostShare.getUsedDisk());
	}
	
	@Test
	public void xmlToObject() throws Exception {
		
		String hostShareXml = "<HOST_SHARE>"
								+ "<DISK_USAGE>0</DISK_USAGE>"
								+ "<MEM_USAGE>21233664</MEM_USAGE>"
								+ "<CPU_USAGE>2050</CPU_USAGE>"
								+ "<MAX_DISK>0</MAX_DISK>"
								+ "<MAX_MEM>66778144</MAX_MEM>"
								+ "<MAX_CPU>2400</MAX_CPU>"
								+ "<FREE_DISK>0</FREE_DISK>"
								+ "<FREE_MEM>44122112</FREE_MEM>"
								+ "<FREE_CPU>2377</FREE_CPU>"
								+ "<USED_DISK>0</USED_DISK>"
								+ "<USED_MEM>22656032</USED_MEM>"
								+ "<USED_CPU>23</USED_CPU>"
								+ "<RUNNING_VMS>12</RUNNING_VMS>"
							+ "</HOST_SHARE>";

		JAXBContext jaxbContext = JAXBContext.newInstance(HostShare.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		HostShare hostShare = (HostShare) jaxbUnmarshaller.unmarshal(new StringReader(hostShareXml));
		
		assertEquals(0, hostShare.getDiskUsage());
		assertEquals(21233664, hostShare.getMemUsage());
		assertEquals(2050, hostShare.getCpuUsage());
		assertEquals(0, hostShare.getMaxDisk());
		assertEquals(66778144, hostShare.getMaxMem());
		assertEquals(2400, hostShare.getMaxCpu());
		assertEquals(0, hostShare.getFreeDisk());
		assertEquals(44122112, hostShare.getFreeMem());
		assertEquals(2377, hostShare.getFreeCpu());
		assertEquals(22656032, hostShare.getUsedMem());
		assertEquals(23, hostShare.getUsedCpu());
		assertEquals(12, hostShare.getRunningVms());
	}
	
	@Test
	public void objectToXml() throws Exception {
		HostShare hostShare = new HostShare();
		hostShare.setDiskUsage(0);
		hostShare.setMemUsage(21233664);
		hostShare.setCpuUsage(2050);
		hostShare.setMaxDisk(0);
		hostShare.setMaxMem(66778144);
		hostShare.setMaxCpu(2400);
		hostShare.setFreeDisk(0);
		hostShare.setFreeMem(44122112);
		hostShare.setFreeCpu(2377);
		hostShare.setUsedMem(22656032);
		hostShare.setUsedCpu(23);
		hostShare.setRunningVms(12);
		
		JAXBContext jaxbContext = JAXBContext.newInstance(HostShare.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(hostShare, out);
		String output = out.toString();
		System.out.println(output);
		
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(output));
		XPath xpath = XPath.newInstance("//HOST_SHARE");
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
				
		XPath xpathName = XPath.newInstance("//DISK_USAGE");
		List listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		Element element = (Element) listxpathName.get(0);
		assertEquals("0", element.getValue());
		
		xpathName = XPath.newInstance("//MEM_USAGE");
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("21233664", element.getValue());
		
		xpathName = XPath.newInstance("//CPU_USAGE");
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("2050", element.getValue());
		
		xpathName = XPath.newInstance("//MAX_DISK");
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("0", element.getValue());
		
		xpathName = XPath.newInstance("//MAX_MEM");
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("66778144", element.getValue());
		
		xpathName = XPath.newInstance("//MAX_CPU");
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("2400", element.getValue());
		
		xpathName = XPath.newInstance("//FREE_DISK");
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("0", element.getValue());
		
		xpathName = XPath.newInstance("//FREE_MEM");
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("44122112", element.getValue());
		
		xpathName = XPath.newInstance("//FREE_CPU");
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("2377", element.getValue());
		
		xpathName = XPath.newInstance("//USED_DISK");
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("0", element.getValue());
		
		xpathName = XPath.newInstance("//USED_MEM");
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("22656032", element.getValue());

		xpathName = XPath.newInstance("//USED_CPU");
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("23", element.getValue());

		xpathName = XPath.newInstance("//RUNNING_VMS");
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("12", element.getValue());
	}
}

