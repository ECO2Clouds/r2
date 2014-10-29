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

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;

import com.vaadin.ui.VerticalLayout;
import eu.eco2clouds.accounting.datamodel.parser.Experiment;
import eu.eco2clouds.portal.E2CPortal;
import eu.eco2clouds.portal.service.data.Notification;
import eu.eco2clouds.portal.service.data.NotificationListFactory;
import java.util.List;

/**
 *
 *  
 */
public class AdaptationReport extends VerticalLayout implements Button.ClickListener {

    AdaptationActionTable actionTable;

    public AdaptationReport() {
        super();
        this.render();
    }

    private BeanItemContainer<AdaptationActionTableBean> generateActions() {
        Experiment experiment = ((E2CPortal) UI.getCurrent()).getSessionStatus().getSelectedExperiment();

        BeanItemContainer<AdaptationActionTableBean> container = new BeanItemContainer<AdaptationActionTableBean>(AdaptationActionTableBean.class);

        int i = 1;
        List<Notification> notifications = NotificationListFactory.getInstance().getList();
        for (Notification notification : notifications) {

            if (notification.getSource().equals("EXP"+experiment.getId())) {

                AdaptationActionTableBean action = new AdaptationActionTableBean(i, notification.getTimestamp(), notification.getDescription());
                container.addBean(action);
            }
        }

        return container;
    }

    private void render() {

        this.setSpacing(true);
        this.setMargin(true);

        this.setSizeFull();

        final HorizontalLayout tables = new HorizontalLayout();
        tables.setSpacing(true);
        tables.setMargin(false);
        tables.setSizeFull();
        this.actionTable = new AdaptationActionTable(this.generateActions());
        tables.addComponent(actionTable);
        //tables.addComponent(new ViolationList());

        Button btnRefresh = new Button("Refresh");
        btnRefresh.addClickListener(this);

        this.addComponent(tables);
        this.addComponent(btnRefresh);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        actionTable.setContainer(this.generateActions());

    }
}
