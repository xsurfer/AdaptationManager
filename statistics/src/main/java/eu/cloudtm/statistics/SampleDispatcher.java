package eu.cloudtm.statistics;


import eu.cloudtm.statistics.ProcessedSample;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/19/13
 */
public interface SampleDispatcher {

    public void dispatch(ProcessedSample sample);

}
