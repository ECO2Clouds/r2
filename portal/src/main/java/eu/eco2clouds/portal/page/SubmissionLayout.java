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

import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import eu.eco2clouds.portal.component.ApplicationProfileLayout;
import eu.eco2clouds.portal.component.NotificationTable;

/**
 *
 *  
 */
public class SubmissionLayout extends VerticalLayout {
    
    private NotificationTable notificationTable = new NotificationTable();
            
    public SubmissionLayout() {
        
        Panel apPanel = new Panel();
        //apPanel.setStyleName("e2c");
        apPanel.setWidth("100%");
        apPanel.setHeight("400px");

        apPanel.setContent(new ApplicationProfileLayout());

        this.addComponent(apPanel);

        //Panel notificationPanel = new Panel();
        //notificationPanel.setStyleName("e2c");
        //notificationPanel.setWidth("100%");
        //notificationPanel.setHeight("100%");
        //notificationPanel.setContent(notificationTable);

        //this.addComponent(notificationPanel);
        //this.setExpandRatio(apPanel, 1.0f);
        //this.setExpandRatio(notificationPanel, 1.5f);
        
    }
    
}
