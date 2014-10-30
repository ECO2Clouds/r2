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
package eu.eco2clouds.portal.page;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import eu.eco2clouds.accounting.datamodel.parser.Experiment;
import eu.eco2clouds.portal.Configuration;
import eu.eco2clouds.portal.E2CPortal;
import eu.eco2clouds.portal.component.ExperimentTable;
import eu.eco2clouds.portal.component.ExperimentTableBean;
import eu.eco2clouds.portal.scheduler.SchedulerManager;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 *  
 */
public class HomeLayout extends VerticalLayout {

    private ExperimentTable userActiveExperimentTable;
    private ExperimentTable groupActiveExperimentTable;
    private ExperimentTable terminatedExperimentTable;

    public HomeLayout() {
        super();

        String loggedUser = ((E2CPortal) UI.getCurrent()).getSessionStatus().getLoggedUser();
        String loggedGroup = ((E2CPortal) UI.getCurrent()).getSessionStatus().getLoggedGroup();

        BeanItemContainer<ExperimentTableBean> userActiveContainer = new BeanItemContainer<ExperimentTableBean>(ExperimentTableBean.class);
        BeanItemContainer<ExperimentTableBean> groupActiveContainer = new BeanItemContainer<ExperimentTableBean>(ExperimentTableBean.class);
        BeanItemContainer<ExperimentTableBean> terminatedContainer = new BeanItemContainer<ExperimentTableBean>(ExperimentTableBean.class);

        SchedulerManager scheduler;
        Collection<Experiment> experiments = null;
        try {
            scheduler = new SchedulerManager(Configuration.schedulerUrl, Configuration.keystoreFilename, Configuration.keystorePwd);
            experiments = scheduler.getExperiments();
        } catch (Exception ex) {
            Logger.getLogger(ExperimentTable.class.getName()).log(Level.SEVERE, null, ex);
            new Notification("Error", "A problem occurred while connecting to the Eco2Clouds scheduler", Notification.Type.ERROR_MESSAGE, true).show(Page.getCurrent());
        }

        //System.out.println("xx" + loggedGroup + " " + loggedUser);
        if (experiments != null) {
            for (Experiment e : experiments) {
                //System.out.println(e.getHref() + " " + e.getBonfireGroupId() + " " + e.getBonfireUserId() + " " + e.getStatus());
                if (e.getStatus() != null) {
                    if (!e.getStatus().equalsIgnoreCase("terminated") && e.getBonfireUserId().equals(loggedUser)) {
                        userActiveContainer.addBean(new ExperimentTableBean(e));
                    } else if (!e.getStatus().equalsIgnoreCase("terminated") && e.getBonfireGroupId().equals(loggedGroup)) {
                        groupActiveContainer.addBean(new ExperimentTableBean(e));

                    } else if (e.getStatus().equalsIgnoreCase("terminated")) {
                        terminatedContainer.addBean(new ExperimentTableBean(e));
                    }
                } else {
                    Logger.getLogger(HomeLayout.class.getName()).log(Level.INFO, "experiment " + e.getId() + " with status null");
                }

            }
        }

        this.userActiveExperimentTable = new ExperimentTable(userActiveContainer);

        this.groupActiveExperimentTable = new ExperimentTable(groupActiveContainer);

        this.terminatedExperimentTable = new ExperimentTable(terminatedContainer);

        this.render();
    }

    public void render() {
        this.setSpacing(true);
        this.setMargin(true);
        this.setSizeFull();
        this.userActiveExperimentTable.setCaption("Your active experiments");
        this.groupActiveExperimentTable.setCaption("Your group experiments");
        this.terminatedExperimentTable.setCaption("All expired experiments");
        //this.userActiveExperimentTable.setHeight("100px");
        //this.groupActiveExperimentTable.setHeight("100px");
        //this.terminatedExperimentTable.setHeight("200px");
        this.addComponent(this.userActiveExperimentTable);
        this.addComponent(this.groupActiveExperimentTable);
        this.addComponent(this.terminatedExperimentTable);


        Label space = new Label("");
        this.addComponent(space);
        Label version = new Label("<hr/>ECO2Clouds Portal v." + E2CPortal.VERSION + " - (c) ECO2Clouds project 2012-2014 (<a href='http://www.eco2clouds.eu' target='_blank'>http://www.eco2clouds.eu</a>)", ContentMode.HTML);
        this.addComponent(version);
        //this.setExpandRatio(space, 2.0f);        

        this.setExpandRatio(this.userActiveExperimentTable, 2.0f);
        this.setExpandRatio(this.groupActiveExperimentTable, 1.0f);
        this.setExpandRatio(this.terminatedExperimentTable, 2.0f);
    }

}
