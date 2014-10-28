package eu.eco2clouds.applicationProfile.datamodel;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonRootName;

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
@JsonPropertyOrder({ "flow", "requirement", "resources", "data" })
@JsonRootName(value = "applicationprofile")
public class ApplicationProfile {

	@JsonProperty("optimize")
	private boolean optimize = true;
	@JsonProperty("flow")
	private Flow flow;
	@JsonProperty("requirements")
	private Requirement requirement;
	@JsonProperty("resources")
	private ExperimentDescriptor experimentDescriptor;
	@JsonProperty("data")
	private Data data;
	@JsonProperty("adaptation")
	private Adaptation adaptation;

	public boolean isOptimize() {
		return optimize;
	}

	public void setOptimize(boolean optimize) {
		this.optimize = optimize;
	}

	public Flow getFlow() {
		return flow;
	}

	public void setFlow(Flow flow) {
		this.flow = flow;
	}

	public Requirement getRequirement() {
		return requirement;
	}

	public void setRequirement(Requirement requirement) {
		this.requirement = requirement;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	@JsonProperty("resources")
	public ExperimentDescriptor getExperimentDescriptor() {
		return experimentDescriptor;
	}

	@JsonProperty("resources")
	public void setExperimentDescriptor(
			ExperimentDescriptor experimentDescriptor) {
		this.experimentDescriptor = experimentDescriptor;
	}

	public Adaptation getAdaptation() {
		return adaptation;
	}

	public void setAdaptation(Adaptation adaptation) {
		this.adaptation = adaptation;
	}

}
