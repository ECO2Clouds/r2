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
//	Last Updated Date :		21 July 2014
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * The Class ConfigurationValues. A single class which contains static configuration data referenced throughout the project, for example the hostname of the Management Message Queue.
 * Values can be user configured through editing a MonitoringCollector.properties file.
 */
public final class ConfigurationValues {

	// ---MonitoringCollector---
	/** The username for accessing the Management Message Queue. */
	public static String mmqUsername;
	
	/** The password for accessing the Management Message Queue. */
	public static String mmqPassword;
	
	/** The host location of the Management Message Queue. */
	public static String mmqHost;
	
	/** The topics to subscribe to on the the Management Message Queue. */
	public static String[] mmqTopics = new String[]{"res-mng.experiment.create",				// Experiment creation (start monitoring)
											 		"res-mng.experiment.state.terminated",		// Experiment termination (stop monitoring)
											 		"res-mng.compute.create",					// Compute creation (aggregator or VM resource)
													"#.compute.state.stopped.done"};			// Compute terminal state (no transitions from DONE)
		   
													/* The following topics are not required for this iteration. VM state transitions not being monitored.
		   									 		"res-mng.compute.state.stopped",			// Compute stopped state (paused waiting for resume)
		   									 		"res-mng.compute.state.suspended",			// Compute suspended state (paused waiting for resume from same host)
		   									 		"res-mng.compute.state.resume"};			// Compute transition (STOPPED|SUSPENDED) --> RUNNING
													 */
	
	// ---MonitorDatabaseConnector---
	/** The username for accessing the MySQL accounting database. */
	public static String mysqlUsername;
	
	/** The password for accessing the MySQL accounting database. */
	public static String mysqlPassword;
	
	/** The host location of the MySQL accounting database. */
	public static String mysqlHost;
	
	// ---MetricsCollector---
	/** The polling rate at which the collector calls the BonFIRE API and updates metrics. Units are milliseconds. */
	public static long pollingRate;
	
	/** The host location of the BonFIRE API. */
	public static String bonfireAPI;
	
	/** The username this client asserts when accessing the BonFIRE API. */
	public static String apiUser;
	
	/** The path for the Java keystore file containing combined client/server certificates and this client's private key for mutual client-server authentication to the BonFIRE API. */
	public static String keyStorePath;
	
	/** The password for accessing the certificate keystore. */
	public static String keyStorePassword;
	
	/** Information regarding the BonFIRE sites and their associated OpenNebula status pages. Must be set by the user to include all BonFIRE testbed sites to ensure site and physical host metrics are successfully gathered.  */
	public static String[][] hostInfo = new String[][]{ {"uk-epcc", "http://crockett.epcc.ed.ac.uk/one-status.xml"},
														{"fr-inria", "http://frontend.integration.bonfire.grid5000.fr/one-status.xml"} }; 
	
	// ---MessageHandler---
	/** Setting to control logging detail. On false, suppresses all messages not sent to stderr. */
	public static MessageHandler.Level loggingLevel =  MessageHandler.Level.DEBUG;
    
	/** The user agent used for identifying the Monitoring Collector to the BonFIRE API. Follows format at http://wiki.bonfire-project.eu/index.php/Provenance_record */
	public static String userAgent = "eco2clouds-accounting-monitoring-collector/0.1";
		
	/** The number of first level threads. */
	public static int firstLevelOfThreads = 4;
	
	/** The number of second level threads. */
	public static int secondLevelOfThreads = 2;
		
	/** The High Priority Physical Host Metrics. */
	public static List<String> highPriorityMetricsPH = new LinkedList<String>();
	
	/** The Medium Priority Physical Host Metrics. */
	public static List<String> mediumPriorityMetricsPH = new LinkedList<String>();
	
	/** The Low Priority Physical Host Metrics. */
	public static List<String> lowPriorityMetricsPH = new LinkedList<String>();
	
	/** The High Priority Virtual Machine Metrics. */
	public static List<String> highPriorityMetricsVM = new LinkedList<String>();
	
	/** The Medium Priority Virtual Machine Metrics. */
	public static List<String> mediumPriorityMetricsVM = new LinkedList<String>();
	
	/** The Low Priority Virtual Machine Metrics. */
	public static List<String> lowPriorityMetricsVM = new LinkedList<String>();
	
	/** The Medium Priority Physical Host Interval. */
	public static int mediumPriorityPHInterval = 600000;
	
	/** The Low Priority Physical Host Interval. */
	public static int lowPriorityPHInterval = 1800000;
			
	/** The Medium Priority Virtual Machine Interval. */
	public static int mediumPriorityVMInterval = 600000;
			
	/** The Medium Priority Virtual Machine Interval. */
	public static int lowPriorityVMInterval = 1800000; 
		
	/**
	 * Load properties from a file. Uses the hard-coded default if a value is not specified.
	 *
	 * @param filepath the location of the properties file to load from.
	 */
	public static void loadProperties(String filepath) {
		
		MessageHandler.print("Loading configuration " + filepath);
		
		PropertiesConfiguration config = null;
		try {
			config = new PropertiesConfiguration(filepath);			
		} catch (ConfigurationException e1) {
			MessageHandler.error("Error attempting to load properties file: " + filepath + ". Exception: " + e1.getMessage() + " . Using default values.");
			return;
		}
		
		fromProperties(config);
	}
	
	/**
	 * Loads in values from the supplied Properties object. Uses the hard-coded default if a value is not specified.
	 *
	 * @param config the properties object to load from.
	 */
	private static void fromProperties(PropertiesConfiguration config) {

		// Ensures any attempted gets for a missing property will throw a NoSuchElementException
		config.setThrowExceptionOnMissing(true);
		
		// Set default values
		highPriorityMetricsPH.add("memfree");
		highPriorityMetricsPH.add("runningvm");
		highPriorityMetricsPH.add("freespacesrv");
		highPriorityMetricsPH.add("Availability");
		highPriorityMetricsPH.add("IOPS");
		highPriorityMetricsPH.add("cpuUtilization");
		highPriorityMetricsPH.add("PowerConsumption");
		lowPriorityMetricsPH.add("procnum");
		lowPriorityMetricsPH.add("cpuload");
		lowPriorityMetricsPH.add("cpuutil");
		lowPriorityMetricsPH.add("memtotal");
		lowPriorityMetricsPH.add("swapfree");
		lowPriorityMetricsPH.add("co2g");
		lowPriorityMetricsPH.add("conswh");
		lowPriorityMetricsPH.add("consva");
		lowPriorityMetricsPH.add("consw");
		
		highPriorityMetricsVM.add("cpuutil");
		highPriorityMetricsVM.add("ioutil");
		highPriorityMetricsVM.add("memfree");
		highPriorityMetricsVM.add("netifin");
		highPriorityMetricsVM.add("netifout");
		highPriorityMetricsVM.add("diskfree");
		highPriorityMetricsVM.add("power");
		highPriorityMetricsVM.add("iops");
		lowPriorityMetricsVM.add("procnum");
		lowPriorityMetricsVM.add("cpuload");
		lowPriorityMetricsVM.add("memused");
		lowPriorityMetricsVM.add("memtotal");
		lowPriorityMetricsVM.add("swapfree");
		lowPriorityMetricsVM.add("swaptotal");
		lowPriorityMetricsVM.add("disktotal");
		lowPriorityMetricsVM.add("diskusage");
		
		// ---MonitoringCollector---
		mmqUsername = setString("mmq.username", config, mmqUsername);		
		mmqPassword = setString("mmq.password", config, mmqPassword);
		mmqHost = setString("mmq.host", config, mmqHost);
		mmqTopics = setStringArray("mmq.topics", config, mmqTopics);
		firstLevelOfThreads = setInt("firstLevelOfThreads", config,firstLevelOfThreads);
		secondLevelOfThreads = setInt("secondLevelOfThreads", config,secondLevelOfThreads);
		mediumPriorityPHInterval = setInt("mediumPriorityPHInterval", config,mediumPriorityPHInterval);
		lowPriorityPHInterval = setInt("lowPriorityPHInterval", config,lowPriorityPHInterval);
		mediumPriorityVMInterval = setInt("mediumPriorityVMInterval", config,mediumPriorityVMInterval);
		lowPriorityVMInterval = setInt("lowPriorityVMInterval", config,lowPriorityVMInterval);
		
		String[] temp = highPriorityMetricsPH.toArray(new String[highPriorityMetricsPH.size()]);
		highPriorityMetricsPH.clear();
		temp = setStringArray("highPriorityMetricsPH", config, temp);
		for (String metric : temp)
			highPriorityMetricsPH.add(metric);
		
		temp = mediumPriorityMetricsPH.toArray(new String[mediumPriorityMetricsPH.size()]);
		mediumPriorityMetricsPH.clear();
		temp = setStringArray("mediumPriorityMetricsPH", config, temp);
		for (String metric : temp)
			mediumPriorityMetricsPH.add(metric);

		temp = lowPriorityMetricsPH.toArray(new String[lowPriorityMetricsPH.size()]);
		lowPriorityMetricsPH.clear();
		temp = setStringArray("lowPriorityMetricsPH", config, temp);
		for (String metric : temp)
			lowPriorityMetricsPH.add(metric);
		
		temp = highPriorityMetricsVM.toArray(new String[highPriorityMetricsVM.size()]);
		highPriorityMetricsVM.clear();
		temp = setStringArray("highPriorityMetricsVM", config, temp);
		for (String metric : temp)
			highPriorityMetricsVM.add(metric);
		
		temp = mediumPriorityMetricsVM.toArray(new String[mediumPriorityMetricsVM.size()]);
		mediumPriorityMetricsVM.clear();
		temp = setStringArray("mediumPriorityMetricsVM", config, temp);
		for (String metric : temp)
			mediumPriorityMetricsVM.add(metric);
		
		temp = lowPriorityMetricsVM.toArray(new String[lowPriorityMetricsVM.size()]);
		lowPriorityMetricsVM.clear();
		temp = setStringArray("lowPriorityMetricsVM", config, temp);
		for (String metric : temp)
			lowPriorityMetricsVM.add(metric);
		
		// Remove empty elements
		highPriorityMetricsPH.removeAll(Collections.singleton(null));
		mediumPriorityMetricsPH.removeAll(Collections.singleton(null));
		lowPriorityMetricsPH.removeAll(Collections.singleton(null));
		highPriorityMetricsVM.removeAll(Collections.singleton(null));
		mediumPriorityMetricsVM.removeAll(Collections.singleton(null));
		lowPriorityMetricsVM.removeAll(Collections.singleton(null));
		
		// ---MonitorDatabaseConnector---
		mysqlUsername = setString("mysql.username",config,mysqlUsername);
		mysqlPassword = setString("mysql.password",config,mysqlPassword);
		mysqlHost = setString("mysql.host",config,mysqlHost);
		
		// ---MetricsCollector---
		pollingRate = setLong("collector.pollingrate",config,pollingRate);
		bonfireAPI = setString("bonfire.api.url",config,bonfireAPI);
		apiUser = setString("bonfire.api.user",config,apiUser);
		userAgent = setString("bonfire.api.useragent",config,userAgent);
		keyStorePath = setString("keystore",config,keyStorePath);
		keyStorePassword = setString("keystore.key",config,keyStorePassword);
		
		// Property hostInfo has structure:
		// uk-epcc;http://crockett.epcc.ed.ac.uk/one-status.xml,fr-inria;http://frontend.integration.bonfire.grid5000.fr/one-status.xml
		// i.e. pairs of hostName;oneStatusURL delimited by ','
		String[] pairs = config.getStringArray("hostinfo"); // Loads array of format { "uk-epcc;URL", "fr-inria;URL" }
		if (pairs != null && pairs.length > 0) {
			
			// Split pairs and insert into hostInfo
			hostInfo = new String[pairs.length][2];
			for (int i=0; i<pairs.length; i++) {
				hostInfo[i] = pairs[i].split(";");
			}
			
		}
		else {
			
			// Build string representation of default value
			String delimiter = "";
			StringBuilder hostsStr = new StringBuilder();
			for (int i=0; i<hostInfo.length; i++) {
				hostsStr.append(delimiter);
				hostsStr.append(hostInfo[i][0] + ";" + hostInfo[i][1]);
				delimiter = ", ";
			}
			MessageHandler.error("Error reading property: hostinfo. Using default values: " + hostsStr.toString());
		}
		
		// ---MessageHandler---
		loggingLevel = setLoggingLevel("logginglevel",config,loggingLevel);
	}
		
	private static String setString(String key, PropertiesConfiguration config, String defaultValue) {
		try {
			return config.getString(key);
		} catch (NoSuchElementException e) {
			MessageHandler.error("Error reading property: " + key + ". Using default value: " + defaultValue);
			return defaultValue;
		}
	}
	
	private static long setLong(String key, PropertiesConfiguration config, long defaultValue) {
		try {
			return config.getLong(key);
		} catch (NoSuchElementException e) {
			MessageHandler.error("Error reading property: " + key + ". Using default value: " + defaultValue);
			return defaultValue;
		}
	}
	
	private static int setInt(String key, PropertiesConfiguration config, int defaultValue) {
		try {
			return config.getInt(key);
		} catch (NoSuchElementException e) {
			MessageHandler.error("Error reading property: " + key + ". Using default value: " + defaultValue);
			return defaultValue;
		}
	}
	
	private static MessageHandler.Level setLoggingLevel(String key, PropertiesConfiguration config, MessageHandler.Level defaultValue) {
		String loggingProperty = "";
		try {
			loggingProperty = config.getString(key);
			return MessageHandler.Level.fromString(loggingProperty);
		} catch (NoSuchElementException e) {
			MessageHandler.error("Error reading property: " + key + ". Using default value: " + defaultValue);
		} catch (IllegalArgumentException e) {
			MessageHandler.error("Invalid value " + loggingProperty + " set for property: " + key + ". Using default value: " + defaultValue);
		}
		return defaultValue;
	}
	
	private static String[] setStringArray(String key, PropertiesConfiguration config, String[] defaultValue) {
		// getStringArray() returns an empty array if the value is not found and apparently does not support throwExceptionOnMissing
		// (according to http://commons.apache.org/proper/commons-configuration/userguide/howto_basicfeatures.html )
		// so check if loaded successfully by comparing to null and checking length > 0.
		String[] retValues = config.getStringArray(key);
		
		if (retValues != null && retValues.length > 0) {
			return retValues;
		}
		else {
			
			// Build string representation of default value
			String delimiter = "";
			StringBuilder defaultStr = new StringBuilder();
			for (int i=0; i<defaultValue.length; i++) {
				defaultStr.append(delimiter);
				defaultStr.append(defaultValue[i]);
				delimiter = ", ";
			}
			MessageHandler.error("Error reading property: " + key + ". Using default values: " + defaultStr.toString());
			return defaultValue;
		}
	}
	
	/**
	 * Private constructor to ensure class methods and attributes can only be statically referenced.
	 */
	private ConfigurationValues() {}
}
