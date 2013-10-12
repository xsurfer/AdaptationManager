package eu.cloudtm.autonomicManager.RESTServer.resources;

import com.google.gson.Gson;
import eu.cloudtm.autonomicManager.commons.TopKeyParam;
import eu.cloudtm.autonomicManager.statistics.StatsManager;
import eu.cloudtm.autonomicManager.statistics.topKeys.TopKeySample;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * // TODO: Document this
 *
 * @author diego
 * @since 4.0
 */
@Singleton
@Path("/topk")
public class WorkloadAnalysisResource extends AbstractResource {
   private final static Log log = LogFactory.getLog(WorkloadAnalysisResource.class);
   @Inject
   private StatsManager statsManager;

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public synchronized Response getTopK() {

      log.info("Requesting top K");
      Gson gson = new Gson();

      TopKeySample s = statsManager.getLastTopKSample().subTopKeySample(TopKeyParam.REMOTE_GET, TopKeyParam.REMOTE_PUT, TopKeyParam.FAILED);
      log.info("TopSample " + s);
      String json = gson.toJson(s);
      log.info("platformConfigurationPredicted: " + json);
      Response.ResponseBuilder builder = Response.ok(json);
      return makeCORS(builder);
   }






}
