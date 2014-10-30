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

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class MetricsCollectorThreadMetric implements
Callable<ArrayList<Item>> {
	@SuppressWarnings("unused")
	private String workerString;
	private int parentThreadNumber;
	private int threadNumber;
	private SSLSocketFactory sslSocketFactory = null;
	private ArrayList<String> aliases = new ArrayList<String>();
	@SuppressWarnings("unused")
	private long startTime;
	private long currentTime;
	private String metricURL;
	private String metric;
	
	MetricsCollectorThreadMetric(ArrayList<String> aliases, int parentThreadNumber, int threadNumber, SSLSocketFactory sslSocketFactory, 
			long startTime, String metricURL, String metric, long currentTime) {
		this.workerString = "";
		this.parentThreadNumber = parentThreadNumber;
		this.threadNumber = threadNumber;
		this.sslSocketFactory = sslSocketFactory;
		this.aliases = aliases;
		this.startTime = startTime;
		this.currentTime = currentTime;
		this.metricURL = metricURL;
		this.metric = metric;
	}

	@Override
	public ArrayList<Item> call() throws Exception {
		long waitedTime = System.currentTimeMillis() - currentTime;
		long startedAt = System.currentTimeMillis();
		
		MessageHandler.debug("\tMetricsCollectorThreadMetric(): Thread: " + parentThreadNumber + ":" + threadNumber + ", waitedFor:" + waitedTime/1000 + " seconds, URL:" + metricURL + ", aliases:" + aliases + "." ); // DEBUG
		
		ArrayList<Item> itemHistory = getMetric(metricURL, metric);

		long runTime = System.currentTimeMillis() - startedAt;
		long elapsedTime = System.currentTimeMillis() - currentTime;

		MessageHandler.debug("\tMetricsCollectorThreadMetric(): Thread: " + parentThreadNumber + ":" + threadNumber + " FINISHED. Runtime:" + runTime/1000 + " seconds, elapsed:" + elapsedTime/1000 + " seconds [" + metricURL + "]." ); // DEBUG
		
		this.workerString 		= null;	// Help GC
		this.sslSocketFactory 	= null;	// Help GC
		this.aliases 			= null;	// Help GC
		this.metricURL 			= null;	// Help GC
		this.metric 			= null;	// Help GC
		
		return itemHistory;
	}
	
	private ArrayList<Item> getMetric(String metricURLString, String metric) {
		ArrayList<Item> items = new ArrayList<Item>();
		String metricResponse = null;
        
		try {
			InputStream in = getURLContent(metricURLString, true);
			metricResponse = convertStreamToString(in);
			in.close();
		} catch (IOException e) {
        	if ( metric.toLowerCase().startsWith("applicationmetric_"))
        		MessageHandler.debug("getMetric(): [THREAD:" + parentThreadNumber + ":" + threadNumber + "] Communication error attempting to gather metric from url: " + metricURLString + ". Exception Message: " + e.getMessage() + ". Skipping.");
    		else
			MessageHandler.error("getMetric(): [THREAD:" + parentThreadNumber + ":" + threadNumber + "] Communication error attempting to gather metric from url: " + metricURLString + ". Exception Message: " + e.getMessage() + ". Skipping.");
			//e.printStackTrace();
			return null;
		}			
		
		// Build an XML file out of the response
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
	    Document doc = null;
	    try
	    {
	        builder = factory.newDocumentBuilder();
	        doc = builder.parse( new InputSource( new StringReader( metricResponse ) ) );
	    } catch (Exception e) {
			MessageHandler.error("getMetric(): [THREAD:" + parentThreadNumber + ":" + threadNumber + "] XML document is not correct: " + metricResponse + ". Exception Message: " + e.getMessage() + " Skipping.");
			//e.printStackTrace();
			return null;
	    }

		// Get the document's root XML node
        NodeList root = doc.getChildNodes();

        // Navigate down the hierarchy to get to the Site node
        Node nodeMetric = getNode("metric", root);
        if ( nodeMetric == null || !nodeMetric.hasChildNodes()) {
        	nodeMetric = getNode("rawmetric", root);
        	if ( nodeMetric == null || !nodeMetric.hasChildNodes()) {
	        	// Do NOT log an error for the application metric case. We expect that some of them are not there.
	        	if ( metric.toLowerCase().startsWith("applicationmetric_"))
	        		MessageHandler.debug("getMetric(): [THREAD:" + parentThreadNumber + ":" + threadNumber + "] Error in XML file (" + metricURLString + "). Metric is empty. Exiting....");
	    		else
			MessageHandler.error("getMetric(): [THREAD:" + parentThreadNumber + ":" + threadNumber + "] Error in XML file (" + metricURLString + "). Metric is empty. Exiting....");
	        	
				return null;
        	}
        }

		String name;
		int zabbix_itemid;
		long clock;
		double value;
		String unity;

		try {
			Node itemidNode = getNode("itemid", nodeMetric.getChildNodes() );
	        if (itemidNode == null ) {
				MessageHandler.error("getMetric(): [THREAD:" + parentThreadNumber + ":" + threadNumber + "] " + metricURLString + " returned no itemid node. Exiting....");
				return null;
	        }
	        zabbix_itemid = Integer.parseInt(getNodeValue(itemidNode));
	
			Node valueNode = getNode("value", nodeMetric.getChildNodes() );
	        if (valueNode == null ) {
				MessageHandler.error("getMetric(): [THREAD:" + parentThreadNumber + ":" + threadNumber + "] " + metricURLString + " returned no value node. Exiting....");
				return null;
	        }
	        if (getNodeValue(valueNode).length() > 60 )
	        {
				MessageHandler.error("getMetric(): [THREAD:" + parentThreadNumber + ":" + threadNumber + "] " + metricURLString + " returned value node LONGER than 60. Exiting....");
				return null;
	        }
	        value = Double.parseDouble(getNodeValue(valueNode));
	        // The following code causes NumberFormatException exceptions for large values....
	        /*
	        NumberFormat number = NumberFormat.getNumberInstance();
			number.setMaximumFractionDigits(10);
			number.setMaximumIntegerDigits(14);
			String snum = number.format(value);
			value = Double.parseDouble(snum);
			*/
	
			Node clockNode = getNode("clock", nodeMetric.getChildNodes() );
	        if (clockNode == null ) {
				MessageHandler.error("getMetric(): [THREAD:" + parentThreadNumber + ":" + threadNumber + "] " + metricURLString + " returned no clock node. Exiting....");
				return null;
	        }
	        clock = Long.parseLong(getNodeValue(clockNode));
	
			Node keynameNode = getNode("key", nodeMetric.getChildNodes() );
	        if (keynameNode == null ) {
				MessageHandler.error("getMetric(): [THREAD:" + parentThreadNumber + ":" + threadNumber + "] " + metricURLString + " returned no key node. Exiting....");
				return null;
	        }
	        name = getNodeValue(keynameNode);
	        
	        Node keynameName = getNode("name", nodeMetric.getChildNodes() );
	        if (keynameName == null ) {
				MessageHandler.error("getMetric(): [THREAD:" + parentThreadNumber + ":" + threadNumber + "] " + metricURLString + " returned no name node. Exiting....");
				return null;
	        }
	        //name = getNodeValue(keynameName);
	
			Node unityNode = getNode("unit", nodeMetric.getChildNodes() );
	        if (unityNode == null ) {
				MessageHandler.error("getMetric(): [THREAD:" + parentThreadNumber + ":" + threadNumber + "] " + metricURLString + " returned no unit node. Exiting....");
				return null;
	        }
	        unity = getNodeValue(unityNode);
		} catch (NumberFormatException e) {
			MessageHandler.error("getMetric(): [THREAD:" + parentThreadNumber + ":" + threadNumber + "] " + metricURLString + " returned an NumberFormatException Exception [" + e.getMessage() + ". Exiting....");
		    return null;
		} catch (Exception e){
			MessageHandler.error("getMetric(): [THREAD:" + parentThreadNumber + ":" + threadNumber + "] " + metricURLString + " returned a Generic Exception [" + e.getMessage() + ". Exiting....");
		    return null;
		 }

		items.add(new Item(name, zabbix_itemid, clock, value, unity));

		return items;		
	}
	
	
	private InputStream getURLContent(String urlString, boolean authRequired) throws IOException {
		return getURLContent(urlString, authRequired, true);
	}
	
	private InputStream getURLContent(String urlString, boolean authRequired, boolean useBonFIREHeader) throws IOException {
		URL url = null;
		URLConnection con = null;
		InputStream in = null;
		
		// Parse the provided full url string (e.g. https://api.integration.bonfire.grid5000.fr:444/locations/fr-inria/hostmetrics/aliases)
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			MessageHandler.error("getURLContent(): [THREAD:" + parentThreadNumber + ":" + threadNumber + "] URL: " + urlString + " is malformed. Failed to contact remote resource.");
			//e.printStackTrace();
			throw new IOException(e);
		}
		
		try {
			con = url.openConnection();
		} catch (IOException e) {
			MessageHandler.error("getURLContent(): [THREAD:" + parentThreadNumber + ":" + threadNumber + "] Networking error. Unable to open connection to URL: " + urlString + ". Exception: " + e.getMessage());
			//e.printStackTrace();
			throw new IOException(e);
		}
        
		// Set up authentication info: username to assert and client certificate ssl context
		if (authRequired) {
			((HttpsURLConnection)con).setSSLSocketFactory(sslSocketFactory);
			con.setRequestProperty("X-Bonfire-Asserted-Id", ConfigurationValues.apiUser);
		}
		
		if (useBonFIREHeader)
			con.setRequestProperty("Accept", "application/vnd.bonfire+xml");
		else
			con.setRequestProperty("Accept", "*/*");
		con.setRequestProperty("User-Agent", ConfigurationValues.userAgent);

		in = con.getInputStream();
	
		return in;
	}
	
	/**
	 *  Helper function to convert the given InputStream to a string.
	 *
	 * @param is the InputStream to convert to a string
	 * @return the string representation of the InputStream
	 */
	@SuppressWarnings("resource")
	private static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
	/**
	 *  Helper function to return an XML Node from a NodeList
	 *
	 * @param tagName the name of the XML node that we want to retrieve
	 * @param nodes the NodeList that we are searching
	 * @return the Node (null if not found)
	 */
	protected static Node getNode(String tagName, NodeList nodes) {
		for (int x = 0; x < nodes.getLength(); x++) {
			Node node = nodes.item(x);
			if (node.getNodeName().equalsIgnoreCase(tagName)) {
				return node;
			}
		}

		return null;
	}
	
	/**
	 *  Helper function to return the value of a Node
	 *
	 * @param node the input XML Node
	 * @return the value using type String (null if not found)
	 */
	protected static String getNodeValue(Node node) {
		NodeList childNodes = node.getChildNodes();
		for (int x = 0; x < childNodes.getLength(); x++) {
			Node data = childNodes.item(x);
			if (data.getNodeType() == Node.TEXT_NODE)
				return data.getNodeValue();
		}
		return "";
	}
}
