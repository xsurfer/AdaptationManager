package eu.cloudtm.controller;

import eu.cloudtm.controller.model.KPI;
import eu.cloudtm.stats.Sample;

import java.util.Set;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/12/13
 */
public interface IOracle {

    public KPI minimizeCosts(Sample sample, double minThroughput );

    public KPI maximizeThroughput(Sample sample);

    public KPI forecast(Sample sample, int numNodes, int numThreads);

}
