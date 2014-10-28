package eu.eco2clouds.applicationProfile.datamodel;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.junit.Test;

import sun.security.action.GetLongAction;

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
public class ComputeTest {

	@Test
	public void testPojo() {

		Compute compute = new Compute();
		compute.setName("Client");
		compute.setDescription("A description of the client");
		compute.setInstanceType("small");
		compute.setMin(3);
		compute.setCluster("cluster");

		ArrayList<String> locationCompute = new ArrayList<String>();
		locationCompute.add("uk-epcc");

		compute.setLocations(locationCompute);

		ArrayList<Resource> resource = new ArrayList<Resource>();
		Resource resourceElem = new Resource();
		resourceElem.setStorage("@BonFIREDebianSqueezev3");
		resourceElem.setNetwork("@BonFIREWAN");
		resource.add(resourceElem);
		compute.setResourceCompute(resource);

		assertEquals("Client", compute.getName());

		assertEquals("A description of the client", compute.getDescription());
		assertEquals("small", compute.getInstanceType());
		assertEquals("uk-epcc", compute.getLocations().get(0));
		assertEquals(3, compute.getMin());
		assertEquals("@BonFIREDebianSqueezev3", compute.getResourceCompute().get(0).getStorage());
		assertEquals("@BonFIREWAN", compute.getResourceCompute().get(0).getNetwork());
		assertEquals("cluster", compute.getCluster());
	}

	@Test
	public void jsonToObject() throws Exception {

		String computeJson = "{ \"compute\": {"
				+"                      \"name\":\"Server\","
				+"                      \"description\":\"A description of the server\","
				+"                      \"instanceType\":\"small\","
				+"                      \"host\":\"node-1.bonfire.grid5000.fr\","
				+"                      \"locations\": ["
				+"                          \"uk-epcc\""
				+"                        ],"
				+"                      \"min\":3,"
				+"                      \"resources\": ["
				+"                            {"
				+"                              \"storage\":\"@BonFIREDebianSqueezev3\""
				+"                            },"
				+"                            {"
				+"                              \"network\":\"@BonFIREWAN\""
				+"                            }"
				+"                        ]"
				+"                    }"
				+"                }";

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);

		Compute compute = mapper.readValue(computeJson, Compute.class);

		System.out.println("ComputeJsonToObject2:" + mapper.writeValueAsString(compute));

		assertEquals("Server", compute.getName());
		assertEquals("A description of the server", compute.getDescription());
		assertEquals("small", compute.getInstanceType());
		assertEquals("uk-epcc", compute.getLocations().get(0));
		assertEquals("@BonFIREDebianSqueezev3", compute.getResourceCompute().get(0).getStorage());
		assertEquals("@BonFIREWAN", compute.getResourceCompute().get(1).getNetwork());
		assertEquals("node-1.bonfire.grid5000.fr", compute.getHost());	
		assertEquals(3, compute.getMin());
	}
	
	@Test
	public void customTypeTest() throws Exception {

		String computeJson = "{ \"compute\": {"
				+"                      \"name\":\"Server\","
				+"                      \"description\":\"A description of the server\","
				+"                      \"instanceType\":\"custom\","
				+"						\"cpu\": \"1\","
	            +"				  		\"vcpu\": \"2.0\","
	            +"						\"memory\": \"256\","
				+"                      \"host\":\"node-1.bonfire.grid5000.fr\","
				+"                      \"locations\": ["
				+"                          \"uk-epcc\""
				+"                        ],"
				+"                      \"min\":3,"
				+"                      \"resources\": ["
				+"                            {"
				+"                              \"storage\":\"@BonFIREDebianSqueezev3\""
				+"                            },"
				+"                            {"
				+"                              \"network\":\"@BonFIREWAN\""
				+"                            }"
				+"                        ]"
				+"                    }"
				+"                }";

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);

		Compute compute = mapper.readValue(computeJson, Compute.class);

		System.out.println("ComputeJsonToObject2:" + mapper.writeValueAsString(compute));

		assertEquals("Server", compute.getName());
		assertEquals("A description of the server", compute.getDescription());
		assertEquals("uk-epcc", compute.getLocations().get(0));
		assertEquals("@BonFIREDebianSqueezev3", compute.getResourceCompute().get(0).getStorage());
		assertEquals("@BonFIREWAN", compute.getResourceCompute().get(1).getNetwork());
		assertEquals("node-1.bonfire.grid5000.fr", compute.getHost());	
		assertEquals(3, compute.getMin());
		assertEquals(1, compute.getCpu(), 0.0001);
		assertEquals(256, compute.getMemory().longValue());
		assertEquals(2.0, compute.getVcpu(), 0.00001);
	}
	
	@Test
	public void testContext() throws Exception {
		String computeJson = "{ \"compute\": {"
				+"                      \"name\":\"Server\","
				+"                      \"description\":\"A description of the server\","
				+"                      \"instanceType\":\"small\","
				+"                      \"cluster\":\"clusterX\","
				+"                      \"host\":\"node-1.bonfire.grid5000.fr\","
				+"                      \"locations\": ["
				+"                          \"uk-epcc\""
				+"                        ],"
				+"                      \"resources\": ["
				+"                            {"
				+"                              \"storage\":\"@BonFIREDebianSqueezev3\""
				+"                            },"
				+"                            {"
				+"                              \"network\":\"@BonFIREWAN\""
				+"                            }"
				+"                        ],"
				+"                      \"contexts\": ["
				+"                          {"
				+"                          	\"aggregator_ip\": [ \"BonFIRE-Monitor\", \"BonFIRE WAN\" ]"
				+"                          },"
				+"							{"
                +" 							   \"experimentmin\": \"0\""
           		+"							},"
           		+"							{"
           		+"								\"experimentmax\": \"100\""
           		+"							}"
           		+"                       ]"
				+"                    }"
				+"                }";

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);

		Compute compute = mapper.readValue(computeJson, Compute.class);

		System.out.println("ComputeJsonToObject3:" + mapper.writeValueAsString(compute));

		assertEquals("Server", compute.getName());
		assertEquals("A description of the server", compute.getDescription());
		assertEquals("small", compute.getInstanceType());
		assertEquals("uk-epcc", compute.getLocations().get(0));
		assertEquals("@BonFIREDebianSqueezev3", compute.getResourceCompute().get(0).getStorage());
		assertEquals("@BonFIREWAN", compute.getResourceCompute().get(1).getNetwork());
		assertEquals("node-1.bonfire.grid5000.fr", compute.getHost());
		assertEquals(3, compute.getContexts().size());
		assertEquals("aggregator_ip", compute.getContexts().get(0).getContextThings().keySet().toArray()[0]);
		assertEquals("BonFIRE-Monitor", ((ArrayList<String>) compute.getContexts().get(0).getContextThings().get("aggregator_ip")).get(0));
		assertEquals("BonFIRE WAN", ((ArrayList<String>) compute.getContexts().get(0).getContextThings().get("aggregator_ip")).get(1));
		assertEquals("experimentmin", compute.getContexts().get(1).getContextThings().keySet().toArray()[0]);
		assertEquals("0", compute.getContexts().get(1).getContextThings().get("experimentmin"));
		assertEquals("experimentmax", compute.getContexts().get(2).getContextThings().keySet().toArray()[0]);
		assertEquals("100", compute.getContexts().get(2).getContextThings().get("experimentmax"));
		assertEquals("clusterX", compute.getCluster());
	}

	@Test
	public void objectToJson() throws Exception {
	
		Compute compute = new Compute();	
		compute.setName("Client");
		compute.setDescription("A description of the client");
		compute.setInstanceType("small");
		compute.setMin(3);
		compute.setHost("host");
		compute.setCluster("cluster");

		ArrayList<String> locationCompute = new ArrayList<String>();
		locationCompute.add("uk-epcc");

		compute.setLocations(locationCompute);

		ArrayList<Resource> resource = new ArrayList<Resource>();
		Resource resourceElem = new Resource();
		resourceElem.setStorage("@BonFIREDebianSqueezev3");
		resourceElem.setNetwork("@BonFIREWAN");
		resource.add(resourceElem);
		compute.setResourceCompute(resource);
		
		// Context
		Contexts contexts = new Contexts();
		contexts.setContextThings("eco2clouds_experiment_id", "33");
		Contexts contexts2 = new Contexts();
		contexts2.setContextThings("xxxx", "xxxx33");
		ArrayList<Contexts> contextses = new ArrayList<Contexts>();
		contextses.add(contexts);
		contextses.add(contexts2);
		compute.setContexts(contextses);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, true);
		mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);

		String computeJson = mapper.writeValueAsString(compute);
		
		
		System.out.println("computeJson:" + computeJson);
		
		Compute computeObject = mapper.readValue(computeJson, Compute.class);

		System.out.println("ComputeJsonToObject:" + mapper.writeValueAsString(compute));

		assertEquals("Client", computeObject.getName());

		assertEquals("A description of the client", computeObject.getDescription());
		assertEquals("small", computeObject.getInstanceType());
		assertEquals("uk-epcc", computeObject.getLocations().get(0));
		assertEquals(3, computeObject.getMin());
		assertEquals("@BonFIREDebianSqueezev3", computeObject.getResourceCompute().get(0).getStorage());
		assertEquals("@BonFIREWAN", computeObject.getResourceCompute().get(0).getNetwork());
		assertEquals("cluster", computeObject.getCluster());
		assertEquals("host", computeObject.getHost());
		assertEquals("33", (String) computeObject.getContexts().get(0).getContextThings().get("eco2clouds_experiment_id"));
	}
}
