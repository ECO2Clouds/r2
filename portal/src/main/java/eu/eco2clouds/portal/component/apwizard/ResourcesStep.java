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
import eu.eco2clouds.applicationProfile.datamodel.Compute;
import eu.eco2clouds.applicationProfile.datamodel.ExperimentDescriptor;
import eu.eco2clouds.applicationProfile.datamodel.ResourceCompute;
import eu.eco2clouds.portal.E2CPortal;
import eu.eco2clouds.portal.component.NewResourceForm;
import eu.eco2clouds.portal.component.NewResourceTable;
import eu.eco2clouds.portal.component.NewResourceTableBean;
import eu.eco2clouds.portal.component.ResourceTableBean;
import eu.eco2clouds.portal.page.APWizardLayout;
import java.util.ArrayList;
import java.util.Collection;


/**
 *
 *  
 */
public class ResourcesStep extends HorizontalLayout {

    final private NewResourceTable newResourceTable = new NewResourceTable();;
    
    private APWizardLayout mainLayout;

    public ResourcesStep(APWizardLayout mainLayout) {

        super();
        
        this.mainLayout = mainLayout;
        
        this.render();
    }

    private void render() {

        this.setMargin(true);
        this.setSpacing(true);
        this.setSizeFull();
        
        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        vl.setMargin(false);
        vl.setSpacing(true);
        
        vl.addComponent(this.newResourceTable);
        
        Button btnDeleteVM = new Button("Remove");
        btnDeleteVM.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {

                NewResourceTableBean r = (NewResourceTableBean) newResourceTable.getValue();
                
                if (r != null) {
                    
                    ExperimentDescriptor ed = ((E2CPortal) UI.getCurrent()).getSessionStatus().getApplicationProfile().getExperimentDescriptor();
                    if (ed != null) {
                        ArrayList<ResourceCompute> rcList = ed.getResourcesCompute();
                        
                        ResourceCompute rctodelete = null;
                        for (ResourceCompute rc : rcList) {
                            Compute c = rc.getCompute();
                            if (c!=null) {
                                if (c.getName().equals(r.getName())) {
                                    rctodelete = rc;
                                }
                            }
                        }
                        if (rctodelete != null) {
                            ed.getResourcesCompute().remove(rctodelete);
                        }
                    }
                    newResourceTable.getContainerDataSource().removeItem(r);
        
                    ((E2CPortal) UI.getCurrent()).getSessionStatus().getApplicationProfile().setExperimentDescriptor(ed);

                    mainLayout.getMenu().getRequirementStep().getNewRequirementForm().updateElementList();

                    mainLayout.getAptext().update();

                }
            }
        });
        
        vl.addComponent(btnDeleteVM);
        
        this.addComponent(new NewResourceForm(this.newResourceTable, mainLayout));
        this.addComponent(vl);
        
        
    }
    
    public ArrayList<String> getResourceList() {
        ArrayList<String> list = new ArrayList<String>();
        
        for (NewResourceTableBean resource: (Collection<NewResourceTableBean>)this.newResourceTable.getItemIds()) {
            list.add("VM#" + resource.getName());
        }
        return list;
    }

    public NewResourceTable getNewResourceTable() {
        return newResourceTable;
    }
    
    
}

