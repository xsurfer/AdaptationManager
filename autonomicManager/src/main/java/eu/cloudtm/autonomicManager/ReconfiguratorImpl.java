package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.actuators.excepions.ActuatorException;
import eu.cloudtm.autonomicManager.commons.*;
import eu.cloudtm.autonomicManager.configs.Config;
import eu.cloudtm.autonomicManager.configs.KeyConfig;
import eu.cloudtm.autonomicManager.configs.ReconfigurationParam;
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
 * Created by: Fabio Perfetti E-mail: perfabio87@gmail.com Date: 6/16/13
 */
public class ReconfiguratorImpl implements Reconfigurator {

   private final Log log = LogFactory.getLog(ReconfiguratorImpl.class);
   private final State platformState;
   private final PlatformConfiguration current;
   private final boolean ignoreError = Config.getInstance().getBoolean(KeyConfig.RECONFIGURATOR_IGNORE_ERROR.key());
   private final boolean testing = Config.getInstance().getBoolean(KeyConfig.RECONFIGURATOR_SIMULATE.key());
   private final int rebalanceSleep = Config.getInstance().getInt(KeyConfig.RECONFIGURATOR_SLEEP_REBALANCING.key());
   private AtomicInteger reconfigurationCounter = new AtomicInteger(0);
   private volatile Map<OptimizerType, Object> request;
   private volatile ReconfigurationParam reconfigurationParam;
   private AtomicBoolean reconfiguring = new AtomicBoolean(false);
   private Throwable error;
   private ExecutorService executorService = Executors.newSingleThreadExecutor();
   private Actuator actuator;
   private ReentrantLock reconfigurationLock = new ReentrantLock();

   public ReconfiguratorImpl(PlatformConfiguration current, State platformState, Actuator actuator) {
      this.current = current;
      this.platformState = platformState;
      this.actuator = actuator;

   }

   @Override
   public boolean reconfigure(Map<OptimizerType, Object> toReconfigure, ReconfigurationParam param) {

      // in case of error during the last reconfiguration, I'm throwing a RuntimeException
      if (error != null) {
         ControllerLogger.log.info("An error occurred during previous reconfiguration...throwing an exception!");
         throw new RuntimeException(error);
      }


      ControllerLogger.log.info("Trying to acquire the reconfiguration lock");
      if (reconfigurationLock.tryLock()) {
         ControllerLogger.log.info("Lock successfully acquired");
         try {
            if (reconfiguring.compareAndSet(false, true)) {
               request = toReconfigure;
               reconfigurationParam = param;
               if (skipReconfiguration()) {
                  ControllerLogger.log.trace("No reconfiguration needed at this step: current and target configuration coincide");
                  request = null;
                  reconfigurationParam = null;
                  reconfiguring.compareAndSet(true, false);
                  return false;
               }
               ControllerLogger.log.info("Reconfiguration request ACCEPTED...");
               executorService.submit(new Runnable() {
                  @Override
                  public void run() {
                     start();
                  }
               });
               return true;
            } else {
               ControllerLogger.log.info("Reconfiguration request DECLINED as compareAndSet was unsuccessful");
               return false;
            }
         } catch (Exception ee) {
            ee.printStackTrace();
            log.error(ee.getCause());
            reconfiguring.compareAndSet(true, false);
            return false;
         } finally {
            ControllerLogger.log.info("releasing lock");
            reconfigurationLock.unlock();
         }
      } else {
         ControllerLogger.log.info("Lock NOT successfully acquired!");
         return false;
      }
   }

   public boolean isReconfiguring() {
      return reconfiguring.get();
   }

   //TODO: I don't understand the need for a thread here. Isn't this supposed to be a singleton?  Request should not be an instance variable then
   private boolean skipReconfiguration() {
      PlatformConfiguration toReconfigurePlatform = (PlatformConfiguration) request.get(OptimizerType.PLATFORM);
      //TODO: this checks only nodes, RP and RD. No Autoplacer, no num threads. Is this correct?
      //This checks that the target config is the same as the current AND there are no other optimizations to do!
      return current.compareTo(toReconfigurePlatform) == 0 && request.size() == 1;
   }

   private void start() {
      boolean info = ControllerLogger.log.isInfoEnabled();

      if (info) {
         ControllerLogger.log.info("#####################");
         ControllerLogger.log.info("Starting a new reconfigurarion request");
         ControllerLogger.log.info("#####################");
      }
      try {
         platformState.update(PlatformState.RECONFIGURING);
         if (testing) {
            ControllerLogger.log.warn("Simulating reconfiguration...No instance will be changed!");
         } else {

            PlatformConfiguration toReconfigurePlatform = (PlatformConfiguration) request.get(OptimizerType.PLATFORM);
            if (toReconfigurePlatform != null && !forceSkipReconfiguration(current, toReconfigurePlatform, reconfigurationParam)) {
               platformReconfiguration(toReconfigurePlatform);
            } else {
               log.info("No reconfiguration to perform");
            }
         }

         request = null;
         reconfigurationParam = null;
         platformState.update(PlatformState.RUNNING);

      } catch (Exception e) {     // capturing all the exceptions because if ignoreError=false, AM must die
         platformState.update(PlatformState.ERROR);

         ControllerLogger.log.warn("An error occurred while reconfiguring. " +
                 "Please check the log.out file for stack traces. " +
                 "Reconfigs are enabled, but system state is: " + platformState.current());

         log.warn(e, e);
         if (!ignoreError) {
            error = e;  // Saving the error, it will be thrown ASAP by reconfigure method
         }
      } finally {

         if (!reconfiguring.compareAndSet(true, false)) {
            error = new RuntimeException("Some error happened! I've finished to reconfigure while controller was not reconfiguring!!!");
         }
      }

      ControllerLogger.log.info("*********************");
      ControllerLogger.log.info("Reconfiguration #" + reconfigurationCounter.incrementAndGet() + " ended");
      ControllerLogger.log.info("*********************");

   }

   private void changeReplicationDegreeIfNeeded(PlatformConfiguration platformRequest) throws ReconfiguratorException {
      int current, target;
      current = this.current.replicationDegree();
      target = platformRequest.replicationDegree();
      if (Config.getInstance().getBoolean(KeyConfig.RECONFIGURATOR_RECONFIGURE_DEGREE.key()) && (current != target)) {
         ControllerLogger.log.info("Reconfiguring replication degree from " + current + " to " + target);
         reconfigureDegree(target);
         current = this.current.replicationDegree();
         ControllerLogger.log.info("Replication degree successfully switched to " + target + " !");

      } else {
         ControllerLogger.log.info("Skipping reconfiguring replication degree");
      }
   }

   private void changeScaleIfNeeded(PlatformConfiguration platformRequest) throws ReconfiguratorException {
      int current, target;
      current = this.current.platformSize();
      target = platformRequest.platformSize();
      if (Config.getInstance().getBoolean(KeyConfig.RECONFIGURATOR_RECONFIGURE_NODES.key())) {
         ControllerLogger.log.info("Reconfiguring scale from " + current + " to " + target + " nodes");
         reconfigureSize(platformRequest.platformSize());
         ControllerLogger.log.info("Scale successfully switched to " + target + " !");
      } else {
         ControllerLogger.log.info("Skipping reconfiguring scale");
      }
   }

   private void changeReplicationProtocolIfNeeded(PlatformConfiguration platformRequest) throws ReconfiguratorException {
      ReplicationProtocol current = this.current.replicationProtocol();
      ReplicationProtocol target = platformRequest.replicationProtocol();
      if (Config.getInstance().getBoolean(KeyConfig.RECONFIGURATOR_RECONFIGURE_PROTOCOL.key()) && !(target.equals(current))) {
         ControllerLogger.log.info("Reconfiguring protocol");
         reconfigureProtocol(platformRequest.replicationProtocol());
         ControllerLogger.log.info("Replication protocol successfully switched to " + target + " !");
      } else {
         ControllerLogger.log.info("Skipping replication protocol");
      }
   }

   private void triggerRebalanceIfNeeded(boolean trigger) throws ReconfiguratorException {
      final String enabled = trigger ? "State transfer enabled.." : "State transfer disabled...";
      final String bool = trigger ? "(true)" : "(false)";
      if (Config.getInstance().getBoolean(KeyConfig.RECONFIGURATOR_SWITCH_REBALANCING.key())) {
         try {
            actuator.triggerRebalancing(trigger);
            if (log.isInfoEnabled())
               log.info(enabled);
         } catch (ActuatorException e) {
            throw new ReconfiguratorException(e);
         }
      } else {
         ControllerLogger.log.info("Skipping triggerRebalancing" + bool + " because disabled!");
      }
   }

   //Given that the exception is not handled...
   private void sleep(int msecToSleep) {
      log.info("Waiting " + msecToSleep + " msecs");
      try {
         Thread.sleep(msecToSleep);
      } catch (InterruptedException e) {
         log.warn("Thread Interrupted!");
      }
   }

   private void platformReconfiguration(PlatformConfiguration platformRequest) throws ReconfiguratorException {


      triggerRebalanceIfNeeded(false);

      changeReplicationDegreeIfNeeded(platformRequest);

      changeScaleIfNeeded(platformRequest);

      // TODO CHECK THE NUM NODEs
      sleep(rebalanceSleep);

      triggerRebalanceIfNeeded(true);

      changeReplicationProtocolIfNeeded(platformRequest);


      ControllerLogger.log.info("Updating Current Configuration");
      current.setPlatformScale(platformRequest.platformSize(), InstanceConfig.MEDIUM);
      current.setRepDegree(platformRequest.replicationDegree());
      current.setRepProtocol(platformRequest.replicationProtocol());

   }

   private void reconfigureSize(int platformSize) throws ReconfiguratorException {

      int currSize = actuator.runningInstances().size();
      int numInstancesToChange = platformSize - currSize; //  <<< COULD BE NEGATIVE, use Math.abs() >>>
      //ControllerLogger.log.info("To change: " + numInstancesToChange );


      if (numInstancesToChange > 0) {
         while (numInstancesToChange-- > 0) {
            try {
               ControllerLogger.log.trace(numInstancesToChange + " instances(s) more to be started...");
               actuator.startInstance();
            } catch (ActuatorException e) {
               throw new ReconfiguratorException(e);
            }
         }
      } else if (numInstancesToChange < 0) {
         while (numInstancesToChange++ < 0) {
            try {
               ControllerLogger.log.trace(-numInstancesToChange + " instances(s) more to be stopped...");
               actuator.stopInstance();
            } catch (ActuatorException e) {
               throw new ReconfiguratorException(e);
            }
         }
      }

   }

   private void reconfigureProtocol(ReplicationProtocol replicationProtocol) throws ReconfiguratorException {
      boolean forceStop = Config.getInstance().getBoolean(KeyConfig.ISPN_ACTUATOR_FORCE_STOP.key());
      boolean abortOnStop = Config.getInstance().getBoolean(KeyConfig.ISPN_ACTUATOR_ABORT_ON_STOP.key());

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

   private boolean forceSkipReconfiguration(PlatformConfiguration from, PlatformConfiguration to, ReconfigurationParam param) {
      log.trace("Force skip?");
      if (!param.isAutoTuning()) {
         log.trace("Autotuning is FALSE, thus NOT forcing skip");
         return false;
      }
      if (Config.getInstance().enforceStability()) {
         log.trace("Enforcing stability");
         ReplicationProtocol fromRP = from.replicationProtocol(), toRP = to.replicationProtocol();
         boolean allowedSwitch = (fromRP == ReplicationProtocol.TWOPC && toRP == ReplicationProtocol.PB) ||
                 (fromRP == ReplicationProtocol.PB && toRP == ReplicationProtocol.TO);
         if (allowedSwitch) {
            log.trace("Switch allowed " + from + " to " + to);
         } else {
            log.trace("Switch not allowed " + from + " to " + to);
         }
         return !allowedSwitch;
      }
      log.trace("Not enforcing stability->not skipping");
      return false;
   }

}

