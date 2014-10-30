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
//	Last Updated Date :		23 Aug 2013
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

/**
 * A base class that abstracts the threading code from any Monitoring Collector implementation.
 * On start(), creates new threads for the monitoring component and the collecting component and starts them asynchronously.
 * On stop(), signals the components to shut down and joins the threads.
 */
public abstract class AbstractMonitoringCollector {

	/** The monitoring component of the Monitoring Collector. Subscribes to the BonFIRE Management Message Queue to watch for ECO2Clouds events. */
	Monitor monitor;
	
	/** The collecting component of the Monitoring Collector. Periodically queries the BonFIRE API for metrics on active resources. */
	Collector collector;
	
	/** The monitoring thread. */
	private Thread monitorThread = null;
	
	/** The collecting thread. */
	private Thread collectorThread = null;
	
	/** Gates start and stop function. */
	public boolean started = false;
	
	/** The Constant TIMEOUT. Used to limit the time spent waiting for thread joins when the Monitoring Collector is shutting down. Set to 250ms.  */
	private final static long TIMEOUT = 250;
	
	/**
	 * Instantiates a new abstract monitoring collector.
	 */
	public AbstractMonitoringCollector() {}
	
	/**
	 * Instantiates a new abstract monitoring collector with the supplied monitor and collector objects.
	 *
	 * @param monitor the monitor
	 * @param collector the collector
	 */
	public AbstractMonitoringCollector(Monitor monitor, Collector collector) {
		this.monitor = monitor;
		this.collector = collector;
		this.started = false;
	}
	
	/**
	 * Sets up and starts threads for the monitor and collector. Sets both threads to daemons to avoid hanging the JVM on failure.
	 */
	public void start() {
		if (!started) {
			
			Thread monitorThread = new Thread(monitor);
			this.monitorThread = monitorThread;
			monitorThread.setDaemon(true);
			monitorThread.start();
			
			Thread collectorThread = new Thread(collector);
			this.collectorThread = collectorThread;
			collectorThread.setDaemon(true);
			collectorThread.start();
			
			started = true;
		}
	}

	/**
	 * Stops and attempts to clean up threads.
	 * Some RabbitMQ functions swallow the InterruptedException used to exit while the monitor is performing a blocking wait on the Management Message Queue.
	 * Timeout on thread join added to handle this.
	 */
	public void stop() {
		if (started) {

			// Threads are daemons so will not halt the JVM if join reaches timeout.			
			if (monitorThread != null) {
				
				this.monitorThread.interrupt();
				try { this.monitorThread.join(TIMEOUT); } catch (InterruptedException e) {}
				this.monitorThread = null;
				
			}
			
			if (collectorThread != null) {
				
				this.collectorThread.interrupt();
				try { this.collectorThread.join(TIMEOUT); } catch (InterruptedException e) {}
				this.collectorThread = null;
				
			}
			
			started = false;
		}
	}

}