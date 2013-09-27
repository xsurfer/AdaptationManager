package eu.cloudtm.autonomicManager.statistics;

import eu.cloudtm.autonomicManager.commons.PlatformConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 7/12/13
 */
public class StatsProcessor implements Processor {

    private static Log log = LogFactory.getLog(StatsProcessor.class);

    private PlatformConfiguration currentConfig;

    public StatsProcessor(PlatformConfiguration currentConfig){
        this.currentConfig = currentConfig;
    }

    public ProcessedSample process(Sample rawSample){
        ProcessedSample processedSample = null;

        switch (currentConfig.replicationProtocol()){

            case TWOPC:
                log.trace("Processing wpm sample in TWOPCProcessedSample");
                processedSample = new TWOPCProcessedSample(rawSample);
                break;
            case TO:
                log.trace("Processing wpm sample in TOProcessedSample");
                processedSample = new TWOPCProcessedSample(rawSample);
                break;
            case PB:
                log.trace("Processing wpm sample in PBProcessedSample");
                processedSample = new TWOPCProcessedSample(rawSample);
                break;
            default:
                throw new RuntimeException("Invalid replication protocol!");

        }
        return processedSample;
    }

}
