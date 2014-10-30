package eu.eco2clouds.scheduler.rest;

import static eu.eco2clouds.scheduler.SchedulerDictionary.CONTENT_TYPE_ECO2CLOUDS_JSON;
import static eu.eco2clouds.scheduler.SchedulerDictionary.CONTENT_TYPE_ECO2CLOUDS_XML;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import eu.eco2clouds.scheduler.designtime.FileOutput;
import eu.eco2clouds.scheduler.designtime.InitialDeployment;
//import eu.eco2clouds.scheduler.eco2cloudsexperimentation.Experimentation;
import eu.eco2clouds.scheduler.runtime.ExperimentLiveMonitoring;

/**
 * 
 * Copyright 2014 ATOS SPAIN S.A. 
 * 
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@Path("/")
public class RestScheduler extends RestService {
	private static Logger logger = Logger.getLogger(RestScheduler.class);

	@GET
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public String listRoot() {
		logger.info("GET PATH /");
		return getListRoot();
	}
	
	@Path("/eco2clouds-experimentation/{arg}/")
	@GET
	public String quartz(@PathParam("arg") String arg)
	{
		if (arg.equals("stop"))
		{
			
//			Experimentation.stopQuartzJob();
		}
		
		if (arg.equals("start"))
		{
	//		Experimentation.startQuartzJob();
		}
		
		return arg;
	}
	
	@Path("/experiment-monitoring/{user}/{group}/{id}")
	@GET
	public String experimentMonitoring(@PathParam("user") String user, @PathParam("group") String group,
			@PathParam("id") String experimentId) throws JsonParseException, JsonMappingException, IOException
	{
		FileOutput.outputToFile("experimentMonitoring\n");
		ExperimentLiveMonitoring.experimentLiveMonitoring(user, group, experimentId);
		
		return null;
	}
	
	@Path("/soo/{mode}")
	@GET
	public String changeSOOConfiguration(@PathParam("mode") String mode)
	{
		InitialDeployment.changeSingleObjectiveOptimization(mode);
		return mode;
	}
	
	@Path("/boo/{mode}")
	@GET
	public String changeBiObjectiveBulkDeploymentMode(@PathParam("mode") String mode)
	{
		InitialDeployment.changeBiObjectiveMode(mode);
		return mode;
	}
	
	@POST
	@Path("/experiments/")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_JSON)
	@Consumes(CONTENT_TYPE_ECO2CLOUDS_JSON)
	public Response submitApplicationProfile(@Context HttpHeaders hh, String payload) {
		logger.info("POST PATH /experiments");
		logger.info("PAYLOAD: " + payload);
		
		parsingHeaders(hh);
		
		return processApplicationProfile(payload);
	}

	@Path("/experiments")
	@GET
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getExperiments() throws JAXBException {
		return getExperimentsFromAccountingService();
	}

	@GET
	@Path("/experiments/{id}")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getExperiment(@PathParam("id") String id) throws NumberFormatException, JAXBException {
		return getExperimentFromAccountingService(Integer.parseInt(id));
	}
	
	@GET
	@Path("/experiments/{id}/vms")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getVMsOfExperiment(@PathParam("id") String id) throws NumberFormatException, JAXBException {
		return getVMsOfExperiment(Integer.parseInt(id));
	}
	
	@GET
	@Path("/experiments/{id}/computes")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getComputesOfExperiment(@PathParam("id") String id) throws NumberFormatException, JAXBException {
		return getVMsOfExperiment(Integer.parseInt(id));
	}
	
	@GET
	@Path("/experiments/{id}/report")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getExperimentReport(@Context HttpHeaders hh, @PathParam("id") String id) throws NumberFormatException, JAXBException {
		parsingHeaders(hh);
		return getExperimentReport(Integer.parseInt(id));
	}
	
	@GET
	@Path("/locations/{location}/computes/{id}")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getCompute(@Context HttpHeaders hh, @PathParam("location") String location, @PathParam("id") String id) throws NumberFormatException, JAXBException {
		parsingHeaders(hh);
		return getComputeOfLocation(location, id);
	}
	
	@GET
	@Path("/locations/{location}/computes/{id}/monitoring")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getComputeMonitoring(@Context HttpHeaders hh, @PathParam("location") String location, @PathParam("id") String id) throws NumberFormatException, JAXBException {
		parsingHeaders(hh);
		return getComputeMonitoringFromAC(location, id);
	}
	
	@GET
	@Path("/locations/{location}/computes/{id}/report")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getComputeReport(@Context HttpHeaders hh, @PathParam("location") String location, @PathParam("id") String id) throws NumberFormatException, JAXBException {
		parsingHeaders(hh);
		return getComputeReportFromAC(location, id);
	}
	
	@PUT
	@Path("/locations/{location}/computes/{id}")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	@Consumes(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response putCompute(@Context HttpHeaders hh, @PathParam("location") String location, @PathParam("id") String id, String payload) throws NumberFormatException, JAXBException {
		parsingHeaders(hh);
		return changeStateOfCompute(location, id, payload);
	}
	
	@GET
	@Path("/experiments/{id}/total_co2")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getTotalCo2FromExperiment(@PathParam("id") String id) throws NumberFormatException, JAXBException {
		return getTotalCo2FromExperiment(Integer.parseInt(id));
	}
	
	@GET
	@Path("/testbeds")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getTestbeds() throws JAXBException {
		return getTestbedsFromAccountingService();
	}
	
	@GET
	@Path("/testbeds/{location}")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getTestbed(@PathParam("location") String location) throws JAXBException {
		return getTestbedFromAccountingService(location);
	}
	
	@GET
	@Path("/testbeds/{location}/monitoring")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getTestbedMonitoring(@PathParam("location") String location) throws JAXBException {
		return getTestbedMonitoringFromAccountingService(location);
	}
	
	@GET
	@Path("/testbeds/{location}/hosts")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getHostsOfTestbed(@PathParam("location") String location) throws JAXBException {
		return getHostsOfTestbedsFromAccounting(location);
	}
	
	@GET
	@Path("/testbeds/{location}/hosts/{hostname}")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getHostOfTestbed(@PathParam("location") String location, @PathParam("hostname") String hostname) throws JAXBException {
		return getHostOfTestbedFromAccountin(location, hostname);
	}
	
	@GET
	@Path("/testbeds/{location}/hosts/{hostname}/monitoring")
	@Produces(CONTENT_TYPE_ECO2CLOUDS_XML)
	public Response getHostMonitoring(@PathParam("location") String location, 
			@PathParam("hostname") String hostname) throws JAXBException {
		return getHostMonitoringFromAccountingService(location, hostname);
	}
}