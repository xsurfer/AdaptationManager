package eu.cloudtm;

import eu.cloudtm.stats.Statistic;
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
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/6/13
 */
public class StatsManager
        extends Observable {

    private final static Log log = LogFactory.getLog(StatsManager.class);

    private WPMConnector connector;
    private Handle lastVmHandle;

    private final static int MAX_SIZE = 1000;

    private Deque<Statistic> stack = new ArrayDeque<Statistic>(MAX_SIZE);

    private AtomicInteger counter = new AtomicInteger(0);

//    private Set<HashMap<String, PublishAttribute>> lastJMX;
//    private Set<HashMap<String, PublishAttribute>> lastMEM;

    public StatsManager(){
        try {
            connector = new WPMConnector();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            connector.registerViewChangeRemoteListener(new WPMViewChangeRemoteListenerImpl(connector,this));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void newStatsAvailables(){
        log.info("NEW STATS AVAILABLES");
    }


    public void addStatistic(Set<HashMap<String, PublishAttribute>> jmx,
                             Set<HashMap<String, PublishAttribute>> mem){
        if(stack.size()>=MAX_SIZE){
            Statistic removed = stack.removeLast();
            log.info("Ho eliminato la statistica: " + removed.getId());
        }
        Statistic newStat = new Statistic(counter.getAndIncrement(),jmx,mem);
        stack.push(newStat);
        log.info("Aggiunta Statistica id: " + newStat.getId());
    }

    public double getLastAvgAttribute(String attribute, ResourceType type) {

        Statistic lastStat = stack.element();
        Set<HashMap<String, PublishAttribute>> values;
        switch (type){
            case JMX:
                values = lastStat.getJmx();
                break;
            case MEMORY:
                values = lastStat.getMem();
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
            log.trace("Asking for " + attribute);
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

    private double cast(Object o) throws ClassCastException {
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



    //LocalReadOnlyTxLocalServiceTime
}


