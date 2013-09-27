package eu.cloudtm.autonomicManager.workloadAnalyzer;

import eu.cloudtm.autonomicManager.Optimizer;
import eu.cloudtm.autonomicManager.Reconfigurator;
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

      if (isTimeToReconfigure()) {
         log.trace("It's safe reconfigure...");
         Map<OptimizerType, Object> optimization = optimizer.optimizeAll(event.getSample(), false);
         if (optimization != null) {
            reconfigurator.reconfigure(optimization);
         } else {
            log.trace("Optimization result is null!!");
         }
      }

   }
}
