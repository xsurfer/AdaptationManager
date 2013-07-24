package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.RESTServer.RESTServer;
import eu.cloudtm.autonomicManager.workloadAnalyzer.WorkloadAnalyzer;
import eu.cloudtm.autonomicManager.workloadAnalyzer.WorkloadAnalyzerFactory;
import eu.cloudtm.commons.*;
import eu.cloudtm.statistics.*;
import org.apache.commons.configuration.Configuration;

import java.io.IOException;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 7/10/13
 */
public class AutonomicManagerFactory implements AbstractAutonomicManagerFactory {

    private State platformState;
    private IReconfigurator reconfigurator;
    private PlatformTuning platformTuning;
    private PlatformConfiguration platformConfiguration;
    private AbstractOptimizer optimizer;
    private SLAManager slaManager;
    private WorkloadAnalyzer workloadAnalyzer;
    private RESTServer restServer;

    private StatsManager wpmStatsManager;

    private WPMStatsManagerFactory wpmStatsManagerFactory;
    //private WorkloadAnalyzerFactory workloadAnalyzerFactory;

    public AutonomicManagerFactory(){
    }


    public AutonomicManager build(){

        wpmStatsManager = new WPMStatsManagerFactory( getPlatformConfiguration() ).build();

        AutonomicManager autonomicManager = new AutonomicManager(
                getPlatformConfiguration(),
                getPlatformTuning(),
                wpmStatsManager,
                getWorkloadAnalyzer(),
                getOptimizer(),
                getReconfigurator());

        try {
            getRESTServer().startServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return autonomicManager;
    }

    @Override
    public State getPlatformState() {
        if( this.platformState == null ){
            this.platformState = new State(PlatformState.RUNNING);
        }
        return this.platformState;
    }

    @Override
    public PlatformTuning getPlatformTuning() {
        if( this.platformTuning==null ) {
            this.platformTuning= new PlatformTuning(Forecaster.ANALYTICAL, true);
        }
        return this.platformTuning;
    }

    @Override
    public IPlatformConfiguration getPlatformConfiguration() {
        if( this.platformConfiguration == null ){
            this.platformConfiguration = new PlatformConfiguration();
        }
        return this.platformConfiguration;
    }

    @Override
    public IReconfigurator getReconfigurator() {
        if( this.reconfigurator == null ){
            this.reconfigurator = new Reconfigurator( getPlatformConfiguration(), getPlatformState());
        }
        return reconfigurator;
    }

    @Override
    public AbstractOptimizer getOptimizer() {

        if( this.optimizer == null ){
            this.optimizer = new MuleOptimizer(getReconfigurator(), getSLAManager(), getPlatformConfiguration() ,getPlatformTuning());
        }
        return this.optimizer;
    }

    @Override
    public SLAManager getSLAManager() {
        if ( this.slaManager == null ){
            this.slaManager = new SLAManager();
        }
        return this.slaManager;
    }

    @Override
    public WorkloadAnalyzer getWorkloadAnalyzer() {
        if( this.workloadAnalyzer == null ){
            WorkloadAnalyzerFactory workloadAnalyzerFactory = new WorkloadAnalyzerFactory((SampleProducer) wpmStatsManager, getReconfigurator(), getOptimizer());
            this.workloadAnalyzer = workloadAnalyzerFactory.build();
        }
        return this.workloadAnalyzer;
    }

    @Override
    public RESTServer getRESTServer() {
        if( this.restServer == null ){
            this.restServer = new RESTServer(wpmStatsManager);
        }
        return this.restServer;
    }


}