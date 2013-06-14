package eu.cloudtm;

import eu.cloudtm.stats.Sample;
import eu.cloudtm.stats.StatisticDTO;
import eu.cloudtm.stats.WPMViewChangeRemoteListenerImpl;
import eu.cloudtm.wpm.connector.WPMConnector;
import eu.cloudtm.wpm.logService.remote.events.*;
import eu.cloudtm.wpm.logService.remote.observables.Handle;
import eu.cloudtm.wpm.parser.ResourceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * It contains a stack of samples. Each {@link Sample} is organized in categories
 *
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/6/13
 */
public class StatsManager {

    private final static Log log = LogFactory.getLog(StatsManager.class);


    private Handle lastVmHandle;

    private final static int MAX_SIZE = 1000;

    private Deque<Sample> stack = new ArrayDeque<Sample>(MAX_SIZE);

    private AtomicInteger counter = new AtomicInteger(0);

    public StatsManager(){

    }

    public Sample add(Set<HashMap<String, PublishAttribute>> jmx,
                      Set<HashMap<String, PublishAttribute>> mem){
        if(stack.size()>=MAX_SIZE){
            Sample removed = stack.removeLast();
            //log.trace("Deleted stat: " + removed.getId());
        }
        Sample newStat = new Sample(counter.getAndIncrement(),jmx,mem);
        stack.push(newStat);
        log.trace("New stas added: " + newStat.getId());
        return newStat;
    }

    /**
     * This method returns a StatisticDTO, useful to communicate to RESTInterface.
     * It contains all the sample averages belong nodes
     * @param param
     * @param type
     * @return
     */
    public StatisticDTO getAllAvgStatistic(String param, ResourceType type){

        StatisticDTO ret = new StatisticDTO(param);

        Iterator<Sample> iter;
        for (iter = stack.descendingIterator(); iter.hasNext();  ) {
            Sample stat = iter.next();
            double mean = getAvgAttribute(param, stat, type);
            ret.addPoint(stat.getId(),mean);
        }
        return ret;
    }

    /**
     * This method returns all samples related to a single stat
     * @param param
     * @param type
     * @return
     */
    public StatisticDTO getLastAvgStatistic(String param, ResourceType type){
        StatisticDTO ret = new StatisticDTO(param);
        Sample lastStat = stack.peek();
        if(lastStat == null)
            return ret;
        double mean = getAvgAttribute(param, lastStat, type);
        ret.addPoint(lastStat.getId(),mean);

        return ret;
    }

    /**
     * This method returns the last sample related to a single stat
     * @param attribute
     * @param type
     * @return
     */
    public double getLastAvgAttribute(String attribute, ResourceType type) {
        Sample lastStat = stack.peek();
        if(lastStat == null)
            return -1;
        return getAvgAttribute(attribute, lastStat, type);
    }

    public Sample getLastSample(){
        return stack.peek();
    }

    /**
     * this method evaluates de average of specific sample related to a single stat
     * @param attribute
     * @param stat
     * @param type
     * @return
     */
    public static double getAvgAttribute(String attribute, Sample stat, ResourceType type) {

        Set<HashMap<String, PublishAttribute>> values;
        switch (type){
            case JMX:
                values = stat.getJmx();
                break;
            case MEMORY:
                values = stat.getMem();
                break;
            default:
                throw new RuntimeException("No stats");
        }

        double num = values.size(), temp = 0;
        Object actualValue;

        for (HashMap<String, PublishAttribute> h : values) {
            if (h == null) {
                throw new RuntimeException("I had a null set of values");
            }
            //log.trace("Asking for " + attribute);
            //the getName may give a nullPointerException, actually
            if ((actualValue = h.get(attribute).getValue()) == null) {
                throw new RuntimeException(h.get(attribute).getName() + " is null");
            }
            try {
                temp += cast(actualValue);
            } catch (ClassCastException c) {
                throw new RuntimeException(h.get(attribute).getName() + " is not a double/long/int and cannot be averaged. It appears to be " + actualValue.getClass());

            }
        }
        return temp / num;
    }

    private static double cast(Object o) throws ClassCastException {
        try {
            return (Long) o;
        } catch (ClassCastException c) {
            try {
                return (Double) o;
            } catch (ClassCastException cc) {
                return (Integer) o;
            }
        }
    }


}


