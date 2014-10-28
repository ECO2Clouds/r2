package eu.eco2clouds.api.bonfire.client.exceptions;

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
 * This exception is typically throw when the API is forced to retrieve a resource that has not an HREF value set
 * Typically this means that the resource has not been read from the BonFIRE API.
 * It was an object just created inside the program.
 *
 */
public class NoHrefSetException extends Exception {
	private static final long serialVersionUID = -801056647435054440L;
	
	public NoHrefSetException() { 
		super(); 
	}
	
	public NoHrefSetException(String message) { 
		super(message); 
	}
	
	public NoHrefSetException(String message, Throwable cause) { 
		super(message, cause); 
	}
	
	public NoHrefSetException(Throwable cause) { 
		super(cause); 
	}
}
