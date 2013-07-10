package eu.cloudtm;

import eu.cloudtm.RESTServer.RESTServer;
import eu.cloudtm.commons.PlatformConfiguration;
import eu.cloudtm.commons.PlatformTuning;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 7/10/13
 */
public class ApplicationFactory {

    private AutonomicManager autonomicManager;

    private StatsManager statsManager;

    private PlatformConfiguration platformConfiguration;

    private PlatformTuning platformTuning;

    private RESTServer restServer;

    public AutonomicManager build(){

        statsManager = new StatsManager();
        platformConfiguration = new PlatformConfiguration();
        platformTuning = new PlatformTuning();

        restServer = new RESTServer();


        autonomicManager = new AutonomicManager();
        return autonomicManager;
    }




}
