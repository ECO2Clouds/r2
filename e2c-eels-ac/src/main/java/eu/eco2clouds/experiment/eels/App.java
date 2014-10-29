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
package eu.eco2clouds.experiment.eels;

import eu.eco2clouds.ac.ACException;
import eu.eco2clouds.ac.ApplicationController;
import eu.eco2clouds.app.Application;
import eu.eco2clouds.app.ApplicationDecisor;
import eu.eco2clouds.app.ApplicationMetric;
import eu.eco2clouds.component.MonitoringManagerFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


public class App {

    public static void main(String[] args) throws ACException {
        SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-dd-HHmm");

        try {
            PrintStream output = null;

            Application app = new EelsApplication();
            ApplicationDecisor dec = new EelsDecisor();
            ApplicationMetric metric = new EelsApplicationMetric();

            ApplicationController ac = new ApplicationController(app, dec, metric);
            ac.start();
            output = new PrintStream(new File("results-" + df.format(new Date()) + ".log"));
            //ac.printVMStatus(output);
            MonitoringManagerFactory.getInstance().printStatusXML(output);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
