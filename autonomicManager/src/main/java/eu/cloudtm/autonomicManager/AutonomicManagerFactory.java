package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.RESTServer.RESTServer;
import eu.cloudtm.autonomicManager.actuators.ActuatorFactory;
import eu.cloudtm.autonomicManager.commons.*;
import eu.cloudtm.autonomicManager.optimizers.MulePlatformOptimizer;
import eu.cloudtm.autonomicManager.statistics.SampleProducer;
import eu.cloudtm.autonomicManager.statistics.StatsManager;
import eu.cloudtm.autonomicManager.statistics.WPMStatsManagerFactory;
import eu.cloudtm.autonomicManager.workloadAnalyzer.WorkloadAnalyzer;
import eu.cloudtm.autonomicManager.workloadAnalyzer.WorkloadAnalyzerFactory;

import java.io.IOException;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 7/10/13
 */
public class AutonomicManagerFactory implements AbstractAutonomicManagerFactory {

    private ActuatorFactory actuatorFactory = new ActuatorFactory();

    private State platformState = new State(PlatformState.RUNNING);
    private Reconfigurator reconfigurator;
    private PlatformTuning platformTuning = new PlatformTuning(Forecaster.ANALYTICAL, true);
    private PlatformConfiguration platformConfiguration;
    private AbstractPlatformOptimizer optimizer;
    private SLAManager slaManager;
    private WorkloadAnalyzer workloadAnalyzer;
    private RESTServer restServer;

    private StatsManager wpmStatsManager;

    public AutonomicManagerFactory(){
    }


    public AutonomicManager build(){

        wpmStatsManager = new WPMStatsManagerFactory( getPlatformConfiguration() ).build();

        AutonomicManager autonomicManager = new AutonomicManager(
                getPlatformConfiguration(),
                platformTuning,
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
    public PlatformConfiguration getPlatformConfiguration() {
        if( this.platformConfiguration == null ){
            this.platformConfiguration = new PlatformConfiguration();
        }
        return this.platformConfiguration;
    }


    @Override
    public Reconfigurator getReconfigurator() {
        if( this.reconfigurator == null ){
            this.reconfigurator = new ReconfiguratorImpl( getPlatformConfiguration(), platformState, actuatorFactory.build()  );
        }
        return reconfigurator;
    }

    @Override
    public AbstractPlatformOptimizer getOptimizer() {

        if( this.optimizer == null ){
            this.optimizer = new MulePlatformOptimizer(getReconfigurator(), getSLAManager(), getPlatformConfiguration() ,platformTuning);
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