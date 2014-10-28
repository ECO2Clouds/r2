package eu.eco2clouds.applicationProfile.datamodel;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.junit.Test;

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
public class FlowTest {

	@Test
	public void testPojo() {

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

		assertEquals("task1", flow.getSequence().get(0).getTask());
		assertEquals("task2", flow.getSequence().get(1).getTask());
		assertEquals(0.3, flow.getSequence().get(2).getBranches().get(0)
				.getBranch().get(0).getProb(), 0.01);

		assertEquals("task3", flow.getSequence().get(2).getBranches().get(0)
				.getBranch().get(1).getTask());
		assertEquals(0.7, flow.getSequence().get(2).getBranches().get(1)
				.getBranch().get(0).getProb(), 0.01);
		assertEquals("task4", flow.getSequence().get(2).getBranches().get(1)
				.getBranch().get(1).getTask());
		assertEquals("task5", flow.getSequence().get(3).getTask());

		assertEquals("task6", flow.getSequence().get(4).getLoop().get(0)
				.getSequence().get(0).getTask());
		assertEquals("task7", flow.getSequence().get(4).getLoop().get(0)
				.getSequence().get(1).getTask());
		
		assertEquals(1, flow.getSequence().get(4).getLoop().get(1)
				.getIteration().get(0).getNum());
		assertEquals(0.7, flow.getSequence().get(4).getLoop().get(1)
				.getIteration().get(0).getProb(), 0.01);
		
		assertEquals(2, flow.getSequence().get(4).getLoop().get(2)
				.getIteration().get(0).getNum());
		assertEquals(0.3, flow.getSequence().get(4).getLoop().get(2)
				.getIteration().get(0).getProb(), 0.01);
		
		assertEquals(3, flow.getSequence().get(4).getLoop().get(3)
				.getIteration().get(0).getNum());
		assertEquals(0.1, flow.getSequence().get(4).getLoop().get(3)
				.getIteration().get(0).getProb(), 0.01);
		
		assertEquals(4, flow.getSequence().get(4).getLoop().get(4)
				.getIteration().get(0).getNum());
		assertEquals(0.01, flow.getSequence().get(4).getLoop().get(4)
				.getIteration().get(0).getProb(), 0.01);
	}

	@Test
	public void jsonToObject() throws Exception {

		String flowJson = "{ \"flow\": {"
					+"          \"sequence\": ["
					+"              {\"task\":\"task1\"},"
					+"              {\"task\":\"task2\"},"
					+"                {"
					+"                  \"branches\": ["
					+"                        {"
					+"                          \"branch\": ["
					+"                                {"
					+"                                  \"prob\": 0.3"
					+"                                },"
					+"                              {\"task\":\"task3\"}"
					+"                            ]"
					+"                        },"
					+"                        {"
					+"                          \"branch\": ["
					+"                                {"
					+"                                  \"prob\": 0.7"
					+"                                },"
					+"                              {\"task\":\"task4\"}"
					+"                            ]"
					+"                        }"
					+"                    ]"
					+"                },"
					+"              {\"task\": \"task5\"},"
					+"                {"
					+"                  \"loop\": ["
					+"                        {"
					+"                          \"sequence\": ["
					+"                              {\"task\":\"task6\"},"
					+"                              {\"task\":\"task7\"}"
					+"                            ]"
					+"                        },"
					+"                        {"
					+"                          \"iteration\": {"
					+"                              \"num\": 1,"
					+"                              \"prob\": 0.7"
					+"                            }"
					+"                        },"
					+"                        {"
					+"                          \"iteration\": {"
					+"                              \"num\": 2,"
					+"                              \"prob\": 0.3"
					+"                            }"
					+"                        },"
					+"                        {"
					+"                          \"iteration\": {"
					+"                              \"num\": 3,"
					+"                              \"prob\": 0.1"
					+"                            }"
					+"                        },"
					+"                        {"
					+"                          \"iteration\": {"
					+"                              \"num\": 4,"
					+"                              \"prob\": 0.01"
					+"                            }"
					+"                        }"
					+"                    ]"
					+"                }"
					+"            ]"
					+"        }"
					+ "}";

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	    mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
	    mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
	    
		mapper.setSerializationInclusion(Inclusion.NON_NULL);


		Flow flow = mapper.readValue(flowJson, Flow.class);

		System.out.println("FlowJsonToObject:"
				+ mapper.writeValueAsString(flow));

		assertEquals("task1", flow.getSequence().get(0).getTask());
		assertEquals("task2", flow.getSequence().get(1).getTask());
		assertEquals(0.3, flow.getSequence().get(2).getBranches().get(0)
				.getBranch().get(0).getProb(), 0.01);

		assertEquals("task3", flow.getSequence().get(2).getBranches().get(0)
				.getBranch().get(1).getTask());
		assertEquals(0.7, flow.getSequence().get(2).getBranches().get(1)
				.getBranch().get(0).getProb(), 0.01);
		assertEquals("task4", flow.getSequence().get(2).getBranches().get(1)
				.getBranch().get(1).getTask());
		assertEquals("task5", flow.getSequence().get(3).getTask());

		assertEquals("task6", flow.getSequence().get(4).getLoop().get(0)
				.getSequence().get(0).getTask());
		assertEquals("task7", flow.getSequence().get(4).getLoop().get(0)
				.getSequence().get(1).getTask());
		
		assertEquals(1, flow.getSequence().get(4).getLoop().get(1)
				.getIteration().get(0).getNum());
		assertEquals(0.7, flow.getSequence().get(4).getLoop().get(1)
				.getIteration().get(0).getProb(), 0.01);
		
		assertEquals(2, flow.getSequence().get(4).getLoop().get(2)
				.getIteration().get(0).getNum());
		assertEquals(0.3, flow.getSequence().get(4).getLoop().get(2)
				.getIteration().get(0).getProb(), 0.01);
		
		assertEquals(3, flow.getSequence().get(4).getLoop().get(3)
				.getIteration().get(0).getNum());
		assertEquals(0.1, flow.getSequence().get(4).getLoop().get(3)
				.getIteration().get(0).getProb(), 0.01);
		
		assertEquals(4, flow.getSequence().get(4).getLoop().get(4)
				.getIteration().get(0).getNum());
		assertEquals(0.01, flow.getSequence().get(4).getLoop().get(4)
				.getIteration().get(0).getProb(), 0.01);

	}

	@Test
	public void objectToJson() throws Exception {

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
		ObjectMapper mapper = new ObjectMapper();

		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, true);
		mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);

		String flowJson = mapper.writeValueAsString(flow);
		
		
		System.out.println("flowJson:" + flowJson);
		
		Flow flowObject = mapper.readValue(
				flowJson, Flow.class);

		assertEquals("task1", flowObject.getSequence().get(0).getTask());
		assertEquals("task2", flowObject.getSequence().get(1).getTask());
		assertEquals(0.3, flowObject.getSequence().get(2).getBranches().get(0)
				.getBranch().get(0).getProb(), 0.01);

		assertEquals("task3", flowObject.getSequence().get(2).getBranches().get(0)
				.getBranch().get(1).getTask());
		assertEquals(0.7, flowObject.getSequence().get(2).getBranches().get(1)
				.getBranch().get(0).getProb(), 0.01);
		assertEquals("task4", flowObject.getSequence().get(2).getBranches().get(1)
				.getBranch().get(1).getTask());
		assertEquals("task5", flowObject.getSequence().get(3).getTask());

		assertEquals("task6", flowObject.getSequence().get(4).getLoop().get(0)
				.getSequence().get(0).getTask());
		assertEquals("task7", flowObject.getSequence().get(4).getLoop().get(0)
				.getSequence().get(1).getTask());
		
		assertEquals(1, flowObject.getSequence().get(4).getLoop().get(1)
				.getIteration().get(0).getNum());
		assertEquals(0.7, flowObject.getSequence().get(4).getLoop().get(1)
				.getIteration().get(0).getProb(), 0.01);
		
		assertEquals(2, flowObject.getSequence().get(4).getLoop().get(2)
				.getIteration().get(0).getNum());
		assertEquals(0.3, flowObject.getSequence().get(4).getLoop().get(2)
				.getIteration().get(0).getProb(), 0.01);
		
		assertEquals(3, flowObject.getSequence().get(4).getLoop().get(3)
				.getIteration().get(0).getNum());
		assertEquals(0.1, flowObject.getSequence().get(4).getLoop().get(3)
				.getIteration().get(0).getProb(), 0.01);
		
		assertEquals(4, flowObject.getSequence().get(4).getLoop().get(4)
				.getIteration().get(0).getNum());
		assertEquals(0.01, flowObject.getSequence().get(4).getLoop().get(4)
				.getIteration().get(0).getProb(), 0.01);
	}
}
