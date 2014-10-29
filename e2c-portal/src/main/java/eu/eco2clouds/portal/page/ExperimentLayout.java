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

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import eu.eco2clouds.accounting.datamodel.parser.Experiment;
import eu.eco2clouds.portal.E2CPortal;
import eu.eco2clouds.portal.component.AdaptationReport;
import eu.eco2clouds.portal.component.EcoReport;
import eu.eco2clouds.portal.component.ExperimentDetails;
import eu.eco2clouds.portal.component.RecommendationReport;
import eu.eco2clouds.portal.component.ResourceTable;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 *  
 */
public class ExperimentLayout extends VerticalLayout {

    Experiment experiment;

    public ExperimentLayout() {

        this.experiment = ((E2CPortal) UI.getCurrent()).getSessionStatus().getSelectedExperiment();
        this.render();
    }

    public void render() {

        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();

        /* sheet on experiment details*/
        VerticalLayout detailsSheet = new VerticalLayout();
        detailsSheet.setSpacing(true);
        detailsSheet.setMargin(true);
        detailsSheet.setSizeFull();

        detailsSheet.addComponent(new ExperimentDetails(experiment));

        detailsSheet.addComponent(new ResourceTable());

        /* sheet reporting experiment energy */
        VerticalLayout ecoReportSheet = new VerticalLayout();
        ecoReportSheet.setSpacing(true);
        ecoReportSheet.setMargin(false);
        ecoReportSheet.setSizeFull();
        ecoReportSheet.addComponent(new EcoReport());

        /* sheet reporting adaptation actions */
        VerticalLayout adaptationActionSheet = new VerticalLayout();
        adaptationActionSheet.setSpacing(true);
        adaptationActionSheet.setMargin(false);
        adaptationActionSheet.setSizeFull();
        adaptationActionSheet.addComponent(new AdaptationReport());

        /* sheet reporting experiment energy */
        /*VerticalLayout recommendationReportSheet = new VerticalLayout();
         recommendationReportSheet.setSpacing(true);
         recommendationReportSheet.setMargin(false);
         recommendationReportSheet.setSizeFull();
         recommendationReportSheet.addComponent(new RecommendationReport());*/
        
        tabSheet.setStyleName(Reindeer.TABSHEET_MINIMAL);

        tabSheet.addTab(detailsSheet, "Details");
        tabSheet.addTab(ecoReportSheet, "Eco Report");
        tabSheet.addTab(adaptationActionSheet, "Adaptation Report");
            //tabSheet.addTab(recommendationReportSheet, "Recommendation Report");

        this.addComponent(tabSheet);
        
        Label space = new Label("");
        this.addComponent(space);
        Label version = new Label("<hr/>ECO2Clouds Portal v." + E2CPortal.VERSION + " - (c) ECO2Clouds project 2012-2014 (<a href='http://www.eco2clouds.eu' target='_blank'>http://www.eco2clouds.eu</a>)", ContentMode.HTML);
        this.addComponent(version);
        
        this.setExpandRatio(tabSheet, 2.0f);

    }
}
