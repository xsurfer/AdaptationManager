package eu.cloudtm.RESTServer.resources;

import com.google.gson.Gson;
import com.sun.jersey.spi.resource.Singleton;
import eu.cloudtm.controller.Controller;
import eu.cloudtm.controller.model.Tuning;
import eu.cloudtm.controller.model.utils.Forecaster;
import eu.cloudtm.controller.model.utils.InstanceConfig;
import eu.cloudtm.controller.model.utils.TuningState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Random;

@Singleton
@Path("/scale")
public class ScaleResource extends AbstractResource {

    private static Log log = LogFactory.getLog(ScaleResource.class);
    private Gson gson = new Gson();


    @PUT
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public synchronized Response setScale(
            @DefaultValue("NONE") @FormParam("scale_forecasting") Forecaster forecaster,
            @DefaultValue("-1") @FormParam("scale_size") int size,
            @FormParam("instance_type") InstanceConfig instanceType
    ) {

        Tuning tuning = new Tuning(forecaster);

        if(!(size>=0 && instanceType!=null))
            throw new WebApplicationException(Response.Status.BAD_REQUEST);

        log.info("tuningState: " + tuning.getState());
        log.info("forecaster: " + forecaster);
        log.info("size: " + size);
        log.info("instanceType: " + instanceType);

        if(tuning.getState().equals(TuningState.SELF)) {
            // TODO: per ora metto un random
            Random rnd = new Random();
            size = rnd.nextInt(10);
            instanceType = InstanceConfig.MEDIUM;
        }

        /** UPDATING STATE **/
        Controller.getInstance().updateScale(size, instanceType, tuning);

        String json = gson.toJson(Controller.getInstance().getState());
        Response.ResponseBuilder builder = Response.ok(json);
        return makeCORS(builder);
    }

}