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
package eu.eco2clouds.experiment.eels.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *  
 */
public class ManageALAServer {

    public static void startALAServer(String ipAddress) {

        try {
            Process p = Runtime.getRuntime().exec("sh /root/eelsAC/startALA.sh " + ipAddress);
            InputStream stream = p.getErrorStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = br.readLine()) != null) {
                Logger.getLogger(ManageALAServer.class.getName()).log(Level.SEVERE, "Impossible to start the ALA server at " + ipAddress + " cause: " + line);
                
            }
             
        } catch (IOException ex) {
            Logger.getLogger(ManageALAServer.class.getName()).log(Level.SEVERE, "Impossible to start the ALA server at " + ipAddress, ex);
        }

    }

    public static void stopALAServer(String ipAddress) {
        try {
            Runtime.getRuntime().exec("sh /root/eelsAC/stopALA.sh " + ipAddress);
            
            

        } catch (IOException ex) {
            Logger.getLogger(ManageALAServer.class.getName()).log(Level.SEVERE, "Impossible to stop the ALA server at " + ipAddress, ex);
        }

    }

    public static void main(String args[]) throws Exception {

        ManageALAServer.startALAServer("172.18.253.238");

        Thread.sleep(20000);

        ManageALAServer.stopALAServer("172.18.253.238");
    }

}
