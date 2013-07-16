package eu.cloudtm;

import eu.cloudtm.commons.*;
import eu.cloudtm.exceptions.ReconfiguratorException;
import eu.cloudtm.oracles.OracleService;
import eu.cloudtm.oracles.exceptions.OracleException;
import eu.cloudtm.statistics.ProcessedSample;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/16/13
 */
public class Optimizer {

    private static Log log = LogFactory.getLog(Optimizer.class);

    private PlatformTuning platformTuning;
    private PlatformConfiguration platformConfiguration;
    private Reconfigurator reconfigurator;

    private final static int ARRIVAL_RATE_GUARANTEE_PERC = 50;
    private final static int ABORT_GUARANTEE_PERC = 5;
    private final static int RESPONSE_TIME_GUARANTEE_PERC = 5;

    public Optimizer(Reconfigurator reconfigurator, PlatformConfiguration platformConfiguration, PlatformTuning platformTuning){
        this.platformTuning = platformTuning;
        this.reconfigurator = reconfigurator;
        this.platformConfiguration = platformConfiguration;
    }

    public void doOptimize(KPI current, ProcessedSample processedSample) throws ReconfiguratorException, OracleException {

        // TODO: Cercare una configurazione valida per ogni classe transazionale (Read, Write) cioè che rispetta gli SLAs

        double arrivalRateToGuarantee =  current.throughput() +  (current.throughput() * (ARRIVAL_RATE_GUARANTEE_PERC / 100));
        log.trace("arrivalRateToGuarantee:" + arrivalRateToGuarantee);

        double abortRateToGuarantee = current.abortRate() + (current.abortRate() * (ABORT_GUARANTEE_PERC / 100));
        log.trace("abortRateToGuarantee:" + abortRateToGuarantee);

        double responseTimeToGuarantee = current.responseTime(0) + (current.responseTime(0) * (RESPONSE_TIME_GUARANTEE_PERC / 100));
        log.trace("responseTimeToGuarantee:" + abortRateToGuarantee);


        //SLAItem sla = slaManager.getReadSLA(arrivalRateToGuarantee);
        // TODO: cercare una configurazione che soddisfi tutte le classi txs

        if(!platformTuning.forecaster().isAutoTuning()){
            return;
        }

        ControllerLogger.log.info("Querying " + platformTuning.forecaster() + " oracle");
        OracleService oracleService = OracleService.getInstance(platformTuning.forecaster().getOracleClass());

        PlatformConfiguration forecastedConfig;
        KPIimpl kpi = null;
        try {
            forecastedConfig = oracleService.minimizeCosts(processedSample, arrivalRateToGuarantee, abortRateToGuarantee, responseTimeToGuarantee );

        } catch (OracleException e) {
            throw e;
        }

        if(forecastedConfig==null){
            ControllerLogger.log.info(" »»» No configuration found «««");
            return;
        } else {
            ControllerLogger.log.info(" »»» Configuration found «««" );
        }

        reconfigurator.reconfigure(createNextConfig(forecastedConfig));
    }

    private PlatformConfiguration createNextConfig(PlatformConfiguration forecastedConfig){
        int size, repDegree;
        ReplicationProtocol repProt;

        size = (!platformTuning.isAutoScale()) ? platformConfiguration.platformSize() : forecastedConfig.platformSize();
        repDegree = (!platformTuning.isAutoDegree()) ? platformConfiguration.replicationDegree() : forecastedConfig.replicationDegree();
        repProt = (!platformTuning.isAutoProtocol()) ? platformConfiguration.replicationProtocol() : forecastedConfig.replicationProtocol();

        return new PlatformConfiguration(size, repDegree, repProt);
    }

}
