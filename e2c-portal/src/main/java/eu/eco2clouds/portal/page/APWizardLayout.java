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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import eu.eco2clouds.portal.E2CPortal;
import eu.eco2clouds.portal.component.apwizard.APText;
import eu.eco2clouds.portal.component.apwizard.APWizardMenu;

/**
 *
 *  
 */
public class APWizardLayout extends VerticalLayout {

    APText aptext = new APText();

    APWizardMenu menu;

    public APWizardLayout() {

        this.render();
    }

    private void render() {

        this.setSpacing(true);
        this.setMargin(true);
        this.setSizeFull();

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.setMargin(false);
        hl.setSizeFull();
        menu = new APWizardMenu(this);

        hl.addComponent(menu);
        hl.addComponent(aptext);
        hl.setExpandRatio(menu, 1.0f);

        this.addComponent(hl);
        
        Label space = new Label("");
        this.addComponent(space);
        Label version = new Label("<hr/>ECO2Clouds Portal v." + E2CPortal.VERSION + " - (c) ECO2Clouds project 2012-2014 (<a href='http://www.eco2clouds.eu' target='_blank'>http://www.eco2clouds.eu</a>)", ContentMode.HTML);
        this.addComponent(version);

        this.setExpandRatio(hl, 1.0f);

    }

    public APText getAptext() {
        return aptext;
    }

    public APWizardMenu getMenu() {
        return menu;
    }

}
