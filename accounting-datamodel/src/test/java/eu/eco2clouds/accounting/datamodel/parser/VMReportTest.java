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

import eu.eco2clouds.accounting.datamodel.parser.Co2;
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
public class VMReportTest {

	@Test 
	public void pojo() {
		VMReport vmReport = new VMReport();
		vmReport.setHref("href");
		PowerConsumption pw = new PowerConsumption();
		CO2Generated co2Generated = new CO2Generated();
		vmReport.setCo2Generated(co2Generated);
		vmReport.setPowerConsumption(pw);
		vmReport.addLink("rel", "href", "type");
		
		assertEquals("href", vmReport.getHref());
		assertEquals(pw, vmReport.getPowerConsumption());
		assertEquals(co2Generated, vmReport.getCo2Generated());
		assertEquals("rel", vmReport.getLinks().get(0).getRel());
		assertEquals("href", vmReport.getLinks().get(0).getHref());
		assertEquals("type", vmReport.getLinks().get(0).getType());
	}
	
	@Test
	public void testXML() throws Exception {
		String vmReportXML = "<vm_report href=\"/xxx/111\" xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\">"
								+ "<power_consumption>"
									+ "<value>94.0</value>"
									+ "<clock>1381512236</clock>"
									+ "<unity>g\\/kWh</unity>"
									+ "<name>Co2 producted per kWh</name>"
								+ "</power_consumption>"
								+ "<co2_generated>"
									+ "<value>94.0</value>"
									+ "<clock>1381512237</clock>"
									+ "<unity>g\\/kWh</unity>"
									+ "<name>Co2 producted per kWh</name>"
								+ "</co2_generated>"
							+ "</vm_report>";
		
		JAXBContext jaxbContext = JAXBContext.newInstance(VMReport.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		VMReport vmReport = (VMReport) jaxbUnmarshaller.unmarshal(new StringReader(vmReportXML));
		
		assertEquals(94.0, vmReport.getPowerConsumption().getValue().doubleValue(), 0.001);
		assertEquals(1381512237, vmReport.getCo2Generated().getClock().longValue());
	}
}
