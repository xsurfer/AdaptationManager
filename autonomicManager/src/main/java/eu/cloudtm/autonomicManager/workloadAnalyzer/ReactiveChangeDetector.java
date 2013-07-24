package eu.cloudtm.autonomicManager.workloadAnalyzer;

import eu.cloudtm.commons.EvaluatedParam;
import eu.cloudtm.commons.Param;
import eu.cloudtm.statistics.ProcessedSample;
import eu.cloudtm.statistics.SampleProducer;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/19/13
 * Time: 2:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReactiveChangeDetector extends ChangeDetector {

    public ReactiveChangeDetector(SampleProducer sampleProducer, Map<Param, Double> monitoredParams2delta, Map<EvaluatedParam, Double> monitoredEvaluatedParams2delta) {
        super(sampleProducer, monitoredParams2delta, monitoredEvaluatedParams2delta);
    }

    @Override
    public void onNewSample(ProcessedSample sample){
        add(sample);

        if(sampleSlideWindow.size() < SLIDE_WINDOW_SIZE){
            return;
        }

        boolean reconfigure = evaluateParam() || evaluateEvaluatedParam();
        if(reconfigure){
            fireEvent( WorkloadEvent.WorkloadEventType.WORKLOAD_CHANGED, sample );
        }
    }

}
