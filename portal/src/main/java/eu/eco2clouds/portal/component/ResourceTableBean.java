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

import eu.eco2clouds.accounting.datamodel.parser.VM;
import java.io.Serializable;

/**
 *
 *  
 */
public class ResourceTableBean implements Serializable {

    private String href;
    private Integer id;
    private String host;
    private String ip;
    private String name;
    private String bonfireId;
    private String bonfireUrl;

    private VM vm;

    public ResourceTableBean() {
        super();
    }

    public ResourceTableBean(VM vm) {

        this.vm = vm;

        if (this.vm != null) {
            this.href = this.vm.getHref();
            this.id = this.vm.getId();
            this.host = this.vm.getHost();
            if (this.vm.getNics()!= null && this.vm.getNics().size() > 0)
                this.ip = this.vm.getNics().get(0).getIp();
            this.name = this.vm.getName();
            this.bonfireId = this.vm.getBonfireId();
            this.bonfireUrl = this.vm.getBonfireUrl();
        }
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBonfireId() {
        return bonfireId;
    }

    public void setBonfireId(String bonfireId) {
        this.bonfireId = bonfireId;
    }

    public String getBonfireUrl() {
        return bonfireUrl;
    }

    public void setBonfireUrl(String bonfireUrl) {
        this.bonfireUrl = bonfireUrl;
    }

    public VM getVm() {
        return vm;
    }

    public void setVm(VM vm) {
        this.vm = vm;
    }

}
