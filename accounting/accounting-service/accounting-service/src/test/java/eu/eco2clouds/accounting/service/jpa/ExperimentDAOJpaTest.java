package eu.eco2clouds.accounting.service.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.eco2clouds.accounting.datamodel.Experiment;
import eu.eco2clouds.accounting.datamodel.VM;
import eu.eco2clouds.accounting.service.ActionDAO;
import eu.eco2clouds.accounting.service.ExperimentDAO;
import eu.eco2clouds.accounting.service.VMDAO;

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
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/scheduler-db-JPA-test-context.xml")
public class ExperimentDAOJpaTest extends
		AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	private ExperimentDAO experimentDAO;
	@Autowired
	private VMDAO vMDAO;
	@Autowired
	private ActionDAO actionDAO;

	@Test
	public void notNull() {
		if (experimentDAO == null || vMDAO == null || actionDAO == null)
			fail();
	}

	@Test
	public void saveAndGet() {
		int size = experimentDAO.getAll().size();

		Experiment experiment = new Experiment();
		experiment.setApplicationProfile("XXXXXX");
		experiment.setBonfireExperimentId(2l);
		experiment.setBonfireGroupId("groups");
		experiment.setBonfireUserId("pepito");
		experiment.setSubmittedExperimentDescriptor("submitted");
		experiment.setManagedExperimentId(55l);

		boolean saved = experimentDAO.save(experiment);
		assertTrue(saved);

		List<Experiment> experiments = experimentDAO.getAll();
		size = size + 1;
		assertEquals(size, experiments.size());
		Experiment experimentFromDatabase = experiments.get(size - 1);
		assertEquals("XXXXXX", experimentFromDatabase.getApplicationProfile());
		assertEquals(2l, experimentFromDatabase.getBonfireExperimentId());
		assertEquals("groups", experimentFromDatabase.getBonfireGroupId());
		assertEquals("pepito", experimentFromDatabase.getBonfireUserId());
		assertEquals("submitted",
				experimentFromDatabase.getSubmittedExperimentDescriptor());
		assertEquals(55l, experimentFromDatabase.getManagedExperimentId());
	}

	@Test
	public void getById() {
		int size = experimentDAO.getAll().size();

		Experiment experiment = new Experiment();
		experiment.setApplicationProfile("XXXXXX");
		experiment.setBonfireExperimentId(2l);
		experiment.setBonfireGroupId("groups");
		experiment.setBonfireUserId("pepito");
		experiment.setSubmittedExperimentDescriptor("submitted");
		experiment.setManagedExperimentId(55l);

		boolean saved = experimentDAO.save(experiment);
		assertTrue(saved);

		Experiment experimentFromDatabase = experimentDAO.getAll().get(size);
		int id = experimentFromDatabase.getId();
		experimentFromDatabase = experimentDAO.getById(id);
		assertEquals("XXXXXX", experimentFromDatabase.getApplicationProfile());
		assertEquals(2l, experimentFromDatabase.getBonfireExperimentId());
		assertEquals("groups", experimentFromDatabase.getBonfireGroupId());
		assertEquals("pepito", experimentFromDatabase.getBonfireUserId());
		assertEquals("submitted",
				experimentFromDatabase.getSubmittedExperimentDescriptor());
		assertEquals(55l, experimentFromDatabase.getManagedExperimentId());

		Experiment nullExperiment = experimentDAO.getById(300000);
		assertEquals(null, nullExperiment);
	}

	@Test
	public void getByGroupId() {
	
		Experiment experiment1 = new Experiment();
		Experiment experiment2 = new Experiment();
		Experiment experiment3 = new Experiment();
		Experiment experiment4 = new Experiment();
		

		List<Experiment> experiments = new ArrayList<Experiment>();

		experiment1.setApplicationProfile("XXXXXX1");
		experiment1.setBonfireExperimentId(2l);
		experiment1.setBonfireGroupId("ATOS");
		experiment1.setBonfireUserId("A1000");
		experiment1.setSubmittedExperimentDescriptor("submitted");

		experiment2.setApplicationProfile("XXXXXX2");
		experiment2.setBonfireExperimentId(2l);
		experiment2.setBonfireGroupId("ATOS");
		experiment2.setBonfireUserId("A1010");
		experiment2.setSubmittedExperimentDescriptor("submitted");

		experiment3.setApplicationProfile("XXXXXX3");
		experiment3.setBonfireExperimentId(2l);
		experiment3.setBonfireGroupId("INRIA");
		experiment3.setBonfireUserId("I2000");
		experiment3.setSubmittedExperimentDescriptor("submitted");

		experiment4.setApplicationProfile("XXXXXX4");
		experiment4.setBonfireExperimentId(2l);
		experiment4.setBonfireGroupId("EPCC");
		experiment4.setBonfireUserId("E1000");
		experiment4.setSubmittedExperimentDescriptor("submitted");

		experiments.add(experiment1);
		experiments.add(experiment2);
		experiments.add(experiment3);
		experiments.add(experiment4);

		boolean saved = experimentDAO.save(experiment1);
		assertTrue(saved);
		saved = experimentDAO.save(experiment2);
		assertTrue(saved);
		saved = experimentDAO.save(experiment3);
		assertTrue(saved);
		saved = experimentDAO.save(experiment4);
		assertTrue(saved);
		
		List<String> groups = new ArrayList<String>();
		groups.add("ATOS");
		groups.add("EPCC");

		List<Experiment> experimentsFromDatabase = experimentDAO
				.getListExperimentByGroupId(groups);
		assertEquals(3, experimentsFromDatabase.size());
		assertEquals("XXXXXX1", experimentsFromDatabase.get(0).getApplicationProfile());
		assertEquals("ATOS", experimentsFromDatabase.get(0).getBonfireGroupId());
		assertEquals("A1000", experimentsFromDatabase.get(0).getBonfireUserId());
		
		assertEquals("XXXXXX2", experimentsFromDatabase.get(1).getApplicationProfile());
	    assertEquals("ATOS", experimentsFromDatabase.get(1).getBonfireGroupId());
		assertEquals("A1010", experimentsFromDatabase.get(1).getBonfireUserId());
		
		
		assertEquals("XXXXXX4", experimentsFromDatabase.get(2).getApplicationProfile());
	    assertEquals("EPCC", experimentsFromDatabase.get(2).getBonfireGroupId());
		assertEquals("E1000", experimentsFromDatabase.get(2).getBonfireUserId());
		

		List<String> noNgroups = new ArrayList<String>();
		noNgroups.add("NonGroup");
		
		List<Experiment> nullExperiment = experimentDAO
				.getListExperimentByGroupId(noNgroups); 
		 assertEquals(0, nullExperiment.size());
	}

	
	@Test
	public void getByIdAndGroupId() {
	
		Experiment experiment1 = new Experiment();
		Experiment experiment2 = new Experiment();
		Experiment experiment3 = new Experiment();
		Experiment experiment4 = new Experiment();
		

		List<Experiment> experiments = new ArrayList<Experiment>();

		experiment1.setApplicationProfile("XXXXXX1");
		experiment1.setBonfireExperimentId(2l);
		experiment1.setBonfireGroupId("ATOS");
		experiment1.setBonfireUserId("A1000");
		experiment1.setSubmittedExperimentDescriptor("submitted");

		experiment2.setApplicationProfile("XXXXXX2");
		experiment2.setBonfireExperimentId(2l);
		experiment2.setBonfireGroupId("ATOS");
		experiment2.setBonfireUserId("A1010");
		experiment2.setSubmittedExperimentDescriptor("submitted");

		experiment3.setApplicationProfile("XXXXXX3");
		experiment3.setBonfireExperimentId(2l);
		experiment3.setBonfireGroupId("INRIA");
		experiment3.setBonfireUserId("I2000");
		experiment3.setSubmittedExperimentDescriptor("submitted");

		experiment4.setApplicationProfile("XXXXXX4");
		experiment4.setBonfireExperimentId(2l);
		experiment4.setBonfireGroupId("EPCC");
		experiment4.setBonfireUserId("E1000");
		experiment4.setSubmittedExperimentDescriptor("submitted");

		experiments.add(experiment1);
		experiments.add(experiment2);
		experiments.add(experiment3);
		experiments.add(experiment4);

		boolean saved = experimentDAO.save(experiment1);
		assertTrue(saved);
		saved = experimentDAO.save(experiment2);
		assertTrue(saved);
		saved = experimentDAO.save(experiment3);
		assertTrue(saved);
		saved = experimentDAO.save(experiment4);
		assertTrue(saved);
		
		List<String> groups = new ArrayList<String>();
		groups.add("ATOS");
		groups.add("INRIA");
		
		Experiment experiment = experimentDAO.getAll().get(0);

		int id = experiment.getId();
		
		Experiment experimentsFromDatabase = experimentDAO.getExperimentByGroupId(id, groups);

		assertEquals("XXXXXX1", experimentsFromDatabase.getApplicationProfile());
		assertEquals("ATOS", experimentsFromDatabase.getBonfireGroupId());
		assertEquals("A1000", experimentsFromDatabase.getBonfireUserId());
		
		

		List<String> noNgroups = new ArrayList<String>();
		noNgroups.add("NonGroup");
		
		List<Experiment> nullExperiment = experimentDAO
				.getListExperimentByGroupId(noNgroups); 
		 assertEquals(0, nullExperiment.size());
	}
	
	
	@Test
	public void delete() {
		int size = experimentDAO.getAll().size();

		Experiment experiment = new Experiment();
		experiment.setApplicationProfile("XXXXXX");
		experiment.setBonfireExperimentId(2l);
		experiment.setBonfireGroupId("groups");
		experiment.setBonfireUserId("pepito");
		experiment.setSubmittedExperimentDescriptor("submitted");

		boolean saved = experimentDAO.save(experiment);
		assertTrue(saved);

		Experiment experimentFromDatabase = experimentDAO.getAll().get(size);

		boolean deleted = experimentDAO.delete(experimentFromDatabase);
		assertTrue(deleted);

		deleted = experimentDAO.delete(experimentFromDatabase);
		assertTrue(!deleted);

		experimentFromDatabase = experimentDAO.getById(size);
		assertEquals(null, experimentFromDatabase);
	}

	@Test
	public void update() {
		int size = experimentDAO.getAll().size();

		Experiment experiment = new Experiment();
		experiment.setApplicationProfile("XXXXXX");
		experiment.setBonfireExperimentId(2l);
		experiment.setBonfireGroupId("groups");
		experiment.setBonfireUserId("pepito");
		experiment.setSubmittedExperimentDescriptor("submitted");

		boolean saved = experimentDAO.save(experiment);
		assertTrue(saved);

		Experiment experimentFromDatabase = experimentDAO.getAll().get(size);
		assertEquals("XXXXXX", experimentFromDatabase.getApplicationProfile());
		assertEquals(2l, experimentFromDatabase.getBonfireExperimentId());
		assertEquals("groups", experimentFromDatabase.getBonfireGroupId());
		assertEquals("pepito", experimentFromDatabase.getBonfireUserId());
		assertEquals("submitted",
				experimentFromDatabase.getSubmittedExperimentDescriptor());

		experimentFromDatabase.setApplicationProfile("1111");
		boolean updated = experimentDAO.update(experimentFromDatabase);
		assertTrue(updated);

		experimentFromDatabase = experimentDAO.getAll().get(size);
		assertEquals("1111", experimentFromDatabase.getApplicationProfile());
		assertEquals(2l, experimentFromDatabase.getBonfireExperimentId());
		assertEquals("groups", experimentFromDatabase.getBonfireGroupId());
		assertEquals("pepito", experimentFromDatabase.getBonfireUserId());
		assertEquals("submitted",
				experimentFromDatabase.getSubmittedExperimentDescriptor());
	}

	@Test
	public void cascadeVMs() {
		int size = experimentDAO.getAll().size();

		Experiment experiment = new Experiment();
		experiment.setApplicationProfile("XXXXXX");
		experiment.setBonfireExperimentId(2l);
		experiment.setBonfireGroupId("groups");
		experiment.setBonfireUserId("pepito");
		experiment.setSubmittedExperimentDescriptor("submitted");

		VM vm1 = new VM();
		vm1.setBonfireUrl("url1");

		VM vm2 = new VM();
		vm2.setBonfireUrl("url2");

		Set<VM> vMs = new HashSet<VM>();
		vMs.add(vm1);
		vMs.add(vm2);
		experiment.setvMs(vMs);

		boolean saved = experimentDAO.save(experiment);
		assertTrue(saved);

		Experiment experimentFromDatabase = experimentDAO.getAll().get(size);
		assertEquals("XXXXXX", experimentFromDatabase.getApplicationProfile());
		assertEquals(2l, experimentFromDatabase.getBonfireExperimentId());
		assertEquals("groups", experimentFromDatabase.getBonfireGroupId());
		assertEquals("pepito", experimentFromDatabase.getBonfireUserId());
		assertEquals("submitted",
				experimentFromDatabase.getSubmittedExperimentDescriptor());
		int id = experimentFromDatabase.getId();

		List<VM> vMsFromDatabase = vMDAO.getAll();
		assertEquals(2, vMsFromDatabase.size());

		experimentFromDatabase = experimentDAO.deleteVM(experimentFromDatabase,
				vm1);

		experimentFromDatabase = experimentDAO.getById(id);
		assertEquals(1, experimentFromDatabase.getvMs().size());

		vMsFromDatabase = vMDAO.getAll();
		assertEquals(1, vMsFromDatabase.size());

		boolean deleted = experimentDAO.delete(experimentFromDatabase);
		assertTrue(deleted);

		List<Experiment> experiments = experimentDAO.getAll();
		assertEquals(0, experiments.size());

		vMsFromDatabase = vMDAO.getAll();
		assertEquals(0, vMsFromDatabase.size());
	}
	
	@Test
	public void getLastExperimentTest() {
		Experiment experiment1 = new Experiment();
		experiment1.setApplicationProfile("XXXXXX");
		experiment1.setBonfireExperimentId(2l);
		experiment1.setBonfireGroupId("groups");
		experiment1.setBonfireUserId("pepito");
		experiment1.setSubmittedExperimentDescriptor("submitted");
		experiment1.setManagedExperimentId(55l);
		
		Experiment experiment2 = new Experiment();
		experiment2.setApplicationProfile("XXXXXX2");
		experiment2.setBonfireExperimentId(22l);
		experiment2.setBonfireGroupId("groups2");
		experiment2.setBonfireUserId("pepito2");
		experiment2.setSubmittedExperimentDescriptor("submitted2");
		experiment2.setManagedExperimentId(552l);

		boolean saved = experimentDAO.save(experiment1);
		assertTrue(saved);
		saved = experimentDAO.save(experiment2);
		assertTrue(saved);

		Experiment experimentFromDatabase = experimentDAO.getLastExperiment();
		assertEquals("XXXXXX2", experimentFromDatabase.getApplicationProfile());
		assertEquals(22l, experimentFromDatabase.getBonfireExperimentId());
		assertEquals("groups2", experimentFromDatabase.getBonfireGroupId());
		assertEquals("pepito2", experimentFromDatabase.getBonfireUserId());
		assertEquals("submitted2", experimentFromDatabase.getSubmittedExperimentDescriptor());
		assertEquals(552l, experimentFromDatabase.getManagedExperimentId());
	}
}
