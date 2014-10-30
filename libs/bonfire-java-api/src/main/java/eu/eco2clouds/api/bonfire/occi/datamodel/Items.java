package eu.eco2clouds.api.bonfire.occi.datamodel;

import static eu.eco2clouds.api.bonfire.occi.Dictionary.NAMESPACE;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 * @author: David Garcia Perez. Atos Research and Innovation, Atos SPAIN SA
 * e-mail david.garciaperez@atos.net
 *
 * POJO represetnation of the items inside a BonFIRE OCCI Collection
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "items", namespace = NAMESPACE)
public class Items {
	@XmlAttribute
	private int offset;
	@XmlAttribute
	private int total;
	@XmlElement(name="compute", namespace = NAMESPACE)
	private ArrayList<Compute> computes;
	@XmlElement(name="network", namespace = NAMESPACE)
	private ArrayList<Network> networks;
	@XmlElement(name="storage", namespace = NAMESPACE)
	private ArrayList<Storage> storages;
	@XmlElement(name="configuration", namespace = NAMESPACE)
	private ArrayList<Configuration> configurations;
	@XmlElement(name="location", namespace = NAMESPACE)
	private ArrayList<Location> locations;
	@XmlElement(name="experiment", namespace = NAMESPACE)
	private ArrayList<Experiment> experiments;

	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}

	public ArrayList<Compute> getComputes() {
		return computes;
	}
	public void setComputes(ArrayList<Compute> computes) {
		this.computes = computes;
	}

	public ArrayList<Network> getNetworks() {
		return networks;
	}
	public void setNetworks(ArrayList<Network> networks) {
		this.networks = networks;
	}

	public ArrayList<Storage> getStorages() {
		return storages;
	}
	public void setStorages(ArrayList<Storage> storages) {
		this.storages = storages;
	}

	public ArrayList<Configuration> getConfigurations() {
		return configurations;
	}
	public void setConfigurations(ArrayList<Configuration> configurations) {
		this.configurations = configurations;
	}

	public ArrayList<Location> getLocations() {
		return locations;
	}
	public void setLocations(ArrayList<Location> locations) {
		this.locations = locations;
	}

	public ArrayList<Experiment> getExperiments() {
		return experiments;
	}
	public void setExperiments(ArrayList<Experiment> experiments) {
		this.experiments = experiments;
	}
}
