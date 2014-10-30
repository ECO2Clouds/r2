package eu.eco2clouds.accounting.datamodel;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "action_type")
@NamedQueries( { 
@NamedQuery(name = "ActionType.findAll", query = "SELECT p FROM ActionType p"),
@NamedQuery(name = "ActionType.findActionTypeByName", query = "SELECT p FROM ActionType p where p.name = :name"),
@NamedQuery(name = "ActionType.findActionTypeById", query = "SELECT p FROM ActionType p where p.id = :id")})
public class ActionType implements Serializable {
	private static final long serialVersionUID = -8531943830530811766L;
	private int id;
	private String name;

	public ActionType() {	}

	public ActionType(int id, String name) {
		this.id = id;
		this.name = name;
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

	
	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}