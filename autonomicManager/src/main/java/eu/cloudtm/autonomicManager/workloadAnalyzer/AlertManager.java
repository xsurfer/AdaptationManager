package eu.cloudtm.autonomicManager.workloadAnalyzer;

import eu.cloudtm.autonomicManager.AbstractOptimizer;
import eu.cloudtm.autonomicManager.IReconfigurator;
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

    protected AbstractOptimizer optimizer;
    protected IReconfigurator reconfigurator;

    public static AlertManager createInstance(String policyStr, AbstractOptimizer optimizer, IReconfigurator reconfigurator ){
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

    public AlertManager(AbstractOptimizer optimizer, IReconfigurator reconfigurator){
        this.optimizer = optimizer;
        this.reconfigurator = reconfigurator;
    }



}
