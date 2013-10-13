package eu.cloudtm.autonomicManager.RESTServer.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.cloudtm.autonomicManager.AutonomicManager;
import eu.cloudtm.autonomicManager.ControllerLogger;
import eu.cloudtm.autonomicManager.commons.AtomicBooleanSerializer;
import eu.cloudtm.autonomicManager.commons.Forecaster;
import eu.cloudtm.autonomicManager.commons.dto.StatusDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.concurrent.atomic.AtomicBoolean;


@Singleton
@Path("/log")
public class LogResource extends AbstractResource {

   private static Log log = LogFactory.getLog(LogResource.class);

   @Inject
   private AutonomicManager autonomicManager;

   @GET
   @Produces("application/json")
   public Response getLog() {

      log.debug("Received request for all logs");

      Gson gson = new Gson();
      String result = gson.toJson( ControllerLogger.getAllLogs() );

      Response.ResponseBuilder builder = Response.ok(result);
      return makeCORS(builder);
   }

   @Path("/{id}")
   @GET
   @Produces("application/json")
   public Response getLog(@DefaultValue("0") @PathParam("id") Integer id) {

      log.debug("Received request for logs starting from id=" + id);

      Gson gson = new Gson();
      String result = gson.toJson( ControllerLogger.getLogsStartingFrom(id) );

      Response.ResponseBuilder builder = Response.ok(result);
      return makeCORS(builder);
   }


}
