package eu.cloudtm.autonomicManager.workloadAnalyzer;

import eu.cloudtm.statistics.ProcessedSample;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/19/13
 * Time: 11:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class WorkloadEvent {

    private final long timestamp;
    private final Object source;
    private final ProcessedSample sample;

    public WorkloadEvent(Object source, ProcessedSample sample){
        this.timestamp = System.currentTimeMillis();
        this.source = source;
        this.sample = sample;
    }

    public ProcessedSample getSample(){
        return sample;
    }

    public long getTimestamp(){
        return timestamp;
    }

    public Object getSource(){
        return source;
    }
}
