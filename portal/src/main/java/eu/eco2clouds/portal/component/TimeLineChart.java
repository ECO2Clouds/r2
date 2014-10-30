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
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.HorizontalLayout;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 *
 *  
 */
public class TimeLineChart extends HorizontalLayout {

    IndexedContainer container = new IndexedContainer();
    Timeline timeline = new Timeline();

    public TimeLineChart() {
        timeline.setWidth("600px");
        timeline.setImmediate(true);
        if (container.size() > 0) {
            // Add the container as a graph container
            timeline.addGraphDataSource(container, Timeline.PropertyId.TIMESTAMP,
                    Timeline.PropertyId.VALUE);
        }
        this.addComponent(timeline);

    }

    public void show(Collection<MonitoredItem> items) {

        timeline.removeGraphDataSource(container);
        this.loadData(items);
        timeline.addGraphDataSource(container, Timeline.PropertyId.TIMESTAMP,
                Timeline.PropertyId.VALUE);

    }

    private IndexedContainer loadData(Collection<MonitoredItem> items) {

        container.removeAllItems();
        container.addContainerProperty(Timeline.PropertyId.TIMESTAMP,
                Date.class, null);


        container.addContainerProperty(Timeline.PropertyId.VALUE,
                Double.class, 0f);

        //items.sort(new Object[]{"clock"}, new boolean[]{true});
        for (MonitoredItem i : items) {
            //System.out.println(i.getClock() + " - " + i.getValue());
            Date d = new Date(i.getClock() * 1000);
            Item item = container.addItem(d);

            if (item == null) {
                //System.out.println("container pieno con " + container.size() + " element");
            } else {
                //System.out.println("container " + container.size() + " element");

                // Set the timestamp property
                item.getItemProperty(Timeline.PropertyId.TIMESTAMP)
                        .setValue(d);

                // Set the value property
                item.getItemProperty(Timeline.PropertyId.VALUE)
                        .setValue(i.getValue());
            }

        }

        return container;

    }
}
