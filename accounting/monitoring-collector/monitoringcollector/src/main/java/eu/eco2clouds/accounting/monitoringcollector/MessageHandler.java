////////////////////////////////////////////////////////////////////////
//
// Copyright (c) The University of Edinburgh, 2013
//
// This software may not be used, sold, licensed, transferred, copied
// or reproduced in whole or in part in any manner or form or in or
// on any media by any person other than in accordance with the terms
// of the Licence Agreement supplied with the software, or otherwise
// without the prior written consent of the copyright owners.
//
// This software is distributed WITHOUT ANY WARRANTY, without even the
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
// PURPOSE, except where stated in the Licence Agreement supplied with
// the software.
//
//	Created By :			Dominic Sloan-Murphy, Iakovos Panourgias
//	Last Updated Date :		06 Sept 2013
//	Created for Project :	ECO2Clouds
//
////////////////////////////////////////////////////////////////////////
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License. 
//
////////////////////////////////////////////////////////////////////////

package eu.eco2clouds.accounting.monitoringcollector;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Provides static functionality for printing timestamped messages for debugging and informative purposes.
 * General messages are printed to stdout provided the verboseLogging configuration value is true.
 * Error message are printed to stderr.
 */
public class MessageHandler {
	
	/** The format of the timestamps added to messages. i.e. dd MMMM yyyy HH:mm:ss*/
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
	
	/**
	 * Prints a timestamped version of the given string to stderr.
	 *
	 * @param msg the message to be printed
	 */
	public static void error(String msg) {
		System.err.println(putTimeStamp(msg));
	}
	
	/**
	 * Prints a timestamped version of the given string to stdout provided the loggingLevel configuration value is INFO or DEBUG.
	 *
	 * @param msg the message to be printed
	 */
	public static void print(String msg) {
		if (ConfigurationValues.loggingLevel == Level.INFO || ConfigurationValues.loggingLevel == Level.DEBUG) { 
			System.out.println(putTimeStamp(msg));
		}
	}
	
	/**
	 * Prints a timestamped version of the given string to stdout provided the loggingLevel configuration value is DEBUG.
	 *
	 * @param msg the message to be printed
	 */
	public static void debug(String msg) {
		if (ConfigurationValues.loggingLevel == Level.DEBUG) { System.out.println(putTimeStamp(msg)); }
	}
	
	// Prepend all messages with a timestamp.
	// e.g. "[06 September 2013 11:23:40] Compute creation accepted..."
	/**
	 * Timestamps the provided string in format [dd MMMM yyyy HH:mm:ss].
	 *
	 * @param msg the message to be timestamped
	 * @return the timestamped message.
	 */
	protected static String putTimeStamp(String msg) {
		Calendar calendar = Calendar.getInstance(); 
		return "[" + sdf.format(calendar.getTime()) + "] " + msg;
	}
	
	// Used to control log level.
	// TODO: Rewrite monitoring collector to use log4j.
	public enum Level {
		ERROR("ERROR"), INFO("INFO"), DEBUG("DEBUG");
		
		private String text;
		
		Level(String text) { this.text = text; }
		
		public String getText() { return this.text; }
		
		public static Level fromString(String text) {
			if (text != null) {
				for (Level l : Level.values()) {
					if (text.equalsIgnoreCase(l.getText())) {
						return l;
					}
				}
			}
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Private constructor to ensure class methods and attributes can only be statically referenced.
	 */
	private MessageHandler() {}
	
}
