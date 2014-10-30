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
package eu.eco2clouds.experiment.eels.task.conf;

import eu.eco2clouds.ac.conf.INodeConfiguration;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 *  
 */
@XmlRootElement
public class InitDat implements INodeConfiguration {

    public static String TIME_PARAM_NAME = "time";
    public static String STEPH_PARAM_NAME = "step_h"; // step_h = 3
    public static String NEEL_PARAM_NAME = "Neel"; //Neel = 5000
    public static String YEARSTART_PARAM_NAME = "year_start"; //year_start = 82
    public static String YEARSTOP_PARAM_NAME = "year_stop"; //year_stop = 83
    public static String MONTHSTART_PARAM_NAME = "month_start"; //month_start = 1
    public static String MONTHSTOP_PARAM_NAME = "month_stop"; //month_stop = 7
    public static String TIMEKILLER_PARAM_NAME = "time_killer"; //time_killer = 4
    public static String IPCODE_PARAM_NAME = "ip_code"; //ip_code = ' 172.18.240.136'
    public static String PREBOUNDARY_PARAM_NAME = "preboundary"; //preboundary = -19
    public static String BOUNDARY_PARAM_NAME = "boundary"; //boundary = -15
    public static String DELTABIRTH_PARAM_NAME = "delta_birth"; //delta_birth = 1
    public static String DELTADEPTH_PARAM_NAME = "delta_depth"; //delta_depth = 0
    public static String DEPTHSTART_PARAM_NAME = "depth_start"; //depth_start = 2.5
    public static String DEPTHSTOP_PARAM_NAME = "depth_stop"; //depth_stop = 695.5
    public static String MUNIGHTSTART_PARAM_NAME = "mu_night_start"; //mu_night_start = 60
    public static String DMUNIGHT_PARAM_NAME = "dmu_night"; //dmu_night = 0
    public static String SIGMANIGHTSTART_PARAM_NAME = "sigma_night_start"; //sigma_night_start = 25
    public static String DSIGMANIGHT_PARAM_NAME = "dsigma_night"; //dsigma_night = 0
    public static String MUDAYSTART_PARAM_NAME = "mu_day_start"; //mu_day_start = 73.5294
    public static String DMUDAY_PARAM_NAME = "dmu_day"; //dmu_day = 5.2941
    public static String SIGMADAYSTART_PARAM_NAME = "sigma_day_start"; //sigma_day_start = 25
    public static String DSIGMADAY_PARAM_NAME = "dsigma_day"; //dsigma_day = 0
    public static String MUBIRTH_PARAM_NAME = "mu_birth"; //mu_birth = 175
    public static String SIGMABIRTH_PARAM_NAME = "sigma_birth"; //sigma_birth = 35
    public static String UPLIMBIRTH_PARAM_NAME = "up_lim_birth"; //up_lim_birth = 2.5
    public static String DOWNLIMBIRTH_PARAM_NAME = "down_lim_birth"; //down_lim_birth = 700
    public static String ALPHANAV_PARAM_NAME = "alpha_nav"; //alpha_nav = -1
    public static String BLS_PARAM_NAME = "BL_s"; //BL_s = 1.0
    public static String ADENSITY_PARAM_NAME = "a_density"; //a_density = 47.5
    public static String BMORTALITY_PARAM_NAME = "b_mortality"; //b_mortality = -0.46
    public static String ALPHAWEIGHT_PARAM_NAME = "alpha_weight"; //alpha_weight = 2.24e-7
    public static String BETAWEIGHT_PARAM_NAME = "beta_weight"; //beta_weight = 3.37
    public static String THRESHOLDLEGTH_PARAM_NAME = "threshold_length"; //threshold_length = 5
    public static String BIRTHLENGTH_PARAM_NAME = "birth_length"; //birth_length = 4
    public static String SIGMAGF_PARAM_NAME = "sigma_gf"; //sigma_gf = 0.0558
    public static String IDUM_PARAM_NAME = "idum"; //idum = 0 
    public static String SIGMATIMEBIRTH_PARAM_NAME = "sigma_time_birth"; //sigma_time_birth = 0.6667
    public static String MULAMBDA_PARAM_NAME = "mu_lambda"; //mu_lambda = -62
    public static String SIGMALAMBDA_PARAM_NAME = "sigma_lambda"; //sigma_lambda = 4
    public static String MUPHI_PARAM_NAME = "mu_phi"; //mu_phi = 26.5
    public static String SIGMAPHI_PARAM_NAME = "sigma_phi"; //sigma_phi = 1.1667
    public static String L0_PARAM_NAME = "L_0"; //L_0 = 4
    public static String ALPHAGROWTH_PARAM_NAME = "alpha_growth"; //alpha_growth = 12.03461480
    public static String BETAGROWTH_PARAM_NAME = "beta_growth"; //beta_growth = 0.53147271


    /**
	 * @uml.property  name="time"
	 */
    private int time = 5;
    /**
	 * @uml.property  name="step_h"
	 */
    private int step_h = 3;
    /**
	 * @uml.property  name="neel"
	 */
    private long Neel = 5000;
    /**
	 * @uml.property  name="year_start"
	 */
    private int year_start = 82;
    /**
	 * @uml.property  name="year_stop"
	 */
    private int year_stop = 83;
    /**
	 * @uml.property  name="month_start"
	 */
    private int month_start = 1;
    /**
	 * @uml.property  name="month_stop"
	 */
    private int month_stop = 7;
    /**
	 * @uml.property  name="time_killer"
	 */
    private int time_killer = 4;
    /**
	 * @uml.property  name="ip_code"
	 */
    private String ip_code = "' 172.18.240.136'";
    /**
	 * @uml.property  name="preboundary"
	 */
    private int preboundary = -19;
    /**
	 * @uml.property  name="boundary"
	 */
    private int boundary = -15;
    /**
	 * @uml.property  name="delta_birth"
	 */
    private int delta_birth = 1;
    /**
	 * @uml.property  name="delta_depth"
	 */
    private int delta_depth = 0;
    /**
	 * @uml.property  name="depth_start"
	 */
    private double depth_start = 2.5;
    /**
	 * @uml.property  name="depth_stop"
	 */
    private double depth_stop = 695.5;
    /**
	 * @uml.property  name="mu_night_start"
	 */
    private int mu_night_start = 60;
    /**
	 * @uml.property  name="dmu_night"
	 */
    private int dmu_night = 0;
    /**
	 * @uml.property  name="sigma_night_start"
	 */
    private int sigma_night_start = 25;
    /**
	 * @uml.property  name="dsigma_night"
	 */
    private int dsigma_night = 0;
    /**
	 * @uml.property  name="mu_day_start"
	 */
    private double mu_day_start = 73.5294;
    /**
	 * @uml.property  name="dmu_day"
	 */
    private double dmu_day = 5.2941;
    /**
	 * @uml.property  name="sigma_day_start"
	 */
    private int sigma_day_start = 25;
    /**
	 * @uml.property  name="dsigma_day"
	 */
    private int dsigma_day = 0;
    /**
	 * @uml.property  name="mu_birth"
	 */
    private int mu_birth = 175;
    /**
	 * @uml.property  name="sigma_birth"
	 */
    private int sigma_birth = 35;
    /**
	 * @uml.property  name="up_lim_birth"
	 */
    private double up_lim_birth = 2.5;
    /**
	 * @uml.property  name="down_lim_birth"
	 */
    private int down_lim_birth = 700;
    /**
	 * @uml.property  name="alpha_nav"
	 */
    private int alpha_nav = -1;
    /**
	 * @uml.property  name="bL_s"
	 */
    private double BL_s = 1.0;
    /**
	 * @uml.property  name="a_density"
	 */
    private double a_density = 47.5;
    /**
	 * @uml.property  name="b_mortality"
	 */
    private double b_mortality = -0.46;
    /**
	 * @uml.property  name="alpha_weight"
	 */
    private double alpha_weight = 2.24e-7;
    /**
	 * @uml.property  name="beta_weight"
	 */
    private double beta_weight = 3.37;
    /**
	 * @uml.property  name="threshold_length"
	 */
    private int threshold_length = 5;
    /**
	 * @uml.property  name="birth_length"
	 */
    private int birth_length = 4;
    /**
	 * @uml.property  name="sigma_gf"
	 */
    private double sigma_gf = 0.0558;
    /**
	 * @uml.property  name="idum"
	 */
    private int idum = 0; 
    /**
	 * @uml.property  name="sigma_time_birth"
	 */
    private double sigma_time_birth = 0.6667;
    /**
	 * @uml.property  name="mu_lambda"
	 */
    private int mu_lambda = -62;
    /**
	 * @uml.property  name="sigma_lambda"
	 */
    private int sigma_lambda = 4;
    /**
	 * @uml.property  name="mu_phi"
	 */
    private double mu_phi = 26.5;
    /**
	 * @uml.property  name="sigma_phi"
	 */
    private double sigma_phi = 1.1667;
    /**
	 * @uml.property  name="l_0"
	 */
    private int L_0 = 4;
    /**
	 * @uml.property  name="alpha_growth"
	 */
    private double alpha_growth = 12.03461480;
    /**
	 * @uml.property  name="beta_growth"
	 */
    private double beta_growth = 0.53147271;

    /**
	 * @return
	 * @uml.property  name="time"
	 */
    public int getTime() {
        return time;
    }

    /**
	 * @param time
	 * @uml.property  name="time"
	 */
    public void setTime(int time) {
        this.time = time;
    }

    /**
	 * @return
	 * @uml.property  name="step_h"
	 */
    public int getStep_h() {
        return step_h;
    }

    /**
	 * @param step_h
	 * @uml.property  name="step_h"
	 */
    public void setStep_h(int step_h) {
        this.step_h = step_h;
    }

    /**
	 * @return
	 * @uml.property  name="neel"
	 */
    public long getNeel() {
        return Neel;
    }

    /**
	 * @param Neel
	 * @uml.property  name="neel"
	 */
    public void setNeel(long Neel) {
        this.Neel = Neel;
    }

    /**
	 * @return
	 * @uml.property  name="year_start"
	 */
    public int getYear_start() {
        return year_start;
    }

    /**
	 * @param year_start
	 * @uml.property  name="year_start"
	 */
    public void setYear_start(int year_start) {
        this.year_start = year_start;
    }

    /**
	 * @return
	 * @uml.property  name="year_stop"
	 */
    public int getYear_stop() {
        return year_stop;
    }

    /**
	 * @param year_stop
	 * @uml.property  name="year_stop"
	 */
    public void setYear_stop(int year_stop) {
        this.year_stop = year_stop;
    }

    /**
	 * @return
	 * @uml.property  name="month_start"
	 */
    public int getMonth_start() {
        return month_start;
    }

    /**
	 * @param month_start
	 * @uml.property  name="month_start"
	 */
    public void setMonth_start(int month_start) {
        this.month_start = month_start;
    }

    /**
	 * @return
	 * @uml.property  name="month_stop"
	 */
    public int getMonth_stop() {
        return month_stop;
    }

    /**
	 * @param month_stop
	 * @uml.property  name="month_stop"
	 */
    public void setMonth_stop(int month_stop) {
        this.month_stop = month_stop;
    }

    /**
	 * @return
	 * @uml.property  name="time_killer"
	 */
    public int getTime_killer() {
        return time_killer;
    }

    /**
	 * @param time_killer
	 * @uml.property  name="time_killer"
	 */
    public void setTime_killer(int time_killer) {
        this.time_killer = time_killer;
    }

    /**
	 * @return
	 * @uml.property  name="ip_code"
	 */
    public String getIp_code() {
        return ip_code;
    }

    /**
	 * @param ip_code
	 * @uml.property  name="ip_code"
	 */
    public void setIp_code(String ip_code) {
        this.ip_code = ip_code;
    }

    /**
	 * @return
	 * @uml.property  name="preboundary"
	 */
    public int getPreboundary() {
        return preboundary;
    }

    /**
	 * @param preboundary
	 * @uml.property  name="preboundary"
	 */
    public void setPreboundary(int preboundary) {
        this.preboundary = preboundary;
    }

    /**
	 * @return
	 * @uml.property  name="boundary"
	 */
    public int getBoundary() {
        return boundary;
    }

    /**
	 * @param boundary
	 * @uml.property  name="boundary"
	 */
    public void setBoundary(int boundary) {
        this.boundary = boundary;
    }

    /**
	 * @return
	 * @uml.property  name="delta_birth"
	 */
    public int getDelta_birth() {
        return delta_birth;
    }

    /**
	 * @param delta_birth
	 * @uml.property  name="delta_birth"
	 */
    public void setDelta_birth(int delta_birth) {
        this.delta_birth = delta_birth;
    }

    /**
	 * @return
	 * @uml.property  name="delta_depth"
	 */
    public int getDelta_depth() {
        return delta_depth;
    }

    /**
	 * @param delta_depth
	 * @uml.property  name="delta_depth"
	 */
    public void setDelta_depth(int delta_depth) {
        this.delta_depth = delta_depth;
    }

    /**
	 * @return
	 * @uml.property  name="depth_start"
	 */
    public double getDepth_start() {
        return depth_start;
    }

    /**
	 * @param depth_start
	 * @uml.property  name="depth_start"
	 */
    public void setDepth_start(double depth_start) {
        this.depth_start = depth_start;
    }

    /**
	 * @return
	 * @uml.property  name="depth_stop"
	 */
    public double getDepth_stop() {
        return depth_stop;
    }

    /**
	 * @param depth_stop
	 * @uml.property  name="depth_stop"
	 */
    public void setDepth_stop(double depth_stop) {
        this.depth_stop = depth_stop;
    }

    /**
	 * @return
	 * @uml.property  name="mu_night_start"
	 */
    public int getMu_night_start() {
        return mu_night_start;
    }

    /**
	 * @param mu_night_start
	 * @uml.property  name="mu_night_start"
	 */
    public void setMu_night_start(int mu_night_start) {
        this.mu_night_start = mu_night_start;
    }

    /**
	 * @return
	 * @uml.property  name="dmu_night"
	 */
    public int getDmu_night() {
        return dmu_night;
    }

    /**
	 * @param dmu_night
	 * @uml.property  name="dmu_night"
	 */
    public void setDmu_night(int dmu_night) {
        this.dmu_night = dmu_night;
    }

    /**
	 * @return
	 * @uml.property  name="sigma_night_start"
	 */
    public int getSigma_night_start() {
        return sigma_night_start;
    }

    /**
	 * @param sigma_night_start
	 * @uml.property  name="sigma_night_start"
	 */
    public void setSigma_night_start(int sigma_night_start) {
        this.sigma_night_start = sigma_night_start;
    }

    /**
	 * @return
	 * @uml.property  name="dsigma_night"
	 */
    public int getDsigma_night() {
        return dsigma_night;
    }

    /**
	 * @param dsigma_night
	 * @uml.property  name="dsigma_night"
	 */
    public void setDsigma_night(int dsigma_night) {
        this.dsigma_night = dsigma_night;
    }

    /**
	 * @return
	 * @uml.property  name="mu_day_start"
	 */
    public double getMu_day_start() {
        return mu_day_start;
    }

    /**
	 * @param mu_day_start
	 * @uml.property  name="mu_day_start"
	 */
    public void setMu_day_start(double mu_day_start) {
        this.mu_day_start = mu_day_start;
    }

    /**
	 * @return
	 * @uml.property  name="dmu_day"
	 */
    public double getDmu_day() {
        return dmu_day;
    }

    /**
	 * @param dmu_day
	 * @uml.property  name="dmu_day"
	 */
    public void setDmu_day(double dmu_day) {
        this.dmu_day = dmu_day;
    }

    /**
	 * @return
	 * @uml.property  name="sigma_day_start"
	 */
    public int getSigma_day_start() {
        return sigma_day_start;
    }

    /**
	 * @param sigma_day_start
	 * @uml.property  name="sigma_day_start"
	 */
    public void setSigma_day_start(int sigma_day_start) {
        this.sigma_day_start = sigma_day_start;
    }

    /**
	 * @return
	 * @uml.property  name="dsigma_day"
	 */
    public int getDsigma_day() {
        return dsigma_day;
    }

    /**
	 * @param dsigma_day
	 * @uml.property  name="dsigma_day"
	 */
    public void setDsigma_day(int dsigma_day) {
        this.dsigma_day = dsigma_day;
    }

    /**
	 * @return
	 * @uml.property  name="mu_birth"
	 */
    public int getMu_birth() {
        return mu_birth;
    }

    /**
	 * @param mu_birth
	 * @uml.property  name="mu_birth"
	 */
    public void setMu_birth(int mu_birth) {
        this.mu_birth = mu_birth;
    }

    /**
	 * @return
	 * @uml.property  name="sigma_birth"
	 */
    public int getSigma_birth() {
        return sigma_birth;
    }

    /**
	 * @param sigma_birth
	 * @uml.property  name="sigma_birth"
	 */
    public void setSigma_birth(int sigma_birth) {
        this.sigma_birth = sigma_birth;
    }

    /**
	 * @return
	 * @uml.property  name="up_lim_birth"
	 */
    public double getUp_lim_birth() {
        return up_lim_birth;
    }

    /**
	 * @param up_lim_birth
	 * @uml.property  name="up_lim_birth"
	 */
    public void setUp_lim_birth(double up_lim_birth) {
        this.up_lim_birth = up_lim_birth;
    }

    /**
	 * @return
	 * @uml.property  name="down_lim_birth"
	 */
    public int getDown_lim_birth() {
        return down_lim_birth;
    }

    /**
	 * @param down_lim_birth
	 * @uml.property  name="down_lim_birth"
	 */
    public void setDown_lim_birth(int down_lim_birth) {
        this.down_lim_birth = down_lim_birth;
    }

    /**
	 * @return
	 * @uml.property  name="alpha_nav"
	 */
    public int getAlpha_nav() {
        return alpha_nav;
    }

    /**
	 * @param alpha_nav
	 * @uml.property  name="alpha_nav"
	 */
    public void setAlpha_nav(int alpha_nav) {
        this.alpha_nav = alpha_nav;
    }

    /**
	 * @return
	 * @uml.property  name="bL_s"
	 */
    public double getBL_s() {
        return BL_s;
    }

    /**
	 * @param BL_s
	 * @uml.property  name="bL_s"
	 */
    public void setBL_s(double BL_s) {
        this.BL_s = BL_s;
    }

    /**
	 * @return
	 * @uml.property  name="a_density"
	 */
    public double getA_density() {
        return a_density;
    }

    /**
	 * @param a_density
	 * @uml.property  name="a_density"
	 */
    public void setA_density(double a_density) {
        this.a_density = a_density;
    }

    /**
	 * @return
	 * @uml.property  name="b_mortality"
	 */
    public double getB_mortality() {
        return b_mortality;
    }

    /**
	 * @param b_mortality
	 * @uml.property  name="b_mortality"
	 */
    public void setB_mortality(double b_mortality) {
        this.b_mortality = b_mortality;
    }

    /**
	 * @return
	 * @uml.property  name="alpha_weight"
	 */
    public double getAlpha_weight() {
        return alpha_weight;
    }

    /**
	 * @param alpha_weight
	 * @uml.property  name="alpha_weight"
	 */
    public void setAlpha_weight(double alpha_weight) {
        this.alpha_weight = alpha_weight;
    }

    /**
	 * @return
	 * @uml.property  name="beta_weight"
	 */
    public double getBeta_weight() {
        return beta_weight;
    }

    /**
	 * @param beta_weight
	 * @uml.property  name="beta_weight"
	 */
    public void setBeta_weight(double beta_weight) {
        this.beta_weight = beta_weight;
    }

    /**
	 * @return
	 * @uml.property  name="threshold_length"
	 */
    public int getThreshold_length() {
        return threshold_length;
    }

    /**
	 * @param threshold_length
	 * @uml.property  name="threshold_length"
	 */
    public void setThreshold_length(int threshold_length) {
        this.threshold_length = threshold_length;
    }

    /**
	 * @return
	 * @uml.property  name="birth_length"
	 */
    public int getBirth_length() {
        return birth_length;
    }

    /**
	 * @param birth_length
	 * @uml.property  name="birth_length"
	 */
    public void setBirth_length(int birth_length) {
        this.birth_length = birth_length;
    }

    /**
	 * @return
	 * @uml.property  name="sigma_gf"
	 */
    public double getSigma_gf() {
        return sigma_gf;
    }

    /**
	 * @param sigma_gf
	 * @uml.property  name="sigma_gf"
	 */
    public void setSigma_gf(double sigma_gf) {
        this.sigma_gf = sigma_gf;
    }

    /**
	 * @return
	 * @uml.property  name="idum"
	 */
    public int getIdum() {
        return idum;
    }

    /**
	 * @param idum
	 * @uml.property  name="idum"
	 */
    public void setIdum(int idum) {
        this.idum = idum;
    }

    /**
	 * @return
	 * @uml.property  name="sigma_time_birth"
	 */
    public double getSigma_time_birth() {
        return sigma_time_birth;
    }

    /**
	 * @param sigma_time_birth
	 * @uml.property  name="sigma_time_birth"
	 */
    public void setSigma_time_birth(double sigma_time_birth) {
        this.sigma_time_birth = sigma_time_birth;
    }

    /**
	 * @return
	 * @uml.property  name="mu_lambda"
	 */
    public int getMu_lambda() {
        return mu_lambda;
    }

    /**
	 * @param mu_lambda
	 * @uml.property  name="mu_lambda"
	 */
    public void setMu_lambda(int mu_lambda) {
        this.mu_lambda = mu_lambda;
    }

    /**
	 * @return
	 * @uml.property  name="sigma_lambda"
	 */
    public int getSigma_lambda() {
        return sigma_lambda;
    }

    /**
	 * @param sigma_lambda
	 * @uml.property  name="sigma_lambda"
	 */
    public void setSigma_lambda(int sigma_lambda) {
        this.sigma_lambda = sigma_lambda;
    }

    /**
	 * @return
	 * @uml.property  name="mu_phi"
	 */
    public double getMu_phi() {
        return mu_phi;
    }

    /**
	 * @param mu_phi
	 * @uml.property  name="mu_phi"
	 */
    public void setMu_phi(double mu_phi) {
        this.mu_phi = mu_phi;
    }

    /**
	 * @return
	 * @uml.property  name="sigma_phi"
	 */
    public double getSigma_phi() {
        return sigma_phi;
    }

    /**
	 * @param sigma_phi
	 * @uml.property  name="sigma_phi"
	 */
    public void setSigma_phi(double sigma_phi) {
        this.sigma_phi = sigma_phi;
    }

    /**
	 * @return
	 * @uml.property  name="l_0"
	 */
    public int getL_0() {
        return L_0;
    }

    /**
	 * @param L_0
	 * @uml.property  name="l_0"
	 */
    public void setL_0(int L_0) {
        this.L_0 = L_0;
    }

    /**
	 * @return
	 * @uml.property  name="alpha_growth"
	 */
    public double getAlpha_growth() {
        return alpha_growth;
    }

    /**
	 * @param alpha_growth
	 * @uml.property  name="alpha_growth"
	 */
    public void setAlpha_growth(double alpha_growth) {
        this.alpha_growth = alpha_growth;
    }

    /**
	 * @return
	 * @uml.property  name="beta_growth"
	 */
    public double getBeta_growth() {
        return beta_growth;
    }

    /**
	 * @param beta_growth
	 * @uml.property  name="beta_growth"
	 */
    public void setBeta_growth(double beta_growth) {
        this.beta_growth = beta_growth;
    }

    @Override
    public String toString() {
        //return "&init\ntime = " + time + "\nstep_h = " + step_h + "\nNeel = " + Neel + "\nyear_start = " + year_start + "\nyear_stop = " + year_stop + "\nmonth_start = " + month_start + "\nmonth_stop = " + month_stop + "\ntime_killer = " + time_killer + "\nip_code = " + ip_code + "\npreboundary = " + preboundary + "\nboundary = " + boundary + "\ndelta_birth = " + delta_birth + "\ndelta_depth = " + delta_depth + "\ndepth_start = " + depth_start + "\ndepth_stop = " + depth_stop + "\nmu_night_start = " + mu_night_start + "\ndmu_night = " + dmu_night + "\nsigma_night_start = " + sigma_night_start + "\ndsigma_night = " + dsigma_night + "\nmu_day_start = " + mu_day_start + "\ndmu_day = " + dmu_day + "\nsigma_day_start = " + sigma_day_start + "\ndsigma_day = " + dsigma_day + "\nmu_birth = " + mu_birth + "\nsigma_birth = " + sigma_birth + "\nup_lim_birth = " + up_lim_birth + "\ndown_lim_birth = " + down_lim_birth + "\nalpha_nav = " + alpha_nav + "\nBL_s = " + BL_s + "\na_density = " + a_density + "\nb_mortality = " + b_mortality + "\nalpha_weight = " + alpha_weight + "\nbeta_weight = " + beta_weight + "\nthreshold_length = " + threshold_length + "\nbirth_length = " + birth_length + "\nsigma_gf = " + sigma_gf + "\nidum = " + idum + "\nsigma_time_birth = " + sigma_time_birth + "\nmu_lambda = " + mu_lambda + "\nsigma_lambda = " + sigma_lambda + "\nmu_phi = " + mu_phi + "\nsigma_phi = " + sigma_phi + "\nL_0 = " + L_0 + "\nalpha_growth = " + alpha_growth + "\nbeta_growth = " + beta_growth + "\n/\n";
        // without ipcode
        return "&init\ntime = " + time + "\nstep_h = " + step_h + "\nNeel = " + Neel + "\nyear_start = " + year_start + "\nyear_stop = " + year_stop + "\nmonth_start = " + month_start + "\nmonth_stop = " + month_stop + "\ntime_killer = " + time_killer + "\npreboundary = " + preboundary + "\nboundary = " + boundary + "\ndelta_birth = " + delta_birth + "\ndelta_depth = " + delta_depth + "\ndepth_start = " + depth_start + "\ndepth_stop = " + depth_stop + "\nmu_night_start = " + mu_night_start + "\ndmu_night = " + dmu_night + "\nsigma_night_start = " + sigma_night_start + "\ndsigma_night = " + dsigma_night + "\nmu_day_start = " + mu_day_start + "\ndmu_day = " + dmu_day + "\nsigma_day_start = " + sigma_day_start + "\ndsigma_day = " + dsigma_day + "\nmu_birth = " + mu_birth + "\nsigma_birth = " + sigma_birth + "\nup_lim_birth = " + up_lim_birth + "\ndown_lim_birth = " + down_lim_birth + "\nalpha_nav = " + alpha_nav + "\nBL_s = " + BL_s + "\na_density = " + a_density + "\nb_mortality = " + b_mortality + "\nalpha_weight = " + alpha_weight + "\nbeta_weight = " + beta_weight + "\nthreshold_length = " + threshold_length + "\nbirth_length = " + birth_length + "\nsigma_gf = " + sigma_gf + "\nidum = " + idum + "\nsigma_time_birth = " + sigma_time_birth + "\nmu_lambda = " + mu_lambda + "\nsigma_lambda = " + sigma_lambda + "\nmu_phi = " + mu_phi + "\nsigma_phi = " + sigma_phi + "\nL_0 = " + L_0 + "\nalpha_growth = " + alpha_growth + "\nbeta_growth = " + beta_growth + "\n/\n";
    }


    
    
    
}
