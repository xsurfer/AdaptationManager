package eu.cloudtm.autonomicManager.workloadAnalyzer;

import eu.cloudtm.autonomicManager.Optimizer;
import eu.cloudtm.autonomicManager.Reconfigurator;
import eu.cloudtm.autonomicManager.optimizers.OptimizerType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/19/13
 * Time: 5:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class PureReactiveAlertManager extends AbstractAlertManager {

    private static Log log = LogFactory.getLog(PureReactiveAlertManager.class);


    public PureReactiveAlertManager(Optimizer optimizer, Reconfigurator reconfigurator) {
        super(optimizer, reconfigurator);
    }

    @Override
    public void workloadEventPerformed(WorkloadEvent event) {

        if( !event.getEventType().equals(WorkloadEvent.WorkloadEventType.WORKLOAD_CHANGED) ){
            return;
        }

        if( isTimeToReconfigure() ){
            Map<OptimizerType, Object> optimization = optimizer.optimizeAll(event.getSample(), false);
            if(optimization!=null){
                reconfigurator.reconfigure(optimization);
            } else {
                log.info("Optimization result is null!!");
            }
        }

    }
}
