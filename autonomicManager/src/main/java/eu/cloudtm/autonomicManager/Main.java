package eu.cloudtm.autonomicManager;

import eu.cloudtm.statistics.WPMStatsManager;
import eu.cloudtm.statistics.WPMStatsManagerFactory;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/5/13
 */
public class Main {

    public static void main(String[] args) {

        AutonomicManagerFactory appFactory = new AutonomicManagerFactory();
        AutonomicManager autonomicManager = appFactory.build();

        autonomicManager.start();

    }
}
