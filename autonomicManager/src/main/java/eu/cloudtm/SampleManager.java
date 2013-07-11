package eu.cloudtm;

import eu.cloudtm.statistics.WPMProcessedSample;
import eu.cloudtm.statistics.StatsManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * It contains a stack of samples.
 *
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/6/13
 */
public class SampleManager implements StatsManager {

    private final static Log log = LogFactory.getLog(SampleManager.class);

    private final static int MAX_SIZE = 1000;

    private final Deque<WPMProcessedSample> stack = new ArrayDeque<WPMProcessedSample>(MAX_SIZE);

    public SampleManager(){

    }

    public void process(WPMProcessedSample sample){
        pushSample(sample);

    }

    private void pushSample(WPMProcessedSample sample){
        if(stack.size()>=MAX_SIZE){
            WPMProcessedSample removed = stack.removeLast();
            //log.trace("Deleted stat: " + removed.getId());
        }
        stack.push(sample);
        log.trace("New stas added: " + sample.getId());
    }

    /**
     * This method returns a StatisticDTO, useful to communicate to RESTInterface.
     * It contains all the sample averages belong nodes
     * @param param
     * @param type
     * @return
     */
//    public StatisticDTO getAllAvgStatistic(String param, ResourceType type){
//
//        StatisticDTO ret = new StatisticDTO(param);
//
//        Iterator<WPMSample> iter;
//        for (iter = stack.descendingIterator(); iter.hasNext();  ) {
//            WPMSample stat = iter.next();
//            double mean = getAvgAttribute(param, stat, type);
//            ret.addPoint(stat.getId(),mean);
//        }
//        return ret;
//    }

    /**
     * This method returns all samples related to a single stat
     * @param param
     * @param type
     * @return
     */
//    public StatisticDTO getLastAvgStatistic(String param, ResourceType type){
//        StatisticDTO ret = new StatisticDTO(param);
//        WPMSample lastStat = stack.peek();
//        if(lastStat == null)
//            return ret;
//        double mean = getAvgAttribute(param, lastStat, type);
//        ret.addPoint(lastStat.getId(),mean);
//
//        return ret;
//    }

    /**
     * This method returns the last sample related to a single stat
     * @return
     */
//    public double getLastAvgAttribute(String attribute, ResourceType type) {
//        WPMSample lastStat = stack.peek();
//        if(lastStat == null)
//            return -1;
//        return getAvgAttribute(attribute, lastStat, type);
//    }

    public WPMProcessedSample getLastSample(){
        return stack.peek();
    }

    public List<WPMProcessedSample> getLastNSample(int n){
        List<WPMProcessedSample> samples = new ArrayList<WPMProcessedSample>();
        Queue<WPMProcessedSample> queue = Collections.asLifoQueue(new ArrayDeque<WPMProcessedSample>(stack));
        for(int i=0; i<n; i++){
            WPMProcessedSample sample = queue.remove();
            samples.add( sample );
        }
        return samples;
    }


//    public static double getAvgAttribute(String attribute, Map<String, PublishAttribute<Double>> values){
//        double ret= -1.0;
//        if( values != null && !values.isEmpty() ){
//            ret = cast( values.get(attribute).getValue() );
//        }
//        return ret;
//    }


//    public static double getAvgAttribute(String attribute, HashMap<String, PublishAttribute> values){
//        double num = values.size(), temp = 0;
//        Object actualValue;
//
//
//            if (h == null) {
//                throw new RuntimeException("I had a null set of values");
//            }
//            //log.trace("Asking for " + attribute);
//            //the getName may give a nullPointerException, actually
//            if ((actualValue = h.get(attribute).getValue()) == null) {
//                throw new RuntimeException(h.get(attribute).getName() + " is null");
//            }
//            try {
//                temp += cast(actualValue);
//            } catch (ClassCastException c) {
//                throw new RuntimeException(h.get(attribute).getName() + " is not a double/long/int and cannot be averaged. It appears to be " + actualValue.getClass());
//
//            }
//
//        return temp / num;
//
//    }

//    /**
//     * this method evaluates de average of specific sample related to a single stat
//     * @param attribute
//     * @param stat
//     * @param type
//     * @return
//     */
//    public static double getAvgAttribute(String attribute, WPMSample stat, ResourceType type) {
//
//        Map<String, PublishAttribute<Double>> values;
//        switch (type){
//            case JMX:
//                values = stat.getJmx();
//                break;
//            case MEMORY:
//                values = stat.getMem();
//                break;
//            default:
//                throw new RuntimeException("No stats");
//        }
//        return getAvgAttribute(attribute, values);
//    }
//
//    private static double cast(Object o) throws ClassCastException {
//        try {
//            return (Long) o;
//        } catch (ClassCastException c) {
//            try {
//                return (Double) o;
//            } catch (ClassCastException cc) {
//                return (Integer) o;
//            }
//        }
//    }



}


