/* 
 * Copyright 2014 Politecnico di Milano.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

 *  @author: Pierluigi Plebani, Politecnico di Milano, Italy
 *  e-mail pierluigi.plebani@polimi.it

 */
package eu.eco2clouds.portal.component;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import eu.eco2clouds.accounting.datamodel.parser.Experiment;
import eu.eco2clouds.accounting.datamodel.parser.ExperimentReport;
import eu.eco2clouds.accounting.datamodel.parser.VMReport;
import eu.eco2clouds.portal.E2CPortal;
import eu.eco2clouds.portal.scheduler.SchedulerManager;
import eu.eco2clouds.portal.scheduler.SchedulerManagerFactory;

public class RecommendationReport extends HorizontalLayout {

    Experiment experiment;

    public RecommendationReport()
    {
        super();
        experiment = ((E2CPortal) UI.getCurrent()).getSessionStatus().getSelectedExperiment();
        
        render();
    }
    
    private void render()
    {
    	HashMap<String, HashMap<String, String>> vmSuggestions = getXml();
    	
        setSpacing(true);
        setMargin(true);

        VerticalLayout vl = new VerticalLayout();

        SchedulerManager sm = SchedulerManagerFactory.getInstance();
        ExperimentReport report = sm.getExperimentReport(experiment.getId());

        vl.addComponent(new Label("<b>" + report.getPowerConsumption().getName() + " :</b> " + report.getPowerConsumption().getValue() + " " + report.getPowerConsumption().getUnity(), ContentMode.HTML));
        vl.addComponent(new Label("<b>" + report.getCo2Generated().getName() + " :</b> " + report.getCo2Generated().getValue() + " " + report.getCo2Generated().getUnity(), ContentMode.HTML));
        
        List<VMReport> vmReports = report.getVmReports();
        if (vmReports != null)
        {
            for (VMReport vmReport : vmReports)
            {
            	HashMap<String, String> vmSuggestion = vmSuggestions.get(vmReport.getHref().split("/")[4]);
                
            	vl.addComponent(new Label("<hr><b>VM " + vmReport.getHref() + ":</b> ", ContentMode.HTML));
                
                vl.addComponent(new Label("<b>Current Host: </b>" + vmSuggestion.get("vm-current-testbed") + "/" + vmSuggestion.get("vm-current-host"), ContentMode.HTML));
                vl.addComponent(new Label("Current Host Energy Consumption: " + vmSuggestion.get("vm-current-host-power"), ContentMode.HTML));
                
                vl.addComponent(new Label("<b>Recommended Host: </b>" + vmSuggestion.get("vm-recommended-testbed") + "/" + vmSuggestion.get("vm-recommended-host"), ContentMode.HTML));
                vl.addComponent(new Label("Recommended Host Energy Consumption: " + vmSuggestion.get("vm-recommended-host-power"), ContentMode.HTML));
            }
        }
        
        else
        {
            addComponent(new Label("no data available for vms"));
        }

        addComponent(vl);
    }
    
    private HashMap<String, HashMap<String, String>> getXml()
    {
        HashMap<String, HashMap<String, String>> vmNodes = new HashMap<String, HashMap<String, String>>();
        
        try
        {
        	InputSource xmlInputSource = new InputSource(new StringReader("<?xml version=\"1.0\" encoding=\"utf-8\"?><experiments BonfireUser=\"uwajid\">"
        			+ "<experiment BonfireId=\"70359\">"
        			+ "<experiment-status>running</experiment-status>"
        			+ "<experiment-co2-consumption>METHOD NOT WORKING</experiment-co2-consumption>"
        			+ "<number-of-vms>6</number-of-vms>"
        			+ "<experiment-vms>"
        			+ "<vm BonfireId=\"25196\">"
        			+ "<vm-name>Node4</vm-name>"
        			+ "<vm-power>25.714286</vm-power"
        			+ "><vm-co2>METHOD NOT AVAILABLE</vm-co2>"
        			+ "<vm-current-host>floccus16</vm-current-host>"
        			+ "<vm-current-testbed>de-hlrs</vm-current-testbed>"
        			+ "<vm-current-host-power>368.0</vm-current-host-power>"
        			+ "<vm-instance-type>large</vm-instance-type>"
        			+ "<vm-recommended-testbed>de-hlrs</vm-recommended-testbed>"
        			+ "<vm-recommended-host>node0114</vm-recommended-host><vm-recommended-host-power>161.0</vm-recommended-host-power></vm><vm BonfireId=\"25197\">"
        			+ "<vm-name>Node3</vm-name><vm-power>25.714286</vm-power><vm-co2>METHOD NOT AVAILABLE</vm-co2><vm-current-host>floccus16</vm-current-host>"
        			+ "<vm-current-testbed>de-hlrs</vm-current-testbed><vm-current-host-power>368.0</vm-current-host-power><vm-instance-type>large</vm-instance-type><vm-recommended-testbed>de-hlrs</vm-recommended-testbed>"
        			+ "<vm-recommended-host>node0114</vm-recommended-host><vm-recommended-host-power>161.0</vm-recommended-host-power></vm><vm BonfireId=\"25198\"><vm-name>Node2</vm-name><vm-power>25.714286</vm-power><vm-co2>METHOD NOT AVAILABLE</vm-co2>"
        			+ "<vm-current-host>floccus16</vm-current-host><vm-current-testbed>de-hlrs</vm-current-testbed><vm-current-host-power>368.0</vm-current-host-power>"
        			+ "<vm-instance-type>large</vm-instance-type><vm-recommended-testbed>de-hlrs</vm-recommended-testbed><vm-recommended-host>node0114</vm-recommended-host>"
        			+ "<vm-recommended-host-power>161.0</vm-recommended-host-power></vm><vm BonfireId=\"25199\"><vm-name>Node1</vm-name><vm-power>25.714286</vm-power>"
        			+ "<vm-co2>METHOD NOT AVAILABLE</vm-co2><vm-current-host>floccus16</vm-current-host><vm-current-testbed>de-hlrs</vm-current-testbed><vm-current-host-power>368.0</vm-current-host-power>"
        			+ "<vm-instance-type>large</vm-instance-type><vm-recommended-testbed>de-hlrs</vm-recommended-testbed><vm-recommended-host>node0114</vm-recommended-host><vm-recommended-host-power>161.0</vm-recommended-host-power>"
        			+ "</vm><vm BonfireId=\"25200\"><vm-name>Master</vm-name><vm-power>25.714286</vm-power><vm-co2>METHOD NOT AVAILABLE</vm-co2><vm-current-host>floccus16</vm-current-host><vm-current-testbed>de-hlrs</vm-current-testbed>"
        			+ "<vm-current-host-power>368.0</vm-current-host-power><vm-instance-type>large</vm-instance-type><vm-recommended-testbed>de-hlrs</vm-recommended-testbed><vm-recommended-host>node0114</vm-recommended-host>"
        			+ "<vm-recommended-host-power>161.0</vm-recommended-host-power></vm><vm BonfireId=\"40855\"><vm-name>BonFIRE-Monitor</vm-name><vm-power>Not Returned from Accounting</vm-power><vm-co2>METHOD NOT AVAILABLE</vm-co2><vm-current-host>vmhost3</vm-current-host><vm-current-testbed>uk-epcc</vm-current-testbed><vm-current-host-power>125.0</vm-current-host-power><vm-instance-type>small</vm-instance-type><vm-recommended-testbed>uk-epcc</vm-recommended-testbed><vm-recommended-host>vmhost4</vm-recommended-host><vm-recommended-host-power>121.0</vm-recommended-host-power></vm></experiment-vms></experiment></experiments>"));
            
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlInputSource);

            doc.getDocumentElement().normalize();

            NodeList vmNodeList = doc.getElementsByTagName("vm");

            for (int nodeIndex = 0; nodeIndex < vmNodeList.getLength(); nodeIndex++)
            {
                Node vmNodeElements = vmNodeList.item(nodeIndex);

                if (vmNodeElements.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element eElement = (Element) vmNodeElements;

                    HashMap<String, String> vmElements = new HashMap<String, String>();

                    String vmCurrentHost = eElement.getElementsByTagName("vm-current-host").item(0).getTextContent();
                    vmElements.put("vm-current-host", vmCurrentHost);

                    String vmCurrentTestbed = eElement.getElementsByTagName("vm-current-testbed").item(0).getTextContent();
                    vmElements.put("vm-current-testbed", vmCurrentTestbed);

                    String vmCurrentHostPower = eElement.getElementsByTagName("vm-current-host-power").item(0).getTextContent();
                    vmElements.put("vm-current-host-power", vmCurrentHostPower);

                    String vmRecommendedHost = eElement.getElementsByTagName("vm-recommended-host").item(0).getTextContent();
                    vmElements.put("vm-recommended-host", vmRecommendedHost);

                    String vmRecommendedTestbed = eElement.getElementsByTagName("vm-recommended-testbed").item(0).getTextContent();
                    vmElements.put("vm-recommended-testbed", vmRecommendedTestbed);

                    String vmRecommendedHostPower = eElement.getElementsByTagName("vm-recommended-host-power").item(0).getTextContent();
                    vmElements.put("vm-recommended-host-power", vmRecommendedHostPower);

                    String vmBonfireId = eElement.getAttribute("BonfireId");
                    vmNodes.put(vmBonfireId, vmElements);
                }
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return vmNodes;
    }
}
