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
public class StatusResource {

    private static Log log = LogFactory.getLog(StatusResource.class);

    private Gson gson = new Gson();
    private Controller controller = LookupRegister.getController();


    @GET
    @Produces("application/json")
    public Response getStatus() {
        String json = gson.toJson(controller.getStateClone());
	    log.info("new status req");
        return Helper.createResponse(json);
    }

    @GET @Path("/replication/degree")
    @Produces("application/json")
    public Response getReplicationDegree() {
        String json = gson.toJson(controller.getStateClone().getReplicationDegreeClone());
        return Helper.createResponse(json);
    }

    @GET @Path("/replication/protocol")
    @Produces("application/json")
    public Response getReplicationProtocol() {
        String json = gson.toJson(controller.getStateClone().getReplicationProtocolClone());
        return Helper.createResponse(json);
    }

    @GET @Path("/placement")
    @Produces("application/json")
    public Response getDataPlacement() {
        String json = gson.toJson(controller.getStateClone().getDataPlacementClone());
        return Helper.createResponse(json);
    }

    @GET @Path("/scale")
    @Produces("application/json")
    public Response getScale() {
        String json = gson.toJson(controller.getStateClone().getScaleClone());
        return Helper.createResponse(json);
    }

}
