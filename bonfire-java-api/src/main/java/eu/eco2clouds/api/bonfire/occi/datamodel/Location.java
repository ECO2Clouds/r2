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
 * POJO representing the Location BonFIRE OCCI datamodel
 * 
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "location", namespace = NAMESPACE)
public class Location {
	@XmlAttribute
	private String href;
	@XmlElement(namespace = NAMESPACE)
	private String name;
	@XmlElement(namespace = NAMESPACE)
	private String url;
	@XmlElement(name="link", namespace = NAMESPACE)
	private ArrayList<Link> links;
	
	/**
	 * @return the BonFIRE relative url for this Location
	 */
	public String getHref() {
		return href;
	}
	
	/**
	 * Sets the BonFIRE relative url for this Location
	 * @param href relative URL
	 */
	public void setHref(String href) {
		this.href = href;
	}
	
	/**
	 * @return the name of the Location
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of this specific BonFIRE location
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the URL of the OCCI server or API server to interact with this specific BonfIRE Location
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * Sets the URL of the OCCI server or API server to interact with this specific BonfIRE Location.
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return returns the relative occi set of links for this location
	 */
	public ArrayList<Link> getLinks() {
		return links;
	}
	/**
	 * Sets the relative occi set of links for this location
	 * @param links
	 */
	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}
}
