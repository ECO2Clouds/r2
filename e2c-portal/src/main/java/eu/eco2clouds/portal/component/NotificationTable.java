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

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.Table;
import eu.eco2clouds.portal.service.data.Notification;
import eu.eco2clouds.portal.service.data.NotificationList;
import eu.eco2clouds.portal.service.data.NotificationListFactory;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

/**
 *
 *  
 */
public class NotificationTable extends Table {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public NotificationTable() {
        super("Notifications");
        this.render();
    }

    private void render() {
        this.setWidth("100%");
        this.setSizeFull();
        this.setImmediate(true);
        this.setSelectable(true);
        
        BeanItemContainer<Notification> container = new BeanItemContainer<Notification>(Notification.class);
        
        this.setContainerDataSource(container);
        this.setVisibleColumns(new Object[]{"timestamp", "level", "title", "description", "source"});
        
        this.setColumnExpandRatio("timestamp", 1.0f);
        this.setColumnExpandRatio("level", 1.0f);
        this.setColumnExpandRatio("title", 2.0f);
        this.setColumnExpandRatio("description", 6.0f);
        this.setColumnExpandRatio("source", 2.0f);


        this.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(final Property.ValueChangeEvent event) {

                Notification n = (Notification) getValue();

                if (n != null) {
                    new com.vaadin.ui.Notification(n.getTitle(), "Recevied at <b>" + sdf.format(n.getTimestamp()).toString() + "</b><br/> from <b>" + n.getSource() + "</b><br/>" + n.getDescription(), com.vaadin.ui.Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
                }

            }
        });

    }

    public void updateNotification() {

        int i = 0;

        NotificationList nl = NotificationListFactory.getInstance();

        BeanItemContainer<Notification> container = (BeanItemContainer<Notification>) this.getContainerDataSource();
        int existing = container.size();
        container.removeAllItems();
        
                                
        LinkedList<eu.eco2clouds.portal.service.data.Notification> notifications = nl.getList();
        for (Notification n : notifications) {

            container.addBean(n);
            if (i >= existing) {

                com.vaadin.ui.Notification not = new com.vaadin.ui.Notification(n.getTitle(), n.getDescription(), com.vaadin.ui.Notification.Type.TRAY_NOTIFICATION);
                not.setDelayMsec(2000);
                not.setPosition(Position.TOP_RIGHT);
                not.show(Page.getCurrent());
            }

            i++;

        }

        this.setContainerDataSource(container);

    }
}
