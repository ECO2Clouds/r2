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
package eu.eco2clouds.portal.service.data;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import eu.eco2clouds.ac.Configuration;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.bind.JAXB;

/**
 *
 *  
 */
public class NotificationSender {

    int id = 0;
    String keystoreFilename;
    String keystorePwd;

    WebResource notificationService;

    public NotificationSender() {
        try {
            this.keystoreFilename = Configuration.getProperties().getProperty(Configuration.KEYSTORE);
            this.keystorePwd = Configuration.getProperties().getProperty(Configuration.PASSWORD);
            ClientConfig config = new DefaultClientConfig();

            //config.getProperties().put(com.sun.jersey.client.urlconnection.HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new com.sun.jersey.client.urlconnection.HTTPSProperties(this.getHostnameVerifier(), this.getSSLContext()));
            Client client = Client.create(config);

            //this.notificationService = client.resource("https://portal.eco2clouds.eu/e2c-portal/service/notification");
            this.notificationService = client.resource("http://localhost:8080/portal/service/notification");
        } catch (Exception ex) {
            Logger.getLogger(NotificationSender.class.getName()).log(Level.SEVERE, null, ex);
        }

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

    public void send(String title, String description) {

        Notification notification = new Notification(new Date(), id, title, description, "EXP" + Configuration.getProperties().getProperty(Configuration.EXPERIMENT_NO));

        id++;

        ClientResponse response = this.notificationService.type("application/xml")
                .post(ClientResponse.class, notification);
    }

    public static void main(String args[]) {

        NotificationSenderFactory.getInstance().send("pippo", "pldfdscc fdsffsfsfsdfsdfsdfsdgfldhjsdf jhdfjkh gfdkjsh kljdfsglkjflkjdfsglkjdflkjh gjhlfj kghljk gdlfsfsdfdsffsdfsdfdsfdsfdsfdsfsdfdsfdsuto");
    }

}
