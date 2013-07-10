package eu.cloudtm;

import eu.cloudtm.RESTServer.RESTServer;
import eu.cloudtm.commons.InstanceConfig;
import eu.cloudtm.commons.PlatformConfiguration;
import eu.cloudtm.commons.ReplicationProtocol;
import eu.cloudtm.stats.WPMStatisticsRemoteListenerImpl;
import eu.cloudtm.stats.WPMViewChangeRemoteListenerImpl;
import eu.cloudtm.wpm.connector.WPMConnector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.RemoteException;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/5/13
 */
public class Main {

    private static Log log = LogFactory.getLog(Main.class);

    public static void main(String[] args) {


        ApplicationFactory appFactory = new ApplicationFactory();
        AutonomicManager autonomicManager = appFactory.build();

        autonomicManager.start();

        /* *** WPM CONNECTOR && LISTENER *** */
        WPMConnector connector;
        try {
            connector = new WPMConnector();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            WPMStatisticsRemoteListenerImpl statisticsListener = new WPMStatisticsRemoteListenerImpl();
            statisticsListener.addSampleListener(controller);
            statisticsListener.addSampleListener(statsManager);

            WPMViewChangeRemoteListenerImpl viewChangeListener = new WPMViewChangeRemoteListenerImpl(connector, statisticsListener);

            connector.registerViewChangeRemoteListener( viewChangeListener );
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        /* ***  END *** */

    }
}
