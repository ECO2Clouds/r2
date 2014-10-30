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
package eu.eco2clouds.experiment.eels.task.status;

import eu.eco2clouds.ac.monitor.AbstractStatus;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 *  
 */

@XmlRootElement
public class EelsTaskStatus  {
    
    private int start_year;
    private int stop_year;
    private int current_year;
    private boolean finish = false;
    private String ac_ip;
    private String od_ip;
    private long neels;
    private long response_time;
    private double throughput; //number of steps for each second
    private double cpuutilization;
    

    public int getStart_year() {
        return start_year;
    }

    public void setStart_year(int start_year) {
        this.start_year = start_year;
    }

    public int getStop_year() {
        return stop_year;
    }

    public void setStop_year(int stop_year) {
        this.stop_year = stop_year;
    }

    public int getCurrent_year() {
        return current_year;
    }

    public void setCurrent_year(int current_year) {
        this.current_year = current_year;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public String getAc_ip() {
        return ac_ip;
    }

    public void setAc_ip(String ac_ip) {
        this.ac_ip = ac_ip;
    }

    public String getOd_ip() {
        return od_ip;
    }

    public void setOd_ip(String od_ip) {
        this.od_ip = od_ip;
    }

    public long getNeels() {
        return neels;
    }

    public void setNeels(long neels) {
        this.neels = neels;
    }

    public long getResponse_time() {
        return response_time;
    }

    public void setResponse_time(long response_time) {
        this.response_time = response_time;
    }

    public double getThroughput() {
        return throughput;
    }

    public void setThroughput(double throughput) {
        this.throughput = throughput;
    }

    public double getCpuutilization() {
        return cpuutilization;
    }

    public void setCpuutilization(double cpuutilization) {
        if (this.cpuutilization > 100) {
            this.cpuutilization = 100;
        } else { 
            this.cpuutilization = cpuutilization;
        }
    }

    
    @Override
    public String toString() {
        return "EelsTaskStatus{" + "start_year=" + start_year + ", stop_year=" + stop_year + ", current_year=" + current_year + ", finish=" + finish + ", ac_ip=" + ac_ip + ", od_ip=" + od_ip + ", neels=" + neels + ", response_time=" + response_time + ", throughput=" + throughput + ", cpuutilization=" + cpuutilization + "}";
    }


}
