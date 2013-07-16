package eu.cloudtm.statistics;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/8/13
 * Time: 3:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomSample extends ProcessedSample {

    private ProcessedSample sample;
    private Map<Param, Double> customParam;
    private Map<EvaluatedParam, Double> customEvaluatedParam;

    public CustomSample(ProcessedSample sample, Map<Param, Double> customParam, Map<EvaluatedParam, Double> customEvaluatedParam ) {
        super(sample);
        this.sample = sample;
        this.customParam = customParam;
        this.customEvaluatedParam = customEvaluatedParam;
    }

    @Override
    public Object getParam(Param param) {
        Object retVal = customParam.get(param);
        if(retVal==null) {
            retVal = sample.getParam(param);
        }
        return retVal;
    }

    @Override
    public double getACF() {
        Double retVal = customEvaluatedParam.get(EvaluatedParam.ACF);
        if(retVal==null) {
            retVal = sample.getEvaluatedParam(EvaluatedParam.ACF);
        }
        return retVal;
    }

}
