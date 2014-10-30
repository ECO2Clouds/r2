package eu.eco2clouds.scheduler.util;

import java.util.List;

import eu.eco2clouds.accounting.datamodel.parser.AbstractMonitoringItem;
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
 * 
 * Performs several energy calculations over the metrics retrieves from the
 * Accounting Service
 * @author David Garcia Perez - AtoS
 *
 */
public class EnergyCalculations {

	/**
	 * Returns the Co2 consumed by hour for a total energy mix of some devide
	 * @param co2
	 * @param totalEnergyMix
	 * @return
	 */
	public static <A extends AbstractMonitoringItem> double gPerHour(Co2 co2, A totalEnergyMix) {
		// We make sure everything is in KiloWatts
		totalEnergyMix = UnitConversion.toKiloWatts(totalEnergyMix);
		co2 = UnitConversion.toGPerKiloWattPerHour(co2);
		
		return co2.getValue().doubleValue() * totalEnergyMix.getValue().doubleValue();
	}
	
	/**
	 * Returns the total Co2 produced by a device...
	 * @param co2 produced at a testbed
	 * @param totalEnergyMix total energy mix of some devide
	 * @param startTime in miliseconds
	 * @param endTime in milisecond
	 * @return co2 in grams
	 */
	public static <A extends AbstractMonitoringItem> double getTotalCo2ForPeriodOfTime(Co2 co2, 
																					   A totalEnergyMix, 
																					   long startTime, 
																					   long endTime) {
		
		double gOfCo2PerHour = gPerHour(co2, totalEnergyMix);
		double timeDifferenceInHours = ((double) (endTime - startTime)) / 60.0 / 60.0;
		return gOfCo2PerHour * timeDifferenceInHours;
	}
	
	/**
	 * Calculates the mean value of the Co2 consumed in a period of time... 
	 * @param co2s
	 * @return
	 */
	public static Co2 getMeanCo2Value(List<Co2> co2s) {
		double total = 0.0;
		
		for(Co2 co2 : co2s) {
			co2 = UnitConversion.toGPerKiloWattPerHour(co2);
			total = total + co2.getValue().doubleValue();
		}
		
		total = total / ((double) co2s.size());
		
		Co2 co2 = new Co2();
		co2.setUnity("g\\/kWh");
		co2.setValue(total);
		
		return co2;
	}
	
	/**
	 * Calculates the mean energy for a collection of energy values... 
	 * @param eneryValues
	 * @return
	 */
	public static <A extends AbstractMonitoringItem> A getMeanEnergy(List<A> eneryValues, Class clazz) {
		double total = 0.0;
		
		for(A a : eneryValues) {
			a = UnitConversion.toKiloWatts(a);
			total = total + a.getValue();
		}
		
		total = total / ((double) eneryValues.size());
		
		try {
			A a = (A) clazz.newInstance();
			a.setValue(total);
			a.setUnity("kW");
			return a;
		} catch(Exception e) {
			System.out.println(e.getStackTrace());
			return null;
		}
	}
	
}
