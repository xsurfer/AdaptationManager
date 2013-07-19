package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.exceptions.ReconfiguratorException;
import eu.cloudtm.autonomicManager.oracles.OracleService;
import eu.cloudtm.autonomicManager.oracles.OutputOracleImpl;
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
public class MuleOptimizer extends Optimizer{

    private static Log log = LogFactory.getLog(MuleOptimizer.class);


    private final static int ARRIVAL_RATE_GUARANTEE_PERC = 50;
    private final static int ABORT_GUARANTEE_PERC = 5;
    private final static int RESPONSE_TIME_GUARANTEE_PERC = 5;

    public MuleOptimizer(Reconfigurator reconfigurator,
                         SLAManager slaManager,
                         PlatformConfiguration platformConfiguration,
                         PlatformTuning platformTuning) {
        super(reconfigurator, slaManager, platformConfiguration, platformTuning);
    }


    public void optimize(ProcessedSample processedSample) throws OracleException {
        ControllerLogger.log.info("Querying " + platformTuning.forecaster() + " oracle");
        OracleService oracleService = OracleService.getInstance(platformTuning.forecaster().getOracleClass());

        PlatformConfiguration forecastedConfig;
        forecastedConfig = oracleService.maximizeThroughput(processedSample);

        if( forecastedConfig != null ){
            ControllerLogger.log.info(" »»» Configuration found «««" );
            reconfigurator.reconfigure(createNextConfig(forecastedConfig));
        } else {
            ControllerLogger.log.info(" »»» Configuration not found «««" );
        }
    }
}
