package eu.cloudtm.autonomicManager;

import eu.cloudtm.commons.IPlatformConfiguration;
import eu.cloudtm.commons.PlatformConfiguration;
import eu.cloudtm.commons.PlatformTuning;
import eu.cloudtm.commons.ReplicationProtocol;
import eu.cloudtm.oracles.exceptions.OracleException;
import eu.cloudtm.statistics.ProcessedSample;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/16/13
 */
public abstract class AbstractOptimizer {

    private static Log log = LogFactory.getLog(AbstractOptimizer.class);

    protected PlatformTuning platformTuning;
    protected IPlatformConfiguration platformConfiguration;
    protected IReconfigurator reconfigurator;
    protected SLAManager slaManager;

    private final static int ARRIVAL_RATE_GUARANTEE_PERC = 50;
    private final static int ABORT_GUARANTEE_PERC = 5;
    private final static int RESPONSE_TIME_GUARANTEE_PERC = 5;

    public AbstractOptimizer(IReconfigurator reconfigurator,
                             SLAManager slaManager,
                             IPlatformConfiguration platformConfiguration,
                             PlatformTuning platformTuning){
        this.slaManager = slaManager;
        this.platformTuning = platformTuning;
        this.reconfigurator = reconfigurator;
        this.platformConfiguration = platformConfiguration;
    }

    public void doOptimize(ProcessedSample sample) throws OracleException {
        if(!platformTuning.forecaster().isAutoTuning()){
            return;
        }
        optimize(sample);
    }

    public abstract void optimize(ProcessedSample processedSample) throws OracleException;

    protected PlatformConfiguration createNextConfig(PlatformConfiguration forecastedConfig){
        int size, repDegree;
        ReplicationProtocol repProt;

        size = (!platformTuning.isAutoScale()) ? platformConfiguration.platformSize() : forecastedConfig.platformSize();
        repDegree = (!platformTuning.isAutoDegree()) ? platformConfiguration.replicationDegree() : forecastedConfig.replicationDegree();
        repProt = (!platformTuning.isAutoProtocol()) ? platformConfiguration.replicationProtocol() : forecastedConfig.replicationProtocol();

        return new PlatformConfiguration(size, repDegree, repProt);
    }

}



// TODO: Cercare una configurazione valida per ogni classe transazionale (Read, Write) cioè che rispetta gli SLAs

        /*
        double arrivalRateToGuarantee =  current.throughput() +  (current.throughput() * (ARRIVAL_RATE_GUARANTEE_PERC / 100));
        log.trace("arrivalRateToGuarantee:" + arrivalRateToGuarantee);

        double abortRateToGuarantee = current.abortRate() + (current.abortRate() * (ABORT_GUARANTEE_PERC / 100));
        log.trace("abortRateToGuarantee:" + abortRateToGuarantee);

        double responseTimeToGuarantee = current.responseTime(0) + (current.responseTime(0) * (RESPONSE_TIME_GUARANTEE_PERC / 100));
        log.trace("responseTimeToGuarantee:" + abortRateToGuarantee);
        */

//SLAItem sla = slaManager.getReadSLA(arrivalRateToGuarantee);
// TODO: cercare una configurazione che soddisfi tutte le classi txs
