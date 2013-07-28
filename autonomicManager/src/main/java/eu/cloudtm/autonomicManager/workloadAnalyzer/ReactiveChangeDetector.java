package eu.cloudtm.autonomicManager.workloadAnalyzer;

import eu.cloudtm.autonomicManager.commons.EvaluatedParam;
import eu.cloudtm.autonomicManager.commons.Param;
import eu.cloudtm.autonomicManager.statistics.ProcessedSample;
import eu.cloudtm.autonomicManager.statistics.SampleProducer;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/19/13
 * Time: 2:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReactiveChangeDetector extends AbstractChangeDetector {

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
