package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.commons.PlatformConfiguration;
import eu.cloudtm.autonomicManager.commons.PlatformTuning;
import eu.cloudtm.autonomicManager.oracles.exceptions.OracleException;
import eu.cloudtm.autonomicManager.statistics.ProcessedSample;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/16/13
 */
public abstract class OpenOptimizer extends AbstractOptimizer {

    private static Log log = LogFactory.getLog(OpenOptimizer.class);

    private final static int ARRIVAL_RATE_GUARANTEE_PERC = 50;
    private final static int ABORT_GUARANTEE_PERC = 5;
    private final static int RESPONSE_TIME_GUARANTEE_PERC = 5;

    public OpenOptimizer(Reconfigurator reconfigurator,
                         SLAManager slaManager,
                         PlatformConfiguration platformConfiguration,
                         PlatformTuning platformTuning) {
        super(reconfigurator, slaManager, platformConfiguration, platformTuning);
    }


    public void optimize(ProcessedSample processedSample) throws OracleException{

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
