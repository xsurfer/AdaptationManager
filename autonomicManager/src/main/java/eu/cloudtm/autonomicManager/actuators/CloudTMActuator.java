package eu.cloudtm.autonomicManager.actuators;

import eu.cloudtm.InfinispanClient.InfinispanClient;
import eu.cloudtm.InfinispanClient.InfinispanClientImpl;
import eu.cloudtm.InfinispanClient.InfinispanMachine;
import eu.cloudtm.InfinispanClient.exception.InvocationException;
import eu.cloudtm.InfinispanClient.exception.NoJmxProtocolRegisterException;
import eu.cloudtm.autonomicManager.Actuator;
import eu.cloudtm.autonomicManager.ControllerLogger;
import eu.cloudtm.autonomicManager.actuators.clients.ApplicationClient;
import eu.cloudtm.autonomicManager.actuators.excepions.ActuatorException;
import eu.cloudtm.autonomicManager.commons.ReplicationProtocol;
import eu.cloudtm.autonomicManager.configs.Config;
import eu.cloudtm.autonomicManager.configs.KeyConfig;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * User: Fabio Perfetti perfabio87 [at] gmail.com Date: 7/26/13 Time: 2:57 PM
 */
public class CloudTMActuator implements Actuator {

   private static Log log = LogFactory.getLog(CloudTMActuator.class);

   private static final int RETRIES = Config.getInstance().getInt(KeyConfig.DELTACLOUD_MAX_RETRIES.key());
   private static final int DELAY_MS = Config.getInstance().getInt(KeyConfig.DELTACLOUD_SECONDS_BETWEEN_RETRY.key());

   private final String keyId = Config.getInstance().getString(KeyConfig.DELTACLOUD_KEY.key());

   private final String prefix = "cloudtm-";

   private final String imageId;

   private final String flavorId;

   private final DeltaCloudClient deltaCloudClient;

   /* APPLICATION CLIENT */
   private final ApplicationClient applicationClient;

   private final int jmxPort;

   private final String ispnDomain;
   private final String ispnCacheName;

   /**
    * @param deltaCloudClient
    * @param jmxPort
    * @param imageId
    * @param flavorId
    */
   @Deprecated
   public CloudTMActuator(DeltaCloudClient deltaCloudClient,
                          int jmxPort,
                          String imageId,
                          String flavorId,
                          String ispnDomain,
                          String ispnCacheName) {
      this.applicationClient = null;
      this.deltaCloudClient = deltaCloudClient;
      this.jmxPort = jmxPort;
      this.imageId = imageId;
      this.flavorId = flavorId;
      this.ispnDomain = ispnDomain;
      this.ispnCacheName = ispnCacheName;
   }

   /**
    * @param deltaCloudClient
    * @param jmxPort
    * @param imageId
    * @param flavorId
    */
   public CloudTMActuator( ApplicationClient applicationClient,
                          DeltaCloudClient deltaCloudClient,
                          int jmxPort,
                          String imageId,
                          String flavorId,
                          String ispnDomain,
                          String ispnCacheName) {
      this.applicationClient = applicationClient;
      this.deltaCloudClient = deltaCloudClient;
      this.jmxPort = jmxPort;
      this.imageId = imageId;
      this.flavorId = flavorId;
      this.ispnDomain = ispnDomain;
      this.ispnCacheName = ispnCacheName;
   }

   @Override
   public void stopApplication(String machine) throws ActuatorException {
      if( applicationClient!=null ){
         ControllerLogger.log.info(" * Stopping application on machine " + machine);
         applicationClient.stop(machine, jmxPort);
      }
   }

   @Override
   public synchronized void stopInstance() throws ActuatorException {
      int expectedSize = runningInstances().size() - 1;

      ControllerLogger.log.info(" * Stopping an instance...");

      List<Instance> instances = runningDeltaCloudInstances();
      Instance instance = instances.get(0);
      String host = instance.getPrivateAddresses().get(0);
      stopApplication(host);

      try {
         instance.stop(deltaCloudClient);
      } catch (DeltaCloudClientException e) {
         throw new ActuatorException(e);
      }

      waitWhile(expectedSize);
      ControllerLogger.log.info("Instance " + instance.getName() + " stopped");
   }

   @Override
   public synchronized void startInstance() throws ActuatorException {
      ControllerLogger.log.info(" * Starting a new instance...");
      int expectedSize = runningInstances().size() + 1;
      String instanceName = prefix.concat(String.valueOf(runningInstances().size()));
      Instance newInstance;
      try {
         newInstance = deltaCloudClient.createInstance(instanceName, imageId, flavorId, null, keyId, null, null);
      } catch (DeltaCloudClientException e) {
         throw new ActuatorException(e);
      }

      waitWhile(expectedSize);

      ControllerLogger.log.info(" * Instance successfully started...");
      return;
   }

   private void waitWhile(int expectedNumNodes) throws ActuatorException {

      ScheduledExecutorService actionExecutor = Executors.newSingleThreadScheduledExecutor();
      int retries = 0;
      int currSize = -1;

      while (retries++ < RETRIES && expectedNumNodes != currSize) {
         ControllerLogger.log.info(" * Waiting... (retry " + retries + "/" + RETRIES + ")");

         Future<Integer> future = actionExecutor.schedule(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
               return runningInstances().size();
            }
         }, DELAY_MS, TimeUnit.SECONDS);

         try {
            currSize = future.get();
            ControllerLogger.log.info("# running instances: " + currSize);
         } catch (InterruptedException e) {
            throw new RuntimeException(e);
         } catch (ExecutionException e) {
            throw new RuntimeException(e);
         }
      }
      actionExecutor.shutdown();

      if (retries >= RETRIES) {
         throw new ActuatorException("Max Retries Reached");
      }
   }

   @Override
   public void switchProtocol(ReplicationProtocol repProtocol) throws ActuatorException {

      Set<InfinispanMachine> ispnMachines = instacesToIspnMachines();
      InfinispanClient infinispanClient = new InfinispanClientImpl(ispnMachines, ispnDomain, ispnCacheName);

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
      InfinispanClient infinispanClient = new InfinispanClientImpl(ispnMachines, ispnDomain, ispnCacheName);

      try {
         infinispanClient.triggerBlockingSwitchReplicationDegree(degree);

      } catch (InvocationException e) {
         ControllerLogger.log.warn(e);
         throw new ActuatorException(e);
      } catch (NoJmxProtocolRegisterException e) {
         ControllerLogger.log.warn(e);
         throw new ActuatorException(e);
      }
   }

   @Override
   public void triggerRebalancing(boolean enabled) throws ActuatorException {
      Set<InfinispanMachine> ispnMachines = instacesToIspnMachines();
      InfinispanClient infinispanClient = new InfinispanClientImpl(ispnMachines, ispnDomain, ispnCacheName);

      try {
         infinispanClient.triggerRebalancing(enabled);

      } catch (InvocationException e) {
         ControllerLogger.log.warn(e);
         throw new ActuatorException(e);
      } catch (NoJmxProtocolRegisterException e) {
         ControllerLogger.log.warn(e);
         throw new ActuatorException(e);
      }

   }

   @Override
   public List<String> runningInstances() {

      List<String> runningInstances = new ArrayList<String>();

      for (Instance instance : runningDeltaCloudInstances()) {
         if (instance.getState() == StateAware.State.RUNNING)
            runningInstances.add(instance.getPrivateAddresses().get(0));
      }
      return runningInstances;
   }


   private List<Instance> runningDeltaCloudInstances() {
      List<Instance> allInstances;
      List<Instance> runningInstances = new ArrayList<Instance>();

      try {
         allInstances = deltaCloudClient.listInstances();
      } catch (DeltaCloudClientException e) {
         throw new RuntimeException(e);
      }

      for (Instance instance : allInstances) {
         if (instance.getState() == StateAware.State.RUNNING)
            runningInstances.add(instance);
      }
      return runningInstances;
   }


   private Set<InfinispanMachine> instacesToIspnMachines() {
      Set<InfinispanMachine> ispnMachines = new HashSet<InfinispanMachine>();
      for (Instance instance : runningDeltaCloudInstances()) {
         String hostname = instance.getName();
         String address = instance.getPrivateAddresses().get(0);
         log.info("Mapping DeltaCloud instances to infinispan machines (" + hostname + ", " + address + ")");

         ispnMachines.add(new InfinispanMachine(hostname, jmxPort, address));
      }
      return ispnMachines;
   }

}