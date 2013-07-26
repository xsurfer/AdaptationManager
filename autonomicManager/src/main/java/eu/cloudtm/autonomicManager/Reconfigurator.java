package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.configs.Config;
import eu.cloudtm.autonomicManager.configs.KeyConfig;
import eu.cloudtm.autonomicManager.exceptions.ReconfiguratorException;
import eu.cloudtm.autonomicManager.actuators.ActuatorException;
import eu.cloudtm.commons.IPlatformConfiguration;
import eu.cloudtm.commons.InstanceConfig;
import eu.cloudtm.commons.PlatformState;
import eu.cloudtm.commons.State;
import eu.cloudtm.exception.InvocationException;
import eu.cloudtm.exception.NoJmxProtocolRegisterException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.deltacloud.client.DeltaCloudClientException;

import java.net.MalformedURLException;
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

    private int jmxPort;
    private String ispnDomain;
    private String ispnCacheName;


    public Reconfigurator( IPlatformConfiguration current, State platformState, IActuator actuator) {
        this.current = current;
        this.platformState = platformState;
        this.actuator = actuator;

        jmxPort = Config.getInstance().getInt( KeyConfig.ISPN_JMX_PORT.key() );
        ispnDomain = Config.getInstance().getString( KeyConfig.ISPN_DOMAIN.key() );
        ispnCacheName = Config.getInstance().getString( KeyConfig.ISPN_CACHE_NAME.key() );

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
            reconfigureDegree();
            reconfigureProtocol();
            reconfigureSize();

            current.setPlatformScale( request.platformSize(), InstanceConfig.MEDIUM );
            current.setRepDegree( request.replicationDegree() );
            current.setRepProtocol( request.replicationProtocol() );
            request = null;
            ControllerLogger.log.info("Reconfiguration #" + reconfigurationCounter.incrementAndGet() + " ended");
        } catch (Exception e) {
            platformState.update(PlatformState.ERROR);
            ControllerLogger.log.warn("An error occurred while reconfiguring...Reconfigs are enabled, but system state is: " + platformState.current() );
        } finally {
            if( !reconfiguring.compareAndSet(true, false) ){
                throw new RuntimeException("Some error happened! I've finished to reconfigure while controller was not reconfiguring!!!");
            }
        }
    }

    private void reconfigureSize() throws ReconfiguratorException {

        if(testing){
            ControllerLogger.log.warn("Reconfigurator is setted for skip the real reconfiguration");
        } else {

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


    private void reconfigureDegree() throws MalformedURLException, DeltaCloudClientException, InvocationException, NoJmxProtocolRegisterException {


    }





}

