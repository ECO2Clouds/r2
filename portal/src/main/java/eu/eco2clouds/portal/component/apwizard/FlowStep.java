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

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import eu.eco2clouds.applicationProfile.datamodel.ApplicationProfile;
import eu.eco2clouds.applicationProfile.datamodel.Sequence;
import eu.eco2clouds.portal.E2CPortal;
import eu.eco2clouds.portal.component.NewTaskForm;
import eu.eco2clouds.portal.component.TaskTable;
import eu.eco2clouds.portal.page.APWizardLayout;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 *  
 */
public class FlowStep extends HorizontalLayout {

    private APWizardLayout mainLayout;

    TaskTable taskTable = new TaskTable();

    public FlowStep(APWizardLayout mainLayout) {

        super();

        this.mainLayout = mainLayout;

        this.setSizeFull();
        this.setMargin(true);
        this.setSpacing(true);

        this.render();

    }

    private void render() {

        NewTaskForm newTaskForm = new NewTaskForm(this.mainLayout);

        this.addComponent(newTaskForm);

        VerticalLayout vl = new VerticalLayout();

        vl.addComponent(this.taskTable);

        Button btnDeleteVM = new Button("Remove");
        btnDeleteVM.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {

                String task = (String) taskTable.getValue();

                if (task != null) {

                    ApplicationProfile ap = ((E2CPortal) UI.getCurrent()).getSessionStatus().getApplicationProfile();

                    if (ap.getFlow() != null && ap.getFlow().getSequence() != null) {
                        ArrayList<Sequence> mainSequence = ap.getFlow().getSequence();

                        Sequence tasktoberemoved = null;

                        for (Sequence sequence : mainSequence) {
                            if (sequence.getTask().equals(task)) {
                                tasktoberemoved = sequence;
                            }
                        }

                        if (tasktoberemoved != null) {
                            mainSequence.remove(tasktoberemoved);
                        }
                    }
                    mainLayout.getAptext().update();

                    mainLayout.getMenu().getRequirementStep().getNewRequirementForm().updateElementList();

                    taskTable.removeTask(task);

                }
            }
        });

        vl.addComponent(btnDeleteVM);

        this.addComponent(vl);

    }

    public TaskTable getTaskTable() {
        return taskTable;
    }

    
    
    public ArrayList<String> getTaskList() {
        ArrayList<String> list = new ArrayList<String>();

        for (String task : (Collection<String>) this.taskTable.getItemIds()) {
            list.add("Task#" + task);
        }
        return list;
    }

}
