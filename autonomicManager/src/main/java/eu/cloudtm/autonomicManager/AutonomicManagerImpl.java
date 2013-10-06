package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.commons.Forecaster;
import eu.cloudtm.autonomicManager.commons.InstanceConfig;
import eu.cloudtm.autonomicManager.commons.PlatformConfiguration;
import eu.cloudtm.autonomicManager.commons.PlatformTuning;
import eu.cloudtm.autonomicManager.commons.ReplicationProtocol;
import eu.cloudtm.autonomicManager.commons.State;
import eu.cloudtm.autonomicManager.commons.dto.WhatIfCustomParamDTO;
import eu.cloudtm.autonomicManager.commons.dto.WhatIfDTO;
import eu.cloudtm.autonomicManager.optimizers.OptimizerType;
import eu.cloudtm.autonomicManager.statistics.ProcessedSample;
import eu.cloudtm.autonomicManager.statistics.StatsManager;
import eu.cloudtm.autonomicManager.workloadAnalyzer.WorkloadAnalyzer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: Fabio Perfetti perfabio87 [at] gmail.com Date: 7/10/13
 */
public class AutonomicManagerImpl implements AutonomicManager {

   private static Log log = LogFactory.getLog(AutonomicManagerImpl.class);

   private State state;
   private PlatformTuning platformTuning;
   private PlatformConfiguration platformConfiguration;
   private StatsManager statsManager;
   private WorkloadAnalyzer workloadAnalyzer;
   private Optimizer optimizer;
   private Reconfigurator reconfigurator;


   private final static int INTERVAL_BETWEEN_FORECAST = 300;
   private Date lastForecastTimestamp;
   private volatile PlatformConfiguration lastForecast;
   private ReentrantLock forecastLock = new ReentrantLock();


   public AutonomicManagerImpl(State state,
                               PlatformConfiguration platformConfiguration,
                               PlatformTuning platformTuning,
                               StatsManager sampleManager,
                               WorkloadAnalyzer workloadAnalyzer,
                               Optimizer optimizer,
                               Reconfigurator reconfigurator) {
      this.state = state;
      this.statsManager = sampleManager;
      this.platformConfiguration = platformConfiguration;
      this.platformTuning = platformTuning;

      this.workloadAnalyzer = workloadAnalyzer;
      this.optimizer = optimizer;
      this.reconfigurator = reconfigurator;
   }

   @Override
   public void customConfiguration(Map<OptimizerType, Object> configuration) {
      reconfigurator.reconfigure(configuration);
   }


   @Override
   public boolean isWorkloadAnalyzerEnabled() {
      return workloadAnalyzer.isEnabled();
   }

   @Override
   public void switchWorkloadAnalyzer() {
      workloadAnalyzer.enable(!workloadAnalyzer.isEnabled());
   }

   @Override
   public PlatformConfiguration forecast() {
      if (forecastLock.tryLock()) {
         try {
            if (lastForecastTimestamp != null) {
               long timeDiff = Math.abs(new Date().getTime() - lastForecastTimestamp.getTime()) / 1000;
               if (timeDiff < INTERVAL_BETWEEN_FORECAST && lastForecast != null) {
                  ControllerLogger.log.info("Not enough time elapsed between forecast...returning previous forecast result");
                  return lastForecast;
               }
            }
            lastForecastTimestamp = new Date();
            lastForecast = optimizer.optimizePlatform(statsManager.getLastSample(), true);
            ControllerLogger.log.info("AM: new lastForecast "+lastForecast);
            log.info("AM: new lastForecast "+lastForecast);
         } finally {
            forecastLock.unlock();
         }
      } else {
         log.info("Another thread is forecasting...");
      }
      return lastForecast;
   }

   @Override
   public void optimizeAndReconfigureNow() {
      if (!reconfigurator.isReconfiguring()) {
         ControllerLogger.log.info("Starting new optimization...");
         Map<OptimizerType, Object> optimization = optimizer.optimizeAll(statsManager.getLastSample(), false);
         reconfigureNow(optimization);

      } else {
         ControllerLogger.log.info("Reconfigurator busy! Skipping...");
      }
   }

   @Override
   public void reconfigureNow(Map<OptimizerType, Object> configuration) {
      ControllerLogger.log.info("Starting reconfiguration...");
      reconfigurator.reconfigure(configuration);

   }

   @Override
   public List<WhatIfDTO> whatIf(WhatIfCustomParamDTO customParamDTO) {
      ProcessedSample processedSample = statsManager.getLastSample();
      if (processedSample == null) {
         log.warn("No sample in the StatsManager");
         return null;
      }

      log.trace("Instancing what-if service");

      WhatIfService whatIfService = new WhatIfService(processedSample);

      if (whatIfService == null) {
         log.trace("whatIfService == null");
      }
      log.trace("A retrieving current param");

        /* retrieving current values */
      WhatIfCustomParamDTO paramDTO = null;
      try {
         paramDTO = whatIfService.retrieveCurrentValues();
      } catch (Exception e) {
         log.fatal(e, e);
         System.out.println(e.getMessage());
         e.printStackTrace();
      }


      log.trace("MERGING customParamDTO to paramDTO...");

        /* forecaster */
      for (Forecaster forecaster : customParamDTO.getForecasters()) {
         log.trace("Adding forecaster: " + forecaster);
         paramDTO.addForecaster(forecaster);
      }
        /* X-axis */
      paramDTO.setXaxis(customParamDTO.getXaxis());

        /* replication scale */
      paramDTO.setFixedNodesMin(customParamDTO.getFixedNodesMin());
      paramDTO.setFixedNodesMax(customParamDTO.getFixedNodesMax());

        /* replication degree */
      paramDTO.setFixedDegreeMin(customParamDTO.getFixedDegreeMin());
      paramDTO.setFixedDegreeMax(customParamDTO.getFixedDegreeMax());

        /* replication protocol */
      paramDTO.setFixedProtocol(customParamDTO.getFixedProtocol());

        /* merge all the others params if not default */

      log.trace("Custom params merged!");

      List<WhatIfDTO> result = whatIfService.evaluate(paramDTO);
      return result;
   }

   @Override
   public PlatformConfiguration currentConfiguration() {
      //log.trace("Cloning platformConfiguration...");
      return platformConfiguration();
   }

   /**
    * Returns a copy of current PlatformTuning
    *
    * @return a copy of current PlatformTuning
    */
   @Override
   public PlatformTuning platformTuning() {
      //log.trace("Cloning platformTuning...");
      return platformTuning.cloneThroughJson();
   }

   /**
    * Returns a copy of current PlatformConfiguration
    *
    * @return a copy of current PlatformConfiguration
    */
   @Override
   public PlatformConfiguration platformConfiguration() {
      //log.trace("Cloning platformConfiguration...");
      return platformConfiguration.cloneThroughJson();
   }

   /**
    * Returns a copy of current State
    *
    * @return a copy of current State
    */
   @Override
   public State state() {
      //log.trace("Cloning state...");
      return state.cloneThroughJson();
   }

   @Override
   public void updateForecaster(Forecaster forecaster) {
      log.info("Updating forecaster from:" + platformTuning().forecaster() + " to " + forecaster);
      platformTuning.setForecaster(forecaster);
   }

   @Override
   public void updateScale(boolean tuning, int size, InstanceConfig instanceConfig) {
      log.info("Updating scale");

      platformTuning.autoScale(tuning);
      if (!tuning) {
         log.info("Triggering reconfiguration (" + size + ")");
         PlatformConfiguration platformReq = new PlatformConfiguration(size, platformConfiguration.replicationDegree(), platformConfiguration.replicationProtocol());
         Map<OptimizerType, Object> optimization = new HashMap<OptimizerType, Object>();
         optimization.put(OptimizerType.PLATFORM, platformReq);
         reconfigureNow(optimization);
         // the curr platform state will be updated when reconfiguration ends
      }
   }

   @Override
   public void updateDegree(boolean tuning, int degree) {
      log.info("Updating degree");

      platformTuning.autoDegree(tuning);
      if (!tuning) {
         log.info("Triggering reconfiguration (" + degree + ")");
         PlatformConfiguration platformReq = new PlatformConfiguration(platformConfiguration.platformSize(), degree, platformConfiguration.replicationProtocol());
         Map<OptimizerType, Object> optimization = new HashMap<OptimizerType, Object>();
         optimization.put(OptimizerType.PLATFORM, platformReq);
         reconfigureNow(optimization);
         // the curr platform state will be updated when reconfiguration ends
      }
   }

   @Override
   public void updateProtocol(boolean tuning, ReplicationProtocol protocol) {
      log.info("Updating protocol");

      platformTuning.autoProtocol(tuning);
      if (!tuning) {
         ControllerLogger.log.info("Triggering new reconfiguration changing replication protocol" + protocol + ")");
         PlatformConfiguration platformReq = new PlatformConfiguration(platformConfiguration.platformSize(), platformConfiguration.replicationDegree(), protocol);
         Map<OptimizerType, Object> optimization = new HashMap<OptimizerType, Object>();
         optimization.put(OptimizerType.PLATFORM, platformReq);
         reconfigureNow(optimization);
         // the curr platform state will be updated when reconfiguration ends
      }
   }

   @Override
   public StatsManager getStatsManager() {
      return statsManager;
   }

}
