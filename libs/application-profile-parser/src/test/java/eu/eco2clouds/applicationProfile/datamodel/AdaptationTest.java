package eu.eco2clouds.applicationProfile.datamodel;

import static org.junit.Assert.assertEquals;

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
public class AdaptationTest {

	@Test
	public void testPojo() {

		Adaptation adaptation = new Adaptation();
		adaptation.setAllowed("yes");

		assertEquals("yes", adaptation.getAllowed());

	}

	@Test
	public void jsonToObject() throws Exception {

		String adaptationJson = "{ \"adaptation\": {"
				+ "\"allowed\": \"yes\" } }";

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);

		Adaptation adaptation = mapper.readValue(adaptationJson,
				Adaptation.class);

		System.out.println("AdaptationJsonToObject2:"
				+ mapper.writeValueAsString(adaptation));

		assertEquals("yes", adaptation.getAllowed());
	}

	@Test
	public void objectToJson() throws Exception {

		// Adaptation element

		Adaptation adaptation = new Adaptation();
		adaptation.setAllowed("yes");

		ObjectMapper mapper = new ObjectMapper();

		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, true);
		mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);

		String adaptationJson = mapper.writeValueAsString(adaptation);

		System.out.println("adaptationJson:" + adaptationJson);

		Adaptation adaptationObject = mapper.readValue(adaptationJson,
				Adaptation.class);

		System.out.println("AdaptationJsonToObject:"
				+ mapper.writeValueAsString(adaptation));

		assertEquals("yes", adaptationObject.getAllowed());

	}
}