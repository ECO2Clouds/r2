package eu.eco2clouds.scheduler.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.eco2clouds.accounting.datamodel.parser.Co2;
import eu.eco2clouds.accounting.datamodel.parser.Coal;
import eu.eco2clouds.accounting.datamodel.parser.Wind;

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
public class UnitConversionTest {

	@Test
	public void testToKiloWatts() {
		Coal coal = new Coal();
		coal.setUnity("MW");
		coal.setValue(1000.0);
		
		Wind wind = new Wind();
		wind.setUnity("XXX");
		wind.setValue(1000.0);
		
		wind = UnitConversion.toKiloWatts(wind);
		assertEquals(1000.0, wind.getValue(), 0.0000001);
		coal = UnitConversion.toKiloWatts(coal);
		assertEquals(1000000.0, coal.getValue(), 0.000001);
	}
	
	@Test
	public void testToMegaWatts() {
		Coal coal = new Coal();
		coal.setUnity("kW");
		coal.setValue(1000.0);
		
		Wind wind = new Wind();
		wind.setUnity("XXX");
		wind.setValue(1000.0);
		
		wind = UnitConversion.toMegaWatts(wind);
		assertEquals(1000.0, wind.getValue(), 0.0000001);
		coal = UnitConversion.toMegaWatts(coal);
		assertEquals(1.0, coal.getValue(), 0.000001);
	}
	
	@Test
	public void testToGPerKiloWattPerHour() {
		Co2 co2 = new Co2();
		co2.setUnity("g\\/MWh");
		co2.setValue(1000.0);
		
		co2 = UnitConversion.toGPerKiloWattPerHour(co2);
		assertEquals(1.0, co2.getValue().doubleValue(), 0.00001);
		
		co2.setName("XXX");
		assertEquals(1.0, co2.getValue().doubleValue(), 0.00001);
	}
	
	@Test
	public void testToGPerMegaWattPerHour() {
		Co2 co2 = new Co2();
		co2.setUnity("g\\/kWh");
		co2.setValue(1000.0);
		
		co2 = UnitConversion.toGPerMegaWattPerHour(co2);
		assertEquals(1000000.0, co2.getValue().doubleValue(), 0.00001);
		
		co2.setName("XXX");
		assertEquals(1000000.0, co2.getValue().doubleValue(), 0.00001);
	}
}
