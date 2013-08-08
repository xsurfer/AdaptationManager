package eu.cloudtm.autonomicManager.reconfigurators;

import eu.cloudtm.autonomicManager.ControllerLogger;
import eu.cloudtm.autonomicManager.IActuator;
import eu.cloudtm.autonomicManager.Reconfigurator;
import eu.cloudtm.autonomicManager.actuators.excepions.ActuatorException;
import eu.cloudtm.autonomicManager.commons.InstanceConfig;
import eu.cloudtm.autonomicManager.commons.PlatformConfiguration;
import eu.cloudtm.autonomicManager.commons.PlatformState;
import eu.cloudtm.autonomicManager.commons.State;
import eu.cloudtm.autonomicManager.configs.Config;
import eu.cloudtm.autonomicManager.configs.KeyConfig;
import eu.cloudtm.autonomicManager.exceptions.ReconfiguratorException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/16/13
 */
public class PlatformReconfigurator implements Reconfigurator<PlatformConfiguration> {

    private static int SECONDS_BETWEEN_RECONFIGURATIONS = Config.getInstance().getInt(KeyConfig.RECONFIGURATOR_SECONDS_BETWEEN_RECONFIGURATIONS.key() );

    private final Log log = LogFactory.getLog(PlatformReconfigurator.class);

    private Date lastReconfiguration;

    private AtomicInteger reconfigurationCounter = new AtomicInteger(0);

    private final State platformState;

    private final PlatformConfiguration current;

    private volatile PlatformConfiguration request;

    private AtomicBoolean reconfiguring = new AtomicBoolean(false);

    private final boolean ignoreError = Config.getInstance().getBoolean( KeyConfig. RECONFIGURATOR_IGNORE_ERROR.key() );

    private Throwable error;

    private final boolean testing = Config.getInstance().getBoolean( KeyConfig.RECONFIGURATOR_SIMULATE.key() );

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private IActuator actuator;

    public PlatformReconfigurator(PlatformConfiguration current, State platformState, IActuator actuator) {
        this.current = current;
        this.platformState = platformState;
        this.actuator = actuator;

    }

    @Override
    public void reconfigure(PlatformConfiguration toReconfigure) {

        // in case of error during the last reconfiguration, I'm throwing a RuntimeException
        if(error!=null){
            ControllerLogger.log.info("An error occurred during previous reconfiguration...throwing an exception!");
            throw new RuntimeException(error);
        }

        if(lastReconfiguration != null){
            long timeDiff = Math.abs( new Date().getTime() - lastReconfiguration.getTime() ) / 1000;
            if( timeDiff < SECONDS_BETWEEN_RECONFIGURATIONS ){
                ControllerLogger.log.info("Not enough time elapsed between reconfigurations...Skipping");
                return;
            }
        }

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
    }

    public boolean isReconfiguring(){
        return reconfiguring.get();
    }

    private void start(){
        ControllerLogger.log.info("#####################");
        ControllerLogger.log.info("Starting a new reconfigurarion request");
        ControllerLogger.log.info(request);
        ControllerLogger.log.info("#####################");


        try{
            if(testing){
                ControllerLogger.log.warn("Simulating reconfiguration...No instance will be changed!");
            } else {

                try {
                    actuator.triggerRebalancing(false);
                    log.info("State transfer disabled..");
                } catch (ActuatorException e) {
                    throw new ReconfiguratorException(e);
                }


                ControllerLogger.log.info("Reconfiguring degree");
                reconfigureDegree();
                ControllerLogger.log.info("Replication degree successfully switched to " + request.replicationDegree() + " !" );

                ControllerLogger.log.info("Reconfiguring scale");
                reconfigureSize();
                ControllerLogger.log.info("Scale successfully switched to " + request.platformSize() + " !" );

                // TODO CHECK THE NUM NODEs
                log.info("Waiting 20 secs");
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    actuator.triggerRebalancing(true);
                    log.info("State transfer enabled..");
                } catch (ActuatorException e) {
                    throw new ReconfiguratorException(e);
                }

                ControllerLogger.log.info("Reconfiguring protocol");
                reconfigureProtocol();
                ControllerLogger.log.info("Replication protocol successfully switched to " + request.replicationProtocol() + " !" );


            }

            ControllerLogger.log.info("Updating Current Configuration");
            current.setPlatformScale( request.platformSize(), InstanceConfig.MEDIUM );
            current.setRepDegree( request.replicationDegree() );
            current.setRepProtocol( request.replicationProtocol() );
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

        lastReconfiguration = new Date();

        ControllerLogger.log.info("*********************");
        ControllerLogger.log.info("Reconfiguration #" + reconfigurationCounter.incrementAndGet() + " ended");
        ControllerLogger.log.info("*********************");

    }

    private void reconfigureSize() throws ReconfiguratorException {

        int currSize = actuator.runningInstances().size();
        int numInstancesToChange = request.platformSize() - currSize; //  <<< COULD BE NEGATIVE, use Math.abs() >>>
        ControllerLogger.log.info("To change: " + numInstancesToChange );



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
            actuator.switchDegree(request.replicationDegree());
        } catch (ActuatorException e) {
            throw new ReconfiguratorException(e);
        }
    }

}

