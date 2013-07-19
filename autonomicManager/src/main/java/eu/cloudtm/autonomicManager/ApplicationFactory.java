package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.RESTServer.RESTServer;
import eu.cloudtm.autonomicManager.workloadAnalyzer.WorkloadAnalyzer;
import eu.cloudtm.autonomicManager.workloadAnalyzer.WorkloadAnalyzerFactory;
import eu.cloudtm.commons.Forecaster;
import eu.cloudtm.commons.PlatformConfiguration;
import eu.cloudtm.commons.PlatformTuning;
import eu.cloudtm.statistics.*;

import java.io.IOException;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 7/10/13
 */
public class ApplicationFactory {

    WPMStatsManagerFactory wpmStatsManagerFactory;
    WorkloadAnalyzerFactory workloadAnalyzerFactory;

    public AutonomicManager build(){
        PlatformConfiguration platformConfiguration = new PlatformConfiguration();
        PlatformTuning platformTuning = new PlatformTuning(Forecaster.ANALYTICAL, true);

        wpmStatsManagerFactory = new WPMStatsManagerFactory(platformConfiguration);


        WPMStatsManager wpmStatsManager = wpmStatsManagerFactory.build();



        Reconfigurator reconfigurator = new Reconfigurator(platformConfiguration);
        Optimizer optimizer = new Optimizer(reconfigurator, platformConfiguration ,platformTuning);

        workloadAnalyzerFactory = new WorkloadAnalyzerFactory(wpmStatsManager, reconfigurator);
        WorkloadAnalyzer workloadAnalyzer = workloadAnalyzerFactory.build();


        RESTServer restServer = new RESTServer(wpmStatsManager);


        AutonomicManager autonomicManager = new AutonomicManager(
                platformConfiguration,
                platformTuning,
                wpmStatsManager,
                workloadAnalyzer,
                optimizer,
                reconfigurator);

        try {
            restServer.startServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return autonomicManager;
    }


}
