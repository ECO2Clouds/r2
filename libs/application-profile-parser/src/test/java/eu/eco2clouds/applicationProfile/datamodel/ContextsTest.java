package eu.eco2clouds.applicationProfile.datamodel;

import static org.junit.Assert.assertEquals;

import java.util.Map;

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
public class ContextsTest {

	@Test
	public void pojo() {
		Contexts contexts = new Contexts();
		
		Object object = new Object();
		
		contexts.setContextThings("clave", object);
		
		Map<String,Object> map = contexts.getContextThings();
		
		assertEquals("clave", map.keySet().toArray()[0]);
		assertEquals(object, map.get("clave"));
	}
	
	@Test
	public void objectToJSON() throws Exception {
		Contexts contexts = new Contexts();
		contexts.setContextThings("eco2clouds_experiment_id", "33");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, true);
		mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);

		String contextJson = mapper.writeValueAsString(contexts);
		System.out.println(contextJson);
		
		Contexts contextsObject = mapper.readValue(contextJson, Contexts.class);
		Map<String,Object> map = contextsObject.getContextThings();
		assertEquals("eco2clouds_experiment_id", map.keySet().toArray()[0]);
		assertEquals("33", (String) map.get("eco2clouds_experiment_id"));
	}
}
