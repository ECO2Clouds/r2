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
//	Last Updated Date :		07 May 2014
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

import java.io.IOException;

/**
 * Wrapper class, the application entry point.
 * Contains a main method that instantiates a MonitoringCollector object and starts it.
 * As the start method is asynchronous, the main thread then waits for a key press to shut down.
 */
public class MainLauncher {
	

	/**
	 * The main method for the application.
	 * Instantiates a MonitoringCollector object and starts it asynchronously.
	 * Waits for a key press to shut down.
	 *
	 * @param args application arguments
	 */
	public static void main(String[] args) {
		MonitoringCollector monitoringCollector = new MonitoringCollector();
		monitoringCollector.start();
		
		MessageHandler.print("Press enter to stop monitoring.");
		
		try { System.in.read(); } catch (IOException e) {
			MessageHandler.error("MainLauncher:main(): Got Exception. IOException Message: " + e.getMessage() + ".");
		}
		
		MessageHandler.print("Main thread stopping Monitoring Collector...");
		monitoringCollector.stop();
		
	}	
}