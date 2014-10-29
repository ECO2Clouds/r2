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
package eu.eco2clouds.portal.component.apwizard;

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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;
import eu.eco2clouds.portal.compute.SourcePercentage;
import java.util.Arrays;

/**
 *
 *  
 */
public class GECWindow extends Window {
    
    
    SourcePercentage sp;
    private static Color[] colors = new VaadinTheme().getColors();

    String combination;
    
    public GECWindow(String combination, SourcePercentage sp) {
        
        super();
        this.setHeight("600px");
        this.setWidth("900px");
        
        this.sp = sp;
        this.combination = combination;
        
        this.render();
    }
    
    private void render() {
        
        this.center();
        this.setModal(true);

        Chart percentageChart = new Chart(ChartType.PIE);

        percentageChart.setImmediate(true);

        Configuration conf = percentageChart.getConfiguration();

        conf.setTitle("Power sources: " + combination);

        PlotOptionsPie pie = new PlotOptionsPie();
        pie.setShadow(false);
        conf.setPlotOptions(pie);

        conf.getTooltip().setValueSuffix("%");

        DataSeries innerSeries = new DataSeries();
        innerSeries.setName("Sources");
        PlotOptionsPie innerPieOptions = new PlotOptionsPie();
        innerSeries.setPlotOptions(innerPieOptions);
        innerPieOptions.setSize(237);
        innerPieOptions.setDataLabels(new Labels());
        innerPieOptions.getDataLabels().setFormatter(
                "this.y > 0.001 ? this.point.name : null");
        innerPieOptions.getDataLabels().setColor(new SolidColor(255, 255, 255));
        innerPieOptions.getDataLabels().setDistance(-30);

        Color[] innerColors = Arrays.copyOf(colors, 2);
        innerSeries.setData(new String[]{"green", "non-green"},
                new Number[]{sp.getGEC(), (100-sp.getGEC())}, new Color[]{SolidColor.GREEN, SolidColor.GRAY});

        DataSeries outerSeries = new DataSeries();
        outerSeries.setName("GEC");
        PlotOptionsPie outerSeriesOptions = new PlotOptionsPie();
        outerSeries.setPlotOptions(outerSeriesOptions);
        outerSeriesOptions.setInnerSize(237);
        outerSeriesOptions.setSize(318);
        outerSeriesOptions.setDataLabels(new Labels());
        outerSeriesOptions
                .getDataLabels()
                .setFormatter(
                        "this.y > 0.001 ? ''+ this.point.name +': '+ this.y +'%' : null");
        
        DataSeriesItem[] outerItems = new DataSeriesItem[9];

        outerItems[0] = new DataSeriesItem("wind", sp.getWind(), SolidColor.GREEN);
        outerItems[1] = new DataSeriesItem("solar", sp.getSolar(), SolidColor.GREEN);
        outerItems[2] = new DataSeriesItem("renewable", sp.getRenewable(), SolidColor.GREEN);
        outerItems[3] = new DataSeriesItem("nuclear", sp.getNuclear(), SolidColor.GRAY);
        outerItems[4] = new DataSeriesItem("oil", sp.getOil(), SolidColor.GRAY);
        outerItems[5] = new DataSeriesItem("gaz", sp.getGaz(), SolidColor.GRAY);
        outerItems[6] = new DataSeriesItem("hydro", sp.getHydro(), SolidColor.GRAY);
        outerItems[7] = new DataSeriesItem("coal", sp.getCoal(), SolidColor.GRAY);
        outerItems[8] = new DataSeriesItem("other", sp.getOther(), SolidColor.GRAY);

        outerSeries.setData(Arrays.asList(outerItems));

        
        conf.setSeries(innerSeries, outerSeries);

        percentageChart.drawChart(conf);

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSizeFull();
        hl.setMargin(true);
        hl.setSpacing(false);
        hl.addComponent(percentageChart);
        
        this.setContent(hl);
        
    }
    
    
}
