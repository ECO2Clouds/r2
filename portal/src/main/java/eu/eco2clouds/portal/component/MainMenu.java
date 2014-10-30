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

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.Reindeer;
import eu.eco2clouds.portal.E2CPortal;
import eu.eco2clouds.portal.SessionStatus;

/**
 *
 *  
 */
public class MainMenu extends HorizontalLayout {

    public MainMenu() {

        this.setHeight("50px");
        this.setSpacing(true);
        this.setMargin(true);
        this.setWidth("100%");

        Label spaceLabel = new Label("  ");
        this.addComponent(spaceLabel);

        Button homePageBtn = new Button("");
        homePageBtn.setIcon(new ThemeResource("img/homepage.png"));
        homePageBtn.setDescription("Home page");
        
        homePageBtn.setStyleName(Reindeer.BUTTON_LINK);
        homePageBtn.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {

                int status = ((E2CPortal) UI.getCurrent()).getSessionStatus().getStatus();
                if (status != SessionStatus.HOME) {
                    ((E2CPortal) UI.getCurrent()).getSessionStatus().setStatus(SessionStatus.HOME);
                    ((E2CPortal) UI.getCurrent()).render();

                }

            }
        });

        this.addComponent(homePageBtn);

        Button addExperimentBtn = new Button("");
        addExperimentBtn.setIcon(new ThemeResource("img/add.png"));
        addExperimentBtn.setDescription("Add new experiment");
        
        addExperimentBtn.setStyleName(Reindeer.BUTTON_LINK);
        addExperimentBtn.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                int status = ((E2CPortal) UI.getCurrent()).getSessionStatus().getStatus();
                if (status != SessionStatus.SUBMITAP) {
                    ((E2CPortal) UI.getCurrent()).getSessionStatus().setStatus(SessionStatus.SUBMITAP);
                    ((E2CPortal) UI.getCurrent()).render();

                }

            }
        });

        this.addComponent(addExperimentBtn);

        Button notificationBtn = new Button("Notifications");
        notificationBtn.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                int status = ((E2CPortal) UI.getCurrent()).getSessionStatus().getStatus();
                if (status != SessionStatus.NOTIFICATIONS) {
                    ((E2CPortal) UI.getCurrent()).getSessionStatus().setStatus(SessionStatus.NOTIFICATIONS);
                    ((E2CPortal) UI.getCurrent()).render();

                }

            }
        });

        //this.addComponent(notificationBtn);
        
        Button logoutBtn = new Button("");
        logoutBtn.setIcon(new ThemeResource("img/logout.png"));
        logoutBtn.setDescription("Logout");
        logoutBtn.setStyleName(Reindeer.BUTTON_LINK);
        logoutBtn.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {

                ((E2CPortal) UI.getCurrent()).getSessionStatus().setStatus(SessionStatus.UNLOGGED);
                ((E2CPortal) UI.getCurrent()).render();

            }
        });

        this.addComponent(logoutBtn);

        /*Button refreshBtn = new Button(" ");
         if (Configuration.refreshUIActive) {
         refreshBtn.setCaption("Disable autorefresh");
         } else {
         refreshBtn.setCaption("Enable autorefresh");
         }
         refreshBtn.addClickListener(new Button.ClickListener() {

         @Override
         public void buttonClick(ClickEvent event) {

         ((E2CPortal) UI.getCurrent()).toggleRefresh();
         Configuration.refreshUIActive = !Configuration.refreshUIActive;
         if (Configuration.refreshUIActive) {
         event.getButton().setCaption("Disable autorefresh");
         } else {
         event.getButton().setCaption("Enable autorefresh");
         }

         }
         });

         this.addComponent(refreshBtn);*/
        this.setExpandRatio(spaceLabel, 20.0f);

        this.setComponentAlignment(homePageBtn, Alignment.MIDDLE_RIGHT);
        this.setComponentAlignment(addExperimentBtn, Alignment.MIDDLE_RIGHT);
        //this.setComponentAlignment(notificationBtn, Alignment.MIDDLE_LEFT);
        this.setComponentAlignment(logoutBtn, Alignment.MIDDLE_RIGHT);
        //this.setComponentAlignment(refreshBtn, Alignment.MIDDLE_RIGHT);

    }

}
