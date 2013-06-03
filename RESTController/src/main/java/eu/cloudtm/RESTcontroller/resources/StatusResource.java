package eu.cloudtm.RESTcontroller.resources;

import com.google.gson.Gson;
import com.sun.jersey.spi.resource.Singleton;
import eu.cloudtm.RESTcontroller.utils.Helper;
import eu.cloudtm.model.State;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;


@Singleton
@Path("/status")
public class StatusResource {

    private static Log log = LogFactory.getLog(StatusResource.class);

    Gson gson = new Gson();

    @GET
    @Produces("application/json")
    public Response getStatus() {
        String json = gson.toJson(State.getInstance());
	log.info("new status req");
        return Helper.createResponse(json);
    }

    @GET @Path("/replication/degree")
    @Produces("application/json")
    public Response getReplicationDegree() {
        String json = gson.toJson(State.getInstance().getReplicationDegree());
        return Helper.createResponse(json);
    }

    @GET @Path("/replication/protocol")
    @Produces("application/json")
    public Response getReplicationProtocol() {
        String json = gson.toJson(State.getInstance().getReplicationProtocol());
        return Helper.createResponse(json);
    }

    @GET @Path("/placement")
    @Produces("application/json")
    public Response getDataPlacement() {
        String json = gson.toJson(State.getInstance().getDataPlacement());
        return Helper.createResponse(json);
    }

    @GET @Path("/scale")
    @Produces("application/json")
    public Response getScale() {
        String json = gson.toJson(State.getInstance().getScale());
        return Helper.createResponse(json);
    }

}
