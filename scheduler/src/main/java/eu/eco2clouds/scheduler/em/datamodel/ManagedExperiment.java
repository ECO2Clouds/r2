package eu.eco2clouds.scheduler.em.datamodel;

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
 * POJO that represents the managed_experiment object of the BonFIRE OCCI
 * @author David Garcia Perez - ATOS
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "managed_experiment")
public class ManagedExperiment {
	@XmlAttribute
	private String href;
	@XmlElement
	private String name;
	@XmlElement
	private String description;
	@XmlElement
	private String status;
	@XmlElement(name="link")
	private ArrayList<Link> links;
	
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	
	public ArrayList<Link> getLinks() {
		return links;
	}
	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Returns the BonFIRE experiment ID of a DEPLOYED Experiment
	 * @return the BonFIRE experiment ID or -1 if the Experiment is not DEPLOYED or
	 *         is any error in the parsing of it.
	 */
	public int getBonFIREExperimentId() {
		int experimentId = -1;
		
		if(status.equals("DEPLOYED") || status.equals("RUNNING")) {
			for(Link link : links) {
				if(link.getRel().equals("experiment")) {
					try {
						String[] urlPortions = link.getHref().split("/");
						experimentId = Integer.parseInt(urlPortions[4]);
					} catch(Exception e) {
						experimentId = -1;
					}
				}
			}
		}
		
		return experimentId;
	}
	
	/**
	 * @return the managed experiment id
	 */
	public int getManagedExperimentId() {
		int managedExperimentId = -1;
		
		if(href != null) {
			String[] urlPortions = href.split("/");
			managedExperimentId = Integer.parseInt(urlPortions[2]);
		}
		
		return managedExperimentId;
	}
}
