package eu.cloudtm.autonomicManager.workloadAnalyzer;

import eu.cloudtm.autonomicManager.ControllerLogger;
import eu.cloudtm.autonomicManager.Optimizer;
import eu.cloudtm.autonomicManager.Reconfigurator;
import eu.cloudtm.autonomicManager.configs.Config;
import eu.cloudtm.autonomicManager.configs.KeyConfig;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/19/13
 * Time: 1:53 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractAlertManager implements WorkloadEventListener {

    public enum Policy {
        REACTIVE, PROACTIVE, MIX;
    }

    private static int SECONDS_BETWEEN_RECONFIGURATIONS = Config.getInstance().getInt(KeyConfig.ALERT_MANAGER_SECONDS_BETWEEN_RECONFIGURATIONS.key() );

    private Date lastReconfiguration;

    protected final Optimizer optimizer;
    protected final Reconfigurator reconfigurator;

    public static AbstractAlertManager createInstance(String policyStr, Optimizer optimizer, Reconfigurator reconfigurator ){
        Policy policy = Policy.valueOf(policyStr);

        AbstractAlertManager alertManager = null;
        switch (policy){
            case REACTIVE:
                alertManager = new PureReactiveAlertManager(optimizer, reconfigurator);
                break;
            case PROACTIVE:
                break;
            case MIX:
                break;
            default:
                throw new RuntimeException("Parameter not valid: " + policyStr);
        }

        return alertManager;
    }

    public AbstractAlertManager(Optimizer optimizer, Reconfigurator reconfigurator){
        this.optimizer = optimizer;
        this.reconfigurator = reconfigurator;
    }

    protected boolean isTimeToReconfigure(){
        if(lastReconfiguration != null){
            long timeDiff = Math.abs( new Date().getTime() - lastReconfiguration.getTime() ) / 1000;
            if( timeDiff < SECONDS_BETWEEN_RECONFIGURATIONS ){
                ControllerLogger.log.info("Not enough time elapsed between reconfigurations...Skipping");
                return false;
            }
        }

        lastReconfiguration = new Date();
        return true;

    }



}
