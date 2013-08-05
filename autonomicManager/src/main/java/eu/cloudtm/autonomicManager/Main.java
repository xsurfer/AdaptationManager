package eu.cloudtm.autonomicManager;

import org.apache.log4j.PropertyConfigurator;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/5/13
 */
public class Main {

    public static void main(String[] args) {

        PropertyConfigurator.configure("config/log4j.properties");

        AutonomicManagerFactory appFactory = new AutonomicManagerFactory();
        AutonomicManager autonomicManager = appFactory.build();

        autonomicManager.start();

    }
}
