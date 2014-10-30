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

import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import eu.eco2clouds.applicationProfile.datamodel.ApplicationProfile;
import eu.eco2clouds.applicationProfile.datamodel.Flow;
import eu.eco2clouds.applicationProfile.datamodel.Sequence;
import eu.eco2clouds.portal.E2CPortal;
import eu.eco2clouds.portal.page.APWizardLayout;
import java.util.ArrayList;

/**
 *
 *  
 */
public class NewTaskForm extends FormLayout implements Button.ClickListener {

    private APWizardLayout mainLayout;

    final TextField name = new TextField("Task Name", "enter a name");

    public NewTaskForm(APWizardLayout mainLayout) {
        super();

        this.mainLayout = mainLayout;

        this.addComponent(name);

        Button btnSave = new Button("Add");
        btnSave.addClickListener(this);

        this.addComponent(btnSave);

    }

    @Override
    public void buttonClick(Button.ClickEvent event) {

        if (this.name.getValue() == null || this.name.getValue().equals("")) {

            Notification.show("Please specify a name", Notification.Type.ERROR_MESSAGE);
        } else {

            if (mainLayout.getMenu().getFlowStep().getTaskTable().getItemIds().contains(this.name.getValue())) {

                Notification.show("Task " + this.name.getValue() + " already exists", Notification.Type.ERROR_MESSAGE);

            } else {

                ApplicationProfile ap = ((E2CPortal) UI.getCurrent()).getSessionStatus().getApplicationProfile();
                if (ap.getFlow() == null) {
                    ap.setFlow(new Flow());
                }

                ArrayList<Sequence> mainSequence = ap.getFlow().getSequence();
                if (mainSequence == null) {
                    mainSequence = new ArrayList<Sequence>();
                }

                Sequence s = new Sequence();
                s.setTask(this.name.getValue());

                mainSequence.add(s);

                ap.getFlow().setSequence(mainSequence);

                mainLayout.getMenu().getFlowStep().getTaskTable().addTask(this.name.getValue());
                this.mainLayout.getMenu().getRequirementStep().getNewRequirementForm().updateElementList();

                mainLayout.getAptext().update();
            }
        }
    }

}
