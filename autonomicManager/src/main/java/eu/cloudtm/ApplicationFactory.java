package eu.cloudtm;

import eu.cloudtm.RESTServer.RESTServer;
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

    private WPMConnector connector;

    public AutonomicManager build(){

        platformConfiguration = new PlatformConfiguration();
        platformTuning = new PlatformTuning();

        wpmStatsManagerFactory = new WPMStatsManagerFactory(platformConfiguration);

        restServer = new RESTServer();

        wpmStatsManager = wpmStatsManagerFactory.build();

        AutonomicManager autonomicManager = new AutonomicManager(platformConfiguration, platformTuning, wpmStatsManager);
        return autonomicManager;
    }


}
