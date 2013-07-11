package eu.cloudtm.statistics;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/8/13
 * Time: 3:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomSample extends WPMProcessedSample {

    private Map<WPMParam, Double> customAvgWPMParam;
    private Map<EvaluatedParam, Double> customEvaluatedParam;
    private WPMProcessedSample inputOracle;

    public CustomSample(WPMSample sample,
                        WPMProcessedSample _inputOracle,
                        Map<WPMParam, Double> _customAvgWPMParam,
                        Map<EvaluatedParam, Double> _evaluatedParam) {
        super(sample);
        inputOracle = _inputOracle;
        customAvgWPMParam = _customAvgWPMParam;
        customEvaluatedParam = _evaluatedParam;
    }

    @Override
    public double getParam(WPMParam param) {
        Double retVal = customAvgWPMParam.get(param);
        if(retVal==null) {
            retVal = sample.getParam(param);
        }
        return retVal;
    }

    @Override
    public double getACF() {
        Double retVal = customEvaluatedParam.get(EvaluatedParam.ACF);
        if(retVal==null) {
            retVal = inputOracle.getEvaluatedParam(EvaluatedParam.ACF);
        }
        return retVal;
    }


}
