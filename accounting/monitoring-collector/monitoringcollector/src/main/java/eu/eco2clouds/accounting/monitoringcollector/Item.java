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

/**
 * Used to represent metrics items as returned by the Zabbix abstraction API.
 */
public class Item {
	
	/** The name of the metric. */
	String name;
	
	/** The item ID as returned by Zabbix. Does not correspond to the record ID in the accounting database. */
	int zabbix_itemid;
	
	/** The metric timestamp. */
	long clock;
	
	/** The real value of the metric. A double as this type can store any of the number types returned by Zabbix. */
	double value;
	
	/** The unit of the metric. e.g. MB, ms, etc. */
	String unity;
	
	/**
	 * Instantiates a new item.
	 *
	 * @param name the name of the metric
	 * @param zabbix_itemid the item ID as returned by Zabbix. Does not correspond to the record ID in the accounting database
	 * @param clock the metric timestamp
	 * @param value the real value of the metric
	 * @param unity the unit of the metric
	 */
	public Item(String name, int zabbix_itemid, long clock, double value, String unity) {
		this.name = name;
		this.zabbix_itemid = zabbix_itemid;
		this.clock = clock;
		this.value = value;
		this.unity = unity;
	}

	// Auto-generated equality methods. Used to facilitate testing.
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (clock ^ (clock >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((unity == null) ? 0 : unity.hashCode());
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + zabbix_itemid;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (clock != other.clock)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (unity == null) {
			if (other.unity != null)
				return false;
		} else if (!unity.equals(other.unity))
			return false;
		if (Double.doubleToLongBits(value) != Double
				.doubleToLongBits(other.value))
			return false;
		if (zabbix_itemid != other.zabbix_itemid)
			return false;
		return true;
	}
		
}
