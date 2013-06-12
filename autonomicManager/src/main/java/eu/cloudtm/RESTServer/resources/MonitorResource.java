package eu.cloudtm.RESTServer.resources;

import com.google.gson.Gson;
import com.sun.jersey.spi.resource.Singleton;
import eu.cloudtm.LookupRegister;
import eu.cloudtm.StatsManager;
import eu.cloudtm.controller.Controller;
import eu.cloudtm.stats.StatisticDTO;
import eu.cloudtm.wpm.parser.ResourceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Singleton
@Path("/monitor")
public class MonitorResource extends AbstractResource {

    private static Log log = LogFactory.getLog(MonitorResource.class);
    private Gson gson = new Gson();
    private StatsManager statsManager = LookupRegister.getStatsManager();
    private Controller controller = LookupRegister.getController();

    private Map<String,String> param2key = new HashMap<String,String>(){{
        put("throughput",                           "Throughput");
        put("nodes",                                "NumNodes");
        put("writePercentage",                      "RetryWritePercentage");

    }};

    private Map<String,ResourceType> monitoredParams = new HashMap<String,ResourceType>(){{
        put("Throughput",                           ResourceType.JMX);
        put("NumNodes",                             ResourceType.JMX);
        put("RetryWritePercentage",                 ResourceType.JMX);
    }};


    @GET @Path("/{param}")
    @Produces("application/json")
    public synchronized Response getAllData(@PathParam("param") String param) {

        String paramKey = param2key.get(param);
        if( paramKey==null || paramKey.length()<=0 )
            throw new WebApplicationException(Response.Status.BAD_REQUEST);

        ResourceType resourceType = monitoredParams.get(paramKey);
        StatisticDTO statDTO = statsManager.getAllAvgStatistic(paramKey, resourceType);

        String json = gson.toJson(statDTO);
        Response.ResponseBuilder builder = Response.ok(json);
        return makeCORS(builder);
    }

    @GET @Path("/{param}/last")
    @Produces("application/json")
    public synchronized Response getLastData(@PathParam("param") String param) {

        String paramKey = param2key.get(param);
        if( paramKey==null || paramKey.length()<=0 )
            throw new WebApplicationException(Response.Status.BAD_REQUEST);

        ResourceType resourceType = monitoredParams.get(paramKey);

        StatisticDTO statDTO = statsManager.getLastAvgStatistic(paramKey, resourceType);

        String json = gson.toJson(statDTO);
        Response.ResponseBuilder builder = Response.ok(json);
        return makeCORS(builder);
    }

}