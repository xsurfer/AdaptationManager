package eu.cloudtm.statistics;

import eu.cloudtm.commons.IPlatformConfiguration;
import eu.cloudtm.commons.PlatformConfiguration;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 7/12/13
 */
public class StatsProcessor {

    private IPlatformConfiguration currentConfig;

    public StatsProcessor(IPlatformConfiguration currentConfig){
        this.currentConfig = currentConfig;
    }

    public WPMProcessedSample process(WPMSample rawSample){
        // TODO processare in base al protocollo o altro... per ora solo 2pc
        TWOPCInputOracle processedSample = new TWOPCInputOracle(rawSample);
        return processedSample;
    }

}
