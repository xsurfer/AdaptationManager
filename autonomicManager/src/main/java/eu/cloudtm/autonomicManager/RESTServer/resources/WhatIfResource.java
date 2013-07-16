package eu.cloudtm.autonomicManager.RESTServer.resources;

import com.google.gson.Gson;
import eu.cloudtm.autonomicManager.WhatIf;
import eu.cloudtm.commons.Forecaster;
import eu.cloudtm.commons.OutputOracle;
import eu.cloudtm.commons.PlatformConfiguration;
import eu.cloudtm.commons.dto.WhatIfCustomParamDTO;
import eu.cloudtm.statistics.StatsManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.TreeMap;

@Path("/whatif")
public class WhatIfResource extends AbstractResource {

    private static Log log = LogFactory.getLog(WhatIfResource.class);

    private Gson gson = new Gson();

    @Inject
    private StatsManager statsManager;


    @GET @Path("/values")
    @Produces("application/json")
    public synchronized Response updateValuesFromSystem() {

        WhatIf whatIf = new WhatIf(statsManager.getLastSample());
        WhatIfCustomParamDTO customDTO = whatIf.retrieveCurrentValues();

        String json = gson.toJson(customDTO);

        log.info("Custom Values Retrieved: " + json);

        Response.ResponseBuilder builder = Response.ok(json.toString());
        return makeCORS(builder);
    }


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

        WhatIfCustomParamDTO customParam = new WhatIfCustomParamDTO();
        customParam.setACF( acf );
        customParam.setCommitBroadcastWallClockTime( commitBroadcastWallClockTime );
        customParam.setGetReadOnlyTx( getReadOnlyTx );
        customParam.setGetWriteTx( getWriteTx );
        customParam.setLocalReadOnlyTxLocalServiceTime( localReadOnlyTxLocalServiceTime );
        customParam.setLocalUpdateTxLocalServiceTime( localUpdateTxLocalServiceTime );
        customParam.setPrepareCommandBytes( prepareCommandBytes );
        customParam.setSuxNumPuts( suxNumPuts );
        customParam.setRemoteGetLatency( remoteGetLatency );
        customParam.setRetryWritePercentage( retryWritePercentage );
        customParam.setRTT( rtt );

        WhatIf whatIf = new WhatIf(statsManager.getLastSample());
        Map<Forecaster, TreeMap<PlatformConfiguration, OutputOracle>> result = whatIf.evaluate(customParam);

        StringBuffer json = new StringBuffer();
        json.append( gson.toJson(result) );

        log.info(json);
        Response.ResponseBuilder builder = Response.ok( json.toString() );
        return makeCORS(builder);
    }


    private String getJSON(String key, String val){
        StringBuffer strBuf = new StringBuffer();
        strBuf.append( gson.toJson( key ) );
        strBuf.append( ":" );
        strBuf.append( gson.toJson( val ) );
        return strBuf.toString();
    }


    // DA VISUALIZZARE:
//    [
//    {
//        "forecaster":"analytical",
//            "throughput": [
//        [2,100],
//        [3,100],
//        [4,100],
//        [5,100],
//        [6,100]
//        ]
//    },
//    {
//        "forecaster":"simulator",
//            "throughput": [
//        [2,100],
//        [3,100],
//        [4,100],
//        [5,100],
//        [6,100]
//        ]
//    },
//    {
//        "forecaster":"morpher",
//            "throughput": [
//        [2,100],
//        [3,100],
//        [4,100],
//        [5,100],
//        [6,100]
//        ]
//    }
//
//    ]

}