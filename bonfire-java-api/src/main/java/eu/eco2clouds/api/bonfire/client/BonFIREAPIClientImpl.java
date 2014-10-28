package eu.eco2clouds.api.bonfire.client;

import static eu.eco2clouds.api.bonfire.occi.Dictionary.LOCATION_URL;
import static eu.eco2clouds.api.bonfire.occi.Dictionary.EXPERIMENT_URL;
import static eu.eco2clouds.api.bonfire.occi.Dictionary.STORAGE_URL;
import static eu.eco2clouds.api.bonfire.occi.Dictionary.NETWORK_URL;
import static eu.eco2clouds.api.bonfire.occi.Dictionary.BONFIRE_XML;
import static eu.eco2clouds.api.bonfire.occi.Dictionary.COMPUTE_URL;
import static eu.eco2clouds.api.bonfire.occi.Dictionary.CONFIGURATIONS_URL;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import eu.eco2clouds.api.bonfire.client.exceptions.NoHrefSetException;
import eu.eco2clouds.api.bonfire.client.rest.Response;
import eu.eco2clouds.api.bonfire.client.rest.RestClient;
import eu.eco2clouds.api.bonfire.occi.datamodel.Collection;
import eu.eco2clouds.api.bonfire.occi.datamodel.Compute;
import eu.eco2clouds.api.bonfire.occi.datamodel.Configuration;
import eu.eco2clouds.api.bonfire.occi.datamodel.Experiment;
import eu.eco2clouds.api.bonfire.occi.datamodel.Link;
import eu.eco2clouds.api.bonfire.occi.datamodel.Location;
import eu.eco2clouds.api.bonfire.occi.datamodel.Network;
import eu.eco2clouds.api.bonfire.occi.datamodel.Nic;
import eu.eco2clouds.api.bonfire.occi.datamodel.Resource;
import eu.eco2clouds.api.bonfire.occi.datamodel.Storage;

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
 * BonFIRE API client implemented using Jersey libraries
 * 
 */
public class BonFIREAPIClientImpl implements BonFIREAPIClient {
	private String userName;
	private String url;
	private String password;

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public ArrayList<Location> getLocations() {
		Collection collection = getCollectionOfResources(LOCATION_URL, userName, password);
		
		if(collection != null) 
			return collection.getItems().getLocations();
		else
			return new ArrayList<Location>();
	}
	
	@Override
	public ArrayList<Experiment> getExperiments() {
		Collection collection = getCollectionOfResources(EXPERIMENT_URL, userName, password);
		
		if(collection != null) 
			return collection.getItems().getExperiments();
		else
			return new ArrayList<Experiment>();
	}
	
	@Override
	public ArrayList<Storage> getStoragesOfALocation(Location location) {
		Collection collection = getCollectionOfResources(location.getHref().substring(1) + "/" + STORAGE_URL, userName, password);
		
		if(collection != null) 
			return collection.getItems().getStorages();
		else
			return new ArrayList<Storage>();
	}
	
	@Override
	public ArrayList<Network> getNetworksOfALocation(Location location) {
		Collection collection = getCollectionOfResources(location.getHref().substring(1) + "/" + NETWORK_URL, userName, password);
		
		if(collection != null) 
			return collection.getItems().getNetworks();
		else
			return new ArrayList<Network>();
	}
	
	@Override
	public Experiment createExperiment(Experiment experiment) {
		
		String payload = marshalResource(Experiment.class, experiment);
		
		Response response = RestClient.executePostMethod(url + "/" + EXPERIMENT_URL, userName, password, payload, BONFIRE_XML);
		
		return unmarshalRetrievedResource(Experiment.class, response.getPayload());
	}
	
	private Collection getCollectionOfResources(String path, String userName, String password) {
		Response response = RestClient.executeGetMethod(url + "/" + path, userName, password);
		
		// TODO implement throw exception if code is different from 200 (build Java API Exceptions...
		
		return unmarshalRetrievedResource(Collection.class, response.getPayload());
	}
	
	@Override
	public boolean deleteResource(Resource resource) {
		Response response = RestClient.executeDeleteMethod(url + resource.getHref(), userName, password);
		
		if(response.getStatusCode() == 202 || response.getStatusCode() == 204) 
			return true;
		else
			return false;
	}
	
	@Override
	public ArrayList<Configuration> getConfigurationsOfALocation(Location location) {
		Collection collection = getCollectionOfResources(location.getHref().substring(1) + "/" + CONFIGURATIONS_URL, userName, password);
		
		if(collection != null) 
			return collection.getItems().getConfigurations();
		else
			return new ArrayList<Configuration>();
	}
	
	@Override
	public Compute createCompute(Location location, Experiment experiment, Compute compute) {
		
		String payload = getComputePayload(location, compute);
		
		Response response = RestClient.executePostMethod(url + experiment.getHref() + "/" + COMPUTE_URL, userName, password, payload, BONFIRE_XML);
		
		return unmarshalRetrievedResource(Compute.class, response.getPayload());
	}
	
	private String getComputePayload(Location location, Compute compute) {
		//We create a link to indicate the API were the Compute is created... 
		Link link  = new Link();
		link.setRel("location");
		link.setHref(location.getHref());
		compute.addLink(link);
		
		return marshalResource(Compute.class, compute);
	}
	
	@Override
	public Storage createStorage(Location location, Experiment experiment, Storage storage) {
		//TODO refactor this with the previous method
		//We create a link to indicate the API were the Compute is created... 
		Link link  = new Link();
		link.setRel("location");
		link.setHref(location.getHref());
		storage.addLink(link);
		
		String payload = marshalResource(Storage.class, storage);
		
		Response response = RestClient.executePostMethod(url + experiment.getHref() + "/" + STORAGE_URL, userName, password, payload, BONFIRE_XML);
		
		return unmarshalRetrievedResource(Storage.class, response.getPayload());
	}
	
	@Override
	public Resource updateResourceInfo(Resource resource) throws NoHrefSetException {
		// TODO implement throw exception if code is different from 200 (build Java API Exceptions...
		String href = resource.getHref();
		
		if(href == null) throw new NoHrefSetException("The resource has no HREF value set, probably because it is not an resource that exists in BonFIRE");
		else if(href.equals("")) throw new NoHrefSetException("The resource has no HREF value set, probably because it is not an resource that exists in BonFIRE");
		
		Response response = RestClient.executeGetMethod(url + href, userName, password);
		
		if(resource instanceof Storage) {
			return unmarshalRetrievedResource(Storage.class, response.getPayload());
		} else if(resource instanceof Network) {
			return unmarshalRetrievedResource(Network.class, response.getPayload());
		} else if(resource instanceof Compute) {
			return unmarshalRetrievedResource(Compute.class, response.getPayload());
		} else if(resource instanceof Experiment) {
			return unmarshalRetrievedResource(Experiment.class, response.getPayload());
		}
		
		return null;
	}
	
	private<T> String marshalResource(Class<T> c, T resource) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(c);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			marshaller.marshal(resource, out);
			return out.toString();
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private<T> T unmarshalRetrievedResource(Class<T> c,String payload) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(c);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			return (T) jaxbUnmarshaller.unmarshal(new StringReader(payload));
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public Storage makeStoragePublic(Storage storage) {
		Storage publicStorage = new Storage();
		publicStorage.setPublicStorage("YES");
		
		return modifyStorage(storage, publicStorage);
	}
	
	private Storage modifyStorage(Storage originalStorage, Storage newStorage) {
		String payload = marshalResource(Storage.class, newStorage);
		
		Response response = RestClient.executePutMethod(url + originalStorage.getHref(), payload, userName, password);
		
		return unmarshalRetrievedResource(Storage.class, response.getPayload());
	}
	
	@Override
	public Storage unMakeStoragePublic(Storage storage) {
		Storage noPublicStorage = new Storage();
		noPublicStorage.setPublicStorage("NO");
		
		return modifyStorage(storage, noPublicStorage);
	}
	
	@Override
	public Compute createCompute(Location location, String experimentId, Compute compute) {
		String payload = getComputePayload(location, compute);
		
		Response response = RestClient.executePostMethod(url + "/experiments/" + experimentId + "/" + COMPUTE_URL, userName, password, payload, BONFIRE_XML);
		
		return unmarshalRetrievedResource(Compute.class, response.getPayload());
	}
	
	@Override
	public Compute createComputeAtBonFIREWAN(Location location, Experiment experiment, Compute compute) {
		setBonFIREWANNetwork(location, compute);

		String payload = getComputePayload(location, compute);
		
		Response response = RestClient.executePostMethod(url + experiment.getHref() + "/" + COMPUTE_URL, userName, password, payload, BONFIRE_XML);
		
		return unmarshalRetrievedResource(Compute.class, response.getPayload());
	}
	
	private void setBonFIREWANNetwork(Location location, Compute compute) {
		List<Network> networks = getNetworksOfALocation(location);
		
		for(Network network : networks) {
			if(network.getName().equals("BonFIRE WAN")) {
				Nic nic = new Nic();
				Network bonfireWAN = new Network();
				bonfireWAN.setHref(network.getHref());
				nic.setNetwork(bonfireWAN);
				compute.addNic(nic);
			}
		}
	}
	
	@Override
	public Compute createComputeAtBonFIREWAN(Location location, String experimentId, Compute compute) {
		setBonFIREWANNetwork(location, compute);

		String payload = getComputePayload(location, compute);
		
		Response response = RestClient.executePostMethod(url + "/experiments/" + experimentId + "/" + COMPUTE_URL, userName, password, payload, BONFIRE_XML);
		
		return unmarshalRetrievedResource(Compute.class, response.getPayload());
	}
	
	@Override
	public Compute suspendCompute(Compute compute) {
		return modifyStateCompute(compute, "suspended");
	}
	
	@Override
	public Compute refreshCompute(Compute compute) {
		Response response = RestClient.executeGetMethod(url + compute.getHref(), userName, password);
		return unmarshalRetrievedResource(Compute.class, response.getPayload());
	}
	
	@Override
	public Compute resumeCompute(Compute compute) {
		return modifyStateCompute(compute, "resume");
	}
	
	private Compute modifyStateCompute(Compute compute, String state) {
		Compute changeState = new Compute();
		changeState.setHref(compute.getHref());
		changeState.setLinks(compute.getLinks());
		changeState.setState(state);
		
		String payload = marshalResource(Compute.class, changeState);
		
		Response response = RestClient.executePutMethod(url + compute.getHref(), payload, userName, password);
		
		return unmarshalRetrievedResource(Compute.class, response.getPayload());
	}
	
	@Override
	public Compute stopCompute(Compute compute) {
		return modifyStateCompute(compute, "stopped");
	}
	
	@Override
	public Compute shutdownCompute(Compute compute) {
		return modifyStateCompute(compute, "shutdown");
	}
	
	@Override
	public Compute cancelCompute(Compute compute) {
		return modifyStateCompute(compute, "cancel");
	}
	@Override
	public Experiment getExperiment(int id) {
		Response response = RestClient.executeGetMethod(url + "/" + EXPERIMENT_URL + "/" + id, userName, password);
		
		// TODO implement throw exception if code is different from 200 (build Java API Exceptions...
		
		return unmarshalRetrievedResource(Experiment.class, response.getPayload());
	}
	
//	@Override
//	public Storage makeStoragePersistent(Storage storage) {
//		Storage persistenStorage = new Storage();
//		persistenStorage.setPersistent("YES");
//		
//		return modifyStorage(storage, persistenStorage);
//	}
//	
//	@Override
//	public Storage unMakeStoragePersistent(Storage storage) {
//		Storage noPersistenStorage = new Storage();
//		noPersistenStorage.setPersistent("NO");
//		
//		return modifyStorage(storage, noPersistenStorage);
//	}
}
