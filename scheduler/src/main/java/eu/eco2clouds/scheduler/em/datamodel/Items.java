package eu.eco2clouds.scheduler.em.datamodel;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
 * POJO represetnation of the items inside a BonFIRE OCCI Collection
 * @author David Garcia Perez - AtoS
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "items")
public class Items {
	@XmlElement(name = "managed_experiment")
	private ArrayList<ManagedExperiment> managedExperiments;

	public ArrayList<ManagedExperiment> getManagedExperiments() {
		return managedExperiments;
	}

	public void setManagedExperiments(ArrayList<ManagedExperiment> managedExperiments) {
		this.managedExperiments = managedExperiments;
	}
}
