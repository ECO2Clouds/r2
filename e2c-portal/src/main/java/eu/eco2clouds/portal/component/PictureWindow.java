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

import com.vaadin.server.FileResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import eu.eco2clouds.portal.Configuration;
import java.io.File;

/**
 *
 *  
 */
public class PictureWindow extends Window {

    public PictureWindow() {
        super();
        this.render();
    }

    private void render() {
        
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setMargin(true);
        content.setSizeFull();
        
        Image image = new Image();
        //image.setSource(new ThemeResource("img/applicationsample.png"));
        image.setSource(new FileResource(new File(Configuration.propertiesDir + File.separator + "sample_applicationprofile.png")));
        //image.setHeight("300px");
        content.addComponent(image);

        Button btnClose = new Button("Close");
        btnClose.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                
                close();
            }
        });
        
        content.addComponent(btnClose);
        content.setExpandRatio(image, 1.0f);
        content.setComponentAlignment(btnClose, Alignment.BOTTOM_CENTER);
        this.setContent(content);
        
        this.setHeight("450px");
        this.setWidth("900px");
        
        this.center();
        this.setModal(true);
    }
}
