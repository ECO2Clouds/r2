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
//	Last Updated Date :		03 Aug 2014
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
 * Used to represent experiment database records.
 */
public class Experiment extends Resource {

	/** The location of the aggregator for this experiment. */
	public String aggregator_location;
	
	/** The group of the experiment. */
	public String groupName;
	
	/** The user of the experiment. */
	public String userName;
	
	/** If the experiment is active. */
	public boolean active; 
	
	/**
	 * Instantiates a new experiment.
	 *
	 * @param id_metrics_experiments the numerical BonFIRE id for this experiment. Additionally corresponds to the record id in the accounting database.
	 * @param aggregator_location the location of the aggregator for this experiment.
	 */
	public Experiment(int id_metrics_experiments, String aggregator_location) {
		this(id_metrics_experiments, aggregator_location, "eco2clouds");
	}
	
	/**
	 * Instantiates a new experiment.
	 *
	 * @param id_metrics_experiments the numerical BonFIRE id for this experiment. Additionally corresponds to the record id in the accounting database.
	 * @param aggregator_location the location of the aggregator for this experiment.
	 * @param group the group name of the experiment
	 */
	public Experiment(int id_metrics_experiments, String aggregator_location, String groupName) {
		this(id_metrics_experiments, aggregator_location, groupName, "");
	}
	
	/**
	 * Instantiates a new experiment.
	 *
	 * @param id_metrics_experiments the numerical BonFIRE id for this experiment. Additionally corresponds to the record id in the accounting database.
	 * @param aggregator_location the location of the aggregator for this experiment.
	 * @param group the group name of the experiment
	 * @param user the user name of the experiment
	 */
	public Experiment(int id_metrics_experiments, String aggregator_location, String groupName, String userName) {
		this(id_metrics_experiments, aggregator_location, groupName, userName, true);
	}
	
	/**
	 * Instantiates a new experiment.
	 *
	 * @param id_metrics_experiments the numerical BonFIRE id for this experiment. Additionally corresponds to the record id in the accounting database.
	 * @param aggregator_location the location of the aggregator for this experiment.
	 * @param group the group name of the experiment
	 * @param user the user name of the experiment
	 * @param active the current status of the experiment
	 */
	public Experiment(int id_metrics_experiments, String aggregator_location, String groupName, String userName, boolean active) {
		this.id = id_metrics_experiments;
		this.aggregator_location = aggregator_location;
		this.groupName = groupName;
		this.userName = userName;
		this.active = active;
	}
}
