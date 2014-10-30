package eu.eco2clouds.scheduler.util;

import eu.eco2clouds.accounting.datamodel.parser.AbstractMonitoringItem;
import eu.eco2clouds.accounting.datamodel.parser.Co2;

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
 * Utility that performs several energy unit conversions...
 * @author David Garcia Perez - AtoS
 */
public class UnitConversion {
	private static String megawatt = "MW";
	private static String kilowatt = "kW";
	private static String gPerKiloWattPerHour = "g\\/kWh";
	private static String gPerMegaWattPerHour = "g\\/MWh";
	
	/**
	 * Converts an Energy Value results to KiloWatts from MegaWatts,
	 * if the result is not in MegaWatts it leaves like it is... 
	 * @param energyResult To be converted...
	 * @return Value in KiloWatts if it was previously in MegaWatts...
	 */
	public static <A extends AbstractMonitoringItem> A toKiloWatts(A energyResult) {
		
		if(energyResult.getUnity().equals(megawatt)) {
			energyResult.setValue(energyResult.getValue() * 1000.0);
		}
		
		return energyResult;
	}
	
	/**
	 * Converts an Energy Value results in MegaWats from KiloWatts,
	 * if the result is not in KiloWatts it leaves like it is...
	 * @param energyResult To be Converted
	 * @return Value in MegaWatts if it was previously in KiloWatts
	 */
	public static <A extends AbstractMonitoringItem> A toMegaWatts(A energyResult) {
		
		if(energyResult.getUnity().equals(kilowatt)) {
			energyResult.setValue(energyResult.getValue() / 1000.0);
		}
		
		return energyResult;
	}
	
	/**
	 * Converts the Co2 produced from MegaWatts to KiloWatts
	 * @param co2 to be converted
	 * @return the value in g/kWh if it was previously in g/MWh, if
	 *         not leaves like it is
	 */
	public static Co2 toGPerKiloWattPerHour(Co2 co2) {
		
		if(co2.getUnity().equals(gPerMegaWattPerHour)) {
			co2.setValue(co2.getValue() / 1000.0);
		}
		
		return co2;
	}
	
	/**
	 * Converts the Co2 produced from KiloWatts to MegaWatts 
	 * @param co2 to be converted
	 * @return the value in g/MWh if it was previously in g/kWh, if
	 *         not leaves like it is
	 */
	public static Co2 toGPerMegaWattPerHour(Co2 co2) {
		
		if(co2.getUnity().equals(gPerKiloWattPerHour)) {
			co2.setValue(co2.getValue() * 1000.0);
		}
		
		return co2;
	}
}
