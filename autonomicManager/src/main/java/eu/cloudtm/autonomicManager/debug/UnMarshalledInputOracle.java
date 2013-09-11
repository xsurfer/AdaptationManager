package eu.cloudtm.autonomicManager.debug;

import eu.cloudtm.autonomicManager.commons.EvaluatedParam;
import eu.cloudtm.autonomicManager.commons.ForecastParam;
import eu.cloudtm.autonomicManager.commons.Param;
import eu.cloudtm.autonomicManager.oracles.InputOracle;

import java.util.Map;

/**
 * // TODO: Document this
 *
 * @author diego
 * @since 4.0
 */
public class UnMarshalledInputOracle implements InputOracle {


   private Map<Param, Object> paramMap;
   private Map<EvaluatedParam, Object> evalMap;
   private Map<ForecastParam, Object> forecastMap;

   public UnMarshalledInputOracle(Map<Param, Object> paramMap, Map<EvaluatedParam, Object> evalMap, Map<ForecastParam, Object> forecastMap) {
      this.paramMap = paramMap;
      this.evalMap = evalMap;
      this.forecastMap = forecastMap;
   }

   @Override
   public Object getParam(Param param) {
      return paramMap.get(param);
   }

   @Override
   public Object getEvaluatedParam(EvaluatedParam param) {
      return evalMap.get(param);
   }

   @Override
   public Object getForecastParam(ForecastParam param) {
      return forecastMap.get(param);
   }

   @Override
   public String toString() {
      return "UnMarshalledInputOracle{" +
            "paramMap=" + paramMap.toString() +
            ", evalMap=" + evalMap.toString() +
            ", forecastMap=" + forecastMap.toString() +
            '}';
   }
}
