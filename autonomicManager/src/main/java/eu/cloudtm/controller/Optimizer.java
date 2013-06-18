package eu.cloudtm.controller;

import eu.cloudtm.LookupRegister;
import eu.cloudtm.StatsManager;
import eu.cloudtm.controller.model.KPI;
import eu.cloudtm.controller.model.PlatformConfiguration;
import eu.cloudtm.controller.model.SLAItem;
import eu.cloudtm.controller.oracles.AbstractOracle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/16/13
 */
public class Optimizer {

    private static Log log = LogFactory.getLog(Optimizer.class);

    private Controller controller;
    private SLAManager slaManager = new SLAManager();

    private StatsManager statsManager = LookupRegister.getStatsManager();

    public Optimizer(Controller _controller){
        controller = _controller;
    }

    public PlatformConfiguration doOptimize(){

        // TODO: Cercare una configurazione valida per ogni classe transazionale (Read, Write) cioè che rispetta gli SLAs

        double arrivalRate = LoadPredictor.doPrediction();

        SLAItem sla = slaManager.getReadSLA(arrivalRate);
        // TODO: cercare una configurazione che soddisfi tutte le classi txs

        PlatformConfiguration nextConfig = null;
        for(IOracle oracle : controller.getOracles()){
            log.info( oracle );
            KPI kpi = oracle.minimizeCosts(statsManager.getLastSample(), arrivalRate, sla.getAbortRate(), sla.getResponseTime());
            if(kpi != null)
                nextConfig = kpi.getPlatformConfiguration();
        }

        log.info("Time to reconfigure");
        return null;
    }

}
