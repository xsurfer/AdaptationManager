package eu.cloudtm.RESTServer.resources;

import com.google.gson.Gson;
import com.sun.jersey.spi.resource.Singleton;
import eu.cloudtm.LookupRegister;
import eu.cloudtm.RESTServer.utils.Helper;
import eu.cloudtm.StatsManager;
import eu.cloudtm.controller.Controller;
import eu.cloudtm.controller.model.Scale;
import eu.cloudtm.controller.model.utils.Forecaster;
import eu.cloudtm.controller.model.utils.InstanceConfigurations;
import eu.cloudtm.controller.model.utils.TuningType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Random;

@Singleton
@Path("/whatif")
public class WhatIfResource extends AbstractResource {

    private static Log log = LogFactory.getLog(WhatIfResource.class);
    private Gson gson = new Gson();
    private StatsManager statsManager = LookupRegister.getStatsManager();
    private Controller controller = LookupRegister.getController();


    @GET @Path("/values")
    @Produces("application/json")
    public synchronized Response updateValuesFromSystem() {

        log.info("req");

        String json = gson.toJson(controller.getStateClone());

        Response.ResponseBuilder builder = Response.ok(json);
        return makeCORS(builder);
    }

}