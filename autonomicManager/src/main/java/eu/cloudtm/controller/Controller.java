package eu.cloudtm.controller;

import eu.cloudtm.controller.model.PlatformConfiguration;
import eu.cloudtm.controller.model.Tuning;
import eu.cloudtm.controller.model.utils.InstanceConfig;
import eu.cloudtm.controller.model.utils.PlatformState;
import eu.cloudtm.controller.model.utils.ReplicationProtocol;
import eu.cloudtm.controller.model.utils.TuningState;
import eu.cloudtm.stats.Sample;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/5/13
 */
public class Controller {

    private Controller instance;

    private static Log log = LogFactory.getLog(Controller.class);

    private PlatformState state =  PlatformState.RUNNING;

    private final PlatformConfiguration platformConfiguration;

    private long transtitoryTime = 30;   //in sec

    /**
     * If true means that new sample is available
     */
    private AtomicBoolean newSample = new AtomicBoolean(false);

    /**
     * If true means that user want to change the configuration
     */
    private AtomicBoolean userAction = new AtomicBoolean(false);

    private long lastInit;

    private final BlockingQueue userRequest = new LinkedBlockingQueue();

    /* * TUNING INFO * */
    private Tuning scaleTuning;
    private Tuning repProtocolTuning;
    private Tuning repDegreeTuning;
    private Boolean dataPlacement;


    private List<IOracle> oracles = new ArrayList<IOracle>(
            Arrays.asList( getOracle("OracleTAS") ) );

    {
        long now = System.currentTimeMillis();
        lastInit = now;
    }

    public Controller(PlatformConfiguration _state) {
        platformConfiguration = _state;
    }


    /**
     * Questo metodo è eseguito quando una è ricevuta una nuova statistica
     * Nel caso è state precedentemente ricevuta una nuova configurazione dall'utente
     * ignoro la statistica e attendo
     *
     * @param sample
     */
    public synchronized void doNotify(Sample sample){
        if(!userAction.get()){
            newSample.set(true);
        }

        log.warn("CALL THE LOAD PREDICTOR");
        double futureWorkload = 6000;

        PlatformConfiguration nextConfig = null;
        for(IOracle oracle : oracles){
             nextConfig = oracle.minimizeCosts(sample, futureWorkload)
                    .getPlatformConfiguration();

        }

        if(nextConfig!=null)
            reconfigure();
    }

    /**
     * Questo metodo è eseguito quando l'utete decide di cambiare manualmente la configurazione.
     * Ha la priorità sulla riconfigurazione automatica
     *
     */
    public synchronized void doNotify(){
        userAction.set(true);
        /* Creo un nuovo thread che si incarica di gestire il reconfigure  */

        state = PlatformState.RECONFIGURING;

        ExecutorService pool = Executors.newSingleThreadExecutor();
        pool.execute(new Runnable() {

            @Override
            public void run() {
                reconfigure();

            }
        });
    }

    public synchronized void reconfigure(){
        log.warn("TO IMPLEMENT: reconfigure");

        state = PlatformState.RUNNING;
    }

    public synchronized void updateScale(int _size, InstanceConfig _instanceConf, Tuning tuning){

        if(state==PlatformState.RUNNING){
            if(!scaleTuning.equals(tuning))
                scaleTuning.set(tuning.getForecaster());

            if(scaleTuning.getState() == TuningState.MANUAL)
                platformConfiguration.setPlatformScale(_size, _instanceConf);

            doNotify();
        }
    }

    public synchronized void updateDegree(int _degree, Tuning tuning){

        if(state==PlatformState.RUNNING){
            if(!repDegreeTuning.equals(tuning))
                repDegreeTuning.set(tuning.getForecaster());

            if(repDegreeTuning.getState()== TuningState.MANUAL)
                platformConfiguration.setRepDegree(_degree);

            doNotify();
        }

    }

    public synchronized void updateProtocol(ReplicationProtocol _protocol, Tuning tuning){

        if(state==PlatformState.RUNNING){
            if(!repProtocolTuning.equals(tuning))
                repProtocolTuning.set(tuning.getForecaster());

            if(repProtocolTuning.getState()== TuningState.MANUAL)
                platformConfiguration.setRepProtocol(_protocol);

            doNotify();
        }

    }


    public static IOracle getOracle(String oracleName) {
        if (oracleName.indexOf('.') < 0) {
            oracleName = "eu.cloudtm.oracles." + oracleName;
        }
        try {
            IOracle obj;
            Constructor c = Class.forName(oracleName).getConstructor();
            obj = (IOracle) c.newInstance();
            return obj;
        } catch (Exception e) {
            String s = "Could not create oracle of type: " + oracleName;
            log.error(s);
            throw new RuntimeException(e);
        }
    }

    /* *** GETTER *** */
    public PlatformState getState(){
        return state;
    }

    public PlatformConfiguration getCurrentConfiguration(){
        return platformConfiguration;
    }


}


//    public synchronized void run() {
//
//        List<IOracle> oracles = new ArrayList<IOracle>();
//        oracles.add( getOracle("OracleTAS") );
//
//
//        while(true) {
//            while (!newSample.getAndSet(false) && !userAction.getAndSet(false)) {
//                try {
//                    wait();
//                } catch (InterruptedException e) {
//                    log.warn("Unexpected interrupt");
//                }
//            }
//
//            log.info("CHi m'ha svegliato?");
//            // utente, statsManager o tutti e due? utente ha priorità
//
//            if(userAction.compareAndSet(true,false)){
//                manageUserInput();
//            } else {
//                manageNewStat();
//            }
//
//
//            for(IOracle oracle : oracles){
//                oracle.minimizeCosts();
//            }
//
//
//            state = PlatformState.RUNNING;
//
//            try {
//                log.info("simulating...");
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                log.info("Interrupted");
//            }
//            log.info("restoring...");
//            state = PlatformState.RECONFIGURING;
//        }
//    }
