package eu.cloudtm.RESTcontroller.resources;

import com.google.gson.Gson;
import com.sun.jersey.spi.resource.Singleton;
import eu.cloudtm.RESTcontroller.utils.Helper;
import eu.cloudtm.model.Scale;
import eu.cloudtm.model.State;
import eu.cloudtm.model.utils.Forecasters;
import eu.cloudtm.model.utils.InstanceConfigurations;
import eu.cloudtm.model.utils.TuningType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Singleton
@Path("/scale")
public class ScaleResource {

    private static Log log = LogFactory.getLog(ScaleResource.class);

    Gson gson = new Gson();

    @PUT
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public synchronized Response setScale(
            @FormParam("tuningType") TuningType tuning,
            @DefaultValue("NONE") @FormParam("tuningMethod") Forecasters forecaster,
            @DefaultValue("-1") @FormParam("size") int size,
            @FormParam("instance_type") InstanceConfigurations instanceType
    ) {
        if(tuning.equals(TuningType.SELF) && forecaster.equals(Forecasters.NONE)){
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        log.info("tuning: " + tuning);
        log.info("forecaster: " + forecaster);

        log.info("small: " + size);
        log.info("instanceType: " + instanceType);

        Scale newScale = new Scale();
        newScale.setTuning(tuning);

        if(tuning.equals(TuningType.MANUAL)){
            newScale.setForecaster(Forecasters.NONE);
        } else {
            newScale.setForecaster(forecaster);
        }

        if(size>=0) newScale.setSize(size);
        else throw new WebApplicationException(Response.Status.BAD_REQUEST);
        
        if(instanceType==null)
        	throw new WebApplicationException(Response.Status.BAD_REQUEST);
        newScale.setInstanceType(instanceType);

        /** UPDATING STATE **/
        State.getInstance().updateScale(newScale);

        String json = gson.toJson(State.getInstance().getScale());
        Response.ResponseBuilder builder = Response.ok(json);
        return makeCORS(builder);

    }

    private String _corsHeaders;

    private Response makeCORS(Response.ResponseBuilder req, String returnMethod) {
        Response.ResponseBuilder rb = req.header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");

        if (!"".equals(returnMethod)) {
            rb.header("Access-Control-Allow-Headers", returnMethod);
        }

        return rb.build();
    }

    private Response makeCORS(Response.ResponseBuilder req) {
        return makeCORS(req, _corsHeaders);
    }

}