package eu.eco2clouds.scheduler.http;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.log4j.Logger;

import eu.eco2clouds.scheduler.accounting.client.ssl.AuthSSLProtocolSocketFactory;
import eu.eco2clouds.scheduler.conf.Configuration;

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
 */
public class HCClientFactory {
	private static Logger logger = Logger.getLogger(HCClientFactory.class);

	public static HttpClient getHttpClient() {
		try {
			Protocol authhttps = new Protocol("https",  
		          new AuthSSLProtocolSocketFactory(
		              new URL("file:" + Configuration.keystore), Configuration.keystoreKey,
		              new URL("file:" + Configuration.keystore), Configuration.keystoreKey), 443); 
			Protocol.registerProtocol("https", authhttps);
		} catch(MalformedURLException e) {
			logger.info("Wrong path for keystore file: " + e);
		}
		return new HttpClient();
	}
}
