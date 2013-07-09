package eu.cloudtm.common;

import eu.cloudtm.stats.WPMSample;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/19/13
 */
public interface SampleListener {

    public void onNewSample(WPMSample sample);

}
