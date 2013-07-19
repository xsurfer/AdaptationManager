package eu.cloudtm.autonomicManager.workloadAnalyzer;

import eu.cloudtm.autonomicManager.Optimizer;
import eu.cloudtm.autonomicManager.Reconfigurator;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/19/13
 * Time: 5:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class PureProactiveAlertManager extends AlertManager {

    public PureProactiveAlertManager(ChangeDetector changeDetector,
                                     Optimizer optimizer,
                                     Reconfigurator reconfigurator) {
        super(optimizer, reconfigurator);
    }

    @Override
    public void workloadChanged(WorkloadEvent e) {

    }

    @Override
    public void workloadWillChange(WorkloadEvent e) {

    }
}
