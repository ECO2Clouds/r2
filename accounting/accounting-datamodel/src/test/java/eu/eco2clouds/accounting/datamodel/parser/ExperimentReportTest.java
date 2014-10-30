package eu.eco2clouds.accounting.datamodel.parser;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.eco2clouds.accounting.datamodel.parser.Link;

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
public class ExperimentReportTest {

	@Test
	public void pojo() {
		
		ExperimentReport experimentReport = new ExperimentReport();
		List<Link> links = new ArrayList<Link>();
		List<VMReport> vmReports = new ArrayList<VMReport>();
		CO2Generated co2 = new CO2Generated();
		PowerConsumption po = new PowerConsumption();
		experimentReport.setHref("href");
		experimentReport.setPowerConsumption(po);
		experimentReport.setCo2Generated(co2);
		experimentReport.setVmReports(vmReports);
		experimentReport.setLinks(links);
		
		assertEquals("href", experimentReport.getHref());
		assertEquals(links, experimentReport.getLinks());
		assertEquals(co2, experimentReport.getCo2Generated());
		assertEquals(po, experimentReport.getPowerConsumption());
		assertEquals(vmReports, experimentReport.getVmReports());
	}
	
	@Test
	public void addLinkTest() {
		ExperimentReport experimentReport = new ExperimentReport();
		
		assertEquals(null, experimentReport.getLinks());
		
		experimentReport.addLink("/", "application_profile", "application+json");
		assertEquals(1, experimentReport.getLinks().size());
		assertEquals("/", ((Link) experimentReport.getLinks().toArray()[0]).getRel());
	}
}
