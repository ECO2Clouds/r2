package eu.eco2clouds.accounting.datamodel;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
 */
@Entity
@Table(name = "vm_host")
@NamedQueries( { 
@NamedQuery(name = "VMHost.findAll", query = "SELECT p FROM VMHost p"),
@NamedQuery(name = "VMHost.getByVmId", query = "SELECT p FROM VMHost p "
		+ "where p.vm.id=:vmId "
		+ "order by id DESC")})
public class VMHost implements Serializable {
	private static final long serialVersionUID = 3663214599063506821L;
	private int id;
	private long timestamp;
	private VM vm;
	private Host host;
	
	public VMHost() {}
	
	public VMHost(int id, long timestamp) {
		this.setId(id);
		this.setTimestamp(timestamp);
	}

	@Column(name = "timestamp")
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
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

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="host_id")
	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="vm_id")
	public VM getVm() {
		return vm;
	}

	public void setVm(VM vm) {
		this.vm = vm;
	}
}
