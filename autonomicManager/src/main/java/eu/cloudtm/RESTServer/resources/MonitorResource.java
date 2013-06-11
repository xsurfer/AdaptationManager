package eu.cloudtm.RESTServer.resources;

import com.google.gson.Gson;
import com.sun.jersey.spi.resource.Singleton;
import eu.cloudtm.LookupRegister;
import eu.cloudtm.StatsManager;
import eu.cloudtm.controller.Controller;
import eu.cloudtm.wpm.parser.ResourceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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

    private Map<String,ResourceType> monitoredParams = new HashMap<String,ResourceType>(){{
        put("Throughput",                           ResourceType.JMX);
    }};



    @GET @Path("/throughputs")
    @Produces("application/json")
    public synchronized Response getAllData() {

        String param = "throughput";
        log.info("Creating JSON");

        StringBuffer json = new StringBuffer();
        json.append("{ ");
        json.append(gson.toJson("param") + ":" + gson.toJson(param));
        json.append("," + gson.toJson("da"))
        for( Map.Entry<String,ResourceType> param : monitoredParams.entrySet() ){
            if (json.length() > 3) {
                json.append(" , ");
            }
            json.append( getJSON(param.getKey(), String.valueOf( statsManager.getLastAvgAttribute(param.getKey(), param.getValue()) ) ) );
        }
        json.append(" }");

        log.info(json.toString());

        Response.ResponseBuilder builder = Response.ok(json.toString());

        return makeCORS(builder);
    }




    private String getJSON(String key, String val){
        StringBuffer strBuf = new StringBuffer();

        strBuf.append( gson.toJson( key ) );
        strBuf.append( ":" );
        strBuf.append( gson.toJson( val ) );

        log.info(strBuf);
        return strBuf.toString();
    }

}