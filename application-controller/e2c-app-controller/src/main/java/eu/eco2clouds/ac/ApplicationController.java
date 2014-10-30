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
package eu.eco2clouds.ac;

import eu.eco2clouds.ac.monitor.AppStatus;
import eu.eco2clouds.ac.monitor.VM;
import eu.eco2clouds.ac.monitor.PMStatus;
import eu.eco2clouds.ac.monitor.Report;
import eu.eco2clouds.ac.monitor.VMStatus;
import eu.eco2clouds.app.Application;
import eu.eco2clouds.app.ApplicationDecisor;
import eu.eco2clouds.app.ApplicationMetric;
import eu.eco2clouds.component.MonitoringManager;
import eu.eco2clouds.component.MonitoringManagerFactory;
import eu.eco2clouds.component.SchedulerManager;
import eu.eco2clouds.component.SchedulerManagerFactory;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * Hello world!
 *
 */
public class ApplicationController {


    private final static Logger LOGGER = Logger.getLogger(ApplicationController.class.getName());
    private Application application;
    private ApplicationDecisor decisor;
    private ApplicationMetric applicationMetric;
    private MonitoringManager mm;
    private SchedulerManager sm;

    public ApplicationController(Application application, ApplicationDecisor decisor, ApplicationMetric applicationMetric) throws ACException {

        super();

        this.application = application;
        this.applicationMetric = applicationMetric;

        if (this.application != null) {
            this.application.setApplicationController(this);
            LOGGER.info("Application embedded in the Application Controller");
        } else {
            throw new ACException("Application passed to the Application Controller is not defined");
        }

        if (this.application != null) {
            this.application.setApplicationController(this);
            LOGGER.info("Application performance calculator defined");
        } else {
            throw new ACException("ApplicationPerformance passed to the Application Controller is not defined. No App metrics will be available");
        }

        this.decisor = decisor;

        if (this.decisor != null) {
            this.decisor.setApplicationController(this);
            LOGGER.info("Application embedded in the Application Controller");
        } else {
            LOGGER.info("Decisor not defined");
        }

        if ("on".equalsIgnoreCase(Configuration.getProperties().getProperty(Configuration.SCHEDULER_STATE))) {
            // create the scheduler manager
            sm = SchedulerManagerFactory.getInstance();
            if (sm == null) {
                LOGGER.info("Impossible to create the SchedulerManager.");
                throw new ACException("Impossible to create the SchedulerManager");
            }
            LOGGER.info("SchedulerManager created");
        } else {
            LOGGER.info("SchdulerManager disabled as requested in the configuration file");

        }

        if ("on".equalsIgnoreCase(Configuration.getProperties().getProperty(Configuration.MONITORING_STATE))) {
            // create the monitoring manager
            mm = MonitoringManagerFactory.getInstance();

            if (mm == null) {
                LOGGER.info("Impossible to create the MonitoringManager. Monitoring disabled.");
            }
            LOGGER.info("MonitoringManager created");
            mm.setApplicationMetric(applicationMetric);

        } else {
            LOGGER.info("MonitoringManager disabled as requested in the configuration file");
        }
        // create the LocalAgents manager
        List<VM> hosts = new LinkedList<VM>();

        if (sm == null) { // if no sm requested then the list of VM for the LocalAgentManager is taken from the files of properties
            String propVMS = Configuration.getProperties().getProperty(Configuration.VMS);
            List<String> vmListProp = Arrays.asList(propVMS.split(","));
            List<VM> vmList = new LinkedList<VM>();
            for (String vm : vmListProp) {
                VM avm = new VM(vm);
            }
            LocalAgentsManagerFactory.configure(vmList);
        } else {
            LocalAgentsManagerFactory.configure(sm.getExperimentVMs());
        }

        LocalAgentsManager lm = LocalAgentsManagerFactory.getInstance();
        if (lm == null) {
            LOGGER.info("Impossible to create the LocalAgentsManager");
            throw new ACException("Impossible to create the LocalAgentsManager");
        }
        LOGGER.info("LocalAgentsManager created");

        if (this.decisor != null) {

            this.decisor.setMonitoringManager(mm);
            this.decisor.setSchedulerManager(sm);
            this.decisor.setLocalAgentsManager(lm);
        }

        if (this.application != null) {
            this.application.setLocalAgentsManager(lm);
        }
        
    }

    private void initLogger() throws ACException {
        try {
            Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
            FileHandler fileTxt = new FileHandler("App.log");
            logger.addHandler(fileTxt);

        } catch (IOException ex) {
            Logger.getLogger(ApplicationController.class.getName()).log(Level.SEVERE, null, ex);
            throw new ACException("IOException in accessing log file", ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ApplicationController.class.getName()).log(Level.SEVERE, null, ex);
            throw new ACException("SecurityException in accessing log file", ex);
        }
    }

    public void start() throws ACException {
        try {
            Thread appThread = new Thread(this.application);
            if (appThread != null) {
                appThread.start();
            } else {
                LOGGER.log(Level.SEVERE, "Application cannot be started");
                throw new ACException("Application cannot be started");
            }

            Thread decisorThread = new Thread(this.decisor);
            if (decisorThread != null) {
                decisorThread.start();
            } else {
                LOGGER.log(Level.SEVERE, "Application Decisor cannot be started");
                throw new ACException("Application Decisor cannot be started");
            }

            appThread.join();
            decisorThread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(ApplicationController.class.getName()).log(Level.SEVERE, null, ex);
            throw new ACException("Application Controller suddendly stopped", ex);
        }

    }

    public void notifyApplicationStatus(int status) {

        if (status == Application.STOPPED) {

            if (this.decisor != null) {
                this.decisor.setStatus(Application.STOPPED);
            }
        }

    }

    public void decide() {

        if (this.decisor != null) {
            this.decisor.decide();
        }

    }

    public ApplicationMetric getApplicationMetric() {
        return applicationMetric;
    }

    public void setApplicationMetric(ApplicationMetric applicationMetric) {
        this.applicationMetric = applicationMetric;
    }

    public ApplicationDecisor getDecisor() {
        return decisor;
    }

    public void setDecisor(ApplicationDecisor decisor) {
        this.decisor = decisor;
    }

    
}
