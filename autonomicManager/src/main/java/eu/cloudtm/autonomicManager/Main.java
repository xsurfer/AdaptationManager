package eu.cloudtm.autonomicManager;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/5/13
 */
public class Main {

    public static void main(String[] args) {

        Configuration configuration;

        try {
            configuration = new PropertiesConfiguration("config.properties");
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }

        ApplicationFactory appFactory = new ApplicationFactory(configuration);
        AutonomicManager autonomicManager = appFactory.build();

        autonomicManager.start();

    }
}
