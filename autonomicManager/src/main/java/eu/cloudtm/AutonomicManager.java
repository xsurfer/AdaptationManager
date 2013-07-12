package eu.cloudtm;

import eu.cloudtm.commons.PlatformConfiguration;
import eu.cloudtm.commons.PlatformTuning;
import eu.cloudtm.statistics.StatsManager;
import eu.cloudtm.statistics.WPMStatsManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 7/10/13
 */
public class AutonomicManager {

    private static Log log = LogFactory.getLog(AutonomicManager.class);

    private PlatformConfiguration platformConfiguration;
    private PlatformTuning platformTuning;
    private WPMStatsManager wpmStatsManager;

    public AutonomicManager(PlatformConfiguration platformConfiguration,
                            PlatformTuning platformTuning,
                            WPMStatsManager sampleManager ){
        this.wpmStatsManager = sampleManager;
        this.platformConfiguration = platformConfiguration;
        this.platformTuning = platformTuning;
    }

    public void start(){
        log.warn("Starting Controller");
    }

}
