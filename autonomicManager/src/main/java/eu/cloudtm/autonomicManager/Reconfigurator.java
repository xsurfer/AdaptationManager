package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.actuators.ActuatorException;
import eu.cloudtm.autonomicManager.configs.Config;
import eu.cloudtm.autonomicManager.configs.KeyConfig;
import eu.cloudtm.autonomicManager.exceptions.ReconfiguratorException;
import eu.cloudtm.autonomicManager.commons.IPlatformConfiguration;
import eu.cloudtm.autonomicManager.commons.InstanceConfig;
import eu.cloudtm.autonomicManager.commons.PlatformState;
import eu.cloudtm.autonomicManager.commons.State;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/16/13
 */
public class Reconfigurator implements IReconfigurator {

    private final Log log = LogFactory.getLog(Reconfigurator.class);

    private AtomicInteger reconfigurationCounter = new AtomicInteger(0);

    private final State platformState;

    private final IPlatformConfiguration current;

    private volatile IPlatformConfiguration request;

    private AtomicBoolean reconfiguring = new AtomicBoolean(false);

    private boolean testing = false;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private IActuator actuator;

    public Reconfigurator( IPlatformConfiguration current, State platformState, IActuator actuator) {
        this.current = current;
        this.platformState = platformState;
        this.actuator = actuator;

    }

    public boolean reconfigure(IPlatformConfiguration nextConf) {
        if( reconfiguring.compareAndSet(false, true) ){
            request = nextConf;

            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    start();
                }
            });

            return true;
        } else {
            return false;
        }
    }

    public boolean isReconfiguring(){
        return reconfiguring.get();
    }

    private void start(){
        ControllerLogger.log.info("Reconfiguring: " + request);

        try{
            if(testing){
                ControllerLogger.log.warn("Reconfigurator is setted for skip the real reconfiguration");
            } else {
                reconfigureDegree();
                reconfigureProtocol();
                reconfigureSize();
            }

            log.info("Updating Current Configuration");
            current.setPlatformScale( request.platformSize(), InstanceConfig.MEDIUM );
            current.setRepDegree( request.replicationDegree() );
            current.setRepProtocol( request.replicationProtocol() );
            request = null;
            ControllerLogger.log.info("Reconfiguration #" + reconfigurationCounter.incrementAndGet() + " ended");

        } catch (ReconfiguratorException e) {

            platformState.update(PlatformState.ERROR);
            ControllerLogger.log.warn("An error occurred while reconfiguring...Reconfigs are enabled, but system state is: " + platformState.current());

        } finally {

            if( !reconfiguring.compareAndSet(true, false) ){
                throw new RuntimeException("Some error happened! I've finished to reconfigure while controller was not reconfiguring!!!");
            }
        }
    }

    private void reconfigureSize() throws ReconfiguratorException {


        int currSize = actuator.runningInstances().size();
        log.info(currSize + " RUNNING instances");

        int numInstancesToChange = request.platformSize() - currSize; //  <<< COULD BE NEGATIVE, use Math.abs() >>>

        if(numInstancesToChange>0){
            while ( numInstancesToChange-- > 0 ){
                try {
                    actuator.startInstance();
                } catch (ActuatorException e) {
                    throw new ReconfiguratorException(e);
                }
            }
        } else if(numInstancesToChange < 0){
            while ( numInstancesToChange++ < 0 ){
                try {
                    actuator.stopInstance();
                } catch (ActuatorException e) {
                    throw new ReconfiguratorException(e);
                }
            }
        } else {
            log.info("Nothing to do");
        }

        currSize = actuator.runningInstances().size();
        log.info(currSize + " RUNNING instances");

    }

    private void reconfigureProtocol() throws ReconfiguratorException {

        boolean forceStop = Config.getInstance().getBoolean( KeyConfig.ISPN_ACTUATOR_FORCE_STOP.key() );
        boolean abortOnStop = Config.getInstance().getBoolean( KeyConfig.ISPN_ACTUATOR_ABORT_ON_STOP.key() );

        try {
            actuator.switchProtocol( request.replicationProtocol() );
        } catch (ActuatorException e) {
            throw new ReconfiguratorException(e);
        }
    }

    private void reconfigureDegree() throws ReconfiguratorException {

        try {
            actuator.switchProtocol( request.replicationProtocol() );
        } catch (ActuatorException e) {
            throw new ReconfiguratorException(e);
        }

    }

}

