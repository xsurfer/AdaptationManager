package eu.cloudtm;

import eu.cloudtm.RESTServer.RESTServer;
import eu.cloudtm.commons.PlatformConfiguration;
import eu.cloudtm.commons.PlatformTuning;
import eu.cloudtm.statistics.SampleDispatcher;
import eu.cloudtm.wpm.connector.WPMConnector;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 7/10/13
 */
public class ApplicationFactory {


    private SampleDispatcher statsManager;

    private PlatformConfiguration platformConfiguration;

    private PlatformTuning platformTuning;

    private RESTServer restServer;

    private WPMConnector connector;

    public AutonomicManager build(){

        statsManager = new SampleManager();
        platformConfiguration = new PlatformConfiguration();
        platformTuning = new PlatformTuning();

        restServer = new RESTServer();

        buildWpmListener();

        AutonomicManager autonomicManager = new AutonomicManager(platformConfiguration, platformTuning, statsManager);
        return autonomicManager;
    }

    private void buildWpmListener(){
        try {
            connector = new WPMConnector();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        WPMViewChangeRemoteListenerImpl viewChangeListener = new WPMViewChangeRemoteListenerImpl(connector);
        WPMStatisticsRemoteListernerFactory WPMStatsfactory = new WPMStatisticsRemoteListernerFactory(statsManager);
    }




}
