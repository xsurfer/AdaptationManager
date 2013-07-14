package eu.cloudtm.statistics;

import eu.cloudtm.wpm.connector.WPMConnector;
import eu.cloudtm.wpm.logService.remote.events.*;
import eu.cloudtm.wpm.logService.remote.listeners.WPMStatisticsRemoteListener;
import eu.cloudtm.wpm.logService.remote.observables.Handle;
import eu.cloudtm.wpm.parser.ResourceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.RemoteException;
import java.util.*;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/7/13
 */
public class WPMStatisticsRemoteListenerImpl implements WPMStatisticsRemoteListener {

    private final static Log log = LogFactory.getLog(WPMStatisticsRemoteListenerImpl.class);

    private StatsManager statsManager;

    private Handle handle;

    private StatsProcessor processor;

    public WPMStatisticsRemoteListenerImpl(WPMConnector connector, StatsManager statsManager, SubscribeEvent subscribeEvent, StatsProcessor processor){
        this.processor = processor;

        this.statsManager = statsManager;
        try {
            handle = connector.registerStatisticsRemoteListener(subscribeEvent, this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Handle getHandle(){
        return handle;
    }

    @Override
    public void onNewPerVMStatistics(PublishStatisticsEvent publishStatisticsEvent) throws RemoteException {
        log.trace("onNewPerVMStatistics");
    }

    @Override
    public void onNewPerSubscriptionStatistics(PublishStatisticsEvent event) throws RemoteException {

//        Set<String> ips = event.getIps();
//        log.trace("Received statistics from wpm instances " + ips.toString());
//
//        Map<String, Map<String, PublishAttribute>> ip2params = new HashMap<String, Map<String, PublishAttribute>>();
//
//        for (String ip : ips) {
//
//            Map<String, PublishAttribute> nodeStats = new HashMap<String, PublishAttribute>();
//            log.trace("Parsing statistics relevant to " + ip);
//
//            log.trace("Parsing JMX stats");
//            int numResources = event.getNumResources(ResourceType.JMX, ip);
//            if (numResources > 0) {
//                if (numResources > 1) {
//                    log.trace("The log file contains " + numResources + " JMX samples. I' going to consider only the first");
//                }
//                nodeStats.putAll(event.getPublishMeasurement(ResourceType.JMX, 0, ip).getValues());
//            }
//
//            log.trace("Parsing MEM stats");
//            numResources = event.getNumResources(ResourceType.MEMORY, ip);
//            if (numResources > 0) {
//                if (numResources > 1) {
//                    log.trace("The log file contains " + numResources + " JMX samples. I' going to consider only the first");
//                }
//                nodeStats.putAll( event.getPublishMeasurement(ResourceType.JMX, 0, ip).getValues() );
//            }
//
//            ip2params.put(ip, nodeStats);
//        }
//
//        WPMSample wpmSample = WPMSample.getInstance(ip2params);
//
//        String singleNode = wpmSample.getNodes().get(0);
//
//        String repProtWPMValue = (String) wpmSample.getPerNodeParam(Param.CurrentProtocolId, singleNode);
//        ReplicationProtocol currentProtocol = ReplicationProtocol.getByWPMValue( repProtWPMValue );
//        ProcessedSample processedSample = null;
//        switch (currentProtocol){
//            case PB:
//
//                break;
//            case TO:
//                break;
//            case TWOPC:
//                processedSample = new TWOPCInputOracle(wpmSample);
//                break;
//        }
//
//        statsManager.process(processedSample);


    }

    @Override
    public void onNewAggregatedStatistics(PublishAggregatedStatisticsEvent event) throws RemoteException {

        Map<String, Double> aggregatedStats = new HashMap<String, Double>();

        log.trace("Parsing JMX stats");
        PublishMeasurement pm = event.getPublishMeasurement(ResourceType.JMX);
        HashMap<String, PublishAttribute> jmx = pm.getValues();

        for(Map.Entry<String,PublishAttribute> e : jmx.entrySet()){
            aggregatedStats.put(e.getKey(), (Double) e.getValue().getValue());
        }

        log.trace("Parsing MEM stats");
        pm = event.getPublishMeasurement(ResourceType.MEMORY);
        HashMap<String, PublishAttribute> mem = pm.getValues();

        for(Map.Entry<String,PublishAttribute> e : mem.entrySet()){
            aggregatedStats.put(e.getKey(), (Double) e.getValue().getValue());
        }

        WPMSample wpmSample = WPMSample.getInstance(aggregatedStats);
        ProcessedSample processedSample = processor.process(wpmSample);
        statsManager.push(processedSample);
    }



    private void trace(Set<HashMap<String, PublishAttribute>> set) {
        int i = 0;
        for (HashMap<String, PublishAttribute> map : set) {
            log.trace("Map " + (++i));
            for (Map.Entry<String, PublishAttribute> e : map.entrySet()) {
                log.trace(e.getKey() + " - " + e.getValue().getValue());
            }
        }
    }

    private void debug(PublishStatisticsEvent event) {
        Set<String> ips = event.getIps();
        log.trace("Statistics!! " + ips.toString());
        for (String ip : ips) {

            log.trace("Printing Statistics for machine " + ip);

            int numResources = event.getNumResources(ResourceType.JMX, ip);

            if (numResources > 0) {
                for (int i = 0; i < numResources; i++) {
                    PublishMeasurement pm = event.getPublishMeasurement(ResourceType.JMX, i, ip);
                    HashMap<String, PublishAttribute> values = pm.getValues();
                    if (values != null && !values.isEmpty()) {

                        Set<Map.Entry<String, PublishAttribute>> entries = values.entrySet();

                        for (Map.Entry<String, PublishAttribute> entry : entries) {

                            log.trace("" + entry.getKey() + " - " + entry.getValue().getValue());
                        }
                    }
                }

            } else {
                log.trace("No resource found!");
            }
        }
    }

}
