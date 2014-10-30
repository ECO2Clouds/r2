package eu.eco2clouds.accounting.datamodel;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
 * POJO that represents a testbed object
 */
@Entity
@Table(name = "testbed")
@NamedQueries( { 
@NamedQuery(name = "Testbed.findAll", query = "SELECT p FROM Testbed p"),
@NamedQuery(name = "Testbed.findTestbedByName", query = "SELECT p FROM Testbed p where p.name=:name")})
public class Testbed implements Serializable {
	private static final long serialVersionUID = -8755646089534968047L;
	private int id;
	private String name;
	private String url;
	private List<Host> hosts;
	
	public Testbed() {}

	public Testbed(int id, String name, String url) {
		this.id = id;
		this.name = name;
		this.url = url;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * @return the url of the testbed
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * Sets the url of the testbed to get the status data
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * @return the BonFIRE name of the testbed
	 */
	public String getName() {
		return name;
	}
	/**
	 * Sets the assigned name of the Testbed in BonFIRE
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "testbed_id", referencedColumnName="id", nullable = true)
	public List<Host> getHosts() {
		return hosts;
	}

	public void setHosts(List<Host> hosts) {
		this.hosts = hosts;
	}
}
