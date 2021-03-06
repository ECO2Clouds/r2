package eu.eco2clouds.api.bonfire.client.rest;


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
 * Simple POJO to encapsulate the response.
 *
 */
public class Response {
	private int statusCode;
	private String payload;
	
	public Response() {}
	
	public Response(int statusCode, String payload) {
		this.statusCode = statusCode;
		this.payload = payload;
	}
	
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public int getStatusCode() {
		return statusCode;
	}
	
	public void setPayload(String payload) {
		this.payload = payload;
	}
	public String getPayload() {
		return payload;
	}
}
