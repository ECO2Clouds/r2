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
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.PlotOptionsSeries;
import com.vaadin.addon.charts.model.Stacking;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;
import eu.eco2clouds.portal.compute.CO2Item;

/**
 *
 *  
 */
public class CO2CountryChart extends Chart {
    
    CO2Item[] co2_fr = null;
    CO2Item[] co2_uk = null;
    CO2Item[] co2_de = null;
    int no_combinations;
    
    double cpuload;
    
    String date;
    
    int type = 0;
    
    public CO2CountryChart(String date, int no_combinations, CO2Item[] co2_fr, CO2Item[] co2_uk, CO2Item[] co2_de, double cpuload) {
        
        super(ChartType.BAR);
        
        this.setImmediate(true);
        
        this.date = date;
        
        Configuration conf = this.getConfiguration();
        
        conf.setTitle("Estimated CO2 emissions @" + date);
        
        YAxis y = new YAxis();
        y.setMin(0);
        y.setTitle("CO2 gr");
        conf.addyAxis(y);
        
        this.no_combinations = no_combinations;
        this.co2_fr = co2_fr;
        this.co2_uk = co2_uk;
        this.co2_de = co2_de;
        
        this.cpuload = cpuload;
        
        if (this.no_combinations > 0 && this.co2_de != null) {
            this.render();
        }
        
    }
    
    public void toggleAspect() {
        Configuration conf = this.getConfiguration();
        PlotOptionsSeries plot = new PlotOptionsSeries();
        if (type == 0) {
            plot.setStacking(Stacking.PERCENT);
            
            type = 1;
        } else {
            plot.setStacking(Stacking.NORMAL);
            type = 0;
        }
        conf.setPlotOptions(plot);
        this.drawChart(conf);
    }
    
    private void render() {

        //this.setWidth("400px");
        //this.setHeight("300px");
        this.setHeight("500px");
        
        Configuration conf = this.getConfiguration();
        
        PlotOptionsSeries plot = new PlotOptionsSeries();
        plot.setStacking(Stacking.NORMAL);
        conf.setPlotOptions(plot);
        
        XAxis x = new XAxis();
        String categories[] = new String[this.no_combinations * 2];
        for (int i = 0; i < this.no_combinations; i++) {
            categories[(2 * i)] = Integer.toString(i + 1) + "-min";
            categories[(2 * i) + 1] = Integer.toString(i + 1) + "-max";
            
        }
        x.setCategories(categories);
        conf.addxAxis(x);
        
        Double[] frD = new Double[this.no_combinations * 2];
        Double[] deD = new Double[this.no_combinations * 2];
        Double[] ukD = new Double[this.no_combinations * 2];
       // Double[] totD = new Double[this.no_combinations * 2];
        
        for (int i = 0; i < this.no_combinations; i++) {
            frD[(2 * i)] = this.co2_fr[i].getMin() * this.cpuload;
            frD[(2 * i) + 1] = this.co2_fr[i].getMax() * this.cpuload;
            
            ukD[(2 * i)] = this.co2_uk[i].getMin() * this.cpuload;
            ukD[(2 * i) + 1] = this.co2_uk[i].getMax() * this.cpuload;
            
            deD[(2 * i)] = this.co2_de[i].getMin() * this.cpuload;
            deD[(2 * i) + 1] = this.co2_de[i].getMax() * this.cpuload;
            
            //totD[2 * i] = this.co2_fr[i].getMin() * this.cpuload + this.co2_uk[i].getMin() * this.cpuload + this.co2_de[i].getMin() * this.cpuload;
            //totD[(2 * i) + 1] = this.co2_fr[i].getMax() * this.cpuload + this.co2_uk[i].getMax() * this.cpuload + this.co2_de[i].getMax() * this.cpuload;
            
        }
        conf.addSeries(new ListSeries("FR", frD));
        conf.addSeries(new ListSeries("UK", ukD));
        conf.addSeries(new ListSeries("DE", deD));
        //conf.addSeries(new ListSeries("Tot", totD));

//        if (this.co2emissions != null) {
//            DataSeries dataSeries = new DataSeries();
//            dataSeries.setData(new String[]{"France", "Germany", "UK", "Total"}, co2emissions);
//            dataSeries.setName(this.date);
//            conf.setSeries(dataSeries);
//        }
        this.drawChart(conf);
        
        
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
}
