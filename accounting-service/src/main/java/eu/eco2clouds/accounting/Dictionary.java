package eu.eco2clouds.accounting;

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
 *
 * It defines a series of values to use in all the other classes
 *
 */
public class Dictionary {
	public static final String VERSION="0.1";
	public static final String CONTENT_TYPE_ECO2CLOUDS_XML = "application/vnd.eco2clouds+xml";
	public static final String CONTENT_TYPE_JSON = "application/json";
	public static final String CONTENT_TYPE_XML = "application/xml";
	
	public static final String NAMESPACE="http://api.bonfire-project.eu/doc/schemas/occi";
	public static final String E2C_NAMESPACE = "http://accounting.eco2clouds.eu/doc/schemas/xml";
	public static final String BONFIRE_ASSERTED_ID_HEADER = "x-bonfire-asserted-id";
	public static final String BONFIRE_ASSERTED_GROUPS_HEADER = "x-bonfire-asserted-groups";
	public static final String BONFIRE_XML = "application/vnd.bonfire+xml";
	
	public static final String USER_AGENT = "ECO2Clouds-Scheduler/0.1";
	public static final String X_BONFIRE_ASSERTED_ID = "X-Bonfire-Asserted-Id";
	public static final String X_BONFIRE_GROUPS_ID = "X-Bonfire-Groups-Id";
	public static final String X_BONFIRE_ASSERTED_SELECTED_GROUP="X-BONFIRE-ASSERTED-SELECTED-GROUP";
	public static final String ACCEPT = "*/*";
}