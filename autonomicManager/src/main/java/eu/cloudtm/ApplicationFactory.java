package eu.cloudtm;

import eu.cloudtm.RESTServer.RESTServer;
import eu.cloudtm.commons.Forecaster;
import eu.cloudtm.commons.PlatformConfiguration;
import eu.cloudtm.commons.PlatformTuning;
import eu.cloudtm.statistics.*;
import eu.cloudtm.wpm.connector.WPMConnector;

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

        return autonomicManager;
    }


}
