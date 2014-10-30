package eu.eco2clouds.scheduler.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import eu.eco2clouds.accounting.datamodel.parser.HostMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.Testbed;
import eu.eco2clouds.accounting.datamodel.parser.TestbedMonitoring;
import eu.eco2clouds.accounting.datamodel.xml.HostShare;
import eu.eco2clouds.accounting.datamodel.xml.HostXml;
import eu.eco2clouds.scheduler.accounting.client.AccountingClientHC;
import eu.eco2clouds.scheduler.conf.Configuration;
import eu.eco2clouds.scheduler.designtime.FileOutput;

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
public class TestbedLiveMonitoring
{
	public static void testbedLiveMonitoring()
	{
		ArrayList<HostXml> hostsWithNoVms = new ArrayList<HostXml>();
		
		AccountingClientHC accountingclient = new AccountingClientHC(
				Configuration.accountingServiceUrl);
		List<Testbed> testbeds = accountingclient.getListOfTestbeds();
		
		if (testbeds == null || testbeds.size() == 0)
			throw new RuntimeException("Testbed Live Monitoring: No Testbeds Found");
		
		for (Testbed testbed : testbeds)
		{
			if (testbed == null)
				throw new RuntimeException("Testbed Live Monitoring: Testbed Null");
			
			/*TestbedMonitoring testbedMonitoring =
					accountingclient.getTestbedMonitoringStatus(testbed);
			
			if (testbedMonitoring == null)
				throw new RuntimeException("Testbed Live Monitoring: TestbedMonitoring: Null");*/
			
			List<HostXml> hosts = accountingclient.getHostStatusForATestbed(testbed).getHosts();
			
			if (hosts == null || hosts.size() == 0)
				throw new RuntimeException(TestbedLiveMonitoring.class +
						"Hosts of " + testbed.getName() + "Null or Empty");
			
			for (HostXml host : hosts)
			{
				HostMonitoring hostMonitoring =
						accountingclient.getHostMonitoringStatus(testbed, host.getName());
				
				if (hostMonitoring.getRunningVMs().getValue() == 0)
				{
					hostsWithNoVms.add(host);
				}
			} // for
			
			FileOutput.outputToFile("Testebed: " + testbed.getName());
			FileOutput.outputToFile(hostsWithNoVms + "");
		} // for
	} // testbedMonitoring

	/*private static String calculateTestbedTotalCpu(
			AccountingClientHC accountingClient, List<HostXml> hosts, Testbed testbed)
	{
		double totalCpuOfHosts = 0, totalCpuUtilizationOfHosts = 0;
		for (HostXml host : hosts)
		{
			HostShare hostShare = host.getHostShare();
			HostMonitoring hostMonitoring =
					accountingClient.getHostMonitoringStatus(testbed, host.getName());
			
			totalCpuOfHosts += hostShare.getMaxCpu();
			//totalCpuUtilizationOfHosts += hostMonitoring.getCpuUtilization().getValue();
		} // for
		
		return totalCpuUtilizationOfHosts + "/" + totalCpuOfHosts;
	} // calculateTestbedTotalCpu*/
} // class