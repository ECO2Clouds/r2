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
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import eu.eco2clouds.portal.E2CPortal;
import eu.eco2clouds.portal.SessionStatus;

/**
 *
 *  
 */
public class LoginFormLayout extends VerticalLayout {
    
    private final TextField username = new TextField("Username");
    private final PasswordField password = new PasswordField("Password");
    
    public LoginFormLayout() {
        
        super();
        this.render();
        
    }
    
    public void render() {
        this.setMargin(true);
        this.setSpacing(true);
        this.setSizeFull();
        
        
        this.addComponent(this.username);
        this.addComponent(this.password);
        
        Button loginBtn = new Button("Login");
        loginBtn.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                
                if (isValid(username.getValue(), password.getValue())) {
                    ((E2CPortal)UI.getCurrent()).getSessionStatus().setLoggedUser("dperez");
                    ((E2CPortal)UI.getCurrent()).getSessionStatus().setLoggedGroup("eco2clouds");
                    ((E2CPortal)UI.getCurrent()).getSessionStatus().setStatus(SessionStatus.HOME);
                    ((E2CPortal)UI.getCurrent()).render();
                } else {
                    Notification.show("Error", "Username and/or password not valid", Notification.Type.ERROR_MESSAGE);
                }
                
            }
        });
        
        this.addComponent(loginBtn);
        Label space = new Label("");
        this.addComponent(space);
        Label version = new Label("<hr/>ECO2Clouds Portal v." + E2CPortal.VERSION + " - (c) ECO2Clouds project 2012-2014 (<a href='http://www.eco2clouds.eu' target='_blank'>http://www.eco2clouds.eu</a>)", ContentMode.HTML);
        this.addComponent(version);
        this.setExpandRatio(space, 2.0f);
    }
    
    private boolean isValid(String username, String password) {
        
        return (username.equals("eco2clouds") && password.equals("bonfire"));
        
    }
    
}
