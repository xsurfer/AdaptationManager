package eu.cloudtm.stats;

import eu.cloudtm.LookupRegister;
import eu.cloudtm.StatsManager;
import eu.cloudtm.controller.Controller;
import eu.cloudtm.wpm.logService.remote.events.PublishAttribute;
import eu.cloudtm.wpm.logService.remote.events.PublishMeasurement;
import eu.cloudtm.wpm.logService.remote.events.PublishStatisticsEvent;
import eu.cloudtm.wpm.logService.remote.listeners.WPMStatisticsRemoteListener;
import eu.cloudtm.wpm.parser.ResourceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/7/13
 */
public class WPMStatisticsRemoteListenerImpl implements WPMStatisticsRemoteListener {

    private final static Log log = LogFactory.getLog(WPMStatisticsRemoteListenerImpl.class);

    private StatsManager statsManager = LookupRegister.getStatsManager();
    private Controller controller = LookupRegister.getController();

    public WPMStatisticsRemoteListenerImpl(){
    }

    @Override
    public void onNewPerVMStatistics(PublishStatisticsEvent publishStatisticsEvent) throws RemoteException {

        log.trace("onNewPerVMStatistics");

    }

    @Override
    public void onNewPerSubscriptionStatistics(PublishStatisticsEvent event) throws RemoteException {

        Set<String> ips = event.getIps();
        log.trace("Received statistics from wpm instances " + ips.toString());
        /*
        if (!controller.canProcessNewData()) {
            log.trace("Masked interrupt. The Controller is still analyzing last data");
            controller.resetStateTimeWindow();
            return;
        }
        */
        Set<HashMap<String, PublishAttribute>> jmx = new HashSet<HashMap<String, PublishAttribute>>();
        Set<HashMap<String, PublishAttribute>> mem = new HashSet<HashMap<String, PublishAttribute>>();
        for (String ip : ips) {
            log.trace("Parsing statistics relevant to " + ip);

            log.trace("Parsing JMX stats");
            int numResources = event.getNumResources(ResourceType.JMX, ip);
            if (numResources > 0) {
                if (numResources > 1) {
                    log.trace("The log file contains " + numResources + " JMX samples. I' going to consider only the first");
                }
                jmx.add(event.getPublishMeasurement(ResourceType.JMX, 0, ip).getValues());
            }

            log.trace("Parsing MEM stats");
            numResources = event.getNumResources(ResourceType.MEMORY, ip);
            if (numResources > 0) {
                if (numResources > 1) {
                    log.trace("The log file contains " + numResources + " MEM samples. I' going to consider only the first");
                }
                mem.add(event.getPublishMeasurement(ResourceType.MEMORY, 0, ip).getValues());
            }
        }

        //lastJMX = jmx;
        //lastMEM = mem;

        //trace(jmx);
        //trace(mem);
        Sample newSample = statsManager.add(jmx,mem);
        controller.doNotify(newSample);


        /*
        try {
            this.controller.consumeStats(jmx, mem);
        } catch (PublishAttributeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (Tas2Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        */

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
