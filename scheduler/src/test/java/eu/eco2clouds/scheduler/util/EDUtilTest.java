package eu.eco2clouds.scheduler.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import eu.eco2clouds.applicationProfile.datamodel.Compute;
import eu.eco2clouds.applicationProfile.datamodel.ExperimentDescriptor;
import eu.eco2clouds.applicationProfile.datamodel.ResourceCompute;

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
 */
public class EDUtilTest {

	@Test
	public void getListHostsTestbedsTest() {
		// We create the experiment descriptor
		ExperimentDescriptor ed = new ExperimentDescriptor();
		
		//We create the resource object containing the compute and the Computes inside... 
		ResourceCompute resourceComputeA = new ResourceCompute();
		Compute computeA = new Compute();
		computeA.setHost("hostnameA");
		ArrayList<String> locationsA = new ArrayList<String>();
		locationsA.add("locationA");
		computeA.setLocations(locationsA);
		resourceComputeA.setCompute(computeA);
		
		ResourceCompute resourceComputeB = new ResourceCompute();
		Compute computeB = new Compute();
		computeB.setHost("hostnameB");
		computeB.setLocations(locationsA);
		resourceComputeB.setCompute(computeB);
		
		ResourceCompute resourceComputeC = new ResourceCompute();
		Compute computeC = new Compute();
		computeC.setHost("hostnameC");
		ArrayList<String> locationsC = new ArrayList<String>();
		locationsC.add("locationC");
		computeC.setLocations(locationsC);
		resourceComputeC.setCompute(computeC);
		
		ArrayList<ResourceCompute> resourceComputes = new ArrayList<ResourceCompute>();
		resourceComputes.add(resourceComputeA);
		resourceComputes.add(resourceComputeB);
		resourceComputes.add(resourceComputeC);
		
		ed.setResourcesCompute(resourceComputes);
		
		Map<String, List<String>> locationsAndComputes = EDUtil.getListHostsTestbeds(ed);
		assertEquals(2, locationsAndComputes.size());
		List<String> hostnamesLocationA = locationsAndComputes.get("locationA");
		assertEquals(2, hostnamesLocationA.size());
		assertEquals("hostnameA", hostnamesLocationA.get(0));
		assertEquals("hostnameB", hostnamesLocationA.get(1));
		List<String> hostnamesLocationC = locationsAndComputes.get("locationC");
		assertEquals(1, hostnamesLocationC.size());
		assertEquals("hostnameC", hostnamesLocationC.get(0));
	}
}
