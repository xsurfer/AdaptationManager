package eu.cloudtm.RESTServer.resources;

import com.google.gson.Gson;
import com.sun.jersey.spi.resource.Singleton;
import eu.cloudtm.LookupRegister;
import eu.cloudtm.StatsManager;
import eu.cloudtm.controller.Controller;
import eu.cloudtm.wpm.parser.ResourceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Singleton
@Path("/whatif")
public class WhatIfResource extends AbstractResource {

    private static Log log = LogFactory.getLog(WhatIfResource.class);
    private Gson gson = new Gson();
    private StatsManager statsManager = LookupRegister.getStatsManager();
    private Controller controller = LookupRegister.getController();

    private Map<String,String> evaluatedParams = new HashMap<String,String>(){{
        put("ACF",                                  "-1"); //1
        put("GetWriteTx",                           "-1"); //4
        put("GetReadOnlyTx",                        "-1"); //5
        put("RemoteGetLatency",                     "-1"); //11

    }};

    private Map<String,ResourceType> sampledParams = new HashMap<String,ResourceType>(){{
        put("LocalReadOnlyTxLocalServiceTime",      ResourceType.JMX);  //7   LocalReadOnlyTxLocalServiceTime
        put("LocalUpdateTxLocalServiceTime",        ResourceType.JMX);  //6
        put("RetryWritePercentage",                 ResourceType.JMX);  //2   RetryWritePercentage
        put("SuxNumPuts",                           ResourceType.JMX);  //3   SuxNumPuts
        put("PrepareCommandBytes",                  ResourceType.JMX);  //8   PrepareCommandBytes
        put("RTT",                                  ResourceType.JMX);  //9   AvgSuccessfulPrepareTime
        put("CommitBroadcastWallClockTime",         ResourceType.JMX);  //10  CommitBroadcastWallClockTime

    }};



    @GET @Path("/values")
    @Produces("application/json")
    public synchronized Response updateValuesFromSystem() {
        StringBuffer json = new StringBuffer();
        json.append("{ ");
        for( Map.Entry<String,ResourceType> param : sampledParams.entrySet() ){
            if (json.length() > 3) {
                json.append(" , ");
            }
            json.append( getJSON(param.getKey(), String.valueOf( statsManager.getLastAvgAttribute(param.getKey(), param.getValue()) ) ) );
        }
        json.append(" }");

        Response.ResponseBuilder builder = Response.ok(json.toString());
        return makeCORS(builder);
    }


    private String getJSON(String key, String val){
        StringBuffer strBuf = new StringBuffer();
        strBuf.append( gson.toJson( key ) );
        strBuf.append( ":" );
        strBuf.append( gson.toJson( val ) );
        return strBuf.toString();
    }

}