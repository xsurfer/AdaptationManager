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
    private Reconfigurator reconfigurator;

    private final static int ARRIVAL_RATE_GUARANTEE_PERC = 50;
    private final static int ABORT_GUARANTEE_PERC = 5;
    private final static int RESPONSE_TIME_GUARANTEE_PERC = 5;

    public Optimizer(Reconfigurator reconfigurator, PlatformTuning platformTuning){
        this.platformTuning = platformTuning;
        this.reconfigurator = reconfigurator;
    }

    public void doOptimize(KPI current, ProcessedSample processedSample) throws ReconfiguratorException, OracleException {

        // TODO: Cercare una configurazione valida per ogni classe transazionale (Read, Write) cioè che rispetta gli SLAs

        double arrivalRateToGuarantee =  current.throughput() +  (current.throughput() * (ARRIVAL_RATE_GUARANTEE_PERC / 100));
        log.trace("arrivalRateToGuarantee:" + arrivalRateToGuarantee);

        double abortRateToGuarantee = current.abortRate() + (current.abortRate() * (ABORT_GUARANTEE_PERC / 100));
        log.trace("abortRateToGuarantee:" + abortRateToGuarantee);

        double responseTimeToGuarantee = current.responseTime() + (current.responseTime() * (RESPONSE_TIME_GUARANTEE_PERC / 100));
        log.trace("responseTimeToGuarantee:" + abortRateToGuarantee);


        //SLAItem sla = slaManager.getReadSLA(arrivalRateToGuarantee);
        // TODO: cercare una configurazione che soddisfi tutte le classi txs

        Set<Forecaster> oracles = new HashSet<Forecaster>();
        oracles.add(platformTuning.scaleForecaster());
        oracles.add(platformTuning.degreeForecaster());
        oracles.add(platformTuning.protocolForecaster());

        log.info("Querying " + oracles.size() + " oracles");

        Map<Forecaster, PlatformConfiguration> oracle2conf = new HashMap<Forecaster, PlatformConfiguration>();

        for(Forecaster forecaster : oracles){
            OracleService oracleService = OracleService.getInstance(forecaster.getOracleClass());
            log.info( "Querying " + oracleService );
            KPIimpl kpi = null;
            try {
                PlatformConfiguration currConfig = oracleService.minimizeCosts(processedSample, arrivalRateToGuarantee, abortRateToGuarantee, responseTimeToGuarantee );
                oracle2conf.put(forecaster, currConfig);
            } catch (OracleException e) {
                throw e;
            }
        }

        reconfigurator.reconfigure(createNextConfig(oracle2conf));
    }

    private PlatformConfiguration createNextConfig(Map<Forecaster, PlatformConfiguration> oracle2conf){

        int size = selectPlatformSize(oracle2conf);
        int repDegree = selectPlatformSize(oracle2conf);
        ReplicationProtocol repProt = selectRepProtocol(oracle2conf);

        return new PlatformConfiguration(size, repDegree, repProt);
    }

    private int selectPlatformSize(Map<Forecaster, PlatformConfiguration> oracle2conf ){
        int size;
        switch (platformTuning.scaleForecaster()){
            case ANALYTICAL:
                size = oracle2conf.get(Forecaster.ANALYTICAL).platformSize();
                break;
            case SIMULATOR:
                size = oracle2conf.get(Forecaster.SIMULATOR).platformSize();
                break;
            case MACHINE_LEARNING:
                size = oracle2conf.get(Forecaster.MACHINE_LEARNING).platformSize();
                break;
            default:
                throw new RuntimeException();
        }
        return size;
    }

    private int selectRepDegree(Map<Forecaster, PlatformConfiguration> oracle2conf ){
        int repDegree;
        switch (platformTuning.degreeForecaster()){
            case ANALYTICAL:
                repDegree = oracle2conf.get(Forecaster.ANALYTICAL).replicationDegree();
                break;
            case SIMULATOR:
                repDegree = oracle2conf.get(Forecaster.SIMULATOR).replicationDegree();
                break;
            case MACHINE_LEARNING:
                repDegree = oracle2conf.get(Forecaster.MACHINE_LEARNING).replicationDegree();
                break;
            default:
                throw new RuntimeException();
        }
        return repDegree;
    }

    private ReplicationProtocol selectRepProtocol(Map<Forecaster, PlatformConfiguration> oracle2conf ){
        ReplicationProtocol repProt;
        switch (platformTuning.protocolForecaster()){
            case ANALYTICAL:
                repProt = oracle2conf.get(Forecaster.ANALYTICAL).replicationProtocol();
                break;
            case SIMULATOR:
                repProt = oracle2conf.get(Forecaster.SIMULATOR).replicationProtocol();
                break;
            case MACHINE_LEARNING:
                repProt = oracle2conf.get(Forecaster.MACHINE_LEARNING).replicationProtocol();
                break;
            default:
                throw new RuntimeException();
        }
        return repProt;
    }

}
