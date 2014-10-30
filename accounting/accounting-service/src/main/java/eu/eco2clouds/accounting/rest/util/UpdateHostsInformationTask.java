package eu.eco2clouds.accounting.rest.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import eu.eco2clouds.accounting.datamodel.Host;
import eu.eco2clouds.accounting.datamodel.Testbed;
import eu.eco2clouds.accounting.datamodel.xml.HostPool;
import eu.eco2clouds.accounting.datamodel.xml.HostXml;
import eu.eco2clouds.accounting.service.HostDAO;
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
public class UpdateHostsInformationTask {
private static Logger logger = Logger.getLogger(UpdateHostsInformationTask.class);
	
	public static String TESTBED_DAO = "testbedDAO";
	public static String HOST_DAO = "hostDAO";
	public static String HOSTINFO_STRING = "hostInfoString";
	public static String TESTBED_NAME = "testbedName";
	
	@Autowired
	private TestbedDAO testbedDAO;
	@Autowired
	private HostDAO hostDAO;

    private Client client;
    
    @Scheduled(cron = "${update.hostinfo}")
    public void updateHostsInfo(){   	
    	client = new ClientHC();    
    	
    	//get testbeds from database
    	List<Testbed> testbeds = this.testbedDAO.getAll();
    	for (Testbed testbed : testbeds){
    		updateHostInformation(testbed.getName(), client.getHostInfo(testbed));    		
    	}    	
    }
    
    private boolean notInDatabase(List<Host> hostsInDatabase, HostXml hostFromStatus) {
		boolean notInDatabase = true;
		
		for(Host hostInDatabase : hostsInDatabase) {
			logger.debug("COMPARING NAMES: " + hostFromStatus.getName() + " HOST DB  NAME: " + hostInDatabase.getName() + "<-");
				if(hostInDatabase.getName().equals(hostFromStatus.getName())) {
					logger.debug("FOUND A MATCH!!!!");
					notInDatabase = false;
				}
			}
		
		return notInDatabase;
	}
    
    
    public void updateHostInformation(String testbedName, String hostInfoString) {
    	
    	List<Host> testbedHostsDB = new ArrayList<Host>();
	    List<HostXml> testbedHosts = new ArrayList<HostXml>();
	    
    	try {
			JAXBContext jaxbContext = JAXBContext.newInstance(HostPool.class);

			Unmarshaller jaxbUnmarshaller = null;
			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			HostPool hostPoolObject = (HostPool) jaxbUnmarshaller.unmarshal(new StringReader(hostInfoString));

			//Obtains list of hosts from the testbedInfo xml
			testbedHosts = hostPoolObject.getHosts();

		} catch (JAXBException e) {
			e.printStackTrace();
		} 
			    
		//Retrieves a list of hosts from the database
		Testbed testbed = this.testbedDAO.getByName(testbedName);
		testbedHostsDB = testbed.getHosts();
		
		for(HostXml host : testbedHosts) {
			logger.debug("HOST FOUND: " + host.getName());
			boolean notInDatabase = notInDatabase(testbedHostsDB, host);
			
			if(notInDatabase) {
				logger.debug("HOST NOT IN DATABASE, ADDING NEW HOST");
				
				Host newHost = new Host();
				newHost.setState(host.getState());
				newHost.setName(host.getName());
				newHost.setConnected(true);
				// We refresh the object from the database...
				testbed = this.testbedDAO.getByName(testbedName);
				testbed.getHosts().add(newHost);
				this.testbedDAO.update(testbed);
			} else {
				logger.debug("HOST IN DATABASE, UPDATION HOST");
				Host updatedHost = this.hostDAO.getByName(host.getName());
				updatedHost.setState(host.getState());
				updatedHost.setConnected(true);
				this.hostDAO.update(updatedHost);
			}
		}
	}
    
}
