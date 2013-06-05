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
import java.util.Random;

@Singleton
@Path("/scale")
public class ScaleResource extends AbstractResource {

    private static Log log = LogFactory.getLog(ScaleResource.class);

    Gson gson = new Gson();

    @PUT
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public synchronized Response setScale(
            @FormParam("scale_tuning") TuningType tuning,
            @DefaultValue("NONE") @FormParam("scale_forecasting") Forecasters forecaster,
            @DefaultValue("-1") @FormParam("scale_size") int size,
            @FormParam("instance_type") InstanceConfigurations instanceType
    ) {
        if( tuning==null || ( tuning.equals(TuningType.SELF) && forecaster.equals(Forecasters.NONE) ) ){
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        Scale newScale = new Scale();
        newScale.setTuning(tuning);

        if(tuning.equals(TuningType.MANUAL)){
            newScale.setForecaster(Forecasters.NONE);
        } else {
            newScale.setForecaster(forecaster);
        }

        if(tuning.equals(TuningType.MANUAL)) {
            if(size>=0 && instanceType!=null){
                newScale.setSize(size);
                newScale.setInstanceType(instanceType);
            } else
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
        } else if(tuning.equals(TuningType.SELF)) {
            // TODO: per ora metto un random
            Random rnd = new Random();
            newScale.setSize(rnd.nextInt(10));
            newScale.setInstanceType(InstanceConfigurations.MEDIUM);
        }

        /** UPDATING STATE **/
        State.getInstance().updateScale(newScale);

        String json = gson.toJson(State.getInstance().getScale());
        Response.ResponseBuilder builder = Response.ok(json);
        return makeCORS(builder);
    }

}