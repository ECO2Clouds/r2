package eu.eco2clouds.scheduler.accounting.client;

import java.util.List;

import eu.eco2clouds.applicationProfile.datamodel.ExperimentDescriptor;
import eu.eco2clouds.accounting.datamodel.parser.Co2;
import eu.eco2clouds.accounting.datamodel.parser.Experiment;
import eu.eco2clouds.accounting.datamodel.parser.ExperimentReport;
import eu.eco2clouds.accounting.datamodel.parser.Host;
import eu.eco2clouds.accounting.datamodel.parser.HostMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.VM;
import eu.eco2clouds.accounting.datamodel.parser.VMMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.VMReport;
import eu.eco2clouds.accounting.datamodel.xml.HostPool;
import eu.eco2clouds.accounting.datamodel.parser.Testbed;
import eu.eco2clouds.accounting.datamodel.parser.TestbedMonitoring;

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
 * 
 * REST Client to the ECO2Clouds Accounting Service
 * @author David Garcia Perez - AtoS
 *
 */
public interface AccountingClient {
	
	/**
	 * Gets the URL of the accounting service
	 * @return the URL of the ECO2Clouds Accounting Service
	 */
	public String getURL();
	
	/**
	 * Sets the URL of the ECO2Clouds Accounting Service
	 * @param url of the ECO2Clouds Accounting Service
	 */
	public void setURL(String url);

	/**
	 * Returns a list with all the testbeds available 
	 * @return a list object with all the availables testbeds (empty list if there was an error at any point...)
	 */
	public List<Testbed> getListOfTestbeds();
	
	/**
	 * Returns the information of an specific testbed
	 * @param location testbed name
	 * @return Testbed Information
	 */
	public Testbed getTestbed(String location);
	
	/**
	 * Return a list with the hosts associated to a testbed
	 * @param location testbed
	 * @return list of hosts
	 */
	public List<Host> getHostsOfTesbed(String location);
	
	/**
	 * Return host info of a specific testbed
	 * @param location testbed
	 * @param hostName hostname
	 * @return Information for the specific host in that testbed
	 */
	public Host getHostOfTestbed(String location, String hostName);
	
	/**
	 * Returns the hoststatus information for an specific Testbed
	 * @param testbed 
	 * @return
	 */
	public HostPool getHostStatusForATestbed(Testbed testbed);
//	
//	/**
//	 * Returns 
//	 * @return
//	 */
//	public List<Testbed> getHostStatusForAllTestbeds();
	
	/**
	 * @return the list of experiments for that user and group.
	 */
	public List<Experiment> getListOfExperiments(String bonfireUser, String bonfireGroup);
	
	/**
	 * @return the specific details for an experiment
	 */
	public Experiment getExperiment(Experiment experiment, String bonfireUser, String bonfireGroup);
	
	/*
	 * @return the Energy report for the specific experiment.
	 */
	public ExperimentReport getExperimentReport(Experiment experiment, String bonfireUser, String bonfireGroup);
	
	/**
	 * Returns an experiment using its Id
	 * @param id of the experiment to retrieve from the accounting system
	 * @param bonfireUser Bonfire User which belongs the experiment
	 * @param bonfireGroup BonFIRE Group whihc belongs the experiment
	 * @return The experiment from the accounting service or an empty object if not possible
	 */
	public Experiment getExperiment(int id, String bonfireUser, String bonfireGroup);
	
	/**
	 * It creates an experiment entry in the database of Experiments
	 * @param experiment to be added to the database
	 * @return the experiment created in the database
	 */
	public Experiment createExperiment(Experiment experiment);
	
	/**
	 * It updates an experiment entry in the database of Experiments
	 * @param experiment to be updated
	 * @return the updated experiment object.
	 */
	public Experiment updateExperiment(Experiment experiment);
	
	/**
	 * Get monitoring information for a Testbed
	 * @param testbed
	 * @return the monitoring object
	 */
	public TestbedMonitoring getTestbedMonitoringStatus(Testbed testbed);
	
	/**
	 * Get monitoring information for a VM
	 * @param vm
	 * @return the monitoring object
	 */
	public VMMonitoring getVMMonitoringStatus(VM vm);
	
	/**
	 * Get monitoring information for a VM
	 * @param vm
	 * @return the Report of the VM
	 */
	public VMReport getVMReport(VM vm);
	
	/**
	 * Returns the host monitoring information of a host in an specific testbed
	 * @param testbed
	 * @param hostname
	 * @return monitoring object information
	 */
	public HostMonitoring getHostMonitoringStatus(Testbed testbed, String hostname);
	
	/**
	 * Returns the hostmonitoring information for a Host between starTime and endTime
	 * @param testbed
	 * @param hostname
	 * @param startTime time indicated in seconds
	 * @param endTime time indicated in seconds
	 * @return
	 */
	public List<HostMonitoring> getHostMonitoringStatus(Testbed testbed, String hostname, long startTime, long endTime);
	
	/**
	 * Returns the Co2 produced by a Host at this precise moment
	 * @param testbed
	 * @param hostname
	 * @return Co2 in g/h
	 */
	public double getCo2PerHourProducedByAHostAtThisMoment(Testbed testbed, String hostname);
	
	/**
	 * Calculates the Co2 comsuption for an specific experiment
	 * @param me
	 * @return
	 */
	public double getCo2Consumption(int experimentId);
	
	/**
	 * Returns the list of Co2 consumed by a tesbed for a given interval
	 * @param testbed
	 * @return
	 */
	public List<Co2> getCo2OfTestbedForInterval(Testbed testbed, long startTime, long endTime);
	
	/**
	 * Returns the submitted experiment descriptor for an Experiment.
	 * @param experiment
	 * @return
	 */
	public ExperimentDescriptor getSubmittedExperimentDescriptor(Experiment experiment);
	
	
	/**
	 * Returns list of running experiment
	 * @param list<experiment>
	 * @return
	 */
	public List<Experiment> getListOfRunningExperiments(String bonfireUser, String bonfireGroup);
	
	public List<VM> getListOfVMsOfExperiment(long bonfireExperimentId, String bonfireUser, String bonfireGroup);
	
}
