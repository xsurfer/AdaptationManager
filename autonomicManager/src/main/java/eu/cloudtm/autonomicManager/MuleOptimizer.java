package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.commons.PlatformConfiguration;
import eu.cloudtm.autonomicManager.commons.PlatformTuning;
import eu.cloudtm.autonomicManager.oracles.OracleService;
import eu.cloudtm.autonomicManager.oracles.exceptions.OracleException;
import eu.cloudtm.autonomicManager.statistics.ProcessedSample;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/16/13
 */
public class MuleOptimizer extends AbstractOptimizer {

    private static Log log = LogFactory.getLog(MuleOptimizer.class);

    public MuleOptimizer(IReconfigurator reconfigurator,
                         SLAManager slaManager,
                         PlatformConfiguration platformConfiguration,
                         PlatformTuning platformTuning) {
        super(reconfigurator, slaManager, platformConfiguration, platformTuning);
    }


    public void optimize(ProcessedSample processedSample) throws OracleException {

        ControllerLogger.log.info("Mule Optimizer: Querying " + platformTuning.forecaster() + " oracle");
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
