package eu.cloudtm.autonomicManager.workloadAnalyzer;

import eu.cloudtm.autonomicManager.commons.EvaluatedParam;
import eu.cloudtm.autonomicManager.commons.Param;
import eu.cloudtm.autonomicManager.configs.Config;
import eu.cloudtm.autonomicManager.statistics.ProcessedSample;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * // TODO: Document this
 *
 * @author diego
 * @since 4.0
 */
public class DummyChangeDetector_Periodic extends ReactiveChangeDetector {

   private static final Log log = LogFactory.getLog(DummyChangeDetector_Periodic.class);

   private final static int period = Config.getInstance().getEvaluationPeriod();
   private int tick = 0;

   public DummyChangeDetector_Periodic(Map<Param, Double> monitoredParams2delta, Map<EvaluatedParam, Double> monitoredEvaluatedParams2delta) {
      super(monitoredParams2delta, monitoredEvaluatedParams2delta);
   }

   @Override
   protected boolean evaluateParam() {
      return true;
   }

   @Override
   protected boolean evaluateEvaluatedParam() {
      return true;
   }

   @Override
   //Either we are not at steady-state (super) or it is not the right cycle to optimize
   //I put the < instead of the == because with == if "super.notEnoughTimeElapsed"  is true before the first time tick gets == period, then
   //this second predicate won't ever be true
   protected boolean notEnoughTimeElapsed() {
      return super.notEnoughTimeElapsed() ||  (tick < period);
   }

   @Override
   //Even if we do not optimize, we want that EVERY X samples we query the oracles
   protected void postFire() {
      tick = 0;
      log.trace("Event fired! Tick = " + tick);
   }

   @Override
   protected void onNewSamplePerformed(ProcessedSample sample) {
      super.onNewSamplePerformed(sample);
      tick++;
      log.trace("Tick " + tick);
   }
}
