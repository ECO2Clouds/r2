package eu.eco2clouds.applicationProfile.datamodel;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
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
public class RequirementTest {

	@Test
	public void testPojo() {
		
		Requirement requirement = new Requirement();

		Constraint constraint1 = new Constraint();
		Constraint constraint2 = new Constraint();
		Constraint constraint3 = new Constraint();
		Constraint constraint4 = new Constraint();

		constraint1.setIndicator("A-PUE");
		constraint1.setElement("VM1");
		constraint1.setOperator("<");
		ArrayList<String>  values=  new ArrayList<String>();;
		values.add("10");
		values.add("50");
		constraint1.setValues(values);

		constraint2.setIndicator("Responsetime");
		constraint2.setElement("Task1");
		constraint2.setOperator("<");
		constraint2.setValue("1ms");
		values = new ArrayList<String>();
		values.add("40");
		values.add("70");
		constraint2.setValues(values);

		constraint3.setIndicator("Responsetime");
		constraint3.setElement("Application");
		constraint3.setOperator("<");
		constraint3.setValue("10ms");

		constraint4.setIndicator("CPUUsage");
		constraint4.setElement("VM1");
		constraint4.setOperator("><");
		values = new ArrayList<String>();
		values.add("45");
		values.add("90");
		constraint4.setValues(values);

		ArrayList<Constraint> constrainsts = new ArrayList<Constraint>();
		constrainsts.add(constraint1);
		constrainsts.add(constraint2);
		constrainsts.add(constraint3);
		constrainsts.add(constraint4);
		

		requirement.setConstraints(constrainsts);

		
		assertEquals("A-PUE", requirement.getConstraints().get(0).getIndicator());
		assertEquals("VM1", requirement.getConstraints().get(0).getElement());
		assertEquals("<",requirement.getConstraints().get(0).getOperator());
		assertEquals(2,requirement.getConstraints().get(0).getValues().size());
		assertEquals("10", requirement.getConstraints().get(0).getValues().get(0));
		assertEquals("50", requirement.getConstraints().get(0).getValues().get(1));

	
		
		assertEquals("Responsetime", requirement.getConstraints().get(1).getIndicator());
		assertEquals("Task1", requirement.getConstraints().get(1).getElement());
		assertEquals("<",requirement.getConstraints().get(1).getOperator());
		assertEquals(2,requirement.getConstraints().get(0).getValues().size());
		assertEquals("40", requirement.getConstraints().get(1).getValues().get(0));
		assertEquals("70", requirement.getConstraints().get(1).getValues().get(1));

		
		assertEquals("Responsetime", requirement.getConstraints().get(2).getIndicator());
		assertEquals("Application", requirement.getConstraints().get(2).getElement());
		assertEquals("<",requirement.getConstraints().get(2).getOperator());
		assertEquals("10ms", requirement.getConstraints().get(2).getValue());
		

		
		assertEquals("CPUUsage", requirement.getConstraints().get(3).getIndicator());
		assertEquals("VM1", requirement.getConstraints().get(3).getElement());
		assertEquals("><",requirement.getConstraints().get(3).getOperator());
		assertEquals(2,requirement.getConstraints().get(0).getValues().size());
		assertEquals("45", requirement.getConstraints().get(3).getValues().get(0));
		assertEquals("90", requirement.getConstraints().get(3).getValues().get(1));
	}


	@Test
	public void jsonToObject() throws Exception {
		

		String requirementJson = "{ \"requirements\": {"
			+"          \"constraints\": ["
			+"                {"
			+"                  \"indicator\":\"A-PUE\","
			+"                  \"element\":\"VM1\","
			+"                  \"operator\":\"<\","
			+"                  \"value\":\"1.4\""
			+"                },"
			+"                {"
			+"                  \"indicator\":\"Responsetime\","
			+"                  \"element\":\"Task1\","
			+"                  \"operator\":\"<\","
			+"                  \"value\":\"1ms\""
			+"                },"
			+"                {"
			+"                  \"indicator\":\"Responsetime\","
			+"                  \"element\":\"Application\","
			+"                  \"operator\":\"<\","
			+"                  \"value\":\"10ms\""
			+"                },"
			+"                {"
			+"                  \"indicator\":\"CPUUsage\","
			+"                  \"element\":\"VM1\","
			+"                  \"operator\":\"><\","
			+"                  \"values\": ["
			+"                      \"60\","
			+"                      \"90\""
			+"                    ]"
			+"                }"
			+"            ]"
			+"        }"
			+ "}";

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);

		
		Requirement requirement =  mapper.readValue(requirementJson,
				Requirement.class);

		System.out.println("RequirementJsonToObject:"
				+ mapper.writeValueAsString(requirement));

	
	
		assertEquals("A-PUE", requirement.getConstraints().get(0).getIndicator());
		assertEquals("VM1", requirement.getConstraints().get(0).getElement());
		assertEquals("<",requirement.getConstraints().get(0).getOperator());
		assertEquals("1.4", requirement.getConstraints().get(0).getValue());
		
		
		assertEquals("Responsetime", requirement.getConstraints().get(1).getIndicator());
		assertEquals("Task1", requirement.getConstraints().get(1).getElement());
		assertEquals("<",requirement.getConstraints().get(1).getOperator());
		assertEquals("1ms", requirement.getConstraints().get(1).getValue());


		
		assertEquals("Responsetime", requirement.getConstraints().get(2).getIndicator());
		assertEquals("Application", requirement.getConstraints().get(2).getElement());
		assertEquals("<",requirement.getConstraints().get(2).getOperator());
		assertEquals("10ms", requirement.getConstraints().get(2).getValue());
		

		
		assertEquals("CPUUsage", requirement.getConstraints().get(3).getIndicator());
		assertEquals("VM1", requirement.getConstraints().get(3).getElement());
		assertEquals("><",requirement.getConstraints().get(3).getOperator());
		assertEquals(2,requirement.getConstraints().get(3).getValues().size());
		assertEquals("60", requirement.getConstraints().get(3).getValues().get(0));
		assertEquals("90", requirement.getConstraints().get(3).getValues().get(1));
		
	}
	
	@Test
	public void objetcToJson() throws Exception {

		Requirement requirement = new Requirement();

		Constraint constraint1 = new Constraint();
		Constraint constraint2 = new Constraint();
		Constraint constraint3 = new Constraint();
		Constraint constraint4 = new Constraint();

		constraint1.setIndicator("A-PUE");
		constraint1.setElement("VM1");
		constraint1.setOperator("<");
		ArrayList<String>  values=  new ArrayList<String>();;
		values.add("10");
		values.add("50");
		constraint1.setValues(values);

		constraint2.setIndicator("Responsetime");
		constraint2.setElement("Task1");
		constraint2.setOperator("<");
		constraint2.setValue("1ms");
		values = new ArrayList<String>();
		values.add("40");
		values.add("70");
		constraint2.setValues(values);

		constraint3.setIndicator("Responsetime");
		constraint3.setElement("Application");
		constraint3.setOperator("<");
		constraint3.setValue("10ms");

		constraint4.setIndicator("CPUUsage");
		constraint4.setElement("VM1");
		constraint4.setOperator("><");
		values = new ArrayList<String>();
		values.add("45");
		values.add("90");
		constraint4.setValues(values);

		ArrayList<Constraint> constrainsts = new ArrayList<Constraint>();
		constrainsts.add(constraint1);
		constrainsts.add(constraint2);
		constrainsts.add(constraint3);
		constrainsts.add(constraint4);
		

		requirement.setConstraints(constrainsts);
		
		
		ObjectMapper mapper = new ObjectMapper();

		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, true);
		mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);

		String requirementString = mapper.writeValueAsString(requirement);
		
		
		System.out.println("requirementJson:" + requirementString);
		
		Requirement requirementObject = mapper.readValue(
				requirementString, Requirement.class);
		

		assertEquals("A-PUE", requirement.getConstraints().get(0).getIndicator());
		assertEquals("VM1", requirement.getConstraints().get(0).getElement());
		assertEquals("<",requirement.getConstraints().get(0).getOperator());
		assertEquals(2,requirement.getConstraints().get(0).getValues().size());
		assertEquals("10", requirement.getConstraints().get(0).getValues().get(0));
		assertEquals("50", requirement.getConstraints().get(0).getValues().get(1));

	
		
		assertEquals("Responsetime", requirement.getConstraints().get(1).getIndicator());
		assertEquals("Task1", requirement.getConstraints().get(1).getElement());
		assertEquals("<",requirement.getConstraints().get(1).getOperator());
		assertEquals(2,requirement.getConstraints().get(0).getValues().size());
		assertEquals("40", requirement.getConstraints().get(1).getValues().get(0));
		assertEquals("70", requirement.getConstraints().get(1).getValues().get(1));

		
		assertEquals("Responsetime", requirement.getConstraints().get(2).getIndicator());
		assertEquals("Application", requirement.getConstraints().get(2).getElement());
		assertEquals("<",requirement.getConstraints().get(2).getOperator());
		assertEquals("10ms", requirement.getConstraints().get(2).getValue());
		

		
		assertEquals("CPUUsage", requirement.getConstraints().get(3).getIndicator());
		assertEquals("VM1", requirement.getConstraints().get(3).getElement());
		assertEquals("><",requirement.getConstraints().get(3).getOperator());
		assertEquals(2,requirement.getConstraints().get(0).getValues().size());
		assertEquals("45", requirement.getConstraints().get(3).getValues().get(0));
		assertEquals("90", requirement.getConstraints().get(3).getValues().get(1));
		
	}
		
}
