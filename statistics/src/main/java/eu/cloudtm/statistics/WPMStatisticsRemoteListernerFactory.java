package eu.cloudtm.statistics;

import eu.cloudtm.commons.IPlatformConfiguration;
import eu.cloudtm.commons.PlatformConfiguration;
import eu.cloudtm.wpm.connector.WPMConnector;
import eu.cloudtm.wpm.logService.remote.events.SubscribeEvent;
import eu.cloudtm.wpm.logService.remote.observables.Handle;

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
    private IPlatformConfiguration currentConfig;

    public WPMStatisticsRemoteListernerFactory(WPMConnector connector, StatsManager dispatcher, IPlatformConfiguration currentConfig){
        this.connector = connector;
        this.dispatcher = dispatcher;
        this.currentConfig = currentConfig;
    }

    public Handle build(SubscribeEvent subscribeEvent){
        StatsProcessor processor = new StatsProcessor(currentConfig);
        return new WPMStatisticsRemoteListenerImpl(connector, dispatcher, subscribeEvent, processor).getHandle();
    }

}
