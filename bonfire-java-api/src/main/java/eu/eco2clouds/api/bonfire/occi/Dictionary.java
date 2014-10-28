package eu.eco2clouds.api.bonfire.occi;

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
	public static final String NAMESPACE="http://api.bonfire-project.eu/doc/schemas/occi";
	public static final String API_SECURITY_REALM = "BonFIRE API";
	public static final String LOCATION_URL = "locations";
	public static final String EXPERIMENT_URL = "experiments";
	public static final String STORAGE_URL = "storages";
	public static final String NETWORK_URL = "networks";
	public static final String COMPUTE_URL = "computes";
	public static final String CONFIGURATIONS_URL = "configurations";
	public static final String BONFIRE_ASSERTED_ID_HEADER = "x-bonfire-asserted-id";
	public static final String BONFIRE_ASSERTED_GROUPS_HEADER = "x-bonfire-asserted-groups";
	public static final String BONFIRE_XML = "application/vnd.bonfire+xml";
}
