package eu.eco2clouds.accounting.rest.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


import eu.eco2clouds.accounting.datamodel.ActionType;
import eu.eco2clouds.accounting.service.ActionTypeDAO;

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

public class AccountingServiceActionType extends AccountingServiceAbstractImpl {

	public AccountingServiceActionType(ActionTypeDAO actionTypeDAO) {

		this.actionTypeDAO = actionTypeDAO;
	}

	public boolean doesElementExist(int id) {

		if (this.actionTypeDAO.getById(id) != null)
			return true;
		else
			return false;
	}

	public Response service(String verb, int id, HttpHeaders hh, String payload)
			throws IOException, JAXBException {

		String xmlResponse = null;

		if (verb == "GET") {
			if (id != -1) {

				if (!doesElementExist(id))
					return buildResponse(
							404,
							"ActionType "
									+ id
									+ " does not exist in the Accounting Service Scheduler Database");
			}

			xmlResponse = new AccountingServiceActionType(this.actionTypeDAO)
					.printInformation(id, null, null);

		} else if (verb == "POST") {
			if (id != -1) {

				if (!doesElementExist(id))
					return buildResponse(
							404,
							"ActionType "
									+ id
									+ " does not exist in the Accounting Service Scheduler Database");
			}

		xmlResponse = new AccountingServiceActionType(this.actionTypeDAO).addActionType(payload);

		}else if (verb == "PUT") {
			if (id != -1) {

				if (!doesElementExist(id))
					return buildResponse(
							404,
							"ActionType "
									+ id
									+ " does not exist in the Accounting Service Scheduler Database");
			}

			xmlResponse = new AccountingServiceActionType(this.actionTypeDAO).updateActionType(payload);

		}

		
		if (xmlResponse == null) {
			if  (verb == "GET") return buildResponse(403,"Not authorized to access to the Action_Type information"); 
			else  return buildResponse(500, "Not inserted Action_Type information"); 
		 } else 
			if (verb == "POST") {
			return buildResponse(202, xmlResponse);
		}
		return buildResponse(200, xmlResponse);

	}

	public StringBuilder printElement(ActionType actionType) {

		StringBuilder xmlResponse = new StringBuilder();

		xmlResponse.append("<id>" + actionType.getId() + "</id>\n");
		xmlResponse.append("<name>" + actionType.getName() + "</name>\n");
		xmlResponse.append("<link rel=\"parent\" href=\"/action_types\"/>\n");
		xmlResponse.append("</action_type>\n");

		return xmlResponse;
	}

	public String printInformation(int id, List<String> groups, String field) {

		List<ActionType> actionTypes = new ArrayList<ActionType>();
		ActionType actionType = null;
		StringBuilder xmlResponse = new StringBuilder();

		
		xmlResponse.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

		if (id == -1) {

			actionTypes = this.actionTypeDAO.getAll();

			/** @Path("/action_types/") */

			xmlResponse
					.append("<collection xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/occi\" href=\"/action_types\">\n");
			xmlResponse.append("<items offset=\"0\" total=\""
					+ actionTypes.size() + "\">\n");

			for (int i = 0; i < actionTypes.size(); i++) {
				xmlResponse.append("<action_type href=\"/action_types/"
						+ actionTypes.get(i).getId() + "\">\n");
				xmlResponse.append(printElement(actionTypes.get(i)));
			}
			xmlResponse.append("</items>\n");
			xmlResponse
					.append("<link href=\"/\" rel=\"parent\" type=\"application/vnd.eco2clouds+xml\"/>\n");
			xmlResponse.append("</collection>\n");
		} else {

			/** @Path("/experiments/{id ") */

			actionType = this.actionTypeDAO.getById(id);

			if (actionType == null)
				return null;
			else {
				xmlResponse
						.append("<action_type xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/occi\" href=\"/action_types/"
								+ actionType.getId() + "\">\n");
				xmlResponse.append(printElement(actionType));
			}

		}
		return xmlResponse.toString();

	}

	public String addActionType(String payload) throws IOException,
			JAXBException {

		String actionTypeString = null;
		int result = 0;
		JAXBContext jaxbContext = JAXBContext.newInstance(eu.eco2clouds.accounting.datamodel.parser.ActionType.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		eu.eco2clouds.accounting.datamodel.parser.ActionType actionTypeParser = (eu.eco2clouds.accounting.datamodel.parser.ActionType) jaxbUnmarshaller
				.unmarshal(new StringReader(payload));
						
		ActionType actionType = new ActionType();
		ActionType actionTypeOut = new ActionType();
		actionType.setName(actionTypeParser.getName());
		
		
		actionTypeOut = this.actionTypeDAO.getByName(actionTypeParser.getName());
		
		if ( actionTypeOut == null ) {
			
			//Insert actionType in the case it doesn't exist in database
			
			result = this.actionTypeDAO.addActionType(actionType);
			
			if (result == 1) {
				
				//If element has been inserted, it is printed
				actionTypeOut = this.actionTypeDAO.getByName(actionTypeParser.getName());
			actionTypeString = new AccountingServiceActionType(this.actionTypeDAO).printInformation(actionTypeOut.getId(), null, null);
				
				
			}
		}
		
		return actionTypeString;			

	}
	

	public String updateActionType(String payload) throws IOException,
			JAXBException {

		String actionTypeString = null;
		int result = 0;
		JAXBContext jaxbContext = JAXBContext.newInstance(eu.eco2clouds.accounting.datamodel.parser.ActionType.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		eu.eco2clouds.accounting.datamodel.parser.ActionType actionTypeParser = (eu.eco2clouds.accounting.datamodel.parser.ActionType) jaxbUnmarshaller
				.unmarshal(new StringReader(payload));
						
		ActionType actionTypeOut = new ActionType();
		
		if (actionTypeParser.getId() != null) {
			
			ActionType actionType = new ActionType(actionTypeParser.getId(),actionTypeParser.getName());
					
		
			actionTypeOut = this.actionTypeDAO.getById(actionTypeParser.getId());
			
					
			if ( actionTypeOut != null ) {
			
			
				//Update actionType in the case it exists in database
				
				result = this.actionTypeDAO.updateActionType(actionType);
				
				if (result == 1) {
					
					//If element has been updated, it is printed
					actionTypeOut = this.actionTypeDAO.getById(actionTypeParser.getId());
				actionTypeString = new AccountingServiceActionType(this.actionTypeDAO).printInformation(actionTypeOut.getId(), null, null);
					
				}
			}
		}
		
		return actionTypeString;
				

	}
	
	
}
