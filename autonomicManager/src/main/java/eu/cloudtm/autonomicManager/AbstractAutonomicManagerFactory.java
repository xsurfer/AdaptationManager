package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.RESTServer.RESTServer;
import eu.cloudtm.autonomicManager.workloadAnalyzer.WorkloadAnalyzer;
import eu.cloudtm.commons.IPlatformConfiguration;
import eu.cloudtm.commons.PlatformTuning;
import eu.cloudtm.commons.State;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/24/13
 * Time: 11:08 AM
 * To change this template use File | Settings | File Templates.
 */
public interface AbstractAutonomicManagerFactory {

    public AutonomicManager build();

    public State getPlatformState();

    public PlatformTuning getPlatformTuning();

    public IPlatformConfiguration getPlatformConfiguration();

    public IReconfigurator getReconfigurator();

    public AbstractOptimizer getOptimizer();

    public SLAManager getSLAManager();

    public WorkloadAnalyzer getWorkloadAnalyzer();

    public RESTServer getRESTServer();

}
