package eu.cloudtm.autonomicManager.workloadAnalyzer;

import eu.cloudtm.autonomicManager.Reconfigurator;
import eu.cloudtm.commons.EvaluatedParam;
import eu.cloudtm.commons.Param;
import eu.cloudtm.statistics.SampleProducer;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/19/13
 * Time: 11:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProactiveChangeDetector extends ChangeDetector {

    public ProactiveChangeDetector(SampleProducer sampleProducer, WorkloadAdapter alertManager, Reconfigurator reconfigurator, Map<Param, Double> monitoredParams2delta, Map<EvaluatedParam, Double> monitoredEvaluatedParams2delta) {
        super(sampleProducer, alertManager, reconfigurator, monitoredParams2delta, monitoredEvaluatedParams2delta);
    }

    @Override
    public void dispatchEvent(WorkloadEvent e) {
        workloadAdapter.workloadWillChange(e);
    }
}
