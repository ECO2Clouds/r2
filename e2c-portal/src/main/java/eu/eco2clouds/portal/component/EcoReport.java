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
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 *
 *  
 */
public class EcoReport extends HorizontalLayout {

    Experiment experiment;

    public EcoReport() {
        super();
        this.experiment = ((E2CPortal) UI.getCurrent()).getSessionStatus().getSelectedExperiment();

        this.render();
    }

    private Collection<MonitoredItem> generate() {

        long now = System.currentTimeMillis();

        List<MonitoredItem> list = new LinkedList<MonitoredItem>();
        for (int i = 0; i < 1000; i++) {
            MonitoredItem mi = new MonitoredItem(now + (i * 1000), Math.random());
            list.add(mi);
        }

        return list;
    }

    private void render() {
        this.setSpacing(true);
        this.setMargin(true);
        this.setSizeFull();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY hh:mm");
        SimpleDateFormat edf = new SimpleDateFormat("dd 'days' hh 'hours' mm ' minutes'");

        VerticalLayout vl = new VerticalLayout();

        SchedulerManager sm = SchedulerManagerFactory.getInstance();
        ExperimentReport report = sm.getExperimentReport(this.experiment.getId());

        vl.addComponent(new Label("<b>id</b>: " + experiment.getId(), ContentMode.HTML));
        vl.addComponent(new Label("<b>user</b>: " + experiment.getBonfireUserId() + " <b>group</b>: " + experiment.getBonfireGroupId(), ContentMode.HTML));
        if (!experiment.getStatus().equalsIgnoreCase("terminated")) {
            vl.addComponent(new Label("<b>started</b>: " + sdf.format(new Date(experiment.getStartTime())) + " <b>planned termination</b>: " + sdf.format(new Date(experiment.getEndTime())), ContentMode.HTML));
            //details.addComponent(new Label(edf.format(new Date(experiment.getEndTime() - experiment.getStartTime())) + " to go"));
        } else {
            vl.addComponent(new Label("<b>started</b>: " + sdf.format(new Date(experiment.getStartTime())) + " <b>terminated</b>: " + sdf.format(new Date(experiment.getEndTime())), ContentMode.HTML));
            vl.addComponent(new Label("lasted for " + edf.format(new Date(experiment.getEndTime() - experiment.getStartTime()))));
        }

        vl.addComponent(new Label("<b>id managed:</b>" + report.getHref() + "<hr>", ContentMode.HTML));

        double pts = Math.round(report.getPowerConsumption().getValue() * 1000.00) / 1000.00;       
        double cts = Math.round(report.getCo2Generated().getValue() * 1000.00) / 1000.00;
        
        vl.addComponent(new Label("<b>Energy total :</b> " + pts + " " + report.getPowerConsumption().getUnity(), ContentMode.HTML));
        vl.addComponent(new Label("<b>CO2 total :</b>" + cts + " " + report.getCo2Generated().getUnity(), ContentMode.HTML));

        double totPower = report.getPowerConsumption().getValue();
        double totCO2 = report.getCo2Generated().getValue();
        //this.addComponent(new Label("<b> Energy consumed by the experiment :</b> 120 Wh", ContentMode.HTML));
        //this.addComponent(new Label("<b> Total CO2 emissions:</b> 79.8 grCO2e", ContentMode.HTML));

        /*this.addComponent(new Label("<hr><b>VM1 @ FR-INRIA:</b> ", ContentMode.HTML));
         this.addComponent(new Label("<b> Energy consumed by the VM :</b> 60Wh", ContentMode.HTML));
         this.addComponent(new Label("<b> VM CO2 emissions :</b> 34.9 grCO2e", ContentMode.HTML));
         this.addComponent(new Label("<hr><b>VM2 @ FR-INRIA:</b> ", ContentMode.HTML));
         this.addComponent(new Label("<b> Energy consumed by the VM :</b> 40Wh", ContentMode.HTML));
         this.addComponent(new Label("<b> VM CO2 emissions :</b> 23.3 grCO2e", ContentMode.HTML));
         this.addComponent(new Label("<hr><b>VM3 @ UK-EPCC:</b> ", ContentMode.HTML));
         this.addComponent(new Label("<b> Energy consumed by the VM :</b> 20Wh", ContentMode.HTML));
         this.addComponent(new Label("<b> VM CO2 emissions :</b> 21.6 grCO2e", ContentMode.HTML));*/
        TreeMap<String, Double> powerData = new TreeMap<String, Double>();
        TreeMap<String, Double> co2Data = new TreeMap<String, Double>();

        double[] powerSites = new double[]{0.0, 0.0, 0.0};
        double[] co2Sites = new double[]{0.0, 0.0, 0.0};

        List<VMReport> vmReports = report.getVmReports();
        if (vmReports != null) {
            for (VMReport vmReport : vmReports) {

                double ps = Math.round(vmReport.getPowerConsumption().getValue() * 1000.00)/1000.00;

                double cs = Math.round(vmReport.getCo2Generated().getValue() * 1000.00)/1000.00;
                
                vl.addComponent(new Label("<hr><b>VM " + vmReport.getHref() + ":</b> ", ContentMode.HTML));
                vl.addComponent(new Label("<b>Energy consumed by the VM :</b> " + ps + " " + vmReport.getPowerConsumption().getUnity(), ContentMode.HTML));
                vl.addComponent(new Label("<b>" + vmReport.getCo2Generated().getName() + " :</b> " + cs + " " + vmReport.getCo2Generated().getUnity(), ContentMode.HTML));

                String[] namevm = vmReport.getHref().split("/");

                if (namevm[2].equals("de-hlrs")) {
                    powerSites[0] = powerSites[0] + vmReport.getPowerConsumption().getValue();
                    //System.out.println("add chart inria" + powerSites[0]);
                    co2Sites[0] = co2Sites[0] + vmReport.getCo2Generated().getValue();
                }
                if (namevm[2].equals("fr-inria")) {
                    powerSites[1] = powerSites[1] + vmReport.getPowerConsumption().getValue();
                    //System.out.println("add chart epcc" + powerSites[1]);
                    co2Sites[1] = co2Sites[1] + vmReport.getCo2Generated().getValue();
                }
                if (namevm[2].equals("uk-epcc")) {
                    powerSites[2] = powerSites[2] + vmReport.getPowerConsumption().getValue();
                    //System.out.println("add chart hlrs" + powerSites[2]);
                    co2Sites[2] = co2Sites[2] + vmReport.getCo2Generated().getValue();
                }

                //System.out.println("add chart " + namevm[2] + ", " + namevm[namevm.length-1] + " = " +  vmReport.getPowerConsumption().getValue()/totPower);
                double p = vmReport.getPowerConsumption().getValue() / totPower * 100;
                p = Math.round(p * 100.00) / 100.00;
                powerData.put(namevm[2] + ", " + namevm[namevm.length - 1], p);

                double c = vmReport.getCo2Generated().getValue() / totCO2 * 100;
                c = Math.round(c * 100.00) / 100.00;
                co2Data.put(namevm[2] + ", " + namevm[namevm.length - 1], c);
            }
        } else {
            this.addComponent(new Label("no data available for vms"));
        }

        this.addComponent(vl);

        for (int i = 0; i < 3; i++) {
            powerSites[i] = powerSites[i] / totPower * 100.00;
            co2Sites[i] = co2Sites[i] / totCO2 * 100.00;

            powerSites[i] = Math.round(powerSites[i] * 100.00) / 100.00;
            co2Sites[i] = Math.round(co2Sites[i] * 100.00) / 100.00;
        }

        ECOReportPieChart chartLayout = new ECOReportPieChart(powerSites, co2Sites, powerData, co2Data);
        this.addComponent(chartLayout);

    }

    private void renderold() {

        this.setSpacing(true);
        this.setMargin(true);

        HorizontalLayout tables = new HorizontalLayout();
        tables.setSpacing(true);
        tables.setMargin(false);

        tables.addComponent(new ReducedResourceTable());
        tables.addComponent(new MetricTable());

        HorizontalLayout charts = new HorizontalLayout();
        charts.setSpacing(true);
        charts.setMargin(false);

        TimeLineChart chart1 = new TimeLineChart();
        chart1.show(this.generate());

        TimeLineChart chart2 = new TimeLineChart();
        chart2.show(this.generate());

        charts.addComponent(chart1);
        charts.addComponent(chart2);

        this.addComponent(tables);
        this.addComponent(charts);

    }
}
