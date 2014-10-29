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

import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import eu.eco2clouds.applicationProfile.datamodel.ApplicationProfile;
import eu.eco2clouds.applicationProfile.parser.Parser;
import eu.eco2clouds.portal.E2CPortal;
import eu.eco2clouds.portal.exception.E2CPortalException;
import eu.eco2clouds.portal.scheduler.ApplicationProfileParserManager;
import eu.eco2clouds.portal.scheduler.SchedulerManager;
import eu.eco2clouds.portal.scheduler.SchedulerManagerFactory;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.JsonMappingException;

/**
 *
 *  
 */
public class APText extends VerticalLayout implements Button.ClickListener {

    public final TextArea textArea = new TextArea("Raw text");

    public APText() {
        super();
        this.render();
    }

    private void render() {
        this.removeAllComponents();
        this.setMargin(true);
        this.setSpacing(true);
        this.setSizeFull();
        this.setWidth("400px");

        textArea.setSizeFull();
        textArea.setImmediate(true);

        ApplicationProfileParserManager apm = new ApplicationProfileParserManager();
        try {
            //textArea.setValue(apm.deserialize(((E2CPortal) UI.getCurrent()).getSessionStatus().getApplicationProfile()));
            textArea.setValue(Parser.getJSONApplicationProfile(((E2CPortal) UI.getCurrent()).getSessionStatus().getApplicationProfile()));
        } catch (JsonMappingException ex) {
            Logger.getLogger(APText.class.getName()).log(Level.SEVERE, null, ex);
            textArea.setValue("error in creating the application profile");
        } catch (IOException ex) {
            Logger.getLogger(APText.class.getName()).log(Level.SEVERE, null, ex);
            textArea.setValue("error in creating the application profile");
        }
        this.addComponent(textArea);

        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setSpacing(true);
        btnLayout.setMargin(true);

        Button btnRefresh = new Button("Refresh");
        btnRefresh.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                update();
            }
        });

        btnLayout.addComponent(btnRefresh);

        Button btnSubmit = new Button("Submit");
        btnSubmit.addClickListener(this);

        btnLayout.addComponent(btnSubmit);

        btnLayout.setComponentAlignment(btnSubmit, Alignment.BOTTOM_RIGHT);
        btnLayout.setComponentAlignment(btnRefresh, Alignment.BOTTOM_RIGHT);

        this.addComponent(btnLayout);

        this.setExpandRatio(textArea, 1.0f);
    }

    public void update() {
        this.render();
    }

    @Override
    public void buttonClick(ClickEvent event) {
        try {

            ApplicationProfileParserManager parser = new ApplicationProfileParserManager();

            //layout.addComponent(new Label("As soon as it is ready, we will send the application profile to the scheduler. Please be patient"));
            System.out.println("I submit \n" + this.textArea.getValue());
            ApplicationProfile applicationProfileObj = parser.parse(this.textArea.getValue());
            Logger.getLogger(E2CPortal.class.getName()).log(Level.FINER, this.textArea.getValue());

            SchedulerManager sm = SchedulerManagerFactory.getInstance();

            sm.submitApplicationProfile(applicationProfileObj);

            Notification okNotification = new Notification("Success", "<br/>Application Profile has been correctly <br/>sent to the Scheduler", Notification.Type.HUMANIZED_MESSAGE, true);
            okNotification.show(Page.getCurrent());

        } catch (JsonMappingException ex) {

            Notification errorNotification = new Notification("Error", "<br/>Application Profile is not valid", Notification.Type.ERROR_MESSAGE, true);
            errorNotification.show(Page.getCurrent());
            Logger.getLogger(E2CPortal.class.getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {

            Notification errorNotification = new Notification("Error", "<br/>Application Profile is not valid", Notification.Type.ERROR_MESSAGE, true);
            errorNotification.show(Page.getCurrent());
            Logger.getLogger(E2CPortal.class.getName()).log(Level.SEVERE, null, ex);

        } catch (E2CPortalException ex) {

            Notification errorNotification = new Notification("Error", "<br/>Problems while communicating with the Scheduler", Notification.Type.ERROR_MESSAGE, true);
            errorNotification.show(Page.getCurrent());
            Logger.getLogger(E2CPortal.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
