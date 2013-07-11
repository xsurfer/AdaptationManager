package eu.cloudtm.statistics;

import eu.cloudtm.wpm.connector.WPMConnector;
import eu.cloudtm.wpm.logService.remote.listeners.WPMViewChangeRemoteListener;

import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/11/13
 * Time: 6:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class WPMStatsManagerFactory {

    private WPMStatisticsRemoteListernerFactory statisticsRemoteListernerFactory;

    public WPMStatsManagerFactory(){

    }

    public WPMStatsManager build(){

        WPMConnector connector = null;
        try {
            connector = new WPMConnector();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }

        WPMViewChangeRemoteListener viewChangeRemoteListener = new WPMViewChangeRemoteListenerImpl(connector);

        WPMStatsManager wpmStatsManager = new WPMStatsManager(viewChangeRemoteListener);
        this.statisticsRemoteListernerFactory =
                new WPMStatisticsRemoteListernerFactory(connector, wpmStatsManager);

        return wpmStatsManager;
    }

    public WPMStatisticsRemoteListernerFactory getStatisticsRemoteListernerFactory(){
        return statisticsRemoteListernerFactory;
    }

}
