package eu.cloudtm;

import eu.cloudtm.statistics.SampleDispatcher;
import eu.cloudtm.wpm.logService.remote.listeners.WPMStatisticsRemoteListener;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/10/13
 * Time: 5:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class WPMStatisticsRemoteListernerFactory {

    private static WPMStatisticsRemoteListernerFactory instance;

    private SampleDispatcher dispatcher;

    public static WPMStatisticsRemoteListernerFactory getInstance(){
        return instance;
    }

    public WPMStatisticsRemoteListernerFactory(SampleDispatcher dispatcher){
        this.dispatcher = dispatcher;
        this.instance = this;
    }

    public WPMStatisticsRemoteListener build(){
        return new WPMStatisticsRemoteListenerImpl(dispatcher);
    }
}
