package eu.cloudtm.controller;

import com.google.gson.Gson;
import eu.cloudtm.StatsManager;
import eu.cloudtm.common.SampleListener;
import eu.cloudtm.controller.exceptions.ActuatorException;
import eu.cloudtm.controller.exceptions.OutputFilterException;
import eu.cloudtm.controller.model.PlatformConfiguration;
import eu.cloudtm.controller.model.PlatformTuning;
import eu.cloudtm.controller.model.State;
import eu.cloudtm.controller.model.Tuning;
import eu.cloudtm.controller.model.utils.InstanceConfig;
import eu.cloudtm.controller.model.utils.PlatformState;
import eu.cloudtm.controller.model.utils.ReplicationProtocol;
import eu.cloudtm.controller.model.utils.TuningState;
import eu.cloudtm.stats.Sample;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/5/13
 */
public class Controller implements SampleListener {

    private static Log log = LogFactory.getLog(Controller.class);

    private static Controller instance;

    /* STATE */
    private State state = new State(PlatformState.RUNNING);

    private final PlatformConfiguration platformConfiguration;

    private PlatformTuning platformTuning;

    public final static double TIME_WINDOW = 60D; // TODO: renderlo parametro


    /* COMPONENTs */

    private StatsManager statsManager;

    private InputFilter inputFilter;

    private OutputFilter outputFilter = new OutputFilter();

    private List<String> oracles = new ArrayList<String>() {{
        add("OracleTAS");
    }};

    private ExecutorService singleThreadExec = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"singleDoController");  //To change body of implemented methods use File | Settings | File Templates.
        }
    });


    /* CONFIGURATION */
    public static final int SAMPLE_WINDOW = 5;


    /* MONITORs */

    private AtomicInteger samplesCounter = new AtomicInteger(1);

    private Lock reconfigurationLock = new ReentrantLock();


    /* *** CONSTRUCTOR && FACTORY METHOD *** */

    public static Controller getInstance(){
        if(instance == null)
            throw new RuntimeException("No Controller instance");
        return instance;
    }

    public static Controller getInstance(PlatformConfiguration _configuration, StatsManager _statsManager){
        if(instance == null)
            instance = new Controller(_configuration, _statsManager);
        return instance;
    }

    private Controller(PlatformConfiguration _configuration, StatsManager _statsManager) {
        statsManager =  _statsManager;
        platformConfiguration = _configuration;
        inputFilter = new InputFilter(statsManager);
    }


    /* METHODS */

    @Override
    public void onNewSample(Sample sample) {
        ControllerLogger.log.info("New sample stats received (" + sample.getId() + ")");
        if( !samplesCounter.compareAndSet(SAMPLE_WINDOW, 1) ){  // Num sample < SAMPLE_WINDOW
            samplesCounter.incrementAndGet();
        } else {
            if( reconfigurationLock.tryLock() ){
                try {
                singleThreadExec.execute(new Runnable() {           // Analyzing
                @Override
                public void run() {
                    doControl();
                }
            });
                } finally {
                    reconfigurationLock.unlock();
                }
            } else {
                ControllerLogger.log.info("Controller is already working. Skipping...");
            }
        }
    }

    /**
     * Solo
     */
    private void doControl(){
        ControllerLogger.log.info("Locking reconfiguration");
        if( !inputFilter.doFilter() ){
            ControllerLogger.log.info("No reconfiguration needed...");

        } else {
            state.update(PlatformState.RECONFIGURING);
            ControllerLogger.log.info("Looking for an available configuration...");

            PlatformConfiguration nextConf = new Optimizer(this, oracles, statsManager).doOptimize(inputFilter.getLastAvgArrivalRate(),
                    inputFilter.getLastAvgAbortRate(), inputFilter.getLastAvgResposeTime());

            if(nextConf != null){
                ControllerLogger.log.info( "Time to reconfigure (" + nextConf.platformSize() + ", " + nextConf.threadPerNode() + ")" );
                try {
                    outputFilter.doFilter(nextConf);
                    state.update(PlatformState.RUNNING);
                } catch (OutputFilterException e) {
                    ControllerLogger.log.warn(e, e);
                    onError();
                    ControllerLogger.log.info("No configuration available! Nothing to do...");
                }
            } else {
                ControllerLogger.log.info("No configuration available! Nothing to do...");
                state.update(PlatformState.RUNNING);
            }

        }
    }

    private void onError(){
        state.update(PlatformState.ERROR);
    }

    private void onUserAction(){
        try {
            reconfigurationLock.lock();


        } finally {
            reconfigurationLock.unlock();
        }
    }


    /* USER CONTROL */

    public void updateScale(int _size, InstanceConfig _instanceConf, Tuning tuning){
        if( state.isRunning() ){

            if( !scaleTuning.equals(tuning) )
                scaleTuning.set(tuning.getForecaster());

            if(scaleTuning.getState() == TuningState.MANUAL)
                platformConfiguration.setPlatformScale(_size, _instanceConf);

            singleThreadExec.execute(new Runnable() {           // Analyzing
                @Override
                public void run() {
                    onUserAction();
                }
            });
        }
    }

    public void updateDegree(int _degree, Tuning tuning){

        if( state.isRunning() ){
            if(!repDegreeTuning.equals(tuning))
                repDegreeTuning.set(tuning.getForecaster());

            if(repDegreeTuning.getState()== TuningState.MANUAL)
                platformConfiguration.setRepDegree(_degree);

            onUserAction();
        }

    }

    public void updateProtocol(ReplicationProtocol _protocol, Tuning tuning){

        if( state.isRunning() ){
            if(!repProtocolTuning.equals(tuning))
                repProtocolTuning.set(tuning.getForecaster());

            if(repProtocolTuning.getState()== TuningState.MANUAL)
                platformConfiguration.setRepProtocol(_protocol);

            onUserAction();
        }

    }

    public PlatformState getState(){
        return state.current();
    }

    public PlatformConfiguration getCurrentConfiguration(){
        return platformConfiguration;
    }

    public List<String> getOracles(){
        return new ArrayList<String>(this.oracles);
    }

    public String getJSONState(){
    /* stampa il seguente output:
        {
           "state":"WORKING",
           "scale":{
                "nodes":3,
                "configuration":"SMALL",
                "forecaster":"SIMULATOR"
            },
            "replication_protocol":{
                "protocol":"TWOPC",
                "forecaster":"NONE"
            },
            "replication_degree":{
                "degree":2,
                "forecaster":"ANALYTIC"
            },
            "data_placement":"enabled"
        }
    */

        Gson gson = new Gson();
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        sb.append(gson.toJson("state") + ":" + gson.toJson(getState()) + "," );
        sb.append(gson.toJson("scale") + ":" );
        sb.append("{");
            sb.append(gson.toJson("nodes") + ":" + platformConfiguration.platformSize() + ",");
            sb.append(gson.toJson("configuration") + ":" + gson.toJson( platformConfiguration.nodeConfiguration() ) +  ",");
            sb.append(gson.toJson("forecaster") + ":" + gson.toJson( scaleTuning.getForecaster() ) );
        sb.append("},");
        sb.append(gson.toJson("replication_protocol") + ":");
        sb.append("{");
            sb.append(gson.toJson("protocol") + ":" + gson.toJson( platformConfiguration.replicationProtocol() ) + "," );
            sb.append(gson.toJson("forecaster") + ":" + gson.toJson( repProtocolTuning.getForecaster() ) );
        sb.append("},");
        sb.append(gson.toJson("replication_degree") + ":" );
        sb.append("{");
            sb.append(gson.toJson("degree") + ":" + platformConfiguration.replicationDegree() + "," );
            sb.append(gson.toJson("forecaster") + ":" + gson.toJson( repDegreeTuning.getForecaster() ) );
        sb.append("},");
        sb.append(gson.toJson("data_placement") + ":" + gson.toJson( (dataPlacement==true) ? "enabled" : "disabled") );
        sb.append("}");
        //log.info(sb);
        return sb.toString();
    }

}