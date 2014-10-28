package eu.eco2clouds.accounting.datamodel.util;

import static eu.eco2clouds.accounting.Dictionary.CONTENT_TYPE_ECO2CLOUDS_XML;
import static eu.eco2clouds.accounting.Dictionary.CONTENT_TYPE_JSON;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.eco2clouds.accounting.datamodel.Experiment;
import eu.eco2clouds.accounting.datamodel.Testbed;
import eu.eco2clouds.accounting.datamodel.VM;

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
public class ModelConversionTest {
	
	@Test
	public void getTestbedXMLTest() {
		Testbed testbedFromDb = new Testbed();
		testbedFromDb.setId(2);
		testbedFromDb.setName("name");
		testbedFromDb.setUrl("url");
		
		eu.eco2clouds.accounting.datamodel.parser.Testbed testbed = ModelConversion.convertTestbed(testbedFromDb);
		assertEquals(2, testbed.getId());
		assertEquals("name", testbed.getName());
		assertEquals("url", testbed.getUrl());
	}

	@Test
	public void getExperimentXMLTest() {
		Experiment dbExperiment = new Experiment();
		dbExperiment.setApplicationProfile("aaa");
		dbExperiment.setBonfireExperimentId(1l);;
		dbExperiment.setBonfireGroupId("groupId");
		dbExperiment.setBonfireUserId("userId");
		dbExperiment.setEndTime(2l);
		dbExperiment.setId(3);
		dbExperiment.setManagedExperimentId(4l);
		dbExperiment.setStartTime(4l);
		
		eu.eco2clouds.accounting.datamodel.parser.Experiment xmlExperiment = ModelConversion.getExperimentXML(dbExperiment);
		
		assertEquals("aaa", xmlExperiment.getApplicationProfile());
		assertEquals(1l, xmlExperiment.getBonfireExperimentId().longValue());
		assertEquals("groupId", xmlExperiment.getBonfireGroupId());
		assertEquals("userId", xmlExperiment.getBonfireUserId());
		assertEquals(2l, xmlExperiment.getEndTime().longValue());
		assertEquals(3, xmlExperiment.getId().intValue());
		assertEquals(4l, xmlExperiment.getStartTime().longValue());
		assertEquals(4l, xmlExperiment.getManagedExperimentId().longValue());
		
		assertEquals(3, xmlExperiment.getLinks().size());
		assertEquals("parent", xmlExperiment.getLinks().get(0).getRel());
		assertEquals("/experiments", xmlExperiment.getLinks().get(0).getHref());
		assertEquals(CONTENT_TYPE_ECO2CLOUDS_XML, xmlExperiment.getLinks().get(0).getType());
		
		assertEquals("application-profile", xmlExperiment.getLinks().get(1).getRel());
		assertEquals("/experiments/3/application-profile", xmlExperiment.getLinks().get(1).getHref());
		assertEquals(CONTENT_TYPE_JSON, xmlExperiment.getLinks().get(1).getType());
		
		assertEquals("submitted-experiment-descriptor", xmlExperiment.getLinks().get(2).getRel());
		assertEquals("/experiments/3/submitted-experiment-descriptor", xmlExperiment.getLinks().get(2).getHref());
		assertEquals(CONTENT_TYPE_JSON, xmlExperiment.getLinks().get(2).getType());
	}
	
	@Test
	public void getVMXMLTest() {
		VM vm = new VM();
		vm.setBonfireUrl("/url");
		vm.setId(111);
		
		// First we make the conversion without Links
		eu.eco2clouds.accounting.datamodel.parser.VM xmlVM = ModelConversion.getVMXML(vm, false, 222);
		
		assertEquals("/url", xmlVM.getBonfireUrl());
		assertEquals(111, xmlVM.getId().intValue());
		
		// Then we make the conversion with links
		xmlVM = ModelConversion.getVMXML(vm, true, 222);
		
		assertEquals("/url", xmlVM.getBonfireUrl());
		assertEquals(111, xmlVM.getId().intValue());
		assertEquals("/experiments/222/vms/111", xmlVM.getHref());
		assertEquals(3, xmlVM.getLinks().size());
		assertEquals("experiment", xmlVM.getLinks().get(0).getRel());
		assertEquals("/experiments/222", xmlVM.getLinks().get(0).getHref());
		assertEquals(CONTENT_TYPE_ECO2CLOUDS_XML, xmlVM.getLinks().get(0).getType());
	}
}
