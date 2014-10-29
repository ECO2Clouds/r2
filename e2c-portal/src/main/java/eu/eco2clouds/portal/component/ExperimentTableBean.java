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

import eu.eco2clouds.accounting.datamodel.parser.Experiment;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 *  
 */
public class ExperimentTableBean implements Serializable {

    private String href;
    private Integer id;
    private String group;
    private String user;
    private Long internalId;
    private Long managedExperimentId;
    private String startTime;
    private String endTime;
    private String status;

    private Experiment experiment;
    
    public ExperimentTableBean() {
        super();
    }
    
    public ExperimentTableBean(Experiment e) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");

        this.id = e.getId();
        this.href = e.getHref();
        this.group = e.getBonfireGroupId();
        this.user = e.getBonfireUserId();
        this.startTime = sdf.format(new Date(e.getStartTime()));
        this.endTime = sdf.format(new Date(e.getEndTime()));
        this.internalId = e.getBonfireExperimentId();
        this.managedExperimentId = e.getManagedExperimentId();
        this.status = e.getStatus();
        
        this.experiment = e;
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getInternalId() {
        return internalId;
    }

    public void setInternalId(Long bonfireExperimentId) {
        this.internalId = bonfireExperimentId;
    }

    public Long getManagedExperimentId() {
        return managedExperimentId;
    }

    public void setManagedExperimentId(Long managedExperimentId) {
        this.managedExperimentId = managedExperimentId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
}