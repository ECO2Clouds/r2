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

import com.vaadin.addon.timeline.Timeline;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import eu.eco2clouds.portal.compute.CO2Item;
import eu.eco2clouds.portal.compute.OctaveManager;
import eu.eco2clouds.portal.compute.PowerItem;
import eu.eco2clouds.portal.scheduler.MonitoringManagerFactory;
import eu.eco2clouds.portal.scheduler.SchedulerManagerFactory;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 *  
 */
public class CO2PredictionChart extends Timeline {


    public CO2PredictionChart() {
        super();


        this.setWidth("100%");
        this.setHeight("300px");
        this.setImmediate(true);
        this.addZoomLevel("Hour", 86400000L /24);
        this.addZoomLevel("Day", 86400000L);
        this.addZoomLevel("Week", 7 * 86400000L);
        this.addZoomLevel("Month", 2629743830L);

    }

    public void update(Date start, PowerItem power_fr, PowerItem power_uk, PowerItem power_de, long duration) {

        this.removeAllGraphDataSources();

        System.out.println("in chart fr " + power_fr);
        System.out.println("in chart uk " + power_uk);
        System.out.println("in chart de " + power_de);
        
        Container.Indexed fr_ic =this.getPredictionByCountry(start, "fr-inria", power_fr, duration); 
        Container.Indexed uk_ic =this.getPredictionByCountry(start, "uk-epcc", power_uk, duration); 
        Container.Indexed de_ic =this.getPredictionByCountry(start, "de-hlrs", power_de, duration); 

        this.addGraphDataSource(fr_ic, Timeline.PropertyId.TIMESTAMP, Timeline.PropertyId.VALUE);
        this.addGraphDataSource(uk_ic, Timeline.PropertyId.TIMESTAMP, Timeline.PropertyId.VALUE);
        this.addGraphDataSource(de_ic, Timeline.PropertyId.TIMESTAMP, Timeline.PropertyId.VALUE);
        
        this.setGraphCaption(fr_ic, "fr-inria");
        this.setGraphCaption(uk_ic, "uk-epcc");
        this.setGraphCaption(de_ic, "de-hlrs");
        
        this.setGraphOutlineColor(fr_ic, Color.BLUE);
        this.setGraphOutlineColor(uk_ic, Color.GREEN);
        this.setGraphOutlineColor(de_ic, Color.RED);
    }

    public Container.Indexed getPredictionByCountry(Date start, String location, PowerItem power, long duration) {
        Container.Indexed container = new IndexedContainer();
        container.addContainerProperty(Timeline.PropertyId.TIMESTAMP,
                Date.class, null);
        
        Calendar calStart = Calendar.getInstance();
        calStart.setTime(start);
        container.addContainerProperty(Timeline.PropertyId.VALUE,
                Double.class, 0f);

        double initial_factor = MonitoringManagerFactory.getInstance().getEmissionFactor(location);
        OctaveManager om = new OctaveManager();
        List<CO2Item> co2items = om.co2Prediction(power, initial_factor, location, start, duration);

        for (CO2Item co2item: co2items) { //1 hour step for the next 30 days
            Item item = container.addItem(calStart.getTime());
            item.getItemProperty(Timeline.PropertyId.TIMESTAMP)
                    .setValue(calStart.getTime());
            item.getItemProperty(Timeline.PropertyId.VALUE)
                    .setValue(co2item.getMin());
            calStart.add(Calendar.HOUR, 1);
        }

        return container;

    }
    
    public static void main(String args[]) throws Exception {

        SchedulerManagerFactory.configure("https://129.69.19.70/scheduler", "/etc/e2c-portal/keystore.jks", "xxxx");


        CO2PredictionChart chart = new CO2PredictionChart();
       // chart.getPredictionByCountry(new Date(), "fr-inria", 100, 100);

    }

}
