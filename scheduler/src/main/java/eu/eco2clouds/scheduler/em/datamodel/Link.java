package eu.eco2clouds.scheduler.em.datamodel;

import static eu.eco2clouds.api.bonfire.occi.Dictionary.NAMESPACE;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 * POJO that represents the link object of the BonFIRE OCCI
 * @author David Garcia Perez - ATOS
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "link")
public class Link {
	@XmlAttribute
	private String rel;
	@XmlAttribute
	private String href;
	
	/**
	 * @return the relative path to this link
	 */
	public String getRel() {
		return rel;
	}
	/**
	 * Sets the relative path for this link
	 * @param rel
	 */
	public void setRel(String rel) {
		this.rel = rel;
	}
	
	/**
	 * @return the URL for this link
	 */
	public String getHref() {
		return href;
	}
	/**
	 * @param href Sets the URL for this link
	 */
	public void setHref(String href) {
		this.href = href;
	}
}
