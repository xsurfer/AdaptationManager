package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.actuators.excepions.ActuatorException;
import eu.cloudtm.autonomicManager.commons.*;
import eu.cloudtm.autonomicManager.configs.Config;
import eu.cloudtm.autonomicManager.configs.KeyConfig;
import eu.cloudtm.autonomicManager.exceptions.ReconfiguratorException;
import eu.cloudtm.autonomicManager.optimizers.OptimizerType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/16/13
 */
public class ReconfiguratorImpl implements Reconfigurator {

    private final Log log = LogFactory.getLog(ReconfiguratorImpl.class);


    private AtomicInteger reconfigurationCounter = new AtomicInteger(0);

    private final State platformState;

    private final PlatformConfiguration current;

    private volatile Map<OptimizerType, Object> request;

    private AtomicBoolean reconfiguring = new AtomicBoolean(false);

    private final boolean ignoreError = Config.getInstance().getBoolean( KeyConfig. RECONFIGURATOR_IGNORE_ERROR.key() );

    private Throwable error;

    private final boolean testing = Config.getInstance().getBoolean( KeyConfig.RECONFIGURATOR_SIMULATE.key() );

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private IActuator actuator;

    private ReentrantLock reconfigurationLock = new ReentrantLock();

    public ReconfiguratorImpl(PlatformConfiguration current, State platformState, IActuator actuator) {
        this.current = current;
        this.platformState = platformState;
        this.actuator = actuator;

    }

    @Override
    public void reconfigure(Map<OptimizerType, Object> toReconfigure) {

        // in case of error during the last reconfiguration, I'm throwing a RuntimeException
        if(error!=null){
            ControllerLogger.log.info("An error occurred during previous reconfiguration...throwing an exception!");
            throw new RuntimeException(error);
        }

        if( reconfigurationLock.tryLock() ){
            ControllerLogger.log.info("Lock successfully acquired");
            try{
                if( reconfiguring.compareAndSet(false, true) ){
                    request = toReconfigure;
                    ControllerLogger.log.info("Reconfiguration request ACCEPTED...");
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            start();
                        }
                    });
                }
            } finally {
                ControllerLogger.log.info("releasing lock");
                reconfigurationLock.unlock();
            }
        }
    }

    public boolean isReconfiguring(){
        return reconfiguring.get();
    }

    private void start(){
        ControllerLogger.log.info("#####################");
        ControllerLogger.log.info("Starting a new reconfigurarion request");
        //ControllerLogger.log.info(request);
        ControllerLogger.log.info("#####################");

        try{
            if(testing){
                ControllerLogger.log.warn("Simulating reconfiguration...No instance will be changed!");
            } else {

                PlatformConfiguration toReconfigurePlatform = (PlatformConfiguration) request.get(OptimizerType.PLATFORM);
                if( toReconfigurePlatform!=null ){
                    platformReconfiguration(toReconfigurePlatform);
                } else {
                    log.info("No reconfiguration platform found in the request!");
                }
            }

            request = null;

        } catch (Exception e) {     // capturing all the exceptions because if ignoreError=false, AM must die
            platformState.update(PlatformState.ERROR);

            ControllerLogger.log.warn("An error occurred while reconfiguring. " +
                    "Please check the log.out file for stack traces. " +
                    "Reconfigs are enabled, but system state is: " + platformState.current());

            log.warn(e,e);
            if(!ignoreError){
                error = e;  // Saving the error, it will be thrown ASAP by reconfigure method
            }
        } finally {

            if( !reconfiguring.compareAndSet(true, false) ){
                error = new RuntimeException("Some error happened! I've finished to reconfigure while controller was not reconfiguring!!!");
            }
        }

        ControllerLogger.log.info("*********************");
        ControllerLogger.log.info("Reconfiguration #" + reconfigurationCounter.incrementAndGet() + " ended");
        ControllerLogger.log.info("*********************");

    }


    private void platformReconfiguration(PlatformConfiguration platformRequest) throws ReconfiguratorException {


        if( Config.getInstance().getBoolean( KeyConfig.RECONFIGURATOR_SWITCH_REBALANCING.key() ) ){
            try {
                actuator.triggerRebalancing(false);
                log.info("State transfer disabled..");
            } catch (ActuatorException e) {
                throw new ReconfiguratorException(e);
            }
        } else {
            ControllerLogger.log.info("Skipping triggerRebalancing(false) because disabled!");
        }

        if( Config.getInstance().getBoolean( KeyConfig.RECONFIGURATOR_RECONFIGURE_DEGREE.key() ) ){
            ControllerLogger.log.info("Reconfiguring degree");
            reconfigureDegree(platformRequest.replicationDegree());
            ControllerLogger.log.info("Replication degree successfully switched to " + platformRequest.replicationDegree() + " !" );
        } else {
            ControllerLogger.log.info("Skipping reconfiguring replication degree because disabled!");
        }

        if( Config.getInstance().getBoolean( KeyConfig.RECONFIGURATOR_RECONFIGURE_NODES.key() ) ){
            ControllerLogger.log.info("Reconfiguring scale from " + current.platformSize() + " to " + platformRequest.platformSize() + " nodes");
            reconfigureSize(platformRequest.platformSize());
            ControllerLogger.log.info("Scale successfully switched to " + platformRequest.platformSize() + " !" );
        } else {
            ControllerLogger.log.info("Skipping reconfiguring scale because disabled!");
        }

        // TODO CHECK THE NUM NODEs
        log.info("Waiting 5 secs");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if( Config.getInstance().getBoolean( KeyConfig.RECONFIGURATOR_SWITCH_REBALANCING.key() ) ){
            try {
                actuator.triggerRebalancing(true);
                log.info("State transfer enabled..");
            } catch (ActuatorException e) {
                throw new ReconfiguratorException(e);
            }
        } else {
            ControllerLogger.log.info("Skipping triggerRebalancing(false) because disabled!");
        }


        if( Config.getInstance().getBoolean( KeyConfig.RECONFIGURATOR_RECONFIGURE_PROTOCOL.key() ) ){
            ControllerLogger.log.info("Reconfiguring protocol");
            reconfigureProtocol(platformRequest.replicationProtocol());
            ControllerLogger.log.info("Replication protocol successfully switched to " + platformRequest.replicationProtocol() + " !" );
        } else {
            ControllerLogger.log.info("Skipping replication protocol because disabled!");
        }


        ControllerLogger.log.info("Updating Current Configuration");
        current.setPlatformScale( platformRequest.platformSize(), InstanceConfig.MEDIUM );
        current.setRepDegree( platformRequest.replicationDegree() );
        current.setRepProtocol( platformRequest.replicationProtocol() );

    }

    private void reconfigureSize(int platformSize) throws ReconfiguratorException {

        int currSize = actuator.runningInstances().size();
        int numInstancesToChange = platformSize - currSize; //  <<< COULD BE NEGATIVE, use Math.abs() >>>
        //ControllerLogger.log.info("To change: " + numInstancesToChange );



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
        }

    }

    private void reconfigureProtocol(ReplicationProtocol replicationProtocol) throws ReconfiguratorException {
        boolean forceStop = Config.getInstance().getBoolean( KeyConfig.ISPN_ACTUATOR_FORCE_STOP.key() );
        boolean abortOnStop = Config.getInstance().getBoolean( KeyConfig.ISPN_ACTUATOR_ABORT_ON_STOP.key() );

        try {
            actuator.switchProtocol(replicationProtocol);
        } catch (ActuatorException e) {
            throw new ReconfiguratorException(e);
        }
    }

    private void reconfigureDegree(int replicationDegree) throws ReconfiguratorException {
        try {
            actuator.switchDegree(replicationDegree);
        } catch (ActuatorException e) {
            throw new ReconfiguratorException(e);
        }
    }

}

