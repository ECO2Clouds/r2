package eu.eco2clouds.api.bonfire.occi.datamodel;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

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
 *
 * Test the POJO Item funcitonality
 * @author David Garcia Perez - AtoS
 *
 */
public class ItemTest {
	
	@Test
	public void pojo() {
		Items items = new Items();
		items.setOffset(1);
		items.setTotal(2);
		
		ArrayList<Compute> computes = new ArrayList<Compute>();
		ArrayList<Network> networks = new ArrayList<Network>();
		ArrayList<Configuration> configurations = new ArrayList<Configuration>();
		ArrayList<Experiment> experiments = new ArrayList<Experiment>();
		ArrayList<Storage> storages = new ArrayList<Storage>();
		ArrayList<Location> locations = new ArrayList<Location>();
		items.setComputes(computes);
		items.setNetworks(networks);
		items.setConfigurations(configurations);
		items.setExperiments(experiments);
		items.setStorages(storages);
		items.setLocations(locations);
		
		assertEquals(computes, items.getComputes());
		assertEquals(networks, items.getNetworks());
		assertEquals(configurations, items.getConfigurations());
		assertEquals(experiments, items.getExperiments());
		assertEquals(storages, items.getStorages());
		assertEquals(locations, items.getLocations());
		assertEquals(1, items.getOffset());
		assertEquals(2, items.getTotal());
	}
}
