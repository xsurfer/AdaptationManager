package eu.cloudtm.autonomicManager.workloadAnalyzer;

import eu.cloudtm.autonomicManager.Optimizer;
import eu.cloudtm.autonomicManager.Reconfigurator;
import eu.cloudtm.autonomicManager.configs.ReconfigurationParamSelf;
import eu.cloudtm.autonomicManager.optimizers.OptimizerType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA. User: fabio Date: 7/19/13 Time: 5:26 PM To change this template use File | Settings |
 * File Templates.
 */
public class PureReactiveAlertManager extends AbstractAlertManager {

   private static Log log = LogFactory.getLog(PureReactiveAlertManager.class);

   private static AtomicInteger counterWorkloadChanged = new AtomicInteger(0);
   private static AtomicInteger counterWorkloadWillChanged = new AtomicInteger(0);

   public PureReactiveAlertManager(Optimizer optimizer, Reconfigurator reconfigurator) {
      super(optimizer, reconfigurator);
   }

   @Override
   public void workloadEventPerformed(WorkloadEvent event) {

      if (event.getEventType().equals(WorkloadEvent.WorkloadEventType.WORKLOAD_CHANGED)) {
         counterWorkloadChanged.incrementAndGet();
         log.warn("RECEIVED " + event.getEventType() + " event # " + counterWorkloadChanged.get() + " !");

      } else if (event.getEventType().equals(WorkloadEvent.WorkloadEventType.WORKLOAD_WILL_CHANGE)) {
         counterWorkloadWillChanged.incrementAndGet();
         log.warn("RECEIVED " + event.getEventType() + " event # " + counterWorkloadWillChanged.get() + " !");
      }

      if (!event.getEventType().equals(WorkloadEvent.WorkloadEventType.WORKLOAD_CHANGED)) {
         return;
      }

      boolean reconfigurationDone = false;
      if (hasPassedEnoughTimeSinceLastReconfiguration()) {
         log.trace("Enough time has passed since last reconfiguration");
         //TODO: Fabio I think that we could do a way simpler thing: optimizeAll should return a special value (even null) to tell
         //TODO that actually there is no optimization to do (this can be the case also if the workload changes: maybe we are in any case
         //TODO in the optimal configuration. In this way we can avoid triggering anything beyond this level and simply go on
         Map<OptimizerType, Object> optimization = optimizer.optimizeAll(event.getSample(), false);
         if (optimization != null) {
            log.trace("Going to invoke reconfigure");
            try {
               //This reconfiguration is automatic so...
               reconfigurationDone = reconfigurator.reconfigure(optimization, new ReconfigurationParamSelf());
            } catch (Exception e) {
               e.printStackTrace();
               log.fatal(e);
            }
            if (reconfigurationDone) {
               resetTimer();
               log.trace("Reconfiguration done!");
            } else {
               log.trace("Reconfiguration not actually performed");
            }
         } else {
            log.trace("Optimization result is null!!");
         }
      }

   }
}
