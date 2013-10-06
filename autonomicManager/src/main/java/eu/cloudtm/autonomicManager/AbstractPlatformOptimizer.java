package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.commons.PlatformConfiguration;
import eu.cloudtm.autonomicManager.commons.PlatformTuning;
import eu.cloudtm.autonomicManager.commons.ReplicationProtocol;
import eu.cloudtm.autonomicManager.optimizers.OptimizerComponent;
import eu.cloudtm.autonomicManager.optimizers.OptimizerType;
import eu.cloudtm.autonomicManager.statistics.ProcessedSample;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by: Fabio Perfetti E-mail: perfabio87@gmail.com Date: 6/16/13
 */
public abstract class AbstractPlatformOptimizer implements OptimizerComponent<PlatformConfiguration> {

   private static Log log = LogFactory.getLog(AbstractPlatformOptimizer.class);

   protected PlatformTuning platformTuning;
   protected PlatformConfiguration platformConfiguration;


   public AbstractPlatformOptimizer(PlatformConfiguration platformConfiguration,
                                    PlatformTuning platformTuning) {
      this.platformTuning = platformTuning;
      this.platformConfiguration = platformConfiguration;
   }

   @Override
   public PlatformConfiguration doOptimize(ProcessedSample sample, boolean purePrediction) {
      if (!purePrediction) {
         if (!platformTuning.forecaster().isAutoTuning()) {
            log.trace("Not pure prediction, but returning null as autoTuning is false");
            return null;
         }
      }
      log.trace("Going to optimize!");
      return optimize(sample, purePrediction);
   }

   protected abstract PlatformConfiguration optimize(ProcessedSample processedSample, boolean purePrediction);


   protected PlatformConfiguration createNextConfig(PlatformConfiguration forecastedConfig) {
      int size, repDegree;
      ReplicationProtocol repProt;

      size = (!platformTuning.isAutoScale()) ? platformConfiguration.platformSize() : forecastedConfig.platformSize();
      repDegree = (!platformTuning.isAutoDegree()) ? platformConfiguration.replicationDegree() : forecastedConfig.replicationDegree();
      repProt = (!platformTuning.isAutoProtocol()) ? platformConfiguration.replicationProtocol() : forecastedConfig.replicationProtocol();

      return new PlatformConfiguration(size, repDegree, repProt);
   }

   @Override
   public final OptimizerType getType() {
      return OptimizerType.PLATFORM;
   }

}

