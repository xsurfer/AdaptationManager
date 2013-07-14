package eu.cloudtm;

import eu.cloudtm.commons.PlatformConfiguration;
import eu.cloudtm.commons.PlatformTuning;
import eu.cloudtm.statistics.ProcessedSample;
import eu.cloudtm.statistics.SampleListener;
import eu.cloudtm.statistics.StatsManager;
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
    private StatsManager statsManager;
    private InputFilter inputFilter;
    private Optimizer optimizer;
    private Reconfigurator reconfigurator;

    public AutonomicManager(PlatformConfiguration platformConfiguration,
                            PlatformTuning platformTuning,
                            StatsManager sampleManager,
                            InputFilter inputFilter,
                            Optimizer optimizer,
                            Reconfigurator reconfigurator){
        this.statsManager = sampleManager;
        this.platformConfiguration = platformConfiguration;

        this.inputFilter = inputFilter;
        this.optimizer = optimizer;
        this.reconfigurator = reconfigurator;
    }

    public void start(){
        log.warn("Autonomic Manager started");
    }

}
