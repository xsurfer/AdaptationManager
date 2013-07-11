package eu.cloudtm.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/8/13
 * Time: 5:31 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class WPMProcessedSample implements Sample {

    protected WPMSample sample;

    private Map<EvaluatedParam, Double> evaluatedParams = new HashMap<EvaluatedParam, Double>();

    public WPMProcessedSample(WPMSample sample){
        this.sample = sample;
        init();
    }

    @Override
    public long getId() {
        return sample.getId();
    }

    private void init(){
        // add here all the customizations
        evaluatedParams.put(EvaluatedParam.ACF, getACF());

    }

    @Override
    public double getParam(WPMParam param) {
        return sample.getParam(param);
    }


    public double getEvaluatedParam(EvaluatedParam param) {
        return evaluatedParams.get(param);
    }

    protected abstract double getACF();

}
