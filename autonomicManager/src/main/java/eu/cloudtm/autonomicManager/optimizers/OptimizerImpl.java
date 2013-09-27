package eu.cloudtm.autonomicManager.optimizers;

import eu.cloudtm.autonomicManager.AbstractPlatformOptimizer;
import eu.cloudtm.autonomicManager.Optimizer;
import eu.cloudtm.autonomicManager.commons.PlatformConfiguration;
import eu.cloudtm.autonomicManager.statistics.ProcessedSample;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Fabio Perfetti (perfabio87 [at] gmail.com) Date: 8/10/13 Time: 11:36 AM
 */
public class OptimizerImpl implements Optimizer {

   private static Log log = LogFactory.getLog(OptimizerImpl.class);

   private final AbstractPlatformOptimizer platformOptimizer;
   private final LAOptimizer autoplacerOptimizer;

   public OptimizerImpl(AbstractPlatformOptimizer platformOptimizer, LAOptimizer autoplacerOptimizer) {
      this.platformOptimizer = platformOptimizer;
      this.autoplacerOptimizer = autoplacerOptimizer;

   }

   @Override
   public Map<OptimizerType, Object> optimizeAll(ProcessedSample processedSample, boolean pureForecast) {

      Map<OptimizerType, Object> filter2output = new HashMap<OptimizerType, Object>();

      log.trace("Optimizing platform...");
      Object ret = optimizePlatform(processedSample, pureForecast);
      filter2output.put(OptimizerType.PLATFORM, ret);

      log.fatal("OptimizerImpl: autoplacer optimizing commented");
        /*
        log.trace("Optimizing autoplacer...");
        ret = optimizeAutoPlacer(processedSample);
        filter2output.put( OptimizerType.AUTOPLACER, ret );
        */

      return filter2output;
   }

   @Override
   public PlatformConfiguration optimizePlatform(ProcessedSample processedSample, boolean pureForecast) {
      if (platformOptimizer != null) {
         return platformOptimizer.doOptimize(processedSample, pureForecast);
      }
      return null;
   }

   @Override
   public Object optimizeAutoPlacer(ProcessedSample processedSample, boolean purePrediction) {
      if (autoplacerOptimizer != null) {
         return autoplacerOptimizer.doOptimize(processedSample, purePrediction);
      }
      return null;
   }


}
