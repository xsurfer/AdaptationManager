package eu.cloudtm.RESTcontroller.resources;

import com.sun.jersey.spi.resource.Singleton;
import eu.cloudtm.model.ScaleInfo;
import eu.cloudtm.model.TuningMethod;
import eu.cloudtm.model.TuningType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

// The Java class will be hosted at the URI path "/myresource"
@Singleton
@Path("/scale")
public class Scale {

    private static Log log = LogFactory.getLog(Scale.class);

    int GETreq = 0;
    int PUTreq = 0;

    ScaleInfo statusInfoBean = new ScaleInfo();

    @GET
    @Produces("application/json")
    public ScaleInfo getStatus() {
        GETreq++;
        log.info("GETreq: " + GETreq);
        return statusInfoBean;
    }

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

        statusInfoBean.type = type;
        if(type.equals(TuningType.AUTO) && method.equals(TuningMethod.NONE))
            throw new WebApplicationException(Response.Status.BAD_REQUEST);

        statusInfoBean.method = method;
        statusInfoBean.small = small;
        statusInfoBean.medium = medium;
        statusInfoBean.large = large;
    }
}