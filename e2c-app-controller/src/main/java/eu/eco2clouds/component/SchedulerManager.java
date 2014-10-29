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
package eu.eco2clouds.component;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import eu.eco2clouds.ac.ACException;
import eu.eco2clouds.ac.Configuration;
import eu.eco2clouds.ac.monitor.Item;
import eu.eco2clouds.ac.monitor.ItemValue;
import eu.eco2clouds.ac.monitor.PM;
import eu.eco2clouds.ac.monitor.PMStatus;
import eu.eco2clouds.ac.monitor.VMStatus;
import eu.eco2clouds.accounting.datamodel.parser.Collection;
import eu.eco2clouds.accounting.datamodel.parser.Experiment;
import eu.eco2clouds.accounting.datamodel.parser.HostMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.TestbedMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.VM;
import eu.eco2clouds.accounting.datamodel.parser.VMMonitoring;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 *
 *  
 */
public class SchedulerManager {

    private final static Logger logger
            = Logger.getLogger(SchedulerManager.class.getName());
    private WebResource schedulerService;
    private String keystoreFilename;
    private String keystorePwd;
    private String url;
    private String experimentId;

    public SchedulerManager() throws Exception {

        Configuration.loadProperties();
        this.url = Configuration.getProperties().getProperty(Configuration.SCHEDULER_URL);
        this.keystoreFilename = Configuration.getProperties().getProperty(Configuration.KEYSTORE);
        this.keystorePwd = Configuration.getProperties().getProperty(Configuration.PASSWORD);
        this.experimentId = Configuration.getProperties().getProperty(Configuration.EXPERIMENT_NO);

        logger.log(Level.INFO, "Connecting to ECO2Clouds Scheduler ..." + this.url);

        ClientConfig config = new DefaultClientConfig();

        config.getProperties().put(com.sun.jersey.client.urlconnection.HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new com.sun.jersey.client.urlconnection.HTTPSProperties(this.getHostnameVerifier(), this.getSSLContext()));
        Client client = Client.create(config);

        schedulerService = client.resource(this.url);

        try {

            this.pingScheduler();
            logger.log(Level.INFO, "Connection to scheduler at " + this.url + " established.");

        } catch (ACException ex) {
            logger.log(Level.INFO, "Impossible to establish a connection to scheduler at " + this.url);
            throw ex;
        }

    }

    public ClientResponse pingScheduler() throws ACException {

        try {

            ClientResponse response = schedulerService.path("/").get(ClientResponse.class);

            //System.out.println(response.getEntity(String.class));
            return response;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new ACException("Scheduler does not respond", ex);
        }
    }

    private Experiment getExperiment() {
        try {

            Experiment result = null;

            schedulerService.getRequestBuilder().header("x-bonfire-asserted-groups", "eco2clouds");
            String response = schedulerService.path("experiments").accept(MediaType.WILDCARD).get(String.class);
            //System.out.println(response);
            JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Collection collection = (Collection) jaxbUnmarshaller.unmarshal(new StringReader(response));

            List<Experiment> experiments = collection.getItems().getExperiments();

            for (Experiment experiment : experiments) {
                if (experiment.getBonfireExperimentId().toString().equals(this.experimentId)) {
                    result = experiment;
                }
            }

            return result;

        } catch (JAXBException ex) {
            Logger.getLogger(SchedulerManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public List<eu.eco2clouds.ac.monitor.VM> getExperimentVMs() {
        List<eu.eco2clouds.ac.monitor.VM> vms = new LinkedList<eu.eco2clouds.ac.monitor.VM>();
        try {

            schedulerService.getRequestBuilder().header("x-bonfire-asserted-groups", "eco2clouds");
            String response = schedulerService.path("experiments/" + this.experimentId + "/vms").accept(MediaType.WILDCARD).get(String.class);
            //System.out.println(response);
            JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Collection collection = (Collection) jaxbUnmarshaller.unmarshal(new StringReader(response));

            if (collection.getItems() != null && collection.getItems().getTotal() > 0) {
                for (VM vmParsed : collection.getItems().getvMs()) {
                    eu.eco2clouds.ac.monitor.VM vm = new eu.eco2clouds.ac.monitor.VM();
                    vm.setId(vmParsed.getLinks().get(0).getHref());
                    vm.setAlaServer(vmParsed.getNics().get(0).getIp());
                    vm.setName(vmParsed.getName());
                    //System.out.println("add" + vm.getId());
                    vms.add(vm);
                }
            }

        } catch (JAXBException ex) {
            Logger.getLogger(SchedulerManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return vms;

    }

    public List<PM> getExperimentPMs() {

        List<PM> pms = new LinkedList<PM>();
        try {

            schedulerService.getRequestBuilder().header("x-bonfire-asserted-groups", "eco2clouds");
            String response = schedulerService.path("experiments/" + this.experimentId + "/vms").accept(MediaType.WILDCARD).get(String.class);
            //System.out.println(response);
            JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Collection collection = (Collection) jaxbUnmarshaller.unmarshal(new StringReader(response));

            if (collection.getItems() != null && collection.getItems().getTotal() > 0) {
                for (VM vmParsed : collection.getItems().getvMs()) {
                    String[] pieces = vmParsed.getBonfireUrl().split("/");

                    PM pm = new PM(vmParsed.getHost(), pieces[4]);
                    if (!pms.contains(pm)) {
                        pms.add(pm);
                    }
                }
            }

        } catch (JAXBException ex) {
            Logger.getLogger(SchedulerManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return pms;

    }

    public VMStatus getVMMonitor(VMStatus vmStatus) {
        List<eu.eco2clouds.ac.monitor.VM> vms = new LinkedList<eu.eco2clouds.ac.monitor.VM>();
        try {

            //schedulerService.addFilter(new LoggingFilter(Logger.getLogger(SchedulerManager.class.getName())));
            schedulerService.getRequestBuilder().header("x-bonfire-asserted-groups", "eco2clouds");
            String response = schedulerService.path(vmStatus.getVM().getId() + "/monitoring").accept(MediaType.WILDCARD).get(String.class);
            //System.out.println("response to " + vmStatus.getVM().getId() + "/monitoring\n" + response);
            JAXBContext jaxbContext = JAXBContext.newInstance(VMMonitoring.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            //TODO: non inserire l'item nel caso il clock sia identico all'ultimo
            VMMonitoring vmMonitoring = (VMMonitoring) jaxbUnmarshaller.unmarshal(new StringReader(response));
            if (vmMonitoring != null) {
                HashMap<Item, List<ItemValue>> metrics = vmStatus.getMetrics();
                for (Item item : metrics.keySet()) {
                    if (item.getId().equals(Metric.CPU_LOAD)) {
                        if (vmMonitoring.getCpuload() != null) {
                            metrics.get(item).add(new ItemValue(vmMonitoring.getCpuload().getClock(), vmMonitoring.getCpuload().getValue()));
                        }
                    } else if (item.getId().equals(Metric.CPU_UTIL)) {
                        if (vmMonitoring.getCpuutil() != null) {
                            metrics.get(item).add(new ItemValue(vmMonitoring.getCpuutil().getClock(), vmMonitoring.getCpuutil().getValue()));
                        }
                    } else if (item.getId().equals(Metric.DISK_FREE)) {
                        if (vmMonitoring.getDiskfree() != null) {
                            metrics.get(item).add(new ItemValue(vmMonitoring.getDiskfree().getClock(), vmMonitoring.getDiskfree().getValue()));
                        }
                    } else if (item.getId().equals(Metric.DISK_TOTAL)) {
                        if (vmMonitoring.getDisktotal() != null) {
                            metrics.get(item).add(new ItemValue(vmMonitoring.getDisktotal().getClock(), vmMonitoring.getDisktotal().getValue()));
                        }
                    } else if (item.getId().equals(Metric.DISK_USAGE)) {
                        if (vmMonitoring.getDiskusage() != null) {
                            metrics.get(item).add(new ItemValue(vmMonitoring.getDiskusage().getClock(), vmMonitoring.getDiskusage().getValue()));
                        }
                    } else if (item.getId().equals(Metric.IOPS)) {
                        if (vmMonitoring.getIops() != null) {
                            metrics.get(item).add(new ItemValue(vmMonitoring.getIops().getClock(), vmMonitoring.getIops().getValue()));
                        }
                    } else if (item.getId().equals(Metric.IO_UTIL)) {
                        if (vmMonitoring.getIoutil() != null) {
                            metrics.get(item).add(new ItemValue(vmMonitoring.getIoutil().getClock(), vmMonitoring.getIoutil().getValue()));
                        }
                    } else if (item.getId().equals(Metric.MEM_FREE)) {
                        if (vmMonitoring.getMemfree() != null) {
                            metrics.get(item).add(new ItemValue(vmMonitoring.getMemfree().getClock(), vmMonitoring.getMemfree().getValue()));
                        }
                    } else if (item.getId().equals(Metric.MEM_TOTAL)) {
                        if (vmMonitoring.getMemtotal() != null) {
                            metrics.get(item).add(new ItemValue(vmMonitoring.getMemtotal().getClock(), vmMonitoring.getMemtotal().getValue()));
                        }
                    } else if (item.getId().equals(Metric.MEM_USED)) {
                        if (vmMonitoring.getMemused() != null) {
                            metrics.get(item).add(new ItemValue(vmMonitoring.getMemused().getClock(), vmMonitoring.getMemused().getValue()));
                        }
                    } else if (item.getId().equals(Metric.NETIF_IN)) {
                        if (vmMonitoring.getNetifin() != null) {
                            metrics.get(item).add(new ItemValue(vmMonitoring.getNetifin().getClock(), vmMonitoring.getNetifin().getValue()));
                        }
                    } else if (item.getId().equals(Metric.NETIF_OUT)) {
                        if (vmMonitoring.getNetifout() != null) {
                            metrics.get(item).add(new ItemValue(vmMonitoring.getNetifout().getClock(), vmMonitoring.getNetifout().getValue()));
                        }
                    } else if (item.getId().equals(Metric.POWER)) {
                        if (vmMonitoring.getPower() != null) {
                            metrics.get(item).add(new ItemValue(vmMonitoring.getPower().getClock(), vmMonitoring.getPower().getValue()));
                        }
                    } else if (item.getId().equals(Metric.PROC_NUM)) {
                        if (vmMonitoring.getProcNum() != null) {
                            metrics.get(item).add(new ItemValue(vmMonitoring.getProcNum().getClock(), vmMonitoring.getProcNum().getValue()));
                        }
                    } else if (item.getId().equals(Metric.SWAP_FREE)) {
                        if (vmMonitoring.getSwapfree() != null) {
                            metrics.get(item).add(new ItemValue(vmMonitoring.getSwapfree().getClock(), vmMonitoring.getSwapfree().getValue()));
                        }
                    } else if (item.getId().equals(Metric.SWAP_TOTAL)) {
                        if (vmMonitoring.getSwaptotal() != null) {
                            metrics.get(item).add(new ItemValue(vmMonitoring.getSwaptotal().getClock(), vmMonitoring.getSwaptotal().getValue()));
                        }
                    }

                }
            }
        } catch (JAXBException ex) {
            Logger.getLogger(SchedulerManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return vmStatus;

    }

    public PMStatus getPMMonitor(PMStatus pmStatus) {
        List<eu.eco2clouds.ac.monitor.PM> pms = new LinkedList<eu.eco2clouds.ac.monitor.PM>();
        try {

            //schedulerService.addFilter(new LoggingFilter(Logger.getLogger(SchedulerManager.class.getName())));
            schedulerService.getRequestBuilder().header("x-bonfire-asserted-groups", "eco2clouds");
            String response = schedulerService.path("/testbeds/location" + pmStatus.getPM().getLocation() + "/hosts/" + pmStatus.getPM().getName() + "/monitoring").accept(MediaType.WILDCARD).get(String.class);
            //System.out.println("response to " + vmStatus.getVM().getId() + "/monitoring\n" + response);
            JAXBContext jaxbContext = JAXBContext.newInstance(HostMonitoring.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            //TODO: non inserire l'item nel caso il clock sia identico all'ultimo
            HostMonitoring hostMonitoring = (HostMonitoring) jaxbUnmarshaller.unmarshal(new StringReader(response));
            if (hostMonitoring != null) {
                HashMap<Item, List<ItemValue>> metrics = pmStatus.getMetrics();
                for (Item item : metrics.keySet()) {
                    if (item.getId().equals(Metric.PM_AVAILABILITY)) {
                        if (hostMonitoring.getAvailability() != null) {
                            metrics.get(item).add(new ItemValue(hostMonitoring.getAvailability().getClock(), hostMonitoring.getAvailability().getValue()));
                        }
                    } else if (item.getId().equals(Metric.PM_CPU_UTILIZATION)) {
                        if (hostMonitoring.getCpuUtilization() != null) {
                            metrics.get(item).add(new ItemValue(hostMonitoring.getCpuUtilization().getClock(), hostMonitoring.getCpuUtilization().getValue()));
                        }
                    } else if (item.getId().equals(Metric.PM_DISK_IOPS)) {
                        if (hostMonitoring.getDiskIOPS() != null) {
                            metrics.get(item).add(new ItemValue(hostMonitoring.getDiskIOPS().getClock(), hostMonitoring.getDiskIOPS().getValue()));
                        }
                    } else if (item.getId().equals(Metric.PM_POWER_CONSUMPTION)) {
                        if (hostMonitoring.getPowerConsumption() != null) {
                            metrics.get(item).add(new ItemValue(hostMonitoring.getPowerConsumption().getClock(), hostMonitoring.getPowerConsumption().getValue()));
                        }
                    } else if (item.getId().equals(Metric.PM_NO_VM)) {
                        if (hostMonitoring.getRunningVMs() != null) {
                            metrics.get(item).add(new ItemValue(hostMonitoring.getRunningVMs().getClock(), hostMonitoring.getRunningVMs().getValue()));
                        }
                    }

                }
            }
        } catch (JAXBException ex) {
            Logger.getLogger(SchedulerManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return pmStatus;

    }

    public Double getCurrentEnergyMixEmissions(String location) {

        try {
            if (location.equals("de-hlrs")) {
                //System.out.println("energy mix at " + location + " is " + (503.00 / 1000));

                return 503.00 / 1000;

            } else {
                schedulerService.getRequestBuilder().header("x-bonfire-asserted-groups", "eco2clouds");
                String response = schedulerService.path("testbeds/" + location + "/monitoring").accept(MediaType.WILDCARD).get(String.class);

                JAXBContext jaxbContext = JAXBContext.newInstance(TestbedMonitoring.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

                TestbedMonitoring testbedMonitoring = (TestbedMonitoring) jaxbUnmarshaller.unmarshal(new StringReader(response));

                double result = 0;
                result = testbedMonitoring.getOil().getValue() * 700;
                result = result + testbedMonitoring.getCoal().getValue() * 900;
                if (location.equals("fr-inria")) {
                    result = result + testbedMonitoring.getGaz().getValue() * 450;
                    result = result + testbedMonitoring.getHydraulic().getValue() * 120;
                }
                if (location.equals("uk-epcc")) {
                    result = result + testbedMonitoring.getCcgt().getValue() * 450;
                    result = result + testbedMonitoring.getOcgt().getValue() * 450;
                    result = result + testbedMonitoring.getPumpedStorage().getValue() * 45;
                    result = result + testbedMonitoring.getNpsHydro().getValue() * 120;
                }
                result = result + testbedMonitoring.getNuclear().getValue() * 65;
                result = result + testbedMonitoring.getWind().getValue() * 65;
                result = result + testbedMonitoring.getSolar().getValue() * 120;
                result = result + testbedMonitoring.getWater().getValue() * 120;

                //System.out.println("energy mix at " + location + " is " + (result / 100 / 1000));

                return result / 100 / 1000; //divided by 100 becaause they return percentage divided by 1000 beacuse is return Kgr/Kwh and we return gr/Kwh
            }

        } catch (JAXBException ex) {
            Logger.getLogger(SchedulerManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public void stopVM(VMStatus vmStatus) {

        //schedulerService.addFilter(new LoggingFilter(Logger.getLogger(SchedulerManager.class.getName())));
        schedulerService.getRequestBuilder().header("x-bonfire-asserted-groups", "eco2clouds");
        
        
        String response = schedulerService.path(vmStatus.getVM().getId()).type("application/vnd.eco2clouds+xml").accept(MediaType.WILDCARD).put(String.class, "<compute xmlns=\"http://api.bonfire-project.eu/doc/schemas/occi\"><state>suspended</state></compute>");
        //System.out.println("response to " + vmStatus.getVM().getId() + "/monitoring\n" + response);

    }

    public void resumeVM() {

    }

    private HostnameVerifier getHostnameVerifier() {
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        return hv;
    }

    private SSLContext getSSLContext() throws Exception {
        KeyStore trustStore;

        InputStream keyInput = new FileInputStream(this.keystoreFilename);

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(keyInput, this.keystorePwd.toCharArray());
        keyInput.close();

        // Use store to set private key.
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, this.keystorePwd.toCharArray());
        KeyManager[] kms = kmf.getKeyManagers();

        // Use store to set trusted certificates.
        TrustManagerFactory trustFactory = TrustManagerFactory.getInstance("PKIX");
        trustFactory.init(ks);
        TrustManager[] tms = trustFactory.getTrustManagers();

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(kms, tms, null);
        SSLContext.setDefault(sc);
        //this.sslSocketFactory = sc.getSocketFactory();

        return sc;

    }

    public static void main(String args[]) throws Exception {

        //SchedulerManager scheduler = new SchedulerManager("https://129.69.19.70/scheduler", "/etc/e2c-portal/keystore.jks", "importkey", "33");
        //SchedulerManager scheduler = new SchedulerManager("https://scheduler.integration.e2c.bonfire.grid5000.fr/scheduler", "/etc/e2c-portal/keystore.jks", "importkey");
        SchedulerManager scheduler = new SchedulerManager();

        //ClientResponse resp = scheduler.pingScheduler();
        //System.out.println(resp);
        Experiment experiment = scheduler.getExperiment();

        List<eu.eco2clouds.ac.monitor.VM> l = scheduler.getExperimentVMs();

        for (eu.eco2clouds.ac.monitor.VM vm : l) {
            VMStatus vms = new VMStatus(vm, new LinkedList<Item>());
            if (vms.getVM().getId().contains("26277")) {
                System.out.println("switching off 26227");
                scheduler.stopVM(vms);
            }
            //scheduler.getVMMonitor(vms);
        }

    }
}
