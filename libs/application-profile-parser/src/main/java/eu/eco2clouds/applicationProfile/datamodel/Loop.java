package eu.eco2clouds.applicationProfile.datamodel;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonProperty;
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
@JsonRootName(value = "loop")
public class Loop {

	@JsonProperty("sequence")
	private  ArrayList<Sequence> sequence= new  ArrayList<Sequence>();

	@JsonProperty("iteration")
	private ArrayList<Iteration> iteration = new ArrayList<Iteration>();

	public ArrayList<Sequence> getSequence() {
		return sequence;
	}

	public void setSequence(ArrayList<Sequence> sequence) {
		this.sequence = sequence;
	}

	public ArrayList<Iteration> getIteration() {
		return iteration;
	}

	public void setIteration(ArrayList<Iteration> iteration) {
		this.iteration = iteration;
	}

}