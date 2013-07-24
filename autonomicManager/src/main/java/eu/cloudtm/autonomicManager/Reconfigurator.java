package eu.cloudtm.autonomicManager;

import eu.cloudtm.InfinispanActuator;
import eu.cloudtm.InfinispanActuatorFactory;
import eu.cloudtm.InfinispanMachine;
import eu.cloudtm.autonomicManager.actuators.DeltaCloudActuator;
import eu.cloudtm.autonomicManager.actuators.DeltaCloudActuatorFactory;
import eu.cloudtm.autonomicManager.configs.Config;
import eu.cloudtm.autonomicManager.configs.KeyConfig;
import eu.cloudtm.commons.*;
import eu.cloudtm.autonomicManager.exceptions.ReconfiguratorException;
import eu.cloudtm.exception.InvocationException;
import eu.cloudtm.exception.NoJmxProtocolRegisterException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.deltacloud.client.DeltaCloudClientException;
import org.apache.deltacloud.client.Instance;

import java.net.MalformedURLException;
import java.util.List;
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

    private DeltaCloudActuator deltaCloudActuator;


    public Reconfigurator( IPlatformConfiguration current, State platformState) {
        this.current = current;
        this.platformState = platformState;
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
            //reconfigureDegree();
            //reconfigureProtocol();
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

    private void reconfigureSize() throws MalformedURLException, DeltaCloudClientException {


        deltaCloudActuator = DeltaCloudActuatorFactory.instance().build();

        if(testing){
            ControllerLogger.log.warn("Actuator is disabled");
        } else {
            deltaCloudActuator.actuate(request.platformSize(), request.threadPerNode());
        }

    }

    private void reconfigureProtocol() {
        int jmxPort = Config.getInstance().getInt( KeyConfig.ISPN_JMX_PORT.key() );

        String ispn_domain = Config.getInstance().getString( KeyConfig.ISPN_DOMAIN.key() );
        String ispn_cacheName = Config.getInstance().getString( KeyConfig.ISPN_CACHE_NAME.key() );

        boolean forceStop = Config.getInstance().getBoolean( KeyConfig.ISPN_ACTUATOR_FORCE_STOP.key() );
        boolean abortOnStop = Config.getInstance().getBoolean( KeyConfig.ISPN_ACTUATOR_ABORT_ON_STOP.key() );

        String fenixFrameworkDomain = Config.getInstance().getString( KeyConfig.ISPN_ACTUATOR_FENIX_FRAMEWORK.key() );
        String applicationName = Config.getInstance().getString( KeyConfig.ISPN_ACTUATOR_APPLICATION_NAME.key() );

        InfinispanActuator infinispanActuator = InfinispanActuatorFactory.getInstance().createActuator();
        List<Instance> instances = deltaCloudActuator.runningInstances();
        for( Instance instance : instances ){
            InfinispanMachine machine = InfinispanActuatorFactory.getInstance().createMachine(instance.getPrivateAddresses().get(0), jmxPort );
            infinispanActuator.addMachine( machine );
        }
        try {
            infinispanActuator.triggerBlockingSwitchReplicationProtocol(ispn_domain, ispn_cacheName, fenixFrameworkDomain, applicationName, request.replicationProtocol().getWpmValue(), forceStop, abortOnStop);
            ControllerLogger.log.warn("New reconfiguration degree successfully executed");
        } catch (NoJmxProtocolRegisterException e) {
            throw new RuntimeException(e);
        } catch (InvocationException e) {
            ControllerLogger.log.warn("Error while triggering a new reconfiguration degree");
            throw new RuntimeException(e);
        }


    }

    private void reconfigureDegree(){

        int jmxPort = Config.getInstance().getInt( KeyConfig.ISPN_JMX_PORT.key() );
        String ispn_domain = Config.getInstance().getString( KeyConfig.ISPN_DOMAIN.key() );
        String ispn_cacheName = Config.getInstance().getString( KeyConfig.ISPN_CACHE_NAME.key() );

        InfinispanActuator infinispanActuator = InfinispanActuatorFactory.getInstance().createActuator();
        List<Instance> instances = deltaCloudActuator.runningInstances();
        for( Instance instance : instances ){
            InfinispanMachine machine = InfinispanActuatorFactory.getInstance().createMachine(instance.getPrivateAddresses().get(0), jmxPort );
            infinispanActuator.addMachine( machine );
        }
        try {
            infinispanActuator.triggerBlockingNewReplicationDegree(ispn_domain, ispn_cacheName, request.replicationDegree());
            ControllerLogger.log.warn("New reconfiguration degree successfully executed");
        } catch (NoJmxProtocolRegisterException e) {
            throw new RuntimeException(e);
        } catch (InvocationException e) {
            ControllerLogger.log.warn("Error while triggering a new reconfiguration degree");
            throw new RuntimeException(e);
        }

    }


}

