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
//	Last Updated Date :		03 July 2014
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
 * Used to represent virtual machine database records.
 */
public class VirtualMachine extends Resource {

	/** The location of the virtual machine e.g. /locations/uk-epcc/computes/990 */
	public String location;
	
	/** The location of the aggregator gathering the metrics on this virtual machine. */
	public String aggregator_location;
	
	public String site;
	
	public long start_time;
	
	public long end_time;
	
	public String Physical_Host_location;
	
	public int experimentId;
	
	/**
	 * Instantiates a new virtual machine.
	 *
	 * @param id_metrics_virtual_machines the record ID of this virtual machine as it appears in the metrics database.
	 * @param location the location of the virtual machine e.g. /locations/uk-epcc/computes/990
	 * @param aggregator_location the location of the aggregator gathering the metrics on this virtual machine.
	 */
	public VirtualMachine(int id_metrics_virtual_machines, String location, String aggregator_location) {
		this(id_metrics_virtual_machines, location, aggregator_location, "", -1L, -1L, -1);
	}
	
	public VirtualMachine(int id_metrics_virtual_machines, String location, String aggregator_location, String physicalHost, long starttime, long endtime, int experimentId) {
		this.id = id_metrics_virtual_machines;
		this.location = location;
		this.aggregator_location = aggregator_location;
		this.Physical_Host_location = physicalHost;
		this.start_time = starttime;
		this.end_time = endtime;
		String[] sites = location.split("\\s*/\\s*");
		for ( String temp : sites)
		{
			if ( temp.length() > 4)
				if ( Character.toString(temp.charAt(2)).equals("-") )
				{
					location = temp;
				}
		}
		this.site = location;
		this.experimentId = experimentId;
	}
}
