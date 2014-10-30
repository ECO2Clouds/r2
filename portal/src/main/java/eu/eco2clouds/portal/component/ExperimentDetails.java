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

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import eu.eco2clouds.accounting.datamodel.parser.Experiment;
import eu.eco2clouds.applicationProfile.parser.Parser;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.JsonMappingException;

/**
 *
 *  
 */
public class ExperimentDetails extends HorizontalLayout {

    Experiment experiment;

    public ExperimentDetails(Experiment experiment) {
        super();
        this.experiment = experiment;
        this.render();
    }

    private void render() {
        try {
            this.setSpacing(false);
            this.setMargin(false);
            this.setHeight("200px");
            
            VerticalLayout details = new VerticalLayout();
            details.setSpacing(true);
            details.setMargin(false);
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY hh:mm");
            SimpleDateFormat edf = new SimpleDateFormat("dd 'days' hh 'hours' mm ' minutes'");
            
            
            details.addComponent(new Label("<b>id</b>: " + experiment.getId(), ContentMode.HTML));
            details.addComponent(new Label("<b>user</b>: " + experiment.getBonfireUserId() + " <b>group</b>: " + experiment.getBonfireGroupId(), ContentMode.HTML));
            if (!experiment.getStatus().equalsIgnoreCase("terminated")) {
                details.addComponent(new Label("<b>started</b>: " + sdf.format(new Date(experiment.getStartTime())) + " <b>planned termination</b>: " + sdf.format(new Date(experiment.getEndTime())), ContentMode.HTML));
                //details.addComponent(new Label(edf.format(new Date(experiment.getEndTime() - experiment.getStartTime())) + " to go"));
            } else {
                details.addComponent(new Label("<b>started</b>: " + sdf.format(new Date(experiment.getStartTime())) + " <b>terminated</b>: " + sdf.format(new Date(experiment.getEndTime())), ContentMode.HTML));
                details.addComponent(new Label("lasted for " + edf.format(new Date(experiment.getEndTime() - experiment.getStartTime()))));
            }
            
            final String ap = Parser.getJSONApplicationProfile(Parser.getApplicationProfile(experiment.getApplicationProfile()));
            
            Button btnApplicationProfile = new Button("Show Application Profile");
            btnApplicationProfile.setStyleName(BaseTheme.BUTTON_LINK);
            btnApplicationProfile.addClickListener(new Button.ClickListener() {
                
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    
                    UI.getCurrent().addWindow(new APWindow(ap));
                    
                }
            });
            details.addComponent(btnApplicationProfile);
            
            this.addComponent(details);
            this.setComponentAlignment(details, Alignment.MIDDLE_LEFT);
            
            Label spacing = new Label(" ");
            this.addComponent(spacing);
            this.setComponentAlignment(details, Alignment.MIDDLE_LEFT);
            
           /* if (!experiment.getStatus().equalsIgnoreCase("terminated")) {
                Button terminateBtn = new Button("Terminate");
                this.addComponent(terminateBtn);
                this.setComponentAlignment(terminateBtn, Alignment.MIDDLE_RIGHT);
            }*/
            
            this.setExpandRatio(spacing, 20.0f);
        } catch (JsonMappingException ex) {
            Logger.getLogger(ExperimentDetails.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExperimentDetails.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}

class APWindow extends Window {

    public APWindow(String ap) {

        super("Application Profile");
        TextArea ta = new TextArea("Application Profile", ap);
        ta.setHeight("600px");
        ta.setWidth("800px");
        //ta.setEnabled(false);
        this.setWidth("-1px");
        this.setHeight("-1px");
        this.center();

        this.setContent(ta);
    }
}

