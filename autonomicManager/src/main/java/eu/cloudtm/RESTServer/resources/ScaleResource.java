package eu.cloudtm.RESTServer.resources;

import com.google.gson.Gson;
import com.sun.jersey.spi.resource.Singleton;
import eu.cloudtm.LookupRegister;
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
@Path("/scale")
public class ScaleResource extends AbstractResource {

    private static Log log = LogFactory.getLog(ScaleResource.class);
    private Gson gson = new Gson();
    private Controller controller = LookupRegister.getController();


    @PUT
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public synchronized Response setScale(
            @FormParam("scale_tuning") TuningType tuning,
            @DefaultValue("NONE") @FormParam("scale_forecasting") Forecaster forecaster,
            @DefaultValue("-1") @FormParam("scale_size") int size,
            @FormParam("instance_type") InstanceConfigurations instanceType
    ) {
        if( tuning==null || ( tuning.equals(TuningType.SELF) && forecaster.equals(Forecaster.NONE) ) ){
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }


        if(tuning.equals(TuningType.MANUAL)){
            forecaster = Forecaster.NONE;
        }

        if(tuning.equals(TuningType.MANUAL)) {
            if(!(size>=0 && instanceType!=null))
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
        } else if(tuning.equals(TuningType.SELF)) {
            // TODO: per ora metto un random
            Random rnd = new Random();
            size = rnd.nextInt(10);
            instanceType = InstanceConfigurations.MEDIUM;
        }

        /** UPDATING STATE **/

        Scale newScale = new Scale(tuning,forecaster,size,instanceType);
        controller.tuneScale(newScale);

        //PlatformState.getInstance().updateScale(newScale);

        String json = gson.toJson(controller.getStateClone().getScaleClone());
        Response.ResponseBuilder builder = Response.ok(json);
        return makeCORS(builder);
    }

}