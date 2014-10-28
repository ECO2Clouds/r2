package eu.eco2clouds.applicationProfile.datamodel;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
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
public class DataTest {

	@Test
	public void testPojo() throws JsonGenerationException,
			JsonMappingException, IOException {

		String task = "task4";
		String task2 = "task5";

		DataDependency dataDependency1 = new DataDependency();
		DataDependency dataDependency2 = new DataDependency();

		dataDependency1.setTask(task);
		dataDependency2.setTask(task2);

		ArrayList<DataDependency> dataDependency = new ArrayList<DataDependency>();

		dataDependency.add(dataDependency1);
		dataDependency.add(dataDependency2);

		Data data = new Data();

		data.setDataDependency(dataDependency);

	
		assertEquals("task4", data.getDataDependency().get(0).getTask());
		assertEquals("task5", data.getDataDependency().get(1).getTask());
	}

	@Test
	public void jsonToObject() throws Exception {

		String dataJson = "{\"data\":{\"datadependency\":[ {\"task\": \"task1\"}, {\"task\": \"task2\"} ]}}";

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);

		Data data = mapper.readValue(dataJson, Data.class);

		System.out.println("DataJsonToObject:"
				+ mapper.writeValueAsString(data));

		assertEquals("task1", data.getDataDependency().get(0).getTask());
		assertEquals("task2", data.getDataDependency().get(1).getTask());

	}

	@Test
	public void objectToJson() throws Exception {

		String task = "task4";
		String task2 = "task5";

		DataDependency dataDependency1 = new DataDependency();
		DataDependency dataDependency2 = new DataDependency();

		dataDependency1.setTask(task);
		dataDependency2.setTask(task2);

		ArrayList<DataDependency> dataDependency = new ArrayList<DataDependency>();

		dataDependency.add(dataDependency1);
		dataDependency.add(dataDependency2);

		Data data = new Data();

		data.setDataDependency(dataDependency);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// convert user object to json string, and save to a file
		mapper.writeValue(new File("src/main/resources/Data.json"), data);

		assertEquals("task4", data.getDataDependency().get(0).getTask());
		assertEquals("task5", data.getDataDependency().get(1).getTask());
		;
	}

}
