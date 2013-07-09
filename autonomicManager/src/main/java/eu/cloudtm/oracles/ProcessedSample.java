package eu.cloudtm.oracles;

import eu.cloudtm.stats.EvaluatedParam;
import eu.cloudtm.stats.Sample;
import eu.cloudtm.stats.WPMParam;
import eu.cloudtm.stats.WPMSample;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/8/13
 * Time: 5:31 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ProcessedSample implements Sample {

    protected WPMSample sample;

    private Map<EvaluatedParam, Double> evaluatedParams = new HashMap<EvaluatedParam, Double>();

    public ProcessedSample(WPMSample sample){
        this.sample = sample;
        init();
    }

    private void init(){
        // add here all the customizations
        evaluatedParams.put(EvaluatedParam.ACF, getACF());

    }

    @Override
    public Set<String> getNodes() {
        return sample.getNodes();
    }

    @Override
    public double getPerNodeParam(WPMParam param, int classIdx, String nodeIP) {
        return sample.getPerNodeParam(param, classIdx, nodeIP);
    }

    @Override
    public double getAvgParam(WPMParam param, int classIdx) {
        return sample.getAvgParam(param, classIdx);
    }


    public double getEvaluatedParam(EvaluatedParam param) {
        return evaluatedParams.get(param);
    }

    protected abstract double getACF();

}
