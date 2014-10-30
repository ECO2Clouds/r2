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
//	Last Updated Date :		03 Sept 2014
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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Connects to an external database and interacts via prepared SQL statements.
 * For the Monitoring component, this enables insertions and updates of active experiment resources records.
 * For the Collecting component, this enables insertions and updates of resource metrics. 
 */
public class DatabaseConnector implements IDatabaseConnector {

	/**
	 * The SQL dialects supported by the connector.
	 * For all practical purposes, MySQL is to be used. HSQLDB is included simply for facilitating testing.
	 */
	public enum Mode {
		/** The mysql mode setting. */
		MYSQL, 
		/** The hsqldb mode setting. */
		HSQLDB;
	}
	
	/** The username for accessing the accounting database. */
	private String username = ConfigurationValues.mysqlUsername;
	
	/** The password for accessing the accounting database. */
	private String password = ConfigurationValues.mysqlPassword;
	
	/** The host location of the accounting database. */
	private String url = ConfigurationValues.mysqlHost;
	
	/** The SQL object representing the connection to the accounting database. */
	public Connection connection = null;
	
	/** A statement object used to prepare SQL transactions. */
	private PreparedStatement statement = null;
	
	/** Additional statement object used to prepare SQL transactions. */
	private PreparedStatement statement2 = null;
	
	/** The results returned by SQL queries. */
	private ResultSet results = null;
	
	/** The current mode of SQL dialect being used by this connector. MySQL by default. */
	private Mode currentMode = Mode.MYSQL;
	
	/** A flag used to gate the connect() and disconnect() functions. */
	public boolean connected = false;
		 
	/**
	 * Instantiates a new database connector.
	 */
	public DatabaseConnector() {
	}

	public DatabaseConnector(String url, String username, String password) {
		this.username = username;
		this.password = password;
		this.url = url;
	}
	
	/* (non-Javadoc)
	 * @see eu.eco2clouds.accounting.monitoringcollector.IDatabaseConnector#connect()
	 */
	@Override
	public boolean connect() {
		int counter = 0;
		while (!connected) {
			try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				connection = DriverManager.getConnection("jdbc:mysql://" + url + "?" + "user=" + username + "&password=" + password);
				connected = true;
				//return true;
			} catch (InstantiationException e) {
				MessageHandler.error("DB_ERROR: Monitoring Collector unable to connect to database server at " + url + ". Fatal. Discarding Data. InstantiationException. " + e.getMessage());
				//return false; // throw new RuntimeException(e);
			} catch (ClassNotFoundException e) {
				MessageHandler.error("DB_ERROR: Monitoring Collector unable to connect to database server at " + url + ". Fatal. Discarding Data. ClassNotFoundException. " + e.getMessage());
				//return false; // throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				MessageHandler.error("DB_ERROR: Monitoring Collector unable to connect to database server at " + url + ". Fatal. Discarding Data. IllegalAccessException. " + e.getMessage());
				//return false; // throw new RuntimeException(e);
			} catch (SQLException e) {
				MessageHandler.error("DB_ERROR: Monitoring Collector unable to connect to database server at " + url + ". Fatal. Discarding Data. SQLException. " + e.getMessage());
				//return false; // throw new RuntimeException(e);
			}

			if ( counter > 0) {
				MessageHandler.error("DB_ERROR: Monitoring Collector unable to connect to database server at " + url + ". Fatal. Retrying.");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					MessageHandler.error("Monitor died whilst sleeping.");
			}
		}
			
			counter++;
			if ( counter > 2 )
				break;
		}
		return connected;
	}
	
	/* (non-Javadoc)
	 * @see eu.eco2clouds.accounting.monitoringcollector.IDatabaseConnector#disconnect()
	 */
	@Override
	public void disconnect() {
		
		cleanUp(); // Close and clean up all JDBC resources
		
		if (connected) {
			try { connection.close(); } catch (SQLException e) { 
				MessageHandler.error("DB_ERROR: Error closing connection (disconnet). Exception: " + e.getMessage() + " Skipping.");
			}
			connected = false;
			connection = null;
		}
		
	}
	
	/** Frees all database resources. */
	private void cleanUp() {
		if (results != null) {
			try { results.close(); } catch (SQLException e) {
				MessageHandler.error("DB_ERROR: Error cleaning-up results. Exception: " + e.getMessage() + " Skipping.");
			}
			results = null;
		}
		
		if (statement != null) {
			try { statement.close(); } catch (SQLException e) {
				MessageHandler.error("DB_ERROR: Error cleaning-up statement. Exception: " + e.getMessage() + " Skipping.");
			}
			statement = null;
		}
		
		if (statement2 != null) {
			try { statement2.close(); } catch (SQLException e) {
				MessageHandler.error("DB_ERROR: Error cleaning-up statement2. Exception: " + e.getMessage() + " Skipping.");
			}
			statement2 = null;
		}
	}
		
	@Override
	public void insertNewExperiment(int experimentId) {
		insertNewExperiment(experimentId, "eco2clouds", "name/");
	}
	/* (non-Javadoc)
	 * @see eu.eco2clouds.accounting.monitoringcollector.IDatabaseConnector#insertNewExperiment(int)
	 */
	@Override
	public void insertNewExperiment(int experimentId, String groupName, String userName) {
		
		if ( connect() ) {
		try {
				statement = connection.prepareStatement("INSERT INTO metrics_experiments (id_metrics_experiments,active,group_name,user_name) " +
						   								"VALUES (?,1,?,?);");
			
			// Parameters index from 1
			statement.setInt(1, experimentId);
			statement.setString(2, groupName.toString());
				statement.setString(3, userName.toString());
			
			statement.executeUpdate();
		} catch (SQLException e) {
				MessageHandler.error("DB_ERROR: Error inserting experiment ID " + experimentId + ", group_name:" + groupName + ", user_name:" + userName + " into database. Exception: " + e.getMessage() + " Skipping.");
		}
		
		disconnect(); // Close and clean up all JDBC resources
	}
	}
	
	// ***TODO: Have to do anything to VM states here? Would require an additional attribute in the metrics_virtual_machines table***
	/* (non-Javadoc)
	 * @see eu.eco2clouds.accounting.monitoringcollector.IDatabaseConnector#setExperimentTerminated(int)
	 */
	@Override
	public void setExperimentTerminated(int experimentId) {
				
		if ( connect() ) {		
		try {
			statement = connection.prepareStatement("UPDATE metrics_experiments " +
													"SET active=0 " +
													"WHERE id_metrics_experiments=?;");
			
			// Parameters index from 1
			statement.setInt(1, experimentId);

			statement.executeUpdate();
			
		} catch (SQLException e) {
			MessageHandler.error("DB_ERROR: Error setting experiment ID " + experimentId + " inactive. Exception: " + e.getMessage() + " Skipping.");
		}
		
		disconnect(); // Close and clean up all JDBC resources
	} 
	}
	
	@Override
	public void setExperimentActive(int experimentId) {
				
		if (connect()) {
		try {
			statement = connection.prepareStatement("UPDATE metrics_experiments " +
													"SET active=1 " +
													"WHERE id_metrics_experiments=?;");
			
			// Parameters index from 1
			statement.setInt(1, experimentId);

			statement.executeUpdate();
			
		} catch (SQLException e) {
			MessageHandler.error("DB_ERROR: Error setting experiment ID " + experimentId + " to active. Exception: " + e.getMessage() + " Skipping.");
		}
		
		disconnect(); // Close and clean up all JDBC resources
	}
	}
	
	/* (non-Javadoc)
	 * @see eu.eco2clouds.accounting.monitoringcollector.IDatabaseConnector#setExperimentAggregator(int, java.net.URI)
	 */
	@Override
	public void setExperimentAggregator(int experimentId, URI aggregator) {

		if ( connect() ) {
		try {
			statement = connection.prepareStatement("UPDATE metrics_experiments " +
													"SET aggregator_location=? " +
													"WHERE id_metrics_experiments=?;");
			
			// Parameters index from 1
			statement.setString(1, aggregator.toString());
			statement.setInt(2, experimentId);

			statement.executeUpdate();

		} catch (SQLException e) {
			MessageHandler.error("DB_ERROR: Error setting experiment ID " + experimentId + " aggregator to " + aggregator.toString() + " in database. Exception: " + e.getMessage() + " Skipping.");			
		}

		disconnect(); // Close and clean up all JDBC resources
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.eco2clouds.accounting.monitoringcollector.IDatabaseConnector#setExperimentUserName(int, java.net.String)
	 */
	@Override
	public void setExperimentUserName(int experimentId, String userName) {

		if ( connect() ) {
			try {
				statement = connection.prepareStatement("UPDATE metrics_experiments " +
														"SET user_name=? " +
														"WHERE id_metrics_experiments=?;");
				
				// Parameters index from 1
				statement.setString(1, userName.toString());
				statement.setInt(2, experimentId);
	
				statement.executeUpdate();
	
			} catch (SQLException e) {
				MessageHandler.error("DB_ERROR: Error setting experiment ID " + experimentId + " user_name to " + userName.toString() + " in database. Exception: " + e.getMessage() + " Skipping.");			
			}
	
			disconnect(); // Close and clean up all JDBC resources
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.eco2clouds.accounting.monitoringcollector.IDatabaseConnector#setExperimentGroupName(int, java.net.String)
	 */
	@Override
	public void setExperimentGroupName(int experimentId, String groupName) {

		if ( connect() ) {
			try {
				statement = connection.prepareStatement("UPDATE metrics_experiments " +
														"SET group_name=? " +
														"WHERE id_metrics_experiments=?;");
				
				// Parameters index from 1
				statement.setString(1, groupName.toString());
				statement.setInt(2, experimentId);
	
				statement.executeUpdate();
	
			} catch (SQLException e) {
				MessageHandler.error("DB_ERROR: Error setting experiment ID " + experimentId + " group_name to " + groupName.toString() + " in database. Exception: " + e.getMessage() + " Skipping.");			
			}
	
			disconnect(); // Close and clean up all JDBC resources
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.eco2clouds.accounting.monitoringcollector.IDatabaseConnector#insertNewVM(int, java.net.URI, long, java.lang.String, java.lang.String)
	 */
	@Override
	public void insertNewVM(int experimentId, URI location, long timeStamp, String host, String site) {
					
		// Insert the host and site into the database. Does nothing if records already exist.
		// Used to avoid problems with scenarios such as a new physical host is added to a site and a VM is deployed on it before the Collector updates the host list from the relevant one-status page.
		if (site != null && !site.equals("null")) { insertNewSites(new String[]{site}); }
		if (host != null && !host.equals("null")) { insertNewPhysicalHosts(new String[]{host},site); }
		
		connect(); // Ensure connection established
		
		try {
			// Transaction to ensure consistency. e.g. A query won't be able to occur between the two statements returning a VM with no host link.
			connection.setAutoCommit(false);
			
			statement = connection.prepareStatement("INSERT INTO metrics_virtual_machines (id_metrics_virtual_machines,location,fk_metrics_experiments,start_time,end_time) " +
													"VALUES (NULL,?,?,?,NULL);");
		
			// Parameters index from 1
			statement.setString(1, location.toString());
			statement.setInt(2, experimentId);
			statement.setLong(3, timeStamp);
		
			statement.executeUpdate();
		} catch (SQLException e) {
			MessageHandler.error("DB_ERROR: Error associating virtual machine ID " + location.toString() + " with experiment ID " + experimentId + " in database. Exception: " + e.getMessage() + " Skipping.");
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e1) {
				MessageHandler.error("DB_ERROR: SQL connection error attempting to re-enable auto commit. Exception: " + e1.getMessage());
			}
			
			disconnect(); // Close and clean up all JDBC resources
			
			return;
		}
		
		try {
			// Also add physical host link
			// ***TODO: Bug with BonFIRE: Message queue compute.create event hosts field is always null for some sites. Breaks this link. See http://tracker.bonfire-project.eu/issues/1809***
			statement2 = connection.prepareStatement("INSERT INTO virtual_machines_physical_hosts_link (id_virtual_machines_physical_hosts_link,fk_metrics_virtual_machines,fk_metrics_physical_hosts,start_time,end_time) " +
																		"VALUES (NULL," + getModeSpecificIdentity() + ",(SELECT id_metrics_physical_hosts " +
																														"FROM metrics_physical_hosts " +
																														"WHERE location=? " +
																														"AND fk_metrics_sites=(SELECT id_metrics_sites " +
																																			  "FROM metrics_sites " +
																																			  "WHERE location=?)), " +
																		"?,NULL);");
		
			statement2.setString(1, host);
			statement2.setString(2, site);
			statement2.setLong(3, timeStamp);
		
			statement2.executeUpdate();
			
			connection.commit();
			
		} catch (SQLException e) {
			MessageHandler.error("DB_ERROR: Error associating virtual machine ID " + location.toString() + " with host " + host + " in database. Warning! Entry in metrics_virtual_machines now has no associated virtual_machines_physical_hosts_link record! Exception: " + e.getMessage());
		}
		
		// Reset for non-batch processing
		try {
			connection.setAutoCommit(true);
		} catch (SQLException e1) {
			MessageHandler.error("DB_ERROR: SQL connection error attempting to re-enable auto commit. Exception: " + e1.getMessage());
		}
				
		disconnect(); // Close and clean up all JDBC resources
	}
	
	/* (non-Javadoc)
	 * @see eu.eco2clouds.accounting.monitoringcollector.IDatabaseConnector#updateVMPhysicalHost(java.net.URI, long, java.lang.String)
	 */
	@Override
	public void updateVMPhysicalHost(int VMid, long timeStamp, String host) {

		connect(); // Ensure connection established
		
		try {
			// Transaction to ensure consistency. e.g. A query won't be able to occur between the two statements returning a VM with no host link.
			connection.setAutoCommit(false);
			
			statement = connection.prepareStatement("UPDATE virtual_machines_physical_hosts_link " +
													"SET fk_metrics_physical_hosts=(select id_metrics_physical_hosts from metrics_physical_hosts where location=?) " + 
													"WHERE fk_metrics_virtual_machines=? AND start_time=?;");
		
			// Parameters index from 1
			statement.setString(1, host);
			statement.setInt(2, VMid);
			statement.setLong(3, timeStamp);
		
			statement.executeUpdate();
			
			connection.commit();
		} catch (SQLException e) {
			MessageHandler.error("DB_ERROR: Error updating virtual machine ID:" + VMid + " with start date:" + timeStamp + ", to host:" + host + ", in database. Exception: " + e.getMessage() + " Skipping.");
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e1) {
				MessageHandler.error("DB_ERROR: SQL connection error attempting to re-enable auto commit. Exception: " + e1.getMessage());
			}
			
			disconnect(); // Close and clean up all JDBC resources
			
			return;
		}
		
		// Reset for non-batch processing
		try {
			connection.setAutoCommit(true);
		} catch (SQLException e1) {
			MessageHandler.error("DB_ERROR: SQL connection error attempting to re-enable auto commit. Exception: " + e1.getMessage());
		}
		
		disconnect(); // Close and clean up all JDBC resources
	}
		
	/* (non-Javadoc)
	 * @see eu.eco2clouds.accounting.monitoringcollector.IDatabaseConnector#updateVM(int, java.net.URI, long, java.lang.String, java.lang.String)
	 */
	@Override
	public void updateVM(int experimentId, URI location, long timeStamp, String host, String site) {

		MessageHandler.error("updatingVM. NOT IMPLEMENTED. experimentId:" + experimentId + 
								", location:" + location + ", timeStamp:" + timeStamp + 
								", host:" + host + ", site:" + site + ".");
		
//		connect(); // Ensure connection established
		
		/*
		try {
			// Transaction to ensure consistency. e.g. A query won't be able to occur between the two statements returning a VM with no host link.
			connection.setAutoCommit(false);
			
			statement = connection.prepareStatement("UPDATE virtual_machines_physical_hosts_link " +
													"SET " + 
													"WHERE ;");
		
			// Parameters index from 1
			statement.setString(1, location.toString());
			statement.setInt(2, experimentId);
		
			statement.executeUpdate();
		} catch (SQLException e) {
			MessageHandler.error("Error associating virtual machine ID " + location.toString() + " with experiment ID " + experimentId + " in database. Exception: " + e.getMessage() + " Skipping.");
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e1) {
				MessageHandler.error("SQL connection error attempting to re-enable auto commit. Exception: " + e1.getMessage());
			}
			return;
		}
		
		try {
			// Also add physical host link
			// ***TODO: Bug with BonFIRE: Message queue compute.create event hosts field is always null for some sites. Breaks this link. See http://tracker.bonfire-project.eu/issues/1809***
			statement2 = connection.prepareStatement("INSERT INTO virtual_machines_physical_hosts_link (id_virtual_machines_physical_hosts_link,fk_metrics_virtual_machines,fk_metrics_physical_hosts,start_time,end_time) " +
																		"VALUES (NULL," + getModeSpecificIdentity() + ",(SELECT id_metrics_physical_hosts " +
																														"FROM metrics_physical_hosts " +
																														"WHERE location=? " +
																														"AND fk_metrics_sites=(SELECT id_metrics_sites " +
																																			  "FROM metrics_sites " +
																																			  "WHERE location=?)), " +
																		"?,NULL);");
		
			statement2.setString(1, host);
			statement2.setString(2, site);
			statement2.setLong(3, timeStamp);
		
			statement2.executeUpdate();
			
			connection.commit();
			
		} catch (SQLException e) {
			MessageHandler.error("Error associating virtual machine ID " + location.toString() + " with host " + host + " in database. Warning! Entry in metrics_virtual_machines now has no associated virtual_machines_physical_hosts_link record! Exception: " + e.getMessage());
		}
		
		// Reset for non-batch processing
		try {
			connection.setAutoCommit(true);
		} catch (SQLException e1) {
			MessageHandler.error("SQL connection error attempting to re-enable auto commit. Exception: " + e1.getMessage());
		}
			
				*/
//		disconnect(); // Close and clean up all JDBC resources
	}
	
	// Insert one or more sites using an SQL transaction
	/* (non-Javadoc)
	 * @see eu.eco2clouds.accounting.monitoringcollector.IDatabaseConnector#insertNewSites(java.lang.String[])
	 */
	@Override
	public void insertNewSites(String[] locations) {
		
		if ( connect() ) {
		try {
			connection.setAutoCommit(false);
			
			//Only insert if doesn't already exist
			statement = connection.prepareStatement("INSERT INTO metrics_sites(id_metrics_sites,location) " +
													"SELECT NULL,? " +
													"FROM DUAL " +
					   								"WHERE NOT EXISTS(SELECT 1 " +
					   												 "FROM metrics_sites " +
					   												 "WHERE location=?);");
			
			for (String location : locations) {					
				// Parameters index from 1
				statement.setString(1, location);
				statement.setString(2, location);

				try {
					statement.addBatch();
				} catch (SQLException e1) {
					MessageHandler.error("DB_ERROR: Error inserting site " + location + " into database. Exception: " + e1.getMessage() + " Skipping.");
				}
			}
			
			statement.executeBatch();
			connection.commit();
			
		} catch (SQLException e) {
			MessageHandler.error("DB_ERROR: Error inserting new site records into database. Exception: " + e.getMessage() + " Skipping.");
		}
		
		try {
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			MessageHandler.error("DB_ERROR: SQL connection error attempting to re-enable auto commit. Exception: " + e.getMessage());
		}
		
		disconnect(); // Close and clean up all JDBC resources
	}
	}
	
	// Insert one or more hosts linked to a specified site using an SQL transaction
	/* (non-Javadoc)
	 * @see eu.eco2clouds.accounting.monitoringcollector.IDatabaseConnector#insertNewPhysicalHosts(java.lang.String[], java.lang.String)
	 */
	@Override
	public void insertNewPhysicalHosts(String[] hosts, String siteName) {
		
		if (connect()) {
		try {
			connection.setAutoCommit(false);
			
			// Only insert if doesn't already exist
			// TODO: Refactor this statment? "(SELECT id_metrics_sites FROM metrics_sites WHERE location=?)" repeated affecting legibility
			statement = connection.prepareStatement("INSERT INTO metrics_physical_hosts (id_metrics_physical_hosts,location,fk_metrics_sites) " +
													"SELECT NULL,?,(SELECT id_metrics_sites FROM metrics_sites WHERE location=?) " +
													"FROM DUAL " +
					   								"WHERE NOT EXISTS(SELECT 1 " +
					   												 "FROM metrics_physical_hosts " +
					   												 "WHERE location=? " +
					   												 "AND fk_metrics_sites=(SELECT id_metrics_sites " +
					   												 						"FROM metrics_sites " +
					   												 						"WHERE location=?));");
			
			for (String host : hosts) {
				// Parameters index from 1
				statement.setString(1, host);
				statement.setString(2, siteName);
				statement.setString(3, host);
				statement.setString(4, siteName);
				
				try {
					statement.addBatch();
				} catch (SQLException e1) {
					MessageHandler.error("DB_ERROR: Error inserting host " + host + " into database. Exception: " + e1.getMessage() + " Skipping.");
				}
			}
			
			statement.executeBatch();
			connection.commit();
			statement.executeUpdate();
			
		} catch (SQLException e) {
			MessageHandler.error("DB_ERROR: Error inserting new hosts records into database. Exception: " + e.getMessage() + " Skipping.");
		}
		
		try {
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			MessageHandler.error("DB_ERROR: SQL connection error attempting to re-enable auto commit. Exception: " + e.getMessage());
		}
		
		disconnect(); // Close and clean up all JDBC resources
		}
	}
	
	/*public void insertNewPhysicalHost(String location, int siteId) {
		
		try {
			//Only insert if doesn't already exist
			statement = connection.prepareStatement("INSERT INTO metrics_physical_hosts (id_metrics_sites_physical_hosts,location,fk_metrics_site) " +
													"SELECT NULL,?,? FROM " + getModeSpecificDummytable() + " " +
					   								"WHERE NOT EXISTS(SELECT 1 FROM metrics_physical_hosts WHERE fk_metrics_sites=?);");
			
			// Parameters index from 1
			statement.setString(1, location);
			statement.setInt(2, siteId);
			statement.setInt(3, siteId);
			
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/
	
	// ***TODO: Consolidate following methods?
	/* (non-Javadoc)
	 * @see eu.eco2clouds.accounting.monitoringcollector.IDatabaseConnector#getActiveExperiments()
	 */
	@Override
	public ArrayList<Experiment> getActiveExperiments() {
		ArrayList<Experiment> experiments = new ArrayList<Experiment>();	// Initialise output container
		
		if ( connect() ) {
		try {
				statement = connection.prepareStatement("SELECT id_metrics_experiments, aggregator_location, group_name, user_name " +
													"FROM metrics_experiments " +
													"WHERE metrics_experiments.active " +
													"ORDER BY metrics_experiments.id_metrics_experiments;");
			results = statement.executeQuery();

			while (results.next()) {
					experiments.add(new Experiment(results.getInt("id_metrics_experiments"), results.getString("aggregator_location"), results.getString("group_name"), results.getString("user_name"), true));
			}
			
		} catch (SQLException e) {
			MessageHandler.error("DB_ERROR: Error querying database for active experiments. Exception: " + e.getMessage());	
		}
		
		disconnect(); // Close and clean up all JDBC resources
		}
		
		return experiments;
	}
	
	@Override
	public Experiment getExperiment(int experimentId) {
		
		if (connect() ) {
		try {
				statement = connection.prepareStatement("SELECT id_metrics_experiments, aggregator_location, group_name, user_name, active " +
													"FROM metrics_experiments " +
													"WHERE metrics_experiments.id_metrics_experiments=?; ");
			statement.setInt(1, experimentId);
			results = statement.executeQuery();

			results.next();
				Experiment experiment = new Experiment(results.getInt("id_metrics_experiments"), results.getString("aggregator_location"), results.getString("group_name"), results.getString("user_name"), results.getBoolean("active"));
			
			disconnect(); // Close and clean up all JDBC resources
			return experiment;
		} catch (SQLException e) {
			MessageHandler.error("DB_ERROR: Error querying database for one experiment. Exception: " + e.getMessage());	
			disconnect(); // Close and clean up all JDBC resources
		}
		}
		return null;
	}
	
	@Override
	public ArrayList<Experiment> getAllExperiments() {
		ArrayList<Experiment> experiments = new ArrayList<Experiment>();	// Initialise output container
		
		if (connect() ) {
		try {
				statement = connection.prepareStatement("SELECT id_metrics_experiments, aggregator_location, group_name, user_name, active " +
													"FROM metrics_experiments " +
													"ORDER BY metrics_experiments.id_metrics_experiments;");
			results = statement.executeQuery();

			while (results.next()) {
					experiments.add(new Experiment(results.getInt("id_metrics_experiments"), results.getString("aggregator_location"), results.getString("group_name"), results.getString("user_name"), results.getBoolean("active")));
			}
			
		} catch (SQLException e) {
			MessageHandler.error("DB_ERROR: Error querying database for all experiments. Exception: " + e.getMessage());	
		}
		
		disconnect(); // Close and clean up all JDBC resources
		}
		
		return experiments;
	}
	
	@Override
	public Boolean updateExperimentGroup(int experimentId, String groupName) {
		if (connect() ) {
		try {
			statement = connection.prepareStatement("UPDATE metrics_experiments " +
													"SET group_name=? " +
													"WHERE id_metrics_experiments=?;");
			
			// Parameters index from 1
			statement.setString(1, groupName.toString());
			statement.setInt(2, experimentId);

			statement.executeUpdate();
			
		} catch (SQLException e) {
			MessageHandler.error("DB_ERROR: Error updating experiment ID " + experimentId + " with group name:" + groupName + ". Exception: " + e.getMessage() + " Skipping.");
		}
		
		disconnect(); // Close and clean up all JDBC resources
		}
		return true;
	}
	
	@Override
	public Boolean updateExperimentUser(int experimentId, String userName) {
		if (connect() ) {
			try {
				statement = connection.prepareStatement("UPDATE metrics_experiments " +
														"SET user_name=? " +
														"WHERE id_metrics_experiments=? ; ");
				
				// Parameters index from 1
				statement.setString(1, userName.toString());
				statement.setInt(2, experimentId);
	
				statement.executeUpdate();
				
			} catch (SQLException e) {
				MessageHandler.error("DB_ERROR: Error updating experiment ID " + experimentId + " with user name:" + userName + ". Exception: " + e.getMessage() + " Skipping.");
			}
			
			disconnect(); // Close and clean up all JDBC resources
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see eu.eco2clouds.accounting.monitoringcollector.IDatabaseConnector#getActiveVirtualMachines()
	 */
	@Override
	public ArrayList<VirtualMachine> getActiveVirtualMachines() {
		ArrayList<VirtualMachine> vms = new ArrayList<VirtualMachine>();	// Initialise output container
		
		if (connect() ) {
		try {
			statement = connection.prepareStatement("SELECT metrics_virtual_machines.id_metrics_virtual_machines, metrics_virtual_machines.location, metrics_experiments.aggregator_location " +
													"FROM metrics_virtual_machines " +
													"INNER JOIN metrics_experiments " +
													"ON metrics_virtual_machines.fk_metrics_experiments=metrics_experiments.id_metrics_experiments " +
													"INNER JOIN virtual_machines_physical_hosts_link " +
													"ON metrics_virtual_machines.id_metrics_virtual_machines = virtual_machines_physical_hosts_link.fk_metrics_virtual_machines " +
													"AND virtual_machines_physical_hosts_link.end_time is null " +
													"WHERE metrics_experiments.active " + 
													"ORDER BY metrics_virtual_machines.id_metrics_virtual_machines;");		

			results = statement.executeQuery();

			while (results.next()) {
				vms.add(new VirtualMachine(results.getInt("id_metrics_virtual_machines"), results.getString("location"), results.getString("aggregator_location")));
			}
			
		} catch (SQLException e) {
			MessageHandler.error("DB_ERROR: Error querying database for active virtual machines. Exception: " + e.getMessage());	
		}
		
		disconnect(); // Close and clean up all JDBC resources
		}
		
		return vms;
	}
	
	@Override
	public ArrayList<VirtualMachine> getActiveVirtualMachinesOfExperiment(int experimentId) {
		ArrayList<VirtualMachine> vms = new ArrayList<VirtualMachine>();	// Initialise output container
		
		if ( connect() ) {
		try {
			statement = connection.prepareStatement("SELECT metrics_virtual_machines.id_metrics_virtual_machines, metrics_virtual_machines.location, metrics_experiments.aggregator_location " +
													"FROM metrics_virtual_machines " +
													"INNER JOIN metrics_experiments " +
													"ON metrics_virtual_machines.fk_metrics_experiments=metrics_experiments.id_metrics_experiments " +
													"INNER JOIN virtual_machines_physical_hosts_link " +
													"ON metrics_virtual_machines.id_metrics_virtual_machines = virtual_machines_physical_hosts_link.fk_metrics_virtual_machines " +
													"AND virtual_machines_physical_hosts_link.end_time is null " +
													"WHERE metrics_experiments.active AND metrics_experiments.id_metrics_experiments=? " + 
													"ORDER BY metrics_virtual_machines.id_metrics_virtual_machines;");		

			statement.setInt(1, experimentId);
			results = statement.executeQuery();

			while (results.next()) {
				vms.add(new VirtualMachine(results.getInt("id_metrics_virtual_machines"), results.getString("location"), results.getString("aggregator_location")));
			}
			
		} catch (SQLException e) {
			MessageHandler.error("DB_ERROR: Error querying database for active virtual machines. Exception: " + e.getMessage());	
		}
		
		disconnect(); // Close and clean up all JDBC resources
		}
		
		return vms;
	}
	
	@Override
	public ArrayList<VirtualMachine> getAllVirtualMachinesOfExperiment(int experimentId) {
		ArrayList<VirtualMachine> vms = new ArrayList<VirtualMachine>();	// Initialise output container
		
		if ( connect() ) {		
		try {
			statement = connection.prepareStatement("SELECT metrics_virtual_machines.id_metrics_virtual_machines, metrics_virtual_machines.location, metrics_experiments.aggregator_location " +
													"FROM metrics_virtual_machines " +
													"INNER JOIN metrics_experiments " +
													"ON metrics_virtual_machines.fk_metrics_experiments=metrics_experiments.id_metrics_experiments " +
													"INNER JOIN virtual_machines_physical_hosts_link " +
													"ON metrics_virtual_machines.id_metrics_virtual_machines = virtual_machines_physical_hosts_link.fk_metrics_virtual_machines " +
													"WHERE metrics_experiments.active AND metrics_experiments.id_metrics_experiments=? " + 
													"ORDER BY metrics_virtual_machines.id_metrics_virtual_machines;");		

			statement.setInt(1, experimentId);
			results = statement.executeQuery();

			while (results.next()) {
				vms.add(new VirtualMachine(results.getInt("id_metrics_virtual_machines"), results.getString("location"), results.getString("aggregator_location")));
			}
			
		} catch (SQLException e) {
			MessageHandler.error("DB_ERROR: Error querying database for active virtual machines. Exception: " + e.getMessage());	
		}
		
		disconnect(); // Close and clean up all JDBC resources
		}
		
		return vms;
	}
	
	@Override
	public ArrayList<VirtualMachine> getVirtualMachinesWithoutPhysicalHost() {
		ArrayList<VirtualMachine> vms = new ArrayList<VirtualMachine>();	// Initialise output container

		if ( connect() ) {
		try {
			statement = connection.prepareStatement("SELECT metrics_virtual_machines.id_metrics_virtual_machines, metrics_virtual_machines.location, metrics_virtual_machines.fk_metrics_experiments, virtual_machines_physical_hosts_link.start_time " +
													"FROM metrics_virtual_machines, virtual_machines_physical_hosts_link " +
													"WHERE virtual_machines_physical_hosts_link.fk_metrics_physical_hosts is null " +
													"AND virtual_machines_physical_hosts_link.fk_metrics_virtual_machines = metrics_virtual_machines.id_metrics_virtual_machines;");		

			results = statement.executeQuery();

			while (results.next()) {
				vms.add(new VirtualMachine(results.getInt("id_metrics_virtual_machines"), results.getString("location"), "", "", results.getLong("start_time"), -1L, results.getInt("fk_metrics_experiments")));
			}
			
		} catch (SQLException e) {
			MessageHandler.error("DB_ERROR: Error querying database for NULL Physical Host virtual machines. Exception: " + e.getMessage());	
		}
		
		disconnect(); // Close and clean up all JDBC resources
		}
		
		return vms;
	}
	
	@Override
	public ArrayList<PhysicalHost> getPhysicalHosts() {
		ArrayList<PhysicalHost> hosts = new ArrayList<PhysicalHost>();		// Initialise output container
		
		if ( connect() ) {	
		try {
			statement = connection.prepareStatement("SELECT metrics_physical_hosts.id_metrics_physical_hosts, metrics_physical_hosts.location, metrics_sites.location " +
													"FROM metrics_physical_hosts " +
													"INNER JOIN metrics_sites " +
													"ON metrics_physical_hosts.fk_metrics_sites=metrics_sites.id_metrics_sites " +
													"ORDER BY metrics_physical_hosts.id_metrics_physical_hosts;");	

			results = statement.executeQuery();

			while (results.next()) {
				hosts.add(new PhysicalHost(results.getInt("metrics_physical_hosts.id_metrics_physical_hosts"), results.getString("metrics_physical_hosts.location"), results.getString("metrics_sites.location")));
			}
			
		} catch (SQLException e) {
			MessageHandler.error("DB_ERROR: Error querying database for current hosts. Exception: " + e.getMessage());	
		}
		
		disconnect(); // Close and clean up all JDBC resources
		}
		
		return hosts;
	}
	
	/* (non-Javadoc)
	 * @see eu.eco2clouds.accounting.monitoringcollector.IDatabaseConnector#updateMetrics(java.util.List, java.lang.String)
	 */
	@Override
	public void updateMetrics(List<? extends Resource> resources, String itemsTable) {
		
		if ( connect () ) {
		try {

			connection.setAutoCommit(false);
			
			// The following is not batched, does one database commit per metric per resource.
			// A previous revision used plain Statements (not preparedStatements) with string concatenation to create a batch of
			// statement pairs but this sometimes caused tests to fail.
			// TODO: Optimise queries and database indexes.
			for (Resource resource : resources) {
				for (Item item : resource.items) {	
					PreparedStatement statement3 = null;
										
					// ***TODO: Refactor this to make it resource type agnostic. Issue is that Site resources do not know their database record ID at time of call and so have to do a SELECT.***
					if (resource instanceof Site) {
						
						// Insert an Item record so long as it's not a duplicate.
						// It is a duplicate if there already exists an association with the current resource in the "<resource>_items" table for an "items" record that has the same zabbix_itemid and timestamp.
						statement3 = connection.prepareStatement("INSERT INTO items (id_items,name,zabbix_itemid,clock,value,unity) " + "VALUES (NULL, ?, ?, ?, ?, ?)");
//																"SELECT NULL,?,?,?,?,? FROM DUAL " +
//																"WHERE NOT EXISTS(SELECT 1 " + // Only insert if there does not exist a site_items link where...
//																				 "FROM sites_items " +
//																				 "WHERE fk_metrics_sites=(SELECT id_metrics_sites " + // ...it is with the current site resource id...
//																				 						 "FROM metrics_sites " +
//																				 						 "WHERE location=?) " +
//																				 						 "AND fk_items in (SELECT id_items " + // ...and it is already associated with the items record we are trying to insert.
//																				 						 				  "FROM items " +
//																				 						 				  "WHERE zabbix_itemid=? AND clock=?))");


//						MessageHandler.error("loc:" + ((Site)resource).location + ", metric:" + item.name + ", val:" + item.value );	// DEBUG
						
						statement3.setString(1, item.name);
						statement3.setInt(2, item.zabbix_itemid);
						statement3.setLong(3, item.clock);
						statement3.setDouble(4, item.value);
						statement3.setString(5, item.unity);
//						statement3.setString(6, ((Site)resource).location);
//						statement3.setInt(7, item.zabbix_itemid);
//						statement3.setLong(8, item.clock);
						
						statement2 = connection.prepareStatement("INSERT INTO sites_items (id_sites_items,fk_metrics_sites,fk_items) " +
								 								 "SELECT NULL,(SELECT id_metrics_sites FROM metrics_sites WHERE location=?)," + getModeSpecificIdentity()  + " FROM DUAL;");

						statement2.setString(1, ((Site)resource).location);
					}
					else {
						
						// Insert an Item record so long as it's not a duplicate.
						// It is a duplicate if there already exists an association with the current resource in the "<resource>_items" table for an "items" record that has the same zabbix_itemid and timestamp.
						statement3 = connection.prepareStatement("INSERT INTO items (id_items,name,zabbix_itemid,clock,value,unity) " + "VALUES (NULL, ?, ?, ?, ?, ?)");
//																"SELECT NULL,?,?,?,?,? FROM DUAL " +
//																"WHERE NOT EXISTS(SELECT 1 " + // Only insert if there does not exist a <itemsTable>_items link where...
//																				 "FROM " + itemsTable + "_items " +
//																				 "WHERE fk_metrics_" + itemsTable + "=? " + // ...it is with the current site resource id...
//																				 "AND fk_items in (SELECT id_items " + // ...and it is already associated with the items record we are trying to insert.
//																				 				  "FROM items " +
//																				 				  "WHERE zabbix_itemid=? AND clock=?))");

						statement3.setString(1, item.name);
						statement3.setInt(2, item.zabbix_itemid);
						statement3.setLong(3, item.clock);
						statement3.setDouble(4, item.value);
						statement3.setString(5, item.unity);
//						statement.setInt(6, resource.id);
//						statement.setInt(7, item.zabbix_itemid);
//						statement.setLong(8, item.clock);
						
						statement2 = connection.prepareStatement("INSERT INTO " + itemsTable + "_items " +
								 								 "SELECT NULL,?," + getModeSpecificIdentity()  + " FROM DUAL;");

						statement2.setInt(1, resource.id);
					}
					
					// Statement2 depends on the previous statement.
					// Only update the corresponding item type table (e.g. experiments_items) if an Item record was inserted
					// i.e. the number of rows affected by the last insert on this connection is >0.
					
					if ( item.zabbix_itemid > 0) {
						int affectedRows = statement3.executeUpdate();
					if (affectedRows > 0) {
						statement2.executeUpdate();
					}
					
					connection.commit();
					}
				}			
			}
		} catch (SQLException e) {
			MessageHandler.error("DB_ERROR: Error updating " + itemsTable + " metrics in database. Exception: " + e.getMessage());
			// TODO: Add rollback here to avoid orphaned "items" table entries?
		} catch (Exception e) {
        	MessageHandler.error("DB_ERROR: Error updating " + itemsTable + ". Generic Exception: " + e.getMessage());
		}
		
		// Reset connection state
		try {
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			MessageHandler.error("DB_ERROR: SQL connection error attempting to re-enable auto commit. Exception: " + e.getMessage());
		}
				
		disconnect(); // Close and clean up all JDBC resources
	}
	}

	// Not tracking VM states in this iteration of the software (broken function)
	@Override
	public void terminateVM(int experimentId, URI location, long timeStamp, String host, String site) {
		
		if ( connect() ) {
		try {
			statement = connection.prepareStatement("UPDATE virtual_machines_physical_hosts_link " +
						"LEFT JOIN metrics_physical_hosts ON virtual_machines_physical_hosts_link.fk_metrics_physical_hosts=metrics_physical_hosts.id_metrics_physical_hosts " +
						"LEFT JOIN metrics_virtual_machines ON virtual_machines_physical_hosts_link.fk_metrics_virtual_machines=metrics_virtual_machines.id_metrics_virtual_machines " +
						"SET virtual_machines_physical_hosts_link.end_time=? " +
						"WHERE metrics_virtual_machines.location = ?;");

			// Parameters index from 1
			statement.setLong(1, timeStamp);
			statement.setString(2, location.toString());

			statement.executeUpdate();
			
		} catch (SQLException e) {
			MessageHandler.error("DB_ERROR: Error terminating VM. Exception: " + e.getMessage());	
		}
		
		try {
			statement = connection.prepareStatement("UPDATE metrics_virtual_machines " +
													"SET end_time=? " +
													"WHERE location=?;");
			
			// Parameters index from 1
			statement.setLong(1, timeStamp);
			statement.setString(2, location.toString());

			statement.executeUpdate();
			
		} catch (SQLException e) {
			MessageHandler.error("DB_ERROR: Error setting experiment ID " + experimentId + " inactive. Exception: " + e.getMessage() + " Skipping.");
			}
		}
		
		disconnect(); // Close and clean up all JDBC resources
	}
	
	// Not tracking VM states in this iteration of the software (broken function)
	/*
	@Override
	public void setVMDone(int experimentId, URI objectId) {
		
		try {			
			statement = connection.prepareStatement("UPDATE metrics_virtual_machines " +
													"SET tracking=0 " + ;// NB! This field does not exist in the current schema iteration and this function will not work
													"WHERE location=? " +
													"AND fk_metrics_experiments=?;");
		
			// Parameters index from 1
			statement.setString(1, objectId.toString());
			statement.setInt(2, experimentId);
		
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	/**
	 * Gets the name of the dialect specific function for returning the last generated id for an inserted record on this connection.
	 * With MySQL: "LAST_INSERT_ID()", HSQLDB: "IDENTITY()".
	 *
	 * @return the mode specific identity
	 */
	private String getModeSpecificIdentity() {
		switch(getMode()) {
			case MYSQL:
				return "LAST_INSERT_ID()";
			case HSQLDB:
				return "IDENTITY()";
			default:
				return "LAST_INSERT_ID()";
		}
	}
	
	/**
	 * Gets the current SQL dialect mode of this connector.
	 *
	 * @return the mode as represented by the Mode enum.
	 */
	public Mode getMode() {
		return currentMode;
	}

	/**
	 * Sets the current SQL dialect mode of this connector to either Mode.MYSQL or Mode.HSQLDB.
	 *
	 * @param mode the mode as represented by the Mode enum.
	 */
	public void setMode(Mode mode) {
		this.currentMode = mode;
	}
}
