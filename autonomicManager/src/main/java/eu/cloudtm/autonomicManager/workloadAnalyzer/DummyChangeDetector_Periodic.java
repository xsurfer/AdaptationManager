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
   protected boolean notEnoughTimeElapsed() {
      return super.notEnoughTimeElapsed() && (tick == period);    // TODO: Customise this generated block
   }

   @Override
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
