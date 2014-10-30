////////////////////////////////////////////////////////////////////////
//
// Copyright (c) The University of Edinburgh, 2013
//
// This software may not be used, sold, licensed, transferred, copied
// or reproduced in whole or in part in any manner or form or in or
// on any media by any person other than in accordance with the terms
// of the Licence Agreement supplied with the software, or otherwise
// without the prior written consent of the copyright owners.
//
// This software is distributed WITHOUT ANY WARRANTY, without even the
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
// PURPOSE, except where stated in the Licence Agreement supplied with
// the software.
//
//	Created By :			Dominic Sloan-Murphy, Iakovos Panourgias
//	Last Updated Date :		23 Aug 2013
//	Created for Project :	ECO2Clouds
//
////////////////////////////////////////////////////////////////////////
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////

package eu.eco2clouds.accounting.monitoringcollector;

import java.net.URI;

/**
 * Represents the object structure of a message queue event. Events are de-serialised from JSON into instances of this type.
 */
public class MMQEvent {
	
	private long timestamp;
	private String eventType;
	private String objectType;
	private URI objectId;
	private String source;
	private String groupId;
	private String userId;
	private ObjectData objectData;
	
	public class ObjectData {
		private String name;
		private String host;
		private int duration;
		private URI experimentId;
		
		// AUTO GENERATED GETTERS AND SETTERS
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}
		
		public int getDuration() {
			return duration;
		}
		
		public void setDuration(int duration) {
			this.duration = duration;
		}
		
		public URI getExperimentId() {
			return experimentId;
		}
		
		public void setExperimentId(URI experimentId) {
			this.experimentId = experimentId;
		}
	}
	
	// AUTO GENERATED GETTERS AND SETTERS
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public URI getObjectId() {
		return objectId;
	}

	public void setObjectId(URI objectId) {
		this.objectId = objectId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public ObjectData getObjectData() {
		return objectData;
	}

	public void setObjectData(ObjectData objectData) {
		this.objectData = objectData;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}