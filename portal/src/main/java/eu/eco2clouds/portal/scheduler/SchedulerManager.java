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
package eu.eco2clouds.portal.scheduler;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import eu.eco2clouds.accounting.datamodel.parser.Collection;
import eu.eco2clouds.accounting.datamodel.parser.Experiment;
import eu.eco2clouds.accounting.datamodel.parser.ExperimentReport;
import eu.eco2clouds.accounting.datamodel.parser.Host;
import eu.eco2clouds.accounting.datamodel.parser.HostMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.Testbed;
import eu.eco2clouds.accounting.datamodel.parser.TestbedMonitoring;
import eu.eco2clouds.accounting.datamodel.parser.VM;
import eu.eco2clouds.applicationProfile.datamodel.ApplicationProfile;
import eu.eco2clouds.portal.compute.SourcePercentage;
import eu.eco2clouds.portal.exception.E2CPortalException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.security.KeyStore;
import java.util.ArrayList;
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
    private List<Testbed> sites = null;

    public SchedulerManager(String url, String keystoreFilename, String keystorePwd) throws Exception {

        this.url = url;
        this.keystoreFilename = keystoreFilename;
        this.keystorePwd = keystorePwd;

        logger.log(Level.INFO, "Connecting to ECO2Clouds Scheduler ..." + url);

        ClientConfig config = new DefaultClientConfig();

        config.getProperties().put(com.sun.jersey.client.urlconnection.HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new com.sun.jersey.client.urlconnection.HTTPSProperties(this.getHostnameVerifier(), this.getSSLContext()));
        Client client = Client.create(config);

        schedulerService = client.resource(url);

        logger.log(Level.INFO, "Connection to scheduler at " + url + " established.");
    }

    public ClientResponse pingScheduler() throws E2CPortalException {

        try {

            ClientResponse response = schedulerService.path("/").get(ClientResponse.class);

            //System.out.println(response.getEntity(String.class));
            return response;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new E2CPortalException(ex);
        }
    }

    public String submitApplicationProfile(ApplicationProfile applicationProfile) throws E2CPortalException {

        logger.log(Level.INFO, "Sending application profile to ECO2Clouds Scheduler ...");
        logger.log(Level.INFO, applicationProfile.toString());
        try {

            String applicationProfileStr = new ApplicationProfileParserManager().deserialize(applicationProfile);

            logger.log(Level.FINE, applicationProfileStr);
            ClientResponse response = schedulerService.path("/experiments").header("x-bonfire-asserted-id", "dperez").header("x-bonfire-group-id", "eco2clouds").type(MediaType.APPLICATION_JSON).post(ClientResponse.class, applicationProfileStr);
            logger.log(Level.INFO, "Application Profile sent");
            return response.getEntity(String.class);

        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new E2CPortalException(ex);
        }

    }

    public List<Experiment> getExperiments() {
        try {
            //schedulerService.addFilter(new LoggingFilter(Logger.getLogger(SchedulerManager.class.getName())));

            schedulerService.getRequestBuilder().header("x-bonfire-asserted-groups", "eco2clouds");
            String response = schedulerService.path("experiments").accept(MediaType.WILDCARD).get(String.class);
            //System.out.println(response);
            JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Collection collection = (Collection) jaxbUnmarshaller.unmarshal(new StringReader(response));

            return collection.getItems().getExperiments();
        } catch (UniformInterfaceException ex) {
            Logger.getLogger(SchedulerManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (JAXBException ex) {
            Logger.getLogger(SchedulerManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public Experiment getExperiment(int id) {
        try {

            //schedulerService.addFilter(new LoggingFilter(Logger.getLogger(SchedulerManager.class.getName())));
            schedulerService.getRequestBuilder().header("x-bonfire-asserted-groups", "eco2clouds");
            String response = schedulerService.path("experiments/" + id).accept(MediaType.WILDCARD).get(String.class);
            //System.out.println(response);
            JAXBContext jaxbContext = JAXBContext.newInstance(Experiment.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (Experiment) jaxbUnmarshaller.unmarshal(new StringReader(response));

        } catch (JAXBException ex) {
            Logger.getLogger(SchedulerManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public HostMonitoring getHostMonitoring(String location, String host) {
        try {
            //schedulerService.addFilter(new LoggingFilter(Logger.getLogger(SchedulerManager.class.getName())));
            schedulerService.getRequestBuilder().header("x-bonfire-asserted-groups", "eco2clouds");
            String response = schedulerService.path("testbeds/" + location + "/hosts/" + host + "/monitoring").accept(MediaType.WILDCARD).get(String.class);
            JAXBContext jaxbContext = JAXBContext.newInstance(HostMonitoring.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (HostMonitoring) jaxbUnmarshaller.unmarshal(new StringReader(response));
        } catch (JAXBException ex) {
            Logger.getLogger(SchedulerManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public HashMap<String, Double> getCpuLoadLocation(String location) {

        HashMap<String, Double> result = new HashMap<String, Double>();

        try {
            //schedulerService.addFilter(new LoggingFilter(Logger.getLogger(SchedulerManager.class.getName())));

            schedulerService.getRequestBuilder().header("x-bonfire-asserted-groups", "eco2clouds");
            String response = schedulerService.path("testbeds/" + location + "/hosts").accept(MediaType.WILDCARD).get(String.class);
            JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            Collection collection = (Collection) jaxbUnmarshaller.unmarshal(new StringReader(response));
            if (collection != null && collection.getItems() != null) {
                List<Host> hosts = collection.getItems().getHosts();
                Double cpuload = new Double(0.0);
                for (Host host : hosts) {

                    HostMonitoring hostMonitoring = this.getHostMonitoring(location, host.getName());
                    if (hostMonitoring.getCpuUtilization() != null) {
                        result.put(hostMonitoring.getHref(), hostMonitoring.getCpuUtilization().getValue());
                        //System.out.println("add " + hostMonitoring.getHref() + " " + hostMonitoring.getCpuUtilization().getValue());
                    } else {
                        result.put(hostMonitoring.getHref(), 1.0);
                        //System.out.println("add " + hostMonitoring.getHref() + " 100.0");
                    }
                }
                return result;
            } else {
                return null;
            }
        } catch (JAXBException ex) {
            Logger.getLogger(SchedulerManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public SourcePercentage getGEC(ArrayList<String> locations) {

        SourcePercentage sp = new SourcePercentage();

        for (String location: locations) {

            try {

                if (location.equals("de-hlrs")) {
                    
                    sp.addRenewable(9.4);
                    sp.addCoal(14.1);
                    sp.addNuclear(12.3);
                    sp.addOther(64.2);
                    
                } else {

                    schedulerService.getRequestBuilder().header("x-bonfire-asserted-groups", "eco2clouds");
                    String response = schedulerService.path("testbeds/" + location + "/monitoring").accept(MediaType.WILDCARD).get(String.class);

                    JAXBContext jaxbContext = JAXBContext.newInstance(TestbedMonitoring.class);
                    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

                    TestbedMonitoring testbedMonitoring = (TestbedMonitoring) jaxbUnmarshaller.unmarshal(new StringReader(response));

                    if (testbedMonitoring.getOil() != null) {
                        sp.addOil(testbedMonitoring.getOil().getValue());
                    }
                    if (testbedMonitoring.getCoal() != null) {
                        sp.addCoal(testbedMonitoring.getCoal().getValue());
                    }
                    if (location.equals("fr-inria")) {
                        if (testbedMonitoring.getGaz() != null) {
                            sp.addGaz(testbedMonitoring.getGaz().getValue());
                        }
                        if (testbedMonitoring.getHydraulic() != null) {
                            sp.addHydro(testbedMonitoring.getHydraulic().getValue());
                        }
                    }
                    if (location.equals("uk-epcc")) {
                        if (testbedMonitoring.getCcgt() != null) {
                            sp.addGaz(testbedMonitoring.getCcgt().getValue());
                        }
                        if (testbedMonitoring.getOcgt() != null) {
                            sp.addGaz(testbedMonitoring.getOcgt().getValue());
                        }
                        if (testbedMonitoring.getPumpedStorage() != null) {
                            sp.addHydro(testbedMonitoring.getPumpedStorage().getValue());
                        }
                        if (testbedMonitoring.getNpsHydro() != null) {
                            sp.addHydro(testbedMonitoring.getNpsHydro().getValue());
                        }
                    }
                    if (testbedMonitoring.getNuclear() != null) {
                        sp.addNuclear(testbedMonitoring.getNuclear().getValue());
                    }
                    if (testbedMonitoring.getWind() != null) {
                        sp.addWind(testbedMonitoring.getWind().getValue());
                    }
                    if (testbedMonitoring.getSolar() != null) {
                        sp.addSolar(testbedMonitoring.getSolar().getValue());
                    }
                    if (testbedMonitoring.getWater() != null) {
                        sp.addHydro(testbedMonitoring.getWater().getValue());
                    }
                }

            } catch (JAXBException ex) {
                Logger.getLogger(SchedulerManager.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
        return sp;

    }

    public Double getCurrentEnergyMixEmissions(String location) {

        try {
            if (location.equals("de-hlrs")) {
                //System.out.println("energy mix at " + location + " is 503.00");

                return 503.00 / 1000;

                /* }
                 if (location.equals("fr-inria")) {
                 return 100.00 / 1000;
                 }
                 if (location.equals("uk-epcc")) {
                 return 400.00 / 1000;*/
            } else {

                //schedulerService.addFilter(new LoggingFilter(Logger.getLogger(SchedulerManager.class.getName())));
                schedulerService.getRequestBuilder().header("x-bonfire-asserted-groups", "eco2clouds");
                String response = schedulerService.path("testbeds/" + location + "/monitoring").accept(MediaType.WILDCARD).get(String.class);

                JAXBContext jaxbContext = JAXBContext.newInstance(TestbedMonitoring.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

                TestbedMonitoring testbedMonitoring = (TestbedMonitoring) jaxbUnmarshaller.unmarshal(new StringReader(response));

                double result = 0;
                if (testbedMonitoring.getOil() != null) {
                    result = testbedMonitoring.getOil().getValue() * 700;
                }
                if (testbedMonitoring.getCoal() != null) {
                    result = result + testbedMonitoring.getCoal().getValue() * 900;
                }
                if (location.equals("fr-inria")) {
                    if (testbedMonitoring.getGaz() != null) {
                        result = result + testbedMonitoring.getGaz().getValue() * 450;
                    }
                    if (testbedMonitoring.getHydraulic() != null) {
                        result = result + testbedMonitoring.getHydraulic().getValue() * 120;
                    }
                }
                if (location.equals("uk-epcc")) {
                    if (testbedMonitoring.getCcgt() != null) {
                        result = result + testbedMonitoring.getCcgt().getValue() * 450;
                    }
                    if (testbedMonitoring.getOcgt() != null) {
                        result = result + testbedMonitoring.getOcgt().getValue() * 450;
                    }
                    if (testbedMonitoring.getPumpedStorage() != null) {
                        result = result + testbedMonitoring.getPumpedStorage().getValue() * 45;
                    }
                    if (testbedMonitoring.getNpsHydro() != null) {
                        result = result + testbedMonitoring.getNpsHydro().getValue() * 120;
                    }
                }
                if (testbedMonitoring.getNuclear() != null) {
                    result = result + testbedMonitoring.getNuclear().getValue() * 65;
                }
                if (testbedMonitoring.getWind() != null) {
                    result = result + testbedMonitoring.getWind().getValue() * 65;
                }
                if (testbedMonitoring.getSolar() != null) {
                    result = result + testbedMonitoring.getSolar().getValue() * 120;
                }
                if (testbedMonitoring.getWater() != null) {
                    result = result + testbedMonitoring.getWater().getValue() * 120;
                }
                //System.out.println("energy mix at " + location + " is " + result);
                return result / 100 / 1000; //divided by 100 becaause they return percentage divided by 1000 beacuse is gr/Kwh
            }

        } catch (JAXBException ex) {
            Logger.getLogger(SchedulerManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public List<VM> getExperimentVMs(int id) {
        try {
            //schedulerService.addFilter(new LoggingFilter(Logger.getLogger(SchedulerManager.class.getName())));
            schedulerService.getRequestBuilder().header("x-bonfire-asserted-groups", "eco2clouds");
            String response = schedulerService.path("experiments/" + id + "/computes").accept(MediaType.WILDCARD).get(String.class);
            //System.out.println(response);
            JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Collection collection = (Collection) jaxbUnmarshaller.unmarshal(new StringReader(response));

            return collection.getItems().getvMs();

        } catch (JAXBException ex) {
            Logger.getLogger(SchedulerManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public ExperimentReport getExperimentReport(int id) {
        try {
            schedulerService.addFilter(new LoggingFilter(Logger.getLogger(SchedulerManager.class.getName())));
            schedulerService.getRequestBuilder().header("x-bonfire-asserted-groups", "eco2clouds");
            String response = schedulerService.path("experiments/" + id + "/report").accept(MediaType.WILDCARD).get(String.class);

            JAXBContext jaxbContext = JAXBContext.newInstance(ExperimentReport.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (ExperimentReport) jaxbUnmarshaller.unmarshal(new StringReader(response));
        } catch (JAXBException ex) {
            Logger.getLogger(SchedulerManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public List<Host> getHosts(String location) {

        try {
            //schedulerService.addFilter(new LoggingFilter(Logger.getLogger(SchedulerManager.class.getName())));

            schedulerService.getRequestBuilder().header("x-bonfire-asserted-groups", "eco2clouds");
            String response = schedulerService.path("testbeds/" + location + "/hosts").accept(MediaType.WILDCARD).get(String.class);

            JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            Collection c = (Collection) jaxbUnmarshaller.unmarshal(new StringReader(response));

            List<Host> hosts = c.getItems().getHosts();

            LinkedList<Host> runningHosts = new LinkedList<Host>();

            for (Host host : hosts) {
                if (host.getState() == 1 || host.getState() == 2) {
                    runningHosts.add(host);
                }
            }

            return runningHosts;
        } catch (JAXBException ex) {
            Logger.getLogger(SchedulerManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public Host getHost(String location, String hostname) {

        try {
            //schedulerService.addFilter(new LoggingFilter(Logger.getLogger(SchedulerManager.class.getName())));

            schedulerService.getRequestBuilder().header("x-bonfire-asserted-groups", "eco2clouds");
            String response = schedulerService.path("testbeds/" + location + "/hosts/" + hostname).accept(MediaType.WILDCARD).get(String.class);

            JAXBContext jaxbContext = JAXBContext.newInstance(Host.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            return (Host) jaxbUnmarshaller.unmarshal(new StringReader(response));

        } catch (JAXBException ex) {
            Logger.getLogger(SchedulerManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public List<Testbed> getSites() {
        if (this.sites == null) {
            try {
                //schedulerService.addFilter(new LoggingFilter(Logger.getLogger(SchedulerManager.class.getName())));

                schedulerService.getRequestBuilder().header("x-bonfire-asserted-groups", "eco2clouds");
                String response = schedulerService.path("testbeds").accept(MediaType.WILDCARD).get(String.class);
                //System.out.println("CALL" + response);
                JAXBContext jaxbContext = JAXBContext.newInstance(Collection.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

                Collection collection = (Collection) jaxbUnmarshaller.unmarshal(new StringReader(response));

                List<Testbed> testbeds = collection.getItems().getTestbeds();

                Testbed tbtoremove = null;
                for (Testbed tb : testbeds) {
                    if (tb.getName() == null || tb.getName().equals("")) {
                        tbtoremove = tb;
                    }
                }

                if (tbtoremove != null) {
                    testbeds.remove(tbtoremove);
                }

                return testbeds;

            } catch (JAXBException ex) {
                Logger.getLogger(SchedulerManager.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        } else {
            return this.sites;
        }
    }

    /**
     * needed for authentication *
     */
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

        SchedulerManager scheduler = new SchedulerManager("https://129.69.19.70/scheduler", "/etc/e2c-portal/keystore.jks", "xxx");
        //SchedulerManager scheduler = new SchedulerManager("https://scheduler.integration.e2c.bonfire.grid5000.fr/scheduler", "/etc/e2c-portal/keystore.jks", "xxxx");
        //scheduler.getCurrentEnergyMixEmissions("fr-inria");
        //scheduler.getCpuLoadLocation("uk-epcc");
        //List<Experiment> exp = scheduler.getExperiments();
        HashMap<String, Double> cpu = scheduler.getCpuLoadLocation("uk-epcc");
        for (String s: cpu.keySet()) {
            System.out.println(s + " = " + cpu.get(s));
        }

    }
}
