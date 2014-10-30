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
public class ComputeTest {

	@Test
	public void testPojo() {
		Compute compute = new Compute();
		compute.setName("name");
		compute.setHref("/href/xxx");
		compute.setCpu("0.25");
		compute.setGroups("atos");
		compute.setHost("hhh");
		compute.setId("1");
		ArrayList<Link> links = new ArrayList<Link>();
		compute.setLinks(links);
		compute.setMemory("256");
		compute.setState("PENDING");
		UserId user = new UserId();
		compute.setUname(user);
		
		assertEquals("name", compute.getName());
		assertEquals("/href/xxx", compute.getHref());
		assertEquals("name", compute.getComputeName());
		assertEquals("0.25", compute.getCpu());
		assertEquals("atos", compute.getGroups());
		assertEquals("hhh", compute.getHost());
		assertEquals("1", compute.getId());
		assertEquals(links, compute.getLinks());
		assertEquals("256", compute.getMemory());
		assertEquals("PENDING", compute.getState());
		assertEquals(user, compute.getUname());
		
		// We verify that the creation of ComputeInstanceType creates a fake Configuration
		ComputeInstanceType computeInstanceType = new ComputeInstanceType();
		computeInstanceType.setHref("href1");
		computeInstanceType.setValue("value1");
		compute.setComputeInstanceType(computeInstanceType);
		
		assertEquals("href1", compute.getComputeInstanceType().getHref());
		assertEquals("value1", compute.getComputeInstanceType().getValue());
		assertEquals("href1", compute.getConfiguration().getHref());
		assertEquals("value1", compute.getConfiguration().getName());
		
		Configuration configuration = new Configuration();
		configuration.setHref("href2");
		configuration.setName("value2");
		compute.setConfiguration(configuration);
		
		assertEquals("href2", compute.getComputeInstanceType().getHref());
		assertEquals("value2", compute.getComputeInstanceType().getValue());
		assertEquals("href2", compute.getConfiguration().getHref());
		assertEquals("value2", compute.getConfiguration().getName());
		
		Compute compute1 = new Compute();
		compute1.setName("computeName");
		assertEquals("computeName", compute1.getComputeName());
		
		Compute compute2 = new Compute();
		compute2.setComputeName("computeName");
		assertEquals("computeName", compute2.getName());
	}
	
	@Test
	public void xmlToObject() throws Exception {
		
		String computeXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
							+ "<compute xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"/locations/fr-inria/computes/39287\">"
								+ "<id>39287</id>"
								+ "<cpu>0.5</cpu>"
								+ "<memory>256</memory>"
								+ "<uname href=\"/locations/fr-inria/users/2\">dperez</uname>"
								+ "<groups>atos</groups>"
								+ "<name>sdfasdfasdf</name>"
								+ "<instance_type href=\"/locations/fr-inria/instance_type/lite\">lite</instance_type>"
								+ "<state>RUNNING</state>"
								+ "<disk id=\"0\">"
									+ "<storage href=\"/locations/fr-inria/storages/2587\" name=\"BonFIRE Debian Squeeze v5\"/>"
									+ "<type>FILE</type>"
									+ "<target>xvda</target>"
								+ "</disk>"
								+ "<nic>"
									+ "<network href=\"/locations/fr-inria/networks/53\" name=\"BonFIRE WAN\"/>"
									+ "<ip>172.18.253.21</ip>"
									+ "<mac>02:00:ac:12:fd:15</mac>"
								+ "</nic>"
								+ "<context>"
									+ "<authorized_keys>ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQChu9trsHSNWBDcXP7C86Qi9v+9IbmkD42GkIT5wF42TtnZ/HvpiAmT2jPBD/b0CI4rnmbqwyB+TknAJjJEAb9C8Tgk7Sjgr/0JCbRZu74tzZkrCn/hiG+9bPQBTBv3sxwJ2p94wvrRrgMK21TwqMQ+FkmVBdk6Zp3PoSRtFLs0Nc95kFZlMeonfgSfOJy1dtkzhTAH8fu9HwrjwAAv0xIX+eQ+n3kQgsxQ6vrPdwjOt0n6h52FpYXcPhRiXmkHa5BEWxhvranzsCeTnUQk5RBmC/p6VqA/mFJOzRuKFPP2bRHaE8tcXVeADBQgrBPvZWsSiuXAdJ8bzP+WeprrTw61 A510804@ES-CNU21131QG\nssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQC7nWAhtXGhsp4zfjyMcR4PhNc/adelbBr2bJptNXqOpiwxiHBpt9Tcv0/Z1mCbc1rfy6qNIRPxtHHKTaZuwiA+BKJRNtKHzxV1RRAJ8tKmF1MmT/cIn+Cgtx2L6uVIsJzwjbC4/IZRE81vIkwxpWRLOKX8kHvLDmCZE0OlLKZh3S7oxzw4WZ4NeVCx3euYcsj+8FBfIV7zwjXLgEtgr2q7f5WpMO5orKXvCPWxuoGgjiCF9/PjDOP99ZLXsWJFXIepgHz8eWxFZIj4wijdIExEq2Lp+mE69QbnGwsQy7Cg8wzPj5tYfZ+1ST7af1R5reMSzdnpt3E8ix6MzwMGIIOX A502860@ES-CNU1461SDB</authorized_keys>"
									+ "<bonfire_credentials>dperez:t5r2hjgfbtca87ndpar4p</bonfire_credentials>"
									+ "<bonfire_experiment_aggregator_password>auasf8</bonfire_experiment_aggregator_password>"
									+ "<bonfire_experiment_expiration_date>1368186714</bonfire_experiment_expiration_date>"
									+ "<bonfire_experiment_id>32860</bonfire_experiment_id>"
									+ "<bonfire_experiment_routing_key>95adcfcbf5329129cd7c</bonfire_experiment_routing_key>"
									+ "<bonfire_provider>fr-inria</bonfire_provider>"
									+ "<bonfire_resource_id>39287</bonfire_resource_id>"
									+ "<bonfire_resource_name>sdfasdfasdf</bonfire_resource_name>"
									+ "<bonfire_uri>https://api.bonfire-project.eu</bonfire_uri>"
									+ "<cluster>default</cluster>"
									+ "<dns_servers>131.254.204.4</dns_servers>"
									+ "<files>/srv/cloud/context/lib /srv/cloud/context/distributions /srv/cloud/context/sites /srv/cloud/context/common /srv/cloud/context/init.sh</files>"
									+ "<hostname>sdfasdfasdf-39287</hostname>"
									+ "<log>http://frontend.bonfire.grid5000.fr/logs/39287.log</log>"
									+ "<ntp_servers>ntp.rennes.grid5000.fr</ntp_servers>"
									+ "<public_gateway>131.254.204.8</public_gateway>"
									+ "<target>xvdb</target>"
									+ "<v6_gw>2001:660:7303:204::1</v6_gw>"
									+ "<v6_prefix>2001:660:7303:204</v6_prefix>"
									+ "<wan_gateway>172.18.248.1</wan_gateway>"
									+ "<wan_ip>172.18.253.21</wan_ip>"
									+ "<wan_mac>02:00:ac:12:fd:15</wan_mac>"
									+ "<wan_netmask>255.255.248.0</wan_netmask>"
								+ "</context>"
								+ "<host>bonfire-blade-3</host>"
								+ "<link rel=\"experiment\" href=\"/experiments/32860\" type=\"application/vnd.bonfire+xml\"/>"
							+ "</compute>";
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Compute.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Compute compute = (Compute) jaxbUnmarshaller.unmarshal(new StringReader(computeXml));
		
		assertEquals("39287", compute.getId());
		assertEquals("sdfasdfasdf", compute.getComputeName());
		assertEquals("0.5", compute.getCpu());
		assertEquals("bonfire-blade-3", compute.getHost());
		assertEquals("/locations/fr-inria/computes/39287", compute.getHref());
		assertEquals("256", compute.getMemory());
		assertEquals("RUNNING", compute.getState());
		assertEquals("/locations/fr-inria/users/2", compute.getUname().getHref());
		assertEquals("dperez", compute.getUname().getValue());
		assertEquals("0", compute.getDisks().get(0).getId());
		assertEquals("FILE", compute.getDisks().get(0).getType());
		assertEquals("xvda", compute.getDisks().get(0).getTarget());
		assertEquals("BonFIRE Debian Squeeze v5", compute.getDisks().get(0).getStorage().getName());
		assertEquals("/locations/fr-inria/storages/2587", compute.getDisks().get(0).getStorage().getHref());
		assertEquals("172.18.253.21", compute.getNics().get(0).getIp());
		assertEquals("02:00:ac:12:fd:15", compute.getNics().get(0).getMac());
		assertEquals("/locations/fr-inria/networks/53", compute.getNics().get(0).getNetwork().getHref());
		assertEquals("BonFIRE WAN", compute.getNics().get(0).getNetwork().getName());
		assertEquals("/locations/fr-inria/instance_type/lite", compute.getComputeInstanceType().getHref());
		assertEquals("lite", compute.getComputeInstanceType().getValue());
		assertEquals("/locations/fr-inria/instance_type/lite", compute.getConfiguration().getHref());
		assertEquals("lite", compute.getConfiguration().getName());
	}
	
	@Test
	public void objectToXml() throws Exception {
		Compute compute = new Compute();
		compute.setName("name");
		compute.setHref("/href/xxx");
		compute.setCpu("0.25");
		compute.setGroups("atos");
		compute.setHost("hhh");
		compute.setId("1");
		ArrayList<Link> links = new ArrayList<Link>();
		Link link = new Link();
		link.setHref("/");
		link.setRel("parent");
		links.add(link);
		compute.setLinks(links);
		compute.setMemory("256");
		compute.setState("PENDING");
		UserId user = new UserId();
		user.setHref("adsfasdf");
		user.setValue("dperez");
		compute.setUname(user);
		Disk disk = new Disk();
		Storage storage = new Storage();
		storage.setName("pepito");
		storage.setHref("adsfasd");
		disk.setStorage(storage);
		disk.setId("0");
		disk.setTarget("xvda");
		disk.setType("FILE");
		ArrayList<Disk> disks = new ArrayList<Disk>();
		disks.add(disk);
		compute.setDisks(disks);
		Network network = new Network();
		network.setName("BonFIRE WAN");
		network.setHref("/adsfasdf");
		Nic nic = new Nic();
		nic.setNetwork(network);
		nic.setIp("182.11.11.2");
		nic.setMac("00:11:00:00:11:11");
		ArrayList<Nic> nics = new ArrayList<Nic>();
		nics.add(nic);
		compute.setNics(nics);
		ComputeInstanceType computeInstanceType = new ComputeInstanceType();
		computeInstanceType.setHref("lite");
		computeInstanceType.setValue("medium");
		compute.setComputeInstanceType(computeInstanceType);
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Compute.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(compute, out);
		String output = out.toString();
		System.out.println(output);
		
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(output));
		XPath xpath = XPath.newInstance("//bnf:compute");
		xpath.addNamespace("bnf", NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element storageElement = (Element) listxpath.get(0);
		assertEquals("/href/xxx", storageElement.getAttributeValue("href"));
		assertEquals("name", storageElement.getAttributeValue("name"));
		
		XPath xpathName = XPath.newInstance("//bnf:name");
		xpathName.addNamespace("bnf", NAMESPACE);
		List listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(3, listxpathName.size());
		Element name = (Element) listxpathName.get(0);
		assertEquals("name", name.getValue());
		
		xpathName = XPath.newInstance("//bnf:groups");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element element = (Element) listxpath.get(0);
		assertEquals("atos", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:cpu");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("0.25", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:host");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("hhh", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:id");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("1", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:memory");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("256", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:state");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("PENDING", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:uname");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("dperez", element.getValue());
		assertEquals("adsfasdf", element.getAttributeValue("href"));
		
		xpathName = XPath.newInstance("//bnf:link");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("parent", element.getAttributeValue("rel"));
		assertEquals("/", element.getAttributeValue("href"));
		
		xpathName = XPath.newInstance("//bnf:disk");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("0", element.getAttributeValue("id"));
		
		xpathName = XPath.newInstance("//bnf:instance_type");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("lite", element.getAttributeValue("href"));
		assertEquals("medium", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:storage");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("pepito", element.getAttributeValue("name"));
		assertEquals("adsfasd", element.getAttributeValue("href"));
		
		xpathName = XPath.newInstance("//bnf:type");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("FILE", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:target");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("xvda", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:network");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("BonFIRE WAN", element.getAttributeValue("name"));
		assertEquals("/adsfasdf", element.getAttributeValue("href"));
		
		xpathName = XPath.newInstance("//bnf:ip");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("182.11.11.2", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:mac");
		xpathName.addNamespace("bnf", NAMESPACE);
		listxpath = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("00:11:00:00:11:11", element.getValue());
	}
	
	@Test
	public void addDiskOrNicOrLink() {
		Disk disk = new Disk();
		
		Nic nic = new Nic();
		
		Link link = new Link();
		
		Compute compute = new Compute();
		
		assertNull(compute.getNics());
		compute.addNic(nic);
		assertEquals(1, compute.getNics().size());
		assertEquals(nic, compute.getNics().get(0));
		
		assertNull(compute.getDisks());
		compute.addDisk(disk);
		assertEquals(1, compute.getDisks().size());
		assertEquals(disk, compute.getDisks().get(0));
		
		assertNull(compute.getLinks());
		compute.addLink(link);
		assertEquals(1, compute.getLinks().size());
		assertEquals(link, compute.getLinks().get(0));
	}
}
