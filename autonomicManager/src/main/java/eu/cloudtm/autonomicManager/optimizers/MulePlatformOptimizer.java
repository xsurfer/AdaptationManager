package eu.cloudtm.autonomicManager.optimizers;

import eu.cloudtm.autonomicManager.AbstractPlatformOptimizer;
import eu.cloudtm.autonomicManager.ControllerLogger;
import eu.cloudtm.autonomicManager.commons.PlatformConfiguration;
import eu.cloudtm.autonomicManager.commons.PlatformTuning;
import eu.cloudtm.autonomicManager.oracles.HillClimbingOracleService;
import eu.cloudtm.autonomicManager.oracles.Oracle;
import eu.cloudtm.autonomicManager.oracles.OracleService;
import eu.cloudtm.autonomicManager.oracles.exceptions.OracleException;
import eu.cloudtm.autonomicManager.statistics.ProcessedSample;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by: Fabio Perfetti E-mail: perfabio87@gmail.com Date: 6/16/13
 */
public class MulePlatformOptimizer extends AbstractPlatformOptimizer {

   private static Log log = LogFactory.getLog(MulePlatformOptimizer.class);

   public MulePlatformOptimizer(PlatformConfiguration platformConfiguration,
                                PlatformTuning platformTuning) {
      super(platformConfiguration, platformTuning);
   }


   public PlatformConfiguration optimize(ProcessedSample processedSample, boolean purePrediction) {

      if (processedSample == null) {
         log.info("Sample is null. Skipping...");
         return null;
      }

      ControllerLogger.log.info("Mule Optimizer: Querying " + platformTuning.forecaster() + " oracle");

      Oracle oracle = platformTuning.forecaster().getInstance();
      OracleService oracleService = new HillClimbingOracleService(oracle);

      PlatformConfiguration forecastedConfig = null;
      try {
         forecastedConfig = oracleService.maximizeThroughput(processedSample);
      } catch (OracleException e) {
         if (log.isDebugEnabled()) {
            log.debug(e, e);
         } else {
            log.warn(e);
         }
      }

      if (forecastedConfig != null) {

         ControllerLogger.log.info(" »»» Configuration found «««");
         if (purePrediction) {
            return forecastedConfig;
         } else {
            return createNextConfig(forecastedConfig);
         }
      } else {
         ControllerLogger.log.info(" »»» Configuration not found «««");
         return null;
      }
   }
}
