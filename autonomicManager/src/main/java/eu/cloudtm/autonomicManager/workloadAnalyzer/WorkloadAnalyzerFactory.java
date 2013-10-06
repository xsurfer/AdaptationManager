package eu.cloudtm.autonomicManager.workloadAnalyzer;

import eu.cloudtm.autonomicManager.Optimizer;
import eu.cloudtm.autonomicManager.Reconfigurator;
import eu.cloudtm.autonomicManager.commons.EvaluatedParam;
import eu.cloudtm.autonomicManager.commons.Param;
import eu.cloudtm.autonomicManager.configs.Config;
import eu.cloudtm.autonomicManager.configs.KeyConfig;
import eu.cloudtm.autonomicManager.statistics.SampleProducer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Fabio Perfetti perfabio87 [at] gmail.com Date: 7/19/13 Time: 1:49 PM
 */
public class WorkloadAnalyzerFactory {

   private static Log log = LogFactory.getLog(WorkloadAnalyzerFactory.class);

   private double delta = Config.getInstance().getDouble(KeyConfig.CHANGE_DETECTOR_DELTA.key());

   private SampleProducer statsManager;
   private Reconfigurator reconfigurator;
   private Optimizer optimizer;

   Map<Param, Double> param2delta = new HashMap<Param, Double>() {{
      log.warn("TODO read params from config file");

      log.trace("CHANGE DETECTOR MONITOR:");
      log.trace("Param.AvgNumPutsBySuccessfulLocalTx, " + delta);
      put(Param.AvgNumPutsBySuccessfulLocalTx, delta);

      log.trace("Param.PercentageSuccessWriteTransactions, " + delta);
      put(Param.PercentageSuccessWriteTransactions, delta);

      log.trace("Param.LocalUpdateTxLocalServiceTime, " + delta);
      put(Param.LocalUpdateTxLocalServiceTime, delta);

      log.trace("Param.LocalReadOnlyTxLocalServiceTime, " + delta);
      put(Param.LocalReadOnlyTxLocalServiceTime, delta);

      log.trace("Param.AvgGetsPerWrTransaction, " + delta);
      put(Param.AvgGetsPerWrTransaction, delta);

      log.trace("Param.AvgGetsPerROTransaction, " + delta);
      put(Param.AvgGetsPerROTransaction, delta);
   }};

   Map<EvaluatedParam, Double> evaluatedParam2delta = new HashMap<EvaluatedParam, Double>() {{
      log.trace("Param.ACF, " + delta);
      put(EvaluatedParam.ACF, delta);
   }};

   public WorkloadAnalyzerFactory(SampleProducer statsManager,
                                  Reconfigurator reconfigurator,
                                  Optimizer optimizer) {
      this.statsManager = statsManager;
      this.reconfigurator = reconfigurator;
      this.optimizer = optimizer;

   }

   public WorkloadAnalyzer build() {

      WorkloadForecaster workloadForecaster = new WorkloadForecaster(
            param2delta.keySet(),
            evaluatedParam2delta.keySet()
      );

      AbstractChangeDetector proactiveChangeDetector = buildProactiveChangeDetector(workloadForecaster);
      /*      new ProactiveChangeDetector(
            param2delta,
            evaluatedParam2delta,
            workloadForecaster
      );  */

      AbstractChangeDetector reactiveChangeDetector = buildReactiveChangeDetector();
      /*
      new ReactiveChangeDetector(
            param2delta,
            evaluatedParam2delta
      );
       */


      String policy = Config.getInstance().getString(KeyConfig.ALERT_MANAGER_POLICY.key());
      AbstractAlertManager alertManager = AbstractAlertManager.createInstance(policy, optimizer, reconfigurator);

      if (proactiveChangeDetector != null) {
         proactiveChangeDetector.addEventListener(alertManager);
      }
      if (reactiveChangeDetector != null) {
         reactiveChangeDetector.addEventListener(alertManager);
      }

      boolean enabled = Config.getInstance().getBoolean(KeyConfig.WORKLOAD_ANALYZER_AUTOSTART.key());

      WorkloadAnalyzer workloadAnalyzer = new WorkloadAnalyzer(enabled,
                                                               statsManager,
                                                               reactiveChangeDetector,
                                                               proactiveChangeDetector,
                                                               alertManager);

      return workloadAnalyzer;
   }


   private AbstractChangeDetector buildReactiveChangeDetector() {
      if (Config.getInstance().isAlertManagerPolicyPureReactive() || Config.getInstance().isAlertManagerPolicyMix()) {
         return new ReactiveChangeDetector(param2delta, evaluatedParam2delta);
      }
      return new DummyChangeDetector_Periodic(param2delta, evaluatedParam2delta);
   }

   //This should preserve backward compatibility with Fabios' code which wants both the changedetectors
   private AbstractChangeDetector buildProactiveChangeDetector(WorkloadForecaster wf) {
      if (Config.getInstance().isAlertManagerPolicyProactive() || Config.getInstance().isAlertManagerPolicyMix() || Config.getInstance().isAlertManagerPolicyPureReactive())
         return new ProactiveChangeDetector(param2delta, evaluatedParam2delta, wf);
      return null;
   }

}
