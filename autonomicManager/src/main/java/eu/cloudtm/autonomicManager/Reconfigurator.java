package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.actuators.DeltaCloudActuator;
import eu.cloudtm.commons.PlatformConfiguration;
import eu.cloudtm.autonomicManager.exceptions.ReconfiguratorException;
import eu.cloudtm.commons.ReplicationProtocol;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.deltacloud.client.DeltaCloudClientException;

import java.net.MalformedURLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/16/13
 */
public class Reconfigurator {

    private final Log log = LogFactory.getLog(Reconfigurator.class);

    private AtomicInteger reconfigurationCounter = new AtomicInteger(0);


    private final PlatformConfiguration current;

    private PlatformConfiguration request;

    private int progress = 0;

    private AtomicBoolean reconfiguring = new AtomicBoolean(false);

    private boolean testing = true;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();


    public Reconfigurator(PlatformConfiguration current) {
        this.current = current;
    }


    public boolean reconfigure(PlatformConfiguration nextConf) {
        if( reconfiguring.compareAndSet(false, true) ){
            executorService.submit();

//            ControllerLogger.log.info("Reconfiguring: " + nextConf);
//            reconfigureSize(nextConf.platformSize(), nextConf.threadPerNode());
//            ControllerLogger.log.info("Reconfiguration #" + reconfigurationCounter.incrementAndGet() + " ended");
            return true;
        } else {
            return false;
        }
    }

    public boolean isReconfiguring(){
        return reconfiguring.get();
    }

    public int progress(){
        if(reconfiguring.get()){
            return progress;
        }
        return -1;
    }


    private class CallableReconfigurator implements Callable<Integer>{

        private final int numNodes;

        public CallableReconfigurator(int numNodes, int degree, ReplicationProtocol protocol){
            this.numNodes = numNodes;
        }

        @Override
        public Integer call() throws ReconfiguratorException {
            reconfigureSize(numNodes, 2);
            progress++;

            return 1;
        }

        private void reconfigureSize(int numNodes, int numThreads) throws ReconfiguratorException {

            try {
                DeltaCloudActuator deltaCloudActuator = DeltaCloudActuator.getInstance(numNodes, numThreads);
                if(testing){
                    ControllerLogger.log.warn("Actuator is disabled");
                } else {
                    deltaCloudActuator.actuate();
                }

            } catch (MalformedURLException e) {
                throw new ReconfiguratorException(e);
            } catch (DeltaCloudClientException e) {
                throw new ReconfiguratorException(e);
            }

        }

        private void reconfigureProtocol() {

        }

        private void reconfigureDegree(){

        }
    }

}