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
package eu.eco2clouds.ac.monitor;

/**
 *
 *  
 */
public class VM {
    
    private String alaServer = null;
    
    private String id = null;
    
    private String name = null;

    public VM() {
        
    }
            
    public VM(String alaServer) {
        this.alaServer = alaServer;
    }
    
    public VM(String alaServer, String id, String name) {
        this.alaServer = alaServer;
        this.id = id;
        this.name = name;
    }

    public String getAlaServer() {
        return alaServer;
    }

    public void setAlaServer(String alaServer) {
        this.alaServer = alaServer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    
}
