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
    private final HashMap<String, PublishAttribute> jmxStack;
    private final HashMap<String, PublishAttribute> memStack;

    public static Sample getInstance(HashMap<String, PublishAttribute> jmx, HashMap<String, PublishAttribute> mem){
        return new Sample(counter.getAndIncrement(),jmx,mem);
    }

    private Sample(long _id, HashMap<String, PublishAttribute> _jmx, HashMap<String, PublishAttribute> _mem){
        this.id = _id;
        this.jmxStack = _jmx;
        this.memStack = _mem;
    }

    public long getId(){ return id; }

    public HashMap<String, PublishAttribute> getJmx(){
        return jmxStack;
    }

    public HashMap<String, PublishAttribute> getMem(){
        return memStack;
    }

}
