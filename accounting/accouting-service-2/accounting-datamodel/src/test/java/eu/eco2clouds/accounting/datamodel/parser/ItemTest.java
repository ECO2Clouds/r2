package eu.eco2clouds.accounting.datamodel.parser;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.eco2clouds.accounting.datamodel.parser.Experiment;
import eu.eco2clouds.accounting.datamodel.parser.HostMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.Items;
import eu.eco2clouds.accounting.datamodel.parser.VM;


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
 *
 */
public class ItemTest {
	
	@Test
	public void pojo() {
		Items items = new Items();
		items.setOffset(1);
		items.setTotal(2);
		
		List<VM> vMs = new ArrayList<VM>();
		items.setvMs(vMs);
		List<Experiment> experiments = new ArrayList<Experiment>();
		items.setExperiments(experiments);
		List<HostMonitoring> hostMonitorings = new ArrayList<HostMonitoring>();
		items.setHostMonitorings(hostMonitorings);

		assertEquals(1, items.getOffset());
		assertEquals(2, items.getTotal());
		assertEquals(vMs, items.getvMs());
		assertEquals(hostMonitorings, items.getHostMonitorings());
		assertEquals(experiments, items.getExperiments());
	}
	
	@Test
	public void addExperiment() {
		Items items = new Items();
		assertEquals(null, items.getExperiments());
		
		Experiment experiment = new Experiment();
		items.addExperiment(experiment);
		
		assertEquals(1, items.getExperiments().size());
		assertEquals(experiment, items.getExperiments().get(0));
	}
	
	@Test
	public void addHostMonitoring() {
		Items items = new Items();
		assertEquals(null, items.getHostMonitorings());
		
		HostMonitoring hostMonitoring = new HostMonitoring();
		items.addHostMonitoring(hostMonitoring);
		
		assertEquals(1, items.getHostMonitorings().size());
		assertEquals(hostMonitoring, items.getHostMonitorings().get(0));
	}
}
