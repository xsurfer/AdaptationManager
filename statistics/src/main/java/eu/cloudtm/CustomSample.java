package eu.cloudtm;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/8/13
 * Time: 3:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomSample extends ProcessedSample {

    private Map<WPMParam, Double> customAvgWPMParam;
    private Map<EvaluatedParam, Double> customEvaluatedParam;
    private ProcessedSample inputOracle;

    public CustomSample(WPMSample sample,
                        ProcessedSample _inputOracle,
                        Map<WPMParam, Double> _customAvgWPMParam,
                        Map<EvaluatedParam, Double> _evaluatedParam) {
        super(sample);
        inputOracle = _inputOracle;
        customAvgWPMParam = _customAvgWPMParam;
        customEvaluatedParam = _evaluatedParam;
    }

    @Override
    public double getPerNodeParam(WPMParam param, int classIdx, String nodeIP) {
        return sample.getPerNodeParam(param, classIdx, nodeIP);
    }

    @Override
    public double getAvgParam(WPMParam param, int classIdx) {
        Double retVal = customAvgWPMParam.get(param);
        if(retVal==null) {
            retVal = sample.getAvgParam(param, classIdx);
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
