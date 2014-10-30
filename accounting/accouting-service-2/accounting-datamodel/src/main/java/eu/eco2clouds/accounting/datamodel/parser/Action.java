package eu.eco2clouds.accounting.datamodel.parser;

import static eu.eco2clouds.accounting.Dictionary.E2C_NAMESPACE;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.eco2clouds.accounting.datamodel.parser.ActionType;
import eu.eco2clouds.accounting.datamodel.parser.HostData;

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
 * A POJO Object that stores all the information from a Action
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="action", namespace = E2C_NAMESPACE)
public class Action  {

	
	@XmlElement(name="id", namespace = E2C_NAMESPACE)
	private int id;
	
	@XmlElement(name="vm_id", namespace = E2C_NAMESPACE)
	private int vm_id;
	
	@XmlElement(name="timestamp", namespace = E2C_NAMESPACE)
	private long timestamp;
	
	@XmlElement(name="actionlog", namespace = E2C_NAMESPACE)
	private String actionLog;
	
	@XmlElement(name="action_type", namespace = E2C_NAMESPACE)
	private ActionType actionType;
	
	@XmlElement(name="host_data", namespace = E2C_NAMESPACE)
	private Set<HostData> hostDatas;

	public Action() {}

	public Action(int id, long timestamp, String actionLog) {
		this.id = id;
		this.timestamp = timestamp;
		this.actionLog = actionLog;
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getActionLog() {
		return actionLog;
	}

	public void setActionLog(String actionLog) {
		this.actionLog = actionLog;
	}


	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}


	public Set<HostData> getHostDatas() {
		return hostDatas;
	}

	public void setHostDatas(Set<HostData> hostDatas) {
		this.hostDatas = hostDatas;
	}

	
	public int getVm_id() {
		return vm_id;
	}

	public void setVm_id(int vm_id) {
		this.vm_id = vm_id;
	}
	
}
