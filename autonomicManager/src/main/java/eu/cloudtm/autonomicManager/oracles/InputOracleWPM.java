package eu.cloudtm.autonomicManager.oracles;

import eu.cloudtm.commons.EvaluatedParam;
import eu.cloudtm.commons.ForecastParam;
import eu.cloudtm.oracles.InputOracle;
import eu.cloudtm.commons.Param;
import eu.cloudtm.statistics.ProcessedSample;

import java.util.Map;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 7/10/13
 */
public class InputOracleWPM implements InputOracle {

    private ProcessedSample processedSample;
    private Map<ForecastParam, Object> forecastParam;

    public InputOracleWPM(ProcessedSample processedSample, Map<ForecastParam, Object> forecastParam) {
        this.processedSample = processedSample;
        this.forecastParam = forecastParam;
    }

    @Override
    public Object getParam(Param param) {
        return processedSample.getParam(param);
    }

    @Override
    public Object getForecastParam(ForecastParam param) {
        return forecastParam.get(param);
    }

    @Override
    public Object getEvaluatedParam(EvaluatedParam param){
        return processedSample.getEvaluatedParam(param);
    }

}
