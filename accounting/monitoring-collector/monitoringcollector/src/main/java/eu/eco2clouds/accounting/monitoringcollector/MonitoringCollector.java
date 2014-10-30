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
//	Last Updated Date :		12 Aug 2013
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
 * A concrete implementation of an AbstractMonitoringCollector. Handles the specifics of the monitoring for this project. 
 */
public class MonitoringCollector extends AbstractMonitoringCollector {
	
	/**
	 * Instantiates a new monitoring collector.
	 * Loads the associated .properties file and performs an initial population of the accounting database with host and site information before monitoring is started.
	 * Initial population is performed to enable ECO2Clouds experiment virtual machines to be correctly associated with a physical host and site by the monitor.
	 */
	public MonitoringCollector() {
		MessageHandler.print("Loading Monitoring Collector properties file...");
		ConfigurationValues.loadProperties("MonitoringCollector.properties");
		
		creatingCollector();
	}
	
	/**
	 * Instantiates a new monitoring collector.
	 * Loads the associated .properties file and performs an initial population of the accounting database with host and site information before monitoring is started.
	 * Initial population is performed to enable ECO2Clouds experiment virtual machines to be correctly associated with a physical host and site by the monitor.
	 * @param configurationFile Loads the configuration file... 
	 */
	public MonitoringCollector(String configurationFile) {
		MessageHandler.print("Loading Monitoring Collector properties file...");
		ConfigurationValues.loadProperties(configurationFile);
		
		creatingCollector();
	}
	
	private void creatingCollector() {
		this.collector = new MetricsCollector();
		MessageHandler.print("Performing initial population of the accounting database with host and site info....");
		((MetricsCollector)collector).updateDatabaseInfrastructureStatus();
		
		this.monitor = new MMQMonitor();
	}
}
