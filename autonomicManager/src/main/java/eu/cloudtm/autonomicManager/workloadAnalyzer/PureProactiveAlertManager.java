package eu.cloudtm.autonomicManager.workloadAnalyzer;

import eu.cloudtm.autonomicManager.AbstractOptimizer;
import eu.cloudtm.autonomicManager.Reconfigurator;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/19/13
 * Time: 5:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class PureProactiveAlertManager extends AbstractAlertManager {

    public PureProactiveAlertManager(AbstractChangeDetector changeDetector,
                                     AbstractOptimizer optimizer,
                                     Reconfigurator reconfigurator) {
        super(optimizer, reconfigurator);
        throw new RuntimeException("NOT IMPLEMENTED YET");
        // TODO implement this
    }


    @Override
    public void workloadEventPerformed(WorkloadEvent e) {

    }
}
