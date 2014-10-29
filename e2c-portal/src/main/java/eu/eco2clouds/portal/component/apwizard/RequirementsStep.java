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
import eu.eco2clouds.applicationProfile.datamodel.Constraint;
import eu.eco2clouds.portal.E2CPortal;
import eu.eco2clouds.portal.component.RequirementTable;
import eu.eco2clouds.portal.component.NewRequirementForm;
import eu.eco2clouds.portal.component.RequirementTableBean;
import eu.eco2clouds.portal.page.APWizardLayout;

/**
 *
 *  
 */
public class RequirementsStep extends HorizontalLayout {

    APWizardLayout mainLayout;
    RequirementTable requirementTable = new RequirementTable();
    NewRequirementForm newRequirementForm;

    public RequirementsStep(APWizardLayout mainLayout) {
        super();
        this.setSizeFull();
        this.setMargin(true);
        this.setSpacing(true);
        
        this.mainLayout = mainLayout;
        this.newRequirementForm = new NewRequirementForm(this.mainLayout);

        this.render();
    }

    private void render() {

        requirementTable.setMultiSelect(false);

        this.addComponent(this.newRequirementForm);
        
        VerticalLayout vl = new VerticalLayout();
        vl.setMargin(false);
        vl.setSpacing(true);
        vl.setSizeFull();
        
        vl.addComponent(requirementTable);
        
        Button btnDeleteReq = new Button("Remove");
        btnDeleteReq.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {

                RequirementTableBean r = (RequirementTableBean) requirementTable.getValue();
                
                if (r != null) {
                    
                    ApplicationProfile ap = ((E2CPortal) UI.getCurrent()).getSessionStatus().getApplicationProfile();
                    if (ap != null) {
                        Constraint constrainttodelete = null;
                        for (Constraint constraint: ap.getRequirement().getConstraints()) {
                            if (constraint.getElement().equals(r.getElement()) && constraint.getIndicator().equals(r.getMetric())) {
                                constrainttodelete = constraint;
                            }
                        }

                        if (constrainttodelete != null) {
                            ap.getRequirement().getConstraints().remove(constrainttodelete);
                        }
                    }
                    requirementTable.getContainerDataSource().removeItem(r);
        
                    mainLayout.getMenu().getRequirementStep().getNewRequirementForm().updateElementList();

                    mainLayout.getAptext().update();

                }
            }
        });
        
        vl.addComponent(btnDeleteReq);
        this.addComponent(vl);
    }

    public RequirementTable getElementTable() {
        return requirementTable;
    }

    public NewRequirementForm getNewRequirementForm() {
        return newRequirementForm;
    }
    
    
    
    
}
