package eu.cloudtm;

import eu.cloudtm.RESTServer.RESTServer;
import eu.cloudtm.controller.Controller;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/5/13
 */
public class LookupRegister {

    private static Controller controller;
    private static RESTServer restServer;
    private static StatsManager statsManager;

    public static void registerController(Controller _controller){
        controller = _controller;
    }

    public static void registerRESTServer(RESTServer _restServer){
        restServer = _restServer;
    }

    public static void registerStatsManager(StatsManager _statsManager){
        statsManager = _statsManager;
    }

    public static Controller getController(){return controller; }

    public static RESTServer getRESTServer(){return restServer; }

    public static StatsManager getStatsManager(){return statsManager; }

}
