package eu.eco2clouds.scheduler.response;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.apache.commons.httpclient.Header;

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
public class RestResponse {
	private int code;
	private String text;
	private Header[] headers;
	private String body;

	/**
	 * @return returned HTTP code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @return status text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @return headers of the response
	 */
	public Header[] getHeaders() {
		return headers;
	}

	/**
	 * @return Body of the response
	 */
	public String getBody() {
		return body;
	}

	public void setCode(int statusCode) {
		this.code = statusCode;
	}

	public void setStatus(String statusText) {
		this.text = statusText;
	}

	public void setHeaders(Header[] responseHeaders) {
		this.headers = responseHeaders;
	}

	public void setBody(String responseBody) {
		this.body = responseBody;
	}

	@Override
	public String toString() {
		return " code " + code + "\n" + "text " + text;
	}

	public Response toJaxRSResponse() {
		ResponseBuilder builder = Response.status(this.getCode());
		Header[] headers = this.getHeaders();
		if (headers != null)
			for (Header h : headers) {
				builder.header(h.getName(), h.getValue());
			}
		builder.entity(this.getBody());
		return builder.build();
	}
}
