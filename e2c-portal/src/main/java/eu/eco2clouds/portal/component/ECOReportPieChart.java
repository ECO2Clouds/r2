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

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.addon.charts.themes.VaadinTheme;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;
import java.util.Arrays;
import java.util.Random;
import java.util.TreeMap;

/**
 *
 *  
 */
public class ECOReportPieChart extends VerticalLayout {

    private static Random rand = new Random(0);

    private static Color[] colors = new VaadinTheme().getColors();

    TreeMap<String, Double> vmPower;
    TreeMap<String, Double> vmCo2;

    double[] powerSites;
    double[] co2Sites;

    Chart powerChart;
    Chart co2Chart;

    public ECOReportPieChart(double[] powerSites, double[] co2Sites, TreeMap<String, Double> vmPower, TreeMap<String, Double> vmCo2) {
        this.vmPower = vmPower;
        this.vmCo2 = vmCo2;

        this.powerSites = powerSites;
        this.co2Sites = co2Sites;

        this.render();
    }

    private void createPowerPie() {

        this.powerChart = new Chart(ChartType.PIE);

        this.powerChart.setImmediate(true);

        Configuration conf = this.powerChart.getConfiguration();

        conf.setTitle("Power consumption");

        PlotOptionsPie pie = new PlotOptionsPie();
        pie.setShadow(false);
        conf.setPlotOptions(pie);

        conf.getTooltip().setValueSuffix("%");

        DataSeries innerSeries = new DataSeries();
        innerSeries.setName("Sites");
        PlotOptionsPie innerPieOptions = new PlotOptionsPie();
        innerSeries.setPlotOptions(innerPieOptions);
        innerPieOptions.setSize(237);
        innerPieOptions.setDataLabels(new Labels());
        innerPieOptions.getDataLabels().setFormatter(
                "this.y > 0.001 ? this.point.name : null");
        innerPieOptions.getDataLabels().setColor(new SolidColor(255, 255, 255));
        innerPieOptions.getDataLabels().setDistance(-30);

        Color[] innerColors = Arrays.copyOf(colors, 3);
        innerSeries.setData(new String[]{"de-hlrs", "fr-inria", "uk-epcc"},
                new Number[]{this.powerSites[0], this.powerSites[1], this.powerSites[2]}, innerColors);

        DataSeries outerSeries = new DataSeries();
        outerSeries.setName("VMs");
        PlotOptionsPie outerSeriesOptions = new PlotOptionsPie();
        outerSeries.setPlotOptions(outerSeriesOptions);
        outerSeriesOptions.setInnerSize(237);
        outerSeriesOptions.setSize(318);
        outerSeriesOptions.setDataLabels(new Labels());
        outerSeriesOptions
                .getDataLabels()
                .setFormatter(
                        "this.y > 0.001 ? ''+ this.point.name +': '+ this.y +'%' : null");

        DataSeriesItem[] outerItems = new DataSeriesItem[this.vmPower.size()];

        int i = 0;
        for (String vm : this.vmPower.keySet()) {
            String site = vm.split(",")[0];
            String id = vm.split(",")[1];
            if (site.equals("de-hlrs")) {
                outerItems[i] = new DataSeriesItem(id, this.vmPower.get(vm), color(0));
            }
            if (site.equals("fr-inria")) {
                outerItems[i] = new DataSeriesItem(id, this.vmPower.get(vm), color(1));
            }
            if (site.equals("uk-epcc")) {
                outerItems[i] = new DataSeriesItem(id, this.vmPower.get(vm), color(2));
            }
            i++;
        }

        outerSeries.setData(Arrays.asList(outerItems));
        conf.setSeries(innerSeries, outerSeries);

        this.powerChart.drawChart(conf);
    }

    private void createCO2Pie() {

        this.co2Chart = new Chart(ChartType.PIE);

        this.co2Chart.setImmediate(true);

        Configuration conf = this.co2Chart.getConfiguration();

        conf.setTitle("CO2 emissions");

        PlotOptionsPie pie = new PlotOptionsPie();
        pie.setShadow(false);
        conf.setPlotOptions(pie);

        conf.getTooltip().setValueSuffix("%");

        DataSeries innerSeries = new DataSeries();
        innerSeries.setName("Sites");
        PlotOptionsPie innerPieOptions = new PlotOptionsPie();
        innerSeries.setPlotOptions(innerPieOptions);
        innerPieOptions.setSize(237);
        innerPieOptions.setDataLabels(new Labels());
        innerPieOptions.getDataLabels().setFormatter(
                "this.y > 0.001 ? this.point.name : null");
        innerPieOptions.getDataLabels().setColor(new SolidColor(255, 255, 255));
        innerPieOptions.getDataLabels().setDistance(-30);

        Color[] innerColors = Arrays.copyOf(colors, 3);
        innerSeries.setData(new String[]{"de-hlrs", "fr-inria", "uk-epcc"},
                new Number[]{this.co2Sites[0], this.co2Sites[1], this.co2Sites[2]}, innerColors);

        DataSeries outerSeries = new DataSeries();
        outerSeries.setName("VMs");
        PlotOptionsPie outerSeriesOptions = new PlotOptionsPie();
        outerSeries.setPlotOptions(outerSeriesOptions);
        outerSeriesOptions.setInnerSize(237);
        outerSeriesOptions.setSize(318);
        outerSeriesOptions.setDataLabels(new Labels());
        outerSeriesOptions
                .getDataLabels()
                .setFormatter(
                        "this.y > 0.001 ? ''+ this.point.name +': '+ this.y +'%' : null");

        DataSeriesItem[] outerItems = new DataSeriesItem[this.vmCo2.size()];

        int i = 0;
        for (String vm : this.vmCo2.keySet()) {
            String site = vm.split(",")[0];
            String id = vm.split(",")[1];
            if (site.equals("de-hlrs")) {
                outerItems[i] = new DataSeriesItem(id, this.vmCo2.get(vm), color(0));
            }
            if (site.equals("fr-inria")) {
                outerItems[i] = new DataSeriesItem(id, this.vmCo2.get(vm), color(1));
            }
            if (site.equals("uk-epcc")) {
                outerItems[i] = new DataSeriesItem(id, this.vmCo2.get(vm), color(2));
            }
            i++;
        }

        outerSeries.setData(Arrays.asList(outerItems));
        conf.setSeries(innerSeries, outerSeries);

        this.co2Chart.drawChart(conf);
    }

    private void render() {

        this.setSpacing(true);
        this.setMargin(false);
        this.setSizeFull();

        this.createPowerPie();
        this.createCO2Pie();

        /*VerticalLayout vl = new VerticalLayout();
        vl.setMargin(false);
        vl.setSpacing(true);
        vl.setSizeFull();*/

        this.addComponent(this.powerChart);
        this.addComponent(this.co2Chart);

        /*vl.setComponentAlignment(this.powerChart, Alignment.TOP_CENTER);
        vl.setComponentAlignment(this.co2Chart, Alignment.TOP_CENTER);
        this.addComponent(vl);*/
    }

    private static SolidColor color(int colorIndex) {
        SolidColor c = (SolidColor) colors[colorIndex];
        String cStr = c.toString().substring(1);

        int r = Integer.parseInt(cStr.substring(0, 2), 16);
        int g = Integer.parseInt(cStr.substring(2, 4), 16);
        int b = Integer.parseInt(cStr.substring(4, 6), 16);

        double opacity = (50 + rand.nextInt(95 - 50)) / 100.0;

        return new SolidColor(r, g, b, opacity);
    }
}
