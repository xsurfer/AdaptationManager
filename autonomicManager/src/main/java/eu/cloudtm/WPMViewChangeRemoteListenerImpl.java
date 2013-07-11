package eu.cloudtm;

import eu.cloudtm.wpm.connector.WPMConnector;
import eu.cloudtm.wpm.logService.remote.events.PublishViewChangeEvent;
import eu.cloudtm.wpm.logService.remote.events.SubscribeEvent;
import eu.cloudtm.wpm.logService.remote.listeners.WPMStatisticsRemoteListener;
import eu.cloudtm.wpm.logService.remote.listeners.WPMViewChangeRemoteListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.RemoteException;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/10/13
 * Time: 1:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class WPMViewChangeRemoteListenerImpl implements WPMViewChangeRemoteListener {

    private static Log log = LogFactory.getLog(WPMViewChangeRemoteListenerImpl.class);

    private WPMConnector connector;

    private String[] currentVMs;

    private WPMStatisticsRemoteListener statisticsListener;

    public WPMViewChangeRemoteListenerImpl(WPMConnector connector){
        this.connector = connector;
        registerViewChangeListener();
    }

    private void registerViewChangeListener(){
        try {
            connector.registerViewChangeRemoteListener(this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onViewChange(PublishViewChangeEvent event) throws RemoteException {
        currentVMs = event.getCurrentVMs();

        if (currentVMs == null) {
            log.info("The set of VMs is empty. No-op");
            return;
        }

        log.info("New set of VMs " + Arrays.toString(currentVMs));

        WPMStatisticsRemoteListener listener = WPMStatisticsRemoteListernerFactory.getInstance().build();
        this.statisticsListener = listener;

        connector.registerStatisticsRemoteListener(new SubscribeEvent(currentVMs), listener);
    }
}
