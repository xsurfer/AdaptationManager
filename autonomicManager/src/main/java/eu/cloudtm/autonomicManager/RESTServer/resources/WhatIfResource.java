package eu.cloudtm.autonomicManager.RESTServer.resources;

import CsvOracles.RadargunCsvInputOracle;
import CsvOracles.params.CsvRgParams;
import com.google.gson.Gson;
import eu.cloudtm.autonomicManager.AutonomicManager;
import eu.cloudtm.autonomicManager.WhatIfService;
import eu.cloudtm.autonomicManager.commons.*;
import eu.cloudtm.autonomicManager.commons.dto.WhatIfCustomParamDTO;
import eu.cloudtm.autonomicManager.commons.dto.WhatIfDTO;
import eu.cloudtm.autonomicManager.configs.Config;
import eu.cloudtm.autonomicManager.statistics.ProcessedSample;
import eu.cloudtm.autonomicManager.statistics.StatsManager;
import eu.cloudtm.autonomicManager.statistics.samples.CustomSample;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Path("/whatif")
public class WhatIfResource extends AbstractResource {

   private static Log log = LogFactory.getLog(WhatIfResource.class);
   @Inject
   private StatsManager statsManager;
   @Inject
   private AutonomicManager autonomicManager;
   private ProcessedSample lastCustomSample = null;

   @GET
   @Path("/values")
   @Produces(MediaType.APPLICATION_JSON)
   public synchronized Response updateValuesFromSystem() {

      ProcessedSample sample;
      if (Config.getInstance().isUsingStub()) {
         log.trace("Using help from stub");
         try {
            if (lastCustomSample == null)
               sample = lastCustomSample = processedSampleFromStub();
            else
               sample = lastCustomSample;
         } catch (IOException e) {
            e.printStackTrace();
            log.error(e);
            return null;
         }
      } else {
         log.trace("Taking values from last measured sample");
         sample = statsManager.getLastSample();
      }

      if (sample != null)
         log.info("Sample: " + sample.getId());
      else {
         log.info("Sample is null");
         throw new IllegalArgumentException("Sample is null");
      }

      log.info("ACF: " + sample.getEvaluatedParam(EvaluatedParam.ACF));

      WhatIfService whatIfService = new WhatIfService(sample);
      WhatIfCustomParamDTO customDTO = whatIfService.retrieveCurrentValues();

      List<String> fieldExclusions = new ArrayList<String>();
      fieldExclusions.add("forecasters");
      fieldExclusions.add("replProtocol");


      Gson gson = GsonFactory.build(fieldExclusions, null);

      String json = gson.toJson(customDTO);

      log.info("Custom Values Retrieved: " + json);

      Response.ResponseBuilder builder = Response.ok(json);
      return makeCORS(builder);
   }

   @GET
   @Path("/forecast")
   @Produces(MediaType.APPLICATION_JSON)
   public synchronized Response forecast() {
      PlatformConfiguration platformConfigurationPredicted = autonomicManager.forecast();

      Gson gson = new Gson();
      String json = gson.toJson(platformConfigurationPredicted);

      log.info("platformConfigurationPredicted: " + json);

      Response.ResponseBuilder builder = Response.ok(json);
      return makeCORS(builder);
   }



    /*
    acf=0
    percentageSuccessWriteTransactions=0.10016601852864385
    avgNumPutsBySuccessfulLocalTx=10
    avgGetsPerWrTransaction=10
    avgGetsPerROTransaction=10
    localUpdateTxLocalServiceTime=244
    localReadOnlyTxLocalServiceTime=122
    avgPrepareCommandSize=5546
    avgPrepareAsync=0
    avgCommitAsync=5
    avgRemoteGetRtt=0
    xaxis=NODES
    fixed_nodes_min=2
    fixed_nodes_max=10
    fixed_degree_max=10
    fixed_protocol=TWOPC
     */

   @POST
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   @Produces(MediaType.APPLICATION_JSON)
   public synchronized Response whatIf(

           @DefaultValue("-1") @FormParam("acf") Double acf,
           @DefaultValue("-1") @FormParam("percentageSuccessWriteTransactions") Double percentageSuccessWriteTransactions,
           @DefaultValue("-1") @FormParam("avgNumPutsBySuccessfulLocalTx") Double avgNumPutsBySuccessfulLocalTx,
           @DefaultValue("-1") @FormParam("avgGetsPerWrTransaction") Double avgGetsPerWrTransaction,
           @DefaultValue("-1") @FormParam("avgGetsPerROTransaction") Double avgGetsPerROTransaction,

           @DefaultValue("-1") @FormParam("localUpdateTxLocalServiceTime") Double localUpdateTxLocalServiceTime,
           @DefaultValue("-1") @FormParam("localReadOnlyTxLocalServiceTime") Double localReadOnlyTxLocalServiceTime,

           @DefaultValue("-1") @FormParam("avgPrepareCommandSize") Double avgPrepareCommandSize,
           @DefaultValue("-1") @FormParam("avgPrepareAsync") Double avgPrepareAsync,
           @DefaultValue("-1") @FormParam("avgCommitAsync") Double avgCommitAsync,
           @DefaultValue("-1") @FormParam("avgRemoteGetRtt") Double avgRemoteGetRtt,

           @DefaultValue("NODES") @FormParam("xaxis") WhatIfCustomParamDTO.Xaxis xaxis,

           @DefaultValue("-1") @FormParam("fixed_nodes_min") int fixedNodesMin,
           @DefaultValue("-1") @FormParam("fixed_nodes_max") int fixedNodesMax,

           @DefaultValue("-1") @FormParam("fixed_degree_min") int fixedDegreeMin,
           @DefaultValue("-1") @FormParam("fixed_degree_max") int fixedDegreeMax,

           @DefaultValue("TWOPC") @FormParam("fixed_protocol") ReplicationProtocol fixedProtocol,

           @FormParam("oracoles") List<String> fores
   ) {
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

      log.trace("xaxis: " + xaxis);

      log.trace("fixed_nodes_min: " + fixedNodesMin);
      log.trace("fixed_nodes_max: " + fixedNodesMax);

      log.trace("fixed_degree_min: " + fixedDegreeMin);
      log.trace("fixed_degree_max " + fixedDegreeMax);

      log.trace("fixed_protocol: " + fixedProtocol);

      log.trace("fores: " + fores);


      WhatIfCustomParamDTO customParam = new WhatIfCustomParamDTO();

      for (String forecasterString : fores) {
         log.trace("Adding " + forecasterString);
         Forecaster forecaster = Forecaster.valueOf(forecasterString);
         customParam.addForecaster(forecaster);
      }

      customParam.setXaxis(xaxis);

      switch (xaxis) {
         case NODES:
            customParam.setFixedNodesMin(fixedNodesMin);
            customParam.setFixedNodesMax(fixedNodesMax);
            customParam.setFixedDegreeMax(fixedDegreeMax);
            customParam.setFixedProtocol(fixedProtocol);
            break;
         case DEGREE:
            customParam.setFixedNodesMax(fixedNodesMax);
            customParam.setFixedDegreeMin(fixedDegreeMin);
            customParam.setFixedDegreeMax(fixedDegreeMax);
            customParam.setFixedProtocol(fixedProtocol);
            break;
         case PROTOCOL:
            customParam.setFixedNodesMax(fixedNodesMax);
            customParam.setFixedDegreeMax(fixedDegreeMax);
            customParam.setFixedProtocol(fixedProtocol);
            break;
         default:
            throw new IllegalStateException("Never should reach this point!");
      }

      customParam.setACF(acf);
      customParam.setAvgCommitAsync(avgCommitAsync);
      customParam.setAvgGetsPerROTransaction(avgGetsPerROTransaction);
      customParam.setAvgGetsPerWrTransaction(avgGetsPerWrTransaction);
      customParam.setLocalReadOnlyTxLocalServiceTime(localReadOnlyTxLocalServiceTime);
      customParam.setLocalUpdateTxLocalServiceTime(localUpdateTxLocalServiceTime);
      customParam.setAvgPrepareCommandSize(avgPrepareCommandSize);
      customParam.setAvgNumPutsBySuccessfulLocalTx(avgNumPutsBySuccessfulLocalTx);
      customParam.setAvgRemoteGetRtt(avgRemoteGetRtt);
      customParam.setPercentageSuccessWriteTransactions(percentageSuccessWriteTransactions);
      customParam.setAvgPrepareAsync(avgPrepareAsync);

      ProcessedSample sample = statsManager.getLastSample();
      WhatIfService whatIfService = new WhatIfService(sample);
      if (sample != null)
         log.info("Sample: " + sample.getId());
      else
         log.info("Sample is null");

      List<WhatIfDTO> result = whatIfService.evaluate(customParam);

      Gson gson = new Gson();
      String json = gson.toJson(result);

      log.trace("RESULT: " + json);

      Response.ResponseBuilder builder = Response.ok(json);
      return makeCORS(builder);
   }

   private ProcessedSample processedSampleFromStub() throws IOException {
      try {
         log.trace("Processing Sample from Stub");
         String stub = Config.getInstance().stub();
         log.trace("Going to use stub " + stub);
         CsvRgParams param = new CsvRgParams(stub);
         log.trace("CsvParams created " + param);
         RadargunCsvInputOracle io = new RadargunCsvInputOracle(param);
         log.trace("InputOracle from csv " + io);
         CustomSample customSample = new CustomSample(null, io.getpMap(), io.geteMap());
         log.trace("Custom Sample " + customSample);
         return customSample;
      } catch (Exception e) {
         log.error(e);
         throw new IOException(e);
      }
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