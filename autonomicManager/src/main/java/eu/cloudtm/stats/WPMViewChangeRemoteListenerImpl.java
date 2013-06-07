package eu.cloudtm.stats;

import eu.cloudtm.StatsManager;
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
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/7/13
 */
public class WPMViewChangeRemoteListenerImpl implements WPMViewChangeRemoteListener {

    private final static Log log = LogFactory.getLog(WPMViewChangeRemoteListenerImpl.class);

    private WPMConnector connector;
    private StatsManager manager;

    public WPMViewChangeRemoteListenerImpl(WPMConnector _connector, StatsManager _manager){
        connector = _connector;
        manager = _manager;
    }

    @Override
    public void onViewChange(PublishViewChangeEvent event)
            throws RemoteException {

        /*
        if (lastVmHandle != null) {
            log.info("Removing last handle");
            connector.removeStatisticsRemoteListener(lastVmHandle);
            lastVmHandle = null;
        }
        */

        String[] VMs = event.getCurrentVMs();

        if (VMs == null) {
            log.info("The set of VMs is empty. No-op");
            return;
        }

        log.info("New set of VMs " + Arrays.toString(VMs));

        WPMStatisticsRemoteListener listener = new WPMStatisticsRemoteListenerImpl(manager);

        connector.registerStatisticsRemoteListener(new SubscribeEvent(VMs), listener);
    }

}
