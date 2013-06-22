package eu.cloudtm;

import eu.cloudtm.common.SampleListener;
import eu.cloudtm.controller.model.PlatformConfiguration;
import eu.cloudtm.stats.Sample;
import eu.cloudtm.common.dto.StatisticDTO;
import eu.cloudtm.wpm.logService.remote.events.*;
import eu.cloudtm.wpm.parser.ResourceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * It contains a stack of samples. Each {@link Sample} is organized in categories
 *
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/6/13
 */
public class StatsManager implements SampleListener {

    private static StatsManager instance;

    private final static Log log = LogFactory.getLog(StatsManager.class);

    private final static int MAX_SIZE = 1000;

    private final Deque<Sample> stack = new ArrayDeque<Sample>(MAX_SIZE);

    private StatsManager(){

    }

    public static StatsManager getInstance(){
        if(instance == null){
            instance = new StatsManager();
        }
        return instance;
    }

    public void onNewSample(Sample _sample){
        if(stack.size()>=MAX_SIZE){
            Sample removed = stack.removeLast();
            //log.trace("Deleted stat: " + removed.getId());
        }
        stack.push(_sample);
        log.trace("New stas added: " + _sample.getId());
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

    public List<Sample> getLastNSample(int n){
        List<Sample> samples = new ArrayList<Sample>();
        Queue<Sample> queue = Collections.asLifoQueue(new ArrayDeque<Sample>(stack));
        for(int i=0; i<n; i++){
            Sample sample = queue.remove();
            samples.add( sample );
        }
        return samples;
    }

    public static double getAvgAttribute(String attribute, Set<HashMap<String, PublishAttribute>> values){
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
        return getAvgAttribute(attribute, values);
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


