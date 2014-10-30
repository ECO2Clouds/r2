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

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.themes.Reindeer;
import eu.eco2clouds.portal.page.APWizardLayout;

/**
 *
 *  
 */
public class APWizardMenu extends TabSheet {

    private APWizardLayout mainLayout;
    private GeneralStep generalStep;
    private RequirementsStep requirementStep;
    private ResourcesStep resourcesStep;
    private FlowStep flowStep;
    private SuggestionStep suggestionStep;

    public APWizardMenu(APWizardLayout mainLayout) {
        super();
        this.mainLayout = mainLayout;

        this.render();
    }

    private void render() {

        this.setHeight(100.0f, Unit.PERCENTAGE);

        this.generalStep = new GeneralStep(this.mainLayout);
        this.requirementStep = new RequirementsStep(this.mainLayout);
        this.resourcesStep = new ResourcesStep(this.mainLayout);
        this.flowStep = new FlowStep(this.mainLayout);
        this.suggestionStep = new SuggestionStep(this.mainLayout);

        this.setStyleName(Reindeer.TABSHEET_MINIMAL);
        this.addTab(this.generalStep, "General");
        this.addTab(this.resourcesStep, "Resources");
        this.addTab(this.flowStep, "Flow");
        this.addTab(this.requirementStep, "Requirements");
        this.addTab(new DataStep(), "Data");
        this.addTab(this.suggestionStep, "CO2 report");

        this.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(SelectedTabChangeEvent event) {
                if (event.getTabSheet().getSelectedTab() == suggestionStep) {
                    SuggestionStep newSuggestionStep = new SuggestionStep(mainLayout);
                    replaceComponent(suggestionStep, newSuggestionStep);
                    suggestionStep = newSuggestionStep;

                }
            }
        });

    }

    public GeneralStep getGeneralStep() {
        return generalStep;
    }

    public RequirementsStep getRequirementStep() {
        return requirementStep;
    }

    public ResourcesStep getResourcesStep() {
        return resourcesStep;
    }

    public FlowStep getFlowStep() {
        return flowStep;
    }

}
