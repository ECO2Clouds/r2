package eu.eco2clouds.accounting.datamodel.xml;

import static org.junit.Assert.assertEquals;
import eu.eco2clouds.accounting.datamodel.xml.HostXml;
import eu.eco2clouds.accounting.datamodel.xml.HostShare;

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
public class HostTest {

	@Test
	public void testPojo() {
		HostXml host = new HostXml();
		host.setId(448);
		host.setName("bonfire-blade-4");
		host.setState(2);
		host.setImMad("im_xen");
		host.setVmMad("vmm_xen");
		host.setVnMad("dummy");
		host.setLastMonTime(1372835028);
		host.setClusterId(-1);

		
		HostShare hostShare = new HostShare();
		host.setHostShare(hostShare);

		assertEquals(448, host.getId());
		assertEquals("bonfire-blade-4", host.getName());
		assertEquals(2, host.getState());
		assertEquals("im_xen", host.getImMad());
		assertEquals("vmm_xen", host.getVmMad());
		assertEquals("dummy", host.getVnMad());
		assertEquals(1372835028, host.getLastMonTime());
		assertEquals(-1, host.getClusterId());
		assertEquals(hostShare, host.getHostShare());
	}
	
	@Test
	public void xmlToObject() throws Exception {	
		String hostXml = "<HOST>"
							+ "<ID>448</ID>"
							+ "<NAME>bonfire-blade-4</NAME>"
							+ "<STATE>2</STATE>"
							+ "<IM_MAD>im_xen</IM_MAD>"
							+ "<VM_MAD>vmm_xen</VM_MAD>"
							+ "<VN_MAD>dummy</VN_MAD>"
							+ "<LAST_MON_TIME>1372835028</LAST_MON_TIME>"
							+ "<CLUSTER_ID>-1</CLUSTER_ID>"
							+ "<CLUSTER>cluster10</CLUSTER>"
							+ "<HOST_SHARE>"
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
							+ "</HOST_SHARE>"
						+ "</HOST>";
		
		JAXBContext jaxbContext = JAXBContext.newInstance(HostXml.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		HostXml host = (HostXml) jaxbUnmarshaller.unmarshal(new StringReader(hostXml));
		
		assertEquals(448, host.getId());
		assertEquals("bonfire-blade-4", host.getName());
		assertEquals(2, host.getState());
		assertEquals("im_xen", host.getImMad());
		assertEquals("vmm_xen", host.getVmMad());
		assertEquals("dummy", host.getVnMad());
		assertEquals(1372835028, host.getLastMonTime());
		assertEquals("cluster10", host.getCluster());
		
		HostShare hostShare = host.getHostShare();
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
	public void objectToXML() throws Exception {
		HostXml host = new HostXml();
		host.setId(448);
		host.setName("bonfire-blade-4");
		host.setState(2);
		host.setImMad("im_xen");
		host.setVmMad("vmm_xen");
		host.setVnMad("dummy");
		host.setLastMonTime(1372835028);
		host.setCluster("cluster_1");
		
		HostShare hostShare = new HostShare();
		host.setHostShare(hostShare);
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
		
		JAXBContext jaxbContext = JAXBContext.newInstance(HostXml.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(host, out);
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
		
		xpathName = XPath.newInstance("//ID");
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("448", element.getValue());
		
		xpathName = XPath.newInstance("//NAME");
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("bonfire-blade-4", element.getValue());
		
		xpathName = XPath.newInstance("//STATE");
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("2", element.getValue());
		
		xpathName = XPath.newInstance("//IM_MAD");
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("im_xen", element.getValue());

		xpathName = XPath.newInstance("//VM_MAD");
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("vmm_xen", element.getValue());
		
		xpathName = XPath.newInstance("//VN_MAD");
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("dummy", element.getValue());
		
		xpathName = XPath.newInstance("//LAST_MON_TIME");
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("1372835028", element.getValue());
		
		xpathName = XPath.newInstance("//CLUSTER");
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("cluster_1", element.getValue());
	}
}

