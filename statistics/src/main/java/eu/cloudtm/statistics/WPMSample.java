package eu.cloudtm.statistics;

import eu.cloudtm.wpm.logService.remote.events.PublishAttribute;

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

    private final Map<String, Map<String, PublishAttribute>> ip2params;

    public static WPMSample getInstance(Map<String, Map<String, PublishAttribute>> ip2params){
        WPMSample sample = new WPMSample(
                counter.getAndIncrement(),
                new HashMap<String, Map<String, PublishAttribute>>(ip2params)
        );

        return sample;
    }

    public WPMSample(long _id, Map<String, Map<String, PublishAttribute>> _ip2params){
        this.id = _id;
        ip2params = _ip2params;

    }

    public long getId(){ return id; }


    @Override
    public Object getPerNodeParam(WPMParam wpmParam, String nodeIP) {
        Map<String, PublishAttribute> string2publishAttribute = ip2params.get(nodeIP);
        return string2publishAttribute.get(wpmParam.getParam()).getValue();
    }

    @Override
    public List<String> getNodes() {
        return new ArrayList<String>( ip2params.keySet() );

    }

    @Override
    public double getAvgParam(WPMParam param) {
        double sum = 0;

        for(String ip : ip2params.keySet()){
            sum += (Double) getPerNodeParam(param, ip);
        }
        double avg = sum / (double) ( ip2params.keySet().size() );
        return avg;
    }

    protected Map<String, Map<String, PublishAttribute>> getAll(){
        return ip2params;
    }
}

