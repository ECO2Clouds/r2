package eu.eco2clouds.scheduler.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
public class TimeCalculationsTest {

	@Test
	public void testIsTerminated() {
		long finalOfExperiment = System.currentTimeMillis();
		finalOfExperiment = finalOfExperiment + 1000000000000l;
		
		// Should return false since the date it is in the future.
		assertFalse(TimeCalculations.isTermnated(finalOfExperiment));
		
		finalOfExperiment = System.currentTimeMillis();
		finalOfExperiment = finalOfExperiment + 300000l;
		
		assertTrue(TimeCalculations.isTermnated(finalOfExperiment));
	}
	
	@Test
	public void testIsStopped() {
		long finalOfExperiment = System.currentTimeMillis();
		finalOfExperiment = finalOfExperiment + 100000000000000000l;
		
		// Should return false since the date it is in the future.
		assertFalse(TimeCalculations.isStopped(finalOfExperiment));
		
		finalOfExperiment = System.currentTimeMillis();
		finalOfExperiment = finalOfExperiment + 300000l;
		
		assertFalse(TimeCalculations.isStopped(finalOfExperiment));
		assertTrue(TimeCalculations.isTermnated(finalOfExperiment));
	}
}
