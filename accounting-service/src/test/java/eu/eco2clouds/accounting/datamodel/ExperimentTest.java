package eu.eco2clouds.accounting.datamodel;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;


import eu.eco2clouds.accounting.datamodel.Experiment;

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
	public void testPojo() {
		Experiment experiment = new Experiment();
		experiment.setBonfireExperimentId(2);
		experiment.setBonfireGroupId("users");
		experiment.setBonfireUserId("user");
		experiment.setId(4);
		experiment.setApplicationProfile("AP");
		experiment.setSubmittedExperimentDescriptor("ED");
		experiment.setStartTime(1l);
		experiment.setEndTime(2l);
		experiment.setManagedExperimentId(55l);
		Set<VM> vMs = new HashSet<VM>();
		experiment.setvMs(vMs);
		
		assertEquals(2, experiment.getBonfireExperimentId());
		assertEquals("users", experiment.getBonfireGroupId());
		assertEquals("user", experiment.getBonfireUserId());
		assertEquals(4l, experiment.getId());
		assertEquals("AP", experiment.getApplicationProfile());
		assertEquals("ED", experiment.getSubmittedExperimentDescriptor());
		assertEquals(vMs, experiment.getvMs());
		assertEquals(1l, experiment.getStartTime());
		assertEquals(2l, experiment.getEndTime());
		assertEquals(55l, experiment.getManagedExperimentId());
		
		Experiment experiment2 = new Experiment(5, "ATOS", "A100", 100, 25000, 35000, "application-profile-A100", "submittedExperimentDescriptor-A100");
		assertEquals(5, experiment2.getId());
	}
}
