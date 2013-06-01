package eu.cloudtm.RESTcontroller.resources;

import com.google.gson.Gson;
import com.sun.jersey.spi.resource.Singleton;
import eu.cloudtm.RESTcontroller.utils.Helper;
import eu.cloudtm.model.Scale;
import eu.cloudtm.model.State;
import eu.cloudtm.model.utils.TuningMethod;
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
            @FormParam("tuningType") TuningType type,
            @DefaultValue("NONE") @FormParam("tuningMethod") TuningMethod method,
            @DefaultValue("-1") @FormParam("small") int small,
            @DefaultValue("-1") @FormParam("medium") int medium,
            @DefaultValue("-1") @FormParam("large") int large
    ) {
        if(type.equals(TuningType.AUTO) && method.equals(TuningMethod.NONE)){
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        log.info("type: " + type);
        log.info("method: " + method);

        log.info("small: " + small);
        log.info("med: " + medium);
        log.info("lar: " + large);

        Scale newScale = new Scale();
        newScale.setType(type);

        if(type.equals(TuningType.MANUAL)){
            newScale.setMethod(TuningMethod.NONE);
        } else {
            newScale.setMethod(method);
        }

        if(small>=0) newScale.setSmall(small);
        if(medium>=0) newScale.setMedium(medium);
        if(large>=0) newScale.setLarge(large);

        State.getInstance().updateScale(newScale);

        String json = gson.toJson(State.getInstance().getScale());
        return Helper.createResponse(json);
    }
}