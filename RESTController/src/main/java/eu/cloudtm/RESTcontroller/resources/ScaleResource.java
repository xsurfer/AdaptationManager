package eu.cloudtm.RESTcontroller.resources;

import com.sun.jersey.spi.resource.Singleton;
import eu.cloudtm.model.Scale;
import eu.cloudtm.model.State;
import eu.cloudtm.model.utils.TuningMethod;
import eu.cloudtm.model.utils.TuningType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

// The Java class will be hosted at the URI path "/myresource"
@Singleton
@Path("/scale")
public class ScaleResource {

    private static Log log = LogFactory.getLog(ScaleResource.class);

    int PUTreq = 0;

    @PUT
    @Consumes("application/x-www-form-urlencoded")
    public synchronized void setStatus(
            @FormParam("tuningType") TuningType type,
            @DefaultValue("NONE") @FormParam("tuningMethod") TuningMethod method,
            @FormParam("small") int small,
            @FormParam("medium") int medium,
            @FormParam("large") int large
    ) {
        PUTreq++;
        log.info("PUTreq: " + PUTreq);

        log.info("type: " + type);
        log.info("method: " + method);

        log.info("small: " + small);
        log.info("med: " + medium);
        log.info("lar: " + large);

        Scale newScale = new Scale();
        newScale.setType(type);
        if(type.equals(TuningType.AUTO) && method.equals(TuningMethod.NONE)){
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        } else if(type.equals(TuningType.MANUAL)){
            newScale.setMethod(TuningMethod.NONE);
        } else {
            newScale.setMethod(method);
        }

        newScale.setSmall(small);
        newScale.setMedium(medium);
        newScale.setLarge(large);
        State.getInstance().updateScale(newScale);
    }


}