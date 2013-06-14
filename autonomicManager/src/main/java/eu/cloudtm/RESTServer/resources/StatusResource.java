package eu.cloudtm.RESTServer.resources;

import com.google.gson.Gson;
import com.sun.jersey.spi.resource.Singleton;
import eu.cloudtm.LookupRegister;
import eu.cloudtm.RESTServer.utils.Helper;
import eu.cloudtm.controller.Controller;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;


@Singleton
@Path("/status")
public class StatusResource extends AbstractResource {

    private static Log log = LogFactory.getLog(StatusResource.class);

    private Gson gson = new Gson();
    private Controller controller = LookupRegister.getController();


    @GET
    @Produces("application/json")
    public Response getState() {
        String json = gson.toJson(controller.getState());

        Response.ResponseBuilder builder = Response.ok(json);
        return makeCORS(builder);
    }

    @GET @Path("/replication/degree")
    @Produces("application/json")
    public Response getReplicationDegree() {
        String json = gson.toJson(controller.getStateClone().getReplicationDegreeClone());

        Response.ResponseBuilder builder = Response.ok(json);
        return makeCORS(builder);
    }

    @GET @Path("/replication/protocol")
    @Produces("application/json")
    public Response getReplicationProtocol() {
        String json = gson.toJson(controller.getStateClone().getReplicationProtocolClone());

        Response.ResponseBuilder builder = Response.ok(json);
        return makeCORS(builder);

    }

    @GET @Path("/placement")
    @Produces("application/json")
    public Response getDataPlacement() {
        String json = gson.toJson(controller.getStateClone().getDataPlacementClone());

        Response.ResponseBuilder builder = Response.ok(json);
        return makeCORS(builder);

    }

    @GET @Path("/scale")
    @Produces("application/json")
    public Response getScale() {
        String json = gson.toJson(controller.getStateClone().getScaleClone());

        Response.ResponseBuilder builder = Response.ok(json);
        return makeCORS(builder);
    }

}
