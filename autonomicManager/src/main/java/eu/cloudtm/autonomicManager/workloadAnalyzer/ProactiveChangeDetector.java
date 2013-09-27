package eu.cloudtm.autonomicManager.workloadAnalyzer;

import eu.cloudtm.autonomicManager.commons.EvaluatedParam;
import eu.cloudtm.autonomicManager.commons.Param;
import eu.cloudtm.autonomicManager.statistics.ProcessedSample;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: Fabio Perfetti perfabio87 [at] gmail.com Date: 7/19/13 Time: 11:17 AM
 */
public class ProactiveChangeDetector extends AbstractChangeDetector {

   private WorkloadForecaster forecaster;
   private AtomicInteger counter = new AtomicInteger(0);

   public ProactiveChangeDetector(Map<Param, Double> monitoredParams2delta,
                                  Map<EvaluatedParam, Double> monitoredEvaluatedParams2delta,
                                  WorkloadForecaster forecaster) {
      super(monitoredParams2delta, monitoredEvaluatedParams2delta);
      this.forecaster = forecaster;
   }

   @Override
   public void samplePerformed(ProcessedSample sample) {
      counter.incrementAndGet();

      if (counter.get() < SLIDE_WINDOW_SIZE) {
         forecaster.add(sample);
         return;
      } else {
         add(forecaster.add(sample));
      }

      boolean reconfigure = evaluateParam() || evaluateEvaluatedParam();
      if (reconfigure) {
         fireEvent(WorkloadEvent.WorkloadEventType.WORKLOAD_WILL_CHANGE, sample);
      }
   }

}
