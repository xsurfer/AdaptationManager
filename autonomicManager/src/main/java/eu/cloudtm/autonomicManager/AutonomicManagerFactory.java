package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.actuators.ActuatorFactory;
import eu.cloudtm.autonomicManager.commons.Forecaster;
import eu.cloudtm.autonomicManager.commons.PlatformConfiguration;
import eu.cloudtm.autonomicManager.commons.PlatformState;
import eu.cloudtm.autonomicManager.commons.PlatformTuning;
import eu.cloudtm.autonomicManager.commons.State;
import eu.cloudtm.autonomicManager.configs.Config;
import eu.cloudtm.autonomicManager.configs.KeyConfig;
import eu.cloudtm.autonomicManager.optimizers.LAOptimizer;
import eu.cloudtm.autonomicManager.optimizers.MulePlatformOptimizer;
import eu.cloudtm.autonomicManager.optimizers.OptimizerComponent;
import eu.cloudtm.autonomicManager.optimizers.OptimizerImpl;
import eu.cloudtm.autonomicManager.statistics.SampleProducer;
import eu.cloudtm.autonomicManager.statistics.WPMStatsManager;
import eu.cloudtm.autonomicManager.statistics.WPMStatsManagerFactory;
import eu.cloudtm.autonomicManager.workloadAnalyzer.WorkloadAnalyzer;
import eu.cloudtm.autonomicManager.workloadAnalyzer.WorkloadAnalyzerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Fabio Perfetti E-mail: perfabio87@gmail.com Date: 7/10/13
 */
public class AutonomicManagerFactory implements AbstractAutonomicManagerFactory {

   private static Log log = LogFactory.getLog(AutonomicManagerFactory.class);

   private ActuatorFactory actuatorFactory = new ActuatorFactory();

   private State platformState = new State(PlatformState.RUNNING);
   private Reconfigurator reconfigurator;
   private PlatformTuning platformTuning = new PlatformTuning(Forecaster.ANALYTICAL, false);
   private PlatformConfiguration platformConfiguration;
   private Optimizer optimizer;
   private SLAManager slaManager;
   private WorkloadAnalyzer workloadAnalyzer;

   private WPMStatsManager wpmStatsManager;

   public AutonomicManagerFactory() {
   }


   public AutonomicManager build() {

      AutonomicManager autonomicManager = new AutonomicManagerImpl(
            platformState,
            getPlatformConfiguration(),
            platformTuning,
            getStatsManager(),
            getWorkloadAnalyzer(),
            getOptimizer(),
            getReconfigurator());

      return autonomicManager;
   }

   public WPMStatsManager getStatsManager() {

      if (wpmStatsManager == null) {
         WPMStatsManagerFactory wpmStatsManagerFactory = new WPMStatsManagerFactory(getPlatformConfiguration());
         wpmStatsManager = wpmStatsManagerFactory.build();
      }

      return wpmStatsManager;
   }

   @Override
   public PlatformConfiguration getPlatformConfiguration() {
      if (this.platformConfiguration == null) {
         this.platformConfiguration = new PlatformConfiguration();
      }
      return this.platformConfiguration;
   }


   @Override
   public Reconfigurator getReconfigurator() {
      if (this.reconfigurator == null) {
         this.reconfigurator = new ReconfiguratorImpl(getPlatformConfiguration(), platformState, actuatorFactory.build());
      }
      return reconfigurator;
   }

   @Override
   public Optimizer getOptimizer() {

      if (this.optimizer == null) {
         List<OptimizerComponent> optimizerFilters = new ArrayList<OptimizerComponent>();


         String platformOptimizerStr = Config.getInstance().getString(KeyConfig.ENVIRONMENT_SYSTEM_TYPE.key());

         AbstractPlatformOptimizer platformOptimizer = null;
         if (platformOptimizerStr.equals("MULE")) {
            log.trace("Platform Optimizer type: MulePlatformOptimizer");
            platformOptimizer = new MulePlatformOptimizer(getPlatformConfiguration(), platformTuning);
         }

         this.optimizer = new OptimizerImpl(platformOptimizer, new LAOptimizer());
      }
      return this.optimizer;
   }

   @Override
   public SLAManager getSLAManager() {
      if (this.slaManager == null) {
         this.slaManager = new SLAManager();
      }
      return this.slaManager;
   }

   @Override
   public WorkloadAnalyzer getWorkloadAnalyzer() {
      if (this.workloadAnalyzer == null) {
         WorkloadAnalyzerFactory workloadAnalyzerFactory = new WorkloadAnalyzerFactory((SampleProducer) getStatsManager(), getReconfigurator(), getOptimizer());
         this.workloadAnalyzer = workloadAnalyzerFactory.build();
      }
      return this.workloadAnalyzer;
   }


}