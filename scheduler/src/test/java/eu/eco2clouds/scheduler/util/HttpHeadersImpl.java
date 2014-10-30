package eu.eco2clouds.scheduler.util;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

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
public class HttpHeadersImpl implements HttpHeaders {
	private MultivaluedMap<String, String> map;

	@Override
	public List<Locale> getAcceptableLanguages() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MediaType> getAcceptableMediaTypes() {
		return null;
	}

	@Override
	public Map<String, Cookie> getCookies() {
		return null;
	}

	@Override
	public Locale getLanguage() {
		return null;
	}

	@Override
	public MediaType getMediaType() {
		return null;
	}

	@Override
	public List<String> getRequestHeader(String arg0) {
		return null;
	}
	
	public void setRequestHeaders(MultivaluedMap<String, String> map) {
		this.map = map;
	}

	@Override
	public MultivaluedMap<String, String> getRequestHeaders() {
		return map;
	}
}
