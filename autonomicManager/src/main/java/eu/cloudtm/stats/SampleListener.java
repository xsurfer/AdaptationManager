package eu.cloudtm.stats;

import eu.cloudtm.oracles.ProcessedSample;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/19/13
 */
public interface SampleListener {

    public void onNewSample(ProcessedSample sample);

}
