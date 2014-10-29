/* 
 * Copyright 2014 Politecnico di Milano.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

 *  @author: Pierluigi Plebani, Politecnico di Milano, Italy
 *  e-mail pierluigi.plebani@polimi.it

 */
package eu.eco2clouds.portal.scheduler;

import eu.eco2clouds.applicationProfile.datamodel.ApplicationProfile;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 *  
 */
public class ApplicationProfileParserManager implements Serializable {

    private final static Logger logger =
            Logger.getLogger(ApplicationProfileParserManager.class.getName());

    public ApplicationProfile parse(String applicationProfileJSON) throws JsonMappingException, IOException {

        ApplicationProfile applicationProfileObject = null;

        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(
                DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
        mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);


        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

        applicationProfileObject = mapper.readValue(
                applicationProfileJSON, ApplicationProfile.class);

        return applicationProfileObject;


    }

    public String deserialize(ApplicationProfile applicationProfile) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(
                DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, true);
        mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);

        return mapper.writeValueAsString(applicationProfile);

    }
    
   
    
    
    public static final String APTEST = "{ \"applicationprofile\": {"
            + "\n" + "\"flow\": {"
            + "\n" + "          \"sequence\": ["
            + "\n" + "              {\"task\":\"task1\"},"
            + "\n" + "              {\"task\":\"task2\"},"
            + "\n" + "                {"
            + "\n" + "                  \"branches\": ["
            + "\n" + "                        {"
            + "\n" + "                          \"branch\": ["
            + "\n" + "                                {"
            + "\n" + "                                  \"prob\": 0.3"
            + "\n" + "                                },"
            + "\n" + "                              {\"task\":\"task3\"}"
            + "\n" + "                            ]"
            + "\n" + "                        },"
            + "\n" + "                        {"
            + "\n" + "                          \"branch\": ["
            + "\n" + "                                {"
            + "\n" + "                                  \"prob\": 0.7"
            + "\n" + "                                },"
            + "\n" + "                              {\"task\":\"task4\"}"
            + "\n" + "                            ]"
            + "\n" + "                        }"
            + "\n" + "                    ]"
            + "\n" + "                },"
            + "\n" + "              {\"task\": \"task5\"},"
            + "\n" + "                {"
            + "\n" + "                  \"loop\": ["
            + "\n" + "                        {"
            + "\n" + "                          \"sequence\": ["
            + "\n" + "                              {\"task\":\"task6\"},"
            + "\n" + "                              {\"task\":\"task7\"}"
            + "\n" + "                            ]"
            + "\n" + "                        },"
            + "\n" + "                        {"
            + "\n" + "                          \"iteration\": {"
            + "\n" + "                              \"num\": 1,"
            + "\n" + "                              \"prob\": 0.7"
            + "\n" + "                            }"
            + "\n" + "                        },"
            + "\n" + "                        {"
            + "\n" + "                          \"iteration\": {"
            + "\n" + "                              \"num\": 2,"
            + "\n" + "                              \"prob\": 0.3"
            + "\n" + "                            }"
            + "\n" + "                        },"
            + "\n" + "                        {"
            + "\n" + "                          \"iteration\": {"
            + "\n" + "                              \"num\": 3,"
            + "\n" + "                              \"prob\": 0.1"
            + "\n" + "                            }"
            + "\n" + "                        },"
            + "\n" + "                        {"
            + "\n" + "                          \"iteration\": {"
            + "\n" + "                              \"num\": 4,"
            + "\n" + "                              \"prob\": 0.01"
            + "\n" + "                            }"
            + "\n" + "                        }"
            + "\n" + "                    ]"
            + "\n" + "                }"
            + "\n" + "            ]"
            + "\n" + "        },"
            + "\n" + "      \"requirements\": {"
            + "\n" + "          \"constraints\": ["
            + "\n" + "                {"
            + "\n" + "                  \"indicator\":\"A-PUE\","
            + "\n" + "                  \"element\":\"VM1\","
            + "\n" + "                  \"operator\":\"<\","
            + "\n" + "                  \"value\":\"1.4\""
            + "\n" + "                },"
            + "\n" + "                {"
            + "\n" + "                  \"indicator\":\"Responsetime\","
            + "\n" + "                  \"element\":\"Task1\","
            + "\n" + "                  \"operator\":\"<\","
            + "\n" + "                  \"value\":\"1ms\""
            + "\n" + "                },"
            + "\n" + "                {"
            + "\n" + "                  \"indicator\":\"Responsetime\","
            + "\n" + "                  \"element\":\"Application\","
            + "\n" + "                  \"operator\":\"<\","
            + "\n" + "                  \"value\":\"10ms\""
            + "\n" + "                },"
            + "\n" + "                {"
            + "\n" + "                  \"indicator\":\"CPUUsage\","
            + "\n" + "                  \"element\":\"VM1\","
            + "\n" + "                  \"operator\":\"><\","
            + "\n" + "                  \"values\": ["
            + "\n" + "                      \"60\","
            + "\n" + "                      \"90\""
            + "\n" + "                    ]"
            + "\n" + "                }"
            + "\n" + "            ]"
            + "\n" + "        },"
            + "\n" + "      \"resources\": {"
            + "\n" + "          \"name\":\"MyExperiment\","
            + "\n" + "          \"description\":\"Experiment description\","
            + "\n" + "          \"duration\": 120,"
            + "\n" + "          \"resources\": ["
            + "\n" + "                {"
            + "\n" + "                  \"compute\": {"
            + "\n" + "                      \"name\":\"Client\","
            + "\n" + "                      \"description\":\"A description of the client\","
            + "\n" + "                      \"instanceType\":\"small\","
            + "\n" + "                      \"locations\": ["
            + "\n" + "                          \"uk-epcc\""
            + "\n" + "                        ],"
            + "\n" + "                      \"resources\": ["
            + "\n" + "                            {"
            + "\n" + "                              \"storage\":\"@BonFIREDebianSqueezev3\""
            + "\n" + "                            },"
            + "\n" + "                            {"
            + "\n" + "                              \"network\":\"@BonFIREWAN\""
            + "\n" + "                            }"
            + "\n" + "                        ]"
            + "\n" + "                    }"
            + "\n" + "                },"
            + "\n" + "                {"
            + "\n" + "                  \"compute\": {"
            + "\n" + "                      \"name\":\"Server\","
            + "\n" + "                      \"description\":\"A description of the server\","
            + "\n" + "                      \"instanceType\":\"small\","
            + "\n" + "                      \"locations\": ["
            + "\n" + "                          \"fr-inria\""
            + "\n" + "                        ],"
            + "\n" + "                      \"resources\": ["
            + "\n" + "                            {"
            + "\n" + "                              \"storage\":\"@BonFIREDebianSqueezev5\""
            + "\n" + "                            },"
            + "\n" + "                            {"
            + "\n" + "                              \"network\":\"@BonFIREWAN\""
            + "\n" + "                            }"
            + "\n" + "                        ]"
            + "\n" + "                    }"
            + "\n" + "                }"
            + "\n" + "            ]"
            + "\n" + "        },"
            + "\n" + "      \"data\": {"
            + "\n" + "          \"datadependency\": ["
            + "\n" + "              {\"task\":\"task1\"},"
            + "\n" + "              {\"task\":\"task2\"}"
            + "\n" + "            ]"
            + "\n" + "        }"
            + "\n" + "    }"
            + "\n" + "}";
}
