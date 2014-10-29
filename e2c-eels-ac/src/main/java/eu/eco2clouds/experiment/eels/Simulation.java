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

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 *  
 */
public class Simulation {

    public static void main(String args[]) throws Exception {

        int numvm = 3;

        double[] th = new double[numvm];
        double[] power = new double[numvm];
        double[] perf = new double[numvm];

        long[] years = new long[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};

        HashMap<Integer, ArrayList<Integer>> wl = new HashMap<Integer, ArrayList<Integer>>();

        boolean stay = true;

        ArrayList<Integer> wl1 = new ArrayList<Integer>();
        wl1.add(1);
        wl1.add(2);
        wl1.add(3);
        wl1.add(4);
        wl1.add(5);
        ArrayList<Integer> wl2 = new ArrayList<Integer>();
        wl2.add(6);
        wl2.add(7);
        wl2.add(8);
        wl2.add(9);
        wl2.add(10);
        ArrayList<Integer> wl3 = new ArrayList<Integer>();
        wl3.add(11);
        wl3.add(12);
        wl3.add(13);
        wl3.add(14);
        wl3.add(15);

        wl.put(1, wl1);
        wl.put(2, wl2);
        wl.put(3, wl3);

        while (stay) {

            for (Integer i : wl.keySet()) {
                System.out.print(i + " = ");
                for (Integer w : wl.get(i)) {
                    System.out.print(w + " ");
                }
                System.out.println();
            }
            Thread.sleep(1000);

            int finisher = 0;
            do {
                finisher = (int) Math.round(Math.random() * 2);
                //System.out.println("prova" + (finisher + 1));

            } while (wl.get(finisher + 1).size() == 0);
            System.out.println("finish " + (finisher + 1));

            wl.get(finisher + 1).remove(0);

            long remaining = 0;
            for (Integer i : wl.keySet()) {
                remaining = remaining + wl.get(i).size();
            }

            for (int i = 0; i < numvm; i++) {
                th[i] = Math.random() * 10;
                power[i] = Math.random() * 100;
                perf[i] = 1 / (th[i] * power[i]); // the lower the better so now the higher the better
            }

            double tot = 0;
            for (int i = 0; i < numvm; i++) {
                tot = tot + perf[i];
            }

            int min = 0;
            int max = 0;
            for (int i = 0; i < numvm; i++) { // to normalize

                perf[i] = perf[i] / tot;
                if (min == 0 || perf[i] < perf[min]) {
                    min = i;
                }
                if (max == 0 || perf[i] > perf[max]) {
                    max = i;
                }

            }
            //System.out.println(perf[0] + " " + perf[1] + " " + perf[2] + " = " + (perf[0] + perf[1] + perf[2]));

            stay = remaining != 0;

            if (remaining != 0) {
                long[] wla = new long[3];

                for (int i = 0; i < numvm; i++) {
                    wla[i] = Math.round(perf[i] * remaining);
                }
                long checkyears = 0; // check if remainders create a new year
                for (int i = 0; i < numvm; i++) {
                    checkyears = checkyears + wla[i];
                }
                //System.out.println("check " + checkyears);
                if (checkyears > 15) {
                    wla[max] = wla[max] - (checkyears - remaining);
                }
                if (checkyears < 15) {
                    wla[max] = wla[max] + (remaining - checkyears);
                }

                ArrayList<Integer> all = new ArrayList<Integer>();
                for (Integer i : wl.keySet()) {
                    for (Integer w : wl.get(i)) {
                        all.add(w);

                    }
                    wl.get(i).clear();
                }

                for (int j = 0; j < wla.length; j++) {
                    for (int k = 0; k < wla[j]; k++) {
                        wl.get(j + 1).add(all.get(0));
                        all.remove(0);
                    }
                }

            }
        }
    }

}
