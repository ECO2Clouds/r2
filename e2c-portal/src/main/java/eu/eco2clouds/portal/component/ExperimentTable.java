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
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import eu.eco2clouds.accounting.datamodel.parser.Experiment;
import eu.eco2clouds.portal.Configuration;
import eu.eco2clouds.portal.E2CPortal;
import eu.eco2clouds.portal.SessionStatus;
import eu.eco2clouds.portal.scheduler.SchedulerManager;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *  
 */
public class ExperimentTable extends Table {

    public ExperimentTable(BeanItemContainer<ExperimentTableBean> container) {
        super();
        this.render(container);
    }

    private void render(BeanItemContainer<ExperimentTableBean> container) {

        this.setWidth("100%");
        this.setSizeFull();
        this.setImmediate(true);
        this.setSelectable(true);

        this.setContainerDataSource(container);
        this.setVisibleColumns(new Object[]{"id", "href", "internalId", "user", "group", "startTime", "endTime", "status"});

        this.sort(new Object[]{"id"}, new boolean[]{false});

        this.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(final Property.ValueChangeEvent event) {

                ExperimentTableBean e = (ExperimentTableBean) getValue();

                if (e != null) {

                    System.out.println("seleziono   " + e.getExperiment().getHref());
                    ((E2CPortal) UI.getCurrent()).getSessionStatus().setSelectedExperiment(e.getExperiment());
                    ((E2CPortal) UI.getCurrent()).getSessionStatus().setStatus(SessionStatus.EXPERIMENT);
                    ((E2CPortal) UI.getCurrent()).render();
                }

            }
        });

    }
}
