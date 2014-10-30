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
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 *
 *  
 */
public class RawWindow extends Window {
    
        public final TextArea textArea = new TextArea("Raw text");

    
      public RawWindow() {
        
        super();
        this.setHeight("500px");
        this.setWidth("900px");
        
    }
    
    private void render() {
        
        this.center();
        this.setModal(true);

        VerticalLayout vl = new VerticalLayout();
        
        textArea.setSizeFull();
        textArea.setImmediate(true);

        vl.addComponent(textArea);
        
        
                Button btnRefresh = new Button("Submit");
        btnRefresh.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
            }
        });

        vl.addComponent(btnRefresh);

        this.setContent(vl);
    }

    
    
}
