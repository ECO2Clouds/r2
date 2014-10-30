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
//	Last Updated Date :		13 Aug 2013
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
 * Used to represent (physical) host database records.

 *
 */
public class PhysicalHost extends Resource {

	/** The locator for the host, its name. For example, "crocket0", "bonfire-blade-1", "vmhost1". */
	public String location;
	
	/** The name of the site/testbed where this host resides. */
	public String site;
	
	/**
	 * Instantiates a new host.
	 *
	 * @param id_metrics_physical_hosts the record ID of this host as it appears in the metrics database.
	 * @param location the locator for the host, its name. For example, "crocket0", "bonfire-blade-1", "vmhost1".
	 * @param site the name of the site/testbed where this host resides.
	 */
	public PhysicalHost(int id_metrics_physical_hosts, String location, String site) {
		this.id = id_metrics_physical_hosts;
		this.location = location;
		this.site = site;
	}
	
}
