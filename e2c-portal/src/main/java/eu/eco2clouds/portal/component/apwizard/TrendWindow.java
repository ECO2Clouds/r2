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

import com.vaadin.addon.timeline.Timeline;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Window;
import eu.eco2clouds.portal.component.CO2PredictionChart;
import eu.eco2clouds.portal.compute.PowerItem;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 *  
 */
public class TrendWindow extends Window {
    
    
    CO2PredictionChart chart;

    PowerItem fr_power;
    PowerItem uk_power;
    PowerItem de_power;
    
    long duration;
    
    public TrendWindow(PowerItem fr_power, PowerItem uk_power, PowerItem de_power, long duration) {
        
        super();
        this.setHeight("500px");
        this.setWidth("900px");
        
        this.fr_power = fr_power;
        this.uk_power = uk_power;
        this.de_power = de_power;
        
        this.duration = duration;
        
        this.render();
    }
    
    private void render() {
        
        this.center();
        this.setModal(true);
        
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.setMargin(true);
        hl.setSizeFull();
        
        final InlineDateField datePicker = new InlineDateField();
        datePicker.setValue(new Date());
        datePicker.setImmediate(true);
        datePicker.setTimeZone(TimeZone.getTimeZone("UTC"));
        datePicker.setLocale(Locale.US);
        datePicker.setResolution(Resolution.MINUTE);
        hl.addComponent(datePicker);

        this.chart = new CO2PredictionChart();
        chart.update(new Date(), fr_power, uk_power, de_power, duration);
        
        datePicker.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(final ValueChangeEvent event) {
                chart.update(datePicker.getValue(), fr_power, uk_power, de_power, duration);
            }
        });
        
        chart.addListener(new Timeline.EventClickListener() {

            @Override
            public void eventClick(Timeline.EventButtonClickEvent event) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        }
        );
        
        hl.addComponent(this.chart);
        
        hl.setComponentAlignment(datePicker, Alignment.TOP_LEFT);
        hl.setComponentAlignment(chart, Alignment.TOP_LEFT);
        hl.setExpandRatio(datePicker, 1.0f);
        hl.setExpandRatio(chart, 3.0f);
        
        this.setContent(hl);
    }

    public CO2PredictionChart getChart() {
        return chart;
    }
    
    
}
