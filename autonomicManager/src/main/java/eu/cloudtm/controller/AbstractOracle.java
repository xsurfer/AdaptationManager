package eu.cloudtm.controller;

import eu.cloudtm.controller.model.KPI;
import eu.cloudtm.stats.Sample;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/14/13
 */
public abstract class AbstractOracle implements IOracle {

    private static Log log = LogFactory.getLog(AbstractOracle.class);

    @Override
    public KPI minimizeCosts(Sample sample, double minThroughput) {
        //log.info(tempScale + " throughput = " + throughput * 1e9 + " txs/sec, rtt = " + rtt + " msec, abortProb = " + abortP);

        // TODO: logica di ricerca

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public KPI maximizeThroughput(Sample sample) {

        // TODO: logica di ricerca

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
