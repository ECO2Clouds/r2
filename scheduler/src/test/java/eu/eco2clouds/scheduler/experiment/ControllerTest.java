package eu.eco2clouds.scheduler.experiment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;
import org.mockito.ArgumentMatcher;

import eu.eco2clouds.applicationProfile.datamodel.ExperimentDescriptor;
import eu.eco2clouds.scheduler.accounting.client.AccountingClient;
import eu.eco2clouds.accounting.datamodel.parser.Experiment;
import eu.eco2clouds.scheduler.em.EMClient;
import eu.eco2clouds.scheduler.em.datamodel.Link;
import eu.eco2clouds.scheduler.em.datamodel.ManagedExperiment;

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
public class ControllerTest {

	@Test
	public void submitExperimentDescriptorTest() {
		EMClient emClientMock = mock(EMClient.class);
		
		ExperimentDescriptor ed = new ExperimentDescriptor();
		ManagedExperiment me = new ManagedExperiment();
		
		when(emClientMock.submitExperiment("test1", ed, 0l)).thenReturn(me);
		
		Controller controller = new Controller();
		controller.emClient = emClientMock;
		
		ManagedExperiment submittedMe = controller.submitExperimentDescriptor("test1", ed);
		assertEquals(me, submittedMe);
	}
	
	@Test
	public void deployExperimentDescriptor() throws Exception {
		EMClient emClientMock = mock(EMClient.class);
		AccountingClient acClient = mock(AccountingClient.class);
		
		ExperimentDescriptor ed = new ExperimentDescriptor();
		ed.setName("ed");
		
		ManagedExperiment me = new ManagedExperiment();
		me.setHref("/managed_experiments/247");
		me.setStatus("QUEUED");
		me.setName("ExperimentName");
		me.setDescription("experiment Description");
		
		ManagedExperiment me2 = new ManagedExperiment();
		me2.setHref("/managed_experiments/247");
		me2.setStatus("DEPLOYABLE");
		
		ManagedExperiment me3 = new ManagedExperiment();
		me3.setHref("/managed_experiments/247");
		me3.setStatus("DEPLOYING");
				
		ManagedExperiment me4 = new ManagedExperiment();
		me4.setHref("/managed_experiments/247");
		me4.setStatus("DEPLOYED");
		
		Link link = new Link();
		link.setHref("https://api.bonfire-project.eu:444/experiments/21272");
		link.setRel("experiment");
		
		ArrayList<Link> links = new ArrayList<Link>();
		links.add(link);
		
		me4.setLinks(links);
		
		ManagedExperiment me6 = new ManagedExperiment();
		me6.setHref("/managed_experiments/247");
		me6.setStatus("FAILED");
		me6.setLinks(links);
		
		// We define now the behavior of the calls to the emClient 
		when(emClientMock.submitExperiment("test1", ed, 0l)).thenReturn(me);
		when(emClientMock.getExperiment("test1", 247)).thenReturn(me)
		                                              .thenReturn(me2)
		                                              .thenReturn(me3)
		                                              .thenReturn(me4);
		
		Experiment experiment = new Experiment();
		experiment.setId(0);
		
		// We define the calls to the accounting client
		when(acClient.createExperiment(argThat(new IsAnExperiment()))).thenReturn(experiment);
		when(acClient.updateExperiment(argThat(new IsAnExperiment()))).thenReturn(experiment);
		
		Controller controller = new Controller();
		controller.emClient = emClientMock;
		controller.acClient = acClient;
		controller.waitingTime = 10;
		
		Experiment experimentFromController = controller.deployExperimentDescritor("test1", "groupId", ed, "application profile");
		long apiExperimentId = experimentFromController.getBonfireExperimentId();
		assertEquals(21272l, apiExperimentId);
		verify(acClient).createExperiment(argThat(new IsAnExperiment()));
		verify(acClient, times(2)).updateExperiment(argThat(new IsAnExperiment()));
		
		EMClient emClientMock2 = mock(EMClient.class);
		// We define now the behavior of the calls to the emClient 
		when(emClientMock2.submitExperiment("test1", ed, 0l)).thenReturn(me);
		when(emClientMock2.getExperiment("test1", 247)).thenReturn(me)
				                                      .thenReturn(me2)
				                                      .thenReturn(me3)
				                                      .thenReturn(me6);
		
		//We define the calls to the accounting client
		when(acClient.createExperiment(argThat(new IsAnExperiment()))).thenReturn(experiment);
		when(acClient.updateExperiment(argThat(new IsAnExperiment()))).thenReturn(experiment);
		
		controller.emClient = emClientMock2;
		
		experimentFromController = controller.deployExperimentDescritor("test1", "groupId", ed, "applciation profile");
		apiExperimentId = experimentFromController.getBonfireExperimentId();
		
		assertEquals(-1l, apiExperimentId);
		verify(acClient, times(2)).createExperiment(argThat(new IsAnExperiment()));
		verify(acClient, times(4)).updateExperiment(argThat(new IsAnExperiment()));
	}
	
	@Test
	public void generateExperiment() {
		ManagedExperiment me = new ManagedExperiment();
		me.setStatus("aaa");
		me.setHref("/managed_experiments/257");
		me.setName("experiment name");
		Link link = new Link();
		link.setHref("https://api.bonfire-project.eu:444/experiments/21272");
		link.setRel("experiment");
		ArrayList<Link> links = new ArrayList<Link>();
		links.add(link);
		
		ExperimentDescriptor ed = new ExperimentDescriptor();
		ed.setName("experiment");
		ed.setDuration(120);
		
		String aplicationProfile = "app";
		
		Date date = new Date();
		
		Controller controller = new Controller();
		
		Experiment original = new Experiment();
		original.setBonfireGroupId("groupId");
		original.setBonfireUserId("userId");
		original.setApplicationProfile(aplicationProfile);
		
		Experiment experiment = controller.convertExperiment(original, me, ed);
		long currentTime = date.getTime();
		long endTime = currentTime + 120 * 60 * 1000;
		
		assertEquals(-1l, experiment.getBonfireExperimentId().longValue());
		assertEquals(257l, experiment.getManagedExperimentId().longValue());
		assertEquals("userId", experiment.getBonfireUserId());
		assertEquals("groupId", experiment.getBonfireGroupId());
		assertEquals("app", experiment.getApplicationProfile());
		assertEquals(currentTime, experiment.getStartTime().longValue(), 100);
		assertEquals(endTime, experiment.getEndTime().longValue(), 100);
		assertTrue(experiment.getSubmittedExperimentDescriptor() != null);
		
		me.setLinks(links);
		me.setStatus("RUNNING");
		experiment = controller.convertExperiment(original, me, ed);
		
		assertEquals(21272l, experiment.getBonfireExperimentId().longValue());
		assertEquals(257l, experiment.getManagedExperimentId().longValue());
		assertEquals("userId", experiment.getBonfireUserId());
		assertEquals("groupId", experiment.getBonfireGroupId());
		assertEquals("app", experiment.getApplicationProfile());
		assertTrue(experiment.getSubmittedExperimentDescriptor() != null);
	}
	
	@Test
	public void testGetEndTime() {
		Controller controller = new Controller();
		Date date = new Date();
		
		long endTime = controller.getEndTime(120);
		long actualEndTime = date.getTime() + 120*60*1000;
		assertEquals(endTime, actualEndTime, 100);
	}
}

class IsAnExperiment extends ArgumentMatcher<Experiment> {

   public boolean matches(Object experiment) {

       return Experiment.class.isInstance(experiment);
   }
}

