package eu.eco2clouds.accounting.service.jpa;

import java.util.ArrayList;
import java.util.List;

import eu.eco2clouds.accounting.datamodel.Host;
import eu.eco2clouds.accounting.datamodel.HostData;
import eu.eco2clouds.accounting.datamodel.Testbed;
import eu.eco2clouds.accounting.service.TestbedDAO;

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
// TODO TO DELETE ONCE IMPLEMENTATION OF DATABASE IS DONE
public class MockTestbedDAO implements TestbedDAO {
	private List<Testbed> testbeds;

	public MockTestbedDAO() {
		Testbed testbed1 = new Testbed();
		testbed1.setName("fr-inria");
		testbed1.setUrl("http://frontend.bonfire.grid5000.fr/one-status.xml");
		Testbed testbed2 = new Testbed();
		testbed2.setName("uk-epcc");
		testbed2.setUrl("http://bonfire.epcc.ed.ac.uk/logs/one-status.xml");
		
		testbeds = new ArrayList<Testbed>();
		testbeds.add(testbed1);
		testbeds.add(testbed2);
	}
	
	@Override
	public boolean save(Testbed t) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Testbed> getAll() {
		return testbeds;
	}

	@Override
	public Testbed getById(int id) {
		
		return null;
	}

	@Override
	public boolean delete(Testbed something) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Testbed something) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Testbed getByName(String name) {
		for(Testbed testbed : testbeds) {
			if(testbed.getName().equals(name)) return testbed;
		}
		return null;
	}

	@Override
	public Testbed find(Testbed Testbed) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Testbed deleteHost(Testbed testbed, Host host) {
		// TODO Auto-generated method stub
		return null;
	}

}
