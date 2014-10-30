package eu.eco2clouds.accounting.datamodel;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
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
 */

@Entity
@Table(name = "action")
@NamedQueries( { 
@NamedQuery(name = "Action.findAll", query = "SELECT p FROM Action p")})



//@NamedQuery(name = "Action.findActionByVmId", query = "SELECT p FROM ActionType p where :vmId in (SELECT id FROM p.actionType )")



public class Action implements Serializable {
	private static final long serialVersionUID = -8712872385957386182L;
	private int id;
	private long timestamp;
	private String actionLog;
	private ActionType actionType;
	private Set<HostData> hostDatas;
	

	public Action() {}

	public Action(int id, long timestamp, String actionLog) {
		this.id = id;
		this.timestamp = timestamp;
		this.actionLog = actionLog;
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

	@Column(name = "timestamp")
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Column(name = "action_log", length=900000)
	public String getActionLog() {
		return actionLog;
	}

	public void setActionLog(String actionLog) {
		this.actionLog = actionLog;
	}

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="action_type_id")
	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "action_id", referencedColumnName="id", nullable = true)
	public Set<HostData> getHostDatas() {
		return hostDatas;
	}

	public void setHostDatas(Set<HostData> hostDatas) {
		this.hostDatas = hostDatas;
	}	
}
