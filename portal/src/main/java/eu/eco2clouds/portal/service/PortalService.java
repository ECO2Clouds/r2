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
package eu.eco2clouds.portal.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import eu.eco2clouds.portal.service.data.Notification;
import eu.eco2clouds.portal.service.data.NotificationListFactory;
import java.util.Date;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 *  
 */
@Path("/")
public class PortalService {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of PortalService
     */
    public PortalService() {
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        //TODO return proper representation object
        String message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<root xmlns=\"http://portal.eco2clouds.eu/doc/schemas/xml\" href=\"/\">\n"
                + "<version>0.1</version>\n"
                + "<timestamp>" + new Date().getTime() + "</timestamp>\n"
                + "<link rel=\"service/notifications\" href=\"/service/notifications\" type=\"application/xml\"/>\n"
                + "</root>";

        return message;
    }

    @POST
    @Path("/notification/")
    @Consumes(MediaType.APPLICATION_XML)
    public void addNotification(Notification notification) {
        
        NotificationListFactory.getInstance().addNotification(notification);
    }
}