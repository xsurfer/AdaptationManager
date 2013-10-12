package eu.cloudtm.autonomicManager.workloadAnalyzer;

import eu.cloudtm.autonomicManager.commons.EvaluatedParam;
import eu.cloudtm.autonomicManager.commons.Param;
import eu.cloudtm.autonomicManager.configs.Config;
import eu.cloudtm.autonomicManager.configs.KeyConfig;
import eu.cloudtm.autonomicManager.statistics.samples.CustomSample;
import eu.cloudtm.autonomicManager.statistics.ProcessedSample;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA. User: fabio Date: 7/19/13 Time: 11:03 AM To change this template use File | Settings |
 * File Templates.
 */
public class WorkloadForecaster {

   private static Log log = LogFactory.getLog(WorkloadForecaster.class);

   private static final int POLYNOMIAL_GRADE = Config.getInstance().getInt(KeyConfig.WORKLOAD_FORECASTER_GRADE.key());

   public static final int TIME_WINDOW = 10; // in seconds; it means that each 5 seconds I have a new sample
   public static final int RECONFIGURATION_TIME = 5 * 60; // in seconds, to divide for the TIME_WINDOW
   public static final int SAFE_TIME = 2 * 60; // in seconds, to divide for the TIME_WINDOW

   private Set<Param> monitoredParams;
   private Map<Param, PolynomialFitter> paramForecasters = new HashMap<Param, PolynomialFitter>();

   private Set<EvaluatedParam> monitoredEvaluatedParam;
   private Map<EvaluatedParam, PolynomialFitter> evaluatedParamForecasters = new HashMap<EvaluatedParam, PolynomialFitter>();

   private ProcessedSample lastSampleForecasted;

   public WorkloadForecaster(Set<Param> monitoredParams,
                             Set<EvaluatedParam> monitoredEvaluatedParam) {
      this.monitoredParams = monitoredParams;
      this.monitoredEvaluatedParam = monitoredEvaluatedParam;
      init();
   }

   private void init() {

      log.trace("POLYNOMIAL_GRADE: " + POLYNOMIAL_GRADE);

      for (Param param : monitoredParams) {
         PolynomialFitter forecaster = new PolynomialFitter(POLYNOMIAL_GRADE);
         paramForecasters.put(param, forecaster);
      }
      for (EvaluatedParam param : monitoredEvaluatedParam) {
         PolynomialFitter forecaster = new PolynomialFitter(POLYNOMIAL_GRADE);
         evaluatedParamForecasters.put(param, forecaster);
      }
   }

   public ProcessedSample add(ProcessedSample sample) {
      for (Param param : monitoredParams) {
         double value = ((Number) sample.getParam(param)).doubleValue();
         if (value < 0) {
            value = 0;
         }
         log.trace("Adding value to forecaster for " + param + ": " + value);
         PolynomialFitter forecaster = paramForecasters.get(param);
         forecaster.addPoint(sample.getId(), value);
      }

      for (EvaluatedParam param : monitoredEvaluatedParam) {
         PolynomialFitter forecaster = evaluatedParamForecasters.get(param);
         forecaster.addPoint(sample.getId(), (Double) sample.getEvaluatedParam(param));
      }

      lastSampleForecasted = new CustomSample(sample, createCustomMap(sample.getId()), createCustomEvaluatedMap(sample.getId()));
      return lastSampleForecasted;
   }

   private Map<Param, Object> createCustomMap(long instance) {
      Map<Param, Object> customMap = new HashMap<Param, Object>();

      for (Param param : monitoredParams) {
         PolynomialFitter forecaster = paramForecasters.get(param);
         double forecastedValue = forecaster.getBestFit().getY(timeToForecast(instance));
         log.trace("Forecasting (t=" + timeToForecast(instance) + ") for " + param + ": " + forecastedValue);
         customMap.put(param, forecastedValue);
      }

      return customMap;
   }

   private Map<EvaluatedParam, Object> createCustomEvaluatedMap(long instance) {
      Map<EvaluatedParam, Object> customEvaluatedMap = new HashMap<EvaluatedParam, Object>();

      for (EvaluatedParam param : monitoredEvaluatedParam) {
         PolynomialFitter forecaster = evaluatedParamForecasters.get(param);
         customEvaluatedMap.put(param, forecaster.getBestFit().getY(timeToForecast(instance)));
      }

      return customEvaluatedMap;
   }

   private double timeToForecast(long instance) {
      return ((double) instance + ((double) (RECONFIGURATION_TIME + SAFE_TIME) / TIME_WINDOW));
   }

}
