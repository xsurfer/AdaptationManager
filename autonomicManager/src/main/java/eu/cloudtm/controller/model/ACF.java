package eu.cloudtm.controller.model;

import eu.cloudtm.LookupRegister;
import eu.cloudtm.StatsManager;
import eu.cloudtm.controller.oracles.common.PublishAttributeException;
import eu.cloudtm.wpm.logService.remote.events.PublishAttribute;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/18/13
 */
public class ACF {

    private static Log log = LogFactory.getLog(ACF.class);

    private static double getLocalAbortProb(Set<HashMap<String, PublishAttribute>> JMXvalues) throws PublishAttributeException {
        double puts = StatsManager.getAvgAttribute("NumPuts", JMXvalues);
        log.trace("Attempted put " + puts);
        double okPuts = StatsManager.getAvgAttribute("PaoloLocalTakenLocks", JMXvalues);
        log.trace("Ok put " + okPuts);
        if (puts != 0) {
            return (puts - okPuts) / puts;
        }
        return 0;
    }

    public static double evaluate(Set<HashMap<String, PublishAttribute>> JMXvalues, double threads, double timeWindow) throws PublishAttributeException {

        double otherThreads = threads - 1.0D;
        double pCont = getLocalAbortProb(JMXvalues);
        log.trace("pCont is " + pCont);
        if (pCont == 0)
            return 0D;
        double otherThreadsFraction = otherThreads / threads;
        double paoloLocalLocks = StatsManager.getAvgAttribute("PaoloLocalTakenLocks", JMXvalues);
        double paoloRemoteLocks = StatsManager.getAvgAttribute("PaoloRemoteTakenLocks", JMXvalues);
        double otherLocalLocks = paoloLocalLocks * otherThreadsFraction;
        log.trace("OtherLocalLocks " + otherLocalLocks);
        double ll = 1e-9 * otherLocalLocks / timeWindow;
        log.trace("localLambda " + ll);
        double rl = 1e-9 * paoloRemoteLocks / timeWindow;
        log.trace("remoteLambda " + rl);
        double lh = pLocalHoldTime(JMXvalues);
        double rh = pRemoteHoldTime(JMXvalues);
        double lm = ll * lh + rl * rh;
        if (lm == 0)
            return 0;

        return pCont / (lm);
    }

    private static double pLocalHoldTime(Set<HashMap<String, PublishAttribute>> JMXvalues) throws PublishAttributeException {
        double pLocalHoldTime = StatsManager.getAvgAttribute("PaoloLocalTakenHoldTime", JMXvalues);
        double pLocalLocks = StatsManager.getAvgAttribute("PaoloLocalTakenLocks", JMXvalues);
        if (pLocalLocks == 0)
            return 0;
        return pLocalHoldTime / pLocalLocks;
    }

    private static double pRemoteHoldTime(Set<HashMap<String, PublishAttribute>> JMXvalues) throws PublishAttributeException {
        double pRemoteHoldTime = StatsManager.getAvgAttribute("PaoloRemoteTakenHoldTime", JMXvalues);
        double pRemoteLocks = StatsManager.getAvgAttribute("PaoloRemoteTakenLocks", JMXvalues);
        if (pRemoteLocks == 0)
            return 0;
        return pRemoteHoldTime / pRemoteLocks;

    }

}
