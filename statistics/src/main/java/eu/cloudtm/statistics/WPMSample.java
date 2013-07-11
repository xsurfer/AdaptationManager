package eu.cloudtm.statistics;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/10/13
 */
public class WPMSample implements Sample {

    private static AtomicLong counter = new AtomicLong(0);

    private final long id;

    private final Map<String, Double> aggregatedFromWPM;

    public static WPMSample getInstance(Map<String, Double> params2values){
        WPMSample sample = new WPMSample(
                counter.getAndIncrement(),
                new HashMap<String, Double>(params2values)
        );

        return sample;
    }

    public WPMSample(long _id, Map<String, Double> aggregated){
        this.id = _id;
        this.aggregatedFromWPM = aggregated;
    }

    public long getId(){ return id; }

    @Override
    public double getParam(WPMParam param) {
        return aggregatedFromWPM.get(param.getKey());
    }

}

