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

import eu.eco2clouds.portal.Configuration;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *  
 */
public class OctaveManager {

    private String octaveDir;
    private String octavePath;
    
    public OctaveManager() {
        
        octaveDir = Configuration.octaveDir;
        octavePath = Configuration.octavePath;
        
    }

    private String[] getCpuLoad(double cpus, String location, HashMap<String, Double> cpuloads) {

        String host = "";

        String cpuload = "";
        if (location.equals("uk-epcc")) {
            double[] nocpuhost = new double[]{48.0, 48.0, 16.0, 16.0, 16.0, 16.0, 16.0};
            cpuload = "[";
            host = "[";
            for (int i = 0; i < 7; i++) {
                double c = cpuloads.get("/testbeds/uk-epcc/hosts/vmhost" + i + "/monitoring");
                cpuload = cpuload + c;
                if (i < 6) {
                    cpuload = cpuload + ",";
                } else {
                    cpuload = cpuload + "]";
                }

                if ((c + cpus * 100.00 / nocpuhost[i]) < 100) {
                    host = host + (i + 1) + ",";

                }
            }
            if (host.endsWith(",")) {
                host = host.substring(0, host.length() - 1) + "]";
            } else {
                host = host + "]";
            }

        }

        if (location.equals(
                "fr-inria")) {
            double[] nocpuhost = new double[]{24.0, 24.0, 24.0, 24.0};
            cpuload = "[";
            host = "[";
            for (int i = 1; i < 5; i++) {
                double c = cpuloads.get("/testbeds/fr-inria/hosts/bonfire-blade-" + i + ".bonfire.grid5000.fr/monitoring");
                cpuload = cpuload + c;
                if (i < 4) {
                    cpuload = cpuload + ",";
                } else {
                    cpuload = cpuload + "]";
                }

                if ((c + cpus * 100.00 / nocpuhost[i - 1]) < 100) {
                    host = host + i + ",";

                }
            }
            if (host.endsWith(",")) {
                host = host.substring(0, host.length() - 1) + "]";
            } else {
                host = host + "]";
            }

        }

        if (location.equals(
                "de-hlrs")) {
            double[] nocpuhost = new double[]{4.0, 4.0, 4.0, 4.0, 4.0, 48.0, 48.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0};

            host = "[";
            cpuload = "[";
            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/floccus01/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/floccus01/monitoring") + cpus * 100.00 / nocpuhost[0]) < 100) {
                host = host + "1,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/floccus02/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/floccus02/monitoring") + cpus * 100.00 / nocpuhost[1]) < 100) {
                host = host + "2,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/floccus03/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/floccus03/monitoring") + cpus * 100.00 / nocpuhost[2]) < 100) {
                host = host + "3,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/floccus04/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/floccus04/monitoring") + cpus * 100.00 / nocpuhost[3]) < 100) {
                host = host + "4,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/floccus06/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/floccus06/monitoring") + cpus * 100.00 / nocpuhost[4]) < 100) {
                host = host + "5,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/floccus15/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/floccus15/monitoring") + cpus * 100.00 / nocpuhost[5]) < 100) {
                host = host + "6,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/floccus16/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/floccus16/monitoring") + cpus * 100.00 / nocpuhost[6]) < 100) {
                host = host + "7,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0101/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0101/monitoring") + cpus * 100.00 / nocpuhost[7]) < 100) {
                host = host + "8,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0102/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0102/monitoring") + cpus * 100.00 / nocpuhost[8]) < 100) {
                host = host + "9,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0103/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0103/monitoring") + cpus * 100.00 / nocpuhost[9]) < 100) {
                host = host + "10,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0104/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0104/monitoring") + cpus * 100.00 / nocpuhost[10]) < 100) {
                host = host + "11,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0105/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0105/monitoring") + cpus * 100.00 / nocpuhost[11]) < 100) {
                host = host + "12,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0106/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0107/monitoring") + cpus * 100.00 / nocpuhost[12]) < 100) {
                host = host + "13,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0107/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0108/monitoring") + cpus * 100.00 / nocpuhost[13]) < 100) {
                host = host + "14,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0108/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0108/monitoring") + cpus * 100.00 / nocpuhost[14]) < 100) {
                host = host + "15,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0109/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0109/monitoring") + cpus * 100.00 / nocpuhost[15]) < 100) {
                host = host + "16,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0110/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0110/monitoring") + cpus * 100.00 / nocpuhost[16]) < 100) {
                host = host + "17,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0111/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0111/monitoring") + cpus * 100.00 / nocpuhost[17]) < 100) {
                host = host + "18,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0112/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0112/monitoring") + cpus * 100.00 / nocpuhost[18]) < 100) {
                host = host + "19,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0113/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0113/monitoring") + cpus * 100.00 / nocpuhost[19]) < 100) {
                host = host + "20,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0114/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0114/monitoring") + cpus * 100.00 / nocpuhost[20]) < 100) {
                host = host + "21,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0201/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0201/monitoring") + cpus * 100.00 / nocpuhost[21]) < 100) {
                host = host + "22,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0202/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0202/monitoring") + cpus * 100.00 / nocpuhost[22]) < 100) {
                host = host + "23,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0203/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0203/monitoring") + cpus * 100.00 / nocpuhost[23]) < 100) {
                host = host + "24,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0204/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0204/monitoring") + cpus * 100.00 / nocpuhost[24]) < 100) {
                host = host + "25,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0205/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0205/monitoring") + cpus * 100.00 / nocpuhost[25]) < 100) {
                host = host + "26,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0206/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0206/monitoring") + cpus * 100.00 / nocpuhost[26]) < 100) {
                host = host + "27,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0207/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0207/monitoring") + cpus * 100.00 / nocpuhost[27]) < 100) {
                host = host + "28,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0208/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0208/monitoring") + cpus * 100.00 / nocpuhost[28]) < 100) {
                host = host + "29,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0209/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0209/monitoring") + cpus * 100.00 / nocpuhost[29]) < 100) {
                host = host + "30,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0210/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0210/monitoring") + cpus * 100.00 / nocpuhost[30]) < 100) {
                host = host + "31,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0211/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0211/monitoring") + cpus * 100.00 / nocpuhost[31]) < 100) {
                host = host + "32,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0212/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0212/monitoring") + cpus * 100.00 / nocpuhost[32]) < 100) {
                host = host + "33,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0213/monitoring") + ",";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0213/monitoring") + cpus * 100.00 / nocpuhost[33]) < 100) {
                host = host + "34,";
            }

            cpuload = cpuload + cpuloads.get("/testbeds/de-hlrs/hosts/node0214/monitoring") + "]";
            if ((cpuloads.get("/testbeds/de-hlrs/hosts/node0214/monitoring") + cpus * 100.00 / nocpuhost[34]) < 100) {
                host = host + "35,";
            }

            if (host.endsWith(",")) {
                host = host.substring(0, host.length() - 1) + "]";
            } else {
                host = host + "]";
            }

        }
        return new String[]{cpuload, host};
    }

    public PowerItem powerSite(double no_cpu, HashMap<String, Double> cpuloads, String location) {

        PowerItem result = null;

        String loc = "";

        if (location.equals("fr-inria")) {
            loc = "1";
        }
        if (location.equals("uk-epcc")) {
            loc = "2";
        }
        if (location.equals("de-hlrs")) {
            loc = "3";
        }

        String current_cpuload = this.getCpuLoad(no_cpu, location, cpuloads)[0];
        String hosts = this.getCpuLoad(no_cpu, location, cpuloads)[1];

        String command = "[min_power max_power]=computepowersite(" + no_cpu + ", " + hosts + "," + current_cpuload + "," + loc + ")";

        System.out.println(command);
        try {
            ProcessBuilder pb = new ProcessBuilder(this.octaveDir  + "/octave", "--eval", "addpath(\"" + this.octavePath + "\");" + command);
            Process p = pb.start();

            //Process p = Runtime.getRuntime().exec(new String[]{"/usr/local/octave/3.8.0/bin/octave", "--eval", "addpath(\"/Users/plebani/Documents/MATLAB\");" + command});
            InputStream stream = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String line;

            int start = 0;
            double min_power = 0.0;
            double max_power = 0.0;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                if (start == 1 || start == 3) {
                    if (line != null && !line.equals("")) {
                        if (start == 1) {
                            start = 2;
                        }

                    }
                }
                if (line.startsWith("min_power")) {
                    start = 1;
                    min_power = (Double.parseDouble(line.substring(12)));
                }
                if (line.startsWith("max_power")) {
                    start = 3;
                    max_power = (Double.parseDouble(line.substring(12)));
                }
            }
            InputStream estream = p.getErrorStream();
            BufferedReader ebr = new BufferedReader(new InputStreamReader(estream));
            String eline;
            while ((eline = ebr.readLine()) != null) {
                System.out.println(eline);

            }

            Logger.getLogger(OctaveManager.class
                    .getName()).log(Level.INFO, "Octave return power = " + min_power + " " + max_power);
            result = new PowerItem();

            result.setMin(min_power);

            result.setMax(max_power);

        } catch (IOException ex) {
            Logger.getLogger(OctaveManager.class
                    .getName()).log(Level.SEVERE, ex.getMessage(), ex);

        }
        return result;
    }

    //location, power, month, dayofmonth, dayofweek ,hour, minute, duration
    public CO2Item co2Site(PowerItem power, double initial_factor, String location, Date startDate, long duration) {

        CO2Item result = new CO2Item();

        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);

        int month = cal.get(Calendar.MONTH) + 1;
        int dayofmonth = cal.get(Calendar.DAY_OF_MONTH);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == Calendar.SUNDAY) {
            dayofweek = 7;
        } else {
            dayofweek = dayofweek - 1;
        }

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        String loc = "";

        if (location.equals("fr-inria")) {
            loc = "1";
        }
        if (location.equals("uk-epcc")) {
            loc = "2";
        }
        if (location.equals("de-hlrs")) {
            loc = "3";
        }

        String command = "[co2min co2max]=computeco2site(" + loc + ", " + power.getMin() + ", " + power.getMax() + ", " + initial_factor + "," + month + "," + dayofmonth + "," + dayofweek + "," + hour + ", " + minute + ", " + duration + ") ";
        System.out.println(command);
        try {
            //ProcessBuilder pb = new ProcessBuilder("/usr/local/octave/3.8.0/bin/octave", "--eval", "addpath(\"/Users/plebani/Documents/MATLAB\");" + command);
            ProcessBuilder pb = new ProcessBuilder(this.octaveDir  + "/octave", "--eval", "addpath(\"" + this.octavePath + "\");" + command);
            Process p = pb.start();

            //Process p = Runtime.getRuntime().exec(new String[]{"/usr/local/octave/3.8.0/bin/octave", "--eval", "addpath(\"/Users/plebani/Documents/MATLAB\");" + command});
            InputStream stream = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String line;

            int start = 0;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                if (start == 1 || start == 3) {
                    if (line != null && !line.equals("")) {
                        if (start == 1) {
                            start = 2;
                        }
                    }
                }
                if (line.startsWith("co2min")) {
                    start = 1;
                    result.setMin((Double.parseDouble(line.substring(10))));
                }
                if (line.startsWith("co2max")) {
                    start = 3;
                    result.setMax((Double.parseDouble(line.substring(10))));
                }
            }
            InputStream estream = p.getErrorStream();
            BufferedReader ebr = new BufferedReader(new InputStreamReader(estream));
            String eline;
            while ((eline = ebr.readLine()) != null) {
                System.out.println(eline);

            }

            Logger.getLogger(OctaveManager.class
                    .getName()).log(Level.INFO, "Octave return co2 = " + result.getMin() + " - " + result.getMax());

        } catch (IOException ex) {
            Logger.getLogger(OctaveManager.class
                    .getName()).log(Level.SEVERE, ex.getMessage(), ex);

        }
        return result;
    }

    //location, power, month, dayofmonth, dayofweek ,hour, minute, duration
    public List<CO2Item> co2Prediction(PowerItem power, double initial_factor, String location, Date startDate, long duration) {

        int steps = 24 * 30; //30 days

        List<CO2Item> result = new LinkedList<CO2Item>();

        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);

        int month = cal.get(Calendar.MONTH) + 1;
        int dayofmonth = cal.get(Calendar.DAY_OF_MONTH);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        String loc = "";

        if (location.equals("fr-inria")) {
            loc = "1";
        }
        if (location.equals("uk-epcc")) {
            loc = "2";
        }
        if (location.equals("de-hlrs")) {
            loc = "3";
        }

        String command = "prediction=co2prediction(" + loc + ", " + power.getMin() + ", " + power.getMax() + ", " + initial_factor + "," + month + "," + dayofmonth + "," + dayofweek + "," + hour + ", " + minute + ", " + duration + ", " + steps + ") ";
        System.out.println(command);
        try {

            ProcessBuilder pb = new ProcessBuilder(this.octaveDir  + "/octave", "--eval", "addpath(\"" + this.octavePath + "\");" + command);
            Process p = pb.start();
            //Process p = Runtime.getRuntime().exec(new String[]{"/usr/local/octave/3.8.0/bin/octave", "--eval", "addpath(\"/Users/plebani/Documents/MATLAB\");" + command});
            InputStream stream = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String line;

            int start = 0;
            while ((line = br.readLine()) != null) {
                //System.out.println(line);
                if (start == 1 || start == 3) {
                    if (line != null && !line.equals("")) {
                        if (start == 1) {
                            String[] values = line.split("  ");
                            CO2Item item = new CO2Item();
                            item.setMin(Double.parseDouble(values[1]));
                            item.setMax(Double.parseDouble(values[2]));
                            result.add(item);
                        }
                    }
                }
                if (line.startsWith("prediction")) {
                    start = 1;
                }
            }
            InputStream estream = p.getErrorStream();
            BufferedReader ebr = new BufferedReader(new InputStreamReader(estream));
            String eline;
            while ((eline = ebr.readLine()) != null) {
                System.out.println(eline);

            }

            Logger.getLogger(OctaveManager.class
                    .getName()).log(Level.INFO, "Octave return co2 = " + result.size());

        } catch (IOException ex) {
            Logger.getLogger(OctaveManager.class
                    .getName()).log(Level.SEVERE, ex.getMessage(), ex);

        }
        return result;
    }

}
