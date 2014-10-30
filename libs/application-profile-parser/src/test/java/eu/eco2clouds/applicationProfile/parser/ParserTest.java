package eu.eco2clouds.applicationProfile.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import eu.eco2clouds.applicationProfile.datamodel.Adaptation;
import eu.eco2clouds.applicationProfile.datamodel.ApplicationProfile;
import eu.eco2clouds.applicationProfile.datamodel.Branch;
import eu.eco2clouds.applicationProfile.datamodel.Branches;
import eu.eco2clouds.applicationProfile.datamodel.Compute;
import eu.eco2clouds.applicationProfile.datamodel.Constraint;
import eu.eco2clouds.applicationProfile.datamodel.Data;
import eu.eco2clouds.applicationProfile.datamodel.DataDependency;
import eu.eco2clouds.applicationProfile.datamodel.ExperimentDescriptor;
import eu.eco2clouds.applicationProfile.datamodel.Flow;
import eu.eco2clouds.applicationProfile.datamodel.Iteration;
import eu.eco2clouds.applicationProfile.datamodel.Loop;
import eu.eco2clouds.applicationProfile.datamodel.Requirement;
import eu.eco2clouds.applicationProfile.datamodel.Resource;
import eu.eco2clouds.applicationProfile.datamodel.ResourceCompute;
import eu.eco2clouds.applicationProfile.datamodel.Sequence;

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
public class ParserTest {

	@Test
	public void getApplicationProfileTest() throws Exception {
		String applicationProfile = "{ \"applicationprofile\": {"
				+ "\"optimize\": false,"
				+ "\"flow\": {"
				+ "          \"sequence\": ["
				+ "              {\"task\":\"task1\"},"
				+ "              {\"task\":\"task2\"},"
				+ "                {"
				+ "                  \"branches\": ["
				+ "                        {"
				+ "                          \"branch\": ["
				+ "                                {"
				+ "                                  \"prob\": 0.3"
				+ "                                },"
				+ "                              {\"task\":\"task3\"}"
				+ "                            ]"
				+ "                        },"
				+ "                        {"
				+ "                          \"branch\": ["
				+ "                                {"
				+ "                                  \"prob\": 0.7"
				+ "                                },"
				+ "                              {\"task\":\"task4\"}"
				+ "                            ]"
				+ "                        }"
				+ "                    ]"
				+ "                },"
				+ "              {\"task\": \"task5\"},"
				+ "                {"
				+ "                  \"loop\": ["
				+ "                        {"
				+ "                          \"sequence\": ["
				+ "                              {\"task\":\"task6\"},"
				+ "                              {\"task\":\"task7\"}"
				+ "                            ]"
				+ "                        },"
				+ "                        {"
				+ "                          \"iteration\": {"
				+ "                              \"num\": 1,"
				+ "                              \"prob\": 0.7"
				+ "                            }"
				+ "                        },"
				+ "                        {"
				+ "                          \"iteration\": {"
				+ "                              \"num\": 2,"
				+ "                              \"prob\": 0.3"
				+ "                            }"
				+ "                        },"
				+ "                        {"
				+ "                          \"iteration\": {"
				+ "                              \"num\": 3,"
				+ "                              \"prob\": 0.1"
				+ "                            }"
				+ "                        },"
				+ "                        {"
				+ "                          \"iteration\": {"
				+ "                              \"num\": 4,"
				+ "                              \"prob\": 0.01"
				+ "                            }"
				+ "                        }"
				+ "                    ]"
				+ "                }"
				+ "            ]"
				+ "        },"
				+ "      \"requirements\": {"
				+ "          \"constraints\": ["
				+ "                {"
				+ "                  \"indicator\":\"A-PUE\","
				+ "                  \"element\":\"VM1\","
				+ "                  \"operator\":\"<\","
				+ "                  \"value\":\"1.4\""
				+ "                },"
				+ "                {"
				+ "                  \"indicator\":\"Responsetime\","
				+ "                  \"element\":\"Task1\","
				+ "                  \"operator\":\"<\","
				+ "                  \"value\":\"1ms\""
				+ "                },"
				+ "                {"
				+ "                  \"indicator\":\"Responsetime\","
				+ "                  \"element\":\"Application\","
				+ "                  \"operator\":\"<\","
				+ "                  \"value\":\"10ms\""
				+ "                },"
				+ "                {"
				+ "                  \"indicator\":\"CPUUsage\","
				+ "                  \"element\":\"VM1\","
				+ "                  \"operator\":\"><\","
				+ "                  \"values\": ["
				+ "                      \"60\","
				+ "                      \"90\""
				+ "                    ]"
				+ "                }"
				+ "            ]"
				+ "        },"
				+ "      \"resources\": {"
				+ "          \"name\":\"MyExperiment\","
				+ "          \"description\":\"Experimentdescription\","
				+ "          \"duration\": 120,"
				+ "          \"resources\": ["
				+ "                {"
				+ "                  \"compute\": {"
				+ "                      \"name\":\"Client\","
				+ "                      \"description\":\"A description of the client\","
				+ "                      \"instanceType\":\"small\","
				+ "                      \"locations\": ["
				+ "                          \"fr-inria\""
				+ "                        ],"
				+ "                      \"resources\": ["
				+ "                            {"
				+ "                              \"storage\":\"@BonFIREDebianSqueezev5\""
				+ "                            },"
				+ "                            {"
				+ "                              \"network\":\"@BonFIREWAN\""
				+ "                            }"
				+ "                        ]"
				+ "                    }"
				+ "                },"
				+ "                {"
				+ "                  \"compute\": {"
				+ "                      \"name\":\"Server\","
				+ "                      \"description\":\"A description of the server\","
				+ "                      \"instanceType\":\"small\","
				+ "                      \"locations\": ["
				+ "                          \"uk-epcc\""
				+ "                        ],"
				+ "                      \"resources\": ["
				+ "                            {"
				+ "                              \"storage\":\"@BonFIREDebianSqueezev3\""
				+ "                            },"
				+ "                            {"
				+ "                              \"network\":\"@BonFIREWAN\""
				+ "                            }" + "                        ]"
				+ "                    }" + "                }"
				+ "            ]" + "        }," + "      \"data\": {"
				+ "          \"datadependency\": ["
				+ "              {\"task\":\"task1\"},"
				+ "              {\"task\":\"task2\"}" + "            ]"
				+ "        },"

				+ "\"adaptation\": {" + "\"allowed\": \"yes\"" + "   }" + "}"
				+ "}";

		ApplicationProfile applicationProfileObject = Parser.getApplicationProfile(applicationProfile);
		
		// Optimize
		assertFalse(applicationProfileObject.isOptimize());

		// Flow Element
		assertEquals("task1", applicationProfileObject.getFlow().getSequence()
				.get(0).getTask());
		assertEquals("task2", applicationProfileObject.getFlow().getSequence()
				.get(1).getTask());
		assertEquals(0.3,
				applicationProfileObject.getFlow().getSequence().get(2)
						.getBranches().get(0).getBranch().get(0).getProb(),
				0.01);

		assertEquals("task3", applicationProfileObject.getFlow().getSequence()
				.get(2).getBranches().get(0).getBranch().get(1).getTask());
		assertEquals(0.7,
				applicationProfileObject.getFlow().getSequence().get(2)
						.getBranches().get(1).getBranch().get(0).getProb(),
				0.01);
		assertEquals("task4", applicationProfileObject.getFlow().getSequence()
				.get(2).getBranches().get(1).getBranch().get(1).getTask());
		assertEquals("task5", applicationProfileObject.getFlow().getSequence()
				.get(3).getTask());

		assertEquals("task6", applicationProfileObject.getFlow().getSequence()
				.get(4).getLoop().get(0).getSequence().get(0).getTask());
		assertEquals("task7", applicationProfileObject.getFlow().getSequence()
				.get(4).getLoop().get(0).getSequence().get(1).getTask());

		assertEquals(1, applicationProfileObject.getFlow().getSequence().get(4)
				.getLoop().get(1).getIteration().get(0).getNum());
		assertEquals(0.7,
				applicationProfileObject.getFlow().getSequence().get(4)
						.getLoop().get(1).getIteration().get(0).getProb(), 0.01);

		assertEquals(2, applicationProfileObject.getFlow().getSequence().get(4)
				.getLoop().get(2).getIteration().get(0).getNum());
		assertEquals(0.3,
				applicationProfileObject.getFlow().getSequence().get(4)
						.getLoop().get(2).getIteration().get(0).getProb(), 0.01);

		assertEquals(3, applicationProfileObject.getFlow().getSequence().get(4)
				.getLoop().get(3).getIteration().get(0).getNum());
		assertEquals(0.1,
				applicationProfileObject.getFlow().getSequence().get(4)
						.getLoop().get(3).getIteration().get(0).getProb(), 0.01);

		assertEquals(4, applicationProfileObject.getFlow().getSequence().get(4)
				.getLoop().get(4).getIteration().get(0).getNum());
		assertEquals(0.01, applicationProfileObject.getFlow().getSequence()
				.get(4).getLoop().get(4).getIteration().get(0).getProb(), 0.01);

		// Requirement element
		assertEquals("A-PUE", applicationProfileObject.getRequirement()
				.getConstraints().get(0).getIndicator());
		assertEquals("VM1", applicationProfileObject.getRequirement()
				.getConstraints().get(0).getElement());
		assertEquals("<", applicationProfileObject.getRequirement()
				.getConstraints().get(0).getOperator());
		assertEquals("1.4", applicationProfileObject.getRequirement()
				.getConstraints().get(0).getValue());

		assertEquals("Responsetime", applicationProfileObject.getRequirement()
				.getConstraints().get(1).getIndicator());
		assertEquals("Task1", applicationProfileObject.getRequirement()
				.getConstraints().get(1).getElement());
		assertEquals("<", applicationProfileObject.getRequirement()
				.getConstraints().get(1).getOperator());
		assertEquals("1ms", applicationProfileObject.getRequirement()
				.getConstraints().get(1).getValue());

		assertEquals("Responsetime", applicationProfileObject.getRequirement()
				.getConstraints().get(2).getIndicator());
		assertEquals("Application", applicationProfileObject.getRequirement()
				.getConstraints().get(2).getElement());
		assertEquals("<", applicationProfileObject.getRequirement()
				.getConstraints().get(2).getOperator());
		assertEquals("10ms", applicationProfileObject.getRequirement()
				.getConstraints().get(2).getValue());

		assertEquals("CPUUsage", applicationProfileObject.getRequirement()
				.getConstraints().get(3).getIndicator());
		assertEquals("VM1", applicationProfileObject.getRequirement()
				.getConstraints().get(3).getElement());
		assertEquals("><", applicationProfileObject.getRequirement()
				.getConstraints().get(3).getOperator());
		assertEquals("60", applicationProfileObject.getRequirement()
				.getConstraints().get(3).getValues().get(0));
		assertEquals("90", applicationProfileObject.getRequirement()
				.getConstraints().get(3).getValues().get(1));

		// Experiment Descriptor
		assertEquals(120, applicationProfileObject.getExperimentDescriptor()
				.getDuration());
		assertEquals("MyExperiment", applicationProfileObject
				.getExperimentDescriptor().getName());
		// Compute 1
		assertEquals("Client", applicationProfileObject
				.getExperimentDescriptor().getResourcesCompute().get(0)
				.getCompute().getName());

		assertEquals("A description of the client", applicationProfileObject
				.getExperimentDescriptor().getResourcesCompute().get(0)
				.getCompute().getDescription());
		assertEquals("small", applicationProfileObject
				.getExperimentDescriptor().getResourcesCompute().get(0)
				.getCompute().getInstanceType());
		assertEquals("fr-inria", applicationProfileObject
				.getExperimentDescriptor().getResourcesCompute().get(0)
				.getCompute().getLocations().get(0));

		assertEquals("@BonFIREDebianSqueezev5", applicationProfileObject
				.getExperimentDescriptor().getResourcesCompute().get(0)
				.getCompute().getResourceCompute().get(0).getStorage());
		assertEquals("@BonFIREWAN", applicationProfileObject
				.getExperimentDescriptor().getResourcesCompute().get(0)
				.getCompute().getResourceCompute().get(1).getNetwork());

		// Compute 2
		assertEquals("Server", applicationProfileObject
				.getExperimentDescriptor().getResourcesCompute().get(1)
				.getCompute().getName());

		assertEquals("A description of the server", applicationProfileObject
				.getExperimentDescriptor().getResourcesCompute().get(1)
				.getCompute().getDescription());
		assertEquals("small", applicationProfileObject
				.getExperimentDescriptor().getResourcesCompute().get(1)
				.getCompute().getInstanceType());
		assertEquals("uk-epcc", applicationProfileObject
				.getExperimentDescriptor().getResourcesCompute().get(1)
				.getCompute().getLocations().get(0));

		assertEquals("@BonFIREDebianSqueezev3", applicationProfileObject
				.getExperimentDescriptor().getResourcesCompute().get(1)
				.getCompute().getResourceCompute().get(0).getStorage());
		assertEquals("@BonFIREWAN", applicationProfileObject
				.getExperimentDescriptor().getResourcesCompute().get(1)
				.getCompute().getResourceCompute().get(1).getNetwork());

		// Data element
		assertEquals("task1", applicationProfileObject.getData()
				.getDataDependency().get(0).getTask());
		assertEquals("task2", applicationProfileObject.getData()
				.getDataDependency().get(1).getTask());

		// Adaptation element
		assertEquals("yes", applicationProfileObject.getAdaptation()
				.getAllowed());
		
		applicationProfile = "{ \"applicationprofile\": {"
				+ "\"flow\": {"
				+ "          \"sequence\": ["
				+ "              {\"task\":\"task1\"},"
				+ "              {\"task\":\"task2\"},"
				+ "                {"
				+ "                  \"branches\": ["
				+ "                        {"
				+ "                          \"branch\": ["
				+ "                                {"
				+ "                                  \"prob\": 0.3"
				+ "                                },"
				+ "                              {\"task\":\"task3\"}"
				+ "                            ]"
				+ "                        },"
				+ "                        {"
				+ "                          \"branch\": ["
				+ "                                {"
				+ "                                  \"prob\": 0.7"
				+ "                                },"
				+ "                              {\"task\":\"task4\"}"
				+ "                            ]"
				+ "                        }"
				+ "                    ]"
				+ "                },"
				+ "              {\"task\": \"task5\"},"
				+ "                {"
				+ "                  \"loop\": ["
				+ "                        {"
				+ "                          \"sequence\": ["
				+ "                              {\"task\":\"task6\"},"
				+ "                              {\"task\":\"task7\"}"
				+ "                            ]"
				+ "                        },"
				+ "                        {"
				+ "                          \"iteration\": {"
				+ "                              \"num\": 1,"
				+ "                              \"prob\": 0.7"
				+ "                            }"
				+ "                        },"
				+ "                        {"
				+ "                          \"iteration\": {"
				+ "                              \"num\": 2,"
				+ "                              \"prob\": 0.3"
				+ "                            }"
				+ "                        },"
				+ "                        {"
				+ "                          \"iteration\": {"
				+ "                              \"num\": 3,"
				+ "                              \"prob\": 0.1"
				+ "                            }"
				+ "                        },"
				+ "                        {"
				+ "                          \"iteration\": {"
				+ "                              \"num\": 4,"
				+ "                              \"prob\": 0.01"
				+ "                            }"
				+ "                        }"
				+ "                    ]"
				+ "                }"
				+ "            ]"
				+ "        },"
				+ "      \"requirements\": {"
				+ "          \"constraints\": ["
				+ "                {"
				+ "                  \"indicator\":\"A-PUE\","
				+ "                  \"element\":\"VM1\","
				+ "                  \"operator\":\"<\","
				+ "                  \"value\":\"1.4\""
				+ "                },"
				+ "                {"
				+ "                  \"indicator\":\"Responsetime\","
				+ "                  \"element\":\"Task1\","
				+ "                  \"operator\":\"<\","
				+ "                  \"value\":\"1ms\""
				+ "                },"
				+ "                {"
				+ "                  \"indicator\":\"Responsetime\","
				+ "                  \"element\":\"Application\","
				+ "                  \"operator\":\"<\","
				+ "                  \"value\":\"10ms\""
				+ "                },"
				+ "                {"
				+ "                  \"indicator\":\"CPUUsage\","
				+ "                  \"element\":\"VM1\","
				+ "                  \"operator\":\"><\","
				+ "                  \"values\": ["
				+ "                      \"60\","
				+ "                      \"90\""
				+ "                    ]"
				+ "                }"
				+ "            ]"
				+ "        },"
				+ "      \"resources\": {"
				+ "          \"name\":\"MyExperiment\","
				+ "          \"description\":\"Experimentdescription\","
				+ "          \"duration\": 120,"
				+ "          \"resources\": ["
				+ "                {"
				+ "                  \"compute\": {"
				+ "                      \"name\":\"Client\","
				+ "                      \"description\":\"A description of the client\","
				+ "                      \"instanceType\":\"small\","
				+ "                      \"locations\": ["
				+ "                          \"fr-inria\""
				+ "                        ],"
				+ "                      \"resources\": ["
				+ "                            {"
				+ "                              \"storage\":\"@BonFIREDebianSqueezev5\""
				+ "                            },"
				+ "                            {"
				+ "                              \"network\":\"@BonFIREWAN\""
				+ "                            }"
				+ "                        ]"
				+ "                    }"
				+ "                },"
				+ "                {"
				+ "                  \"compute\": {"
				+ "                      \"name\":\"Server\","
				+ "                      \"description\":\"A description of the server\","
				+ "                      \"instanceType\":\"small\","
				+ "                      \"locations\": ["
				+ "                          \"uk-epcc\""
				+ "                        ],"
				+ "                      \"resources\": ["
				+ "                            {"
				+ "                              \"storage\":\"@BonFIREDebianSqueezev3\""
				+ "                            },"
				+ "                            {"
				+ "                              \"network\":\"@BonFIREWAN\""
				+ "                            }" + "                        ]"
				+ "                    }" + "                }"
				+ "            ]" + "        }," + "      \"data\": {"
				+ "          \"datadependency\": ["
				+ "              {\"task\":\"task1\"},"
				+ "              {\"task\":\"task2\"}" + "            ]"
				+ "        },"

				+ "\"adaptation\": {" + "\"allowed\": \"yes\"" + "   }" + "}"
				+ "}";
		
		applicationProfileObject = Parser.getApplicationProfile(applicationProfile);
		
		// Optimize
		assertTrue(applicationProfileObject.isOptimize());
	}

	public void getJsonFromApplicationProfileTest() throws Exception {
		ApplicationProfile applicationProfileObject = new ApplicationProfile();

		// Flow Element
		Flow flow = new Flow();

		ArrayList<Sequence> sequence = new ArrayList<Sequence>();

		Sequence sequenceElem0 = new Sequence();
		Sequence sequenceElem1 = new Sequence();
		Sequence sequenceElem2 = new Sequence();
		Sequence sequenceElem3 = new Sequence();
		Sequence sequenceElem4 = new Sequence();

		// Sequence Elem 0
		sequenceElem0.setTask("task1");

		// Sequence Elem 1
		sequenceElem1.setTask("task2");

		// Sequence Elem 2
		Branch branch1 = new Branch();
		Branch branch2 = new Branch();
		Branch branch3 = new Branch();
		Branch branch4 = new Branch();

		branch1.setProb(0.3);
		branch2.setTask("task3");

		branch3.setProb(0.7);
		branch4.setTask("task4");

		ArrayList<Branch> branchElem = new ArrayList<Branch>();
		ArrayList<Branch> branchElem2 = new ArrayList<Branch>();
		branchElem.add(branch1);
		branchElem.add(branch2);
		branchElem2.add(branch3);
		branchElem2.add(branch4);

		Branches branchesElem1 = new Branches();
		Branches branchesElem2 = new Branches();

		branchesElem1.setBranch(branchElem);
		branchesElem2.setBranch(branchElem2);

		ArrayList<Branches> branches = new ArrayList<Branches>();

		branches.add(branchesElem1);
		branches.add(branchesElem2);

		sequenceElem2.setBranches(branches);

		// Sequence Elem 3
		sequenceElem3.setTask("task5");

		Iteration iterationElem1 = new Iteration();
		Iteration iterationElem2 = new Iteration();
		Iteration iterationElem3 = new Iteration();
		Iteration iterationElem4 = new Iteration();

		Sequence sequenceLoopElem1 = new Sequence();
		Sequence sequenceLoopElem2 = new Sequence();

		sequenceLoopElem1.setTask("task6");
		sequenceLoopElem2.setTask("task7");
		ArrayList<Sequence> sequenceLoop = new ArrayList<Sequence>();

		sequenceLoop.add(sequenceLoopElem1);
		sequenceLoop.add(sequenceLoopElem2);

		iterationElem1.setNum(1);
		iterationElem1.setProb(0.7);

		iterationElem2.setNum(2);
		iterationElem2.setProb(0.3);

		iterationElem3.setNum(3);
		iterationElem3.setProb(0.1);

		iterationElem4.setNum(4);
		iterationElem4.setProb(0.01);

		Loop loopElem0 = new Loop();
		Loop loopElem1 = new Loop();
		Loop loopElem2 = new Loop();
		Loop loopElem3 = new Loop();
		Loop loopElem4 = new Loop();

		ArrayList<Iteration> iteration1 = new ArrayList<Iteration>();
		ArrayList<Iteration> iteration2 = new ArrayList<Iteration>();
		ArrayList<Iteration> iteration3 = new ArrayList<Iteration>();
		ArrayList<Iteration> iteration4 = new ArrayList<Iteration>();

		iteration1.add(iterationElem1);
		iteration2.add(iterationElem2);
		iteration3.add(iterationElem3);
		iteration4.add(iterationElem4);

		loopElem0.setSequence(sequenceLoop);
		loopElem1.setIteration(iteration1);
		loopElem2.setIteration(iteration2);
		loopElem3.setIteration(iteration3);
		loopElem4.setIteration(iteration4);

		ArrayList<Loop> loopList = new ArrayList<Loop>();
		loopList.add(loopElem0);
		loopList.add(loopElem1);
		loopList.add(loopElem2);
		loopList.add(loopElem3);
		loopList.add(loopElem4);

		// Sequence Elem4
		sequenceElem4.setLoop(loopList);

		sequence.add(sequenceElem0);
		sequence.add(sequenceElem1);
		sequence.add(sequenceElem2);
		sequence.add(sequenceElem3);
		sequence.add(sequenceElem4);

		flow.setSequence(sequence);

		// Requirement element
		Requirement requirement = new Requirement();

		Constraint constraint1 = new Constraint();
		Constraint constraint2 = new Constraint();
		Constraint constraint3 = new Constraint();
		Constraint constraint4 = new Constraint();

		constraint1.setIndicator("A-PUE");
		constraint1.setElement("VM1");
		constraint1.setOperator("<");
		constraint1.setValue("1.4");

		constraint2.setIndicator("Responsetime");
		constraint2.setElement("Task1");
		constraint2.setOperator("<");
		constraint2.setValue("1ms");

		constraint3.setIndicator("Responsetime");
		constraint3.setElement("Application");
		constraint3.setOperator("<");
		constraint3.setValue("10ms");

		constraint4.setIndicator("CPUUsage");
		constraint4.setElement("VM1");
		constraint4.setOperator("><");
		ArrayList<String> values = new ArrayList<String>();
		values.add("60");
		values.add("90");
		constraint4.setValues(values);

		ArrayList<Constraint> constrainsts = new ArrayList<Constraint>();
		constrainsts.add(constraint1);
		constrainsts.add(constraint2);
		constrainsts.add(constraint3);
		constrainsts.add(constraint4);

		requirement.setConstraints(constrainsts);

		// Resources element
		ExperimentDescriptor expDesc = new ExperimentDescriptor();
		expDesc.setDescription("Experiment description");
		expDesc.setDuration(120);
		expDesc.setName("MyExperiment");

		// Compute 1
		Compute compute1 = new Compute();

		compute1.setName("Server");
		compute1.setDescription("A description of the server");
		compute1.setInstanceType("small");

		ArrayList<String> locationCompute1 = new ArrayList<String>();
		locationCompute1.add("uk-epcc");

		compute1.setLocations(locationCompute1);

		ArrayList<Resource> resource = new ArrayList<Resource>();
		Resource resourceElem = new Resource();
		resourceElem.setStorage("@BonFIREDebianSqueezev3");
		resourceElem.setNetwork("@BonFIREWAN");
		resource.add(resourceElem);

		compute1.setResourceCompute(resource);

		// Compute 2
		Compute compute2 = new Compute();

		compute2.setName("Server");
		compute2.setDescription("A description of the server");
		compute2.setInstanceType("small");

		ArrayList<String> locationCompute2 = new ArrayList<String>();
		locationCompute2.add("uk-epcc");

		compute2.setLocations(locationCompute2);

		ArrayList<Resource> resourceCompute2 = new ArrayList<Resource>();

		Resource resourceElem2 = new Resource();

		resourceElem2.setStorage("@BonFIREDebianSqueezev3");
		resourceElem2.setNetwork("@BonFIREWAN");

		resourceCompute2.add(resourceElem2);

		compute2.setResourceCompute(resourceCompute2);

		ResourceCompute resourceComputeElem = new ResourceCompute();
		ResourceCompute resourceComputeElem2 = new ResourceCompute();
		resourceComputeElem.setCompute(compute1);
		resourceComputeElem2.setCompute(compute2);

		ArrayList<ResourceCompute> resourceCompute = new ArrayList<ResourceCompute>();

		resourceCompute.add(resourceComputeElem);
		resourceCompute.add(resourceComputeElem2);

		expDesc.setResourcesCompute(resourceCompute);

		// Data Element
		String task = "task1";
		String task2 = "task2";

		DataDependency dataDependency1 = new DataDependency();
		DataDependency dataDependency2 = new DataDependency();

		dataDependency1.setTask(task);
		dataDependency2.setTask(task2);

		ArrayList<DataDependency> dataDependency = new ArrayList<DataDependency>();

		dataDependency.add(dataDependency1);
		dataDependency.add(dataDependency2);

		Data data = new Data();

		data.setDataDependency(dataDependency);
		
		
		// Adaptation element
		Adaptation adaptation = new Adaptation();

		adaptation.setAllowed("yes");


		// Add the different blocks of elements to the applicationProfile Object

		applicationProfileObject.setFlow(flow);
		applicationProfileObject.setRequirement(requirement);
		applicationProfileObject.setExperimentDescriptor(expDesc);
		applicationProfileObject.setData(data);
		applicationProfileObject.setAdaptation(adaptation);

		String applicationProfile = Parser
				.getJSONApplicationProfile(applicationProfileObject);

		System.out.println("ApplicationProfileJsonToObject:"
				+ applicationProfile);
	}
}
