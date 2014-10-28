package eu.eco2clouds.api.bonfire.client;

import static eu.eco2clouds.api.bonfire.occi.Dictionary.NAMESPACE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import eu.eco2clouds.api.bonfire.client.exceptions.NoHrefSetException;
import eu.eco2clouds.api.bonfire.client.util.MockWebServer;
import eu.eco2clouds.api.bonfire.occi.datamodel.Compute;
import eu.eco2clouds.api.bonfire.occi.datamodel.Configuration;
import eu.eco2clouds.api.bonfire.occi.datamodel.Disk;
import eu.eco2clouds.api.bonfire.occi.datamodel.Experiment;
import eu.eco2clouds.api.bonfire.occi.datamodel.Link;
import eu.eco2clouds.api.bonfire.occi.datamodel.Location;
import eu.eco2clouds.api.bonfire.occi.datamodel.Network;
import eu.eco2clouds.api.bonfire.occi.datamodel.Nic;
import eu.eco2clouds.api.bonfire.occi.datamodel.Storage;

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
public class BonFIREAPIClientTest {
	@Rule
	public ExpectedException exception = ExpectedException.none();
	private MockWebServer mServer;
	private String mBaseURL = "http://localhost:";
	
	@Before
	public void before() {
		mServer = new MockWebServer();
		mServer.start();
		mBaseURL = mBaseURL + mServer.getPort();
	}

	@Test 
	public void pojoTest() {
		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setPassword("password");
		client.setUserName("username");
		client.setUrl("https://api.bonfire-project.eu");
		
		assertEquals("password", client.getPassword());
		assertEquals("username", client.getUserName());
		assertEquals("https://api.bonfire-project.eu", client.getUrl());
	}
	
	
	@Test
	public void testGetLocations() {
		String xmlLocations = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
							  + "<collection xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"/locations\">"
							  	+ "<items offset=\"0\" total=\"3\">"
							  		+ "<location href=\"/locations/autobahn\">"
							  			+ "<name>autobahn</name>"
							  			+ "<url>http://172.18.9.5:8080/autobahn/uap</url>"
							  			+ "<link rel=\"parent\" href=\"/\" type=\"application/vnd.bonfire+xml\"/>"
							  		+ "</location>"
							  		+ "<location href=\"/locations/be-ibbt\">"
							  			+ "<name>be-ibbt</name>"
							  			+ "<url>https://bonfire.test.atlantis.ugent.be</url>"
							  			+ "<link rel=\"parent\" href=\"/\" type=\"application/vnd.bonfire+xml\"/>"
							  		+ "</location>"
							  	+ "</items>"
							  	+ "<link href=\"/\" rel=\"parent\" type=\"application/vnd.bonfire+xml\"/>"
							  + "</collection>";

		mServer.addPath("/locations", xmlLocations);
		
		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setUserName("username");
		client.setPassword("password");
		client.setUrl(mBaseURL);
		
		ArrayList<Location> locations = client.getLocations();
		assertEquals(2, locations.size());
		assertEquals("/locations/autobahn", locations.get(0).getHref());
		assertEquals("autobahn", locations.get(0).getName());
		assertEquals("http://172.18.9.5:8080/autobahn/uap", locations.get(0).getUrl());
		assertEquals(1, locations.get(1).getLinks().size());
	}
	
	@Test
	public void getListExperiments() {
		String xmlListOfExperiments = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
									  + "<collection xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"/experiments\">"
											+ "<items offset=\"0\" total=\"2\">"
												+ "<experiment href=\"/experiments/2831\">" 
													+ "<id>2831</id>"
													+ "<description>asdfasdf</description>"
													+ "<name>asdfasd</name>"
													+ "<walltime>7200</walltime>"
													+ "<user_id>dperez</user_id>"
													+ "<groups>dperez</groups>"
													+ "<status>ready</status>"
													+ "<routing_key>dc5f115659fe2fc98ec5</routing_key>"
													+ "<aggregator_password>tk3485</aggregator_password>"
													+ "<created_at>2013-05-24T10:22:15Z</created_at>"
													+ "<updated_at>2013-05-24T10:23:15Z</updated_at>"
													+ "<networks>"
													+ "</networks>"
													+ "<computes>"
													+ "</computes>"
													+ "<storages>"
													+ "</storages>"
													+ "<routers>"
													+ "</routers>"
													+ "<site_links>"
													+ "</site_links>"
													+ "<link rel=\"parent\" href=\"/\"/>"
													+ "<link rel=\"storages\" href=\"/experiments/2831/storages\"/>"
													+ "<link rel=\"networks\" href=\"/experiments/2831/networks\"/>"
													+ "<link rel=\"computes\" href=\"/experiments/2831/computes\"/>"
													+ "<link rel=\"routers\" href=\"/experiments/2831/routers\"/>"
													+ "<link rel=\"site_links\" href=\"/experiments/2831/site_links\"/>"
												+ "</experiment>"
												+ "<experiment href=\"/experiments/2830\">"
													+ "<id>2830</id>"
													+ "<description>asdfasd</description>"
													+ "<name>adfasd</name>"
													+ "<walltime>7200</walltime>"
													+ "<user_id>dperez</user_id>"
													+ "<groups>atos</groups>"
													+ "<status>ready</status>"
													+ "<routing_key>e1be0adcbf2851c6f896</routing_key>"
													+ "<aggregator_password>z68r3g</aggregator_password>"
													+ "<created_at>2013-05-24T10:21:56Z</created_at>"
													+ "<updated_at>2013-05-24T10:21:56Z</updated_at>"
													+ "<networks>"
													+ "</networks>"
													+ "<computes>"
													+ "</computes>"
													+ "<storages>"
													+ "</storages>"
													+ "<routers>"
													+ "</routers>"
													+ "<site_links>"
													+ "</site_links>"
													+ "<link rel=\"parent\" href=\"/\"/>"
													+ "<link rel=\"storages\" href=\"/experiments/2830/storages\"/>"
													+ "<link rel=\"networks\" href=\"/experiments/2830/networks\"/>"
													+ "<link rel=\"computes\" href=\"/experiments/2830/computes\"/>"
													+ "<link rel=\"routers\" href=\"/experiments/2830/routers\"/>"
													+ "<link rel=\"site_links\" href=\"/experiments/2830/site_links\"/>"
												+ "</experiment>"
											+ "</items>"
											+ "<link href=\"/\" rel=\"parent\" type=\"application/vnd.bonfire+xml\"/>"
										+ "</collection>";
		
		mServer.addPath("/experiments", xmlListOfExperiments);
		
		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setUserName("username");
		client.setPassword("password");
		client.setUrl(mBaseURL);
		
		ArrayList<Experiment> experiments = client.getExperiments();
		
		assertEquals(2, experiments.size());
		assertEquals("/experiments/2831", experiments.get(0).getHref());
		assertEquals("2831", experiments.get(0).getId());
		assertEquals("asdfasdf", experiments.get(0).getDescription());
		assertEquals("asdfasd", experiments.get(0).getName());
		assertEquals(7200l, experiments.get(0).getWalltime());
		assertEquals("dperez", experiments.get(0).getGroups());
		assertEquals("dperez", experiments.get(0).getUserId());
		assertEquals("dc5f115659fe2fc98ec5", experiments.get(0).getRoutingKey());
		assertEquals("tk3485", experiments.get(0).getAggregatorPassword());
		assertEquals("2013-05-24T10:22:15Z", experiments.get(0).getCreatedAt());
		assertEquals("2013-05-24T10:23:15Z", experiments.get(0).getUpdatedAt());
		assertEquals(6, experiments.get(0).getLinks().size());
		assertEquals("2013-05-24T10:21:56Z", experiments.get(1).getCreatedAt());
		assertEquals("atos", experiments.get(1).getGroups());
	}
	
	@Test
	public void getStoragesOfALocation() {
		String xmlCollectionStorages = "<?xml version='1.0' encoding='UTF-8'?>"
									   + "<collection xmlns='http://api.bonfire-project.eu/doc/schemas/occi'>"
									   		+ "<items>"
									   			+ "<storage href=\"/locations/fr-inria/storages/190\" name=\"Enactor Integration Test Save As\">"
									   				+ "<name>Enactor Integration Test Save As</name>"
									   				+ "<user_id>dperez</user_id>"
									   				+ "<state>READY</state>"
									   				+ "<type>OS</type>"
									   				+ "<size>700</size>"
									   				+ "<persistent>NO</persistent>"
									   				+ "<link href=\"/locations/fr-inria\" rel=\"location\"/>"
									   				+ "<link rel=\"experiment\" type=\"application/vnd.bonfire+xml\"/>"
									   			+ "</storage>"
									   			+ "<storage href=\"/locations/fr-inria/storages/340\" name=\"testdone\">"
									   				+ "<name>testdone</name>"
									   				+ "<user_id>yliang</user_id>"
									   				+ "<state>READY</state>"
									   				+ "<type>DATABLOCK</type>"
									   				+ "<size>1024</size>"
									   				+ "<persistent>NO</persistent>"
									   				+ "<link href=\"/locations/fr-inria\" rel=\"location\"/>"
									   				+ "<link rel=\"experiment\" type=\"application/vnd.bonfire+xml\"/>"
									   			+ "</storage>"
									   		+ "</items>"
									   	+ "</collection>";
		
		mServer.addPath("/locations/fr-inria/storages", xmlCollectionStorages);
		
		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setUserName("username");
		client.setPassword("password");
		client.setUrl(mBaseURL);
		
		Location location = new Location();
		location.setHref("/locations/fr-inria");
		location.setName("fr-inria");
		location.setUrl("nice occi server url");
		
		ArrayList<Storage> storages = client.getStoragesOfALocation(location);
		assertEquals(2, storages.size());
		assertEquals("/locations/fr-inria/storages/190", storages.get(0).getHref());
		assertEquals("Enactor Integration Test Save As", storages.get(0).getName());
		assertEquals("dperez", storages.get(0).getUserId().getValue());
		assertEquals("READY", storages.get(0).getState());
		assertEquals("OS", storages.get(0).getType());
		assertEquals("700", storages.get(0).getSize());
		assertEquals("NO", storages.get(0).getPersistent());
		assertEquals(2, storages.get(0).getLinks().size());
		assertEquals("/locations/fr-inria/storages/340", storages.get(1).getHref());
		assertEquals("testdone", storages.get(1).getStorageName());
	}
	
	@Test
	public void getNetworksOfALocation() {
		String xmlCollectionOfNetwroks = "<?xml version=\'1.0\' encoding=\'UTF-8\'?>"
										 + "<collection xmlns=\'http://api.bonfire-project.eu/doc/schemas/occi\'>"
										 	+ "<items>"
										 		+ "<network href=\"/locations/fr-inria/networks/1\" name=\"BonFIRE WAN\">"
										 			+ "<name>BonFIRE WAN</name>"
										 			+ "<uname>admin</uname>"
										 			+ "<link href=\"/locations/fr-inria\" rel=\"location\"/>"
										 			+ "<link rel=\"experiment\" type=\"application/vnd.bonfire+xml\"/>"
										 		+ "</network>"
										 	+ "</items>"
										 + "</collection>";
		
		mServer.addPath("/locations/fr-inria/networks", xmlCollectionOfNetwroks);
		
		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setUserName("username");
		client.setPassword("password");
		client.setUrl(mBaseURL);
		
		Location location = new Location();
		location.setHref("/locations/fr-inria");
		location.setName("fr-inria");
		location.setUrl("nice occi server url");
		
		List<Network> networks = client.getNetworksOfALocation(location);
		assertEquals(1, networks.size());
		assertEquals("BonFIRE WAN", networks.get(0).getName());
		assertEquals("admin", networks.get(0).getUname().getValue());
		assertEquals("/locations/fr-inria/networks/1", networks.get(0).getHref());
	}
	
	@Test
	public void testListConfigurations() {
		String xmlListOfConfigurations = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
										 + "<collection xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\">"
										 	+ "<items offset=\"0\" total=\"4\">"
										 		+ "<configuration href=\"/locations/uk-epcc/configurations/custom\">"
										 			+ "<name>custom</name>"
										 		+ "</configuration>"
										 		+ "<configuration href=\"/locations/uk-epcc/configurations/lite\">"
										 			+ "<name>lite</name>"
										 			+ "<memory>256</memory>"
										 			+ "<cpu>0.25</cpu>"
										 		+ "</configuration>"
										 	+ "</items>"
										 	+ "<link rel=\"parent\" href=\"/locations/uk-epcc\" type=\"application/vnd.bonfire+xml\"/>"
										 + "</collection>";

		mServer.addPath("/locations/uk-epcc/configurations", xmlListOfConfigurations);
		
		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setUserName("username");
		client.setPassword("password");
		client.setUrl(mBaseURL);
		
		Location location = new Location();
		location.setHref("/locations/uk-epcc");
		location.setName("fr-inria");
		location.setUrl("nice occi server url");
		
		List<Configuration> configurations = client.getConfigurationsOfALocation(location);
		assertEquals(2, configurations.size());
		assertEquals("custom", configurations.get(0).getName());
		assertEquals("/locations/uk-epcc/configurations/lite", configurations.get(1).getHref());
		assertEquals(256l, configurations.get(1).getMemory());
	}
	
	/**
	 * Experiment that we are trying to create:
	 * <experiment xmlns="http://api.bonfire-project.eu/doc/schemas/occi">
	 *	<name>MyExpoerimetn</name>
  	 *	<groups>dperez</groups>
  	 * 	<description>Test Experiment</description>
  	 *	<walltime>7200</walltime>
  	 *	<status>ready</status>
	 * </experiment>
	 */
	@Test
	public void postExperiment() throws Exception {
		
		String apiXMLOCCIReply = "<experiment xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"/experiments/3005\">"
									+ "<id>3005</id>"
									+ "<description>Test Experiment</description>"
									+ "<name>MyExpoerimetn</name>"
									+ "<walltime>7200</walltime>"
									+ "<user_id>dperez</user_id>"
									+ "<groups>dperez</groups>"
									+ "<status>ready</status>"
									+ "<routing_key>dc0e76a89b66d78b6594</routing_key>"
									+ "<aggregator_password>3tt6ne</aggregator_password>"
									+ "<created_at>2013-05-27T16:27:10Z</created_at>"
									+ "<updated_at>2013-05-27T16:27:10Z</updated_at>"
									+ "<link rel=\"parent\" href=\"/\"/>"
									+ "<link rel=\"storages\" href=\"/experiments/3005/storages\"/>"
									+ "<link rel=\"networks\" href=\"/experiments/3005/networks\"/>"
									+ "<link rel=\"computes\" href=\"/experiments/3005/computes\"/>"
									+ "<link rel=\"routers\" href=\"/experiments/3005/routers\"/>"
									+ "<link rel=\"site_links\" href=\"/experiments/3005/site_links\"/>"
								+ "</experiment>";
		
		mServer.addPath("/experiments", apiXMLOCCIReply);
		
		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setUserName("username");
		client.setPassword("password");
		client.setUrl(mBaseURL);
		
		Experiment experiment = new Experiment();
		experiment.setDescription("Test Experiment");
		experiment.setGroups("dperez");
		experiment.setStatus("ready");
		experiment.setWalltime(7200l);
		experiment.setName("MyExpoerimetn");
		
		Experiment experimentCreated = client.createExperiment(experiment);
		
		String messageSentToBonFIREAPI = mServer.getRequestBody();
		
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(messageSentToBonFIREAPI));
		XPath xpath = XPath.newInstance("//bnf:experiment");
		xpath.addNamespace("bnf", NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		
		XPath xpathName = XPath.newInstance("//bnf:name");
		xpathName.addNamespace("bnf", NAMESPACE);
		List listxpathName = xpathName.selectNodes(xmldoc);
		assertEquals(1, listxpathName.size());
		Element name = (Element) listxpathName.get(0);
		assertEquals("MyExpoerimetn", name.getValue());
				
		xpath = XPath.newInstance("//bnf:description");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element description = (Element) listxpath.get(0);
		assertEquals("Test Experiment", description.getValue());
		
		xpath = XPath.newInstance("//bnf:status");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element status = (Element) listxpath.get(0);
		assertEquals("ready", status.getValue());

		xpath = XPath.newInstance("//bnf:walltime");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element walltime = (Element) listxpath.get(0);
		assertEquals("7200", walltime.getValue());
		
		//We verify the experiment was created as expected...
		assertEquals("3005", experimentCreated.getId());
		assertEquals("Test Experiment", experimentCreated.getDescription());
		assertEquals("MyExpoerimetn", experimentCreated.getName());
		assertEquals(7200l, experimentCreated.getWalltime());
		assertEquals("dperez", experimentCreated.getGroups());
	}
	
	@Test
	public void testDeleteExperiment() throws Exception {
		Experiment experiment = new Experiment();
		experiment.setId("111");
		experiment.setHref("/experiments/111");
		
		mServer.addPath("/experiments/111", "", 202);

		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setUserName("username");
		client.setPassword("password");
		client.setUrl(mBaseURL);
		
		boolean isDeleted = client.deleteResource(experiment);
		assertTrue("Experiment deleted correctly ", isDeleted);
		
		mServer.addPath("/experiments/112", "", 400);
		experiment.setHref("/experiments/112");
		isDeleted = client.deleteResource(experiment);
		assertFalse("Experiment was not deleted correctly: ", isDeleted);
		
		mServer.addPath("/experiments/113", "", 204);
		experiment.setHref("/experiments/113");
		isDeleted = client.deleteResource(experiment);
		assertTrue("Experiment was deleted correctly: ", isDeleted);
	}
	
	@Test
	public void creationOfACompute() throws Exception {
		
		String returnedOCCIBytheAPI = "<compute xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"/locations/uk-epcc/computes/583\">"
										+ "<id>583</id>"
										+ "<cpu>0.25</cpu>"
										+ "<memory>256</memory>"
										+ "<uname href=\"/locations/uk-epcc/users/7\">dperez</uname>"
										+ "<groups>dperez</groups>"
										+ "<name>asdfasdfas</name>"
										+ "<instance_type href=\"/locations/uk-epcc/configurations/lite\">lite</instance_type>"
										+ "<state>RUNNING</state>"
										+ "<disk id=\"0\">"
											+ "<storage href=\"/locations/uk-epcc/storages/0\" name=\"BonFIRE Debian Squeeze v5\"/>"
											+ "<type>FILE</type>"
											+ "<target>sda</target>"
										+ "</disk>"
										+ "<nic>"
											+ "<network href=\"/locations/uk-epcc/networks/0\" name=\"BonFIRE WAN\"/>"
											+ "<ip>172.18.3.160</ip>"
											+ "<mac>02:00:ac:12:03:a0</mac>"
										+ "</nic>"
										+ "<context>"
											+ "<authorized_keys>ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQChu9trsHSNWBDcXP7C86Qi9v+9IbmkD42GkIT5wF42TtnZ/HvpiAmT2jPBD/b0CI4rnmbqwyB+TknAJjJEAb9C8Tgk7Sjgr/0JCbRZu74tzZkrCn/hiG+9bPQBTBv3sxwJ2p94wvrRrgMK21TwqMQ+FkmVBdk6Zp3PoSRtFLs0Nc95kFZlMeonfgSfOJy1dtkzhTAH8fu9HwrjwAAv0xIX+eQ+n3kQgsxQ6vrPdwjOt0n6h52FpYXcPhRiXmkHa5BEWxhvranzsCeTnUQk5RBmC/p6VqA/mFJOzRuKFPP2bRHaE8tcXVeADBQgrBPvZWsSiuXAdJ8bzP+WeprrTw61 A510804@ES-CNU21131QG</authorized_keys>"
											+ "<bonfire_credentials>dperez:g3uqkvz3czmpv79zusups</bonfire_credentials>"
											+ "<bonfire_experiment_aggregator_password>6afh7a</bonfire_experiment_aggregator_password>"
											+ "<bonfire_experiment_expiration_date>1369744294</bonfire_experiment_expiration_date>"
											+ "<bonfire_experiment_id>3032</bonfire_experiment_id>"
											+ "<bonfire_experiment_routing_key>3fdff0c64c51135f4e5e</bonfire_experiment_routing_key>"
											+ "<bonfire_provider>uk-epcc</bonfire_provider>"
											+ "<bonfire_resource_id>583</bonfire_resource_id>"
											+ "<bonfire_resource_name>asdfasdfas</bonfire_resource_name>"
											+ "<bonfire_uri>https://api.integration.bonfire.grid5000.fr</bonfire_uri>"
											+ "<dns_servers>172.18.6.1</dns_servers>"
											+ "<eth0_gateway>172.18.3.1</eth0_gateway>"
											+ "<eth0_ip>172.18.3.160</eth0_ip>"
											+ "<eth0_mask>255.255.255.0</eth0_mask>"
											+ "<eth0_name>BonFIRE WAN</eth0_name>"
											+ "<files>/srv/cloud/context /srv/cloud/context/lib /srv/cloud/context/distributions /srv/cloud/context/sites /srv/cloud/context/common /srv/cloud/context/init.sh</files>"
											+ "<hostname>asdfasdfas-583</hostname>"
											+ "<ntp_servers>129.215.175.254</ntp_servers>"
											+ "<public_gateway>129.215.175.254</public_gateway>"
											+ "<public_netmask>255.255.255.128</public_netmask>"
											+ "<target>sdb</target>"
											+ "<usage>zabbix-agent</usage>"
											+ "<wan_ip>172.18.3.160</wan_ip>"
											+ "<wan_mac>02:00:ac:12:03:a0</wan_mac>"
										+ "</context>"
										+ "<host>crockett0</host>"
										+ "<link rel=\"experiment\" href=\"/experiments/3032\" type=\"application/vnd.bonfire+xml\"/>"
									+ "</compute>";
		
		mServer.addPath("/experiments/211/computes", returnedOCCIBytheAPI);
		
		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setUserName("username");
		client.setPassword("password");
		client.setUrl(mBaseURL);
		
		Experiment experiment = new Experiment();
		experiment.setId("211");
		experiment.setHref("/experiments/211");
		
		Compute compute = new Compute();
		compute.setComputeName("computeName");
		compute.setGroups("dperez");
		Configuration configuration = new Configuration();
		configuration.setName("lite");
		compute.setConfiguration(configuration);
		Disk disk = new Disk();
		Storage storage = new Storage();
		storage.setHref("/locations/uk-epcc/storages/0");
		disk.setStorage(storage);
		disk.setType("OS");
		disk.setTarget("hda");
		compute.addDisk(disk);
		Nic nic = new Nic();
		Network network = new Network();
		network.setHref("/locations/uk-epcc/networks/0");
		nic.setNetwork(network);
		compute.addNic(nic);
		
		Location location = new Location();
		location.setHref("/locations/uk-epcc");

		Compute createdCompute = client.createCompute(location, experiment, compute);
		
		String messageSentToBonFIREAPI = mServer.getRequestBody();
		
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(messageSentToBonFIREAPI));
		XPath xpath = XPath.newInstance("//bnf:compute");
		xpath.addNamespace("bnf", NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		
		xpath = XPath.newInstance("//bnf:name");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element element = (Element) listxpath.get(0);
		assertEquals("computeName", element.getValue());
		
		xpath = XPath.newInstance("//bnf:groups");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("dperez", element.getValue());
		
		xpath = XPath.newInstance("//bnf:instance_type");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("lite", element.getValue());

		xpath = XPath.newInstance("//bnf:storage");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("/locations/uk-epcc/storages/0", element.getAttributeValue("href"));
		
		xpath = XPath.newInstance("//bnf:network");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("/locations/uk-epcc/networks/0", element.getAttributeValue("href"));
		
		xpath = XPath.newInstance("//bnf:target");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("hda", element.getValue());
		
		xpath = XPath.newInstance("//bnf:link");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("/locations/uk-epcc", element.getAttributeValue("href"));
		assertEquals("location", element.getAttributeValue("rel"));		
		
		assertEquals("583", createdCompute.getId());
		assertEquals("256", createdCompute.getMemory());
		assertEquals("0.25", createdCompute.getCpu());
		assertEquals("dperez", createdCompute.getGroups());
		assertEquals("/locations/uk-epcc/storages/0", createdCompute.getDisks().get(0).getStorage().getHref());
		assertEquals("/locations/uk-epcc/networks/0", createdCompute.getNics().get(0).getNetwork().getHref());
	}
	
	@Test
	public void resumeCompute() throws Exception {
		
		String returnedOCCIByTheAPI = "<compute xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"/locations/uk-epcc/computes/583\">"
											+ "<id>583</id>"
											+ "<cpu>0.25</cpu>"
											+ "<memory>256</memory>"
											+ "<uname href=\"/locations/uk-epcc/users/7\">dperez</uname>"
											+ "<groups>dperez</groups>"
											+ "<name>asdfasdfas</name>"
											+ "<instance_type href=\"/locations/uk-epcc/configurations/lite\">lite</instance_type>"
											+ "<state>RUNNING</state>"
											+ "<disk id=\"0\">"
												+ "<storage href=\"/locations/uk-epcc/storages/0\" name=\"BonFIRE Debian Squeeze v5\"/>"
												+ "<type>FILE</type>"
												+ "<target>sda</target>"
											+ "</disk>"
											+ "<nic>"
												+ "<network href=\"/locations/uk-epcc/networks/0\" name=\"BonFIRE WAN\"/>"
												+ "<ip>172.18.3.160</ip>"
												+ "<mac>02:00:ac:12:03:a0</mac>"
											+ "</nic>"
											+ "<context>"
												+ "<authorized_keys>ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQChu9trsHSNWBDcXP7C86Qi9v+9IbmkD42GkIT5wF42TtnZ/HvpiAmT2jPBD/b0CI4rnmbqwyB+TknAJjJEAb9C8Tgk7Sjgr/0JCbRZu74tzZkrCn/hiG+9bPQBTBv3sxwJ2p94wvrRrgMK21TwqMQ+FkmVBdk6Zp3PoSRtFLs0Nc95kFZlMeonfgSfOJy1dtkzhTAH8fu9HwrjwAAv0xIX+eQ+n3kQgsxQ6vrPdwjOt0n6h52FpYXcPhRiXmkHa5BEWxhvranzsCeTnUQk5RBmC/p6VqA/mFJOzRuKFPP2bRHaE8tcXVeADBQgrBPvZWsSiuXAdJ8bzP+WeprrTw61 A510804@ES-CNU21131QG</authorized_keys>"
												+ "<bonfire_credentials>dperez:g3uqkvz3czmpv79zusups</bonfire_credentials>"
												+ "<bonfire_experiment_aggregator_password>6afh7a</bonfire_experiment_aggregator_password>"
												+ "<bonfire_experiment_expiration_date>1369744294</bonfire_experiment_expiration_date>"
												+ "<bonfire_experiment_id>3032</bonfire_experiment_id>"
												+ "<bonfire_experiment_routing_key>3fdff0c64c51135f4e5e</bonfire_experiment_routing_key>"
												+ "<bonfire_provider>uk-epcc</bonfire_provider>"
												+ "<bonfire_resource_id>583</bonfire_resource_id>"
												+ "<bonfire_resource_name>asdfasdfas</bonfire_resource_name>"
												+ "<bonfire_uri>https://api.integration.bonfire.grid5000.fr</bonfire_uri>"
												+ "<dns_servers>172.18.6.1</dns_servers>"
												+ "<eth0_gateway>172.18.3.1</eth0_gateway>"
												+ "<eth0_ip>172.18.3.160</eth0_ip>"
												+ "<eth0_mask>255.255.255.0</eth0_mask>"
												+ "<eth0_name>BonFIRE WAN</eth0_name>"
												+ "<files>/srv/cloud/context /srv/cloud/context/lib /srv/cloud/context/distributions /srv/cloud/context/sites /srv/cloud/context/common /srv/cloud/context/init.sh</files>"
												+ "<hostname>asdfasdfas-583</hostname>"
												+ "<ntp_servers>129.215.175.254</ntp_servers>"
												+ "<public_gateway>129.215.175.254</public_gateway>"
												+ "<public_netmask>255.255.255.128</public_netmask>"
												+ "<target>sdb</target>"
												+ "<usage>zabbix-agent</usage>"
												+ "<wan_ip>172.18.3.160</wan_ip>"
												+ "<wan_mac>02:00:ac:12:03:a0</wan_mac>"
											+ "</context>"
											+ "<host>crockett0</host>"
											+ "<link rel=\"experiment\" href=\"/experiments/3032\" type=\"application/vnd.bonfire+xml\"/>"
										+ "</compute>";
		
		mServer.addPath("/locations/de-hlrs/computes/22260", returnedOCCIByTheAPI);
		
		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setUserName("username");
		client.setPassword("password");
		client.setUrl(mBaseURL);
		
		Compute compute = new Compute();
		compute.setHref("/locations/de-hlrs/computes/22260");
		compute.setId("22260");
		compute.setComputeName("computeName");
		compute.setGroups("dperez");
		Configuration configuration = new Configuration();
		configuration.setName("lite");
		compute.setConfiguration(configuration);
		Disk disk = new Disk();
		Storage storage = new Storage();
		storage.setHref("/locations/de-hlrs/storages/0");
		disk.setStorage(storage);
		disk.setType("OS");
		disk.setTarget("hda");
		compute.addDisk(disk);
		Link link = new Link();
		link.setHref("/locations/de-hlrs");
		link.setRel("location");
		compute.addLink(link);
		
		Compute changedStateCompute = client.resumeCompute(compute);
		
		String messageSentToBonFIREAPI = mServer.getRequestBody();
		
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(messageSentToBonFIREAPI));
		XPath xpath = XPath.newInstance("//bnf:compute");
		xpath.addNamespace("bnf", NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element element = (Element) listxpath.get(0);
		assertEquals("/locations/de-hlrs/computes/22260", element.getAttributeValue("href"));
		
		xpath = XPath.newInstance("//bnf:state");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("resume", element.getValue());

		
		xpath = XPath.newInstance("//bnf:link");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("/locations/de-hlrs", element.getAttributeValue("href"));
		assertEquals("location", element.getAttributeValue("rel"));		
		
		assertEquals("583", changedStateCompute.getId());
	}
	
	@Test
	public void stopCompute() throws Exception {
		
		String returnedOCCIByTheAPI = "<compute xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"/locations/uk-epcc/computes/583\">"
											+ "<id>583</id>"
											+ "<cpu>0.25</cpu>"
											+ "<memory>256</memory>"
											+ "<uname href=\"/locations/uk-epcc/users/7\">dperez</uname>"
											+ "<groups>dperez</groups>"
											+ "<name>asdfasdfas</name>"
											+ "<instance_type href=\"/locations/uk-epcc/configurations/lite\">lite</instance_type>"
											+ "<state>RUNNING</state>"
											+ "<disk id=\"0\">"
												+ "<storage href=\"/locations/uk-epcc/storages/0\" name=\"BonFIRE Debian Squeeze v5\"/>"
												+ "<type>FILE</type>"
												+ "<target>sda</target>"
											+ "</disk>"
											+ "<nic>"
												+ "<network href=\"/locations/uk-epcc/networks/0\" name=\"BonFIRE WAN\"/>"
												+ "<ip>172.18.3.160</ip>"
												+ "<mac>02:00:ac:12:03:a0</mac>"
											+ "</nic>"
											+ "<context>"
												+ "<authorized_keys>ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQChu9trsHSNWBDcXP7C86Qi9v+9IbmkD42GkIT5wF42TtnZ/HvpiAmT2jPBD/b0CI4rnmbqwyB+TknAJjJEAb9C8Tgk7Sjgr/0JCbRZu74tzZkrCn/hiG+9bPQBTBv3sxwJ2p94wvrRrgMK21TwqMQ+FkmVBdk6Zp3PoSRtFLs0Nc95kFZlMeonfgSfOJy1dtkzhTAH8fu9HwrjwAAv0xIX+eQ+n3kQgsxQ6vrPdwjOt0n6h52FpYXcPhRiXmkHa5BEWxhvranzsCeTnUQk5RBmC/p6VqA/mFJOzRuKFPP2bRHaE8tcXVeADBQgrBPvZWsSiuXAdJ8bzP+WeprrTw61 A510804@ES-CNU21131QG</authorized_keys>"
												+ "<bonfire_credentials>dperez:g3uqkvz3czmpv79zusups</bonfire_credentials>"
												+ "<bonfire_experiment_aggregator_password>6afh7a</bonfire_experiment_aggregator_password>"
												+ "<bonfire_experiment_expiration_date>1369744294</bonfire_experiment_expiration_date>"
												+ "<bonfire_experiment_id>3032</bonfire_experiment_id>"
												+ "<bonfire_experiment_routing_key>3fdff0c64c51135f4e5e</bonfire_experiment_routing_key>"
												+ "<bonfire_provider>uk-epcc</bonfire_provider>"
												+ "<bonfire_resource_id>583</bonfire_resource_id>"
												+ "<bonfire_resource_name>asdfasdfas</bonfire_resource_name>"
												+ "<bonfire_uri>https://api.integration.bonfire.grid5000.fr</bonfire_uri>"
												+ "<dns_servers>172.18.6.1</dns_servers>"
												+ "<eth0_gateway>172.18.3.1</eth0_gateway>"
												+ "<eth0_ip>172.18.3.160</eth0_ip>"
												+ "<eth0_mask>255.255.255.0</eth0_mask>"
												+ "<eth0_name>BonFIRE WAN</eth0_name>"
												+ "<files>/srv/cloud/context /srv/cloud/context/lib /srv/cloud/context/distributions /srv/cloud/context/sites /srv/cloud/context/common /srv/cloud/context/init.sh</files>"
												+ "<hostname>asdfasdfas-583</hostname>"
												+ "<ntp_servers>129.215.175.254</ntp_servers>"
												+ "<public_gateway>129.215.175.254</public_gateway>"
												+ "<public_netmask>255.255.255.128</public_netmask>"
												+ "<target>sdb</target>"
												+ "<usage>zabbix-agent</usage>"
												+ "<wan_ip>172.18.3.160</wan_ip>"
												+ "<wan_mac>02:00:ac:12:03:a0</wan_mac>"
											+ "</context>"
											+ "<host>crockett0</host>"
											+ "<link rel=\"experiment\" href=\"/experiments/3032\" type=\"application/vnd.bonfire+xml\"/>"
										+ "</compute>";
		
		mServer.addPath("/locations/de-hlrs/computes/22260", returnedOCCIByTheAPI);
		
		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setUserName("username");
		client.setPassword("password");
		client.setUrl(mBaseURL);
		
		Compute compute = new Compute();
		compute.setHref("/locations/de-hlrs/computes/22260");
		compute.setId("22260");
		compute.setComputeName("computeName");
		compute.setGroups("dperez");
		Configuration configuration = new Configuration();
		configuration.setName("lite");
		compute.setConfiguration(configuration);
		Disk disk = new Disk();
		Storage storage = new Storage();
		storage.setHref("/locations/de-hlrs/storages/0");
		disk.setStorage(storage);
		disk.setType("OS");
		disk.setTarget("hda");
		compute.addDisk(disk);
		Link link = new Link();
		link.setHref("/locations/de-hlrs");
		link.setRel("location");
		compute.addLink(link);
		
		Compute changedStateCompute = client.stopCompute(compute);
		
		String messageSentToBonFIREAPI = mServer.getRequestBody();
		
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(messageSentToBonFIREAPI));
		XPath xpath = XPath.newInstance("//bnf:compute");
		xpath.addNamespace("bnf", NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element element = (Element) listxpath.get(0);
		assertEquals("/locations/de-hlrs/computes/22260", element.getAttributeValue("href"));
		
		xpath = XPath.newInstance("//bnf:state");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("stopped", element.getValue());

		
		xpath = XPath.newInstance("//bnf:link");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("/locations/de-hlrs", element.getAttributeValue("href"));
		assertEquals("location", element.getAttributeValue("rel"));		
		
		assertEquals("583", changedStateCompute.getId());
	}
	
	@Test
	public void cancelCompute() throws Exception {
		
		String returnedOCCIByTheAPI = "<compute xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"/locations/uk-epcc/computes/583\">"
											+ "<id>583</id>"
											+ "<cpu>0.25</cpu>"
											+ "<memory>256</memory>"
											+ "<uname href=\"/locations/uk-epcc/users/7\">dperez</uname>"
											+ "<groups>dperez</groups>"
											+ "<name>asdfasdfas</name>"
											+ "<instance_type href=\"/locations/uk-epcc/configurations/lite\">lite</instance_type>"
											+ "<state>RUNNING</state>"
											+ "<disk id=\"0\">"
												+ "<storage href=\"/locations/uk-epcc/storages/0\" name=\"BonFIRE Debian Squeeze v5\"/>"
												+ "<type>FILE</type>"
												+ "<target>sda</target>"
											+ "</disk>"
											+ "<nic>"
												+ "<network href=\"/locations/uk-epcc/networks/0\" name=\"BonFIRE WAN\"/>"
												+ "<ip>172.18.3.160</ip>"
												+ "<mac>02:00:ac:12:03:a0</mac>"
											+ "</nic>"
											+ "<context>"
												+ "<authorized_keys>ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQChu9trsHSNWBDcXP7C86Qi9v+9IbmkD42GkIT5wF42TtnZ/HvpiAmT2jPBD/b0CI4rnmbqwyB+TknAJjJEAb9C8Tgk7Sjgr/0JCbRZu74tzZkrCn/hiG+9bPQBTBv3sxwJ2p94wvrRrgMK21TwqMQ+FkmVBdk6Zp3PoSRtFLs0Nc95kFZlMeonfgSfOJy1dtkzhTAH8fu9HwrjwAAv0xIX+eQ+n3kQgsxQ6vrPdwjOt0n6h52FpYXcPhRiXmkHa5BEWxhvranzsCeTnUQk5RBmC/p6VqA/mFJOzRuKFPP2bRHaE8tcXVeADBQgrBPvZWsSiuXAdJ8bzP+WeprrTw61 A510804@ES-CNU21131QG</authorized_keys>"
												+ "<bonfire_credentials>dperez:g3uqkvz3czmpv79zusups</bonfire_credentials>"
												+ "<bonfire_experiment_aggregator_password>6afh7a</bonfire_experiment_aggregator_password>"
												+ "<bonfire_experiment_expiration_date>1369744294</bonfire_experiment_expiration_date>"
												+ "<bonfire_experiment_id>3032</bonfire_experiment_id>"
												+ "<bonfire_experiment_routing_key>3fdff0c64c51135f4e5e</bonfire_experiment_routing_key>"
												+ "<bonfire_provider>uk-epcc</bonfire_provider>"
												+ "<bonfire_resource_id>583</bonfire_resource_id>"
												+ "<bonfire_resource_name>asdfasdfas</bonfire_resource_name>"
												+ "<bonfire_uri>https://api.integration.bonfire.grid5000.fr</bonfire_uri>"
												+ "<dns_servers>172.18.6.1</dns_servers>"
												+ "<eth0_gateway>172.18.3.1</eth0_gateway>"
												+ "<eth0_ip>172.18.3.160</eth0_ip>"
												+ "<eth0_mask>255.255.255.0</eth0_mask>"
												+ "<eth0_name>BonFIRE WAN</eth0_name>"
												+ "<files>/srv/cloud/context /srv/cloud/context/lib /srv/cloud/context/distributions /srv/cloud/context/sites /srv/cloud/context/common /srv/cloud/context/init.sh</files>"
												+ "<hostname>asdfasdfas-583</hostname>"
												+ "<ntp_servers>129.215.175.254</ntp_servers>"
												+ "<public_gateway>129.215.175.254</public_gateway>"
												+ "<public_netmask>255.255.255.128</public_netmask>"
												+ "<target>sdb</target>"
												+ "<usage>zabbix-agent</usage>"
												+ "<wan_ip>172.18.3.160</wan_ip>"
												+ "<wan_mac>02:00:ac:12:03:a0</wan_mac>"
											+ "</context>"
											+ "<host>crockett0</host>"
											+ "<link rel=\"experiment\" href=\"/experiments/3032\" type=\"application/vnd.bonfire+xml\"/>"
										+ "</compute>";
		
		mServer.addPath("/locations/de-hlrs/computes/22260", returnedOCCIByTheAPI);
		
		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setUserName("username");
		client.setPassword("password");
		client.setUrl(mBaseURL);
		
		Compute compute = new Compute();
		compute.setHref("/locations/de-hlrs/computes/22260");
		compute.setId("22260");
		compute.setComputeName("computeName");
		compute.setGroups("dperez");
		Configuration configuration = new Configuration();
		configuration.setName("lite");
		compute.setConfiguration(configuration);
		Disk disk = new Disk();
		Storage storage = new Storage();
		storage.setHref("/locations/de-hlrs/storages/0");
		disk.setStorage(storage);
		disk.setType("OS");
		disk.setTarget("hda");
		compute.addDisk(disk);
		Link link = new Link();
		link.setHref("/locations/de-hlrs");
		link.setRel("location");
		compute.addLink(link);
		
		Compute changedStateCompute = client.cancelCompute(compute);
		
		String messageSentToBonFIREAPI = mServer.getRequestBody();
		
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(messageSentToBonFIREAPI));
		XPath xpath = XPath.newInstance("//bnf:compute");
		xpath.addNamespace("bnf", NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element element = (Element) listxpath.get(0);
		assertEquals("/locations/de-hlrs/computes/22260", element.getAttributeValue("href"));
		
		xpath = XPath.newInstance("//bnf:state");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("cancel", element.getValue());

		
		xpath = XPath.newInstance("//bnf:link");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("/locations/de-hlrs", element.getAttributeValue("href"));
		assertEquals("location", element.getAttributeValue("rel"));		
		
		assertEquals("583", changedStateCompute.getId());
	}
	
	@Test
	public void shutdownCompute() throws Exception {
		
		String returnedOCCIByTheAPI = "<compute xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"/locations/uk-epcc/computes/583\">"
											+ "<id>583</id>"
											+ "<cpu>0.25</cpu>"
											+ "<memory>256</memory>"
											+ "<uname href=\"/locations/uk-epcc/users/7\">dperez</uname>"
											+ "<groups>dperez</groups>"
											+ "<name>asdfasdfas</name>"
											+ "<instance_type href=\"/locations/uk-epcc/configurations/lite\">lite</instance_type>"
											+ "<state>RUNNING</state>"
											+ "<disk id=\"0\">"
												+ "<storage href=\"/locations/uk-epcc/storages/0\" name=\"BonFIRE Debian Squeeze v5\"/>"
												+ "<type>FILE</type>"
												+ "<target>sda</target>"
											+ "</disk>"
											+ "<nic>"
												+ "<network href=\"/locations/uk-epcc/networks/0\" name=\"BonFIRE WAN\"/>"
												+ "<ip>172.18.3.160</ip>"
												+ "<mac>02:00:ac:12:03:a0</mac>"
											+ "</nic>"
											+ "<context>"
												+ "<authorized_keys>ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQChu9trsHSNWBDcXP7C86Qi9v+9IbmkD42GkIT5wF42TtnZ/HvpiAmT2jPBD/b0CI4rnmbqwyB+TknAJjJEAb9C8Tgk7Sjgr/0JCbRZu74tzZkrCn/hiG+9bPQBTBv3sxwJ2p94wvrRrgMK21TwqMQ+FkmVBdk6Zp3PoSRtFLs0Nc95kFZlMeonfgSfOJy1dtkzhTAH8fu9HwrjwAAv0xIX+eQ+n3kQgsxQ6vrPdwjOt0n6h52FpYXcPhRiXmkHa5BEWxhvranzsCeTnUQk5RBmC/p6VqA/mFJOzRuKFPP2bRHaE8tcXVeADBQgrBPvZWsSiuXAdJ8bzP+WeprrTw61 A510804@ES-CNU21131QG</authorized_keys>"
												+ "<bonfire_credentials>dperez:g3uqkvz3czmpv79zusups</bonfire_credentials>"
												+ "<bonfire_experiment_aggregator_password>6afh7a</bonfire_experiment_aggregator_password>"
												+ "<bonfire_experiment_expiration_date>1369744294</bonfire_experiment_expiration_date>"
												+ "<bonfire_experiment_id>3032</bonfire_experiment_id>"
												+ "<bonfire_experiment_routing_key>3fdff0c64c51135f4e5e</bonfire_experiment_routing_key>"
												+ "<bonfire_provider>uk-epcc</bonfire_provider>"
												+ "<bonfire_resource_id>583</bonfire_resource_id>"
												+ "<bonfire_resource_name>asdfasdfas</bonfire_resource_name>"
												+ "<bonfire_uri>https://api.integration.bonfire.grid5000.fr</bonfire_uri>"
												+ "<dns_servers>172.18.6.1</dns_servers>"
												+ "<eth0_gateway>172.18.3.1</eth0_gateway>"
												+ "<eth0_ip>172.18.3.160</eth0_ip>"
												+ "<eth0_mask>255.255.255.0</eth0_mask>"
												+ "<eth0_name>BonFIRE WAN</eth0_name>"
												+ "<files>/srv/cloud/context /srv/cloud/context/lib /srv/cloud/context/distributions /srv/cloud/context/sites /srv/cloud/context/common /srv/cloud/context/init.sh</files>"
												+ "<hostname>asdfasdfas-583</hostname>"
												+ "<ntp_servers>129.215.175.254</ntp_servers>"
												+ "<public_gateway>129.215.175.254</public_gateway>"
												+ "<public_netmask>255.255.255.128</public_netmask>"
												+ "<target>sdb</target>"
												+ "<usage>zabbix-agent</usage>"
												+ "<wan_ip>172.18.3.160</wan_ip>"
												+ "<wan_mac>02:00:ac:12:03:a0</wan_mac>"
											+ "</context>"
											+ "<host>crockett0</host>"
											+ "<link rel=\"experiment\" href=\"/experiments/3032\" type=\"application/vnd.bonfire+xml\"/>"
										+ "</compute>";
		
		mServer.addPath("/locations/de-hlrs/computes/22260", returnedOCCIByTheAPI);
		
		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setUserName("username");
		client.setPassword("password");
		client.setUrl(mBaseURL);
		
		Compute compute = new Compute();
		compute.setHref("/locations/de-hlrs/computes/22260");
		compute.setId("22260");
		compute.setComputeName("computeName");
		compute.setGroups("dperez");
		Configuration configuration = new Configuration();
		configuration.setName("lite");
		compute.setConfiguration(configuration);
		Disk disk = new Disk();
		Storage storage = new Storage();
		storage.setHref("/locations/de-hlrs/storages/0");
		disk.setStorage(storage);
		disk.setType("OS");
		disk.setTarget("hda");
		compute.addDisk(disk);
		Link link = new Link();
		link.setHref("/locations/de-hlrs");
		link.setRel("location");
		compute.addLink(link);
		
		Compute changedStateCompute = client.shutdownCompute(compute);
		
		String messageSentToBonFIREAPI = mServer.getRequestBody();
		
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(messageSentToBonFIREAPI));
		XPath xpath = XPath.newInstance("//bnf:compute");
		xpath.addNamespace("bnf", NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element element = (Element) listxpath.get(0);
		assertEquals("/locations/de-hlrs/computes/22260", element.getAttributeValue("href"));
		
		xpath = XPath.newInstance("//bnf:state");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("shutdown", element.getValue());

		
		xpath = XPath.newInstance("//bnf:link");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("/locations/de-hlrs", element.getAttributeValue("href"));
		assertEquals("location", element.getAttributeValue("rel"));		
		
		assertEquals("583", changedStateCompute.getId());
	}
	
	@Test
	public void changeStateOfComputeToSuspendend() throws Exception {
		
		String returnedOCCIByTheAPI = "<compute xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"/locations/uk-epcc/computes/583\">"
											+ "<id>583</id>"
											+ "<cpu>0.25</cpu>"
											+ "<memory>256</memory>"
											+ "<uname href=\"/locations/uk-epcc/users/7\">dperez</uname>"
											+ "<groups>dperez</groups>"
											+ "<name>asdfasdfas</name>"
											+ "<instance_type href=\"/locations/uk-epcc/configurations/lite\">lite</instance_type>"
											+ "<state>SUSPENDED</state>"
											+ "<disk id=\"0\">"
												+ "<storage href=\"/locations/uk-epcc/storages/0\" name=\"BonFIRE Debian Squeeze v5\"/>"
												+ "<type>FILE</type>"
												+ "<target>sda</target>"
											+ "</disk>"
											+ "<nic>"
												+ "<network href=\"/locations/uk-epcc/networks/0\" name=\"BonFIRE WAN\"/>"
												+ "<ip>172.18.3.160</ip>"
												+ "<mac>02:00:ac:12:03:a0</mac>"
											+ "</nic>"
											+ "<context>"
												+ "<authorized_keys>ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQChu9trsHSNWBDcXP7C86Qi9v+9IbmkD42GkIT5wF42TtnZ/HvpiAmT2jPBD/b0CI4rnmbqwyB+TknAJjJEAb9C8Tgk7Sjgr/0JCbRZu74tzZkrCn/hiG+9bPQBTBv3sxwJ2p94wvrRrgMK21TwqMQ+FkmVBdk6Zp3PoSRtFLs0Nc95kFZlMeonfgSfOJy1dtkzhTAH8fu9HwrjwAAv0xIX+eQ+n3kQgsxQ6vrPdwjOt0n6h52FpYXcPhRiXmkHa5BEWxhvranzsCeTnUQk5RBmC/p6VqA/mFJOzRuKFPP2bRHaE8tcXVeADBQgrBPvZWsSiuXAdJ8bzP+WeprrTw61 A510804@ES-CNU21131QG</authorized_keys>"
												+ "<bonfire_credentials>dperez:g3uqkvz3czmpv79zusups</bonfire_credentials>"
												+ "<bonfire_experiment_aggregator_password>6afh7a</bonfire_experiment_aggregator_password>"
												+ "<bonfire_experiment_expiration_date>1369744294</bonfire_experiment_expiration_date>"
												+ "<bonfire_experiment_id>3032</bonfire_experiment_id>"
												+ "<bonfire_experiment_routing_key>3fdff0c64c51135f4e5e</bonfire_experiment_routing_key>"
												+ "<bonfire_provider>uk-epcc</bonfire_provider>"
												+ "<bonfire_resource_id>583</bonfire_resource_id>"
												+ "<bonfire_resource_name>asdfasdfas</bonfire_resource_name>"
												+ "<bonfire_uri>https://api.integration.bonfire.grid5000.fr</bonfire_uri>"
												+ "<dns_servers>172.18.6.1</dns_servers>"
												+ "<eth0_gateway>172.18.3.1</eth0_gateway>"
												+ "<eth0_ip>172.18.3.160</eth0_ip>"
												+ "<eth0_mask>255.255.255.0</eth0_mask>"
												+ "<eth0_name>BonFIRE WAN</eth0_name>"
												+ "<files>/srv/cloud/context /srv/cloud/context/lib /srv/cloud/context/distributions /srv/cloud/context/sites /srv/cloud/context/common /srv/cloud/context/init.sh</files>"
												+ "<hostname>asdfasdfas-583</hostname>"
												+ "<ntp_servers>129.215.175.254</ntp_servers>"
												+ "<public_gateway>129.215.175.254</public_gateway>"
												+ "<public_netmask>255.255.255.128</public_netmask>"
												+ "<target>sdb</target>"
												+ "<usage>zabbix-agent</usage>"
												+ "<wan_ip>172.18.3.160</wan_ip>"
												+ "<wan_mac>02:00:ac:12:03:a0</wan_mac>"
											+ "</context>"
											+ "<host>crockett0</host>"
											+ "<link rel=\"experiment\" href=\"/experiments/3032\" type=\"application/vnd.bonfire+xml\"/>"
										+ "</compute>";
		
		mServer.addPath("/locations/de-hlrs/computes/22260", returnedOCCIByTheAPI);
		
		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setUserName("username");
		client.setPassword("password");
		client.setUrl(mBaseURL);
		
		Compute compute = new Compute();
		compute.setHref("/locations/de-hlrs/computes/22260");
		compute.setId("22260");
		compute.setComputeName("computeName");
		compute.setGroups("dperez");
		Configuration configuration = new Configuration();
		configuration.setName("lite");
		compute.setConfiguration(configuration);
		Disk disk = new Disk();
		Storage storage = new Storage();
		storage.setHref("/locations/de-hlrs/storages/0");
		disk.setStorage(storage);
		disk.setType("OS");
		disk.setTarget("hda");
		compute.addDisk(disk);
		Link link = new Link();
		link.setHref("/locations/de-hlrs");
		link.setRel("location");
		compute.addLink(link);
		
		Compute changedStateCompute = client.suspendCompute(compute);
		
		String messageSentToBonFIREAPI = mServer.getRequestBody();
		
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(messageSentToBonFIREAPI));
		XPath xpath = XPath.newInstance("//bnf:compute");
		xpath.addNamespace("bnf", NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element element = (Element) listxpath.get(0);
		assertEquals("/locations/de-hlrs/computes/22260", element.getAttributeValue("href"));
		
		xpath = XPath.newInstance("//bnf:state");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("suspended", element.getValue());

		
		xpath = XPath.newInstance("//bnf:link");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("/locations/de-hlrs", element.getAttributeValue("href"));
		assertEquals("location", element.getAttributeValue("rel"));		
		
		assertEquals("583", changedStateCompute.getId());
	}
	
	@Test
	public void refreshCompute() throws Exception {
		String returnedOCCIBytheAPI = "<compute xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"/locations/uk-epcc/computes/583\">"
										+ "<id>583</id>"
										+ "<cpu>0.25</cpu>"
										+ "<memory>256</memory>"
										+ "<uname href=\"/locations/uk-epcc/users/7\">dperez</uname>"
										+ "<groups>dperez</groups>"
										+ "<name>asdfasdfas</name>"
										+ "<instance_type href=\"/locations/uk-epcc/configurations/lite\">lite</instance_type>"
										+ "<state>RUNNING</state>"
										+ "<disk id=\"0\">"
											+ "<storage href=\"/locations/uk-epcc/storages/0\" name=\"BonFIRE Debian Squeeze v5\"/>"
											+ "<type>FILE</type>"
											+ "<target>sda</target>"
										+ "</disk>"
										+ "<nic>"
											+ "<network href=\"/locations/uk-epcc/networks/0\" name=\"BonFIRE WAN\"/>"
											+ "<ip>172.18.3.160</ip>"
											+ "<mac>02:00:ac:12:03:a0</mac>"
										+ "</nic>"
										+ "<context>"
											+ "<authorized_keys>ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQChu9trsHSNWBDcXP7C86Qi9v+9IbmkD42GkIT5wF42TtnZ/HvpiAmT2jPBD/b0CI4rnmbqwyB+TknAJjJEAb9C8Tgk7Sjgr/0JCbRZu74tzZkrCn/hiG+9bPQBTBv3sxwJ2p94wvrRrgMK21TwqMQ+FkmVBdk6Zp3PoSRtFLs0Nc95kFZlMeonfgSfOJy1dtkzhTAH8fu9HwrjwAAv0xIX+eQ+n3kQgsxQ6vrPdwjOt0n6h52FpYXcPhRiXmkHa5BEWxhvranzsCeTnUQk5RBmC/p6VqA/mFJOzRuKFPP2bRHaE8tcXVeADBQgrBPvZWsSiuXAdJ8bzP+WeprrTw61 A510804@ES-CNU21131QG</authorized_keys>"
											+ "<bonfire_credentials>dperez:g3uqkvz3czmpv79zusups</bonfire_credentials>"
											+ "<bonfire_experiment_aggregator_password>6afh7a</bonfire_experiment_aggregator_password>"
											+ "<bonfire_experiment_expiration_date>1369744294</bonfire_experiment_expiration_date>"
											+ "<bonfire_experiment_id>3032</bonfire_experiment_id>"
											+ "<bonfire_experiment_routing_key>3fdff0c64c51135f4e5e</bonfire_experiment_routing_key>"
											+ "<bonfire_provider>uk-epcc</bonfire_provider>"
											+ "<bonfire_resource_id>583</bonfire_resource_id>"
											+ "<bonfire_resource_name>asdfasdfas</bonfire_resource_name>"
											+ "<bonfire_uri>https://api.integration.bonfire.grid5000.fr</bonfire_uri>"
											+ "<dns_servers>172.18.6.1</dns_servers>"
											+ "<eth0_gateway>172.18.3.1</eth0_gateway>"
											+ "<eth0_ip>172.18.3.160</eth0_ip>"
											+ "<eth0_mask>255.255.255.0</eth0_mask>"
											+ "<eth0_name>BonFIRE WAN</eth0_name>"
											+ "<files>/srv/cloud/context /srv/cloud/context/lib /srv/cloud/context/distributions /srv/cloud/context/sites /srv/cloud/context/common /srv/cloud/context/init.sh</files>"
											+ "<hostname>asdfasdfas-583</hostname>"
											+ "<ntp_servers>129.215.175.254</ntp_servers>"
											+ "<public_gateway>129.215.175.254</public_gateway>"
											+ "<public_netmask>255.255.255.128</public_netmask>"
											+ "<target>sdb</target>"
											+ "<usage>zabbix-agent</usage>"
											+ "<wan_ip>172.18.3.160</wan_ip>"
											+ "<wan_mac>02:00:ac:12:03:a0</wan_mac>"
										+ "</context>"
										+ "<host>crockett0</host>"
										+ "<link rel=\"experiment\" href=\"/experiments/3032\" type=\"application/vnd.bonfire+xml\"/>"
									+ "</compute>";
		
		mServer.addPath("/locations/de-hlrs/computes/22260", returnedOCCIBytheAPI);
		
		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setUserName("username");
		client.setPassword("password");
		client.setUrl(mBaseURL);
		
		Compute compute = new Compute();
		compute.setHref("/locations/de-hlrs/computes/22260");
		
		Compute refreshedCompute = client.refreshCompute(compute);	
		
		assertEquals("583", refreshedCompute.getId());
	}
	
	@Test
	public void creationOfAComputeInBonFIREWAN() throws Exception {
		
		String xmlCollectionOfNetwroks = "<?xml version=\'1.0\' encoding=\'UTF-8\'?>"
				 						+ "<collection xmlns=\'http://api.bonfire-project.eu/doc/schemas/occi\'>"
				 							+ "<items>"
				 								+ "<network href=\"/locations/uk-epcc/networks/0\" name=\"BonFIRE WAN\">"
				 									+ "<name>BonFIRE WAN</name>"
				 									+ "<uname>admin</uname>"
				 									+ "<link href=\"/locations/uk-epcc\" rel=\"location\"/>"
				 									+ "<link rel=\"experiment\" type=\"application/vnd.bonfire+xml\"/>"
				 								+ "</network>"
				 								+ "<network href=\"/locations/uk-epcc/networks/1\" name=\"Public Internet\">"
			 										+ "<name>Public Internet</name>"
			 										+ "<uname>admin</uname>"
			 										+ "<link href=\"/locations/uk-epcc\" rel=\"location\"/>"
			 										+ "<link rel=\"experiment\" type=\"application/vnd.bonfire+xml\"/>"
			 									+ "</network>"
				 							+ "</items>"
				 						+ "</collection>";

		mServer.addPath("/locations/uk-epcc/networks", xmlCollectionOfNetwroks);
		
		String returnedOCCIBytheAPI = "<compute xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"/locations/uk-epcc/computes/583\">"
										+ "<id>583</id>"
										+ "<cpu>0.25</cpu>"
										+ "<memory>256</memory>"
										+ "<uname href=\"/locations/uk-epcc/users/7\">dperez</uname>"
										+ "<groups>dperez</groups>"
										+ "<name>asdfasdfas</name>"
										+ "<instance_type href=\"/locations/uk-epcc/configurations/lite\">lite</instance_type>"
										+ "<state>RUNNING</state>"
										+ "<disk id=\"0\">"
											+ "<storage href=\"/locations/uk-epcc/storages/0\" name=\"BonFIRE Debian Squeeze v5\"/>"
											+ "<type>FILE</type>"
											+ "<target>sda</target>"
										+ "</disk>"
										+ "<nic>"
											+ "<network href=\"/locations/uk-epcc/networks/0\" name=\"BonFIRE WAN\"/>"
											+ "<ip>172.18.3.160</ip>"
											+ "<mac>02:00:ac:12:03:a0</mac>"
										+ "</nic>"
										+ "<context>"
											+ "<authorized_keys>ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQChu9trsHSNWBDcXP7C86Qi9v+9IbmkD42GkIT5wF42TtnZ/HvpiAmT2jPBD/b0CI4rnmbqwyB+TknAJjJEAb9C8Tgk7Sjgr/0JCbRZu74tzZkrCn/hiG+9bPQBTBv3sxwJ2p94wvrRrgMK21TwqMQ+FkmVBdk6Zp3PoSRtFLs0Nc95kFZlMeonfgSfOJy1dtkzhTAH8fu9HwrjwAAv0xIX+eQ+n3kQgsxQ6vrPdwjOt0n6h52FpYXcPhRiXmkHa5BEWxhvranzsCeTnUQk5RBmC/p6VqA/mFJOzRuKFPP2bRHaE8tcXVeADBQgrBPvZWsSiuXAdJ8bzP+WeprrTw61 A510804@ES-CNU21131QG</authorized_keys>"
											+ "<bonfire_credentials>dperez:g3uqkvz3czmpv79zusups</bonfire_credentials>"
											+ "<bonfire_experiment_aggregator_password>6afh7a</bonfire_experiment_aggregator_password>"
											+ "<bonfire_experiment_expiration_date>1369744294</bonfire_experiment_expiration_date>"
											+ "<bonfire_experiment_id>3032</bonfire_experiment_id>"
											+ "<bonfire_experiment_routing_key>3fdff0c64c51135f4e5e</bonfire_experiment_routing_key>"
											+ "<bonfire_provider>uk-epcc</bonfire_provider>"
											+ "<bonfire_resource_id>583</bonfire_resource_id>"
											+ "<bonfire_resource_name>asdfasdfas</bonfire_resource_name>"
											+ "<bonfire_uri>https://api.integration.bonfire.grid5000.fr</bonfire_uri>"
											+ "<dns_servers>172.18.6.1</dns_servers>"
											+ "<eth0_gateway>172.18.3.1</eth0_gateway>"
											+ "<eth0_ip>172.18.3.160</eth0_ip>"
											+ "<eth0_mask>255.255.255.0</eth0_mask>"
											+ "<eth0_name>BonFIRE WAN</eth0_name>"
											+ "<files>/srv/cloud/context /srv/cloud/context/lib /srv/cloud/context/distributions /srv/cloud/context/sites /srv/cloud/context/common /srv/cloud/context/init.sh</files>"
											+ "<hostname>asdfasdfas-583</hostname>"
											+ "<ntp_servers>129.215.175.254</ntp_servers>"
											+ "<public_gateway>129.215.175.254</public_gateway>"
											+ "<public_netmask>255.255.255.128</public_netmask>"
											+ "<target>sdb</target>"
											+ "<usage>zabbix-agent</usage>"
											+ "<wan_ip>172.18.3.160</wan_ip>"
											+ "<wan_mac>02:00:ac:12:03:a0</wan_mac>"
										+ "</context>"
										+ "<host>crockett0</host>"
										+ "<link rel=\"experiment\" href=\"/experiments/3032\" type=\"application/vnd.bonfire+xml\"/>"
									+ "</compute>";
		
		mServer.addPath("/experiments/211/computes", returnedOCCIBytheAPI);
		
		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setUserName("username");
		client.setPassword("password");
		client.setUrl(mBaseURL);
		
		Experiment experiment = new Experiment();
		experiment.setId("211");
		experiment.setHref("/experiments/211");
		
		Compute compute = new Compute();
		compute.setComputeName("computeName");
		compute.setGroups("dperez");
		Configuration configuration = new Configuration();
		configuration.setName("lite");
		compute.setConfiguration(configuration);
		Disk disk = new Disk();
		Storage storage = new Storage();
		storage.setHref("/locations/uk-epcc/storages/0");
		disk.setStorage(storage);
		disk.setType("OS");
		disk.setTarget("hda");
		compute.addDisk(disk);
		
		Location location = new Location();
		location.setHref("/locations/uk-epcc");

		Compute createdCompute = client.createComputeAtBonFIREWAN(location, experiment, compute);
		
		String messageSentToBonFIREAPI = mServer.getRequestBody();
		
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(messageSentToBonFIREAPI));
		XPath xpath = XPath.newInstance("//bnf:compute");
		xpath.addNamespace("bnf", NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		
		xpath = XPath.newInstance("//bnf:name");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element element = (Element) listxpath.get(0);
		assertEquals("computeName", element.getValue());
		
		xpath = XPath.newInstance("//bnf:groups");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("dperez", element.getValue());
		
		xpath = XPath.newInstance("//bnf:instance_type");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("lite", element.getValue());

		xpath = XPath.newInstance("//bnf:storage");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("/locations/uk-epcc/storages/0", element.getAttributeValue("href"));
		
		xpath = XPath.newInstance("//bnf:network");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("/locations/uk-epcc/networks/0", element.getAttributeValue("href"));
		
		xpath = XPath.newInstance("//bnf:target");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("hda", element.getValue());
		
		xpath = XPath.newInstance("//bnf:link");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("/locations/uk-epcc", element.getAttributeValue("href"));
		assertEquals("location", element.getAttributeValue("rel"));		
		
		assertEquals("583", createdCompute.getId());
		assertEquals("256", createdCompute.getMemory());
		assertEquals("0.25", createdCompute.getCpu());
		assertEquals("dperez", createdCompute.getGroups());
		assertEquals("/locations/uk-epcc/storages/0", createdCompute.getDisks().get(0).getStorage().getHref());
		assertEquals("/locations/uk-epcc/networks/0", createdCompute.getNics().get(0).getNetwork().getHref());
	}
	
	@Test
	public void creationOfAComputeInBonFIREWANWithExperimentId() throws Exception {
		
		String xmlCollectionOfNetwroks = "<?xml version=\'1.0\' encoding=\'UTF-8\'?>"
				 						+ "<collection xmlns=\'http://api.bonfire-project.eu/doc/schemas/occi\'>"
				 							+ "<items>"
				 								+ "<network href=\"/locations/uk-epcc/networks/0\" name=\"BonFIRE WAN\">"
				 									+ "<name>BonFIRE WAN</name>"
				 									+ "<uname>admin</uname>"
				 									+ "<link href=\"/locations/uk-epcc\" rel=\"location\"/>"
				 									+ "<link rel=\"experiment\" type=\"application/vnd.bonfire+xml\"/>"
				 								+ "</network>"
				 								+ "<network href=\"/locations/uk-epcc/networks/1\" name=\"Public Internet\">"
			 										+ "<name>Public Internet</name>"
			 										+ "<uname>admin</uname>"
			 										+ "<link href=\"/locations/uk-epcc\" rel=\"location\"/>"
			 										+ "<link rel=\"experiment\" type=\"application/vnd.bonfire+xml\"/>"
			 									+ "</network>"
				 							+ "</items>"
				 						+ "</collection>";

		mServer.addPath("/locations/uk-epcc/networks", xmlCollectionOfNetwroks);
		
		String returnedOCCIBytheAPI = "<compute xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"/locations/uk-epcc/computes/583\">"
										+ "<id>583</id>"
										+ "<cpu>0.25</cpu>"
										+ "<memory>256</memory>"
										+ "<uname href=\"/locations/uk-epcc/users/7\">dperez</uname>"
										+ "<groups>dperez</groups>"
										+ "<name>asdfasdfas</name>"
										+ "<instance_type href=\"/locations/uk-epcc/configurations/lite\">lite</instance_type>"
										+ "<state>RUNNING</state>"
										+ "<disk id=\"0\">"
											+ "<storage href=\"/locations/uk-epcc/storages/0\" name=\"BonFIRE Debian Squeeze v5\"/>"
											+ "<type>FILE</type>"
											+ "<target>sda</target>"
										+ "</disk>"
										+ "<nic>"
											+ "<network href=\"/locations/uk-epcc/networks/0\" name=\"BonFIRE WAN\"/>"
											+ "<ip>172.18.3.160</ip>"
											+ "<mac>02:00:ac:12:03:a0</mac>"
										+ "</nic>"
										+ "<context>"
											+ "<authorized_keys>ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQChu9trsHSNWBDcXP7C86Qi9v+9IbmkD42GkIT5wF42TtnZ/HvpiAmT2jPBD/b0CI4rnmbqwyB+TknAJjJEAb9C8Tgk7Sjgr/0JCbRZu74tzZkrCn/hiG+9bPQBTBv3sxwJ2p94wvrRrgMK21TwqMQ+FkmVBdk6Zp3PoSRtFLs0Nc95kFZlMeonfgSfOJy1dtkzhTAH8fu9HwrjwAAv0xIX+eQ+n3kQgsxQ6vrPdwjOt0n6h52FpYXcPhRiXmkHa5BEWxhvranzsCeTnUQk5RBmC/p6VqA/mFJOzRuKFPP2bRHaE8tcXVeADBQgrBPvZWsSiuXAdJ8bzP+WeprrTw61 A510804@ES-CNU21131QG</authorized_keys>"
											+ "<bonfire_credentials>dperez:g3uqkvz3czmpv79zusups</bonfire_credentials>"
											+ "<bonfire_experiment_aggregator_password>6afh7a</bonfire_experiment_aggregator_password>"
											+ "<bonfire_experiment_expiration_date>1369744294</bonfire_experiment_expiration_date>"
											+ "<bonfire_experiment_id>3032</bonfire_experiment_id>"
											+ "<bonfire_experiment_routing_key>3fdff0c64c51135f4e5e</bonfire_experiment_routing_key>"
											+ "<bonfire_provider>uk-epcc</bonfire_provider>"
											+ "<bonfire_resource_id>583</bonfire_resource_id>"
											+ "<bonfire_resource_name>asdfasdfas</bonfire_resource_name>"
											+ "<bonfire_uri>https://api.integration.bonfire.grid5000.fr</bonfire_uri>"
											+ "<dns_servers>172.18.6.1</dns_servers>"
											+ "<eth0_gateway>172.18.3.1</eth0_gateway>"
											+ "<eth0_ip>172.18.3.160</eth0_ip>"
											+ "<eth0_mask>255.255.255.0</eth0_mask>"
											+ "<eth0_name>BonFIRE WAN</eth0_name>"
											+ "<files>/srv/cloud/context /srv/cloud/context/lib /srv/cloud/context/distributions /srv/cloud/context/sites /srv/cloud/context/common /srv/cloud/context/init.sh</files>"
											+ "<hostname>asdfasdfas-583</hostname>"
											+ "<ntp_servers>129.215.175.254</ntp_servers>"
											+ "<public_gateway>129.215.175.254</public_gateway>"
											+ "<public_netmask>255.255.255.128</public_netmask>"
											+ "<target>sdb</target>"
											+ "<usage>zabbix-agent</usage>"
											+ "<wan_ip>172.18.3.160</wan_ip>"
											+ "<wan_mac>02:00:ac:12:03:a0</wan_mac>"
										+ "</context>"
										+ "<host>crockett0</host>"
										+ "<link rel=\"experiment\" href=\"/experiments/3032\" type=\"application/vnd.bonfire+xml\"/>"
									+ "</compute>";
		
		mServer.addPath("/experiments/3009/computes", returnedOCCIBytheAPI);
		
		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setUserName("username");
		client.setPassword("password");
		client.setUrl(mBaseURL);
		
		Experiment experiment = new Experiment();
		experiment.setId("211");
		experiment.setHref("/experiments/211");
		
		Compute compute = new Compute();
		compute.setComputeName("computeName");
		compute.setGroups("dperez");
		Configuration configuration = new Configuration();
		configuration.setName("lite");
		compute.setConfiguration(configuration);
		Disk disk = new Disk();
		Storage storage = new Storage();
		storage.setHref("/locations/uk-epcc/storages/0");
		disk.setStorage(storage);
		disk.setType("OS");
		disk.setTarget("hda");
		compute.addDisk(disk);
		
		Location location = new Location();
		location.setHref("/locations/uk-epcc");

		Compute createdCompute = client.createComputeAtBonFIREWAN(location, "3009", compute);
		
		String messageSentToBonFIREAPI = mServer.getRequestBody();
		
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(messageSentToBonFIREAPI));
		XPath xpath = XPath.newInstance("//bnf:compute");
		xpath.addNamespace("bnf", NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		
		xpath = XPath.newInstance("//bnf:name");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element element = (Element) listxpath.get(0);
		assertEquals("computeName", element.getValue());
		
		xpath = XPath.newInstance("//bnf:groups");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("dperez", element.getValue());
		
		xpath = XPath.newInstance("//bnf:instance_type");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("lite", element.getValue());

		xpath = XPath.newInstance("//bnf:storage");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("/locations/uk-epcc/storages/0", element.getAttributeValue("href"));
		
		xpath = XPath.newInstance("//bnf:network");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("/locations/uk-epcc/networks/0", element.getAttributeValue("href"));
		
		xpath = XPath.newInstance("//bnf:target");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("hda", element.getValue());
		
		xpath = XPath.newInstance("//bnf:link");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("/locations/uk-epcc", element.getAttributeValue("href"));
		assertEquals("location", element.getAttributeValue("rel"));		
		
		assertEquals("583", createdCompute.getId());
		assertEquals("256", createdCompute.getMemory());
		assertEquals("0.25", createdCompute.getCpu());
		assertEquals("dperez", createdCompute.getGroups());
		assertEquals("/locations/uk-epcc/storages/0", createdCompute.getDisks().get(0).getStorage().getHref());
		assertEquals("/locations/uk-epcc/networks/0", createdCompute.getNics().get(0).getNetwork().getHref());
	}
	
	@Test
	public void creationOfAComputeWithExperimentId() throws Exception {
				
		String returnedOCCIBytheAPI = "<compute xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"/locations/uk-epcc/computes/583\">"
										+ "<id>583</id>"
										+ "<cpu>0.25</cpu>"
										+ "<memory>256</memory>"
										+ "<uname href=\"/locations/uk-epcc/users/7\">dperez</uname>"
										+ "<groups>dperez</groups>"
										+ "<name>asdfasdfas</name>"
										+ "<instance_type href=\"/locations/uk-epcc/configurations/lite\">lite</instance_type>"
										+ "<state>RUNNING</state>"
										+ "<disk id=\"0\">"
											+ "<storage href=\"/locations/uk-epcc/storages/0\" name=\"BonFIRE Debian Squeeze v5\"/>"
											+ "<type>FILE</type>"
											+ "<target>sda</target>"
										+ "</disk>"
										+ "<nic>"
											+ "<network href=\"/locations/uk-epcc/networks/0\" name=\"BonFIRE WAN\"/>"
											+ "<ip>172.18.3.160</ip>"
											+ "<mac>02:00:ac:12:03:a0</mac>"
										+ "</nic>"
										+ "<context>"
											+ "<authorized_keys>ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQChu9trsHSNWBDcXP7C86Qi9v+9IbmkD42GkIT5wF42TtnZ/HvpiAmT2jPBD/b0CI4rnmbqwyB+TknAJjJEAb9C8Tgk7Sjgr/0JCbRZu74tzZkrCn/hiG+9bPQBTBv3sxwJ2p94wvrRrgMK21TwqMQ+FkmVBdk6Zp3PoSRtFLs0Nc95kFZlMeonfgSfOJy1dtkzhTAH8fu9HwrjwAAv0xIX+eQ+n3kQgsxQ6vrPdwjOt0n6h52FpYXcPhRiXmkHa5BEWxhvranzsCeTnUQk5RBmC/p6VqA/mFJOzRuKFPP2bRHaE8tcXVeADBQgrBPvZWsSiuXAdJ8bzP+WeprrTw61 A510804@ES-CNU21131QG</authorized_keys>"
											+ "<bonfire_credentials>dperez:g3uqkvz3czmpv79zusups</bonfire_credentials>"
											+ "<bonfire_experiment_aggregator_password>6afh7a</bonfire_experiment_aggregator_password>"
											+ "<bonfire_experiment_expiration_date>1369744294</bonfire_experiment_expiration_date>"
											+ "<bonfire_experiment_id>3032</bonfire_experiment_id>"
											+ "<bonfire_experiment_routing_key>3fdff0c64c51135f4e5e</bonfire_experiment_routing_key>"
											+ "<bonfire_provider>uk-epcc</bonfire_provider>"
											+ "<bonfire_resource_id>583</bonfire_resource_id>"
											+ "<bonfire_resource_name>asdfasdfas</bonfire_resource_name>"
											+ "<bonfire_uri>https://api.integration.bonfire.grid5000.fr</bonfire_uri>"
											+ "<dns_servers>172.18.6.1</dns_servers>"
											+ "<eth0_gateway>172.18.3.1</eth0_gateway>"
											+ "<eth0_ip>172.18.3.160</eth0_ip>"
											+ "<eth0_mask>255.255.255.0</eth0_mask>"
											+ "<eth0_name>BonFIRE WAN</eth0_name>"
											+ "<files>/srv/cloud/context /srv/cloud/context/lib /srv/cloud/context/distributions /srv/cloud/context/sites /srv/cloud/context/common /srv/cloud/context/init.sh</files>"
											+ "<hostname>asdfasdfas-583</hostname>"
											+ "<ntp_servers>129.215.175.254</ntp_servers>"
											+ "<public_gateway>129.215.175.254</public_gateway>"
											+ "<public_netmask>255.255.255.128</public_netmask>"
											+ "<target>sdb</target>"
											+ "<usage>zabbix-agent</usage>"
											+ "<wan_ip>172.18.3.160</wan_ip>"
											+ "<wan_mac>02:00:ac:12:03:a0</wan_mac>"
										+ "</context>"
										+ "<host>crockett0</host>"
										+ "<link rel=\"experiment\" href=\"/experiments/3032\" type=\"application/vnd.bonfire+xml\"/>"
									+ "</compute>";
		
		mServer.addPath("/experiments/3032/computes", returnedOCCIBytheAPI);
		
		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setUserName("username");
		client.setPassword("password");
		client.setUrl(mBaseURL);
		
		Compute compute = new Compute();
		compute.setComputeName("computeName");
		compute.setGroups("dperez");
		Configuration configuration = new Configuration();
		configuration.setName("lite");
		compute.setConfiguration(configuration);
		Disk disk = new Disk();
		Storage storage = new Storage();
		storage.setHref("/locations/uk-epcc/storages/0");
		disk.setStorage(storage);
		disk.setType("OS");
		disk.setTarget("hda");
		compute.addDisk(disk);
		Nic nic = new Nic();
		Network network = new Network();
		network.setHref("/locations/uk-epcc/networks/0");
		nic.setNetwork(network);
		compute.addNic(nic);
		
		Location location = new Location();
		location.setHref("/locations/uk-epcc");

		Compute createdCompute = client.createCompute(location, "3032", compute);
		
		String messageSentToBonFIREAPI = mServer.getRequestBody();
		
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(messageSentToBonFIREAPI));
		XPath xpath = XPath.newInstance("//bnf:compute");
		xpath.addNamespace("bnf", NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		
		xpath = XPath.newInstance("//bnf:name");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element element = (Element) listxpath.get(0);
		assertEquals("computeName", element.getValue());
		
		xpath = XPath.newInstance("//bnf:groups");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("dperez", element.getValue());
		
		xpath = XPath.newInstance("//bnf:instance_type");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("lite", element.getValue());

		xpath = XPath.newInstance("//bnf:storage");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("/locations/uk-epcc/storages/0", element.getAttributeValue("href"));
		
		xpath = XPath.newInstance("//bnf:network");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("/locations/uk-epcc/networks/0", element.getAttributeValue("href"));
		
		xpath = XPath.newInstance("//bnf:target");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("hda", element.getValue());
		
		xpath = XPath.newInstance("//bnf:link");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("/locations/uk-epcc", element.getAttributeValue("href"));
		assertEquals("location", element.getAttributeValue("rel"));		
		
		assertEquals("583", createdCompute.getId());
		assertEquals("256", createdCompute.getMemory());
		assertEquals("0.25", createdCompute.getCpu());
		assertEquals("dperez", createdCompute.getGroups());
		assertEquals("/locations/uk-epcc/storages/0", createdCompute.getDisks().get(0).getStorage().getHref());
		assertEquals("/locations/uk-epcc/networks/0", createdCompute.getNics().get(0).getNetwork().getHref());
	}
	
	public void testCreateStorage() throws Exception {
		
		String xmlStorageCreatedResponse = "<storage xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"/locations/uk-epcc/storages/250\">"
												+ "<id>250</id>"
												+ "<name>MyStorage</name>"
												+ "<user_id href=\"/locations/uk-epcc/users/7\">dperez</user_id>"
												+ "<groups>dperez</groups>"
												+ "<state>READY</state>"
												+ "<type>DATABLOCK</type>"
												+ "<description>Test Storage</description>"
												+ "<size>10</size>"
												+ "<fstype>ext3</fstype>"
												+ "<public>NO</public>"
												+ "<persistent>NO</persistent>"
												+ "<datastore>default</datastore>"
												+ "<datastore_id>1</datastore_id>"
												+ "<link rel=\"experiment\" href=\"/experiments/3047\" type=\"application/vnd.bonfire+xml\"/>"
											+ "</storage>";
		
		mServer.addPath("/experiments/211/storages", xmlStorageCreatedResponse);
		
		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setUserName("username");
		client.setPassword("password");
		client.setUrl(mBaseURL);
		
		Experiment experiment = new Experiment();
		experiment.setId("211");
		experiment.setHref("/experiments/211");
		
		Storage storage = new Storage();
		storage.setStorageName("MyStorage");
		storage.setGroups("dperez");
		storage.setDescription("My Storage");
		storage.setType("DATABLOCK");
		storage.setFstype("ext3");
		storage.setSize("10");
		
		Location location = new Location();
		location.setHref("/locations/uk-epcc");
		
		Storage createdStorage = client.createStorage(location, experiment, storage);
		
		String messageSentToBonFIREAPI = mServer.getRequestBody();
		
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(messageSentToBonFIREAPI));
		XPath xpath = XPath.newInstance("//bnf:storage");
		xpath.addNamespace("bnf", NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		
		xpath = XPath.newInstance("//bnf:name");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element element = (Element) listxpath.get(0);
		assertEquals("MyStorage", element.getValue());
		
		xpath = XPath.newInstance("//bnf:description");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("My Storage", element.getValue());
		
		xpath = XPath.newInstance("//bnf:link");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		element = (Element) listxpath.get(0);
		assertEquals("/locations/uk-epcc", element.getAttributeValue("href"));
		assertEquals("location", element.getAttributeValue("rel"));
		
		assertEquals("250", createdStorage.getId());
		assertEquals("dperez", createdStorage.getGroups());
	}
	
	@Test
	public void testUpdateResourceInfo() throws NoHrefSetException {
		String xmlStoragee = "<storage xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"/locations/uk-epcc/storages/250\">"
								+ "<id>250</id>"
								+ "<name>MyStorage</name>"
								+ "<user_id href=\"/locations/uk-epcc/users/7\">dperez</user_id>"
								+ "<groups>dperez</groups>"
								+ "<state>READY</state>"
								+ "<type>DATABLOCK</type>"
								+ "<description>Test Storage</description>"
								+ "<size>10</size>"
								+ "<fstype>ext3</fstype>"
								+ "<public>NO</public>"
								+ "<persistent>NO</persistent>"
								+ "<datastore>default</datastore>"
								+ "<datastore_id>1</datastore_id>"
								+ "<link rel=\"experiment\" href=\"/experiments/3047\" type=\"application/vnd.bonfire+xml\"/>"
							+ "</storage>";
		
		String xmlNetwork = "<network xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"/locations/fr-inria/networks/1\">"
								+ "<id>1</id>"
								+ "<name>BonFIRE WAN</name>"
								+ "<uname href=\"/locations/fr-inria/users/0\">admin</uname>"
								+ "<groups>users</groups>"
								+ "<public>NO</public>"
								+ "<vlan />"
							+ "</network>";
		
		String xmlCompute = "<compute xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"/locations/uk-epcc/computes/583\">"
								+ "<id>583</id>"
								+ "<cpu>0.25</cpu>"
								+ "<memory>256</memory>"
								+ "<uname href=\"/locations/uk-epcc/users/7\">dperez</uname>"
								+ "<groups>dperez</groups>"
								+ "<name>asdfasdfas</name>"
								+ "<instance_type href=\"/locations/uk-epcc/configurations/lite\">lite</instance_type>"
								+ "<state>RUNNING</state>"
								+ "<disk id=\"0\">"
									+ "<storage href=\"/locations/uk-epcc/storages/0\" name=\"BonFIRE Debian Squeeze v5\"/>"
									+ "<type>FILE</type>"
									+ "<target>sda</target>"
								+ "</disk>"
								+ "<nic>"
									+ "<network href=\"/locations/uk-epcc/networks/0\" name=\"BonFIRE WAN\"/>"
									+ "<ip>172.18.3.160</ip>"
									+ "<mac>02:00:ac:12:03:a0</mac>"
								+ "</nic>"
								+ "<context>"
									+ "<authorized_keys>ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQChu9trsHSNWBDcXP7C86Qi9v+9IbmkD42GkIT5wF42TtnZ/HvpiAmT2jPBD/b0CI4rnmbqwyB+TknAJjJEAb9C8Tgk7Sjgr/0JCbRZu74tzZkrCn/hiG+9bPQBTBv3sxwJ2p94wvrRrgMK21TwqMQ+FkmVBdk6Zp3PoSRtFLs0Nc95kFZlMeonfgSfOJy1dtkzhTAH8fu9HwrjwAAv0xIX+eQ+n3kQgsxQ6vrPdwjOt0n6h52FpYXcPhRiXmkHa5BEWxhvranzsCeTnUQk5RBmC/p6VqA/mFJOzRuKFPP2bRHaE8tcXVeADBQgrBPvZWsSiuXAdJ8bzP+WeprrTw61 A510804@ES-CNU21131QG</authorized_keys>"
									+ "<bonfire_credentials>dperez:g3uqkvz3czmpv79zusups</bonfire_credentials>"
									+ "<bonfire_experiment_aggregator_password>6afh7a</bonfire_experiment_aggregator_password>"
									+ "<bonfire_experiment_expiration_date>1369744294</bonfire_experiment_expiration_date>"
									+ "<bonfire_experiment_id>3032</bonfire_experiment_id>"
									+ "<bonfire_experiment_routing_key>3fdff0c64c51135f4e5e</bonfire_experiment_routing_key>"
									+ "<bonfire_provider>uk-epcc</bonfire_provider>"
									+ "<bonfire_resource_id>583</bonfire_resource_id>"
									+ "<bonfire_resource_name>asdfasdfas</bonfire_resource_name>"
									+ "<bonfire_uri>https://api.integration.bonfire.grid5000.fr</bonfire_uri>"
									+ "<dns_servers>172.18.6.1</dns_servers>"
									+ "<eth0_gateway>172.18.3.1</eth0_gateway>"
									+ "<eth0_ip>172.18.3.160</eth0_ip>"
									+ "<eth0_mask>255.255.255.0</eth0_mask>"
									+ "<eth0_name>BonFIRE WAN</eth0_name>"
									+ "<files>/srv/cloud/context /srv/cloud/context/lib /srv/cloud/context/distributions /srv/cloud/context/sites /srv/cloud/context/common /srv/cloud/context/init.sh</files>"
									+ "<hostname>asdfasdfas-583</hostname>"
									+ "<ntp_servers>129.215.175.254</ntp_servers>"
									+ "<public_gateway>129.215.175.254</public_gateway>"
									+ "<public_netmask>255.255.255.128</public_netmask>"
									+ "<target>sdb</target>"
									+ "<usage>zabbix-agent</usage>"
									+ "<wan_ip>172.18.3.160</wan_ip>"
									+ "<wan_mac>02:00:ac:12:03:a0</wan_mac>"
								+ "</context>"
								+ "<host>crockett0</host>"
								+ "<link rel=\"experiment\" href=\"/experiments/3032\" type=\"application/vnd.bonfire+xml\"/>"
							+ "</compute>";
		
		String xmlOfExperiment = "<experiment xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"/experiments/3005\">"
									+ "<id>3005</id>"
									+ "<description>Test Experiment</description>"
									+ "<name>MyExpoerimetn</name>"
									+ "<walltime>7200</walltime>"
									+ "<user_id>dperez</user_id>"
									+ "<groups>dperez</groups>"
									+ "<status>ready</status>"
									+ "<routing_key>dc0e76a89b66d78b6594</routing_key>"
									+ "<aggregator_password>3tt6ne</aggregator_password>"
									+ "<created_at>2013-05-27T16:27:10Z</created_at>"
									+ "<updated_at>2013-05-27T16:27:10Z</updated_at>"
									+ "<link rel=\"parent\" href=\"/\"/>"
									+ "<link rel=\"storages\" href=\"/experiments/3005/storages\"/>"
									+ "<link rel=\"networks\" href=\"/experiments/3005/networks\"/>"
									+ "<link rel=\"computes\" href=\"/experiments/3005/computes\"/>"
									+ "<link rel=\"routers\" href=\"/experiments/3005/routers\"/>"
									+ "<link rel=\"site_links\" href=\"/experiments/3005/site_links\"/>"
								  + "</experiment>";
		
		// We create the client
		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setUserName("username");
		client.setPassword("password");
		client.setUrl(mBaseURL);

		// We setup the mock BonFIRE API
		mServer.addPath("/locations/uk-epcc/storages/250", xmlStoragee);
		
		Storage storage = new Storage();
		storage.setHref("/locations/uk-epcc/storages/250");
		
		Storage updateStorage = (Storage) client.updateResourceInfo(storage);
		assertEquals("/locations/uk-epcc/storages/250", updateStorage.getHref());
		assertEquals("MyStorage", updateStorage.getName());
		
		// We setup the mock BonFIRE API for the Network query
		mServer.addPath("/locations/fr-inria/networks/1", xmlNetwork);
		
		Network network = new Network();
		network.setHref("/locations/fr-inria/networks/1");
		Network networkUpdated = (Network) client.updateResourceInfo(network);
		assertEquals("/locations/fr-inria/networks/1", networkUpdated.getHref());
		assertEquals("BonFIRE WAN", networkUpdated.getName());
		
		// We setup the mock BonFIRE API for the Network query
		mServer.addPath("/locations/uk-epcc/computes/583", xmlCompute);
		Compute compute = new Compute();
		compute.setHref("/locations/uk-epcc/computes/583");
		Compute updatedCompute = (Compute) client.updateResourceInfo(compute);
		assertEquals("/locations/uk-epcc/computes/583", updatedCompute.getHref());
		assertEquals("asdfasdfas", updatedCompute.getComputeName());
		
		// We setup the mock BonFIRE API for the Experiment query
		mServer.addPath("/experiments/3005", xmlOfExperiment);
		Experiment experiment = new Experiment();
		experiment.setHref("/experiments/3005");
		Experiment updatedExperiment = (Experiment) client.updateResourceInfo(experiment);
		assertEquals("/experiments/3005", updatedExperiment.getHref());
		assertEquals("MyExpoerimetn", updatedExperiment.getName());
	}
	
	/**
	 * If we try to update the information from the BonFIRE API of an resource that has HREF null
	 * the Java API should throw an exception
	 * @throws NoHrefSetException
	 */
	@Test
	public void testUpdateHrefNull() throws NoHrefSetException {
		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setUserName("username");
		client.setPassword("password");
		client.setUrl(mBaseURL);
		
		// An exception should be thrown since we are asking to update information of a Compute
		// without an HREF set!
		Compute compute = new Compute();
		exception.expect(NoHrefSetException.class);
		client.updateResourceInfo(compute);
	}
	
	/**
	 * If we try to update the information from the BonFIRE API of an resource with an empty HREF
	 * the Java API should throw an exception
	 * @throws NoHrefSetException
	 */
	@Test
	public void testUpdateHrefEmpty() throws NoHrefSetException {
		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setUserName("username");
		client.setPassword("password");
		client.setUrl(mBaseURL);
		
		Network network = new Network();
		network.setHref("");
		exception.expect(NoHrefSetException.class);
		client.updateResourceInfo(network);
	}
	
	/**
	 * We verify that the storage can be converted to public by the API
	 */
	@Test
	public void testUpdatedStoragePublic() throws Exception {
		Storage storage = new Storage();
		storage.setHref("/locations/uk-epcc/storages/250");
		storage.setId("250");
		storage.setName("MyStorage");
		storage.setPublicStorage("NO");
		storage.setFstype("ext3");
		storage.setType("DATABLOCK");
		storage.setGroups("dperez");
		storage.setDescription("my description");
		
		String xmlStoragee = "<storage xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"/locations/uk-epcc/storages/250\">"
								+ "<id>250</id>"
								+ "<name>MyStorage</name>"
								+ "<user_id href=\"/locations/uk-epcc/users/7\">dperez</user_id>"
								+ "<groups>dperez</groups>"
								+ "<state>READY</state>"
								+ "<type>DATABLOCK</type>"
								+ "<description>Test Storage</description>"
								+ "<size>10</size>"
								+ "<fstype>ext3</fstype>"
								+ "<public>YES</public>"
								+ "<persistent>NO</persistent>"
								+ "<datastore>default</datastore>"
								+ "<datastore_id>1</datastore_id>"
								+ "<link rel=\"experiment\" href=\"/experiments/3047\" type=\"application/vnd.bonfire+xml\"/>"
							+ "</storage>";
		
		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setUserName("username");
		client.setPassword("password");
		client.setUrl(mBaseURL);
		
		mServer.addPath("/locations/uk-epcc/storages/250", xmlStoragee);
		
		Storage publicStorage = client.makeStoragePublic(storage);
		
		String messageSentToBonFIREAPI = mServer.getRequestBody();
		
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(messageSentToBonFIREAPI));
		XPath xpath = XPath.newInstance("//bnf:storage");
		xpath.addNamespace("bnf", NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		
		xpath = XPath.newInstance("//bnf:name");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(0, listxpath.size());
		
		xpath = XPath.newInstance("//bnf:id");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(0, listxpath.size());
		
		xpath = XPath.newInstance("//bnf:user_id");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(0, listxpath.size());
		
		xpath = XPath.newInstance("//bnf:groups");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(0, listxpath.size());
		
		xpath = XPath.newInstance("//bnf:type");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(0, listxpath.size());
		
		xpath = XPath.newInstance("//bnf:fstype");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(0, listxpath.size());
		
		xpath = XPath.newInstance("//bnf:description");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(0, listxpath.size());
		
		xpath = XPath.newInstance("//bnf:public");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element element = (Element) listxpath.get(0);
		assertEquals("YES", element.getValue());
		
		// We verify that the returned Storage was correctly parsed.
		assertEquals("YES", publicStorage.getPublicStorage());
	}
	
	/**
	 * We verify that the storage can be converted to Non public by the API
	 */
	@Test
	public void testUpdatedStorageNoPublic() throws Exception {
		Storage storage = new Storage();
		storage.setHref("/locations/uk-epcc/storages/250");
		storage.setId("250");
		storage.setName("MyStorage");
		storage.setPublicStorage("YES");
		storage.setFstype("ext3");
		storage.setType("DATABLOCK");
		storage.setGroups("dperez");
		storage.setDescription("my description");
		
		String xmlStoragee = "<storage xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"/locations/uk-epcc/storages/250\">"
								+ "<id>250</id>"
								+ "<name>MyStorage</name>"
								+ "<user_id href=\"/locations/uk-epcc/users/7\">dperez</user_id>"
								+ "<groups>dperez</groups>"
								+ "<state>READY</state>"
								+ "<type>DATABLOCK</type>"
								+ "<description>Test Storage</description>"
								+ "<size>10</size>"
								+ "<fstype>ext3</fstype>"
								+ "<public>NO</public>"
								+ "<persistent>NO</persistent>"
								+ "<datastore>default</datastore>"
								+ "<datastore_id>1</datastore_id>"
								+ "<link rel=\"experiment\" href=\"/experiments/3047\" type=\"application/vnd.bonfire+xml\"/>"
							+ "</storage>";
		
		BonFIREAPIClient client = new BonFIREAPIClientImpl();
		client.setUserName("username");
		client.setPassword("password");
		client.setUrl(mBaseURL);
		
		mServer.addPath("/locations/uk-epcc/storages/250", xmlStoragee);
		
		Storage publicStorage = client.unMakeStoragePublic(storage);
		
		String messageSentToBonFIREAPI = mServer.getRequestBody();
		
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(messageSentToBonFIREAPI));
		XPath xpath = XPath.newInstance("//bnf:storage");
		xpath.addNamespace("bnf", NAMESPACE);
		List listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		
		xpath = XPath.newInstance("//bnf:name");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(0, listxpath.size());
		
		xpath = XPath.newInstance("//bnf:id");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(0, listxpath.size());
		
		xpath = XPath.newInstance("//bnf:user_id");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(0, listxpath.size());
		
		xpath = XPath.newInstance("//bnf:groups");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(0, listxpath.size());
		
		xpath = XPath.newInstance("//bnf:type");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(0, listxpath.size());
		
		xpath = XPath.newInstance("//bnf:fstype");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(0, listxpath.size());
		
		xpath = XPath.newInstance("//bnf:description");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(0, listxpath.size());
		
		xpath = XPath.newInstance("//bnf:public");
		xpath.addNamespace("bnf", NAMESPACE);
		listxpath = xpath.selectNodes(xmldoc);
		assertEquals(1, listxpath.size());
		Element element = (Element) listxpath.get(0);
		assertEquals("NO", element.getValue());
		
		// We verify that the returned Storage was correctly parsed.
		assertEquals("NO", publicStorage.getPublicStorage());
	}
	
//	/**
//	 * We verify that the storage can be converted to public by the API
//	 */
//	@Test
//	public void testUpdatedStoragePersistent() throws Exception {
//		Storage storage = new Storage();
//		storage.setHref("/locations/uk-epcc/storages/250");
//		storage.setId("250");
//		storage.setName("MyStorage");
//		storage.setPublicStorage("NO");
//		storage.setPersistent("NO");
//		storage.setFstype("ext3");
//		storage.setType("DATABLOCK");
//		storage.setGroups("dperez");
//		storage.setDescription("my description");
//		
//		String xmlStoragee = "<storage xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"/locations/uk-epcc/storages/250\">"
//								+ "<id>250</id>"
//								+ "<name>MyStorage</name>"
//								+ "<user_id href=\"/locations/uk-epcc/users/7\">dperez</user_id>"
//								+ "<groups>dperez</groups>"
//								+ "<state>READY</state>"
//								+ "<type>DATABLOCK</type>"
//								+ "<description>Test Storage</description>"
//								+ "<size>10</size>"
//								+ "<fstype>ext3</fstype>"
//								+ "<public>NO</public>"
//								+ "<persistent>YES</persistent>"
//								+ "<datastore>default</datastore>"
//								+ "<datastore_id>1</datastore_id>"
//								+ "<link rel=\"experiment\" href=\"/experiments/3047\" type=\"application/vnd.bonfire+xml\"/>"
//							+ "</storage>";
//		
//		BonFIREAPIClient client = new BonFIREAPIClientImpl();
//		client.setUserName("username");
//		client.setPassword("password");
//		client.setUrl(mBaseURL);
//		
//		mServer.addPath("/locations/uk-epcc/storages/250", xmlStoragee);
//		
//		Storage publicStorage = client.makeStoragePersistent(storage);
//		
//		String messageSentToBonFIREAPI = mServer.getRequestBody();
//		
//		SAXBuilder builder = new SAXBuilder();
//		builder.setValidation(false);
//		builder.setIgnoringElementContentWhitespace(true);
//		Document xmldoc = builder.build(new StringReader(messageSentToBonFIREAPI));
//		XPath xpath = XPath.newInstance("//bnf:storage");
//		xpath.addNamespace("bnf", NAMESPACE);
//		List listxpath = xpath.selectNodes(xmldoc);
//		assertEquals(1, listxpath.size());
//		
//		xpath = XPath.newInstance("//bnf:name");
//		xpath.addNamespace("bnf", NAMESPACE);
//		listxpath = xpath.selectNodes(xmldoc);
//		assertEquals(0, listxpath.size());
//		
//		xpath = XPath.newInstance("//bnf:id");
//		xpath.addNamespace("bnf", NAMESPACE);
//		listxpath = xpath.selectNodes(xmldoc);
//		assertEquals(0, listxpath.size());
//		
//		xpath = XPath.newInstance("//bnf:user_id");
//		xpath.addNamespace("bnf", NAMESPACE);
//		listxpath = xpath.selectNodes(xmldoc);
//		assertEquals(0, listxpath.size());
//		
//		xpath = XPath.newInstance("//bnf:groups");
//		xpath.addNamespace("bnf", NAMESPACE);
//		listxpath = xpath.selectNodes(xmldoc);
//		assertEquals(0, listxpath.size());
//		
//		xpath = XPath.newInstance("//bnf:type");
//		xpath.addNamespace("bnf", NAMESPACE);
//		listxpath = xpath.selectNodes(xmldoc);
//		assertEquals(0, listxpath.size());
//		
//		xpath = XPath.newInstance("//bnf:fstype");
//		xpath.addNamespace("bnf", NAMESPACE);
//		listxpath = xpath.selectNodes(xmldoc);
//		assertEquals(0, listxpath.size());
//		
//		xpath = XPath.newInstance("//bnf:description");
//		xpath.addNamespace("bnf", NAMESPACE);
//		listxpath = xpath.selectNodes(xmldoc);
//		assertEquals(0, listxpath.size());
//		
//		xpath = XPath.newInstance("//bnf:public");
//		xpath.addNamespace("bnf", NAMESPACE);
//		listxpath = xpath.selectNodes(xmldoc);
//		assertEquals(0, listxpath.size());
//		
//		xpath = XPath.newInstance("//bnf:persistent");
//		xpath.addNamespace("bnf", NAMESPACE);
//		listxpath = xpath.selectNodes(xmldoc);
//		assertEquals(1, listxpath.size());
//		Element element = (Element) listxpath.get(0);
//		assertEquals("YES", element.getValue());
//		
//		// We verify that the returned Storage was correctly parsed.
//		assertEquals("YES", publicStorage.getPersistent());
//	}
//	
//	/**
//	 * We verify that the storage can be converted to public by the API
//	 */
//	@Test
//	public void testUpdatedStorageNoPersistent() throws Exception {
//		Storage storage = new Storage();
//		storage.setHref("/locations/uk-epcc/storages/250");
//		storage.setId("250");
//		storage.setName("MyStorage");
//		storage.setPublicStorage("NO");
//		storage.setPersistent("YES");
//		storage.setFstype("ext3");
//		storage.setType("DATABLOCK");
//		storage.setGroups("dperez");
//		storage.setDescription("my description");
//		
//		String xmlStoragee = "<storage xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\" href=\"/locations/uk-epcc/storages/250\">"
//								+ "<id>250</id>"
//								+ "<name>MyStorage</name>"
//								+ "<user_id href=\"/locations/uk-epcc/users/7\">dperez</user_id>"
//								+ "<groups>dperez</groups>"
//								+ "<state>READY</state>"
//								+ "<type>DATABLOCK</type>"
//								+ "<description>Test Storage</description>"
//								+ "<size>10</size>"
//								+ "<fstype>ext3</fstype>"
//								+ "<public>NO</public>"
//								+ "<persistent>NO</persistent>"
//								+ "<datastore>default</datastore>"
//								+ "<datastore_id>1</datastore_id>"
//								+ "<link rel=\"experiment\" href=\"/experiments/3047\" type=\"application/vnd.bonfire+xml\"/>"
//							+ "</storage>";
//		
//		BonFIREAPIClient client = new BonFIREAPIClientImpl();
//		client.setUserName("username");
//		client.setPassword("password");
//		client.setUrl(mBaseURL);
//		
//		mServer.addPath("/locations/uk-epcc/storages/250", xmlStoragee);
//		
//		Storage publicStorage = client.unMakeStoragePersistent(storage);
//		
//		String messageSentToBonFIREAPI = mServer.getRequestBody();
//		
//		SAXBuilder builder = new SAXBuilder();
//		builder.setValidation(false);
//		builder.setIgnoringElementContentWhitespace(true);
//		Document xmldoc = builder.build(new StringReader(messageSentToBonFIREAPI));
//		XPath xpath = XPath.newInstance("//bnf:storage");
//		xpath.addNamespace("bnf", NAMESPACE);
//		List listxpath = xpath.selectNodes(xmldoc);
//		assertEquals(1, listxpath.size());
//		
//		xpath = XPath.newInstance("//bnf:name");
//		xpath.addNamespace("bnf", NAMESPACE);
//		listxpath = xpath.selectNodes(xmldoc);
//		assertEquals(0, listxpath.size());
//		
//		xpath = XPath.newInstance("//bnf:id");
//		xpath.addNamespace("bnf", NAMESPACE);
//		listxpath = xpath.selectNodes(xmldoc);
//		assertEquals(0, listxpath.size());
//		
//		xpath = XPath.newInstance("//bnf:user_id");
//		xpath.addNamespace("bnf", NAMESPACE);
//		listxpath = xpath.selectNodes(xmldoc);
//		assertEquals(0, listxpath.size());
//		
//		xpath = XPath.newInstance("//bnf:groups");
//		xpath.addNamespace("bnf", NAMESPACE);
//		listxpath = xpath.selectNodes(xmldoc);
//		assertEquals(0, listxpath.size());
//		
//		xpath = XPath.newInstance("//bnf:type");
//		xpath.addNamespace("bnf", NAMESPACE);
//		listxpath = xpath.selectNodes(xmldoc);
//		assertEquals(0, listxpath.size());
//		
//		xpath = XPath.newInstance("//bnf:fstype");
//		xpath.addNamespace("bnf", NAMESPACE);
//		listxpath = xpath.selectNodes(xmldoc);
//		assertEquals(0, listxpath.size());
//		
//		xpath = XPath.newInstance("//bnf:description");
//		xpath.addNamespace("bnf", NAMESPACE);
//		listxpath = xpath.selectNodes(xmldoc);
//		assertEquals(0, listxpath.size());
//		
//		xpath = XPath.newInstance("//bnf:public");
//		xpath.addNamespace("bnf", NAMESPACE);
//		listxpath = xpath.selectNodes(xmldoc);
//		assertEquals(0, listxpath.size());
//		
//		xpath = XPath.newInstance("//bnf:persistent");
//		xpath.addNamespace("bnf", NAMESPACE);
//		listxpath = xpath.selectNodes(xmldoc);
//		assertEquals(1, listxpath.size());
//		Element element = (Element) listxpath.get(0);
//		assertEquals("NO", element.getValue());
//		
//		// We verify that the returned Storage was correctly parsed.
//		assertEquals("NO", publicStorage.getPersistent());
//	}
	
	@After
	public void after() {
		mServer.stop();
	}
}
