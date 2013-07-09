package eu.cloudtm.stats;

import eu.cloudtm.wpm.logService.remote.events.PublishAttribute;
import eu.cloudtm.wpm.parser.ResourceType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
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

    private final Map<String, Map<WPMParam, Double>> ip2params;

    public static WPMSample getInstance(Map<String, Map<WPMParam, Double>> ip2params){
        return new WPMSample(
                counter.getAndIncrement(),
                new HashMap<String, Map<WPMParam, Double>>(ip2params)
        );
    }

    private WPMSample(long _id, Map<String, Map<WPMParam, Double>> _ip2params){
        this.id = _id;
        ip2params = _ip2params;

    }

    public long getId(){ return id; }


    @Override
    public double getPerNodeParam(WPMParam wpmParam, int classIdx, String nodeIP) {
        Map<WPMParam, Double> param2double = ip2params.get(nodeIP);
        Double ret = param2double.get(wpmParam.getParam());
        if(ret == null){
            throw new IllegalArgumentException("Param " + wpmParam + " has not been sampled!!" );
        }
        return ret;
    }

    @Override
    public Set<String> getNodes() {
        return ip2params.keySet();
    }

    @Override
    public double getAvgParam(WPMParam param, int classIdx) {
        double sum = 0;

        for(String ip : ip2params.keySet()){
            sum += getPerNodeParam(param, 0, ip);
        }
        double avg = sum / (double) ( ip2params.keySet().size() );
        return avg;
    }

    protected Map<String, Map<WPMParam, Double>> getAll(){
        return ip2params;
    }
}

