package eu.eco2clouds.scheduler.util;

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
public class TimeCalculations {
	private static long fiveMinutesInMiliseconds = 300000l;
	
	public static boolean isTermnated(long endTimeOfExperiment) {
		long now = System.currentTimeMillis();
		long nowPlusFive = now + fiveMinutesInMiliseconds;
		System.out.println("NOW + 5 minutes: " + nowPlusFive);
		System.out.println("endTimeOfExperiment: " + endTimeOfExperiment);
		
		// Experiment it is not terminated
		if(nowPlusFive < endTimeOfExperiment) return false;

		return true;
	}
	
	public static boolean isStopped(long endTimeOfExperiment) {
		long now = System.currentTimeMillis();
		long endPlusFive = endTimeOfExperiment + fiveMinutesInMiliseconds;
		// Experiment it is not stopped
		if(now < endTimeOfExperiment || now > endPlusFive) return false;

		return true;
	}

}
