package eu.cloudtm;

import eu.cloudtm.RESTServer.RESTServer;
import eu.cloudtm.controller.Controller;
import eu.cloudtm.controller.ControllerLogger;
import eu.cloudtm.controller.model.*;
import eu.cloudtm.controller.model.utils.*;
import eu.cloudtm.stats.WPMStatisticsRemoteListenerImpl;
import eu.cloudtm.stats.WPMViewChangeRemoteListenerImpl;
import eu.cloudtm.wpm.connector.WPMConnector;
import eu.cloudtm.wpm.logService.remote.listeners.WPMStatisticsRemoteListener;
import eu.cloudtm.wpm.logService.remote.listeners.WPMViewChangeRemoteListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.RemoteException;
import java.util.Scanner;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/5/13
 */
public class Main {

    private static Log log = LogFactory.getLog(Main.class);

    public static void main(String[] args) {



        PlatformConfiguration configuration = new PlatformConfiguration(2, 2, InstanceConfig.SMALL, ReplicationProtocol.TWOPC, 2, true);

        StatsManager statsManager = StatsManager.getInstance();
        Controller controller = Controller.getInstance(configuration, statsManager);
        RESTServer restServer = new RESTServer();

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

        restServer.startServer();

        /*
        int num = 0;
        while (num != 1000) {
            Scanner in = new Scanner(System.in);
            num = in.nextInt();
        }
        restServer.stopServer();
        */
    }
}
