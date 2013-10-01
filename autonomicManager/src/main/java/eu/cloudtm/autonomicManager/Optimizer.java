package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.commons.PlatformConfiguration;
import eu.cloudtm.autonomicManager.optimizers.OptimizerType;
import eu.cloudtm.autonomicManager.statistics.ProcessedSample;

import java.util.Map;

/**
 * Author: Fabio Perfetti (perfabio87 [at] gmail.com) Date: 8/6/13 Time: 2:08 PM
 */
public interface Optimizer {

   public Map<OptimizerType, Object> optimizeAll(ProcessedSample processedSample, boolean pureForecast);

   public PlatformConfiguration optimizePlatform(ProcessedSample processedSample, boolean pureForecast);

   public <T> T optimizeAutoPlacer(ProcessedSample processedSample, boolean pureForecast);

}
