package eu.cloudtm.autonomicManager.statistics;

import eu.cloudtm.autonomicManager.commons.IPlatformConfiguration;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 7/12/13
 */
public class StatsProcessor implements Processor {

    private IPlatformConfiguration currentConfig;

    public StatsProcessor(IPlatformConfiguration currentConfig){
        this.currentConfig = currentConfig;
    }

    public ProcessedSample process(Sample rawSample){
        // TODO processare in base al protocollo o altro... per ora solo 2pc
        ProcessedSample processedSample = new TWOPCProcessedSample(rawSample);
        return processedSample;
    }

}
