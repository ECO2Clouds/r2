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
package eu.eco2clouds.portal;
/**
 * 
 */
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;
import eu.eco2clouds.portal.page.LoginFormLayout;
import eu.eco2clouds.portal.component.Logo;
import eu.eco2clouds.portal.component.MainMenu;
import eu.eco2clouds.portal.page.APWizardLayout;
import eu.eco2clouds.portal.page.ExperimentLayout;
import eu.eco2clouds.portal.page.HomeLayout;
import eu.eco2clouds.portal.page.NotificationLayout;
import eu.eco2clouds.portal.scheduler.ApplicationProfileParserManager;
import eu.eco2clouds.portal.scheduler.MonitoringManager;
import eu.eco2clouds.portal.scheduler.MonitoringManagerFactory;
import eu.eco2clouds.portal.scheduler.SchedulerManagerFactory;
import java.util.logging.Logger;

@Theme("mytheme")
@SuppressWarnings("serial")
//@Push
@PreserveOnRefresh
public class E2CPortal extends UI {

    public final static String VERSION = "0.4.8";
    
    private final static Logger logger
            = Logger.getLogger(ApplicationProfileParserManager.class.getName());
    private VerticalLayout layout = new VerticalLayout();
    private RefreshThread refreshThread;
    private SessionStatus sessionStatus = new SessionStatus();

    //final private NotificationTable notificationTable = new NotificationTable();
    //final private ExperimentTable experimentTable = new ExperimentTable();
    @WebServlet(value = {"/gui/*", "/VAADIN/*"}, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = E2CPortal.class, widgetset = "eu.eco2clouds.portal.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {

        SchedulerManagerFactory.configure(Configuration.schedulerUrl, Configuration.keystoreFilename, Configuration.keystorePwd);

        super.getSession().getService()
                .addSessionDestroyListener(new SessionDestroyListener() {
                    @Override
                    public void sessionDestroy(SessionDestroyEvent event) {
                        if (refreshThread != null) {
                            refreshThread.done();
                        }
                    }
                });

        
        layout = new VerticalLayout();
        layout.setStyleName(Reindeer.LAYOUT_WHITE);
        this.setContent(layout);
        
        

        this.sessionStatus.setLoggedUser("dperez");
        this.sessionStatus.setLoggedGroup("eco2clouds");
        this.sessionStatus.setStatus(SessionStatus.UNLOGGED);

        this.render();

        //if (Configuration.refreshUIActive == true) {
        //    System.out.println("start repaint");
        refreshThread = new RefreshThread();
        refreshThread.start();
        //}

    }

    public void render() {

        if (this.layout != null) {

            this.layout.removeAllComponents();
            this.layout.setMargin(true);
            this.layout.setSpacing(true);
            this.layout.setSizeFull();

            //this.placeHolder.setImmediate(true);
            Logo logo = new Logo();
            this.layout.addComponent(logo);

            MainMenu mainMenu = new MainMenu();
            if (this.sessionStatus.getStatus() != SessionStatus.UNLOGGED) {
                this.layout.addComponent(mainMenu);
            }

            Layout mainContent = null;
            if (this.sessionStatus.getStatus() == SessionStatus.UNLOGGED) {
                mainContent = new LoginFormLayout();
            } else if (this.sessionStatus.getStatus() == SessionStatus.HOME) {
                mainContent = new HomeLayout();
            } else if (this.sessionStatus.getStatus() == SessionStatus.SUBMITAP) {
                //mainContent = new ApplicationProfileLayout();
                mainContent = new APWizardLayout();
            } else if (this.sessionStatus.getStatus() == SessionStatus.EXPERIMENT) {
                mainContent = new ExperimentLayout();
            } else if (this.sessionStatus.getStatus() == SessionStatus.NOTIFICATIONS) {
                mainContent = new NotificationLayout();
            }

            this.layout.addComponent(mainContent);
            

            if (this.sessionStatus.getStatus() != SessionStatus.UNLOGGED) {
                this.layout.setExpandRatio(logo, 0.0f);
                this.layout.setExpandRatio(mainMenu, 0.0f);
                this.layout.setExpandRatio(mainContent, 3.0f);
            } else {
                this.layout.setExpandRatio(logo, 0.0f);
                this.layout.setExpandRatio(mainContent, 3.0f);

            }

        }

    }

    public SessionStatus getSessionStatus() {
        return sessionStatus;
    }

    /*public void toggleRefresh() {

     if (this.refreshThread == null) {
     System.out.println("start repai nt");
     refreshThread = new RefreshThread();
     refreshThread.start();
     } else {
     System.out.println("stop repaint");
     refreshThread.done();
     refreshThread = null;

     }

     }*/
    class RefreshThread extends Thread {

        private boolean active = true;

        MonitoringManager mm = MonitoringManagerFactory.getInstance();

        @Override
        public void run() {

            while (active) {
                // Do initialization which takes some time.
                // Here represented by a 1s sleep
                try {
                    //System.out.println("next repaint in " + Configuration.refreshUIInterval);
                    mm.refreshValues();
                    Thread.sleep(Configuration.refreshMonitorInterval);
                } catch (InterruptedException e) {
                }

                /*if (active) {
                 // Init done, update the UI after doing locking
                 access(new Runnable() {
                 @Override
                 public void run() {
                 //ProgressBarWindow sub = new ProgressBarWindow();

                 // Add it to the root component
                 //UI.getCurrent().addWindow(sub);
                 // Here the UI is locked and can be updated
                 if (sessionStatus.isRefreshable()) {
                 ((E2CPortal) UI.getCurrent()).render();
                 }
                 //UI.getCurrent().removeWindow(sub);

                 }
                 });
                 }*/
            }
        }

        public void done() {
            this.active = false;
        }
    }
}

class ProgressBarWindow extends Window {

    public ProgressBarWindow() {
        ProgressBar progress = new ProgressBar();
        progress.setIndeterminate(true);
        this.setContent(progress);

    }
}
