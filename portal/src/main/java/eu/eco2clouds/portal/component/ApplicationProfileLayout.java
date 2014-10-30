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

import com.vaadin.server.Page;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import eu.eco2clouds.applicationProfile.datamodel.ApplicationProfile;
import eu.eco2clouds.portal.Configuration;
import eu.eco2clouds.portal.E2CPortal;
import eu.eco2clouds.portal.exception.E2CPortalException;
import eu.eco2clouds.portal.scheduler.ApplicationProfileParserManager;
import eu.eco2clouds.portal.scheduler.SchedulerManager;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.JsonMappingException;

/**
 *
 *  
 */
public class ApplicationProfileLayout extends HorizontalLayout {

    public final ApplicationProfileTxt apArea = new ApplicationProfileTxt();

    public ApplicationProfileLayout() {
        super();
        this.render();
    }

    private void render() {

        this.setMargin(true);
        this.setSpacing(false);
        this.setSizeFull();

        this.addComponent(apArea);
        this.addComponent(new ButtonSet());
        //this.setExpandRatio(apArea, 1.0f);

    }

    class ApplicationProfileTxt extends TextArea {

        public ApplicationProfileTxt() {

            super("Application profile", "--- Insert your application profile here ---");
            this.setHeight("400px");
            //apArea.setRows(50);
            this.setImmediate(true);
            this.setSizeFull();
            
            this.setValue(Configuration.getApplicationProfileSample());

        }
    }

    class ButtonSet extends VerticalLayout {

        public ButtonSet() {
            this.setMargin(true);
            this.setSpacing(true);
            //this.setWidth("300px");

            //this.addComponent(new BtnLoadSample());
            this.addComponent(new BtnValidate());
            this.addComponent(new BtnSubmit());
        }
    }

    public class BtnSubmit extends Button implements Button.ClickListener {

        private SchedulerManager scheduler;
        private ApplicationProfileParserManager parser = new ApplicationProfileParserManager();

        public BtnSubmit() {
            super("Submit");
            this.setId("btnSubmit");
            this.addClickListener(this);
            try {
                this.scheduler = new SchedulerManager(Configuration.schedulerUrl, Configuration.keystoreFilename, Configuration.keystorePwd);
            } catch (Exception ex) {
                new Notification("Error", "A problem occurred while connecting to the Eco2Clouds scheduler", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
            }

        }

        public void buttonClick(Button.ClickEvent event) {
            try {
                //layout.addComponent(new Label("As soon as it is ready, we will send the application profile to the scheduler. Please be patient"));
                ApplicationProfile applicationProfileObj = parser.parse(apArea.getValue());
                Logger.getLogger(E2CPortal.class.getName()).log(Level.FINER, apArea.getValue());
                scheduler.submitApplicationProfile(applicationProfileObj);

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

        public SchedulerManager getScheduler() {
            return scheduler;
        }

        public ApplicationProfileParserManager getParser() {
            return parser;
        }
        
        
    }

    public class BtnValidate extends Button implements Button.ClickListener {

        private ApplicationProfileParserManager parser = new ApplicationProfileParserManager();

        public BtnValidate() {
            super("Validate");
            this.addClickListener(this);
            this.setId("btnValidate");

        }

        public void buttonClick(Button.ClickEvent event) {
            try {
                ApplicationProfile applicationProfileObj = parser.parse(apArea.getValue());
                Notification okNotification = new Notification("Success", "<br/>Application Profile is valid", Notification.Type.HUMANIZED_MESSAGE, true);
                okNotification.show(Page.getCurrent());
                
            } catch (JsonMappingException ex) {
                Notification errorNotification = new Notification("Error", "<br/>Application Profile is not valid", Notification.Type.ERROR_MESSAGE, true);
                errorNotification.show(Page.getCurrent());
                Logger.getLogger(ApplicationProfileLayout.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Notification errorNotification = new Notification("Error", "<br/>Application Profile is not valid", Notification.Type.ERROR_MESSAGE, true);
                errorNotification.show(Page.getCurrent());
                Logger.getLogger(ApplicationProfileLayout.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public class BtnLoadSample extends Button implements Button.ClickListener {


        public BtnLoadSample() {
            super("Load sample");
            this.addClickListener(this);
            this.setId("btnLoadSample");
        }

        public void buttonClick(Button.ClickEvent event) {
            apArea.setValue(Configuration.getApplicationProfileSample());
        }
    }

    public class BtnPicture extends Button implements Button.ClickListener {

        public BtnPicture() {
            super("see Process");
            this.addClickListener(this);
            this.setId("btnPicture");
        }

        public void buttonClick(Button.ClickEvent event) {
            PictureWindow sub = new PictureWindow();

            // Add it to the root component
            UI.getCurrent().addWindow(sub);

        }
    }
}
