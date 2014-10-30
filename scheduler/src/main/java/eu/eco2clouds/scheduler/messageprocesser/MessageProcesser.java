package eu.eco2clouds.scheduler.messageprocesser;

import static eu.eco2clouds.scheduler.SchedulerDictionary.SCHEDULER_VERSION;

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import eu.eco2clouds.applicationProfile.datamodel.ApplicationProfile;
import eu.eco2clouds.applicationProfile.datamodel.ExperimentDescriptor;

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
public class MessageProcesser {
	/*
	 * GET Path: /
	 */

	public String getBodySchedulerInfo() {

		// TO-DO timeStamp

		String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<root xmlns=\"http://api.eco2clouds-project.eu/doc/schemas/occi\" href=\"/\">\n"
				+ "<version>"
				+ SCHEDULER_VERSION
				+ "</version>\n"
				+ "<timeStamp>1368714310</version>\n"
				+ "<link rel=\"experiments\" href=\"/experiments\" type=\"application/vnd.eco2clouds+xml\"/>\n";
		return body;
	}

	/*
	 * GET Path /experiments
	 */
	public String getBodyGetExperiments() throws JsonParseException,
			JsonMappingException, IOException {

		String body = "";
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);

		try {
			ApplicationProfile applicationProfile = mapper.readValue(new File(
					"./src/main/resources/applicationprofile.json"),
					ApplicationProfile.class);

			ExperimentDescriptor expDesc = applicationProfile
					.getExperimentDescriptor();

			String result = "";
			;

			result += "<experiment>\n" + "    <name>" + expDesc.getName()
					+ "</name>\n" + "    <description>"
					+ expDesc.getDescription() + "</description>\n"
					+ "    <duration>" + expDesc.getDuration() + "</duration>"
					+ "\n" + "</experiment>" + "\n";

			body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
					+ "<collection xmlns=\" http://api.eco2clouds.eu/doc/schemas/occi\" href=\"/experiments\">"
					+ "\n"
					+ "<items offset=\"0\" total=\"0\">"
					+ "\n"
					+ result
					+ "<link href=\"/\" rel=\"parent\" type=\"application/vnd.eco2clouds+xml\"/>";

		} catch (Exception ex) {
			System.err.println("Unable to retrieve Experiment");
		}
		return body;
	}

	/*
	 * GET Path : Experiments/{id}
	 * 
	 * <?xml version="1.0" encoding="UTF-8"?> <experiment
	 * xmlns=" http://api.eco2clouds.eu/doc/schemas/occi " href="/experiments">
	 * <id>...</id> BonFIRE experiment OCCI document </experiment>
	 */

	public String getBodyGetExperiment(String id) throws JsonParseException,
			JsonMappingException, IOException {

		int experimentId = Integer.parseInt(id);
		String body = "";

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);

		ApplicationProfile applicationProfile = mapper.readValue(new File(
				"./src/main/resources/myApplicationProfile.json"),
				ApplicationProfile.class);

		try {

			ExperimentDescriptor expDesc = applicationProfile.getExperimentDescriptor();
				

			body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<collection xmlns=\" http://api.eco2clouds.eu/doc/schemas/occi\" href=\"/experiments\">\n"
					+ "<items offset=\"0\" total=\"0\">\n"
					+ "<experiment>\n"
					+ "   <name>"
					+ expDesc.getName()
					+ "</name>\n"
					+ "   <description>"
					+ expDesc.getDescription()
					+ "</description>\n"
					+ "   <duration>"
					+ expDesc.getDuration()
					+ "</duration>\n"
					+ "</experiment>\n"
					+ "<link href=\"/\" rel=\"parent\" type=\"application/vnd.eco2clouds+xml\"/>";

		} catch (Exception ex) {
			System.err.println("Unable to retrieve Experiment");
		}
		return body;
		/*
		 * POST Path /experiments
		 * 
		 * <?xml version="1.0" encoding="UTF-8"?>
		 * 
		 * <experiment xmlns=" http://api.eco2clouds.eu/doc/schemas/occi "
		 * href="/experiments"> <id>...</id> BonFIRE experiment OCCI document
		 * </experiment>
		 */

	}
}