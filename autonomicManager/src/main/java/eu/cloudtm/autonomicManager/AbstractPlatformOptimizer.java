package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.commons.PlatformConfiguration;
import eu.cloudtm.autonomicManager.commons.PlatformTuning;
import eu.cloudtm.autonomicManager.commons.ReplicationProtocol;
import eu.cloudtm.autonomicManager.oracles.exceptions.OracleException;
import eu.cloudtm.autonomicManager.statistics.ProcessedSample;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/16/13
 */
public abstract class AbstractPlatformOptimizer implements Optimizer {

    private static Log log = LogFactory.getLog(AbstractPlatformOptimizer.class);

    protected PlatformTuning platformTuning;
    protected PlatformConfiguration platformConfiguration;
    protected Reconfigurator reconfigurator;
    protected SLAManager slaManager;

    public AbstractPlatformOptimizer(Reconfigurator reconfigurator,
                                     SLAManager slaManager,
                                     PlatformConfiguration platformConfiguration,
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



    protected PlatformConfiguration createNextConfig(PlatformConfiguration forecastedConfig){
        int size, repDegree;
        ReplicationProtocol repProt;

        size = (!platformTuning.isAutoScale()) ? platformConfiguration.platformSize() : forecastedConfig.platformSize();
        repDegree = (!platformTuning.isAutoDegree()) ? platformConfiguration.replicationDegree() : forecastedConfig.replicationDegree();
        repProt = (!platformTuning.isAutoProtocol()) ? platformConfiguration.replicationProtocol() : forecastedConfig.replicationProtocol();

        return new PlatformConfiguration(size, repDegree, repProt);
    }

}

