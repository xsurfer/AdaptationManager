package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.RESTServer.RESTServer;
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

    private WPMStatsManager wpmStatsManager;
    private PlatformConfiguration platformConfiguration;
    private PlatformTuning platformTuning;
    private RESTServer restServer;


    public AutonomicManager build(){
        platformConfiguration = new PlatformConfiguration();
        platformTuning = new PlatformTuning(Forecaster.ANALYTICAL, true);

        wpmStatsManagerFactory = new WPMStatsManagerFactory(platformConfiguration);

        wpmStatsManager = wpmStatsManagerFactory.build();



        Reconfigurator reconfigurator = new Reconfigurator();
        Optimizer optimizer = new Optimizer(reconfigurator, platformConfiguration ,platformTuning);
        InputFilter inputFilter = new InputFilter(wpmStatsManager, optimizer);


        restServer = new RESTServer(wpmStatsManager);


        AutonomicManager autonomicManager = new AutonomicManager(
                platformConfiguration,
                platformTuning,
                wpmStatsManager,
                inputFilter,
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
