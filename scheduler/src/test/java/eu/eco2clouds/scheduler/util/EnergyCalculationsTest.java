package eu.eco2clouds.scheduler.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.eco2clouds.accounting.datamodel.parser.Co2;
import eu.eco2clouds.accounting.datamodel.parser.DummyEnergyMetric;

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
public class EnergyCalculationsTest {

	@Test
	public void testgPerHour() {
		Co2 co2 = new Co2();
		co2.setUnity("g\\/kWh");
		co2.setValue(1000.0);
		
		DummyEnergyMetric dummy = new DummyEnergyMetric();
		dummy.setUnity("MW");
		dummy.setValue(10.0);
		
		double result = EnergyCalculations.gPerHour(co2, dummy);
		
		assertEquals(10000000.0, result, 0.00001);
	}
	
	@Test
	public void testgetTotalCo2ForPeriodOfTime() {
		Co2 co2 = new Co2();
		co2.setUnity("g\\/kWh");
		co2.setValue(1000.0);
		
		DummyEnergyMetric dummy = new DummyEnergyMetric();
		dummy.setUnity("MW");
		dummy.setValue(10.0);
		
		double result = EnergyCalculations.getTotalCo2ForPeriodOfTime(co2, dummy, 10l, 7210l);
		
		assertEquals(20000000.0, result, 0.00001);
	}
	
	@Test
	public void getMeanCo2ValueTest() {
		List<Co2> co2s = new ArrayList<Co2>();
		
		Co2 co21 = new Co2();
		co21.setValue(1000.0);
		co21.setUnity("g\\/kWh");
		co2s.add(co21);
		
		Co2 co22 = new Co2();
		co22.setValue(2000000.0);
		co22.setUnity("g\\/MWh");
		co2s.add(co22);
		
		Co2 co23 = new Co2();
		co23.setValue(1000.0);
		co23.setUnity("g\\/kWh");
		co2s.add(co23);
		
		Co2 result = EnergyCalculations.getMeanCo2Value(co2s);
		assertEquals("g\\/kWh", result.getUnity());
		assertEquals(1333.33, result.getValue(), 0.1);
	}
	
	@Test
	public void getMeanValueTest() {
		DummyEnergyMetric dummy1 = new DummyEnergyMetric();
		dummy1.setValue(10.0);
		dummy1.setUnity("MW");
		
		DummyEnergyMetric dummy2 = new DummyEnergyMetric();
		dummy2.setValue(1000.0);
		dummy2.setUnity("kW");
		
		List<DummyEnergyMetric> dummies = new ArrayList<DummyEnergyMetric>();
		dummies.add(dummy2);
		dummies.add(dummy1);
		
		DummyEnergyMetric dummy = EnergyCalculations.getMeanEnergy(dummies, DummyEnergyMetric.class);
		assertEquals(5500.0, dummy.getValue(), 0.1);
		assertEquals("kW", dummy.getUnity());
	}
}
