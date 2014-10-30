package eu.eco2clouds.scheduler.runtime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import eu.eco2clouds.accounting.datamodel.parser.Experiment;
import eu.eco2clouds.accounting.datamodel.parser.Host;
import eu.eco2clouds.accounting.datamodel.parser.Testbed;
import eu.eco2clouds.accounting.datamodel.parser.VM;
import eu.eco2clouds.accounting.datamodel.parser.VMMonitoring;
import eu.eco2clouds.applicationProfile.datamodel.ApplicationProfile;
import eu.eco2clouds.applicationProfile.datamodel.Compute;
import eu.eco2clouds.applicationProfile.datamodel.ExperimentDescriptor;
import eu.eco2clouds.applicationProfile.datamodel.ResourceCompute;
import eu.eco2clouds.applicationProfile.parser.Parser;
import eu.eco2clouds.scheduler.accounting.client.AccountingClientHC;
import eu.eco2clouds.scheduler.conf.Configuration;
import eu.eco2clouds.scheduler.designtime.FileOutput;
import eu.eco2clouds.scheduler.designtime.InitialDeployment;
import eu.eco2clouds.scheduler.designtime.InstanceTypeRequirements;

/**
 * 
 * Copyright 2014 University of Manchester 
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
 */
public class ExperimentLiveMonitoring
{
	public static void experimentLiveMonitoring(String bonFireUser, String bonFireGroup, String bonFireExperimentId)
			throws JsonParseException, JsonMappingException, IOException
	{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		
		try
		{
			docBuilder = docFactory.newDocumentBuilder();
		}
		
		catch (ParserConfigurationException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// root element experiments
		Document document = docBuilder.newDocument();
		Element experiments = document.createElement("experiments");
		document.appendChild(experiments);
                
		// set experiment bonfire id attribute to experiment element
		experiments.setAttribute("BonfireUser", bonFireUser);
				
		AccountingClientHC accountingClient = new AccountingClientHC(Configuration.accountingServiceUrl);
		
		for (Experiment runningExperiment : accountingClient.getListOfExperiments(bonFireUser, bonFireGroup))
		{
			if (runningExperiment.getBonfireExperimentId().toString().equals(bonFireExperimentId))
			{
				// sub-root element experiment
				Element experiment = document.createElement("experiment");
				experiments.appendChild(experiment);
		 
				// set experiment bonfire id attribute to experiment element
				experiment.setAttribute("BonfireId", runningExperiment.getBonfireExperimentId().toString());
				
				// experiment status element
				Element experimentStatus = document.createElement("experiment-status");
				experimentStatus.appendChild(document.createTextNode(runningExperiment.getStatus()));
				experiment.appendChild(experimentStatus);
				
				// experiment co2 consumption element
				Element experimentCo2Consumption = document.createElement("experiment-co2-consumption");
				experimentCo2Consumption.appendChild(
						document.createTextNode(
								/*accountingClient.getCo2Consumption(runningExperiment.getId())*/ "METHOD NOT WORKING")); // probably the other id
				experiment.appendChild(experimentCo2Consumption);
				
				ApplicationProfile applicationProfile = Parser.getApplicationProfile(runningExperiment.getApplicationProfile());
				ExperimentDescriptor experimentDescriptor = applicationProfile.getExperimentDescriptor();
				
				List<VM> vms = accountingClient.getListOfVMsOfExperiment(runningExperiment.getId(), bonFireUser, bonFireGroup);
				
				// number of vms element
				Element numberOfVms = document.createElement("number-of-vms");
				numberOfVms.appendChild(document.createTextNode(vms.size() + ""));
				experiment.appendChild(numberOfVms);
				
				// sub-root element experiment vms
				Element experimentVms = document.createElement("experiment-vms");
				experiment.appendChild(experimentVms);
				
				for (VM vm : vms)
				{
					VMMonitoring vmMonitoring = accountingClient.getVMMonitoringStatus(vm);
					
					// sub-root element vm
					Element vmElement = document.createElement("vm");
					experimentVms.appendChild(vmElement);
	                
	                // set vm bonfire id attribute to vm element
	                vmElement.setAttribute("BonfireId", vm.getBonfireId());
	                
	                // vm name element
					Element vmName = document.createElement("vm-name");
					vmName.appendChild(document.createTextNode(vm.getName()));
					vmElement.appendChild(vmName);
					
					String vmPowerValue = "Not Returned from Accounting";
					try
					{
						vmPowerValue = vmMonitoring.getPower().getValue().toString();
					} // try
					
					catch (NullPointerException e)
					{
					} // catch
					
					// vm power element
					Element vmPower = document.createElement("vm-power");
					vmPower.appendChild(document.createTextNode(vmPowerValue.toString()));
					vmElement.appendChild(vmPower);
					
					String vmCo2Value = "Not Returned from Accounting";
					try
					{
						//No Co2 Method
					} // try
					
					catch (NullPointerException e)
					{
					} // catch
					
					// vm co2 element
					Element vmCo2 = document.createElement("vm-co2");
					vmCo2.appendChild(document.createTextNode("METHOD NOT AVAILABLE"));
					vmElement.appendChild(vmCo2);
					
					// vm current host element
					Element vmCurrentHost = document.createElement("vm-current-host");
					vmCurrentHost.appendChild(document.createTextNode(vm.getHost()));
					vmElement.appendChild(vmCurrentHost);
					
					String currentVmTestbed = "Not Returned from Accounting";
					for (Testbed testbed : accountingClient.getListOfTestbeds())
					{
						for (Host host : accountingClient.getHostsOfTesbed(testbed.getName()))
						{
							if (host.getName().equals(vm.getHost()))
							{
								currentVmTestbed = testbed.getName();
							} // if
						} // for
					} // for
					
					
					// vm current testbed element
					Element vmCurrentTestbed = document.createElement("vm-current-testbed");
					vmCurrentTestbed.appendChild(document.createTextNode(currentVmTestbed));
					vmElement.appendChild(vmCurrentTestbed);
					
					///TODO: GET current CPU and MEM "usage" of VM, INFORM USMAN
					//double currentVmCpuUsage = 0, currentVmMemUsage = 0;
					//TODO: INFORM USMAN OF SWITCH TO CURRENT INSTANCE TYPE
					
					String vmInstanceType = "Not Returned from Accounting";
					Compute compute = null;
					ArrayList<String> computeLocation = null;
					for (ResourceCompute resourceCompute : experimentDescriptor.getResourcesCompute())
					{
						if (resourceCompute.getCompute().getName().equals(vm.getName()))
						{
							compute = resourceCompute.getCompute();
							vmInstanceType = resourceCompute.getCompute().getInstanceType();
							computeLocation = resourceCompute.getCompute().getLocations();
						} // if
					} // for
					
					//TODO: What shall I do when an InstanceType is undefined?
					
					if (vmInstanceType == null || vmInstanceType.isEmpty())
					{
						vmInstanceType = "Instance Type returned from Accounting is empty";
					}
					
					// vm instance type element
					Element vmInstanceTypeElement = document.createElement("vm-instance-type");
					vmInstanceTypeElement.appendChild(document.createTextNode(vmInstanceType));
					vmElement.appendChild(vmInstanceTypeElement);
					
					ArrayList<String> newTestbedAndHost = new ArrayList<String>();
					if (computeLocation == null || computeLocation.get(0) == null || computeLocation.get(0) == "")
					{
						//TODO: Add other options of biObjective
						newTestbedAndHost = InitialDeployment.optimize(new InstanceTypeRequirements(vmInstanceType, compute));
					} // if
					
					else
					{
						String newHost = InitialDeployment.optimize(new InstanceTypeRequirements(vmInstanceType, compute), computeLocation.get(0));
						newTestbedAndHost.add(computeLocation.get(0));
						newTestbedAndHost.add(newHost);
					} // else
					
					// vm new testbed element
					Element vmNewTestbed = document.createElement("vm-recommended-testbed");
					vmNewTestbed.appendChild(document.createTextNode(newTestbedAndHost.get(0)));
					vmElement.appendChild(vmNewTestbed);
					
					// vm new host element
					Element vmNewHost = document.createElement("vm-recommended-host");
					vmNewHost.appendChild(document.createTextNode(newTestbedAndHost.get(1)));
					vmElement.appendChild(vmNewHost);
				} // for
			} // if
		} // for
		
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try
		{
			transformer = transformerFactory.newTransformer();
		} // try
		
		catch (TransformerConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // catch
		
		saveToFile(document, new File("./" + bonFireExperimentId + ".xml"));
		
		/*DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(new File(bonFireExperimentId + ".xml"));
		
		
		try
		{
			transformer.transform(source, result);
		} // try
		
		catch (TransformerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // catch*/
	} // experimentMonitoring
	
	public static void saveToFile(Document doc, File toFile) throws UnsupportedEncodingException, IOException
	{

		DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
		LSSerializer lsSerializer = domImplementation.createLSSerializer();
		LSOutput lso = domImplementation.createLSOutput();
		lso.setEncoding("utf-8");

		FileOutputStream fos = new FileOutputStream(toFile);
		lso.setByteStream(fos);
		lsSerializer.write(doc, lso);
	}
	
	private static int safeLongToInt(long l)
	{
	    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE)
	    {
	        throw new IllegalArgumentException
	            (l + " cannot be cast to int without changing its value.");
	    } // if
	    
	    return (int) l;
	} // safeLongToInt
} // class
