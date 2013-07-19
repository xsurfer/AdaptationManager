package eu.cloudtm.autonomicManager.workloadAnalyzer;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/19/13
 * Time: 11:01 AM
 * To change this template use File | Settings | File Templates.
 */
public interface WorkloadAdapter {

    public void workloadChanged(WorkloadEvent e);

    public void workloadWillChange(WorkloadEvent e);

}
