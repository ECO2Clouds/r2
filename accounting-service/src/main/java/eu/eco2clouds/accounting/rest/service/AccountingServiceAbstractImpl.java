package eu.eco2clouds.accounting.rest.service;

import static eu.eco2clouds.accounting.Dictionary.VERSION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.springframework.beans.factory.annotation.Autowired;

import eu.eco2clouds.accounting.service.ActionDAO;
import eu.eco2clouds.accounting.service.ActionTypeDAO;
import eu.eco2clouds.accounting.service.ExperimentDAO;
import eu.eco2clouds.accounting.service.HostDAO;
import eu.eco2clouds.accounting.service.HostDataDAO;
import eu.eco2clouds.accounting.service.TestbedDAO;
import eu.eco2clouds.accounting.service.VMDAO;
import eu.eco2clouds.accounting.service.VMHostDAO;

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
public abstract class AccountingServiceAbstractImpl implements AccountingService {
	
	@Autowired
	public TestbedDAO testbedDAO;
	@Autowired
	public HostDAO hostDAO;
	@Autowired
	public HostDataDAO hostDataDAO;
	@Autowired
	public ExperimentDAO experimentDAO;
	@Autowired
	public ActionDAO actionDAO;	
	@Autowired
	public ActionTypeDAO actionTypeDAO;	
	@Autowired
	public VMDAO vmDAO;
	@Autowired
	public VMHostDAO vmHostDAO;
	
	//public abstract Response service (int id, HttpHeaders hh, String field);
	
	public Response buildResponse(int code, String payload) {
		ResponseBuilder builder = Response.status(code);
		builder.entity(payload);

		return builder.build();
	}
	
	public List<String> getGroupHeader(HttpHeaders hh) {

		List<String> groups = new ArrayList<String>();
		String headers = null;

		if (hh != null) {
			MultivaluedMap<String, String> httpRequestHeadersMap = hh
					.getRequestHeaders();
			Set<String> keys = httpRequestHeadersMap.keySet();
			for (String key : keys) {

				String keyparsed = key.toLowerCase();
				
				if (keyparsed.equals("x_bonfire_asserted_groups")
					|| keyparsed.equals("x-bonfire-asserted-groups")
					|| keyparsed.equals("x-bonfire-groups-id")
					|| keyparsed.equals("x_bonfire_groups_id")) {
					
					headers = httpRequestHeadersMap.get(key).get(0);
					groups= Arrays.asList(headers.split("\\s*,\\s*"));
				}
			}
		}
		return groups;
	}
	
	public String getList () {
		
		Date date = new Date();

		String xmlResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<root xmlns=\"http://accounting.eco2clouds.eu/doc/schemas/xml\" href=\"/\">\n"
				+ "<version>"
				+ VERSION
				+ "</version>\n"
				+ "<timestamp>"
				+ date.getTime()
				+ "</timestamp>\n"
				+ "<link rel=\"testbeds\" href=\"/testbeds\" type=\"application/vnd.eco2clouds+xml\"/>\n"
				+ "</root>";

		return xmlResponse;
	}
	

}
