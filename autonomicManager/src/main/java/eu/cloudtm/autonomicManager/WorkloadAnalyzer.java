package eu.cloudtm.autonomicManager;

import eu.cloudtm.oracles.OutputOracle;
import eu.cloudtm.autonomicManager.oracles.OutputOracleImpl;
import eu.cloudtm.autonomicManager.exceptions.ReconfiguratorException;
import eu.cloudtm.oracles.exceptions.OracleException;
import eu.cloudtm.commons.Param;
import eu.cloudtm.statistics.ProcessedSample;

import eu.cloudtm.statistics.SampleListener;
import eu.cloudtm.statistics.StatsManager;
import org.apache.commons.collections15.Buffer;
import org.apache.commons.collections15.BufferUtils;
import org.apache.commons.collections15.buffer.CircularFifoBuffer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/16/13
 */
public class WorkloadAnalyzer implements SampleListener {

    private static Log log = LogFactory.getLog(WorkloadAnalyzer.class);

    private static final int SLIDE_WINDOW_SIZE = 30;

    private Optimizer optimizer;

    private Buffer<ProcessedSample> sampleSlideWindow = BufferUtils.synchronizedBuffer(new CircularFifoBuffer<ProcessedSample>(SLIDE_WINDOW_SIZE));

    /* *** SOGLIE *** */
    private static final int DELTA_ARRIVAL_RATE = 20;
    private volatile double lastAvgArrivalRate = 0.0;

    private static final int DELTA_ABORT_RATE = 20;
    private volatile double lastAvgAbortRate = 0.0;

    private static final int DELTA_RESPONSE_TIME = 2;
    private volatile double lastAvgResposeTime = 0.0;

    private Lock reconfigurationLock = new ReentrantLock();

//    /* *** VALORI ATTUALI *** */
//    private double currentArrivalRate;
//    private double current;
//    private double currentThroughput;

    public WorkloadAnalyzer(StatsManager statsManager, Optimizer optimizer){
        this.optimizer = optimizer;
        statsManager.addListener(this);
    }

    @Override
    public synchronized void onNewSample(ProcessedSample sample) {
        sampleSlideWindow.add(sample);

        if(sampleSlideWindow.size() < SLIDE_WINDOW_SIZE){
            return;
        }
        boolean reconfigure;
        boolean arrivalRateResponse = evaluateArrivalRate();
        boolean responseTimeResponse = evaluateResponseTime();
        boolean abortRateResponse = evaluateAbortRate();

        reconfigure =  arrivalRateResponse || responseTimeResponse || abortRateResponse;

        if(reconfigure){
            if(reconfigurationLock.tryLock()){
                try{
                    ControllerLogger.log.info("Starting a new reconfiguration...");
                    OutputOracle current = new OutputOracleImpl(lastAvgArrivalRate, lastAvgAbortRate, lastAvgResposeTime, lastAvgResposeTime);
                    try {
                        optimizer.doOptimize(current, sample);
                    } catch (ReconfiguratorException e) {
                        ControllerLogger.log.warn("Exeception while reconfiguring!!");
                    } catch (OracleException e) {
                        ControllerLogger.log.warn("Exeception while optimizing!!");
                    }

                } finally {
                    reconfigurationLock.unlock();
                }
            } else {
                ControllerLogger.log.info("Reconfiguration is running, skipping...");
            }
        }
    }

    private  synchronized boolean evaluateAbortRate(){
        double abortSum = 0.0;
        for (ProcessedSample sample : sampleSlideWindow){
            abortSum += (Double) sample.getParam(Param.AbortRate);
        }
        double currentAbortAvg =  abortSum / ((double) sampleSlideWindow.size());
        log.debug("currentAbortAvg: " + currentAbortAvg);
        log.debug("lastAvgAbortRate: " + lastAvgAbortRate);

        if(lastAvgAbortRate == 0 || lastAvgAbortRate == Double.NaN){
            log.debug("Updating && Skipping lastAvgAbortRate");
            lastAvgAbortRate = currentAbortAvg;
        } else {
            double rapporto = ( currentAbortAvg / lastAvgAbortRate ) * 100;
            log.debug("ratio: " + rapporto );

            double variazione = Math.abs(rapporto - 100);
            log.debug("variation: " + variazione );

            if( variazione >= DELTA_ABORT_RATE ){
                log.trace("Update the lastAvgAbortRate");
                lastAvgAbortRate = currentAbortAvg;
                log.info("BOUND REACHED (AbortRate)");
                return true;
            }
        }
        return false;
    }

    private synchronized boolean evaluateResponseTime(){
        return false;
    }

    private synchronized boolean evaluateArrivalRate(){
        double throughputSum = 0.0;
        for (ProcessedSample sample : sampleSlideWindow){
            throughputSum += (Double) sample.getParam(Param.Throughput);
        }
        double currentThroughputAvg =  throughputSum / ((double) sampleSlideWindow.size());
        log.debug("currentThroughputAvg: " + currentThroughputAvg);
        log.debug("lastAvgArrivalRate: " + lastAvgArrivalRate);

        if(lastAvgArrivalRate == 0D || lastAvgArrivalRate == Double.NaN){
            log.info("Updating && Skipping lastAvgArrivalRate");
            lastAvgArrivalRate = currentThroughputAvg;
        } else {
            double rapporto = ( currentThroughputAvg / lastAvgArrivalRate ) * 100;
            log.debug("ratio: " + rapporto );

            double variazione = Math.abs(rapporto - 100);
            log.debug("variation: " + variazione );

            if( variazione >= DELTA_ARRIVAL_RATE ){
                log.trace("Update the lastAvgArrivalRate");
                lastAvgArrivalRate = currentThroughputAvg;
                log.info("BOUND REACHED (ArrivalRate)");
                return true;
            }
        }
        return false;
    }


    /* *** GETTER PER I VALORI ATTUALI *** */

    public double getLastAvgResposeTime() {
        return lastAvgResposeTime;
    }

    public double getLastAvgAbortRate() {
        return lastAvgAbortRate;
    }

    public double getLastAvgArrivalRate() {
        return lastAvgArrivalRate;
    }

}