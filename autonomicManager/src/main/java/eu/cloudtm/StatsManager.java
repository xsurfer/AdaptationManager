package eu.cloudtm;

import eu.cloudtm.stats.WPMStatisticsRemoteListenerImpl;
import eu.cloudtm.stats.WPMViewChangeRemoteListenerImpl;
import eu.cloudtm.wpm.connector.WPMConnector;
import eu.cloudtm.wpm.logService.remote.events.*;
import eu.cloudtm.wpm.logService.remote.listeners.WPMStatisticsRemoteListener;
import eu.cloudtm.wpm.logService.remote.listeners.WPMViewChangeRemoteListener;
import eu.cloudtm.wpm.logService.remote.observables.Handle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.RemoteException;
import java.util.*;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/6/13
 */
public class StatsManager
        extends Observable {

    private final static Log log = LogFactory.getLog(StatsManager.class);

    private WPMConnector connector;
    private Handle lastVmHandle;

    private Set<HashMap<String, PublishAttribute>> lastJMX;
    private Set<HashMap<String, PublishAttribute>> lastMEM;

    public StatsManager(){
        try {
            connector = new WPMConnector();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            connector.registerViewChangeRemoteListener(new WPMViewChangeRemoteListenerImpl(connector,this));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void newStatsAvailables(){
        log.info("NEW STATS AVAILABLES");
    }


    public Set<HashMap<String, PublishAttribute>> getLastJMXStats(){
        return lastJMX;
    }

    public Set<HashMap<String, PublishAttribute>> getLastMEMStats(){
        return lastMEM;
    }

    public
    //LocalReadOnlyTxLocalServiceTime
}


