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
//	Last Updated Date :		28 Aug 2014
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
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import eu.eco2clouds.accounting.monitoringcollector.MessageHandler.Level;

public class MetricsCollectorThread implements
Callable<ArrayList<Item>> {
	/** A pool of threads */
	private ExecutorService tpes 	= Executors.newFixedThreadPool(ConfigurationValues.secondLevelOfThreads);
	
	private static Integer maximumNumberOfMetrics = 37;
	/** */
    private MetricsCollectorThreadMetric workers[]	= new MetricsCollectorThreadMetric[maximumNumberOfMetrics];
    
    /** */
    @SuppressWarnings("rawtypes")
	private Future futures[] 		= new Future[maximumNumberOfMetrics];

	@SuppressWarnings("unused")
	private String workerString;
	private int workerNumber;
	private SSLSocketFactory sslSocketFactory = null;
	private ArrayList<String> aliases = new ArrayList<String>();
	private long startTime;
	private String aliasesURLString;
	private String rawURLString;
	private long currentTime;
	private static ArrayList<String> applicationMetrics = new ArrayList<String>(); 

	MetricsCollectorThread(ArrayList<String> aliases, int number, SSLSocketFactory sslSocketFactory, 
							long startTime, String aliasesURLString, String rawURLString, long currentTime) {
		this.workerString = "";
		this.workerNumber = number;
		this.sslSocketFactory = sslSocketFactory;
		this.aliases = aliases;
		this.startTime = startTime;
		this.aliasesURLString = aliasesURLString;
		this.rawURLString = rawURLString;
		this.currentTime = currentTime;
	}

	public ArrayList<Item> call() {
		applicationMetrics.add("applicationmetric_2");
		applicationMetrics.add("applicationmetric_3");
		applicationMetrics.add("applicationmetric_4");
		applicationMetrics.add("applicationmetric_5");
		
		// Say hi
		long waitedTime = System.currentTimeMillis() - currentTime;
		long startedAt = System.currentTimeMillis();
		
		MessageHandler.debug("MetricsCollectorThread(): Thread: " + workerNumber + ", waitedFor:" + waitedTime/1000 + " seconds, URL:" + aliasesURLString + ", raw:" + rawURLString + ", aliases:" + aliases + "." ); // DEBUG
		ArrayList<Item> items = new ArrayList<Item>();
		int i = 0;
		
		// Perform some work ...
		for (String metric : aliases) {	
			// Get all values of this metric recorded in Zabbix while the Collector was asleep.
			// Call is for <URL>/history_metrics/(pollingRate + processingTime).
			// Since history_metrics precision is only available in seconds, rounding up may result in duplicate metrics being gathered.
			// The SQL statements for inserting Items data ensure any duplicates are not recorded in the metrics database.  
			long processingTime = System.currentTimeMillis() - startTime;
			@SuppressWarnings("unused")
			long time = ConfigurationValues.pollingRate + (long)Math.ceil( (processingTime / 1000.0) );

			//String metricURL = aliasesURLString + "/" + metric + "/history_metrics/" + time;
			String metricURL = aliasesURLString + "/" + metric;  // Only gets single latest value, chance for a metric to be missed
			
			if ( !metric.equals("applicationmetric_1") )
			this.workers[i] = new MetricsCollectorThreadMetric(aliases, workerNumber, i, sslSocketFactory, startedAt, metricURL, metric, System.currentTimeMillis());
			else if ( metric.equals("applicationmetric_1") )
				this.workers[i] = new MetricsCollectorThreadMetric(aliases, workerNumber, i, sslSocketFactory, startedAt, rawURLString + "/" + metric, metric, System.currentTimeMillis());
			this.futures[i] = this.tpes.submit(workers[i]);
			i++;
		}
		
		i = 0;
		for (String metric : aliases) {
			try {
				ArrayList<Item> itemHistory = (ArrayList<Item>) this.futures[i].get();
				if ( itemHistory == null ) {
					if ( !metric.equals("applicationmetric_1") )
					MessageHandler.error("MetricsCollectorThread(): " + i + "[" + aliasesURLString + "/" + metric + "], returned itemHistory is NULL!!!"); // DEBUG
					else if ( metric.equals("applicationmetric_1") )
						MessageHandler.debug("MetricsCollectorThread(): " + i + " [" + rawURLString + "/" + metric + "], returned itemHistory is NULL!!!"); // DEBUG
				}
			}
			catch (Exception e) {}
			i++;
		}
		
		
		i = 0;
		for (@SuppressWarnings("unused") String metric : aliases) {
            try {
            	ArrayList<Item> itemHistory = (ArrayList<Item>) this.futures[i].get();
					for( Item localItem : itemHistory )
					MessageHandler.debug("MetricsCollectorThread(): \t\t " + workerNumber + ", metric:" + ", \tItem(name:" + localItem.name + ", zabbix_itemid:" + localItem.zabbix_itemid + ", clock:" + localItem.clock + ", value:" + localItem.value + ", unity:" + localItem.unity); // DEBUG

				if ( metric.equals("applicationmetric_1") ) {
					// Special handling for the applicationMetric Metrics
					if ( itemHistory != null || itemHistory.size() > 0) {
						MessageHandler.debug("MetricsCollectorThread(): \t " + workerNumber + ", item:" + i + ". Found application metric."); // DEBUG
						int y = 0;
						for (String appmetric : applicationMetrics ) {
							int threadNumber = maximumNumberOfMetrics - 1 - y;
							this.workers[threadNumber] = new MetricsCollectorThreadMetric(aliases, workerNumber, threadNumber, sslSocketFactory, startedAt, rawURLString + "/" + appmetric, appmetric, System.currentTimeMillis());
							this.futures[threadNumber] = this.tpes.submit(workers[threadNumber]);
							
							ArrayList<Item> itemHistoryApplicationMetric = (ArrayList<Item>) this.futures[threadNumber].get();
							if ( itemHistoryApplicationMetric == null )  {
								MessageHandler.debug("MetricsCollectorThread(): " + workerNumber + ", item:" + i + "/" + threadNumber + " [" + rawURLString + "/" + appmetric + "], returned itemHistory is NULL. Breaking!!!!"); // DEBUG
								break;
							}
							else if ( itemHistoryApplicationMetric.size() == 0 )  {
								MessageHandler.debug("MetricsCollectorThread(): " + workerNumber + ", item:" + i + "/" + threadNumber + " [" + rawURLString + "/" + appmetric + "], returned itemHistory is NULL. Breaking 1!!!!"); // DEBUG
								break;
							} else {
								for( Item localItem : itemHistoryApplicationMetric )
									MessageHandler.debug("MetricsCollectorThread(): " + workerNumber + ", item:" + i + "/" + threadNumber + "\t\t\t metric:" + appmetric + ", \tItem(name:" + localItem.name + ", zabbix_itemid:" + localItem.zabbix_itemid + ", clock:" + localItem.clock + ", value:" + localItem.value + ", unity:" + localItem.unity); // DEBUG
							}
								
							itemHistory.addAll(itemHistoryApplicationMetric);
							this.workers[threadNumber] = null;	// Help GC
							this.futures[threadNumber] = null;	// Help GC
							y++;
						}
					}
				}

            	items.addAll(itemHistory);
            } catch (Exception e) {}
            
            this.workers[i] = null;	// Help GC
			this.futures[i] = null;	// Help GC
            i++;
        }
		
		this.tpes.shutdown();			// Help GC
		this.workerString 		= null;	// Help GC
		this.sslSocketFactory 	= null;	// Help GC
		this.aliases 			= null;	// Help GC
		
		long runTime = System.currentTimeMillis() - startedAt;
		long elapsedTime = System.currentTimeMillis() - currentTime;

		MessageHandler.debug("MetricsCollectorThread(): Thread: " + workerNumber + " FINISHED. Runtime:" + runTime/1000 + " seconds, elapsed:" + elapsedTime/1000 + " seconds [" + aliasesURLString + "]." ); // DEBUG
		
		this.aliasesURLString 	= null;	// Help GC
		this.tpes.shutdownNow();		// Help GC
		this.tpes 				= null;	// Help GC
		return items;
	}
	
}
