package eu.eco2clouds.accounting.rest.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import eu.eco2clouds.accounting.datamodel.Action;
import eu.eco2clouds.accounting.datamodel.Host;
import eu.eco2clouds.accounting.datamodel.HostData;
import eu.eco2clouds.accounting.datamodel.Testbed;
import eu.eco2clouds.accounting.datamodel.xml.HostPool;
import eu.eco2clouds.accounting.datamodel.xml.HostShare;
import eu.eco2clouds.accounting.service.HostDAO;
import eu.eco2clouds.accounting.service.HostDataDAO;
import eu.eco2clouds.accounting.service.TestbedDAO;
import eu.eco2clouds.accounting.testbedclient.Client;
import eu.eco2clouds.accounting.testbedclient.ClientHC;

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
public class UpdateTestbedsHostsThread extends Thread {

	private String hostInfoString = null;
	private JAXBContext jaxbContext = null;
	private TestbedDAO testbedDAO;
	private HostDAO hostDAO;
	private HostDataDAO hostDataDAO;
	private Testbed testbed = null;
	private Set<String> listHostNamesInTestbed = new HashSet<String>();
	private List <Host> testbedHostsDB = new ArrayList<Host>();
	private List<eu.eco2clouds.accounting.datamodel.xml.HostXml> testbedHosts = new ArrayList<eu.eco2clouds.accounting.datamodel.xml.HostXml>();
	private eu.eco2clouds.accounting.datamodel.xml.HostXml hostInfo;
	private Host host;
	private HostData hostData;
	private HostShare hShare;
	private Action action;
	private int vmId;
	private List testbeds = new ArrayList<Testbed>();
	private String name = null;
	private Client client = new ClientHC();
	private Host hostInserted = new Host();

	private String testbedName = null;

	public UpdateTestbedsHostsThread(TestbedDAO testbedDAO, HostDAO hostDAO,
			HostDataDAO hostDataDAO,List testbeds,
			String hostInfo, Action action) {

		this.testbedDAO = testbedDAO;
		this.hostDAO = hostDAO;
		this.hostDataDAO = hostDataDAO;
		this.hostInfoString = hostInfo;
		this.action = action;
		this.testbeds=testbeds;

	}

	public void run() {

		try {
			

			for (int i = 0; i < testbeds.size(); i++) {
				testbed = (Testbed) testbeds.get(i);
				testbedName = testbed.getName();
			}

		
			hostInfoString = client.getHostInfo(testbed);

			if (hostInfoString != null) {

				jaxbContext = JAXBContext
						.newInstance(eu.eco2clouds.accounting.datamodel.xml.HostPool.class);

				Unmarshaller jaxbUnmarshaller = null;
				jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				HostPool hostPoolObject = (HostPool) jaxbUnmarshaller
						.unmarshal(new StringReader(hostInfoString));

				// Obtains list of hosts from the testbedInfo xml
				testbedHosts = hostPoolObject.getHosts();

				// Retrieves a list of hosts from the database
				testbed = this.testbedDAO.getByName(testbedName);
				testbedHostsDB = testbed.getHosts();


				// Obtains a list with only the name of the hosts
				for (Host hostInTestbed : testbedHostsDB)
					listHostNamesInTestbed.add(hostInTestbed.getName());

				for (int hostIndex = 0; hostIndex < testbedHosts.size(); hostIndex++) {

					hostInfo = testbedHosts.get(hostIndex);
					// Testbed name from the list of testbedInfo xml does not
					// exist in
					// database and is stored
					if (!listHostNamesInTestbed.contains(hostInfo.getName())) {

						host = new Host();
						HostData hostData = new HostData();
						hShare = hostInfo.getHostShare();

						host.setState(hostInfo.getState());
						host.setName(hostInfo.getName());
                        
						
						if (action != null) {
							hostData.setAction(action);
						}
						
				
						this.hostDAO.saveHost(host,testbed.getId());

						hostInserted = this.hostDAO.getByName(host.getName());
						
						hostData.setHost(hostInserted);
						hostData.setCpuUsage(hShare.getCpuUsage());
						hostData.setDiskUsage(hShare.getDiskUsage());
						hostData.setFreeCpu(hShare.getFreeCpu());
						hostData.setFreeDisk(hShare.getFreeDisk());
						hostData.setFreeMen(hShare.getFreeMem());
						hostData.setMaxCpu(hShare.getMaxDisk());
						hostData.setMaxDisk(hShare.getMaxDisk());
						hostData.setMaxMem(hShare.getMaxMem());
						hostData.setRunningVms(hShare.getRunningVms());
						hostData.setUsedCpu(hShare.getUsedCpu());
						hostData.setUsedDisk(hShare.getUsedDisk());
						hostData.setUsedMem(hShare.getUsedMem());
						
						
						this.hostDataDAO.saveHostData(hostData);
					}
				}

			}

		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

}
