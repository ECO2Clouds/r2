package eu.eco2clouds.scheduler.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import eu.eco2clouds.applicationProfile.datamodel.ExperimentDescriptor;
import eu.eco2clouds.applicationProfile.datamodel.ResourceCompute;

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
 */
public class RestServiceTest {
	private static Logger logger = Logger.getLogger(RestServiceTest.class);

	@Test
	public void checkIfItIsNecessaryToOptimizeTest() throws Exception {
		String applicationProfile = "{ \"applicationprofile\": {"
				+ "\"optimize\": false,"
				+ "\"flow\": {"
				+ "          \"sequence\": ["
				+ "              {\"task\":\"task1\"},"
				+ "              {\"task\":\"task2\"},"
				+ "                {"
				+ "                  \"branches\": ["
				+ "                        {"
				+ "                          \"branch\": ["
				+ "                                {"
				+ "                                  \"prob\": 0.3"
				+ "                                },"
				+ "                              {\"task\":\"task3\"}"
				+ "                            ]"
				+ "                        },"
				+ "                        {"
				+ "                          \"branch\": ["
				+ "                                {"
				+ "                                  \"prob\": 0.7"
				+ "                                },"
				+ "                              {\"task\":\"task4\"}"
				+ "                            ]"
				+ "                        }"
				+ "                    ]"
				+ "                },"
				+ "              {\"task\": \"task5\"},"
				+ "                {"
				+ "                  \"loop\": ["
				+ "                        {"
				+ "                          \"sequence\": ["
				+ "                              {\"task\":\"task6\"},"
				+ "                              {\"task\":\"task7\"}"
				+ "                            ]"
				+ "                        },"
				+ "                        {"
				+ "                          \"iteration\": {"
				+ "                              \"num\": 1,"
				+ "                              \"prob\": 0.7"
				+ "                            }"
				+ "                        },"
				+ "                        {"
				+ "                          \"iteration\": {"
				+ "                              \"num\": 2,"
				+ "                              \"prob\": 0.3"
				+ "                            }"
				+ "                        },"
				+ "                        {"
				+ "                          \"iteration\": {"
				+ "                              \"num\": 3,"
				+ "                              \"prob\": 0.1"
				+ "                            }"
				+ "                        },"
				+ "                        {"
				+ "                          \"iteration\": {"
				+ "                              \"num\": 4,"
				+ "                              \"prob\": 0.01"
				+ "                            }"
				+ "                        }"
				+ "                    ]"
				+ "                }"
				+ "            ]"
				+ "        },"
				+ "      \"requirements\": {"
				+ "          \"constraints\": ["
				+ "                {"
				+ "                  \"indicator\":\"A-PUE\","
				+ "                  \"element\":\"VM1\","
				+ "                  \"operator\":\"<\","
				+ "                  \"value\":\"1.4\""
				+ "                },"
				+ "                {"
				+ "                  \"indicator\":\"Responsetime\","
				+ "                  \"element\":\"Task1\","
				+ "                  \"operator\":\"<\","
				+ "                  \"value\":\"1ms\""
				+ "                },"
				+ "                {"
				+ "                  \"indicator\":\"Responsetime\","
				+ "                  \"element\":\"Application\","
				+ "                  \"operator\":\"<\","
				+ "                  \"value\":\"10ms\""
				+ "                },"
				+ "                {"
				+ "                  \"indicator\":\"CPUUsage\","
				+ "                  \"element\":\"VM1\","
				+ "                  \"operator\":\"><\","
				+ "                  \"values\": ["
				+ "                      \"60\","
				+ "                      \"90\""
				+ "                    ]"
				+ "                }"
				+ "            ]"
				+ "        },"
				+ "      \"resources\": {"
				+ "          \"name\":\"MyExperiment\","
				+ "          \"description\":\"Experimentdescription\","
				+ "          \"duration\": 120,"
				+ "          \"resources\": ["
				+ "                {"
				+ "                  \"compute\": {"
				+ "                      \"name\":\"Client1\","
				+ "                      \"description\":\"A description of the client\","
				+ "                      \"instanceType\":\"small\","
				+ "                      \"locations\": [],"
				+ "                      \"resources\": ["
				+ "                            {"
				+ "                              \"storage\":\"@BonFIREDebianSqueezev5\""
				+ "                            },"
				+ "                            {"
				+ "                              \"network\":\"@BonFIREWAN\""
				+ "                            }"
				+ "                        ]"
				+ "                    }"
				+ "                },"
				+ "                { \"compute\": {"
				+ "                      \"name\":\"Client2\","
				+ "                      \"description\":\"A description of the client\","
				+ "                      \"instanceType\":\"small\","
				+ "                      \"resources\": ["
				+ "                            {"
				+ "                              \"storage\":\"@BonFIREDebianSqueezev5\""
				+ "                            },"
				+ "                            {"
				+ "                              \"network\":\"@BonFIREWAN\""
				+ "                            }"
				+ "                        ]"
				+ "                    }"
				+ "                },"
				+ "                { \"compute\": {"
				+ "                      \"name\":\"Client3\","
				+ "                      \"description\":\"A description of the client\","
				+ "                      \"instanceType\":\"small\","
				+ "                      \"locations\": ["
				+ "                          \"fr-inria\""
				+ "                        ],"
				+ "                      \"resources\": ["
				+ "                            {"
				+ "                              \"storage\":\"@BonFIREDebianSqueezev5\""
				+ "                            },"
				+ "                            {"
				+ "                              \"network\":\"@BonFIREWAN\""
				+ "                            }"
				+ "                        ]"
				+ "                    }"
				+ "                },"
				+ "                {"
				+ "                  \"compute\": {"
				+ "                      \"name\":\"Client4\","
				+ "                      \"description\":\"A description of the server\","
				+ "                      \"instanceType\":\"small\","
				+ "                      \"locations\": ["
				+ "                          \"uk-epcc\", \"fr-inria\""
				+ "                        ],"
				+ "                      \"resources\": ["
				+ "                            {"
				+ "                              \"storage\":\"@BonFIREDebianSqueezev3\""
				+ "                            },"
				+ "                            {"
				+ "                              \"network\":\"@BonFIREWAN\""
				+ "                            }" + "                        ]"
				+ "                    }" 
				+ "                }"
				+ "            ]" 
				+ "        }," 
				 + "      \"data\": {"
					+ "          \"datadependency\": ["
					+ "              {\"task\":\"task1\"},"
					+ "              {\"task\":\"task2\"}" + "            ]"
					+ "        },"

					+ "\"adaptation\": {" + "\"allowed\": \"yes\"" + "   }" + "}"
				+ "}";
		
			RestService restService = new RestService();
		
			ExperimentDescriptor ed = restService.checkIfItIsNecessaryToOptimize(applicationProfile);
			
			List<ResourceCompute> computes =  ed.getResourcesCompute();
			
			for(ResourceCompute compute : computes) {
				List<String> locations = compute.getCompute().getLocations();
				assertEquals(1, locations.size());
				
				if(compute.getCompute().getName().equals("Client1")) {
					boolean ok = false;
					
					if(locations.get(0).equals("uk-epcc") || locations.get(0).equals("de-hlrs") || locations.get(0).equals("fr-inria")) {
						logger.debug("Client: " + compute.getCompute().getName() + " location: " + locations.get(0));
						ok = true;
					}
					
					assertTrue(ok);
				} else if(compute.getCompute().getName().equals("Client2")) {
					boolean ok = false;
					
					if(locations.get(0).equals("uk-epcc") || locations.get(0).equals("de-hlrs") || locations.get(0).equals("fr-inria")) {
						logger.debug("Client: " + compute.getCompute().getName() + " location: " + locations.get(0));
						
						ok = true;
					}
					
					assertTrue(ok);
				} else if(compute.getCompute().getName().equals("Client3")) {
					boolean ok = false;
					
					if(locations.get(0).equals("fr-inria")) {
						logger.debug("Client: " + compute.getCompute().getName() + " location: " + locations.get(0));
						ok = true;
					}
					
					assertTrue(ok);
				} else {
					boolean ok = false;
					
					if(locations.get(0).equals("uk-epcc") || locations.get(0).equals("fr-inria")) {
						logger.debug("Client: " + compute.getCompute().getName() + " location: " + locations.get(0));
						ok = true;
					}
					
					assertTrue(ok);
				} 
			}
	}
}
