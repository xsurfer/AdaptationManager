package eu.cloudtm;

import eu.cloudtm.RESTServer.RESTServer;
import eu.cloudtm.controller.Controller;
import eu.cloudtm.controller.model.*;
import eu.cloudtm.controller.model.decorators.TuningDecorator;
import eu.cloudtm.controller.model.utils.*;
import eu.cloudtm.stats.WPMViewChangeRemoteListenerImpl;
import eu.cloudtm.wpm.connector.WPMConnector;
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

        PlatformConfiguration state = new PlatformConfiguration(2, InstanceConfig.SMALL, ReplicationProtocol.TWOPC, 2, true);

        StatsManager statsManager = new StatsManager();
        LookupRegister.registerStatsManager(statsManager);

        Controller controller = new Controller(state);
        LookupRegister.registerController(controller);

        RESTServer restServer = new RESTServer();
        LookupRegister.registerRESTServer(restServer);

        /* *** WPM CONNECTOR && LISTENER *** */
        WPMConnector connector;
        try {
            connector = new WPMConnector();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            connector.registerViewChangeRemoteListener(new WPMViewChangeRemoteListenerImpl(connector,statsManager));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        /* ***  END *** */

        restServer.startServer();

        int num = 0;
        while (num != 1000) {
            Scanner in = new Scanner(System.in);
            num = in.nextInt();
        }
        restServer.stopServer();
    }
}
