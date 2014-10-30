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

import com.vaadin.ui.VerticalLayout;
import eu.eco2clouds.portal.component.NotificationTable;

/**
 *
 *  
 */
public class NotificationLayout extends VerticalLayout {
    
    final private NotificationTable notificationTable = new NotificationTable();
    
    public NotificationLayout() {
        super();
        this.setMargin(false);
        this.setSpacing(true);
        this.render();
    }
    
    private void render() {
        this.removeAllComponents();
        this.addComponent(notificationTable);
    }
    
}
