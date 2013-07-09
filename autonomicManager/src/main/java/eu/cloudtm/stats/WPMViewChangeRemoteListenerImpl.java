package eu.cloudtm.stats;

import eu.cloudtm.wpm.connector.WPMConnector;
import eu.cloudtm.wpm.logService.remote.events.PublishViewChangeEvent;
import eu.cloudtm.wpm.logService.remote.events.SubscribeEvent;
import eu.cloudtm.wpm.logService.remote.listeners.WPMViewChangeRemoteListener;
import eu.cloudtm.wpm.logService.remote.observables.Handle;
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
    private Handle lastVmHandle;

    private String[] currentVMs;

    private WPMStatisticsRemoteListenerImpl statisticsListener;

    public WPMViewChangeRemoteListenerImpl(WPMConnector _connector, WPMStatisticsRemoteListenerImpl _statisticsListener){
        connector = _connector;
        statisticsListener = _statisticsListener;
    }


    @Override
    public void onViewChange(PublishViewChangeEvent event)
            throws RemoteException {

        /*
        if (lastVmHandle != null) {
            log.trace("Removing last handle");

            connector.removeStatisticsRemoteListener(lastVmHandle);
            lastVmHandle = null;
        }
        */


        currentVMs = event.getCurrentVMs();

        if (currentVMs == null) {
            log.info("The set of VMs is empty. No-op");
            return;
        }

        log.info("New set of VMs " + Arrays.toString(currentVMs));

        WPMStatisticsRemoteListenerImpl listener = new WPMStatisticsRemoteListenerImpl(this.statisticsListener.getSampleListeners());
        this.statisticsListener = listener;


        lastVmHandle = connector.registerStatisticsRemoteListener(new SubscribeEvent(currentVMs), listener);
    }

}
