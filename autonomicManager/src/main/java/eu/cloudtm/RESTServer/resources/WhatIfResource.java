package eu.cloudtm.RESTServer.resources;

import com.google.gson.Gson;
import com.sun.jersey.spi.resource.Singleton;
import eu.cloudtm.StatsManager;
import eu.cloudtm.common.dto.WhatIfCustomParamDTO;
import eu.cloudtm.common.dto.WhatIfDTO;
import eu.cloudtm.controller.Controller;
import eu.cloudtm.controller.IOracle;
import eu.cloudtm.controller.model.ACF;
import eu.cloudtm.controller.model.KPI;
import eu.cloudtm.controller.oracles.AbstractOracle;
import eu.cloudtm.controller.oracles.common.PublishAttributeException;
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

    private Sample lastSample;

    private Map<String,String> evaluatedParams = new HashMap<String,String>(){{
        //put("ACF",                                  "-1"); //1
        //put("GetWriteTx",                           "-1"); //4
        //put("GetReadOnlyTx",                        "-1"); //5
        //put("RemoteGetLatency",                     "-1"); //11

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


    @PUT
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public synchronized Response whatIf(
            @DefaultValue("-1") @FormParam("acf") double acf,
            @DefaultValue("-1") @FormParam("RetryWritePercentage") double retryWritePercentage,
            @DefaultValue("-1") @FormParam("SuxNumPuts") double suxNumPuts,
            @DefaultValue("-1") @FormParam("GetWriteTx") double getWriteTx,
            @DefaultValue("-1") @FormParam("GetReadOnlyTx") double getReadOnlyTx,
            @DefaultValue("-1") @FormParam("LocalUpdateTxLocalServiceTime") double localUpdateTxLocalServiceTime,
            @DefaultValue("-1") @FormParam("LocalReadOnlyTxLocalServiceTime") double localReadOnlyTxLocalServiceTime,
            @DefaultValue("-1") @FormParam("PrepareCommandBytes") double prepareCommandBytes,
            @DefaultValue("-1") @FormParam("RTT") double rtt,
            @DefaultValue("-1") @FormParam("CommitBroadcastWallClockTime") double commitBroadcastWallClockTime,
            @DefaultValue("-1") @FormParam("RemoteGetLatency") double remoteGetLatency
    ){

        if(lastSample==null)
            lastSample = StatsManager.getInstance().getLastSample();

        WhatIfCustomParamDTO customParam = new WhatIfCustomParamDTO();
        customParam.setACF( validateParam(acf) );
        customParam.setCommitBroadcastWallClockTime( validateParam(commitBroadcastWallClockTime) );
        customParam.setGetReadOnlyTx(validateParam(getReadOnlyTx));
        customParam.setGetWriteTx(validateParam(getWriteTx));
        customParam.setLocalReadOnlyTxLocalServiceTime(validateParam(localReadOnlyTxLocalServiceTime));
        customParam.setLocalUpdateTxLocalServiceTime(validateParam(localUpdateTxLocalServiceTime));
        customParam.setPrepareCommandBytes(validateParam(prepareCommandBytes));
        customParam.setSuxNumPuts(validateParam(suxNumPuts));
        customParam.setRemoteGetLatency(validateParam(remoteGetLatency));
        customParam.setRetryWritePercentage(validateParam(retryWritePercentage));
        customParam.setRTT( validateParam(rtt) );

        Set<KPI> result = null;
        for( String oracleName : Controller.getInstance().getOracles() ){
            IOracle oracle = AbstractOracle.getInstance(oracleName, Controller.getInstance());
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

    private double validateParam(double val ){

        double ret = val;
        if( val < 0 ){
            ret = -1;
        }
        log.info("valore ritornato --> " + ret);
        return ret;
    }

    @GET @Path("/values")
    @Produces("application/json")
    public synchronized Response updateValuesFromSystem() {
        lastSample = StatsManager.getInstance().getLastSample();

        StringBuffer json = new StringBuffer();

        json.append("{ ");
        for( Map.Entry<String,ResourceType> param : sampledParams.entrySet() ){
            if (json.length() > 3) {
                json.append(" , ");
            }
            json.append( getJSON(param.getKey(), String.valueOf(StatsManager.getInstance().getAvgAttribute(param.getKey(), lastSample, param.getValue())) ) );
        }

        if (json.length() > 3) {
            json.append(" , ");
        }

        double acf;
        try {
            acf = ACF.evaluate(lastSample.getJmx(), Controller.getInstance().getCurrentConfiguration().threadPerNode(), Controller.TIME_WINDOW );
            log.info("********************************** ACF = " + acf + " ***************************");
        } catch (PublishAttributeException e) {
            e.printStackTrace();
            acf = -1;
        }
        json.append( getJSON("ACF", String.valueOf(acf) ) );

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