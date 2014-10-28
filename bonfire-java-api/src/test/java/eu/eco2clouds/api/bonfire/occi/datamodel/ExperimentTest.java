package eu.eco2clouds.api.bonfire.occi.datamodel;

import static eu.eco2clouds.api.bonfire.occi.Dictionary.NAMESPACE;
import static org.junit.Assert.assertEquals;

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
public class ExperimentTest {
	
	@Test
	public void pojoTest() {
		Experiment experiment = new Experiment();
		experiment.setAggregatorPassword("asdafsd");
		experiment.setCreatedAt("asdfasdfasdfasf");
		experiment.setDescription("a nice description");
		experiment.setGroups("atos");
		experiment.setHref("http://asdfasd");
		experiment.setId("1");
		ArrayList<Link> links = new ArrayList<Link>();
		experiment.setLinks(links);
		experiment.setName("experimentName");
		experiment.setRoutingKey("routingKey");
		experiment.setStatus("RUNNING");
		experiment.setUpdatedAt("asdfasdfasdfadsasdfasdf");
		experiment.setWalltime(3600l);
		
		assertEquals(3600l, experiment.getWalltime());
		assertEquals("asdfasdfasdfadsasdfasdf", experiment.getUpdatedAt());
		assertEquals("RUNNING", experiment.getStatus());
		assertEquals("routingKey", experiment.getRoutingKey());
		assertEquals("experimentName", experiment.getName());
		assertEquals(links, experiment.getLinks());
		assertEquals("1", experiment.getId());
		assertEquals("http://asdfasd", experiment.getHref());
		assertEquals("atos", experiment.getGroups());
		assertEquals("a nice description", experiment.getDescription());
		assertEquals("asdfasdfasdfasf", experiment.getCreatedAt());
		assertEquals("asdafsd", experiment.getAggregatorPassword());
	}

	@Test
	public void xmlToObject() throws Exception {
		String experimentXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
								+ "<experiment href=\"/experiments/32497\" xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\">"
									+ "<id>32497</id>"
									+ "<description>asdfasd</description>"
									+ "<name>asdfasdf</name>"
									+ "<walltime>7200</walltime>"
									+ "<user_id>dperez</user_id>"
									+ "<groups>atos</groups>"
									+ "<status>ready</status>"
									+ "<routing_key>2df1dc3ce5efd6e42aac</routing_key>"
									+ "<aggregator_password>b9w8ka</aggregator_password>"
									+ "<created_at>2013-05-07T15:15:52Z</created_at>"
									+ "<updated_at>2013-05-07T15:15:52Z</updated_at>"
									+ "<networks>"
									+ "</networks>"
									+ "<computes>"
										+ "<compute href=\"/locations/uk-epcc/computes/20047\" name=\"asdfasdfasdf\"/>"
										+ "<compute href=\"/locations/fr-inria/computes/39097\" name=\"asdfasdfasd\"/>"
									+ "</computes>"
									+ "<storages>"
										+ "<storage href=\"/locations/uk-epcc/storages/3206\" name=\"asdfasdf\"/>"
									+ "</storages>"
									+ "<routers>"
									+ "</routers>" 
									+ "<site_links>"
									+ "</site_links>"
									+ "<link rel=\"parent\" href=\"/\"/>"
									+ "<link rel=\"storages\" href=\"/experiments/32497/storages\"/>"
									+ "<link rel=\"networks\" href=\"/experiments/32497/networks\"/>"
									+ "<link rel=\"computes\" href=\"/experiments/32497/computes\"/>"
									+ "<link rel=\"routers\" href=\"/experiments/32497/routers\"/>"
									+ "<link rel=\"site_links\" href=\"/experiments/32497/site_links\"/>" 
								+ "</experiment>";
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Experiment.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		
		Experiment experiment = (Experiment) jaxbUnmarshaller.unmarshal(new StringReader(experimentXml));
		assertEquals("/experiments/32497", experiment.getHref());
		assertEquals("32497", experiment.getId());
		assertEquals("asdfasdf", experiment.getName());
		assertEquals(7200l, experiment.getWalltime());
		assertEquals("dperez", experiment.getUserId());
		assertEquals("atos", experiment.getGroups());
		assertEquals("ready", experiment.getStatus());
		assertEquals("2df1dc3ce5efd6e42aac", experiment.getRoutingKey());
		assertEquals("b9w8ka", experiment.getAggregatorPassword());
		assertEquals("2013-05-07T15:15:52Z", experiment.getCreatedAt());
		assertEquals("2013-05-07T15:15:52Z", experiment.getUpdatedAt());
		
		ArrayList<Link> links = experiment.getLinks();
		
		assertEquals("parent", links.get(0).getRel());
		assertEquals("/", links.get(0).getHref());
		
		assertEquals("storages", links.get(1).getRel());
		assertEquals("/experiments/32497/storages", links.get(1).getHref());
		
		assertEquals("networks", links.get(2).getRel());
		assertEquals("/experiments/32497/networks", links.get(2).getHref());
		
		assertEquals("computes", links.get(3).getRel());
		assertEquals("/experiments/32497/computes", links.get(3).getHref());
		
		assertEquals("routers", links.get(4).getRel());
		assertEquals("/experiments/32497/routers", links.get(4).getHref());
		
		assertEquals("site_links", links.get(5).getRel());
		assertEquals("/experiments/32497/site_links", links.get(5).getHref());
		
		ArrayList<Compute> computes = experiment.getComputes();
		
		assertEquals("/locations/uk-epcc/computes/20047", computes.get(0).getHref());
		assertEquals("asdfasdfasdf", computes.get(0).getName());
		
		assertEquals("/locations/fr-inria/computes/39097", computes.get(1).getHref());
		assertEquals("asdfasdfasd", computes.get(1).getName());
		
		ArrayList<Storage> storages = experiment.getStorages();
		
		assertEquals("/locations/uk-epcc/storages/3206", storages.get(0).getHref());
		assertEquals("asdfasdf", storages.get(0).getName());
	}
	
	@Test
	public void objectToXml() throws Exception {
		Compute compute = new Compute();
		compute.setHref("/adfasdfad/adfasdfad");
		compute.setName("nameCompute");
		ArrayList<Compute> computes = new ArrayList<Compute>();
		computes.add(compute);
		
		Storage storage = new Storage();
		storage.setHref("/storage/href");
		storage.setName("storagename");
		ArrayList<Storage> storages = new ArrayList<Storage>();
		storages.add(storage);
		
		Link link = new Link();
		link.setRel("/");
		link.setHref("/href");
		ArrayList<Link> links = new ArrayList<Link>();
		links.add(link);
		
		Experiment experiment = new Experiment();
		experiment.setAggregatorPassword("agropassword");
		experiment.setComputes(computes);
		experiment.setCreatedAt("ayer");
		experiment.setDescription("a beautiful description");
		experiment.setGroups("atos");
		experiment.setHref("http://www.some.thing");
		experiment.setId("111");
		experiment.setLinks(links);
		experiment.setName("experimentName");
		experiment.setRoutingKey("routing-key");
		experiment.setStatus("PENDIGN");
		experiment.setStorages(storages);
		experiment.setUpdatedAt("updatedAt");
		experiment.setUserId("dperez");
		experiment.setWalltime(3600l);
		
		//We convert it to XML text...
		JAXBContext jaxbContext = JAXBContext.newInstance(Experiment.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(experiment, out);
		String output = out.toString();
		
		//Termina esto!!!
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(output));
		XPath xpath = XPath.newInstance("//bnf:experiment");
		xpath.addNamespace("bnf", NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element locationElement = (Element) listxpath.get(0);
		assertEquals("http://www.some.thing", locationElement.getAttributeValue("href"));
		
		XPath xpathName = XPath.newInstance("//bnf:name");
		xpathName.addNamespace("bnf", NAMESPACE);
		List listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(3, listxpathName.size());
		Element name = (Element) listxpathName.get(0);
		assertEquals("experimentName", name.getValue());
		
		xpath = XPath.newInstance("//bnf:id");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element id = (Element) listxpath.get(0);
		assertEquals("111", id.getValue());
		
		xpath = XPath.newInstance("//bnf:created_at");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element createdAt = (Element) listxpath.get(0);
		assertEquals("ayer", createdAt.getValue());
		
		xpath = XPath.newInstance("//bnf:description");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element description = (Element) listxpath.get(0);
		assertEquals("a beautiful description", description.getValue());
		
		xpath = XPath.newInstance("//bnf:routing_key");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element routingKey = (Element) listxpath.get(0);
		assertEquals("routing-key", routingKey.getValue());
		
		xpath = XPath.newInstance("//bnf:status");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element status = (Element) listxpath.get(0);
		assertEquals("PENDIGN", status.getValue());
		
		xpath = XPath.newInstance("//bnf:updated_at");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element updatedAt = (Element) listxpath.get(0);
		assertEquals("updatedAt", updatedAt.getValue());
		
		xpath = XPath.newInstance("//bnf:compute");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element computeElement = (Element) listxpath.get(0);
		assertEquals("/adfasdfad/adfasdfad", computeElement.getAttributeValue("href"));
		assertEquals("nameCompute", computeElement.getAttributeValue("name"));
		
		xpath = XPath.newInstance("//bnf:storage");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element storageElement = (Element) listxpath.get(0);
		assertEquals("/storage/href", storageElement.getAttributeValue("href"));
		assertEquals("storagename", storageElement.getAttributeValue("name"));
		
		xpath = XPath.newInstance("//bnf:link");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element linkElement = (Element) listxpath.get(0);
		assertEquals("/href", linkElement.getAttributeValue("href"));
		assertEquals("/", linkElement.getAttributeValue("rel"));
	}
	
}
