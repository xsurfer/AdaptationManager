package eu.cloudtm.autonomicManager.RESTServer.resources;

import com.google.gson.Gson;
import eu.cloudtm.autonomicManager.WhatIfService;
import eu.cloudtm.autonomicManager.commons.EvaluatedParam;
import eu.cloudtm.autonomicManager.commons.Forecaster;
import eu.cloudtm.autonomicManager.commons.GsonFactory;
import eu.cloudtm.autonomicManager.commons.ReplicationProtocol;
import eu.cloudtm.autonomicManager.commons.dto.WhatIfCustomParamDTO;
import eu.cloudtm.autonomicManager.commons.dto.WhatIfDTO;
import eu.cloudtm.autonomicManager.statistics.ProcessedSample;
import eu.cloudtm.autonomicManager.statistics.StatsManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/whatif")
public class WhatIfResource extends AbstractResource {

    private static Log log = LogFactory.getLog(WhatIfResource.class);

    @Inject
    private StatsManager statsManager;


    @GET @Path("/values")
    @Produces(MediaType.APPLICATION_JSON)
    public synchronized Response updateValuesFromSystem() {

        ProcessedSample sample = statsManager.getLastSample();

        if(sample!=null)
            log.info("Sample: " + sample.getId() );
        else
            log.info("Sample is null");

        log.info("ACF: " + sample.getEvaluatedParam(EvaluatedParam.ACF));

        WhatIfService whatIfService = new WhatIfService(sample);
        WhatIfCustomParamDTO customDTO = whatIfService.retrieveCurrentValues();

        List<String> fieldExclusions = new ArrayList<String>();
        fieldExclusions.add("forecasters");
        fieldExclusions.add("replProtocol");


        Gson gson = GsonFactory.build(fieldExclusions, null);

        String json = gson.toJson(customDTO);

        log.info("Custom Values Retrieved: " + json);

        Response.ResponseBuilder builder = Response.ok( json );
        return makeCORS(builder);
    }


    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public synchronized Response whatIf(

            @DefaultValue("-1") @FormParam("acf") Double acf,
            @DefaultValue("-1") @FormParam("PercentageSuccessWriteTransactions") Double percentageSuccessWriteTransactions,
            @DefaultValue("-1") @FormParam("AvgNumPutsBySuccessfulLocalTx") Double avgNumPutsBySuccessfulLocalTx,
            @DefaultValue("-1") @FormParam("AvgGetsPerWrTransaction") Double avgGetsPerWrTransaction,
            @DefaultValue("-1") @FormParam("AvgGetsPerROTransaction") Long avgGetsPerROTransaction,

            @DefaultValue("-1") @FormParam("LocalUpdateTxLocalServiceTime") Long localUpdateTxLocalServiceTime,
            @DefaultValue("-1") @FormParam("LocalReadOnlyTxLocalServiceTime") Long localReadOnlyTxLocalServiceTime,

            @DefaultValue("-1") @FormParam("AvgPrepareCommandSize") Long avgPrepareCommandSize,
            @DefaultValue("-1") @FormParam("AvgPrepareAsync") Long avgPrepareAsync,
            @DefaultValue("-1") @FormParam("AvgCommitAsync") Long avgCommitAsync,
            @DefaultValue("-1") @FormParam("AvgRemoteGetRtt") Long avgRemoteGetRtt,

            @FormParam("oracoles") List<String> fores,
            @DefaultValue("2")@FormParam("repDegree") Integer repDegree,
            @DefaultValue("TWOPC") @FormParam("repProtocol") ReplicationProtocol repProtocol
    ){
        log.trace("acf: " + acf);
        log.trace("percentageSuccessWriteTransactions: " + percentageSuccessWriteTransactions);
        log.trace("avgNumPutsBySuccessfulLocalTx: " + avgNumPutsBySuccessfulLocalTx);
        log.trace("avgGetsPerWrTransaction: " + avgGetsPerWrTransaction);
        log.trace("avgGetsPerROTransaction: " + avgGetsPerROTransaction);
        log.trace("localUpdateTxLocalServiceTime: " + localUpdateTxLocalServiceTime);
        log.trace("localReadOnlyTxLocalServiceTime: " + localReadOnlyTxLocalServiceTime);
        log.trace("avgPrepareCommandSize: " + avgPrepareCommandSize);
        log.trace("avgPrepareAsync: " + avgPrepareAsync);
        log.trace("avgCommitAsync: " + avgCommitAsync);
        log.trace("avgRemoteGetRtt: " + avgRemoteGetRtt);
        log.trace("fores: " + fores);
        log.trace("repDegree: " + repDegree);

        WhatIfCustomParamDTO customParam = new WhatIfCustomParamDTO();

        for(String forecasterString : fores){
            log.trace("Adding " + forecasterString);
            Forecaster forecaster = Forecaster.valueOf(forecasterString);
            customParam.addForecaster(forecaster);
        }
        customParam.setReplicationDegree(repDegree);
        customParam.setReplicationProtocol(repProtocol);

        customParam.setACF( acf );
        customParam.setAvgCommitAsync(avgCommitAsync);
        customParam.setAvgGetsPerROTransaction(avgGetsPerROTransaction);
        customParam.setAvgGetsPerWrTransaction(avgGetsPerWrTransaction);
        customParam.setLocalReadOnlyTxLocalServiceTime( localReadOnlyTxLocalServiceTime );
        customParam.setLocalUpdateTxLocalServiceTime( localUpdateTxLocalServiceTime );
        customParam.setAvgPrepareCommandSize(avgPrepareCommandSize);
        customParam.setAvgNumPutsBySuccessfulLocalTx(avgNumPutsBySuccessfulLocalTx);
        customParam.setAvgRemoteGetRtt(avgRemoteGetRtt);
        customParam.setPercentageSuccessWriteTransactions(percentageSuccessWriteTransactions);
        customParam.setAvgPrepareAsync(avgPrepareAsync);

        ProcessedSample sample = statsManager.getLastSample();
        WhatIfService whatIfService = new WhatIfService(sample);
        if(sample!=null)
            log.info("Sample: " + sample.getId() );
        else
            log.info("Sample is null");

        List<WhatIfDTO> result = whatIfService.evaluate(customParam);

        Gson gson = new Gson();
        String json = gson.toJson(result);

        log.trace("RESULT: " + json);

        Response.ResponseBuilder builder = Response.ok( json );
        return makeCORS(builder);
    }



//    ESEMPIO DI RISPOSTA DA GENERARE:
//    [
//    {
//        "forecaster":"analytical",
//        "throughput": [
//              [2,100],
//              [3,100],
//              [4,100],
//              [5,100],
//              [6,100]
//          ],
//        "responseTimeRead": [
//              [2,100],
//              [3,100],
//              [4,100],
//              [5,100],
//              [6,100]
//          ],
//        "responseTimeWrite": [
//              [2,100],
//              [3,100],
//              [4,100],
//              [5,100],
//              [6,100]
//          ],
//        "abortRate": [
//              [2,100],
//              [3,100],
//              [4,100],
//              [5,100],
//              [6,100]
//          ],
//    },
//    {
//        "forecaster":"simulator",
//        "throughput": [
//              [2,100],
//              [3,100],
//              [4,100],
//              [5,100],
//              [6,100]
//          ],
//        "responseTimeRead": [
//              [2,100],
//              [3,100],
//              [4,100],
//              [5,100],
//              [6,100]
//          ],
//        "responseTimeWrite": [
//              [2,100],
//              [3,100],
//              [4,100],
//              [5,100],
//              [6,100]
//          ],
//        "abortRate": [
//              [2,100],
//              [3,100],
//              [4,100],
//              [5,100],
//              [6,100]
//          ],
//    }
//    {
//        "forecaster":"morpher",
//        "throughput": [
//              [2,100],
//              [3,100],
//              [4,100],
//              [5,100],
//              [6,100]
//          ],
//        "responseTimeRead": [
//              [2,100],
//              [3,100],
//              [4,100],
//              [5,100],
//              [6,100]
//          ],
//        "responseTimeWrite": [
//              [2,100],
//              [3,100],
//              [4,100],
//              [5,100],
//              [6,100]
//          ],
//        "abortRate": [
//              [2,100],
//              [3,100],
//              [4,100],
//              [5,100],
//              [6,100]
//          ],
//    },
//
//    ]

}