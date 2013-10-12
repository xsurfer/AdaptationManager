package eu.cloudtm.autonomicManager.workloadAnalyzer;

import eu.cloudtm.autonomicManager.commons.EvaluatedParam;
import eu.cloudtm.autonomicManager.commons.Param;
import eu.cloudtm.autonomicManager.statistics.ProcessedSample;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.Map;

/**
 * User: Fabio Perfetti perfabio87 [at] gmail.com Date: 7/19/13 Time: 2:06 PM
 */
public class ReactiveChangeDetector extends AbstractChangeDetector {

   private static final Log log = LogFactory.getLog(ReactiveChangeDetector.class);

   public ReactiveChangeDetector(Map<Param, Double> monitoredParams2delta, Map<EvaluatedParam, Double> monitoredEvaluatedParams2delta) {
      super(monitoredParams2delta, monitoredEvaluatedParams2delta);
   }

   @Override
   public final void samplePerformed(ProcessedSample sample) {

      onNewSamplePerformed(sample);

      if (notEnoughTimeElapsed()) {
         log.trace("Not enough time has passed to start reconfiguring the system.");
         return;
      }

      boolean reconfigure = evaluateParam() || evaluateEvaluatedParam();
      if (reconfigure) {
         log.trace("Going to trigger a reconfiguration");
         try {
            fireEvent(WorkloadEvent.WorkloadEventType.WORKLOAD_CHANGED, sample);
            postFire();
         } catch (Exception e) {
            log.trace(e);
            log.trace(Arrays.toString(e.getStackTrace()));
         }
      }


   }

   protected boolean notEnoughTimeElapsed() {
      return sampleSlideWindow.size() < SLIDE_WINDOW_SIZE;
   }

   protected void onNewSamplePerformed(ProcessedSample sample) {
      add(sample);
   }

   protected void postFire() {
      log.trace("NO-OP");
   }

}
