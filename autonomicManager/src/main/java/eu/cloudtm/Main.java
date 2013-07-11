package eu.cloudtm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/5/13
 */
public class Main {

    public static void main(String[] args) {

        ApplicationFactory appFactory = new ApplicationFactory();
        AutonomicManager autonomicManager = appFactory.build();

        autonomicManager.start();

    }
}
