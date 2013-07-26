package eu.cloudtm.autonomicManager.actuators;

import eu.cloudtm.InfinispanClient;
import eu.cloudtm.InfinispanClientImpl;
import eu.cloudtm.InfinispanMachine;
import eu.cloudtm.autonomicManager.IActuator;
import eu.cloudtm.autonomicManager.actuators.excepions.RadargunException;
import eu.cloudtm.commons.ReplicationProtocol;
import eu.cloudtm.exception.InvocationException;
import eu.cloudtm.exception.NoJmxProtocolRegisterException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.deltacloud.client.DeltaCloudClient;
import org.apache.deltacloud.client.DeltaCloudClientException;
import org.apache.deltacloud.client.Instance;
import org.apache.deltacloud.client.StateAware;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/26/13
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class CloudTMActuator implements IActuator {

    private static Log log = LogFactory.getLog(CloudTMActuator.class);

    private static final int RETRIES = 5;
    private static final int DELAY_MS = 5000;

    private final String prefix = "cloudtm-";

    private final String imageId;

    private final String flavorId;

    private final DeltaCloudClient deltaCloudClient;
    private final RadargunClient radargunClient;

    private final boolean isRadargun;
    private final int jmxPort;

    private final String ispnDomain;
    private final String ispnCacheName;

    /**
     *
     * @param deltaCloudClient
     * @param jmxPort
     * @param imageId
     * @param flavorId
     */
    public CloudTMActuator(DeltaCloudClient deltaCloudClient,
                           int jmxPort,
                           String imageId,
                           String flavorId,
                           String ispnDomain,
                           String ispnCacheName){
        this.deltaCloudClient = deltaCloudClient;
        this.radargunClient = null;
        this.isRadargun = false;
        this.jmxPort = jmxPort;
        this.imageId = imageId;
        this.flavorId = flavorId;
        this.ispnDomain = ispnDomain;
        this.ispnCacheName = ispnCacheName;
    }

    public CloudTMActuator(DeltaCloudClient deltaCloudClient,
                           RadargunClient radargunClient,
                           int jmxPort,
                           String imageId,
                           String flavorId,
                           String ispnDomain,
                           String ispnCacheName){
        this.deltaCloudClient = deltaCloudClient;
        this.radargunClient = radargunClient;
        this.isRadargun = true;
        this.jmxPort = jmxPort;
        this.imageId = imageId;
        this.flavorId = flavorId;
        this.ispnDomain = ispnDomain;
        this.ispnCacheName = ispnCacheName;
    }

    @Override
    public synchronized void stopInstance() throws ActuatorException {
        List<Instance> instances = runningInstances();
        Instance instance = instances.get(0);
        if(isRadargun){
            String host = instance.getPrivateAddresses().get(0);
            try {
                radargunClient.stop(host, jmxPort);
            } catch (RadargunException e) {
                log.warn("Trying to stopping Radargun thrown an exception. I'm reporting the error and going ahead");
            }
        }
        try {
            instance.stop(deltaCloudClient);
        } catch (DeltaCloudClientException e) {
            throw new ActuatorException(e);
        }
    }

    @Override
    public synchronized void startInstance() throws ActuatorException {
        int expectedSize = runningInstances().size() +1;
        String instanceName = prefix.concat( String.valueOf( runningInstances().size() ) );
        Instance newInstance;
        try {
            newInstance = deltaCloudClient.createInstance(instanceName, imageId, flavorId, null, null, null);
        } catch (DeltaCloudClientException e) {
            throw new ActuatorException(e);
        }

        ScheduledExecutorService actionExecutor = Executors.newSingleThreadScheduledExecutor();
        int retries = RETRIES;
        int currSize = -1;

        while (retries-- > 0 && expectedSize!=currSize){
            Future<Integer> future = actionExecutor.schedule(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return runningInstances().size();
                }
            }, DELAY_MS, TimeUnit.MILLISECONDS);
            try {
                currSize = future.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        actionExecutor.shutdown();

        if(retries<=0){
            throw new ActuatorException("Max Retries Reached");
        }
        return;
    }

    @Override
    public void switchProtocol(ReplicationProtocol repProtocol) throws ActuatorException {


        Set<InfinispanMachine> ispnMachines = instacesToIspnMachines();
        InfinispanClient infinispanClient = new InfinispanClientImpl(ispnMachines, ispnDomain, ispnCacheName, jmxPort);

        try {
            infinispanClient.triggerBlockingSwitchReplicationProtocol(repProtocol.getWpmValue(), false, false);


        } catch (InvocationException e) {
            throw new ActuatorException(e);
        } catch (NoJmxProtocolRegisterException e) {
            throw new ActuatorException(e);
        }

    }

    @Override
    public void switchDegree(int degree) throws ActuatorException {

        Set<InfinispanMachine> ispnMachines = instacesToIspnMachines();
        InfinispanClient infinispanClient = new InfinispanClientImpl(ispnMachines, ispnDomain, ispnCacheName, jmxPort);

        try {
            infinispanClient.triggerBlockingSwitchReplicationDegree(degree);

        } catch (InvocationException e) {
            throw new ActuatorException(e);
        } catch (NoJmxProtocolRegisterException e) {
            throw new ActuatorException(e);
        }
    }

    @Override
    public synchronized List<Instance> runningInstances(){
        List<Instance> allInstances;
        List<Instance> runningInstances = new ArrayList<Instance>();

        try {
            allInstances = deltaCloudClient.listInstances();
        } catch (DeltaCloudClientException e) {
            throw new RuntimeException(e);
        }

        for(Instance instance : allInstances){
            if(instance.getState()== StateAware.State.RUNNING)
                runningInstances.add(instance);
        }
        return runningInstances;
    }


    private Set<InfinispanMachine> instacesToIspnMachines() {
        Set<InfinispanMachine> ispnMachines = new HashSet<InfinispanMachine>();
        for( Instance instance : runningInstances() ){
            String address = instance.getPrivateAddresses().get(0);
            ispnMachines.add( new InfinispanMachine(address, jmxPort) );
        }
        return ispnMachines;
    }


}
