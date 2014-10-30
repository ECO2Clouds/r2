package eu.eco2clouds.scheduler.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.eco2clouds.applicationProfile.datamodel.Compute;
import eu.eco2clouds.applicationProfile.datamodel.ExperimentDescriptor;
import eu.eco2clouds.applicationProfile.datamodel.ResourceCompute;

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
 */
public class EDUtil {

	/**
	 * Returns a list of testbed and hostname in an Experiment Descriptor.
	 * @param ed
	 * @return
	 */
	public static Map<String,List<String>> getListHostsTestbeds(ExperimentDescriptor ed) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		
		List<ResourceCompute> resourceComputes = ed.getResourcesCompute();
		
		for(ResourceCompute resourceCompute : resourceComputes) {
			Compute compute = resourceCompute.getCompute();
			List<String> locations = compute.getLocations();
			String location = locations.get(0);
			List<String> listHostnames = map.get(location);
			
			if(listHostnames == null) {
				listHostnames = new ArrayList<String>();
				listHostnames.add(compute.getHost());
				map.put(location, listHostnames);
			} else {
				listHostnames.add(compute.getHost());
			}
		}
		
		return map;
	}
}
