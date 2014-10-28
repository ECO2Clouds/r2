package eu.eco2clouds.accounting.rest.service;

import static eu.eco2clouds.accounting.Dictionary.CONTENT_TYPE_ECO2CLOUDS_XML;

import java.io.ByteArrayOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import eu.eco2clouds.accounting.conf.Configuration;
import eu.eco2clouds.accounting.datamodel.Action;
import eu.eco2clouds.accounting.datamodel.HostData;
import eu.eco2clouds.accounting.datamodel.Testbed;
import eu.eco2clouds.accounting.datamodel.parser.AggregateEnergy;
import eu.eco2clouds.accounting.datamodel.parser.AggregateEnergyUsage;
import eu.eco2clouds.accounting.datamodel.parser.ApparentPower;
import eu.eco2clouds.accounting.datamodel.parser.ApparentPowerUsage;
import eu.eco2clouds.accounting.datamodel.parser.ApplicationMetric;
import eu.eco2clouds.accounting.datamodel.parser.Availability;
import eu.eco2clouds.accounting.datamodel.parser.Biomass;
import eu.eco2clouds.accounting.datamodel.parser.CCGT;
import eu.eco2clouds.accounting.datamodel.parser.CO2Generated;
import eu.eco2clouds.accounting.datamodel.parser.CO2GenerationRate;
import eu.eco2clouds.accounting.datamodel.parser.CPULoad;
import eu.eco2clouds.accounting.datamodel.parser.CPUUserTimeAvg1;
import eu.eco2clouds.accounting.datamodel.parser.CPUUtil;
import eu.eco2clouds.accounting.datamodel.parser.CPUUtilization;
import eu.eco2clouds.accounting.datamodel.parser.Co2;
import eu.eco2clouds.accounting.datamodel.parser.Co2GenerationPer30s;
import eu.eco2clouds.accounting.datamodel.parser.Co2Producted;
import eu.eco2clouds.accounting.datamodel.parser.Co2Raw;
import eu.eco2clouds.accounting.datamodel.parser.Coal;
import eu.eco2clouds.accounting.datamodel.parser.Cogeneration;
import eu.eco2clouds.accounting.datamodel.parser.Collection;
import eu.eco2clouds.accounting.datamodel.parser.Cost;
import eu.eco2clouds.accounting.datamodel.parser.CustomCpuUtilization;
import eu.eco2clouds.accounting.datamodel.parser.CustomVMSRunning;
import eu.eco2clouds.accounting.datamodel.parser.CustomVfsIops;
import eu.eco2clouds.accounting.datamodel.parser.DiskFree;
import eu.eco2clouds.accounting.datamodel.parser.DiskIOPS;
import eu.eco2clouds.accounting.datamodel.parser.DiskReadWrite;
import eu.eco2clouds.accounting.datamodel.parser.DiskTotal;
import eu.eco2clouds.accounting.datamodel.parser.DiskUsage;
import eu.eco2clouds.accounting.datamodel.parser.ExperimentReport;
import eu.eco2clouds.accounting.datamodel.parser.Exported;
import eu.eco2clouds.accounting.datamodel.parser.Fossil;
import eu.eco2clouds.accounting.datamodel.parser.FreeMemory;
import eu.eco2clouds.accounting.datamodel.parser.FreeSpaceOnSrv;
import eu.eco2clouds.accounting.datamodel.parser.FreeSwapSpace;
import eu.eco2clouds.accounting.datamodel.parser.Gaz;
import eu.eco2clouds.accounting.datamodel.parser.Geothermal;
import eu.eco2clouds.accounting.datamodel.parser.Host;
import eu.eco2clouds.accounting.datamodel.parser.HostAvailability;
import eu.eco2clouds.accounting.datamodel.parser.HostMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.Hydraulic;
import eu.eco2clouds.accounting.datamodel.parser.Imported;
import eu.eco2clouds.accounting.datamodel.parser.IoUtil;
import eu.eco2clouds.accounting.datamodel.parser.Iops;
import eu.eco2clouds.accounting.datamodel.parser.Items;
import eu.eco2clouds.accounting.datamodel.parser.Link;
import eu.eco2clouds.accounting.datamodel.parser.MemFree;
import eu.eco2clouds.accounting.datamodel.parser.MemTotal;
import eu.eco2clouds.accounting.datamodel.parser.MemUsed;
import eu.eco2clouds.accounting.datamodel.parser.NPSHydro;
import eu.eco2clouds.accounting.datamodel.parser.NetIfIn;
import eu.eco2clouds.accounting.datamodel.parser.NetIfOut;
import eu.eco2clouds.accounting.datamodel.parser.Nuclear;
import eu.eco2clouds.accounting.datamodel.parser.NumberOfProcesses;
import eu.eco2clouds.accounting.datamodel.parser.NumberOfVMsRunning;
import eu.eco2clouds.accounting.datamodel.parser.OCGT;
import eu.eco2clouds.accounting.datamodel.parser.Oil;
import eu.eco2clouds.accounting.datamodel.parser.OneAvailability;
import eu.eco2clouds.accounting.datamodel.parser.OneUtilizationCpu;
import eu.eco2clouds.accounting.datamodel.parser.Other;
import eu.eco2clouds.accounting.datamodel.parser.PDUFr;
import eu.eco2clouds.accounting.datamodel.parser.PUE;
import eu.eco2clouds.accounting.datamodel.parser.Power;
import eu.eco2clouds.accounting.datamodel.parser.PowerCo2Generated;
import eu.eco2clouds.accounting.datamodel.parser.PowerConsumption;
import eu.eco2clouds.accounting.datamodel.parser.PowerCurrentvaAggregated;
import eu.eco2clouds.accounting.datamodel.parser.PowerCurrentwAggregated;
import eu.eco2clouds.accounting.datamodel.parser.PowerCurrentwhReal;
import eu.eco2clouds.accounting.datamodel.parser.PowerTotalAggregated;
import eu.eco2clouds.accounting.datamodel.parser.ProcNum;
import eu.eco2clouds.accounting.datamodel.parser.ProcessorLoad;
import eu.eco2clouds.accounting.datamodel.parser.Ps;
import eu.eco2clouds.accounting.datamodel.parser.PumpedStorage;
import eu.eco2clouds.accounting.datamodel.parser.RealPower;
import eu.eco2clouds.accounting.datamodel.parser.RealPowerUsage;
import eu.eco2clouds.accounting.datamodel.parser.RunningVMs;
import eu.eco2clouds.accounting.datamodel.parser.SiteUtilization;
import eu.eco2clouds.accounting.datamodel.parser.Solar;
import eu.eco2clouds.accounting.datamodel.parser.StorageUtilization;
import eu.eco2clouds.accounting.datamodel.parser.SwapFree;
import eu.eco2clouds.accounting.datamodel.parser.SwapTotal;
import eu.eco2clouds.accounting.datamodel.parser.SystemCPULoad;
import eu.eco2clouds.accounting.datamodel.parser.SystemCPUUtilPerc;
import eu.eco2clouds.accounting.datamodel.parser.SystemCpuUtil;
import eu.eco2clouds.accounting.datamodel.parser.SystemPowerConsumption;
import eu.eco2clouds.accounting.datamodel.parser.SystemSwapSize;
import eu.eco2clouds.accounting.datamodel.parser.TestbedMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.Total;
import eu.eco2clouds.accounting.datamodel.parser.TotalGreen;
import eu.eco2clouds.accounting.datamodel.parser.TotalMemory;
import eu.eco2clouds.accounting.datamodel.parser.VMMemorySizeFree;
import eu.eco2clouds.accounting.datamodel.parser.VMMemorySizeTotal;
import eu.eco2clouds.accounting.datamodel.parser.VMMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.VMReport;
import eu.eco2clouds.accounting.datamodel.parser.Water;
import eu.eco2clouds.accounting.datamodel.parser.Wind;
import eu.eco2clouds.accounting.datamodel.util.ModelConversion;
import eu.eco2clouds.accounting.monitoringcollector.DatabaseConnector;
import eu.eco2clouds.accounting.monitoringcollector.MessageHandler;
import eu.eco2clouds.accounting.rest.util.UpdateHostsThread;
import eu.eco2clouds.accounting.rest.util.UpdateTestbedsHostsThread;
import eu.eco2clouds.accounting.rest.util.Util;
import eu.eco2clouds.accounting.service.HostDAO;
import eu.eco2clouds.accounting.service.HostDataDAO;
import eu.eco2clouds.accounting.service.TestbedDAO;
import eu.eco2clouds.accounting.testbedclient.Client;

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
 * @author: David Garcia Perez. Atos Research and Innovation, Atos SPAIN SA
 * e-mail david.garciaperez@atos.net
 */

public class AccountingServiceTestbed extends AccountingServiceAbstractImpl {
	private static Logger logger = Logger.getLogger(AccountingServiceTestbed.class);
	private Client client;
	private int actualCO2Index = 1;
	private static long PERIOD = 9600000l;
	
	/** Used to abstract database functions from the collector itself. */
	public DatabaseConnector dbConnector = new DatabaseConnector(
			Configuration.mysqlCollectorHost,
			Configuration.mysqlCollectorUsername,
			Configuration.mysqlCollectorPassword);

	public static Hashtable<String, String> itemNames = new Hashtable<String, String>();

	/** A statement object used to prepare SQL transactions. */
	private PreparedStatement statement = null;

	/** The results returned by SQL queries. */
	private ResultSet results = null;

	public AccountingServiceTestbed() {
		setItemsName();
	}

	public AccountingServiceTestbed(TestbedDAO testbedDAO) {
		this.testbedDAO = testbedDAO;
		setItemsName();
	}

	public AccountingServiceTestbed(TestbedDAO testbedDAO, HostDAO hostDAO, HostDataDAO hostDataDAO) {
		this.testbedDAO = testbedDAO;
		this.hostDAO = hostDAO;
		this.hostDataDAO = hostDataDAO;
		setItemsName();
	}

	public AccountingServiceTestbed(TestbedDAO testbedDAO, HostDAO hostDAO,
			HostDataDAO hostDataDAO, Client client) {
		this.testbedDAO = testbedDAO;
		this.hostDAO = hostDAO;
		this.hostDataDAO = hostDataDAO;
		this.client = client;
		setItemsName();
	}

	public AccountingServiceTestbed(TestbedDAO testbedDAO, Client client) {
		this.testbedDAO = testbedDAO;
		this.client = client;
		setItemsName();
	}

	private void setItemsName() {
		
		//Testbeds

		//|  8528005 |           |        103442 | 1401978242 |     57575 | MW    |
		//|  8528003 |       |        106582 | 1401978142 |         0 | %     |


		//itemNames.put("Co2 producted per kWh", "Co2");
		//itemNames.put("Coal power", "Coal");
		//itemNames.put("GaZ", "Gaz");
		//itemNames.put("Gaz power", "Gaz");
		//itemNames.put("Hydraulic power", "Hydraulic");
		//itemNames.put("Nuclear power", "Nuclear");
		//itemNames.put("Total power", "Total");
		//itemNames.put("Cost", "Cost");
		//itemNames.put("PDU fr", "Pdu");
		//itemNames.put("CO2 generation rate", "CO2GenerateRate");
		itemNames.put("Processor load", "ProcessorLoad");
		itemNames.put("Free memory", "FreeMemory");
		itemNames.put("Total memory", "TotalMemory");
		itemNames.put("Free swap space", "FreeSwapSpace");
		itemNames.put("Number of VMs running", "NumberOfVMsRunning");
		itemNames.put("Aggregate energy", "AggregateEnergy");
		itemNames.put("Apparent power", "ApparentPower");
		itemNames.put("Real power", "RealPower");
		itemNames.put("Number of processes", "NumberOfProcesses");
		itemNames.put("CPU user time avg1", "CPUUserTimeAvg1");
		itemNames.put("Running VMs", "RunningVMs");
		itemNames.put("Co2 producted", "Co2Producted");
		itemNames.put("Aggregate energy usage", "AggregateEnergyUsage");
		itemNames.put("Apparent power usage", "ApparentPowerUsage");
		itemNames.put("Real power usage", "RealPowerUsage");
		
		//new metrics
		//testbed
		//itemNames.put("Availability", "Availability");
		itemNames.put("Biomass", "Biomass");
		itemNames.put("CCGT", "CCGT");
		itemNames.put("Center", "Center");
		itemNames.put("Co2", "Co2");
		itemNames.put("Coal", "Coal");
		itemNames.put("Cogeneration", "Cogeneration");
		itemNames.put("Exported", "Exported");
		itemNames.put("Fossil", "Fossil");
		itemNames.put("Gas", "Gaz");
		itemNames.put("Geothermal", "Geothermal");
		itemNames.put("Hydraulic", "Hydraulic");
		itemNames.put("Imported", "Imported");
		itemNames.put("NPSHydro", "NPSHydro");
		itemNames.put("Nuclear", "Nuclear");
		itemNames.put("OCGT", "OCGT");
		itemNames.put("Oil", "Oil");
		itemNames.put("Other", "Other");
		itemNames.put("PUE", "PUE");
		itemNames.put("SiteUtilization", "SiteUtilization");
		itemNames.put("Solar", "Solar");
		itemNames.put("StorageUtilization", "StorageUtilization");
		itemNames.put("PumpedStorage", "PumpedStorage");
		itemNames.put("GridTotal", "Total");
		itemNames.put("TotalGreen", "TotalGreen");
		itemNames.put("Water", "Water");
		itemNames.put("Wind", "Wind");
		
		// Attention to this, not sure if it is correct:
		
		//epcc
		//itemNames.put("uk_grid_const[biomass]", "Biomass");
		//itemNames.put("uk_co2[]", "Co2");
		//itemNames.put("uk_grid_const[cogeneration]", "Cogeneration");
		//itemNames.put("uk_grid_const[exported]", "Exported");
		//itemNames.put("uk_grid_const[fossil]", "Fossil");
		//itemNames.put("uk_grid_const[gas]", "Gaz");
		//itemNames.put("uk_grid_const[geothermal]", "Geothermal");
		//itemNames.put("uk_grid_const[hydraulic]", "Hydraulic");
		//itemNames.put("uk_grid_const[solar]", "Solar");
		//itemNames.put("uk_grid_const[water]", "Water");
		//itemNames.put("uk_grid_extract[CCGT]", "CCGT");
		//itemNames.put("uk_grid_extract[COAL]", "Coal");
		//itemNames.put("uk_grid_extract[NPSHYD]", "NPSHydro");
		//itemNames.put("uk_grid_extract[NUCLEAR]", "Nuclear");
		//itemNames.put("uk_grid_extract[OCGT]", "OCGT");
		//itemNames.put("uk_grid_extract[OIL]", "Oil");
		//itemNames.put("uk_grid_extract[OTHER]", "Other");
		//itemNames.put("uk_grid_extract[PS]", "PS");
		//itemNames.put("uk_grid_extract[WIND]", "Wind");
		//itemNames.put("uk_grid_total", "Total");
		//itemNames.put("uk_grid_total_green", "TotalGreen");
		//itemNames.put("uk_grid_total_import", "Imported");
		//itemNames.put("one.availability", "Availability");
		//itemNames.put("one.utilization.cpu", "OneCpuUtilization");
		//itemNames.put("power.total.center", "Center");
		//itemNames.put("pue", "PUE");
		
		//inria
		//itemNames.put("french_grid_extract[Autres]", "Other");
		//itemNames.put("french_grid_extract[Charbon]", "Coal");
		//itemNames.put("french_grid_extract[CO2]", "Co2");
		//itemNames.put("french_grid_extract[Eolien]", "Wind");
		//itemNames.put("french_grid_extract[Export]", "Exported");
		//itemNames.put("french_grid_extract[Fioul]", "Oil");
		//itemNames.put("french_grid_extract[Gaz]", "Gaz");
		//itemNames.put("french_grid_extract[Hydraulique]", "Hydraulic");
		//itemNames.put("french_grid_extract[Import]", "Imported");
		//itemNames.put("french_grid_extract[Nucl?aire]", "Nuclear");
		//itemNames.put("french_grid_extract[Solaire]", "Solar");
		//itemNames.put("french_grid_extract[TOTALGREEN]", "TotalGreen");
		//itemNames.put("french_grid_extract[TOTAL]", "Total");
		//itemNames.put("location.pue", "pue");
		
		//HLRS
		//itemNames.put("power_extract[Biomass]", "Biomass");
		//itemNames.put("power_extract[CCGT]", "CCGT");
		//itemNames.put("power.total.center", "Center");
		//itemNames.put("power_extract[CO2]", "Co2");
		//itemNames.put("power_extract[Coal]","Coal");
		//itemNames.put("power_extract[Cogeneration]", "Cogeneration");
		//itemNames.put("power_extract[Exported]", "Exported");
		//itemNames.put("power_extract[Fossil]", "Fossil");
		//itemNames.put("power_extract[Gas]", "Gaz");
		//itemNames.put("power_extract[Geothermal]", "Geothermal");
		//itemNames.put("power_extract[Hydraulic]", "Hydraulic");
		//itemNames.put("power_extract[Imported]", "Imported");
		//itemNames.put("power_extract[NPS]", "NPSHydro");
		//itemNames.put("power_extract[Nuclear]", "Nuclear");
		//itemNames.put("power_extract[OCGT]", "OCGT");
		//itemNames.put("power_extract[Oil]", "Oil");
		//itemNames.put("power_extract[Other]", "Other");
		//itemNames.put("power_extract[Solar]", "Solar");
		//itemNames.put("power_extract[Total]", "Total");
		//itemNames.put("power_extract[GreenTotal]", "TotalGreen");
		//itemNames.put("power_extract[Water]", "Water");
		//itemNames.put("power_extract[Wind]", "Wind");
		
		//host
		itemNames.put("Storage utilization", "StorageUtilization");
		itemNames.put("CO2 generation per 30s", "Co2GenerationPer30s");
		itemNames.put("CPU Utilization", "CPUUtilization");
		itemNames.put("Disk IOPS", "DiskIOPS");
		itemNames.put("Free space on srv", "FreeSpaceOnSrv");
		itemNames.put("Power consumption", "PowerConsumption");
		// new new...
		itemNames.put("co2.raw", "Co2Raw");
		itemNames.put("disk.read.write", "DiskReadWrite");
		itemNames.put("host.availability", "HostAvailability");
		itemNames.put("power.currentva.aggregated", "PowerCurrentvaAggregated");
		itemNames.put("power.currentw.aggregated", "PowerCurrentWAggregated");
		itemNames.put("power.total.aggregated", "PowerTotalAggregated");
		itemNames.put("system.cpu.load[,avg1]", "SystemCpuLoad");
		itemNames.put("system.cpu.util.perc", "SystemCpuUtilPerc");
		itemNames.put("system.power.consumption", "SystemPowerConsumption");
		itemNames.put("system.swap.size[,free]", "SystemSwapSizeFree");
		itemNames.put("vm.memory.size[free]", "VMMemorySizeFree");
		itemNames.put("vm.memory.size[total]", "VMMemorySizeTotal");
		itemNames.put("custom.cpu.utilization", "CustomCPUUtilization");
		itemNames.put("custom.vfs.iops", "CustonVfsIops");
		itemNames.put("custom.vms.running", "CustomVMSRunning");
		itemNames.put("one.availability", "OneAvailability");
		itemNames.put("power.co2.generated", "PowerCo2Generated");
		itemNames.put("power.consumption", "PowerConsumption");
		itemNames.put("power.currentwh.real", "PowerCurrentWhReal");
		itemNames.put("proc.num[]", "ProcNum");
		itemNames.put("Running VMs", "RunningVMs");
		itemNames.put("system.cpu.util[,user,avg1]", "SystemCpuUtil");
		//Host probably final ones... 
		itemNames.put("Availability", "Availability");                
		itemNames.put("co2g", "co2g");
		itemNames.put("consva", "PowerCurrentvaAggregated");
		itemNames.put("consw", "consw");  
		itemNames.put("conswh", "PowerCurrentWhReal");
		itemNames.put("freespacesrv", "FreeSpaceOnSrv");
		itemNames.put("IOPS", "DiskIOPS");
		itemNames.put("memfree", "VMMemorySizeFree");
		itemNames.put("memtotal", "VMMemorySizeTotal");
		itemNames.put("PowerConsumption", "PowerConsumption");
		itemNames.put("procnum", "procnum");
		itemNames.put("runningvm", "RunningVMs");
		itemNames.put("swapfree", "swapfree");
		itemNames.put("cpuload", "SystemCpuLoad");
		itemNames.put("cpuutil", "cpuutil");
		itemNames.put("cpuUtilization", "cpuUtilization");              
		
		//vm
		itemNames.put("cpuload", "cpuload");
		itemNames.put("cpuutil", "cpuutil");
		itemNames.put("diskfree", "diskfree");
		itemNames.put("disktotal", "disktotal");
		itemNames.put("diskusage", "diskusage");
		itemNames.put("iops", "iops");
		itemNames.put("ioutil", "ioutil");
		itemNames.put("memfree", "memfree");
		itemNames.put("memtotal", "memtotal");
		itemNames.put("memused", "memused");
		itemNames.put("netifin", "netifin");
		itemNames.put("netifout", "netifout");
		itemNames.put("power", "power");
		itemNames.put("procnum", "procnum");
		itemNames.put("swapfree", "swapfree");
		itemNames.put("swaptotal", "swaptotal");
		itemNames.put("applicationmetric_1", "applicationmetric_1");
		itemNames.put("applicationmetric_2", "applicationmetric_2");
		itemNames.put("applicationmetric_3", "applicationmetric_3");
		itemNames.put("applicationmetric_4", "applicationmetric_4");
	}

	public Testbed getTestbed(String name) {
		logger.debug("Looking for the testbed: " + name + " in the database...");

		return testbedDAO.getByName(name);
	}

	public String getListOfTesbeds() {
		logger.debug("Getting the list of testbeds from the database");

		List<Testbed> testbeds = testbedDAO.getAll();
		
		Collection collection = new Collection();
		Items items = new Items();
		items.setOffset(0);
		items.setTotal(testbeds.size());
		collection.setItems(items);
		List<eu.eco2clouds.accounting.datamodel.parser.Testbed> testbedsXML = new ArrayList<eu.eco2clouds.accounting.datamodel.parser.Testbed>();
		
		
		for(Testbed testbed : testbeds) {
			eu.eco2clouds.accounting.datamodel.parser.Testbed testbedXML = ModelConversion.convertTestbed(testbed);
			setTestbedLinks(testbedXML);
			testbedsXML.add(testbedXML);
		}
		
		items.setTestbeds(testbedsXML);
		Link linkParent = new Link();
		linkParent.setHref("/");
		linkParent.setRel("parent");
		linkParent.setType(CONTENT_TYPE_ECO2CLOUDS_XML);
		ArrayList<Link> links = new ArrayList<Link>();
		links.add(linkParent);
		collection.setLinks(links);
		
		logger.debug("Found " + testbeds.size() + " in the database");

		String testbedsStringXML = "";
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Collection .class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			marshaller.marshal(collection, out);
			testbedsStringXML = out.toString();

			logger.debug("Testbed Monitoring data: " + testbedsStringXML);

		} catch (JAXBException jaxbExpcetion) {
			logger.debug("Error trying to marshall TestbedMonitoring XML " + jaxbExpcetion.getStackTrace());
		}

		return testbedsStringXML;
	}
	
	private void setTestbedLinks(eu.eco2clouds.accounting.datamodel.parser.Testbed testbed) {
		Link linkParent = new Link();
		linkParent.setHref("/");
		linkParent.setRel("parent");
		linkParent.setType(CONTENT_TYPE_ECO2CLOUDS_XML);
		Link linkStatus = new Link();
		linkStatus.setHref("/testbeds/"+ testbed.getName() + "/status");
		linkStatus.setRel("status");
		linkStatus.setType(CONTENT_TYPE_ECO2CLOUDS_XML);
		Link linkMonitoring = new Link();
		linkMonitoring.setHref("/testbeds/"+ testbed.getName() + "/monitoring");
		linkMonitoring.setRel("monitoring");
		linkMonitoring.setType(CONTENT_TYPE_ECO2CLOUDS_XML);
		Link linkHosts = new Link();
		linkHosts.setHref("/testbeds/"+ testbed.getName() + "/hosts");
		linkHosts.setRel("hosts");
		linkHosts.setType(CONTENT_TYPE_ECO2CLOUDS_XML);
		Link linkSelf = new Link();
		linkSelf.setHref("/testbeds/"+ testbed.getName());
		linkSelf.setRel("self");
		linkSelf.setType(CONTENT_TYPE_ECO2CLOUDS_XML);
		ArrayList<Link> links = new ArrayList<Link>();
		links.add(linkParent);
		links.add(linkSelf);
		links.add(linkStatus);
		links.add(linkMonitoring);
		links.add(linkHosts);
		
		testbed.setLinks(links);
	}

	/**
	 * Returns a testbed XML by the name, null otherwise
	 * 
	 * @param name
	 * @return
	 */
	public String getTestbedByName(String name) {
		eu.eco2clouds.accounting.datamodel.parser.Testbed testbed = ModelConversion.convertTestbed(getTestbed(name));

		if (testbed == null)
			return null;
		
		setTestbedLinks(testbed);
		
		String testbedXML = "";
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(eu.eco2clouds.accounting.datamodel.parser.Testbed .class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			marshaller.marshal(testbed, out);
			testbedXML = out.toString();

			logger.debug("Testbed Monitoring data: " + testbedXML);

		} catch (JAXBException jaxbExpcetion) {
			logger.debug("Error trying to marshall TestbedMonitoring XML " + jaxbExpcetion.getStackTrace());
		}

		return testbedXML;
	}

	/**
	 * Returns the testbed host info provided by OpenNebula
	 * 
	 * @param testbed
	 *            from which we want to get the info
	 * @return XML file with the info details
	 */
	public String getTestbedHostsStatusInfoService(String name, Action action) {

		Testbed testbed = getTestbed(name);
		String hostInfoString = null;
		UpdateHostsThread updateHostsThread;

		if (testbed == null)
			return null;
		else {

			hostInfoString = client.getHostInfo(testbed);

			if (hostInfoString != null) {

				updateHostsThread = new UpdateHostsThread(testbedDAO, hostDAO, hostDataDAO, name, hostInfoString);
				updateHostsThread.start();
			}

			return hostInfoString;
		}
	}

	/**
	 * Returns the testbed host info provided by OpenNebula
	 * 
	 * @param testbed
	 *            from which we want to get the info
	 * @return XML file with the info details
	 */
	public String getTestbedsHostsStatusInfoService(List<Testbed> testbeds, Action action) {

		// Testbed testbed = getTestbed(name);
		String hostInfoString = null;
		UpdateTestbedsHostsThread updateTestbedsHostsThread;

		if (testbeds == null)
			return null;
		else {

			updateTestbedsHostsThread = new UpdateTestbedsHostsThread( testbedDAO, hostDAO, hostDataDAO, testbeds, hostInfoString,action);
			updateTestbedsHostsThread.start();

		}

		return hostInfoString;
	}

	public String printTestbedMonitoring(ResultSet rs, String location) {

		TestbedMonitoring testbedMonitoring = new TestbedMonitoring();
		String testbedMonitoringXML = "";

		try {
			if (rs.next()) {
				ResultSetMetaData rsmd = rs.getMetaData();
				 logger.debug("Colummn names: " + rsmd.getColumnName(1) + " , " + rsmd.getColumnName(2) + " , " + rsmd.getColumnName(3) + " , " 
						 + rsmd.getColumnName(4) + " , " + rsmd.getColumnName(5) + " , ");
				
				do {
					if (rs.isFirst()) {
						testbedMonitoring.setHref("/testbeds/" + location + "/monitoring");
						Link link = new Link();
						link.setRel("testbed");
						link.setHref("/testbeds/" + location);
						List<Link> links = new ArrayList<Link>();
						links.add(link);
						testbedMonitoring.setLinks(links);
					}

					String name = rs.getString("name");
					if(itemNames.get(name) != null ) {
						if (itemNames.get(name).equals("Co2")) {
							Co2 co2 = new Co2();
							co2.setName(name);
							co2.setClock(Long.parseLong(rs.getString("clock")));
							co2.setUnity(rs.getString("unity"));
							co2.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setCo2(co2);
						} else if (itemNames.get(name).equals("HostAvailability")) {
							Availability availability = new Availability();
							availability.setName(name);
							availability.setClock(Long.parseLong(rs.getString("clock")));
							availability.setUnity(rs.getString("unity"));
							availability.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setAvailability(availability);
						} else if (itemNames.get(name).equals("PumpedStorage")) {
							PumpedStorage pumpedStorage = new PumpedStorage();
							pumpedStorage.setName(name);
							pumpedStorage.setClock(Long.parseLong(rs.getString("clock")));
							pumpedStorage.setUnity(rs.getString("unity"));
							pumpedStorage.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setPumpedStorage(pumpedStorage);
						} else if (itemNames.get(name).equals("Coal")) {
							Coal coal = new Coal();
							coal.setName(name);
							coal.setClock(Long.parseLong(rs.getString("clock")));
							coal.setUnity(rs.getString("unity"));
							coal.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setCoal(coal);
						} else if (itemNames.get(name).equals("Cost")) {
							Cost cost = new Cost();
							cost.setName(name);
							cost.setClock(Long.parseLong(rs.getString("clock")));
							cost.setUnity(rs.getString("unity"));
							cost.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setCost(cost);
						} else if (itemNames.get(name).equals("Gaz")) {
							Gaz gaz = new Gaz();
							gaz.setName(name);
							gaz.setClock(Long.parseLong(rs.getString("clock")));
							gaz.setUnity(rs.getString("unity"));
							gaz.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setGaz(gaz);
						} else if (itemNames.get(name).equals("Hydraulic")) {
							Hydraulic hydraulic = new Hydraulic();
							hydraulic.setName(name);
							hydraulic.setClock(Long.parseLong(rs.getString("clock")));
							hydraulic.setUnity(rs.getString("unity"));
							hydraulic.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setHydraulic(hydraulic);
						} else if (itemNames.get(name).equals("Nuclear")) {
							Nuclear nuclear = new Nuclear();
							nuclear.setName(name);
							nuclear.setClock(Long.parseLong(rs.getString("clock")));
							nuclear.setUnity(rs.getString("unity"));
							nuclear.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setNuclear(nuclear);
						} else if (itemNames.get(name).equals("Oil")) {
							Oil oil = new Oil();
							oil.setName(name);
							oil.setClock(Long.parseLong(rs.getString("clock")));
							oil.setUnity(rs.getString("unity"));
							oil.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setOil(oil);
						} else if (itemNames.get(name).equals("Other")) {
							Other other = new Other();
							other.setName(name);
							other.setClock(Long.parseLong(rs.getString("clock")));
							other.setUnity(rs.getString("unity"));
							other.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setOther(other);
						} else if (itemNames.get(name).equals("Pdu")) {
							PDUFr pDUFr = new PDUFr();
							pDUFr.setName(name);
							pDUFr.setClock(Long.parseLong(rs.getString("clock")));
							pDUFr.setUnity(rs.getString("unity"));
							pDUFr.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setpDUFr(pDUFr);
						} else if (itemNames.get(name).equals("Total")) {
							Total total = new Total();
							total.setName(name);
							total.setClock(Long.parseLong(rs.getString("clock")));
							total.setUnity(rs.getString("unity"));
							total.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setTotal(total);
						} else if (itemNames.get(name).equals("Wind")) {
							Wind wind = new Wind();
							wind.setName(name);
							wind.setClock(Long.parseLong(rs.getString("clock")));
							wind.setUnity(rs.getString("unity"));
							wind.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setWind(wind);
						}
						//new metrics
						else if (itemNames.get(name).equals("Availability")) {
							Availability availability = new Availability();
							availability.setName(name);
							availability.setClock(Long.parseLong(rs.getString("clock")));
							availability.setUnity(rs.getString("unity"));
							availability.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setAvailability(availability);
						}
						else if (itemNames.get(name).equals("Biomass")) {
							Biomass biomass = new Biomass();
							biomass.setName(name);
							biomass.setClock(Long.parseLong(rs.getString("clock")));
							biomass.setUnity(rs.getString("unity"));
							biomass.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setBiomass(biomass);
						}
						else if (itemNames.get(name).equals("CCGT")) {
							CCGT ccgt = new CCGT();
							ccgt.setName(name);
							ccgt.setClock(Long.parseLong(rs.getString("clock")));
							ccgt.setUnity(rs.getString("unity"));
							ccgt.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setCcgt(ccgt);;
						}
						else if (itemNames.get(name).equals("Cogeneration")) {
							Cogeneration cogeneration = new Cogeneration();
							cogeneration.setName(name);
							cogeneration.setClock(Long.parseLong(rs.getString("clock")));
							cogeneration.setUnity(rs.getString("unity"));
							cogeneration.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setCogeneration(cogeneration);
						}
						else if (itemNames.get(name).equals("Exported")) {
							Exported exported = new Exported();
							exported.setName(name);
							exported.setClock(Long.parseLong(rs.getString("clock")));
							exported.setUnity(rs.getString("unity"));
							exported.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setExported(exported);
						}
						else if (itemNames.get(name).equals("Fossil")) {
							Fossil fossil = new Fossil();
							fossil.setName(name);
							fossil.setClock(Long.parseLong(rs.getString("clock")));
							fossil.setUnity(rs.getString("unity"));
							fossil.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setFossil(fossil);
						}
						else if (itemNames.get(name).equals("Geothermal")) {
							Geothermal geothermal = new Geothermal();
							geothermal.setName(name);
							geothermal.setClock(Long.parseLong(rs.getString("clock")));
							geothermal.setUnity(rs.getString("unity"));
							geothermal.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setGeothermal(geothermal);
						}
						else if (itemNames.get(name).equals("Imported")) {
							Imported imported = new Imported();
							imported.setName(name);
							imported.setClock(Long.parseLong(rs.getString("clock")));
							imported.setUnity(rs.getString("unity"));
							imported.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setImported(imported);
						}
						else if (itemNames.get(name).equals("NPSHydro")) {
							NPSHydro npsHydro = new NPSHydro();
							npsHydro.setName(name);
							npsHydro.setClock(Long.parseLong(rs.getString("clock")));
							npsHydro.setUnity(rs.getString("unity"));
							npsHydro.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setNpsHydro(npsHydro);						
						}
						else if (itemNames.get(name).equals("OCGT")) {
							OCGT ocgt = new OCGT();
							ocgt.setName(name);
							ocgt.setClock(Long.parseLong(rs.getString("clock")));
							ocgt.setUnity(rs.getString("unity"));
							ocgt.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setOcgt(ocgt);
						}
						else if (itemNames.get(name).equals("PUE")) {
							PUE pue = new PUE();
							pue.setName(name);
							pue.setClock(Long.parseLong(rs.getString("clock")));
							pue.setUnity(rs.getString("unity"));
							pue.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setPue(pue);
						}
						else if (itemNames.get(name).equals("SiteUtilization")) {
							SiteUtilization siteUtilization = new SiteUtilization();
							siteUtilization.setName(name);
							siteUtilization.setClock(Long.parseLong(rs.getString("clock")));
							siteUtilization.setUnity(rs.getString("unity"));
							siteUtilization.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setSiteUtilization(siteUtilization);
						}
						else if (itemNames.get(name).equals("Solar")) {
							Solar solar = new Solar();
							solar.setName(name);
							solar.setClock(Long.parseLong(rs.getString("clock")));
							solar.setUnity(rs.getString("unity"));
							solar.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setSolar(solar);
						}
						else if (itemNames.get(name).equals("StorageUtilization")) {
							StorageUtilization storageUtilization = new StorageUtilization();
							storageUtilization.setName(name);
							storageUtilization.setClock(Long.parseLong(rs.getString("clock")));
							storageUtilization.setUnity(rs.getString("unity"));
							storageUtilization.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setStorageUtilization(storageUtilization);
						}
						else if (itemNames.get(name).equals("TotalGreen")) {
							TotalGreen totalGreen = new TotalGreen();
							totalGreen.setName(name);
							totalGreen.setClock(Long.parseLong(rs.getString("clock")));
							totalGreen.setUnity(rs.getString("unity"));
							totalGreen.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setTotalGreen(totalGreen);
						}
						else if (itemNames.get(name).equals("Water")) {
							Water water = new Water();
							water.setName(name);
							water.setClock(Long.parseLong(rs.getString("clock")));
							water.setUnity(rs.getString("unity"));
							water.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setWater(water);
						}
						else if (itemNames.get(name).equals("OneCpuUtilization")) {
							OneUtilizationCpu oneUtilizationCpu = new OneUtilizationCpu();
							oneUtilizationCpu.setName(name);
							oneUtilizationCpu.setClock(Long.parseLong(rs.getString("clock")));
							oneUtilizationCpu.setUnity(rs.getString("unity"));
							oneUtilizationCpu.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setOneUtlizationCpu(oneUtilizationCpu);
						}
						else if (itemNames.get(name).equals("PS")) {
							Ps ps = new Ps();
							ps.setName(name);
							ps.setClock(Long.parseLong(rs.getString("clock")));
							ps.setUnity(rs.getString("unity"));
							ps.setValue(Double.parseDouble(rs.getString("value")));
							testbedMonitoring.setPs(ps);
						}
				}

				} while (rs.next());
			}
		} catch (SQLException e) {
			logger.debug("" + e.getStackTrace());
		}

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(TestbedMonitoring.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			marshaller.marshal(testbedMonitoring, out);
			testbedMonitoringXML = out.toString();

			logger.debug("Testbed Monitoring data: " + testbedMonitoringXML);

		} catch (JAXBException jaxbExpcetion) {
			logger.debug("Error trying to marshall TestbedMonitoring XML " + jaxbExpcetion.getStackTrace());
		}

		return testbedMonitoringXML;
	}	
	
	public String printHostMonitoring(ResultSet rs, String testbed, String location) {

		HostMonitoring hostMonitoring = new HostMonitoring();
		String hostMonitoringXML = "";
		
		try {
			
			 ResultSetMetaData rsmd = rs.getMetaData();
			 logger.debug("Colummn names: " + rsmd.getColumnName(1) + " , " + rsmd.getColumnName(2) + " , " + rsmd.getColumnName(3) + " , " 
					 + rsmd.getColumnName(4) + " , " + rsmd.getColumnName(5) + " , ");
			
			 
			if (rs.next()) {
				do {
					if (rs.isFirst()) {
						logger.debug("Creating links...");
						hostMonitoring.setHref("/testbeds/" + testbed + "/hosts/"
								+ location + "/monitoring");
						Link link = new Link();
						link.setRel("testbed");
						link.setHref("/testbeds/" + testbed);
						List<Link> links = new ArrayList<Link>();
						links.add(link);
						hostMonitoring.setLinks(links);
						
					}
				
					//analyze nodes
					String name = rs.getString("name");
					logger.debug("Item name: " + name + " itemNames.get: " + itemNames.get(name));
					if(itemNames.get(name) != null ) {
						if (itemNames.get(name).equals("CO2GenerateRate")) {
							CO2GenerationRate co2GenerateRate = new CO2GenerationRate();
							co2GenerateRate.setName(name);
							co2GenerateRate.setClock(Long.parseLong(rs.getString("clock")));
							co2GenerateRate.setUnity(rs.getString("unity"));
							co2GenerateRate.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setCo2GenerationRate(co2GenerateRate);
						} else if (itemNames.get(name).equals("ProcessorLoad")) {
							ProcessorLoad processorLoad = new ProcessorLoad();
							processorLoad.setName(name);
							processorLoad.setClock(Long.parseLong(rs.getString("clock")));
							processorLoad.setUnity(rs.getString("unity"));
							processorLoad.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setProcessorLoad(processorLoad);
						} else if (itemNames.get(name).equals("memfree")) {
							FreeMemory freeMemory = new FreeMemory();
							freeMemory.setName(name);
							freeMemory.setClock(Long.parseLong(rs.getString("clock")));
							freeMemory.setUnity(rs.getString("unity"));
							freeMemory.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setFreeMemory(freeMemory);
						} else if (itemNames.get(name).equals("memtotal")) {
							TotalMemory totalMemory = new TotalMemory();
							totalMemory.setName(name);
							totalMemory.setClock(Long.parseLong(rs.getString("clock")));
							totalMemory.setUnity(rs.getString("unity"));
							totalMemory.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setTotalMemory(totalMemory);
						} else if (itemNames.get(name).equals("swapfree")) {
							FreeSwapSpace freeSwapSpace = new FreeSwapSpace();
							freeSwapSpace.setName(name);
							freeSwapSpace.setClock(Long.parseLong(rs.getString("clock")));
							freeSwapSpace.setUnity(rs.getString("unity"));
							freeSwapSpace.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setFreeSwapSpace(freeSwapSpace);
						} else if (itemNames.get(name).equals("NumberOfVMsRunning")) {
							NumberOfVMsRunning numberOfVMsRunning = new NumberOfVMsRunning();
							numberOfVMsRunning.setName(name);
							numberOfVMsRunning.setClock(Long.parseLong(rs.getString("clock")));
							numberOfVMsRunning.setUnity(rs.getString("unity"));
							numberOfVMsRunning.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setNumberOfVMsRunning(numberOfVMsRunning);
						} else if (itemNames.get(name).equals("AggregateEnergy")) {
							AggregateEnergy aggregateEnergy = new AggregateEnergy();
							aggregateEnergy.setName(name);
							aggregateEnergy.setClock(Long.parseLong(rs.getString("clock")));
							aggregateEnergy.setUnity(rs.getString("unity"));
							aggregateEnergy.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setAggregateEnergy(aggregateEnergy);
						} else if (itemNames.get(name).equals("AggregatePower")) {
							ApparentPower apparentPower = new ApparentPower();
							apparentPower.setName(name);
							apparentPower.setClock(Long.parseLong(rs.getString("clock")));
							apparentPower.setUnity(rs.getString("unity"));
							apparentPower.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setApparentPower(apparentPower);
						} else if (itemNames.get(name).equals("RealPower")) {
							RealPower realPower = new RealPower();
							realPower.setName(name);
							realPower.setClock(Long.parseLong(rs.getString("clock")));
							realPower.setUnity(rs.getString("unity"));
							realPower.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setRealPower(realPower);
						} else if (itemNames.get(name).equals("NumberOfProcesses")) {
							NumberOfProcesses numberOfProcesses = new NumberOfProcesses();
							numberOfProcesses.setName(name);
							numberOfProcesses.setClock(Long.parseLong(rs.getString("clock")));
							numberOfProcesses.setUnity(rs.getString("unity"));
							numberOfProcesses.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setNumberOfProcesses(numberOfProcesses);
						} else if (itemNames.get(name).equals("CPUUserTimeAvg1")) {
							CPUUserTimeAvg1 cpuUserTimeAvg1 = new CPUUserTimeAvg1();
							cpuUserTimeAvg1.setName(name);
							cpuUserTimeAvg1.setClock(Long.parseLong(rs.getString("clock")));
							cpuUserTimeAvg1.setUnity(rs.getString("unity"));
							cpuUserTimeAvg1.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setCpuUserTimeAvg1(cpuUserTimeAvg1);
						} else if (itemNames.get(name).equals("RunningVMs")) {
							RunningVMs runningVMs = new RunningVMs();
							runningVMs.setName(name);
							runningVMs.setClock(Long.parseLong(rs.getString("clock")));
							runningVMs.setUnity(rs.getString("unity"));
							runningVMs.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setRunningVMs(runningVMs);
						} else if (itemNames.get(name).equals("co2g")) {
							Co2Producted co2Producted = new Co2Producted();
							co2Producted.setName(name);
							co2Producted.setClock(Long.parseLong(rs.getString("clock")));
							co2Producted.setUnity(rs.getString("unity"));
							co2Producted.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setCo2Producted(co2Producted);
						} else if (itemNames.get(name).equals("AggregateEnergyUsage")) {
							AggregateEnergyUsage aggregateEnergyUsage = new AggregateEnergyUsage();
							aggregateEnergyUsage.setName(name);
							aggregateEnergyUsage.setClock(Long.parseLong(rs.getString("clock")));
							aggregateEnergyUsage.setUnity(rs.getString("unity"));
							aggregateEnergyUsage.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setAggregateEnergyUsage(aggregateEnergyUsage);
						} else if (itemNames.get(name).equals("ApparentEnergyUsage")) {
							ApparentPowerUsage apparentPowerUsage = new ApparentPowerUsage();
							apparentPowerUsage.setName(name);
							apparentPowerUsage.setClock(Long.parseLong(rs.getString("clock")));
							apparentPowerUsage.setUnity(rs.getString("unity"));
							apparentPowerUsage.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setApparentPowerUsage(apparentPowerUsage);
						} else if (itemNames.get(name).equals("RealPowerUsage")) {
							RealPowerUsage realPowerUsage = new RealPowerUsage();
							realPowerUsage.setName(name);
							realPowerUsage.setClock(Long.parseLong(rs.getString("clock")));
							realPowerUsage.setUnity(rs.getString("unity"));
							realPowerUsage.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setRealPowerUsage(realPowerUsage);
						} 
						//new metrics
						else if (itemNames.get(name).equals("Availability")) {
							Availability availability = new Availability();
							availability.setName(name);
							availability.setClock(Long.parseLong(rs.getString("clock")));
							availability.setUnity(rs.getString("unity"));
							availability.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setAvailability(availability);
						} 
						else if (itemNames.get(name).equals("Co2GenerationPer30s")) {
							Co2GenerationPer30s co2GenerationPer30s = new Co2GenerationPer30s();
							co2GenerationPer30s.setName(name);
							co2GenerationPer30s.setClock(Long.parseLong(rs.getString("clock")));
							co2GenerationPer30s.setUnity(rs.getString("unity"));
							co2GenerationPer30s.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setCo2GenerationPer30s(co2GenerationPer30s);
						} 
						else if (itemNames.get(name).equals("cpuUtilization")) {
							CPUUtilization cpuUtilization = new CPUUtilization();
							cpuUtilization.setName(name);
							cpuUtilization.setClock(Long.parseLong(rs.getString("clock")));
							cpuUtilization.setUnity(rs.getString("unity"));
							cpuUtilization.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setCpuUtilization(cpuUtilization);
						} 
						else if (itemNames.get(name).equals("DiskIOPS")) {
							DiskIOPS diskIOPS = new DiskIOPS();
							diskIOPS.setName(name);
							diskIOPS.setClock(Long.parseLong(rs.getString("clock")));
							diskIOPS.setUnity(rs.getString("unity"));
							diskIOPS.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setDiskIOPS(diskIOPS);
						} 
						else if (itemNames.get(name).equals("FreeSpaceOnSrv")) {
							FreeSpaceOnSrv freeSpaceOnSrv = new FreeSpaceOnSrv();
							freeSpaceOnSrv.setName(name);
							freeSpaceOnSrv.setClock(Long.parseLong(rs.getString("clock")));
							freeSpaceOnSrv.setUnity(rs.getString("unity"));
							freeSpaceOnSrv.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setFreeSpaceOnSrv(freeSpaceOnSrv);
						}
						else if (itemNames.get(name).equals("PowerConsumption")) {
							PowerConsumption powerConsumption = new PowerConsumption();
							powerConsumption.setName(name);
							powerConsumption.setClock(Long.parseLong(rs.getString("clock")));
							powerConsumption.setUnity(rs.getString("unity"));
							powerConsumption.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setPowerConsumption(powerConsumption);
						}
						else if(itemNames.get(name).equals("Co2Raw")) {
							Co2Raw co2Raw = new Co2Raw();
							co2Raw.setName(name);
							co2Raw.setClock(Long.parseLong(rs.getString("clock")));
							co2Raw.setUnity(rs.getString("unity"));
							co2Raw.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setCo2Raw(co2Raw);
						}
						else if(itemNames.get(name).equals("DiskReadWrite")) {
							DiskReadWrite diskReadWrite = new DiskReadWrite();
							diskReadWrite.setName(name);
							diskReadWrite.setClock(Long.parseLong(rs.getString("clock")));
							diskReadWrite.setUnity(rs.getString("unity"));
							diskReadWrite.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setDiskReadWrite(diskReadWrite);
						}
						else if(itemNames.get(name).equals("HostAvailability")) {
							HostAvailability hostAvailability = new HostAvailability();
							hostAvailability.setName(name);
							hostAvailability.setClock(Long.parseLong(rs.getString("clock")));
							hostAvailability.setUnity(rs.getString("unity"));
							hostAvailability.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setHostAvailability(hostAvailability);
						}
						else if(itemNames.get(name).equals("PowerCurrentvaAggregated")) {
							PowerCurrentvaAggregated powerCurrentvaAggregated = new PowerCurrentvaAggregated();
							powerCurrentvaAggregated.setName(name);
							powerCurrentvaAggregated.setClock(Long.parseLong(rs.getString("clock")));
							powerCurrentvaAggregated.setUnity(rs.getString("unity"));
							powerCurrentvaAggregated.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setPowerCurrentvaAggregated(powerCurrentvaAggregated);
						}
						else if(itemNames.get(name).equals("consw")) {
							PowerCurrentwAggregated powerCurrentwAggregated = new PowerCurrentwAggregated();
							powerCurrentwAggregated.setName(name);
							powerCurrentwAggregated.setClock(Long.parseLong(rs.getString("clock")));
							powerCurrentwAggregated.setUnity(rs.getString("unity"));
							powerCurrentwAggregated.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setPowerCurrentwAggregated(powerCurrentwAggregated);
						}
						else if(itemNames.get(name).equals("PowerTotalAggregated")) {
							PowerTotalAggregated powerTotalAggregated = new PowerTotalAggregated();
							powerTotalAggregated.setName(name);
							powerTotalAggregated.setClock(Long.parseLong(rs.getString("clock")));
							powerTotalAggregated.setUnity(rs.getString("unity"));
							powerTotalAggregated.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setPowerTotalAggregated(powerTotalAggregated);
						}
						else if(itemNames.get(name).equals("cpuload")) {
							SystemCPULoad systemCPULoad = new SystemCPULoad();
							systemCPULoad.setName(name);
							systemCPULoad.setClock(Long.parseLong(rs.getString("clock")));
							systemCPULoad.setUnity(rs.getString("unity"));
							systemCPULoad.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setSystemCPULoad(systemCPULoad);
						}
						else if(itemNames.get(name).equals("OneAvailability")) {
							OneAvailability oneAvailability = new OneAvailability();
							oneAvailability.setName(name);
							oneAvailability.setClock(Long.parseLong(rs.getString("clock")));
							oneAvailability.setUnity(rs.getString("unity"));
							oneAvailability.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setOneAvailability(oneAvailability);
						}
						else if(itemNames.get(name).equals("SystemCpuUtilPerc")) {
							SystemCPUUtilPerc systemCPUUtilPerc = new SystemCPUUtilPerc();
							systemCPUUtilPerc.setName(name);
							systemCPUUtilPerc.setClock(Long.parseLong(rs.getString("clock")));
							systemCPUUtilPerc.setUnity(rs.getString("unity"));
							systemCPUUtilPerc.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setSystemCPUUtilPerc(systemCPUUtilPerc);
						} else if(itemNames.get(name).equals("SystemPowerConsumption")) {
							SystemPowerConsumption systemPowerConsumption = new SystemPowerConsumption();
							systemPowerConsumption.setName(name);
							systemPowerConsumption.setClock(Long.parseLong(rs.getString("clock")));
							systemPowerConsumption.setUnity(rs.getString("unity"));
							systemPowerConsumption.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setSystemPowerConsumption(systemPowerConsumption);
						} else if(itemNames.get(name).equals("SystemSwapSizeFree")) {
							SystemSwapSize systemSwapSize = new SystemSwapSize();
							systemSwapSize.setName(name);
							systemSwapSize.setClock(Long.parseLong(rs.getString("clock")));
							systemSwapSize.setUnity(rs.getString("unity"));
							systemSwapSize.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setSystemSwapSize(systemSwapSize);
						} else if(itemNames.get(name).equals("VMMemorySizeFree")) {
							VMMemorySizeFree vMMemoryFree = new VMMemorySizeFree();
							vMMemoryFree.setName(name);
							vMMemoryFree.setClock(Long.parseLong(rs.getString("clock")));
							vMMemoryFree.setUnity(rs.getString("unity"));
							vMMemoryFree.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setvMMemorySizeFree(vMMemoryFree);
						} else if(itemNames.get(name).equals("VMMemorySizeTotal")) {
							VMMemorySizeTotal vMMemoryTotal = new VMMemorySizeTotal();
							vMMemoryTotal.setName(name);
							vMMemoryTotal.setClock(Long.parseLong(rs.getString("clock")));
							vMMemoryTotal.setUnity(rs.getString("unity"));
							vMMemoryTotal.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setvMMemorySizeTotal(vMMemoryTotal);
						} else if(itemNames.get(name).equals("CustomCPUUtilization")) {
							CustomCpuUtilization customCPUUtilization = new CustomCpuUtilization();
							customCPUUtilization.setName(name);
							customCPUUtilization.setClock(Long.parseLong(rs.getString("clock")));
							customCPUUtilization.setUnity(rs.getString("unity"));
							customCPUUtilization.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setCustomCpuUtilization(customCPUUtilization);
						} else if(itemNames.get(name).equals("CustonVfsIops")) {
							CustomVfsIops customVfsIops = new CustomVfsIops();
							customVfsIops.setName(name);
							customVfsIops.setClock(Long.parseLong(rs.getString("clock")));
							customVfsIops.setUnity(rs.getString("unity"));
							customVfsIops.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setCustomVfsIops(customVfsIops);
						} else if(itemNames.get(name).equals("CustomVMSRunning")) {
							CustomVMSRunning customVMSRunning = new CustomVMSRunning();
							customVMSRunning.setName(name);
							customVMSRunning.setClock(Long.parseLong(rs.getString("clock")));
							customVMSRunning.setUnity(rs.getString("unity"));
							customVMSRunning.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setCustomVMsRunning(customVMSRunning);
						} else if(itemNames.get(name).equals("PowerCo2Generated")) {
							PowerCo2Generated powerCo2Generated = new PowerCo2Generated();
							powerCo2Generated.setName(name);
							powerCo2Generated.setClock(Long.parseLong(rs.getString("clock")));
							powerCo2Generated.setUnity(rs.getString("unity"));
							powerCo2Generated.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setPowerCo2Generated(powerCo2Generated);
						} else if(itemNames.get(name).equals("PowerCurrentWhReal")) {
							PowerCurrentwhReal powerCurrentwhReal = new PowerCurrentwhReal();
							powerCurrentwhReal.setName(name);
							powerCurrentwhReal.setClock(Long.parseLong(rs.getString("clock")));
							powerCurrentwhReal.setUnity(rs.getString("unity"));
							powerCurrentwhReal.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setPowerCurrentwhReal(powerCurrentwhReal);
						} else if(itemNames.get(name).equals("procnum")) {
							ProcNum procNum = new ProcNum();
							procNum.setName(name);
							procNum.setClock(Long.parseLong(rs.getString("clock")));
							procNum.setUnity(rs.getString("unity"));
							procNum.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setProcNum(procNum);
						} else if(itemNames.get(name).equals("cpuutil")) {
							SystemCpuUtil systemCpuUtil = new SystemCpuUtil();
							systemCpuUtil.setName(name);
							systemCpuUtil.setClock(Long.parseLong(rs.getString("clock")));
							systemCpuUtil.setUnity(rs.getString("unity"));
							systemCpuUtil.setValue(Double.parseDouble(rs.getString("value")));
							hostMonitoring.setSystemCpuUtil(systemCpuUtil);
						}
					}
					
				}
				while (rs.next());
			}
		}
		catch (SQLException e) {
			logger.debug("" + e.getStackTrace());
		}
		

		//Obtain de XML from object (marshall)
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(HostMonitoring.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
			
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			marshaller.marshal(hostMonitoring, out);
			hostMonitoringXML = out.toString();

			logger.debug("Host Monitoring data: " + hostMonitoringXML);

		} catch (JAXBException jaxbExpcetion) {
			logger.debug("Error trying to marshall HostMonitoring XML " + jaxbExpcetion.getMessage());
		}
		
		return hostMonitoringXML;

	}

	public void obtainHostMonitoring(ResultSet rs, HostMonitoring hostMonitoring) throws SQLException {
		String name = rs.getString("A.name");
		if (itemNames.get(name).equals("CO2GenerateRate")) {
			CO2GenerationRate co2GenerateRate = new CO2GenerationRate();
			co2GenerateRate.setName(name);
			co2GenerateRate.setClock(Long.parseLong(rs.getString("A.clock")));
			co2GenerateRate.setUnity(rs.getString("A.unity"));
			co2GenerateRate.setValue(Double.parseDouble(rs.getString("A.value")));
			hostMonitoring.setCo2GenerationRate(co2GenerateRate);
		} else if (itemNames.get(name).equals("ProcessorLoad")) {
			ProcessorLoad processorLoad = new ProcessorLoad();
			processorLoad.setName(name);
			processorLoad.setClock(Long.parseLong(rs.getString("A.clock")));
			processorLoad.setUnity(rs.getString("A.unity"));
			processorLoad.setValue(Double.parseDouble(rs.getString("A.value")));
			hostMonitoring.setProcessorLoad(processorLoad);
		} else if (itemNames.get(name).equals("FreeMemory")) {
			FreeMemory freeMemory = new FreeMemory();
			freeMemory.setName(name);
			freeMemory.setClock(Long.parseLong(rs.getString("A.clock")));
			freeMemory.setUnity(rs.getString("A.unity"));
			freeMemory.setValue(Double.parseDouble(rs.getString("A.value")));
			hostMonitoring.setFreeMemory(freeMemory);
		} else if (itemNames.get(name).equals("TotalMemory")) {
			TotalMemory totalMemory = new TotalMemory();
			totalMemory.setName(name);
			totalMemory.setClock(Long.parseLong(rs.getString("A.clock")));
			totalMemory.setUnity(rs.getString("A.unity"));
			totalMemory.setValue(Double.parseDouble(rs.getString("A.value")));
			hostMonitoring.setTotalMemory(totalMemory);
		} else if (itemNames.get(name).equals("FreeSwapSpace")) {
			FreeSwapSpace freeSwapSpace = new FreeSwapSpace();
			freeSwapSpace.setName(name);
			freeSwapSpace.setClock(Long.parseLong(rs.getString("A.clock")));
			freeSwapSpace.setUnity(rs.getString("A.unity"));
			freeSwapSpace.setValue(Double.parseDouble(rs.getString("A.value")));
			hostMonitoring.setFreeSwapSpace(freeSwapSpace);
		} else if (itemNames.get(name).equals("NumberOfVMsRunning")) {
			NumberOfVMsRunning numberOfVMsRunning = new NumberOfVMsRunning();
			numberOfVMsRunning.setName(name);
			numberOfVMsRunning.setClock(Long.parseLong(rs.getString("A.clock")));
			numberOfVMsRunning.setUnity(rs.getString("A.unity"));
			numberOfVMsRunning.setValue(Double.parseDouble(rs.getString("A.value")));
			hostMonitoring.setNumberOfVMsRunning(numberOfVMsRunning);
		} else if (itemNames.get(name).equals("AggregateEnergy")) {
			AggregateEnergy aggregateEnergy = new AggregateEnergy();
			aggregateEnergy.setName(name);
			aggregateEnergy.setClock(Long.parseLong(rs.getString("A.clock")));
			aggregateEnergy.setUnity(rs.getString("A.unity"));
			aggregateEnergy.setValue(Double.parseDouble(rs.getString("A.value")));
			hostMonitoring.setAggregateEnergy(aggregateEnergy);
		} else if (itemNames.get(name).equals("AggregatePower")) {
			ApparentPower apparentPower = new ApparentPower();
			apparentPower.setName(name);
			apparentPower.setClock(Long.parseLong(rs.getString("A.clock")));
			apparentPower.setUnity(rs.getString("A.unity"));
			apparentPower.setValue(Double.parseDouble(rs.getString("A.value")));
			hostMonitoring.setApparentPower(apparentPower);
		} else if (itemNames.get(name).equals("RealPower")) {
			RealPower realPower = new RealPower();
			realPower.setName(name);
			realPower.setClock(Long.parseLong(rs.getString("A.clock")));
			realPower.setUnity(rs.getString("A.unity"));
			realPower.setValue(Double.parseDouble(rs.getString("A.value")));
			hostMonitoring.setRealPower(realPower);
		} else if (itemNames.get(name).equals("NumberOfProcesses")) {
			NumberOfProcesses numberOfProcesses = new NumberOfProcesses();
			numberOfProcesses.setName(name);
			numberOfProcesses.setClock(Long.parseLong(rs.getString("A.clock")));
			numberOfProcesses.setUnity(rs.getString("A.unity"));
			numberOfProcesses.setValue(Double.parseDouble(rs.getString("A.value")));
			hostMonitoring.setNumberOfProcesses(numberOfProcesses);
		} else if (itemNames.get(name).equals("CPUUserTimeAvg1")) {
			CPUUserTimeAvg1 cpuUserTimeAvg1 = new CPUUserTimeAvg1();
			cpuUserTimeAvg1.setName(name);
			cpuUserTimeAvg1.setClock(Long.parseLong(rs.getString("A.clock")));
			cpuUserTimeAvg1.setUnity(rs.getString("A.unity"));
			cpuUserTimeAvg1.setValue(Double.parseDouble(rs.getString("A.value")));
			hostMonitoring.setCpuUserTimeAvg1(cpuUserTimeAvg1);
		} else if (itemNames.get(name).equals("RunningVMs")) {
			RunningVMs runningVMs = new RunningVMs();
			runningVMs.setName(name);
			runningVMs.setClock(Long.parseLong(rs.getString("A.clock")));
			runningVMs.setUnity(rs.getString("A.unity"));
			runningVMs.setValue(Double.parseDouble(rs.getString("A.value")));
			hostMonitoring.setRunningVMs(runningVMs);
		} else if (itemNames.get(name).equals("Co2Producted")) {
			Co2Producted co2Producted = new Co2Producted();
			co2Producted.setName(name);
			co2Producted.setClock(Long.parseLong(rs.getString("A.clock")));
			co2Producted.setUnity(rs.getString("A.unity"));
			co2Producted.setValue(Double.parseDouble(rs.getString("A.value")));
			hostMonitoring.setCo2Producted(co2Producted);
		} else if (itemNames.get(name).equals("AggregateEnergyUsage")) {
			AggregateEnergyUsage aggregateEnergyUsage = new AggregateEnergyUsage();
			aggregateEnergyUsage.setName(name);
			aggregateEnergyUsage.setClock(Long.parseLong(rs.getString("A.clock")));
			aggregateEnergyUsage.setUnity(rs.getString("A.unity"));
			aggregateEnergyUsage.setValue(Double.parseDouble(rs.getString("A.value")));
			hostMonitoring.setAggregateEnergyUsage(aggregateEnergyUsage);
		} else if (itemNames.get(name).equals("ApparentEnergyUsage")) {
			ApparentPowerUsage apparentPowerUsage = new ApparentPowerUsage();
			apparentPowerUsage.setName(name);
			apparentPowerUsage.setClock(Long.parseLong(rs.getString("A.clock")));
			apparentPowerUsage.setUnity(rs.getString("A.unity"));
			apparentPowerUsage.setValue(Double.parseDouble(rs.getString("A.value")));
			hostMonitoring.setApparentPowerUsage(apparentPowerUsage);
		} else if (itemNames.get(name).equals("RealPowerUsage")) {
			RealPowerUsage realPowerUsage = new RealPowerUsage();
			realPowerUsage.setName(name);
			realPowerUsage.setClock(Long.parseLong(rs.getString("A.clock")));
			realPowerUsage.setUnity(rs.getString("A.unity"));
			realPowerUsage.setValue(Double.parseDouble(rs.getString("A.value")));
			hostMonitoring.setRealPowerUsage(realPowerUsage);
		}
		//new metrics
		else if (itemNames.get(name).equals("Availability")) {
			Availability availability = new Availability();
			availability.setName(name);
			availability.setClock(Long.parseLong(rs.getString("A.clock")));
			availability.setUnity(rs.getString("A.unity"));
			availability.setValue(Double.parseDouble(rs.getString("A.value")));
			hostMonitoring.setAvailability(availability);
		} 
		else if (itemNames.get(name).equals("Co2GenerationPer30s")) {
			Co2GenerationPer30s co2GenerationPer30s = new Co2GenerationPer30s();
			co2GenerationPer30s.setName(name);
			co2GenerationPer30s.setClock(Long.parseLong(rs.getString("A.clock")));
			co2GenerationPer30s.setUnity(rs.getString("A.unity"));
			co2GenerationPer30s.setValue(Double.parseDouble(rs.getString("A.value")));
			hostMonitoring.setCo2GenerationPer30s(co2GenerationPer30s);
		} 
		else if (itemNames.get(name).equals("CPUUtilization")) {
			CPUUtilization cpuUtilization = new CPUUtilization();
			cpuUtilization.setName(name);
			cpuUtilization.setClock(Long.parseLong(rs.getString("A.clock")));
			cpuUtilization.setUnity(rs.getString("A.unity"));
			cpuUtilization.setValue(Double.parseDouble(rs.getString("A.value")));
			hostMonitoring.setCpuUtilization(cpuUtilization);
		} 
		else if (itemNames.get(name).equals("DiskIOPS")) {
			DiskIOPS diskIOPS = new DiskIOPS();
			diskIOPS.setName(name);
			diskIOPS.setClock(Long.parseLong(rs.getString("A.clock")));
			diskIOPS.setUnity(rs.getString("A.unity"));
			diskIOPS.setValue(Double.parseDouble(rs.getString("A.value")));
			hostMonitoring.setDiskIOPS(diskIOPS);
		} 
		else if (itemNames.get(name).equals("FreeSpaceOnSrv")) {
			FreeSpaceOnSrv freeSpaceOnSrv = new FreeSpaceOnSrv();
			freeSpaceOnSrv.setName(name);
			freeSpaceOnSrv.setClock(Long.parseLong(rs.getString("A.clock")));
			freeSpaceOnSrv.setUnity(rs.getString("A.unity"));
			freeSpaceOnSrv.setValue(Double.parseDouble(rs.getString("A.value")));
			hostMonitoring.setFreeSpaceOnSrv(freeSpaceOnSrv);
		}
		else if (itemNames.get(name).equals("PowerConsumption")) {
			PowerConsumption powerConsumption = new PowerConsumption();
			powerConsumption.setName(name);
			powerConsumption.setClock(Long.parseLong(rs.getString("A.clock")));
			powerConsumption.setUnity(rs.getString("A.unity"));
			powerConsumption.setValue(Double.parseDouble(rs.getString("A.value")));
			hostMonitoring.setPowerConsumption(powerConsumption);
		}
	}
	
	private ResultSet getListOfVMsForAnExperiment(String experimentId) throws SQLException {
		statement = dbConnector.connection
				.prepareStatement("SELECT * FROM metrics_virtual_machines " +
								  "WHERE metrics_virtual_machines.fk_metrics_Experiments=?;");
		statement.setString(1, experimentId);
		logger.debug("Query statement for getting the VMs associated to an Experiment: " + statement.toString());
		
		return statement.executeQuery();
	}
	
	protected Map<String,String> getListOfLocationsMapping(List<String> hrefsOfVMs, Set<String> locations) {
		Map<String,String> vMsAndLocations = new HashMap<String,String>();
		for(String hrefCompute : hrefsOfVMs) {
			String location = Util.getLocationNameFromComputeHref(hrefCompute);
			vMsAndLocations.put(hrefCompute, location);
			locations.add(location);
		}
		
		return vMsAndLocations;
	}
	
	public Response getPowerUsageOfAnExperiment(String experimentId) {
		String xmlResponse = null;
		dbConnector.connect();
		
		try {
			//Get the list of VMs associated to that experiment
			ResultSet listOfVMsInDB = getListOfVMsForAnExperiment(experimentId);
			
			long startTime = Long.MAX_VALUE;
			long endTime = 0;
			boolean vmStillRunning = false;
			List<String> hrefVMs = new ArrayList<String>();
			
			while(listOfVMsInDB.next()) {
				hrefVMs.add(listOfVMsInDB.getString("location"));
				long vmStartTime = listOfVMsInDB.getLong("start_time");
				if(vmStartTime<startTime) startTime = vmStartTime;
				long vmEndTime = listOfVMsInDB.getLong("end_time");
				if(vmEndTime == 0) vmStillRunning = true;
				if(vmEndTime>endTime) endTime = vmEndTime;
			}
			
			// If there is a VM still running we get the current time in milliseconds
			if(vmStillRunning || endTime == 0) endTime = System.currentTimeMillis();
			
			Set<String> listOfLocations = new HashSet<String>();
			Map<String,String> vMsAndLocations = getListOfLocationsMapping(hrefVMs, listOfLocations);
			
			Map<String, ResultSet> cO2ValuesForLocation = new HashMap<String, ResultSet>();
			
			for(String location : listOfLocations) {
				cO2ValuesForLocation.put(location, getCo2ForALocationInAPeriod(location, startTime, endTime));
			}
			
			List<VMReport> vMReports = new ArrayList<VMReport>();
			
			for(String href : vMsAndLocations.keySet()) {
				ResultSet powerConsumedByVM = getPowerUsageOfAVM(href);
				ResultSet cO2OfLocation = cO2ValuesForLocation.get(vMsAndLocations.get(href));
				vMReports.add(createVMPowerReport(powerConsumedByVM, href, cO2OfLocation));
			}
			
			double totalPower = 0.0;
			double totalCO2 = 0.0;
			long previousTime = System.currentTimeMillis();
			
			for(VMReport report : vMReports) {
				totalPower = totalPower + report.getPowerConsumption().getValue();
				totalCO2 = totalCO2 + report.getCo2Generated().getValue();
			}
			
			PowerConsumption powerConsumption = new PowerConsumption();
			powerConsumption.setClock(previousTime);
			powerConsumption.setName("Power consumed by the VM");
			powerConsumption.setUnity("Wh");
			powerConsumption.setValue(totalPower); 
			
			CO2Generated co2Generated = new CO2Generated();
			co2Generated.setClock(previousTime);
			co2Generated.setName("CO2 Generated by the VM");
			co2Generated.setUnity("g");
			co2Generated.setValue(totalCO2);
			
			ExperimentReport experimentReport = new ExperimentReport();
			experimentReport.setHref("/experiments/" + experimentId + "/report");
			experimentReport.setCo2Generated(co2Generated);
			experimentReport.setPowerConsumption(powerConsumption);
			experimentReport.setVmReports(vMReports);

			//Obtain XML from object (marshall)
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(ExperimentReport.class);
				Marshaller marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				marshaller.marshal(experimentReport, out);
				xmlResponse = out.toString();

				logger.debug("Host Monitoring data: " + xmlResponse);
			} catch (JAXBException jaxbExpcetion) {
				logger.debug("Error trying to marshall VMReport XML " + jaxbExpcetion.getMessage());
			}
			
		} catch (SQLException e) {
			MessageHandler.error("Error setting href " + experimentId + " inactive. Exception: " + e.getMessage() + " Skipping.");
		}

		dbConnector.disconnect(); // Close and clean up all JDBC resources

		if (xmlResponse != null)
			return buildResponse(200, xmlResponse);
		else
			return buildResponse(404, "No items for the experiment " + experimentId);
	}
	
	private ResultSet getCo2ForALocationInAPeriod(String location, long initialTime, long finalTime) throws SQLException {
		statement = dbConnector.connection
				.prepareStatement("SELECT items.* FROM items, metrics_sites, sites_items " +
								  "WHERE items.name='Co2' " +
								  "AND items.id_items=sites_items.fk_items " +
								  "AND sites_items.fk_metrics_sites=metrics_sites.id_metrics_sites " +
								  "AND metrics_sites.location=? " +
								  "AND items.clock>=? " +
								  "AND items.clock<=? ;");
		// Parameters index from 1
		statement.setString(1, location);
		statement.setString(2, "" + initialTime);
		statement.setString(3, "" + finalTime);
		
		logger.debug("Query statement for CO2 of testbed: " + statement.toString());
		
		return statement.executeQuery();
	}
	
	private ResultSet getPowerUsageOfAVM(String href) throws SQLException {
		statement = dbConnector.connection
				.prepareStatement("SELECT items.* " +
						          "FROM items, metrics_virtual_machines, virtual_machines_items " +
						          "WHERE items.name='power' " +
						          "AND items.id_items=virtual_machines_items.fk_items " +
						          "AND virtual_machines_items.fk_metrics_virtual_machines=metrics_virtual_machines.id_metrics_virtual_machines " + 
						          "AND metrics_virtual_machines.location=?;");
		// Parameters index from 1
		statement.setString(1, href);
		logger.debug("Query statement for getting power usage of a VM: " + statement.toString());
		
		return statement.executeQuery();
	}
	
	public Response getPowerUsageOfAVM(String location, String vmId) {
		String xmlResponse = null;
		String href = "/locations/" + location + "/computes/" + vmId;
		dbConnector.connect();
		
		try {
			//We get the power consumed by a VM
			ResultSet resultsPowerOfVM = getPowerUsageOfAVM(href);
			
			// We get the CO2 that it is produced in the site
			long initialTime = 0l;
			long finalTime = 0l;
			if(resultsPowerOfVM.next()) {
				initialTime = resultsPowerOfVM.getLong("clock");
				resultsPowerOfVM.last();
				finalTime = resultsPowerOfVM.getLong("clock");
			}
			resultsPowerOfVM.beforeFirst();
			logger.debug("Initial time: " + initialTime + " finalTime: " + finalTime);
			
			ResultSet resultsCO2Data = getCo2ForALocationInAPeriod(location, initialTime, finalTime);

			xmlResponse =  printVMPowerReport(resultsPowerOfVM, href, resultsCO2Data);
			logger.debug("VM Monitoring: " + xmlResponse);

		} catch (SQLException e) {
			MessageHandler.error("Error setting href " + href
					+ " inactive. Exception: " + e.getMessage() + " Skipping.");
		}

		dbConnector.disconnect(); // Close and clean up all JDBC resources

		if (xmlResponse != null)
			return buildResponse(200, xmlResponse);
		else
			return buildResponse(404, "No items for the host " + href);
	}

	public Response getVMMonitoring(String location, String vmId) {
		
		String xmlResponse = null;
		String href = "/locations/" + location + "/computes/" + vmId;
		dbConnector.connect();

		try {
			statement = dbConnector.connection
					.prepareStatement("SELECT items_1.* " +
									  "FROM items AS items_1 " +
							          "INNER JOIN (" +
									  		"SELECT MAX(items_2.id_items) AS max_id " +
							                "FROM items AS items_2,  metrics_virtual_machines AS vms, virtual_machines_items AS st " +
									  		"WHERE vms.location=? " + 
									  			"AND vms.id_metrics_virtual_machines = st.fk_metrics_virtual_machines " + 
									  		    "AND items_2.id_items = st.fk_items " + 
									  			"GROUP BY items_2.name) " +
									  		 "AS max_items " +
									  		 "on max_items.max_id = items_1.id_items;");
			// Parameters index from 1
			statement.setString(1, href);
			logger.debug("Query statement for getting energy from testbeds: " + statement.toString());

			results = statement.executeQuery();

			//xmlResponse = printHostMonitoring(results);
			xmlResponse =  printVMMonitoring(results, href);
			logger.debug("VM Monitoring: " + xmlResponse);

		} catch (SQLException e) {
			MessageHandler.error("Error setting href " + href
					+ " inactive. Exception: " + e.getMessage() + " Skipping.");
		}

		dbConnector.disconnect(); // Close and clean up all JDBC resources

		if (xmlResponse != null)
			return buildResponse(200, xmlResponse);
		else
			return buildResponse(404, "No items for the host " + href);
	}


	/**
	 * 
	 * 
	 * @param host
	 *            from which we want to get the info
	 * @return XML file with the info details
	 * @throws SQLException
	 */
	public Response getHostMonitoring(String name) {

		String xmlResponse = null;
		dbConnector.connect();
		long currentTime = System.currentTimeMillis();
		currentTime = (currentTime - PERIOD)/1000l;

		try {
			statement = dbConnector.connection
					.prepareStatement("SELECT items_1.* " +
							"FROM items AS items_1 " +
							"INNER JOIN ( " +
								"SELECT MAX(items_2.id_items) AS max_id " +
								"FROM items AS items_2,  metrics_physical_hosts AS hosts, physical_hosts_items AS st " +
								"WHERE hosts.location=? " +
									"AND hosts.id_metrics_physical_hosts = st.fk_metrics_physical_hosts " +
									"AND items_2.id_items = st.fk_items " +
									"AND items_2.clock>=? " +
								"GROUP BY items_2.name) " +
							"AS max_items " +
							"on max_items.max_id = items_1.id_items;");

			// Parameters index from 1
			statement.setString(1, name);
			statement.setString(2, "" + currentTime);

			logger.debug("Query statement for getting energy from testbeds: "
					+ statement.toString());

			results = statement.executeQuery();

			xmlResponse =  printHostMonitoring(results, "testbed", name);

		} catch (SQLException e) {
			MessageHandler.error("Error setting host " + name
					+ " inactive. Exception: " + e.getMessage() + " Skipping.");
		}

		dbConnector.disconnect(); // Close and clean up all JDBC resources

		if (xmlResponse != null)
			return buildResponse(200, xmlResponse);
		else
			return buildResponse(404, "No items for the host " + name);
	}

	/**
	 * Returns the last monitoring items (by clock field) from a host
	 * 
	 * @param testbed
	 *            name of the testbed from which we want the info
	 * @param hostname
	 *            from which we want to get the info
	 * @return XML file with the info details
	 * @throws SQLException
	 */
	public Response getHostMonitoring(String testbed, String hostname) {

		String xmlResponse = null;
		dbConnector.connect();
		
		long currentTime = System.currentTimeMillis();
		currentTime = (currentTime - PERIOD)/1000l;
		
		try {
			statement = dbConnector.connection
						.prepareStatement("SELECT items_1.* " + 
								"FROM items AS items_1 " + 
								"INNER JOIN ( " + 
									"SELECT MAX(items_2.id_items) AS max_id " +
									"FROM items AS items_2,  metrics_physical_hosts AS hosts, physical_hosts_items AS st " +
									"WHERE hosts.location=? " +
										"AND hosts.id_metrics_physical_hosts = st.fk_metrics_physical_hosts " +
										"AND items_2.id_items = st.fk_items " +
										"AND items_2.clock>=? " +
										"GROUP BY items_2.name) " +
									"AS max_items " +
								"on max_items.max_id = items_1.id_items;");

			// Parameters index from 1
			statement.setString(1, hostname);
			statement.setString(2, "" + currentTime);
			logger.debug("Query statement for getting energy from testbeds: " + statement.toString());

			results = statement.executeQuery();

			//xmlResponse = printHostMonitoring(testbed, results);
			xmlResponse =  printHostMonitoring(results, testbed, hostname);

		} catch (SQLException e) {
			MessageHandler.error("Error setting host " + hostname
					+ " inactive. Exception: " + e.getMessage() + " Skipping.");
		}

		dbConnector.disconnect(); // Close and clean up all JDBC resources

		if (xmlResponse != null)
			return buildResponse(200, xmlResponse);
		else
			return buildResponse(404, "No items for the host " + hostname);
	}

	/**
	 * 
	 * @param testbed
	 * @param hostname
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Response getHostMonitoring(String testbed, String hostname,
			String startTime, String endTime) {
		
		/** TODO Does this method have sense? Pending review **/
		
		String xmlResponse = null;
		dbConnector.connect();

		try {
			statement = dbConnector.connection
					.prepareStatement("SELECT items_1.* " +
							"FROM items AS items_1 " +
							"INNER JOIN ( " +
								"SELECT MAX(items_2.id_items) AS max_id " +
								"FROM items AS items_2,  metrics_physical_hosts AS hosts, physical_hosts_items AS st " +
								"WHERE hosts.location=? " +
									"AND hosts.id_metrics_physical_hosts = st.fk_metrics_physical_hosts " +
									"AND items_2.id_items = st.fk_items " +
									"AND items_2.clock>? " +
									"AND items_2.clock<? " +
								"GROUP BY items_2.name) " +
							"AS max_items " +
							"on max_items.max_id = items_1.id_items;");

			// Parameters index from 1
			statement.setString(1, hostname);
			statement.setString(2, startTime);
			statement.setString(3, endTime);

			logger.debug("Query statement for getting energy from testbeds: "
					+ statement.toString());

			results = statement.executeQuery();

			xmlResponse = printCollectionOfHostMonitoring(testbed, results);
			//xmlResponse =  printHostMonitoring(results, testbed, hostname);
			
		} catch (SQLException e) {
			MessageHandler.error("Error setting host " + hostname
					+ " inactive. Exception: " + e.getMessage() + " Skipping.");
		}

		dbConnector.disconnect(); // Close and clean up all JDBC resources

		if (xmlResponse != null)
			return buildResponse(200, xmlResponse);
		else
			return buildResponse(404, "No items for the host " + hostname);
	}
	
	private double getCO2Value(ResultSet co2Data, long time) throws SQLException {
		if(co2Data.absolute(actualCO2Index)) {
			double value = co2Data.getDouble("value");
			long timeDifference = Math.abs(time - co2Data.getLong("clock"));
			//System.out.println("START -> Initial values: Index: " + actualCO2Index + " Value: " + value + " timeDifference: " + timeDifference + " Time: " + time + " Timefrom DB: " + co2Data.getLong("clock"));
			while(co2Data.next()) {
				long newTimeDifference = Math.abs(time - co2Data.getLong("clock"));
				
				if(newTimeDifference < timeDifference) {
					value = co2Data.getLong("value");
					actualCO2Index = co2Data.getRow();
				}
				//System.out.println("Iteration: Index: " + actualCO2Index + " Value: " + value + " timeDifference: " + timeDifference + "New TIME difference: " + newTimeDifference + " Time: " + time + " Timefrom DB: " + co2Data.getLong("clock"));
			}
			return value;
		} else return 0.0;
	}
	
	private VMReport createVMPowerReport(ResultSet rs, String href, ResultSet co2Data) throws SQLException {
		double previousValue = 0.0;
		long previousTime = 0l;
		double totalPower = 0.0;
		double totalCO2 = 0.0;
		actualCO2Index = 1;
		
		if(rs.next()) {
			do {
				if(!rs.isFirst()) {
					double actualPowerToAdd = 0.0;
					actualPowerToAdd = (rs.getDouble("value") + previousValue ) / 2.0 * (double) (rs.getLong("clock") - previousTime);
					totalPower = totalPower + actualPowerToAdd;
					previousValue = rs.getDouble("value");
					previousTime = rs.getLong("clock");
					double co2Value = getCO2Value(co2Data, previousTime);
					totalCO2 = totalCO2 + co2Value * (actualPowerToAdd / 3600000000.0);
				} else {
					previousValue = rs.getDouble("value");
					previousTime = rs.getLong("clock");
				}
			
			} while (rs.next());
		}
		
		VMReport vmReport = new VMReport();
		vmReport.setHref(href);
		
		PowerConsumption powerConsumption = new PowerConsumption();
		powerConsumption.setClock(previousTime);
		powerConsumption.setName("Power consumed by the VM");
		powerConsumption.setUnity("Wh");
		powerConsumption.setValue(totalPower / 3600000.0); // The time calculation is in ms, we pass that to hours.
		vmReport.setPowerConsumption(powerConsumption);
		
		CO2Generated co2Generated = new CO2Generated();
		co2Generated.setClock(previousTime);
		co2Generated.setName("CO2 Generated by the VM");
		co2Generated.setUnity("g");
		co2Generated.setValue(totalCO2);
		vmReport.setCo2Generated(co2Generated);
		
		return vmReport;
	}
	
	protected String printVMPowerReport(ResultSet rs, String href, ResultSet co2Data) throws SQLException {
		VMReport vmReport = createVMPowerReport(rs, href, co2Data);
		
		String vMReportXML = "";
		//Obtain de XML from object (marshall)
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(VMReport.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			marshaller.marshal(vmReport, out);
			vMReportXML = out.toString();

			logger.debug("Host Monitoring data: " + vMReportXML);
		} catch (JAXBException jaxbExpcetion) {
			logger.debug("Error trying to marshall VMReport XML " + jaxbExpcetion.getMessage());
		}

		return vMReportXML;
	}

	protected String printHostMonitoring(String testbed, ResultSet rs) {
		HostMonitoring hostMonitoring = new HostMonitoring();
		String hostMonitoringXML = "";

		try {
			if (rs.next()) {
				String location = rs.getString("C.location");

				hostMonitoring.setHref("/testbeds/" + testbed + "/hosts/"
						+ location + "/monitoring");
				Link link = new Link();
				link.setRel("testbed");
				link.setHref("/testbeds/" + testbed);
				List<Link> links = new ArrayList<Link>();
				links.add(link);
				hostMonitoring.setLinks(links);

				obtainHostMonitoring(rs, hostMonitoring);

			}
		} catch (SQLException e) {
			logger.debug("" + e.getStackTrace());
		}

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(HostMonitoring.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			marshaller.marshal(hostMonitoring, out);
			hostMonitoringXML = out.toString();

			logger.debug("Host Monitoring data: " + hostMonitoringXML);

		} catch (JAXBException jaxbExpcetion) {
			logger.debug("Error trying to marshal HostMonitoring XML "
					+ jaxbExpcetion.getStackTrace());
		}

		return hostMonitoringXML;
	}

	/**
	 * Returns the last 10 items (by clock field) from a host
	 * 
	 * @param testbed
	 *            from which we want to get the info
	 * @return XML file with the info details
	 * @throws SQLException
	 */
	public Response getTestbedMonitoringCo2(String location, String startTime,
			String endTime) {

		String xmlResponse = null;
		dbConnector.connect();

		try {
			statement = dbConnector.connection
					.prepareStatement("SELECT DISTINCT A.*, C.location "
							+ "FROM items A, sites_items B, metrics_sites C "
							+ "WHERE A.id_items = B.fk_items AND B.fk_metrics_sites = C.id_metrics_sites "
							+ "AND C.location =? "
							+ "AND A.name='Co2 producted per kWh' "
							+ "AND A.clock >? " 
							+ "AND A.clock <? "
							+ "ORDER BY A.clock DESC;");

			// Parameters index from 1
			statement.setString(1, location);
			statement.setString(2, startTime);
			statement.setString(3, endTime);

			logger.debug("Query statement for getting energy from testbeds: " + statement.toString());

			results = statement.executeQuery();

			xmlResponse = printTestbedCo2(results);

		} catch (SQLException e) {
			MessageHandler.error("Error setting testbed " + location
					+ " inactive. Exception: " + e.getMessage() + " Skipping.");
		}

		dbConnector.disconnect(); // Close and clean up all JDBC resources

		if (xmlResponse != null)
			return buildResponse(200, xmlResponse);
		else
			return buildResponse(404, "No items for the testbed " + location);
	}

	protected String printTestbedCo2(ResultSet rs) {
		Collection collection = new Collection();
		Items items = new Items();
		collection.setItems(items);
		int count = 0;
		String collectionXML = "";

		try {
			do {
				if (rs.next()) {
					TestbedMonitoring testbedMonitoring = new TestbedMonitoring();

					String location = rs.getString("C.location");
					testbedMonitoring.setHref("/testbeds/" + location + "/monitoring/co2");
					Link link = new Link();
					link.setRel("testbed");
					link.setHref("/testbeds/" + location);
					List<Link> links = new ArrayList<Link>();
					links.add(link);
					testbedMonitoring.setLinks(links);

					String name = rs.getString("A.name");

					if (itemNames.get(name).equals("Co2")) {
						Co2 co2 = new Co2();
						co2.setName(name);
						co2.setClock(Long.parseLong(rs.getString("A.clock")));
						co2.setUnity(rs.getString("A.unity"));
						co2.setValue(Double.parseDouble(rs.getString("A.value")));
						testbedMonitoring.setCo2(co2);
					}

					items.addTestbedMonitoring(testbedMonitoring);
					count++;
				}
			} while (!rs.isAfterLast());
		} catch (SQLException e) {
			logger.debug("" + e.getStackTrace());
		}

		items.setTotal(count);

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			marshaller.marshal(collection, out);
			collectionXML = out.toString();

			logger.debug("Testbed Monitoring Co2 data: " + collectionXML);

		} catch (JAXBException jaxbExpcetion) {
			logger.debug("Error trying to marshal Testbed Monitoring XML " + jaxbExpcetion.getStackTrace());
		}

		return collectionXML;
	}

	/**
	 * Returns the last 10 items (by clock field) from a host
	 * 
	 * @param testbed from which we want to get the info
	 * @return XML file with the info details
	 * @throws SQLException
	 */
	public Response getTestbedMonitoring(String location) {

		String xmlResponse = null;
		dbConnector.connect();

		try {
			statement = dbConnector.connection
					.prepareStatement("SELECT items_1.* "
							+ "FROM items AS items_1 "
							+ "INNER JOIN (SELECT MAX(items_2.id_items) AS max_id "
										+ "FROM items AS items_2,  metrics_sites AS sites, sites_items AS st "
										+ "WHERE sites.location=? "
											+ "	AND sites.id_metrics_sites = st.fk_metrics_sites "
											+ "AND items_2.id_items = st.fk_items "
										+ "GROUP BY items_2.name) "
							+ "AS max_items "
							+ "on max_items.max_id = items_1.id_items;");
					
			// Parameters index from 1
			statement.setString(1, location);

			logger.debug("Query statement for getting energy from testbeds: "
					+ statement.toString());

			results = statement.executeQuery();

			xmlResponse = printTestbedMonitoring(results, location);
			//xmlResponse =  printHostMonitoring(results, "testbed", location);

		} catch (SQLException e) {
			MessageHandler.error("Error setting testbed " + location + " inactive. Exception: " + e.getMessage() + " Skipping.");
		}

		dbConnector.disconnect(); // Close and clean up all JDBC resources

		if (xmlResponse != null)
			return buildResponse(200, xmlResponse);
		else
			return buildResponse(404, "No items for the testbed " + location);
	}

	protected String printCollectionOfHostMonitoring(String testbed,
			ResultSet rs) {

		Collection collection = new Collection();
		Items items = new Items();
		collection.setItems(items);
		int count = 0;
		String collectionXML = "";

		try {
			do {
				if (rs.next()) {
					HostMonitoring hostMonitoring = new HostMonitoring();

					String location = rs.getString("C.location");
					hostMonitoring.setHref("/testbeds/" + testbed + "/hosts/"
							+ location + "/monitoring");
					Link link = new Link();
					link.setRel("testbed");
					link.setHref("/testbeds/" + testbed);
					List<Link> links = new ArrayList<Link>();
					links.add(link);
					hostMonitoring.setLinks(links);

					obtainHostMonitoring(rs, hostMonitoring);

					items.addHostMonitoring(hostMonitoring);
					count++;
				}
			} while (!rs.isAfterLast());
		} catch (SQLException e) {
			logger.debug("" + e.getStackTrace());
		}

		items.setTotal(count);

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			marshaller.marshal(collection, out);
			collectionXML = out.toString();

			logger.debug("Host Monitoring data: " + collectionXML);

		} catch (JAXBException jaxbExpcetion) {
			logger.debug("Error trying to marshal Host Monitoring XML " + jaxbExpcetion.getStackTrace());
		}

		return collectionXML;
	}

	public String getHostsOfTestbedFromDatabase(String name) {
		refreshHostDBInfo(name);
		
		Testbed testbed = getTestbed(name);
		
		List<eu.eco2clouds.accounting.datamodel.Host> hostsFromDB = testbed.getHosts();
		List<Host> hosts = new ArrayList<Host>();
		for(eu.eco2clouds.accounting.datamodel.Host hostFromDB : hostsFromDB) {
			Host host = ModelConversion.convertHost(hostFromDB);
			setHostLinks(host, name);
			hosts.add(host);
		}
		
		Collection collection = new Collection();
		Items items = new Items();
		items.setOffset(0);
		items.setTotal(hosts.size());
		collection.setItems(items);
		items.setHosts(hosts);
		Link linkParent = new Link();
		linkParent.setHref("/testbeds/"+ name);
		linkParent.setRel("parent");
		linkParent.setType(CONTENT_TYPE_ECO2CLOUDS_XML);
		ArrayList<Link> links = new ArrayList<Link>();
		links.add(linkParent);
		collection.setLinks(links);
		
		String hostsStringXML = "";
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Collection .class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			marshaller.marshal(collection, out);
			hostsStringXML = out.toString();

			logger.debug("Testbed Monitoring data: " + hostsStringXML);

		} catch (JAXBException jaxbExpcetion) {
			logger.debug("Error trying to marshall TestbedMonitoring XML " + jaxbExpcetion.getStackTrace());
		}

		return hostsStringXML;
	}
	
	private void setHostLinks(Host host, String testbedName) {
		
		Link linkParent = new Link();
		linkParent.setHref("/testbeds/"+ testbedName + "/hosts");
		linkParent.setRel("parent");
		linkParent.setType(CONTENT_TYPE_ECO2CLOUDS_XML);
		Link linkMonitoring = new Link();
		linkMonitoring.setHref("/testbeds/"+ testbedName + "/hosts/" + host.getName() + "/monitoring");
		linkMonitoring.setRel("monitoring");
		linkMonitoring.setType(CONTENT_TYPE_ECO2CLOUDS_XML);
		Link linkSelf = new Link();
		linkSelf.setHref("/testbeds/"+ testbedName + "/hosts/" + host.getName());
		linkSelf.setRel("self");
		linkSelf.setType(CONTENT_TYPE_ECO2CLOUDS_XML);
		ArrayList<Link> links = new ArrayList<Link>();
		links.add(linkParent);
		links.add(linkSelf);
		links.add(linkMonitoring);
		
		host.setLinks(links);
	}
	
	private void refreshHostDBInfo(String testbedName) {
		Testbed testbed = getTestbed(testbedName);
		//First we update the database information
		String hostInfo = client.getHostInfo(testbed);
		UpdateHostsThread update = new UpdateHostsThread(testbedDAO, hostDAO, hostDataDAO, testbedName, hostInfo);
		update.updateHostInformation();
	}

	public String getHostOfTestbedFromDatabase(String testbedName, String hostName) {
		refreshHostDBInfo(testbedName);
		
		eu.eco2clouds.accounting.datamodel.Host hostFromDB = hostDAO.getByName(hostName);
		List<HostData> hostDatas = hostDataDAO.getLastEntry(hostFromDB);
		if(hostFromDB == null) return null;
		
		Host host = ModelConversion.convertHost(hostFromDB);
		setHostLinks(host, testbedName);
		
		if(hostDatas != null) {
			HostData hostData = hostDatas.get(0);
			host.setHostData(ModelConversion.convertHostData(hostData));
		}

		String hostStringXML = "";
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Host .class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			marshaller.marshal(host, out);
			hostStringXML = out.toString();

			logger.debug("Testbed Monitoring data: " + hostStringXML);

		} catch (JAXBException jaxbExpcetion) {
			logger.debug("Error trying to marshall TestbedMonitoring XML " + jaxbExpcetion.getStackTrace());
		}
		
		
		return hostStringXML;
	}

	protected String printVMMonitoring(ResultSet rs, String href) {
		VMMonitoring vmMonitoring = new VMMonitoring();
		String vmMonitoringString = "";
		
		try {
			
			 ResultSetMetaData rsmd = rs.getMetaData();
			 logger.debug("Colummn names: " + rsmd.getColumnName(1) + " , " + rsmd.getColumnName(2) + " , " + rsmd.getColumnName(3) + " , " 
					 + rsmd.getColumnName(4) + " , " + rsmd.getColumnName(5) + " , ");
			
			 
			if (rs.next()) {
				do {
					if (rs.isFirst()) {
						logger.debug("Creating links...");
						vmMonitoring.setHref(href + "/monitoring");
						Link link = new Link();
						link.setRel("vm");
						link.setHref(href);
						List<Link> links = new ArrayList<Link>();
						links.add(link);
						vmMonitoring.setLinks(links);
						
					}
				
					//analyze nodes
					String name = rs.getString("name");
					logger.debug("Item name: " + name + " itemNames.get: " + itemNames.get(name));
					if(itemNames.get(name) != null ) {
						if (itemNames.get(name).equals("cpuload")) {
							CPULoad cpuLoad = new CPULoad();
							cpuLoad.setName(name);
							cpuLoad.setClock(Long.parseLong(rs.getString("clock")));
							cpuLoad.setUnity(rs.getString("unity"));
							cpuLoad.setValue(Double.parseDouble(rs.getString("value")));
							vmMonitoring.setCpuload(cpuLoad);
						} else if (itemNames.get(name).equals("cpuutil")) {
							CPUUtil cpuutil = new CPUUtil();
							cpuutil.setName(name);
							cpuutil.setClock(Long.parseLong(rs.getString("clock")));
							cpuutil.setUnity(rs.getString("unity"));
							cpuutil.setValue(Double.parseDouble(rs.getString("value")));
							vmMonitoring.setCpuutil(cpuutil);
						} else if (itemNames.get(name).equals("diskfree")) {
							DiskFree diskfree = new DiskFree();
							diskfree.setName(name);
							diskfree.setClock(Long.parseLong(rs.getString("clock")));
							diskfree.setUnity(rs.getString("unity"));
							diskfree.setValue(Double.parseDouble(rs.getString("value")));
							vmMonitoring.setDiskfree(diskfree);
						} else if (itemNames.get(name).equals("disktotal")) {
							DiskTotal disktotal = new DiskTotal();
							disktotal.setName(name);
							disktotal.setClock(Long.parseLong(rs.getString("clock")));
							disktotal.setUnity(rs.getString("unity"));
							disktotal.setValue(Double.parseDouble(rs.getString("value")));
							vmMonitoring.setDisktotal(disktotal);
						} else if (itemNames.get(name).equals("diskusage")) {
							DiskUsage diskusage = new DiskUsage();
							diskusage.setName(name);
							diskusage.setClock(Long.parseLong(rs.getString("clock")));
							diskusage.setUnity(rs.getString("unity"));
							diskusage.setValue(Double.parseDouble(rs.getString("value")));
							vmMonitoring.setDiskusage(diskusage);
						} else if (itemNames.get(name).equals("iops")) {
							Iops iops = new Iops();
							iops.setName(name);
							iops.setClock(Long.parseLong(rs.getString("clock")));
							iops.setUnity(rs.getString("unity"));
							iops.setValue(Double.parseDouble(rs.getString("value")));
							vmMonitoring.setIops(iops);
						} else if (itemNames.get(name).equals("ioutil")) {
							IoUtil ioutil = new IoUtil();
							ioutil.setName(name);
							ioutil.setClock(Long.parseLong(rs.getString("clock")));
							ioutil.setUnity(rs.getString("unity"));
							ioutil.setValue(Double.parseDouble(rs.getString("value")));
							vmMonitoring.setIoutil(ioutil);
						} else if (itemNames.get(name).equals("memfree")) {
							MemFree memfree = new MemFree();
							memfree.setName(name);
							memfree.setClock(Long.parseLong(rs.getString("clock")));
							memfree.setUnity(rs.getString("unity"));
							memfree.setValue(Double.parseDouble(rs.getString("value")));
							vmMonitoring.setMemfree(memfree);
						} else if (itemNames.get(name).equals("memtotal")) {
							MemTotal memtotal = new MemTotal();
							memtotal.setName(name);
							memtotal.setClock(Long.parseLong(rs.getString("clock")));
							memtotal.setUnity(rs.getString("unity"));
							memtotal.setValue(Double.parseDouble(rs.getString("value")));
							vmMonitoring.setMemtotal(memtotal);
						} else if (itemNames.get(name).equals("memused")) {
							MemUsed memused = new MemUsed();
							memused.setName(name);
							memused.setClock(Long.parseLong(rs.getString("clock")));
							memused.setUnity(rs.getString("unity"));
							memused.setValue(Double.parseDouble(rs.getString("value")));
							vmMonitoring.setMemused(memused);
						} else if (itemNames.get(name).equals("memused")) {
							MemUsed memused = new MemUsed();
							memused.setName(name);
							memused.setClock(Long.parseLong(rs.getString("clock")));
							memused.setUnity(rs.getString("unity"));
							memused.setValue(Double.parseDouble(rs.getString("value")));
							vmMonitoring.setMemused(memused);
						} else if (itemNames.get(name).equals("netifin")) {
							NetIfIn netifin = new NetIfIn();
							netifin.setName(name);
							netifin.setClock(Long.parseLong(rs.getString("clock")));
							netifin.setUnity(rs.getString("unity"));
							netifin.setValue(Double.parseDouble(rs.getString("value")));
							vmMonitoring.setNetifin(netifin);
						} else if (itemNames.get(name).equals("netifout")) {
							NetIfOut netifout = new NetIfOut();
							netifout.setName(name);
							netifout.setClock(Long.parseLong(rs.getString("clock")));
							netifout.setUnity(rs.getString("unity"));
							netifout.setValue(Double.parseDouble(rs.getString("value")));
							vmMonitoring.setNetifout(netifout);
						} else if (itemNames.get(name).equals("power")) {
							Power power = new Power();
							power.setName(name);
							power.setClock(Long.parseLong(rs.getString("clock")));
							power.setUnity(rs.getString("unity"));
							power.setValue(Double.parseDouble(rs.getString("value")));
							vmMonitoring.setPower(power);
						} else if (itemNames.get(name).equals("procnum")) {
							ProcNum procnum = new ProcNum();
							procnum.setName(name);
							procnum.setClock(Long.parseLong(rs.getString("clock")));
							procnum.setUnity(rs.getString("unity"));
							procnum.setValue(Double.parseDouble(rs.getString("value")));
							vmMonitoring.setProcNum(procnum);
						} else if (itemNames.get(name).equals("swapfree")) {
							SwapFree swapfree = new SwapFree();
							swapfree.setName(name);
							swapfree.setClock(Long.parseLong(rs.getString("clock")));
							swapfree.setUnity(rs.getString("unity"));
							swapfree.setValue(Double.parseDouble(rs.getString("value")));
							vmMonitoring.setSwapfree(swapfree);
						} else if (itemNames.get(name).equals("swaptotal")) {
							SwapTotal swaptotal = new SwapTotal();
							swaptotal.setName(name);
							swaptotal.setClock(Long.parseLong(rs.getString("clock")));
							swaptotal.setUnity(rs.getString("unity"));
							swaptotal.setValue(Double.parseDouble(rs.getString("value")));
							vmMonitoring.setSwaptotal(swaptotal);
						} else if (itemNames.get(name).equals("applicationmetric_1")) {
							ApplicationMetric applicationMetric1 = new ApplicationMetric();
							applicationMetric1.setName(name);
							applicationMetric1.setClock(Long.parseLong(rs.getString("clock")));
							applicationMetric1.setUnity(rs.getString("unity"));
							applicationMetric1.setValue(Double.parseDouble(rs.getString("value")));
							vmMonitoring.setApplicationMetric1(applicationMetric1);
						} else if (itemNames.get(name).equals("applicationmetric_2")) {
							ApplicationMetric applicationMetric2 = new ApplicationMetric();
							applicationMetric2.setName(name);
							applicationMetric2.setClock(Long.parseLong(rs.getString("clock")));
							applicationMetric2.setUnity(rs.getString("unity"));
							applicationMetric2.setValue(Double.parseDouble(rs.getString("value")));
							vmMonitoring.setApplicationMetric2(applicationMetric2);
						} else if (itemNames.get(name).equals("applicationmetric_3")) {
							ApplicationMetric applicationMetric3 = new ApplicationMetric();
							applicationMetric3.setName(name);
							applicationMetric3.setClock(Long.parseLong(rs.getString("clock")));
							applicationMetric3.setUnity(rs.getString("unity"));
							applicationMetric3.setValue(Double.parseDouble(rs.getString("value")));
							vmMonitoring.setApplicationMetric3(applicationMetric3);
						} else if (itemNames.get(name).equals("applicationmetric_4")) {
							ApplicationMetric applicationMetric4 = new ApplicationMetric();
							applicationMetric4.setName(name);
							applicationMetric4.setClock(Long.parseLong(rs.getString("clock")));
							applicationMetric4.setUnity(rs.getString("unity"));
							applicationMetric4.setValue(Double.parseDouble(rs.getString("value")));
							vmMonitoring.setApplicationMetric4(applicationMetric4);
						}
					}
					
				}
				while (rs.next());
			}
		}
		catch (SQLException e) {
			logger.debug("" + e.getStackTrace());
		}
		

		//Obtain de XML from object (marshall)
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(VMMonitoring.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
			
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			marshaller.marshal(vmMonitoring, out);
			vmMonitoringString = out.toString();

			logger.debug("Host Monitoring data: " + vmMonitoringString);

		} catch (JAXBException jaxbExpcetion) {
			logger.debug("Error trying to marshall VMMonitoring XML " + jaxbExpcetion.getMessage());
		}
		
		return vmMonitoringString;
	}
}
