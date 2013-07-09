package eu.cloudtm.stats;

import eu.cloudtm.common.SampleListener;
import eu.cloudtm.wpm.logService.remote.events.*;
import eu.cloudtm.wpm.logService.remote.listeners.WPMStatisticsRemoteListener;
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

    private Set<SampleListener> listeners = new HashSet();

    public WPMStatisticsRemoteListenerImpl(){

    }

    public WPMStatisticsRemoteListenerImpl(Set<SampleListener> _listeners){
        listeners.addAll(_listeners);
    }

    @Override
    public void onNewPerVMStatistics(PublishStatisticsEvent publishStatisticsEvent) throws RemoteException {
        log.trace("onNewPerVMStatistics");
    }

    @Override
    public void onNewPerSubscriptionStatistics(PublishStatisticsEvent event) throws RemoteException {

        Set<String> ips = event.getIps();
        log.trace("Received statistics from wpm instances " + ips.toString());

        Map<String, Map<WPMParam, Double>> ip2params = new HashMap<String, Map<WPMParam, Double>>();

        for (String ip : ips) {

            Map<WPMParam, Double> nodeStats = new HashMap<WPMParam, Double>();       // conterra tutti i campionamenti per tale ip, perdendo l'organizzazione in JMX, MEM...
            log.trace("Parsing statistics relevant to " + ip);

            log.trace("Parsing JMX stats");
            int numResources = event.getNumResources(ResourceType.JMX, ip);
            if (numResources > 0) {
                if (numResources > 1) {
                    log.trace("The log file contains " + numResources + " JMX samples. I' going to consider only the first");
                }

                for(Map.Entry<String, PublishAttribute> entry : event.getPublishMeasurement(ResourceType.JMX, 0, ip).getValues().entrySet()){
                    WPMParam key = WPMParam.getByName(entry.getKey());
                    double val = new Double(entry.getValue().getValue().toString());
                    nodeStats.put(key, val);
                }

            }

            log.trace("Parsing MEM stats");
            numResources = event.getNumResources(ResourceType.MEMORY, ip);
            if (numResources > 0) {
                if (numResources > 1) {
                    log.trace("The log file contains " + numResources + " JMX samples. I' going to consider only the first");
                }

                for(Map.Entry<String, PublishAttribute> entry : event.getPublishMeasurement(ResourceType.MEMORY, 0, ip).getValues().entrySet()){
                    WPMParam key = WPMParam.getByName(entry.getKey());
                    double val = new Double(entry.getValue().getValue().toString());
                    nodeStats.put(key, val);
                }
            }


            ip2params.put(ip, nodeStats);
        }

        //trace(jmx);
        //trace(mem);

        WPMSample newSample = WPMSample.getInstance(ip2params);
        notifyListeners(newSample);

    }

    @Override
    public void onNewAggregatedStatistics(PublishAggregatedStatisticsEvent event) throws RemoteException {
        /*
        log.trace("Parsing JMX stats");
        PublishMeasurement pm = event.getPublishMeasurement(ResourceType.JMX);
        HashMap<String, PublishAttribute> jmx = pm.getValues();

        log.trace("Parsing MEM stats");
        pm = event.getPublishMeasurement(ResourceType.MEMORY);
        HashMap<String, PublishAttribute> mem = pm.getValues();


        WPMSample newSample = WPMSample.getInstance(jmx, mem);
        notifyListeners(newSample);
        */
    }

    private void notifyListeners(WPMSample sample){
        for(SampleListener listener : listeners){
            listener.onNewSample(sample);
        }
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


    public boolean addSampleListener(SampleListener sampleListener){
        return listeners.add(sampleListener);
    }

    public boolean removeSampleListener(SampleListener sampleListener){
        return listeners.remove(sampleListener);
    }

    public Set<SampleListener> getSampleListeners(){
        return this.listeners;
    }

}
