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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
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
 * A POJO Object that stores all the information from a VM
 */

@Entity
@Table(name = "vm")
@NamedQueries( { 
@NamedQuery(name = "VM.findAll", query = "SELECT p FROM VM p")})
public class VM implements Serializable {
	private static final long serialVersionUID = 8780591888940069411L;
	private int id;
	private String bonfireUrl;
	private String ip;
	private List<Action> actions;
	private Set<VMHost> vMhosts;

	public VM() {}

	public VM(int id, String bonfireUrl) {
		this.id = id;
		this.bonfireUrl = bonfireUrl;
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

	@Column(name = "bonfire_url")
	public String getBonfireUrl() {
		return bonfireUrl;
	}

	public void setBonfireUrl(String bonfireUrl) {
		this.bonfireUrl = bonfireUrl;
	}
	
	@Column(name = "ip")
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "vm_id", referencedColumnName="id", nullable = true)
	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "vm_id", referencedColumnName="id", nullable = true)
	public Set<VMHost> getvMhosts() {
		return vMhosts;
	}

	public void setvMhosts(Set<VMHost> vMhosts) {
		this.vMhosts = vMhosts;
	}

}
