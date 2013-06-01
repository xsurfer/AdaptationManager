package eu.cloudtm.RESTcontroller.resources;

import com.google.gson.Gson;
import com.sun.jersey.spi.resource.Singleton;
import eu.cloudtm.model.State;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;


@Singleton
@Path("/status")
public class StatusResource {

    private static Log log = LogFactory.getLog(StatusResource.class);
    Gson gson = new Gson();

    int GETreq = 0;

    @GET
    @Produces("application/json")
    public String getStatus() {
        String json = gson.toJson(State.getInstance());
        log.debug(json);
        return json;
    }

    @GET
    @Path("/replication")
    @Produces("application/json")
    public String getReplication() {
        String json = gson.toJson(State.getInstance().getReplication());
        return json;
    }

    @GET
    @Path("/placement")
    @Produces("application/json")
    public String getDataPlacement() {
        String json = gson.toJson(State.getInstance().getPlacement());
        return json;
    }

    @GET
    @Path("/scale")
    @Produces("application/json")
    public String getScale() {
        GETreq++;
        log.info("GETreq: " + GETreq);

        String json = gson.toJson(State.getInstance().getScale());

        return json;
    }

}