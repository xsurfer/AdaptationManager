package eu.cloudtm.controller;

import com.google.gson.Gson;
import eu.cloudtm.controller.model.KPI;
import eu.cloudtm.controller.model.PlatformConfiguration;
import eu.cloudtm.controller.model.Tuning;
import eu.cloudtm.controller.model.utils.InstanceConfig;
import eu.cloudtm.controller.model.utils.PlatformState;
import eu.cloudtm.controller.model.utils.ReplicationProtocol;
import eu.cloudtm.controller.model.utils.TuningState;
import eu.cloudtm.controller.oracles.AbstractOracle;
import eu.cloudtm.stats.Sample;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/5/13
 */
public class Controller {

    private static Log log = LogFactory.getLog(Controller.class);

    /* STATE */
    private PlatformState state =  PlatformState.RUNNING;

    private final PlatformConfiguration platformConfiguration;

    public final static double TIME_WINDOW = 5D; // TODO: renderlo parametro


    /* COMPONENTs */
    private Gson gson = new Gson();

    private InputFilter inputFilter = new InputFilter();

    private Optimizer optimizer = new Optimizer(this);

    private OutputFilter outputFilter = new OutputFilter();

    private List<AbstractOracle> oracles = new ArrayList<AbstractOracle>(
            Arrays.asList(AbstractOracle.getInstance("OracleTAS"))    );


    private ExecutorService singleThreadExec = Executors.newSingleThreadExecutor();


    /* CONFIGURATION */
    private static final int SAMPLE_WINDOW = 1;

    private AtomicInteger samplesCounter = new AtomicInteger(0);


    /* TUNING CONFIGURATION */
    private Tuning scaleTuning = new Tuning();

    private Tuning repProtocolTuning = new Tuning();

    private Tuning repDegreeTuning = new Tuning();

    private Boolean dataPlacement = true;


    public Controller(PlatformConfiguration _state) {
        platformConfiguration = _state;
    }

    public synchronized void onNewStat(Sample sample){
        increment();
    }

    public void increment(){
        if( !samplesCounter.compareAndSet(SAMPLE_WINDOW, 0) ){
            samplesCounter.incrementAndGet();
            return;
        }

        singleThreadExec.execute(new Runnable() {
            @Override
            public void run() {
                doControl();
            }
        });
    }

    private void doControl(){
        log.info("Starting...");
        boolean toReconfigure = inputFilter.doFilter();
        if(toReconfigure){
            log.info("Reconf needed...");
            PlatformConfiguration nextConf = optimizer.doOptimize();
            outputFilter.doFilter(nextConf);
        } else {
            log.info("No reconf needed...");
        }
    }

    private synchronized void onUserAction(){
        throw new RuntimeException("TO IMPLEMENT");
    }

    /* USER CONTROL */
    public synchronized void updateScale(int _size, InstanceConfig _instanceConf, Tuning tuning){

        if(state==PlatformState.RUNNING){
            if(!scaleTuning.equals(tuning))
                scaleTuning.set(tuning.getForecaster());

            if(scaleTuning.getState() == TuningState.MANUAL)
                platformConfiguration.setPlatformScale(_size, _instanceConf);

            onUserAction();
        }
    }

    public synchronized void updateDegree(int _degree, Tuning tuning){

        if(state==PlatformState.RUNNING){
            if(!repDegreeTuning.equals(tuning))
                repDegreeTuning.set(tuning.getForecaster());

            if(repDegreeTuning.getState()== TuningState.MANUAL)
                platformConfiguration.setRepDegree(_degree);

            onUserAction();
        }

    }

    public synchronized void updateProtocol(ReplicationProtocol _protocol, Tuning tuning){

        if(state==PlatformState.RUNNING){
            if(!repProtocolTuning.equals(tuning))
                repProtocolTuning.set(tuning.getForecaster());

            if(repProtocolTuning.getState()== TuningState.MANUAL)
                platformConfiguration.setRepProtocol(_protocol);

            onUserAction();
        }

    }

    /* *** WEB IF INTERACTION *** */


/*
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

    public String getJSONState(){
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
        log.info(sb);
        return sb.toString();
    }

    public PlatformState getState(){
        return state;
    }

    public PlatformConfiguration getCurrentConfiguration(){
        return platformConfiguration;
    }

    public List<AbstractOracle> getOracles(){
        return this.oracles;
    }


}


//private AtomicBoolean newSample = new AtomicBoolean(false);

//private AtomicBoolean userAction = new AtomicBoolean(false);
