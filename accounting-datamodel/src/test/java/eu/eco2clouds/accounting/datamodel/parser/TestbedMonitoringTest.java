package eu.eco2clouds.accounting.datamodel.parser;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import eu.eco2clouds.accounting.datamodel.parser.Co2;
import eu.eco2clouds.accounting.datamodel.parser.Coal;
import eu.eco2clouds.accounting.datamodel.parser.Cost;
import eu.eco2clouds.accounting.datamodel.parser.Gaz;
import eu.eco2clouds.accounting.datamodel.parser.Hydraulic;
import eu.eco2clouds.accounting.datamodel.parser.Link;
import eu.eco2clouds.accounting.datamodel.parser.Nuclear;
import eu.eco2clouds.accounting.datamodel.parser.Oil;
import eu.eco2clouds.accounting.datamodel.parser.Other;
import eu.eco2clouds.accounting.datamodel.parser.PDUFr;
import eu.eco2clouds.accounting.datamodel.parser.TestbedMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.Total;
import eu.eco2clouds.accounting.datamodel.parser.Wind;

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
public class TestbedMonitoringTest {

	@Test
	public void pojo() {
		TestbedMonitoring testbedMonitoring = new TestbedMonitoring();
		testbedMonitoring.setHref("href");
		Co2 co2 = new Co2();
		testbedMonitoring.setCo2(co2);
		Coal coal = new Coal();
		testbedMonitoring.setCoal(coal);
		Gaz gaz = new Gaz();
		testbedMonitoring.setGaz(gaz);
		Hydraulic hydraulic = new Hydraulic();
		testbedMonitoring.setHydraulic(hydraulic);
		Nuclear nuclear = new Nuclear();
		testbedMonitoring.setNuclear(nuclear);
		Oil oil = new Oil();
		testbedMonitoring.setOil(oil);
		Other other = new Other();
		testbedMonitoring.setOther(other);
		PDUFr pDUFr = new PDUFr();
		testbedMonitoring.setpDUFr(pDUFr);
		Total total = new Total();
		testbedMonitoring.setTotal(total);
		Wind wind = new Wind();
		testbedMonitoring.setWind(wind);
		Cost cost = new Cost();
		testbedMonitoring.setCost(cost);
		List<Link> links = new ArrayList<Link>();
		testbedMonitoring.setLinks(links);
		
		assertEquals("href", testbedMonitoring.getHref());
		assertEquals(co2, testbedMonitoring.getCo2());
		assertEquals(coal, testbedMonitoring.getCoal());
		assertEquals(gaz, testbedMonitoring.getGaz());
		assertEquals(hydraulic, testbedMonitoring.getHydraulic());
		assertEquals(nuclear, testbedMonitoring.getNuclear());
		assertEquals(oil, testbedMonitoring.getOil());
		assertEquals(other, testbedMonitoring.getOther());
		assertEquals(pDUFr, testbedMonitoring.getpDUFr());
		assertEquals(total, testbedMonitoring.getTotal());
		assertEquals(wind, testbedMonitoring.getWind());
		assertEquals(links, testbedMonitoring.getLinks());
		assertEquals(cost, testbedMonitoring.getCost());
	}
	
	@Test
	public void xmlToObject() throws Exception {
		String testbedMonitoringXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
									+ "<testbed_monitoring xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/testbeds/fr-inria/monitoring\">"
										+ "<Other>"
											+ "<value>1984.0</value>"
											+ "<clock>1381512234</clock>"
											+ "<unity>MW</unity>"
											+ "<name>Other power</name>"
										+ "</Other>"
										+ "<Coal>"
											+ "<value>3949.0</value>"
											+ "<clock>1381512235</clock>"
											+ "<unity>MW</unity>"
											+ "<name>Coal power</name>"
										+ "</Coal>"
										+ "<Co2>"
											+ "<value>94.0</value>"
											+ "<clock>1381512236</clock>"
											+ "<unity>g\\/kWh</unity>"
											+ "<name>Co2 producted per kWh</name>"
										+ "</Co2>"
										+ "<Wind>"
											+ "<value>648.0</value>"
											+ "<clock>1381512237</clock>"
											+ "<unity>MW</unity>"
											+ "<name>Wind power</name>"
										+ "</Wind>"
										+ "<Oil>"
											+ "<value>0.0</value>"
											+ "<clock>1381512238</clock>"
											+ "<unity>MW</unity>"
											+ "<name>Oil power</name>"
										+ "</Oil>"
										+ "<Gaz>"
											+ "<value>2667.0</value>"
											+ "<clock>1381512239</clock>"
											+ "<unity>MW</unity>"
											+ "<name>Gaz power</name>"
										+ "</Gaz>"
										+ "<Hydraulic>"
											+ "<value>7317.0</value>"
											+ "<clock>1381512240</clock>"
											+ "<unity>MW</unity>"
											+ "<name>Hydraulic power</name>"
										+ "</Hydraulic>"
										+ "<Nuclear>"
											+ "<value>42343.0</value>"
											+ "<clock>1381512241</clock>"
											+ "<unity>MW</unity>"
											+ "<name>Nuclear power</name>"
										+ "</Nuclear>"
										+ "<Total>"
											+ "<value>58908.0</value>"
											+ "<clock>1381512242</clock>"
											+ "<unity>MW</unity>"
											+ "<name>Total power</name>"
										+ "</Total>"
										+ "<Cost>"
											+ "<value>0.33</value>"
											+ "<clock>1381512630</clock>"
											+ "<unity>euros/h</unity>"
											+ "<name>Cost</name>"
										+ "</Cost>"
										+ "<link rel=\"parent\" href=\"/testbeds/fr-inria\"/>"
									+ "</testbed_monitoring>";
		
		JAXBContext jaxbContext = JAXBContext.newInstance(TestbedMonitoring.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		TestbedMonitoring testbedMonitoring = (TestbedMonitoring) jaxbUnmarshaller.unmarshal(new StringReader(testbedMonitoringXML));
		
		assertEquals("/testbeds/fr-inria/monitoring", testbedMonitoring.getHref());
		assertEquals(0.33, testbedMonitoring.getCost().getValue().doubleValue(), 0.0001);
		assertEquals(1381512242, testbedMonitoring.getTotal().getClock().longValue());
		assertEquals(1381512241, testbedMonitoring.getNuclear().getClock().longValue());
		assertEquals(1381512240, testbedMonitoring.getHydraulic().getClock().longValue());
		assertEquals(1381512239, testbedMonitoring.getGaz().getClock().longValue());
		assertEquals(1381512238, testbedMonitoring.getOil().getClock().longValue());
		assertEquals(1381512237, testbedMonitoring.getWind().getClock().longValue());
		assertEquals(1381512236, testbedMonitoring.getCo2().getClock().longValue());
		assertEquals(1381512234, testbedMonitoring.getOther().getClock().longValue());
		assertEquals("/testbeds/fr-inria", testbedMonitoring.getLinks().get(0).getHref());
	}
}
