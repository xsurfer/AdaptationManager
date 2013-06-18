package eu.cloudtm.RESTServer.resources;

import com.google.gson.Gson;
import com.sun.jersey.spi.resource.Singleton;
import eu.cloudtm.LookupRegister;
import eu.cloudtm.StatsManager;
import eu.cloudtm.common.dto.WhatIfCustomParamDTO;
import eu.cloudtm.common.dto.WhatIfDTO;
import eu.cloudtm.controller.Controller;
import eu.cloudtm.controller.model.KPI;
import eu.cloudtm.controller.oracles.AbstractOracle;
import eu.cloudtm.stats.Sample;
import eu.cloudtm.wpm.parser.ResourceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Singleton
@Path("/whatif")
public class WhatIfResource extends AbstractResource {

    private static Log log = LogFactory.getLog(WhatIfResource.class);
    private Gson gson = new Gson();
    private StatsManager statsManager = LookupRegister.getStatsManager();
    private Controller controller = LookupRegister.getController();

    private Sample lastSample;

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


    @GET
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public synchronized Response whatIf(){

        if(lastSample==null)
            lastSample = statsManager.getLastSample();

        double dumb = 0;
        WhatIfCustomParamDTO customParam = new WhatIfCustomParamDTO();
        //customParam.setACF(dumb);
        //customParam.setCommitBroadcastWallClockTime(dumb);
        //customParam.setGetReadOnlyTx(dumb);
        //customParam.setGetWriteTx(dumb);
        //customParam.setLocalReadOnlyTxLocalServiceTime(dumb);
        //customParam.setLocalUpdateTxLocalServiceTime(dumb);
        //customParam.setPrepareCommandBytes(dumb);
        //customParam.setSuxNumPuts(dumb);
        //customParam.setRemoteGetLatency(dumb);
        //customParam.setRetryWritePercentage(dumb);
        //customParam.setRTT(dumb);

        Set<KPI> result = null;
        for( AbstractOracle oracle : controller.getOracles() ){
            result = oracle.whatIf(lastSample, customParam);
        }
        lastSample = null;

        WhatIfDTO whatIfResult = new WhatIfDTO();
        for(KPI kpi:result){
            whatIfResult.addThroughputPoint(kpi.getPlatformConfiguration().platformSize(),kpi.getThroughput());
            whatIfResult.addResponseTimePoint(kpi.getPlatformConfiguration().platformSize(),kpi.getRtt());
            whatIfResult.addAbortRatePoint(kpi.getPlatformConfiguration().platformSize(),kpi.getAbortProbability());
        }
        StringBuffer json = new StringBuffer();
        json.append( gson.toJson(whatIfResult) );

        log.info(json);
        Response.ResponseBuilder builder = Response.ok( json.toString() );
        return makeCORS(builder);
    }

    @GET @Path("/values")
    @Produces("application/json")
    public synchronized Response updateValuesFromSystem() {
        lastSample = statsManager.getLastSample();

        StringBuffer json = new StringBuffer();

        json.append("{ ");
        for( Map.Entry<String,ResourceType> param : sampledParams.entrySet() ){
            if (json.length() > 3) {
                json.append(" , ");
            }
            json.append( getJSON(param.getKey(), String.valueOf( statsManager.getAvgAttribute(param.getKey(), lastSample, param.getValue()) ) ) );
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