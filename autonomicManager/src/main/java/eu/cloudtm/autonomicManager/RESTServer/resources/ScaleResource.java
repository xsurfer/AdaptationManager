package eu.cloudtm.autonomicManager.RESTServer.resources;

import com.google.gson.Gson;
import eu.cloudtm.commons.Forecaster;
import eu.cloudtm.commons.InstanceConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Singleton;
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

        if(!(size>=0 && instanceType!=null))
            throw new WebApplicationException(Response.Status.BAD_REQUEST);

        log.info("forecaster: " + forecaster);
        log.info("size: " + size);
        log.info("instanceType: " + instanceType);

        if(forecaster.isAutoTuning()) {
            // TODO: per ora metto un random
            Random rnd = new Random();
            size = rnd.nextInt(10);
            instanceType = InstanceConfig.MEDIUM;
        }

        /** UPDATING STATE **/
        //ControllerOld.getInstance().updateScale(size, instanceType, tuning);

        //TODO sistemare qui sotto String json = gson.toJson(ControllerOld.getInstance().getState());

        String json = "{ \"todo\" : \"da impl\" }";
        Response.ResponseBuilder builder = Response.ok(json);
        return makeCORS(builder);
    }

}