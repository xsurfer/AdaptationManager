package eu.cloudtm.controller;

import com.google.gson.Gson;
import eu.cloudtm.StatsManager;
import eu.cloudtm.controller.model.ReplicationDegree;
import eu.cloudtm.controller.model.ReplicationProtocol;
import eu.cloudtm.controller.model.Scale;
import eu.cloudtm.controller.model.PlatformConfiguration;
import eu.cloudtm.controller.model.utils.PlatformState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/5/13
 */
public class Controller extends Thread implements Observer {

    private Controller instance;

    private static Log log = LogFactory.getLog(Controller.class);

    private PlatformConfiguration state;
    private Gson gson = new Gson();

    private AtomicBoolean wasSignalled = new AtomicBoolean(false);

    public Controller(PlatformConfiguration _state) {
        state = _state;
    }

    public synchronized void run() {
        while(true) {
            while (!wasSignalled.getAndSet(false)) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    log.warn("Unexpected interrupt");
                }
            }
            state.updateState(PlatformState.PENDING);
            try {
                log.info("simulating...");
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                log.info("Interrupted");
            }
            log.info("restoring...");
            state.updateState(PlatformState.WORKING);
        }
    }

    private synchronized void doNotify(){
        wasSignalled.set(true);
        notify();
    }

    public PlatformConfiguration getStateClone() {
        return state.cloneJSON();
    }


    public void tuneScale(Scale newScale) {
        state.updateScale(newScale);
        doNotify();
    }

    public void tuneReplicationDegree(ReplicationDegree newRepDegree) {
        state.updateState(PlatformState.PENDING);
        try {
            log.info("simulating...");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            log.info("Interrupted");
        }
        state.updateReplicationDegree(newRepDegree);
        state.updateState(PlatformState.WORKING);
    }

    public void tuneReplicationProtocol(ReplicationProtocol newRepProtocol) {
        state.updateState(PlatformState.PENDING);
        try {
            log.info("simulating...");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            log.info("Interrupted");
        }
        state.updateReplicationProtocol(newRepProtocol);
        state.updateState(PlatformState.WORKING);
    }

    @Override
    public void update(Observable o, Object arg) {
        log.info("GENERIC ha ricevuto un nuovo coso");
    }
}
