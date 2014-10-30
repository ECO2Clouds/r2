package eu.eco2clouds.scheduler.designtime;

import java.util.HashMap;

import eu.eco2clouds.accounting.datamodel.xml.HostXml;

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
public class HostsChangesBeforeDeployment
{
	private static HostsChangesBeforeDeployment hostsChangesBeforeDeployment;
	
	public static void createNewInstance()
	{
		if (hostsChangesBeforeDeployment == null)
			hostsChangesBeforeDeployment = new HostsChangesBeforeDeployment();
	} // getCurrentInstance
	
	public static HostsChangesBeforeDeployment getCurrentInstance()
	{
		if (hostsChangesBeforeDeployment == null)
			throw new RuntimeException("HostsChangesBeforeDeployment is not initialized");
		
		return hostsChangesBeforeDeployment;
	} // getCurrentInstance
	
	public static void deleteCurrentInstance()
	{
		hostsChangesBeforeDeployment = null;
	} // deleteCurrentInstance
	
	private HashMap<String, HostXml> hosts;
	
	HostsChangesBeforeDeployment()
	{
		hosts = new HashMap<String, HostXml>();
	} // constructor
	
	public void addHostChange(HostXml host, long newCpuUsage, int newMemUsage)
	{
		if (hosts.get(host.getName()) != null)
		{
			HostXml hostToUpdate = hosts.get(host.getName());
			
			newCpuUsage += hostToUpdate.getHostShare().getCpuUsage();
			newMemUsage += hostToUpdate.getHostShare().getMemUsage();
			
			hostToUpdate.getHostShare().setCpuUsage(newCpuUsage);
			hostToUpdate.getHostShare().setMemUsage(newMemUsage);
			
			return;
		} // if
		
		host.getHostShare().setCpuUsage(newCpuUsage);
		host.getHostShare().setMemUsage(newMemUsage);
		
		hosts.put(host.getName(), host);
	} // addHostChange
	
	public HashMap<String, HostXml> getHostsChanges()
	{
		return hosts;
	} // getHostsChanges
} // class