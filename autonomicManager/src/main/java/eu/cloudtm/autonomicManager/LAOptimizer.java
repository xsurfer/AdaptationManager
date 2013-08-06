package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.commons.Param;
import eu.cloudtm.autonomicManager.commons.PlatformConfiguration;
import eu.cloudtm.autonomicManager.commons.PlatformTuning;
import eu.cloudtm.autonomicManager.oracles.exceptions.OracleException;
import eu.cloudtm.autonomicManager.statistics.ProcessedSample;

/**
 * Author: Fabio Perfetti (perfabio87 [at] gmail.com)
 * Date: 8/6/13
 * Time: 2:05 PM
 */
public class LAOptimizer extends AbstractOptimizer {


    public LAOptimizer(IReconfigurator reconfigurator, SLAManager slaManager, PlatformConfiguration platformConfiguration, PlatformTuning platformTuning) {
        super(reconfigurator, slaManager, platformConfiguration, platformTuning);
    }

    @Override
    public void optimize(ProcessedSample processedSample) {




    }
}
