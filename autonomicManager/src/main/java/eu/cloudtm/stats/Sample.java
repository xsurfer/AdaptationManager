package eu.cloudtm.stats;

import eu.cloudtm.wpm.logService.remote.events.PublishAttribute;

import java.util.HashMap;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is a container for several statistics, organized in JMX, MEM
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/10/13
 */
public class Sample {

    private static AtomicInteger counter = new AtomicInteger(0);

    private final long id;
    private final Set<HashMap<String, PublishAttribute>> jmxStack;
    private final Set<HashMap<String, PublishAttribute>> memStack;

    public static Sample getInstance(Set<HashMap<String, PublishAttribute>> jmx, Set<HashMap<String, PublishAttribute>> mem){
        return new Sample(counter.getAndIncrement(),jmx,mem);
    }

    private Sample(long _id, Set<HashMap<String, PublishAttribute>> _jmx, Set<HashMap<String, PublishAttribute>> _mem){
        this.id = _id;
        this.jmxStack = _jmx;
        this.memStack = _mem;
    }

    public long getId(){ return id; }

    public Set<HashMap<String, PublishAttribute>> getJmx(){
        return jmxStack;
    }

    public Set<HashMap<String, PublishAttribute>> getMem(){
        return memStack;
    }

}
