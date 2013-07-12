package eu.cloudtm.statistics;

import eu.cloudtm.commons.dto.StatisticDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Singleton;
import java.util.*;

/**
 * It contains a stack of samples.
 *
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/6/13
 */

@Singleton
public class WPMStatsManager implements StatsManager {

    private final static Log log = LogFactory.getLog(WPMStatsManager.class);

    private final static int MAX_SIZE = 1000;

    private final Deque<WPMProcessedSample> stack = new ArrayDeque<WPMProcessedSample>(MAX_SIZE);

    public WPMStatsManager(){
    }

    @Override
    public void push(WPMProcessedSample wpmProcessedSample){
        if(stack.size()>=MAX_SIZE){
            WPMProcessedSample removed = stack.removeLast();
            //log.trace("Deleted stat: " + removed.getId());
        }
        stack.push(wpmProcessedSample);
        log.trace("New stas added: " + wpmProcessedSample.getId());
    }

    @Override
    public WPMProcessedSample getLastSample(){
        return stack.peek();
    }

    @Override
    public List<WPMProcessedSample> getLastNSample(int n){
        List<WPMProcessedSample> samples = new ArrayList<WPMProcessedSample>();
        Queue<WPMProcessedSample> queue = Collections.asLifoQueue(new ArrayDeque<WPMProcessedSample>(stack));
        for(int i=0; i<n; i++){
            WPMProcessedSample sample = queue.remove();
            samples.add( sample );
        }
        return samples;
    }

    /**
     * This method returns a StatisticDTO, useful to communicate to RESTInterface.
     * It contains all the sample averages belong nodes
     * @param param
     * @return
     */
    public StatisticDTO getAllAvgStatistic(String param){

        StatisticDTO ret = new StatisticDTO(param);

        Iterator<WPMProcessedSample> iter;
        for (iter = stack.descendingIterator(); iter.hasNext();  ) {
            WPMProcessedSample processedSample = iter.next();
            ret.addPoint(
                    processedSample.getId(),
                    processedSample.getParam(WPMParam.getByName(param))
            );
        }
        return ret;
    }

    /**
     * This method returns all samples related to a single stat
     * @param param
     * @return
     */
    public StatisticDTO getLastAvgStatistic(String param){
        StatisticDTO ret = new StatisticDTO(param);
        WPMProcessedSample processedSample = stack.peek();
        if(processedSample == null)
            return ret;
        ret.addPoint(
                processedSample.getId(),
                processedSample.getParam(WPMParam.getByName(param))
        );

        return ret;
    }

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

}


