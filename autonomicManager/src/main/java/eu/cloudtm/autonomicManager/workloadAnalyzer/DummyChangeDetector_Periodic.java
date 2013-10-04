package eu.cloudtm.autonomicManager.workloadAnalyzer;

import eu.cloudtm.autonomicManager.commons.EvaluatedParam;
import eu.cloudtm.autonomicManager.commons.Param;

import java.util.Map;

/**
 * // TODO: Document this
 *
 * @author diego
 * @since 4.0
 */
public class DummyChangeDetector_Periodic extends ReactiveChangeDetector {

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

}
