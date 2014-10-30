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
import eu.eco2clouds.accounting.datamodel.parser.Experiment;
import eu.eco2clouds.accounting.datamodel.parser.Link;
import eu.eco2clouds.accounting.datamodel.parser.VM;

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
	public void pojo() {
		
		// We prepare the objects that we expect to be retrieved by the database.
		Experiment experiment1 = new Experiment();
		experiment1.setHref("/experimens/1");
		experiment1.setId(1);
		experiment1.setBonfireExperimentId((long) 101);
		experiment1.setBonfireUserId("A100");
		experiment1.setBonfireGroupId("ATOS");
		experiment1.setStartTime((long)15000);
		experiment1.setEndTime((long)18000);
		experiment1.setApplicationProfile("application-profile-A100");
		experiment1.setSubmittedExperimentDescriptor("submitted-experiment-descriptor-A100");
		experiment1.setManagedExperimentId(55l);
		experiment1.setStatus("status");
		
		List<Link> links = new ArrayList<Link>();
		experiment1.setLinks(links);
		
		assertEquals(links, experiment1.getLinks());
		assertEquals("/experimens/1", experiment1.getHref());
		assertEquals("status", experiment1.getStatus());
		
		List<VM> vMs = new ArrayList<VM>();
		VM vm = new VM ();
		
		Set<Action> actions = new HashSet<Action>();
		Action action = new Action();
		
		action.setId(1);
		action.setVm_id(101);
		action.setTimestamp(13000);
			
		actions.add(action);
		
		vm.setActions(actions);
		
		vMs.add(vm);
		
		experiment1.setvMs(vMs);
		

		Experiment experiment2 = new Experiment();
		experiment2.setId(2);
		experiment2.setBonfireExperimentId((long)102);
		experiment2.setBonfireUserId("E102");
		experiment2.setBonfireGroupId("EPCC");
		experiment2.setStartTime((long)25000);
		experiment2.setEndTime((long)28000);
		experiment2.setApplicationProfile("application-profile-E102");
		experiment2.setSubmittedExperimentDescriptor("submitted-experiment-descriptor-E102");

		Set<Action> actions2 = new HashSet<Action>();
		VM vm2 = new VM ();
		
		Action action2 = new Action(2,28000,"action-log2");
		
		actions2.add(action2);
		
		vm2.setActions(actions2);
		
		vMs.add(vm);
		
		experiment1.setvMs(vMs);
		
		
		List<Experiment> experiments = new ArrayList<Experiment>();
		experiments.add(experiment1);
		experiments.add(experiment2);
		
		List<String> groups = new ArrayList<String>();
		
		groups.add("EPCC");
		groups.add("ATOS");
		
		
		assertEquals(1, experiments.get(0).getId().intValue());
		assertEquals(101, experiments.get(0).getBonfireExperimentId().longValue());
		assertEquals("A100", experiments.get(0).getBonfireUserId());
		assertEquals("ATOS", experiments.get(0).getBonfireGroupId());
		assertEquals(15000, experiments.get(0).getStartTime().longValue());
		assertEquals(18000, experiments.get(0).getEndTime().longValue());
		assertEquals(2,experiments.get(0).getvMs().size());
		assertEquals(55l, experiments.get(0).getManagedExperimentId().longValue());
		
		assertEquals(2, experiments.get(1).getId().intValue());
		assertEquals(102, experiments.get(1).getBonfireExperimentId().longValue());
		assertEquals("E102", experiments.get(1).getBonfireUserId());
		assertEquals("EPCC", experiments.get(1).getBonfireGroupId());
		assertEquals(25000, experiments.get(1).getStartTime().longValue());
		assertEquals(28000, experiments.get(1).getEndTime().longValue());
	}
	
	@Test
	public void addLinkTest() {
		Experiment experiment = new Experiment();
		
		assertEquals(null, experiment.getLinks());
		
		experiment.addLink("/", "application_profile", "application+json");
		assertEquals(1, experiment.getLinks().size());
		assertEquals("/", ((Link) experiment.getLinks().toArray()[0]).getRel());
	}
	
	@Test
	public void XmlToObject() throws Exception {
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<experiment xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/experiments/1\">"
					+ "<bonfire-experiment-id>101</bonfire-experiment-id>"
					+ "<bonfire-user-id>A100</bonfire-user-id>"
					+ "<bonfire-group-id>ATOS</bonfire-group-id>"
					+ "<start-time>15000</start-time>"
					+ "<end-time>18000</end-time>"
					+ "<link rel=\"parent\" href=\"/experiments\"/>"
					+ "<link rel=\"application-profile\" href=\"/experiments/1/application-profile\"/>"
					+ "<link rel=\"submitted-experiment-descriptor\" href=\"/experiments/1/submitted-experimen-descriptor\"/>"
					+ "<link rel=\"vms\" href=\"/experiments/1/vms\"/>"  
					+ "</experiment>";
		
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Experiment.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Experiment experiment = (Experiment) jaxbUnmarshaller.unmarshal(new StringReader(xml));
		
		assertEquals(101, experiment.getBonfireExperimentId().longValue());
		assertEquals(null, experiment.getId());
		
		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<experiment xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/experiments/1\">"
				+ "<id>1</id>"
				+ "<bonfire-experiment-id>101</bonfire-experiment-id>"
				+ "<bonfire-user-id>A100</bonfire-user-id>"
				+ "<bonfire-group-id>ATOS</bonfire-group-id>"
				+ "<start-time>15000</start-time>"
				+ "<end-time>18000</end-time>"
				+ "<application-profile>application-profile-A100</application-profile>"
				+ "<submitted-experiment-descriptor>submitted-experiment-descriptor-A100</submitted-experiment-descriptor>"
				+ "<link rel=\"parent\" href=\"/experiments\"/>"
				+ "<link rel=\"application-profile\" href=\"/experiments/1/application-profile\"/>"
				+ "<link rel=\"submitted-experiment-descriptor\" href=\"/experiments/1/submitted-experimen-descriptor\"/>"
				+ "<link rel=\"vms\" href=\"/experiments/1/vms\"/>"  
				+ "</experiment>";
		
		experiment = (Experiment) jaxbUnmarshaller.unmarshal(new StringReader(xml));
		
		assertEquals(101, experiment.getBonfireExperimentId().longValue());
		assertEquals(1, experiment.getId().intValue());
		assertEquals("A100", experiment.getBonfireUserId());
		assertEquals("ATOS", experiment.getBonfireGroupId());
		assertEquals(15000, experiment.getStartTime().longValue());
		assertEquals(18000, experiment.getEndTime().longValue());
		assertEquals("application-profile-A100",experiment.getApplicationProfile());
		assertEquals("submitted-experiment-descriptor-A100",experiment.getSubmittedExperimentDescriptor());
		
	}
		
	
	
	@Test
	public void objectToXml() throws Exception {
		
		
		Experiment experiment3 = new Experiment(3,"EPCC", "E103",(long) 103,(long)28000,(long)38000, "application-profile-E103","submitted-experiment-descriptor-E103");
		
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Experiment.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(experiment3, out);
		String output = out.toString();
		System.out.println(output);
			
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(output));
		XPath xpath = XPath.newInstance("//bnf:experiment");
		xpath.addNamespace("bnf", E2C_NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		
		
		XPath xpathName = XPath.newInstance("//bnf:id");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		List listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		Element element = (Element) listxpathName.get(0);
		assertEquals("3", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:bonfire-group-id");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("EPCC", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:bonfire-user-id");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("E103", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:bonfire-experiment-id");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("103", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:start-time");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("28000", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:end-time");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("38000", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:application-profile");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("application-profile-E103", element.getValue());
		
		xpathName = XPath.newInstance("//bnf:submitted-experiment-descriptor");
		xpathName.addNamespace("bnf", E2C_NAMESPACE);
		listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		element = (Element) listxpathName.get(0);
		assertEquals("submitted-experiment-descriptor-E103", element.getValue());
	}
}
