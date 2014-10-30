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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *  
 */
public class InitParser {

    private final static Logger LOGGER = Logger.getLogger(InitParser.class.getName());

    public static InitDat readFile(String filename) throws IOException {
        BufferedReader in = null;
        InitDat initdat = new InitDat();

        try {
            in = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = in.readLine()) != null) {

                String pairs[] = line.split("=");
                if (pairs.length == 2) {

                    String param = pairs[0].trim();
                    String value = pairs[1].trim();

                    InitParser.assign(param, value, initdat);

                }


            }


        } catch (IOException ex) {
            LOGGER.severe(ex.getMessage());
            throw ex;
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                LOGGER.severe(ex.getMessage());
                throw ex;
            }
            
            return initdat;
        }



    }

    public static boolean writeFile(String filename, InitDat initdat) throws IOException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            
            writer.write(initdat.toString());
            
            writer.close();
            
            writer = null;
            
            return true;
            
        } catch (IOException ex) {
            LOGGER.severe(ex.getMessage());
            throw ex;
        }
        
        
    }

    private static void assign(String param, String value, InitDat initdat) {

        if (param.equals(InitDat.TIME_PARAM_NAME)) {
            initdat.setTime(Integer.parseInt(value));
        } else if (param.equals(InitDat.STEPH_PARAM_NAME)) {
            initdat.setStep_h(Integer.parseInt(value));
        } else if (param.equals(InitDat.NEEL_PARAM_NAME)) {
            initdat.setNeel(Long.parseLong(value));
        } else if (param.equals(InitDat.YEARSTART_PARAM_NAME)) {
            initdat.setYear_start(Integer.parseInt(value));
        } else if (param.equals(InitDat.YEARSTOP_PARAM_NAME)) {
            initdat.setYear_stop(Integer.parseInt(value));
        } else if (param.equals(InitDat.MONTHSTART_PARAM_NAME)) {
            initdat.setMonth_start(Integer.parseInt(value));
        } else if (param.equals(InitDat.MONTHSTOP_PARAM_NAME)) {
            initdat.setMonth_stop(Integer.parseInt(value));
        } else if (param.equals(InitDat.TIMEKILLER_PARAM_NAME)) {
            initdat.setTime_killer(Integer.parseInt(value));
        } else if (param.equals(InitDat.IPCODE_PARAM_NAME)) {
            initdat.setIp_code(value);
        } else if (param.equals(InitDat.PREBOUNDARY_PARAM_NAME)) {
            initdat.setPreboundary(Integer.parseInt(value));
        } else if (param.equals(InitDat.BOUNDARY_PARAM_NAME)) {
            initdat.setBoundary(Integer.parseInt(value));
        } else if (param.equals(InitDat.DELTABIRTH_PARAM_NAME)) {
            initdat.setDelta_birth(Integer.parseInt(value));
        } else if (param.equals(InitDat.DELTADEPTH_PARAM_NAME)) {
            initdat.setDelta_depth(Integer.parseInt(value));
        } else if (param.equals(InitDat.DEPTHSTART_PARAM_NAME)) {
            initdat.setDepth_start(Double.parseDouble(value));
        } else if (param.equals(InitDat.DEPTHSTOP_PARAM_NAME)) {
            initdat.setDepth_stop(Double.parseDouble(value));
        } else if (param.equals(InitDat.MUNIGHTSTART_PARAM_NAME)) {
            initdat.setMu_night_start(Integer.parseInt(value));
        } else if (param.equals(InitDat.DMUNIGHT_PARAM_NAME)) {
            initdat.setDmu_night(Integer.parseInt(value));
        } else if (param.equals(InitDat.SIGMANIGHTSTART_PARAM_NAME)) {
            initdat.setSigma_night_start(Integer.parseInt(value));
        } else if (param.equals(InitDat.DSIGMANIGHT_PARAM_NAME)) {
            initdat.setDsigma_night(Integer.parseInt(value));
        } else if (param.equals(InitDat.MUDAYSTART_PARAM_NAME)) {
            initdat.setMu_day_start(Double.parseDouble(value));
        } else if (param.equals(InitDat.DMUDAY_PARAM_NAME)) {
            initdat.setDmu_day(Integer.parseInt(value));
        } else if (param.equals(InitDat.SIGMADAYSTART_PARAM_NAME)) {
            initdat.setSigma_day_start(Integer.parseInt(value));
        } else if (param.equals(InitDat.DSIGMADAY_PARAM_NAME)) {
            initdat.setDsigma_day(Integer.parseInt(value));
        } else if (param.equals(InitDat.MUBIRTH_PARAM_NAME)) {
            initdat.setMu_birth(Integer.parseInt(value));
        } else if (param.equals(InitDat.SIGMABIRTH_PARAM_NAME)) {
            initdat.setSigma_birth(Integer.parseInt(value));
        } else if (param.equals(InitDat.UPLIMBIRTH_PARAM_NAME)) {
            initdat.setUp_lim_birth(Double.parseDouble(value));
        } else if (param.equals(InitDat.DOWNLIMBIRTH_PARAM_NAME)) {
            initdat.setDown_lim_birth(Integer.parseInt(value));
        } else if (param.equals(InitDat.ALPHANAV_PARAM_NAME)) {
            initdat.setAlpha_nav(Integer.parseInt(value));
        } else if (param.equals(InitDat.BLS_PARAM_NAME)) {
            initdat.setBL_s(Double.parseDouble(value));
        } else if (param.equals(InitDat.ADENSITY_PARAM_NAME)) {
            initdat.setA_density(Double.parseDouble(value));
        } else if (param.equals(InitDat.BMORTALITY_PARAM_NAME)) {
            initdat.setB_mortality(Double.parseDouble(value));
        } else if (param.equals(InitDat.ALPHAWEIGHT_PARAM_NAME)) {
            initdat.setAlpha_weight(Double.parseDouble(value));
        } else if (param.equals(InitDat.BETAWEIGHT_PARAM_NAME)) {
            initdat.setBeta_weight(Double.parseDouble(value));
        } else if (param.equals(InitDat.THRESHOLDLEGTH_PARAM_NAME)) {
            initdat.setThreshold_length(Integer.parseInt(value));
        } else if (param.equals(InitDat.BIRTHLENGTH_PARAM_NAME)) {
            initdat.setBirth_length(Integer.parseInt(value));
        } else if (param.equals(InitDat.SIGMAGF_PARAM_NAME)) {
            initdat.setSigma_gf(Double.parseDouble(value));
        } else if (param.equals(InitDat.IDUM_PARAM_NAME)) {
            initdat.setIdum(Integer.parseInt(value));
        } else if (param.equals(InitDat.SIGMATIMEBIRTH_PARAM_NAME)) {
            initdat.setSigma_time_birth(Double.parseDouble(value));
        } else if (param.equals(InitDat.MULAMBDA_PARAM_NAME)) {
            initdat.setMu_lambda(Integer.parseInt(value));
        } else if (param.equals(InitDat.SIGMALAMBDA_PARAM_NAME)) {
            initdat.setSigma_lambda(Integer.parseInt(value));
        } else if (param.equals(InitDat.MUPHI_PARAM_NAME)) {
            initdat.setMu_phi(Double.parseDouble(value));
        } else if (param.equals(InitDat.SIGMAPHI_PARAM_NAME)) {
            initdat.setSigma_phi(Double.parseDouble(value));
        } else if (param.equals(InitDat.L0_PARAM_NAME)) {
            initdat.setL_0(Integer.parseInt(value));
        } else if (param.equals(InitDat.ALPHAGROWTH_PARAM_NAME)) {
            initdat.setAlpha_growth(Double.parseDouble(value));
        } else if (param.equals(InitDat.BETAGROWTH_PARAM_NAME)) {
            initdat.setBeta_growth(Double.parseDouble(value));
        }

    }

    public static void main(String args[]) throws Exception {

        InitParser parser = new InitParser();


        InitDat ini = parser.readFile("src/main/resources/init.dat");
        parser.writeFile("src/main/resources/init1.dat", ini);

    }
}
