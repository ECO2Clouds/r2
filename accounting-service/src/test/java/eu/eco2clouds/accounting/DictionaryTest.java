package eu.eco2clouds.accounting;

import static org.junit.Assert.assertEquals;
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
public class DictionaryTest {
	
	@Test
	 public void loadDictionaryTest() {
	
		assertEquals("0.1",Dictionary.VERSION);
		assertEquals("application/vnd.eco2clouds+xml", Dictionary.CONTENT_TYPE_ECO2CLOUDS_XML);
		assertEquals("application/json", Dictionary.CONTENT_TYPE_JSON);
		assertEquals("application/xml", Dictionary.CONTENT_TYPE_XML);
		assertEquals("http://api.bonfire-project.eu/doc/schemas/occi", Dictionary.NAMESPACE);
		assertEquals("http://accounting.eco2clouds.eu/doc/schemas/xml",  Dictionary.E2C_NAMESPACE);
		assertEquals("x-bonfire-asserted-id",Dictionary.BONFIRE_ASSERTED_ID_HEADER);
		assertEquals("x-bonfire-asserted-groups", Dictionary.BONFIRE_ASSERTED_GROUPS_HEADER);
		assertEquals("application/vnd.bonfire+xml", Dictionary.BONFIRE_XML);
		
	}

}