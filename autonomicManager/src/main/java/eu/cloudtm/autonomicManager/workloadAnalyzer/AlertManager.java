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
public abstract class AlertManager implements WorkloadEventListener {

    public enum Policy {
        REACTIVE, PROACTIVE, MIX;
    }

    protected Optimizer optimizer;
    protected Reconfigurator reconfigurator;

    public static AlertManager createInstance(String policyStr, Optimizer optimizer, Reconfigurator reconfigurator ){
        Policy policy = Policy.valueOf(policyStr);

        AlertManager alertManager = null;
        switch (policy){
            case REACTIVE:
                alertManager = new PureReactiveAlertManager(optimizer, reconfigurator);
                break;
            case PROACTIVE:
                break;
            case MIX:
                break;
            default:
                throw new RuntimeException("Paramer not valid: " + policyStr);
        }

        return alertManager;
    }

    public AlertManager(Optimizer optimizer, Reconfigurator reconfigurator){
        this.optimizer = optimizer;
        this.reconfigurator = reconfigurator;
    }



}
