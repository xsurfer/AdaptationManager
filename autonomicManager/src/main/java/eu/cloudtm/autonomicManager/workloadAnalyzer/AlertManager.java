package eu.cloudtm.autonomicManager.workloadAnalyzer;

import eu.cloudtm.autonomicManager.Optimizer;
import eu.cloudtm.autonomicManager.Reconfigurator;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/19/13
 * Time: 1:53 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AlertManager implements WorkloadAdapter {

    protected Optimizer optimizer;
    protected Reconfigurator reconfigurator;

    public static AlertManager createInstance(String policy, Optimizer optimizer, Reconfigurator reconfigurator ){
        AlertManager alertManager = null;
        if(policy.equals("REACTIVE")){
            alertManager = new PureReactiveAlertManager(optimizer, reconfigurator);

        } else if(policy.equals("PROACTIVE")){

        } else { // REACTIVE-PROACTIVE

        }
        return alertManager;
    }

    public AlertManager(Optimizer optimizer, Reconfigurator reconfigurator){
        this.optimizer = optimizer;
        this.reconfigurator = reconfigurator;
    }



}
