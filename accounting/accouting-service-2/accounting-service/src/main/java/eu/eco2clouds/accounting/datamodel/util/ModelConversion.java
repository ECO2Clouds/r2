package eu.eco2clouds.accounting.datamodel.util;

import static eu.eco2clouds.accounting.Dictionary.CONTENT_TYPE_ECO2CLOUDS_XML;
import static eu.eco2clouds.accounting.Dictionary.CONTENT_TYPE_JSON;
import eu.eco2clouds.accounting.datamodel.Experiment;
import eu.eco2clouds.accounting.datamodel.Host;
import eu.eco2clouds.accounting.datamodel.HostData;
import eu.eco2clouds.accounting.datamodel.Testbed;
import eu.eco2clouds.accounting.datamodel.VM;

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

public class ModelConversion {

	public static eu.eco2clouds.accounting.datamodel.parser.Experiment getExperimentXML(Experiment experiment) {
		eu.eco2clouds.accounting.datamodel.parser.Experiment experimentXML = new eu.eco2clouds.accounting.datamodel.parser.Experiment();
		experimentXML.setHref("/experiments/" + experiment.getId());
		experimentXML.setApplicationProfile(experiment.getApplicationProfile());
		experimentXML.setBonfireExperimentId(experiment.getBonfireExperimentId());
		experimentXML.setBonfireGroupId(experiment.getBonfireGroupId());
		experimentXML.setBonfireUserId(experiment.getBonfireUserId());
		experimentXML.setEndTime(experiment.getEndTime());
		experimentXML.setId(experiment.getId());
		experimentXML.setManagedExperimentId(experiment.getManagedExperimentId());
		experimentXML.setStartTime(experiment.getStartTime());
		experimentXML.setSubmittedExperimentDescriptor(experiment.getSubmittedExperimentDescriptor());
		
		// We add the necessary links
		experimentXML.addLink("parent", "/experiments", CONTENT_TYPE_ECO2CLOUDS_XML);
		experimentXML.addLink("application-profile", "/experiments/" + experimentXML.getId().intValue() + "/application-profile", CONTENT_TYPE_JSON);
		experimentXML.addLink("submitted-experiment-descriptor", "/experiments/" + experimentXML.getId().intValue() + "/submitted-experiment-descriptor", CONTENT_TYPE_JSON);
		
		return experimentXML;
	}

	public static eu.eco2clouds.accounting.datamodel.parser.VM getVMXML(VM vm, boolean withLinks, int experimentId) {
		eu.eco2clouds.accounting.datamodel.parser.VM vmXML = new eu.eco2clouds.accounting.datamodel.parser.VM();
		vmXML.setBonfireUrl(vm.getBonfireUrl());
		vmXML.setId(vm.getId());
		vmXML.setIp(vm.getIp());
		vmXML.setBonfireUrl(vm.getBonfireUrl());
		
		if(withLinks) {
			vmXML.setHref("/experiments/" + experimentId + "/vms/" + vmXML.getId());
			
			vmXML.addLink("experiment", "/experiments/" + experimentId, CONTENT_TYPE_ECO2CLOUDS_XML);
			vmXML.addLink("parent", "/experiments/" + experimentId + "/vms", CONTENT_TYPE_ECO2CLOUDS_XML);
			vmXML.addLink("self", "/experiments/" + experimentId + "/vms/" + vm.getId(), CONTENT_TYPE_ECO2CLOUDS_XML);
		}
		
		return vmXML;
	}
	
	public static eu.eco2clouds.accounting.datamodel.parser.HostData convertHostData(HostData hostData) {
		if(hostData == null) return null;
		
		eu.eco2clouds.accounting.datamodel.parser.HostData newHostData = new eu.eco2clouds.accounting.datamodel.parser.HostData();
		newHostData.setCpuUsage(hostData.getCpuUsage());
		newHostData.setDiskUsage(hostData.getDiskUsage());
		newHostData.setFreeCpu(hostData.getFreeCpu());
		newHostData.setFreeDisk(hostData.getFreeDisk());
		newHostData.setFreeMen(hostData.getFreeMen());
		newHostData.setMaxCpu(hostData.getMaxCpu());
		newHostData.setMaxDisk(hostData.getMaxDisk());
		newHostData.setMaxMem(hostData.getMaxMem());
		newHostData.setMemUsage(hostData.getMemUsage());
		newHostData.setRunningVms(hostData.getRunningVms());
		newHostData.setUsedCpu(hostData.getUsedCpu());
		newHostData.setUsedDisk(hostData.getUsedDisk());
		newHostData.setUsedMem(hostData.getUsedMem());
		
		return newHostData;
	}
	
	public static eu.eco2clouds.accounting.datamodel.parser.Host convertHost(Host hostFromDB) {
		if(hostFromDB == null) return null;
		
		eu.eco2clouds.accounting.datamodel.parser.Host host = new eu.eco2clouds.accounting.datamodel.parser.Host();
		host.setId(hostFromDB.getId());
		host.setName(hostFromDB.getName());
		host.setState(hostFromDB.getState());
		host.setConnected(hostFromDB.isConnected());
		return host;
	}
	
	public static eu.eco2clouds.accounting.datamodel.parser.Testbed convertTestbed(Testbed testbedFromDB) {
		eu.eco2clouds.accounting.datamodel.parser.Testbed testbed = new eu.eco2clouds.accounting.datamodel.parser.Testbed();
		
		if(testbedFromDB == null) return null;
		
		testbed.setId(testbedFromDB.getId());
		testbed.setName(testbedFromDB.getName());
		testbed.setUrl(testbedFromDB.getUrl());
		
		return testbed;
	}
}
