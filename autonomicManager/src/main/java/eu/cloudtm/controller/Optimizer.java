package eu.cloudtm.controller;

import eu.cloudtm.StatsManager;
import eu.cloudtm.controller.exceptions.OracleException;
import eu.cloudtm.controller.model.KPI;
import eu.cloudtm.controller.model.PlatformConfiguration;
import eu.cloudtm.controller.model.SLAItem;
import eu.cloudtm.controller.oracles.AbstractOracle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/16/13
 */
public class Optimizer {

    private static Log log = LogFactory.getLog(Optimizer.class);

    private List<String> oracles;

    private Controller controller;

    private SLAManager slaManager = new SLAManager();

    private StatsManager statsManager;

    private final static int ARRIVAL_RATE_GUARANTEE_PERC = 50;
    private final static int ABORT_GUARANTEE_PERC = 5;
    private final static int RESPONSE_TIME_GUARANTEE_PERC = 5;

    public Optimizer(Controller _controller, List<String> _oracles, StatsManager _statsManager){
        controller = _controller;
        oracles = _oracles;
        statsManager = _statsManager;
    }

    public PlatformConfiguration doOptimize(double currentThroughput, double currentAbortRate, double currentResponseTime){

        // TODO: Cercare una configurazione valida per ogni classe transazionale (Read, Write) cioè che rispetta gli SLAs

        double arrivalRateToGuarantee =  currentThroughput +  (currentThroughput * (ARRIVAL_RATE_GUARANTEE_PERC / 100));
        log.trace("arrivalRateToGuarantee:" + arrivalRateToGuarantee);

        double abortRateToGuarantee = currentAbortRate + (currentAbortRate * (ABORT_GUARANTEE_PERC / 100));
        log.trace("abortRateToGuarantee:" + abortRateToGuarantee);

        double responseTimeToGuarantee = currentResponseTime + (currentResponseTime * (RESPONSE_TIME_GUARANTEE_PERC / 100));
        log.trace("responseTimeToGuarantee:" + abortRateToGuarantee);


        //SLAItem sla = slaManager.getReadSLA(arrivalRateToGuarantee);
        // TODO: cercare una configurazione che soddisfi tutte le classi txs

        PlatformConfiguration nextConfig = null;
        for(String oracleName : oracles){
            IOracle oracle = AbstractOracle.getInstance(oracleName, controller);
            log.info( oracle );
            KPI kpi = null;
            try {
                kpi = oracle.minimizeCosts(statsManager.getLastSample(), arrivalRateToGuarantee, abortRateToGuarantee, responseTimeToGuarantee );
            } catch (OracleException e) {
                log.warn(oracle + " had problem solving the current scenario!");
            }
            if(kpi != null){
                nextConfig = kpi.getPlatformConfiguration();
            }
        }

        return nextConfig;
    }

}
