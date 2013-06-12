package eu.cloudtm;

import eu.cloudtm.RESTServer.RESTServer;
import eu.cloudtm.controller.Controller;
import eu.cloudtm.controller.model.*;
import eu.cloudtm.controller.model.utils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import java.util.Scanner;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/5/13
 */
public class Main {

    private static Log log = LogFactory.getLog(Main.class);

    public static void main(String[] args) {


        Scale scale = new Scale(TuningType.SELF, Forecaster.ANALYTICAL, 2, InstanceConfigurations.MEDIUM);
        ReplicationProtocol repProtocol = new ReplicationProtocol(TuningType.SELF, Forecaster.ANALYTICAL, ReplicationProtocols.TWOPC);
        ReplicationDegree repDegree = new ReplicationDegree(TuningType.SELF, Forecaster.ANALYTICAL, 2);
        Placement placement = new Placement();

        PlatformConfiguration state = new PlatformConfiguration(PlatformState.WORKING,
                                            scale,
                                            repProtocol,
                                            repDegree,
                                            placement
                                            );

        StatsManager statsManager = new StatsManager();
        Controller controller = new Controller(state);
        RESTServer restServer = new RESTServer();

        LookupRegister.registerController(controller);
        LookupRegister.registerRESTServer(restServer);
        LookupRegister.registerStatsManager(statsManager);

        controller.start();
        restServer.startServer();

        int num = 0;
        while (num != 1000) {
            Scanner in = new Scanner(System.in);
            num = in.nextInt();
        }
        restServer.stopServer();
    }
}
