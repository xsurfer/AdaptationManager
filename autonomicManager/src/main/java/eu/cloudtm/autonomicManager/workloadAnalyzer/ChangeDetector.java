package eu.cloudtm.autonomicManager.workloadAnalyzer;

import eu.cloudtm.commons.EvaluatedParam;
import eu.cloudtm.commons.Param;
import eu.cloudtm.statistics.ProcessedSample;
import eu.cloudtm.statistics.SampleProducer;
import org.apache.commons.collections15.Buffer;
import org.apache.commons.collections15.BufferUtils;
import org.apache.commons.collections15.buffer.CircularFifoBuffer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/19/13
 * Time: 11:24 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ChangeDetector implements IChangeDetector {

    private static Log log = LogFactory.getLog(ChangeDetector.class);

    protected static final int SLIDE_WINDOW_SIZE = 30;

    protected Buffer<ProcessedSample> sampleSlideWindow = BufferUtils.synchronizedBuffer(new CircularFifoBuffer<ProcessedSample>(SLIDE_WINDOW_SIZE));

    private List<WorkloadEventListener> listeners = new ArrayList<WorkloadEventListener>();

    private Map<Param, Double> monitoredParams2delta = new HashMap<Param, Double>();
    private Map<EvaluatedParam, Double> monitoredEvaluatedParams2delta = new HashMap<EvaluatedParam, Double>();

    private Map<Param, Double> lastAvgParams = new HashMap<Param, Double>();
    private Map<EvaluatedParam, Double> lastAvgEvaluatedParams = new HashMap<EvaluatedParam, Double>();



    public ChangeDetector(SampleProducer sampleProducer,
                          Map<Param, Double> monitoredParams2delta,
                          Map<EvaluatedParam, Double> monitoredEvaluatedParams2delta){
        sampleProducer.addListener(this);
        this.monitoredParams2delta = monitoredParams2delta;
        this.monitoredEvaluatedParams2delta = monitoredEvaluatedParams2delta;
        init();
    }


    private void init(){
        for(Param param : monitoredParams2delta.keySet()){
            lastAvgParams.put(param, 0.0);
        }
        for(EvaluatedParam evaluatedParam : monitoredEvaluatedParams2delta.keySet()){
            lastAvgEvaluatedParams.put(evaluatedParam, 0.0);
        }
    }

    protected boolean evaluateParam(){
        for(Param param : monitoredParams2delta.keySet()){
            double sum = 0.0;
            for (ProcessedSample sample : sampleSlideWindow){
                sum += (Double) sample.getParam( param );
            }
            double currentAvg =  sum / ((double) sampleSlideWindow.size());
            log.debug("currentAvg: " + currentAvg);
            log.debug("lastAvg: " + lastAvgParams.get(param));

            if(lastAvgParams.get(param) == 0 || lastAvgParams.get(param) == Double.NaN){
                log.debug("Updating && Skipping lastAvgParams");
                lastAvgParams.put(param, currentAvg);
            } else {
                double ratio = ( currentAvg / lastAvgParams.get(param) ) * 100;
                log.debug("ratio: " + ratio );

                double variation = Math.abs(ratio - 100);
                log.debug("variation: " + variation );

                if( variation >= monitoredParams2delta.get(param) ){
                    log.trace("Update the lastAvgAbortRate");
                    lastAvgParams.put(param, currentAvg);
                    log.info("BOUND REACHED (AbortRate)");
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean evaluateEvaluatedParam(){
        for(EvaluatedParam param : monitoredEvaluatedParams2delta.keySet()){
            double sum = 0.0;
            for (ProcessedSample sample : sampleSlideWindow){
                sum += (Double) sample.getEvaluatedParam( param );
            }
            double currentAvg =  sum / ((double) sampleSlideWindow.size());
            log.debug("currentAvg: " + currentAvg);
            log.debug("lastAvg: " + lastAvgEvaluatedParams.get(param));

            if(lastAvgEvaluatedParams.get(param) == 0 || lastAvgEvaluatedParams.get(param) == Double.NaN){
                log.debug("Updating && Skipping lastAvgEvaluatedParams");
                lastAvgEvaluatedParams.put(param, currentAvg);
            } else {
                double ratio = ( currentAvg / lastAvgEvaluatedParams.get(param) ) * 100;
                log.debug("ratio: " + ratio );

                double variation = Math.abs(ratio - 100);
                log.debug("variation: " + variation );

                if( variation >= monitoredParams2delta.get(param) ){
                    log.trace("Update the lastAvgAbortRate");
                    lastAvgEvaluatedParams.put(param, currentAvg);
                    log.info("BOUND REACHED (AbortRate)");
                    return true;
                }
            }
        }
        return false;
    }

    protected void add(ProcessedSample sample){
        sampleSlideWindow.add(sample);
    }

    public synchronized void addEventListener(WorkloadEventListener listener)  {
        listeners.add(listener);
    }

    public synchronized void removeEventListener(WorkloadEventListener listener)   {
        listeners.remove(listener);
    }

    protected synchronized void fireEvent(WorkloadEvent.WorkloadEventType type, ProcessedSample sample) {
        WorkloadEvent event = new WorkloadEvent(this, type, sample);
        Iterator<WorkloadEventListener> i = listeners.iterator();
        while(i.hasNext())  {
            i.next().workloadEventPerformed(event);
        }
    }



}
