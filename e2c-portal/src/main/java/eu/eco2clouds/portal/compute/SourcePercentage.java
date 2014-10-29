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
package eu.eco2clouds.portal.compute;

/**
 *
 *  
 */
public class SourcePercentage {

    double oil = 0.0;
    double coal = 0.0;
    double gaz = 0.0;
    double hydro = 0.0;
    double wind = 0.0;
    double solar = 0.0;
    double nuclear = 0.0;
    double renewable = 0.0;
    double other = 0.0;

    public SourcePercentage() {
        super();
    }

    public double getOil() {
        return Math.round(oil/this.getTotal()*1000.00)/10.00;
    }

    public void addOil(double oil) {
        this.oil = this.oil + oil;
    }

    public double getCoal() {
        return Math.round(coal/this.getTotal()*1000.00)/10.00;
    }

    public void addCoal(double coal) {
        this.coal = this.coal + coal;
    }

    public double getGaz() {
        return Math.round(gaz/this.getTotal()*1000.00)/10.00;
    }

    public void addGaz(double gaz) {
        this.gaz = this.gaz + gaz;
    }

    public double getHydro() {
        return Math.round(hydro/this.getTotal()*1000.00)/10.00;
    }

    public void addHydro(double hydro) {
        this.hydro = this.hydro + hydro;
    }

    public double getWind() {
        return Math.round(wind/this.getTotal()*1000.00)/10.00;
    }

    public void addWind(double wind) {
        this.wind = this.wind + wind;
    }

    public double getSolar() {
        return Math.round(solar/this.getTotal()*1000.00)/10.00;
    }

    public void addSolar(double solar) {
        this.solar = this.solar + solar;
    }

    public double getNuclear() {
        return Math.round(nuclear/this.getTotal()*1000.00)/10.00;
    }

    public void addNuclear(double nuclear) {
        this.nuclear = this.nuclear + nuclear;
    }
        
    public double getRenewable() {
        return Math.round(renewable/this.getTotal()*1000.00)/10.00;
    }

    public void addRenewable(double renewable) {
        this.renewable = this.renewable + renewable;
    }
            
    public double getOther() {
        return Math.round(other/this.getTotal()*1000.00)/10.00;
    }

    public void addOther(double other) {
        this.other = this.other + other;
    }
            
    public double getTotal() {
        return (this.coal + this.gaz + this.hydro + this.oil + this.nuclear + this.wind + this.solar + this.renewable + this.other);
    }
    
    public double getGEC() {
        
        double v = (this.wind + this.solar + this.renewable) / (this.coal + this.gaz + this.hydro + this.oil + this.nuclear + this.wind + this.solar + this.renewable + this.other);
        
        return Math.round(v*1000.00)/10.00;
        
    }
    
    
}
