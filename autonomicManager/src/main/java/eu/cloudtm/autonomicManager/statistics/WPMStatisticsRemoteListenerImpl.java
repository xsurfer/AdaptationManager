package eu.cloudtm.autonomicManager.statistics;

import eu.cloudtm.autonomicManager.commons.TopKeyParam;
import eu.cloudtm.autonomicManager.statistics.samples.WPMSample;
import eu.cloudtm.autonomicManager.statistics.topKeys.TopKeyNodeBucket;
import eu.cloudtm.autonomicManager.statistics.topKeys.TopKeyNodeBucketImpl;
import eu.cloudtm.autonomicManager.statistics.topKeys.TopKeySample;
import eu.cloudtm.autonomicManager.statistics.topKeys.TopKeySampleImpl;
import eu.cloudtm.wpm.connector.WPMConnector;
import eu.cloudtm.wpm.logService.remote.events.PublishAggregatedStatisticsEvent;
import eu.cloudtm.wpm.logService.remote.events.PublishAttribute;
import eu.cloudtm.wpm.logService.remote.events.PublishMeasurement;
import eu.cloudtm.wpm.logService.remote.events.PublishStatisticsEvent;
import eu.cloudtm.wpm.logService.remote.events.SubscribeEvent;
import eu.cloudtm.wpm.logService.remote.listeners.WPMStatisticsRemoteListener;
import eu.cloudtm.wpm.logService.remote.observables.Handle;
import eu.cloudtm.wpm.parser.ResourceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by: Fabio Perfetti E-mail: perfabio87@gmail.com Date: 6/7/13
 */
public class WPMStatisticsRemoteListenerImpl implements WPMStatisticsRemoteListener {

   private final static Log log = LogFactory.getLog(WPMStatisticsRemoteListenerImpl.class);
   private StatsManager statsManager;
   private Handle handle;
   private Processor processor;

   public WPMStatisticsRemoteListenerImpl(WPMConnector connector, StatsManager statsManager, SubscribeEvent subscribeEvent, Processor processor) {
      log.trace("Creating WPMStatisticsRemoteListenerImpl");
      this.processor = processor;

      this.statsManager = statsManager;
      try {
         handle = connector.registerStatisticsRemoteListener(subscribeEvent, this);
      } catch (RemoteException e) {
         throw new RuntimeException(e);
      }
   }

   public Handle getHandle() {
      return handle;
   }

   @Override
   public void onNewPerVMStatistics(PublishStatisticsEvent publishStatisticsEvent) throws RemoteException {
      log.trace("onNewPerVMStatistics");
   }

   private Map<String, Long> mapFromString(String s) {
      if (s == null || s.equals("null"))
         return null;
      if (true)
         return fenixMapFromString(s);
      log.trace("String\n" + s);
      String[] split = s.split("|"), kv;
      log.trace("Split " + Arrays.toString(split));
      Map<String, Long> map = new HashMap<String, Long>();
      for (String topK : split) {
         kv = topK.split("=");
         map.put(kv[0], Long.parseLong(kv[1]));
      }
      return map;
   }

   @Override
   public void onNewPerSubscriptionStatistics(PublishStatisticsEvent event) throws RemoteException {
      log.trace("onNewPerSubscriptionStatistics");
      Set<String> ips = event.getIps();
      TopKeyNodeBucket tnb;
      TopKeySample tks = new TopKeySampleImpl();
      for (String ip : ips) {
         tnb = new TopKeyNodeBucketImpl(ip);
         log.trace("Parsing JMX stats for top Kkeys for node " + ip);
         int numResources = event.getNumResources(ResourceType.JMX, ip);
         if (numResources > 0) {
            if (numResources > 1) {
               log.trace("The log file contains " + numResources + " JMX samples. I' going to consider only the first");
            }
            Map<String, PublishAttribute> jmx = (event.getPublishMeasurement(ResourceType.JMX, 0, ip).getValues());
            if (log.isTraceEnabled()) {
               for (Map.Entry<String, PublishAttribute> e : jmx.entrySet()) {
                  log.trace(e.getKey() + " => " + e.getValue());
                  log.trace(e.getKey() + " => " + e.getValue().getValue());
               }
            }
            String k;
            for (TopKeyParam t : TopKeyParam.values()) {
               k = t.getKey();
               if (jmx.containsKey(k)) {
                  log.trace("Collecting " + k + " for " + ip);
                  PublishAttribute p = jmx.get(k);
                  String keys = (String) p.getValue();
                  tnb.push(t, mapFromString(keys));
               } else {
                  log.trace("Skipping " + k + " for " + ip);
               }
            }
            tks.push(tnb);
         }
      }
      statsManager.pushTopKSample(tks);
      log.trace("pushing new k " + tks);

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
//                processedSample = new TWOPCProcessedSample(wpmSample);
//                break;
//        }
//
//        statsManager.process(processedSample);


   }

   @Override
   public void onNewAggregatedStatistics(PublishAggregatedStatisticsEvent event) throws RemoteException {

      Map<String, Object> aggregatedStats = new HashMap<String, Object>();

      log.trace("Parsing JMX stats");
      PublishMeasurement pm = event.getPublishMeasurement(ResourceType.JMX);
      HashMap<String, PublishAttribute> jmx = pm.getValues();

      for (Map.Entry<String, PublishAttribute> e : jmx.entrySet()) {
         aggregatedStats.put(e.getKey(), e.getValue().getValue());
      }

      log.trace("Parsing MEM stats");
      pm = event.getPublishMeasurement(ResourceType.MEMORY);
      HashMap<String, PublishAttribute> mem = pm.getValues();

      for (Map.Entry<String, PublishAttribute> e : mem.entrySet()) {
         aggregatedStats.put(e.getKey(), e.getValue().getValue());
      }

      WPMSample wpmSample = WPMSample.getInstance(aggregatedStats);
      ProcessedSample processedSample = processor.process(wpmSample);
      statsManager.push(processedSample);
   }


//    Usato per leggere le chiavi
//    @Override
//    public void onNewAggregatedStatistics(PublishAggregatedStatisticsEvent event) throws RemoteException {
//
//        Map<String, Object> aggregatedStats = new HashMap<String, Object>();
//
//        log.trace("Parsing JMX stats");
//        PublishMeasurement pm = event.getPublishMeasurement(ResourceType.JMX);
//        HashMap<String, PublishAttribute> jmx = pm.getValues();
//
//        log.info("SIZE jmx: " + jmx.size());
//        int i = 0;
//        for(Map.Entry<String,PublishAttribute> e : jmx.entrySet()){
//            log.info(i + " - " +e.getKey() + " : " + e.getValue().getValue().getClass());
//        }
//
//        log.trace("Parsing MEM stats");
//        pm = event.getPublishMeasurement(ResourceType.MEMORY);
//        HashMap<String, PublishAttribute> mem = pm.getValues();
//
//        for(Map.Entry<String,PublishAttribute> e : mem.entrySet()){
//            log.info(e.getKey() + ":" + e.getValue().getValue().getClass());
//        }
//
//        //WPMSample wpmSample = WPMSample.getInstance(aggregatedStats);
//        //ProcessedSample processedSample = processor.process(wpmSample);
//        //statsManager.push(processedSample);
//    }

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

   //Apparently,this method depends on the application. It should be refactored and made pluggable through an object
   private Map<String, Long> fenixMapFromString(String s) {

      s = s.substring(1);
      log.trace("TOMAP " + s);
      String[] eq;
      String temp1, temp2;
      String[] split = s.split("\\|");
      log.trace(Arrays.toString(split));
      Map<String, Long> map = new HashMap<String, Long>();

      for (int i = 0; i < split.length; ) {
         temp1 = split[i++];
         eq = split[i++].split("=");
         temp1 = temp1.concat("\\|").concat(eq[0]);
         map.put(temp1, Long.parseLong(eq[1]));
         log.trace("ADDING " + temp1 + " - " + Long.parseLong(eq[1]));
      }
      return map;
   }

}
