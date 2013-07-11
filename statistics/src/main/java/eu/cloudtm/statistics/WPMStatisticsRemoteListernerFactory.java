package eu.cloudtm.statistics;

import eu.cloudtm.wpm.connector.WPMConnector;
import eu.cloudtm.wpm.logService.remote.events.SubscribeEvent;
import eu.cloudtm.wpm.logService.remote.listeners.WPMStatisticsRemoteListener;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/10/13
 * Time: 5:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class WPMStatisticsRemoteListernerFactory {

    private StatsManager dispatcher;
    private WPMConnector connector;

    public WPMStatisticsRemoteListernerFactory(WPMConnector connector, StatsManager dispatcher){
        this.connector = connector;
        this.dispatcher = dispatcher;
    }

    public WPMStatisticsRemoteListener build(SubscribeEvent subscribeEvent){
        return new WPMStatisticsRemoteListenerImpl(connector, dispatcher);
    }

}
