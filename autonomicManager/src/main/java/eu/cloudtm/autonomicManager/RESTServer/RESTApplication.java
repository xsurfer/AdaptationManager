package eu.cloudtm.autonomicManager.RESTServer;

import eu.cloudtm.autonomicManager.AutonomicManager;
import eu.cloudtm.autonomicManager.statistics.StatsManager;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 7/2/13
 */
public class RESTApplication extends ResourceConfig {

    public RESTApplication(StatsManager statsManager, AutonomicManager autonomicManager) {
        register(new Binder(statsManager, autonomicManager));
        packages(true, "eu.cloudtm.autonomicManager.RESTServer.resources");
    }

}

