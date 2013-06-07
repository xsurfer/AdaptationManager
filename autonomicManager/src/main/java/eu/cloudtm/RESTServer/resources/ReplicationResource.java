package eu.cloudtm.RESTServer.resources;

import com.google.gson.Gson;
import com.sun.jersey.spi.resource.Singleton;
import eu.cloudtm.LookupRegister;
import eu.cloudtm.controller.Controller;
import eu.cloudtm.controller.model.ReplicationDegree;
import eu.cloudtm.controller.model.ReplicationProtocol;
import eu.cloudtm.controller.model.utils.Forecaster;
import eu.cloudtm.controller.model.utils.ReplicationProtocols;
import eu.cloudtm.controller.model.utils.TuningType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

// The Java class will be hosted at the URI path "/myresource"
@Singleton
@Path("/replication")
public class ReplicationResource extends AbstractResource {

    private static Log log = LogFactory.getLog(ReplicationResource.class);
    private Controller controller = LookupRegister.getController();
    Gson gson = new Gson();

    @PUT
    @Path("/degree")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public synchronized Response setDegree(
            @FormParam("rep_degree_tuning") TuningType type,
            @DefaultValue("NONE") @FormParam("rep_degree_forecasting") Forecaster forecaster,
            @DefaultValue("0") @FormParam("rep_degree_size") int degree
    ) {
        if(type==null)
            throw new WebApplicationException(Response.Status.BAD_REQUEST);

        if(type.equals(TuningType.SELF) && forecaster.equals(Forecaster.NONE)){
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        log.info("type: " + type);
        log.info("forecaster: " + forecaster);
        log.info("degree: " + degree);

        if( degree<=0 ){
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        if(type.equals(TuningType.MANUAL)){
            forecaster = Forecaster.NONE;
        }

        ReplicationDegree repDegreeConf = new ReplicationDegree(type, forecaster, degree);
        controller.tuneReplicationDegree(repDegreeConf);


        String json = gson.toJson(controller.getStateClone().getReplicationDegreeClone());
        Response.ResponseBuilder builder = Response.ok(json);
        return makeCORS(builder);
    }

    @PUT
    @Path("/protocol")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public synchronized Response setProtocol(
            @FormParam("rep_protocol_tuning") TuningType type,
            @DefaultValue("NONE") @FormParam("rep_protocol_forecasting") Forecaster forecaster,
            @FormParam("rep_protocol") ReplicationProtocols protocol
    ) {
        if(type==null || protocol==null)
            throw new WebApplicationException(Response.Status.BAD_REQUEST);

        if(type.equals(TuningType.SELF) && forecaster.equals(Forecaster.NONE)){
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        log.info("type: " + type);
        log.info("forecaster: " + forecaster);
        log.info("protocol: " + protocol);

        if(type.equals(TuningType.MANUAL)){
            forecaster = Forecaster.NONE;
        }

        ReplicationProtocol repProtocolConf = new ReplicationProtocol(type, forecaster, protocol);
        controller.tuneReplicationProtocol(repProtocolConf);

        String json = gson.toJson(controller.getStateClone().getReplicationProtocolClone());
        Response.ResponseBuilder builder = Response.ok(json);
        return makeCORS(builder);
    }


}