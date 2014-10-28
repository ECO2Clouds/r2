package eu.eco2clouds.applicationProfile.datamodel;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
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
public class ExperimentDescriptorTest {

	@Test
	public void testPojo() {

		ExperimentDescriptor expDesc = new ExperimentDescriptor();
		expDesc.setDescription("Experiment description");
		expDesc.setDuration(120);
		expDesc.setName("MyExperiment");

		// Compute 1
		Compute compute1 = new Compute();

		compute1.setName("Client");
		compute1.setDescription("A description of the client");
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
		locationCompute2.add("fr-inria");

		compute2.setLocations(locationCompute2);

		ArrayList<Resource> resourceCompute2 = new ArrayList<Resource>();

		Resource resourceElem2 = new Resource();

		resourceElem2.setStorage("@BonFIREDebianSqueezev5");
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

		assertEquals("Experiment description", expDesc.getDescription());
		assertEquals(120, expDesc.getDuration());
		assertEquals("MyExperiment", expDesc.getName());
		// Compute 1
		assertEquals("Client", expDesc.getResourcesCompute().get(0)
				.getCompute().getName());

		assertEquals("A description of the client", expDesc
				.getResourcesCompute().get(0).getCompute().getDescription());
		assertEquals("small", expDesc.getResourcesCompute().get(0).getCompute()
				.getInstanceType());
		assertEquals("uk-epcc", expDesc.getResourcesCompute().get(0)
				.getCompute().getLocations().get(0));

		assertEquals("@BonFIREDebianSqueezev3", expDesc.getResourcesCompute()
				.get(0).getCompute().getResourceCompute().get(0).getStorage());
		assertEquals("@BonFIREWAN", expDesc.getResourcesCompute().get(0)
				.getCompute().getResourceCompute().get(0).getNetwork());

		// Compute 2
		assertEquals("Server", expDesc.getResourcesCompute().get(1)
				.getCompute().getName());

		assertEquals("A description of the server", expDesc
				.getResourcesCompute().get(1).getCompute().getDescription());
		assertEquals("small", expDesc.getResourcesCompute().get(1).getCompute()
				.getInstanceType());
		assertEquals("fr-inria", expDesc.getResourcesCompute().get(1)
				.getCompute().getLocations().get(0));

		assertEquals("@BonFIREDebianSqueezev5", expDesc.getResourcesCompute()
				.get(1).getCompute().getResourceCompute().get(0).getStorage());
		assertEquals("@BonFIREWAN", expDesc.getResourcesCompute().get(1)
				.getCompute().getResourceCompute().get(0).getNetwork());
	}

	@Test
	public void jsonToObject() throws Exception {

		String resourcesJson = "{ \"resources\": {"
			+"          \"name\":\"MyExperiment\","
			+"          \"description\":\"Experiment description\","
			+"          \"duration\": 120,"
			+"          \"resources\": ["
			+"                {"
			+"                  \"compute\": {"
			+"                      \"name\":\"Server\","
			+"                      \"description\":\"A description of the server\","
			+"                      \"instanceType\":\"small\","
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
			+"                        ]"
			+"                    }"
			+"                },"
			+"                {"
			+"                  \"compute\": {"
			+"                      \"name\":\"Client\","
			+"                      \"description\":\"A description of the client\","
			+"                      \"instanceType\":\"small\","
			+"                      \"locations\": ["
			+"                          \"fr-inria\""
			+"                        ],"
			+"                      \"resources\": ["
			+"                            {"
			+"                              \"storage\":\"@BonFIREDebianSqueezev5\""
			+"                            },"
			+"                            {"
			+"                              \"network\":\"@BonFIREWAN\""
			+"                            }"
			+"                        ]"
			+"                    }"
			+"                }"
			+"            ]"
			+"        }"
			+"}";

		
		ObjectMapper mapper = new ObjectMapper();

		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);

		mapper.setSerializationInclusion(Inclusion.NON_NULL);
		
		ExperimentDescriptor expDesc = new ExperimentDescriptor();
		
		expDesc = mapper.readValue(resourcesJson,
				ExperimentDescriptor.class);

		System.out.println("ResourcesJsonToObject:"
				+ mapper.writeValueAsString(expDesc));

		assertEquals("Experiment description", expDesc.getDescription());
		assertEquals(120, expDesc.getDuration());
		assertEquals("MyExperiment", expDesc.getName());
		// Compute 1
		assertEquals("Server", expDesc.getResourcesCompute().get(0)
				.getCompute().getName());

		assertEquals("A description of the server", expDesc
				.getResourcesCompute().get(0).getCompute().getDescription());
		assertEquals("small", expDesc.getResourcesCompute().get(0).getCompute()
				.getInstanceType());
		assertEquals("uk-epcc", expDesc.getResourcesCompute().get(0)
				.getCompute().getLocations().get(0));
		assertEquals("@BonFIREDebianSqueezev3", expDesc.getResourcesCompute()
				.get(0).getCompute().getResourceCompute().get(0).getStorage());
		assertEquals("@BonFIREWAN", expDesc.getResourcesCompute().get(0)
				.getCompute().getResourceCompute().get(1).getNetwork());

		// Compute 2
		assertEquals("Client", expDesc.getResourcesCompute().get(1)
				.getCompute().getName());

		assertEquals("A description of the client", expDesc
				.getResourcesCompute().get(1).getCompute().getDescription());
		assertEquals("small", expDesc.getResourcesCompute().get(1).getCompute()
				.getInstanceType());
		assertEquals("fr-inria", expDesc.getResourcesCompute().get(1)
				.getCompute().getLocations().get(0));

		assertEquals("@BonFIREDebianSqueezev5", expDesc.getResourcesCompute()
				.get(1).getCompute().getResourceCompute().get(0).getStorage());
		assertEquals("@BonFIREWAN", expDesc.getResourcesCompute().get(1)
				.getCompute().getResourceCompute().get(1).getNetwork());
	}
	
	
	@Test
	public void objectTojson () throws JsonGenerationException, JsonMappingException, IOException  {
		
		ExperimentDescriptor expDesc = new ExperimentDescriptor();
		expDesc.setDescription("Experiment description");
		expDesc.setDuration(120);
		expDesc.setName("MyExperiment");

		// Compute 1
		Compute compute1 = new Compute();

		compute1.setName("Client");
		compute1.setDescription("A description of the client");
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
		locationCompute2.add("fr-inria");

		compute2.setLocations(locationCompute2);

		ArrayList<Resource> resourceCompute2 = new ArrayList<Resource>();

		Resource resourceElem2 = new Resource();

		resourceElem2.setStorage("@BonFIREDebianSqueezev5");
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

		
		
		ObjectMapper mapper = new ObjectMapper();

		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, true);
		mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);

		String expDescString = mapper.writeValueAsString(expDesc);
		
		
		System.out.println("experimentDescriptorJson:" + expDescString);
		
		ExperimentDescriptor experimentDescriptorObject = mapper.readValue(
				expDescString, ExperimentDescriptor.class);
		
		
		assertEquals("Experiment description", expDesc.getDescription());
		assertEquals(120, expDesc.getDuration());
		assertEquals("MyExperiment", expDesc.getName());
		// Compute 1
		assertEquals("Client", expDesc.getResourcesCompute().get(0)
				.getCompute().getName());

		assertEquals("A description of the client", expDesc
				.getResourcesCompute().get(0).getCompute().getDescription());
		assertEquals("small", expDesc.getResourcesCompute().get(0).getCompute()
				.getInstanceType());
		assertEquals("uk-epcc", expDesc.getResourcesCompute().get(0)
				.getCompute().getLocations().get(0));

		assertEquals("@BonFIREDebianSqueezev3", expDesc.getResourcesCompute()
				.get(0).getCompute().getResourceCompute().get(0).getStorage());
		assertEquals("@BonFIREWAN", expDesc.getResourcesCompute().get(0)
				.getCompute().getResourceCompute().get(0).getNetwork());

		// Compute 2
		assertEquals("Server", expDesc.getResourcesCompute().get(1)
				.getCompute().getName());

		assertEquals("A description of the server", expDesc
				.getResourcesCompute().get(1).getCompute().getDescription());
		assertEquals("small", expDesc.getResourcesCompute().get(1).getCompute()
				.getInstanceType());
		assertEquals("fr-inria", expDesc.getResourcesCompute().get(1)
				.getCompute().getLocations().get(0));

		assertEquals("@BonFIREDebianSqueezev5", expDesc.getResourcesCompute()
				.get(1).getCompute().getResourceCompute().get(0).getStorage());
		assertEquals("@BonFIREWAN", expDesc.getResourcesCompute().get(1)
				.getCompute().getResourceCompute().get(0).getNetwork());
		
	}	
}