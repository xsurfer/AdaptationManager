package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.actuators.DeltaCloudActuator;
import eu.cloudtm.commons.PlatformConfiguration;
import eu.cloudtm.autonomicManager.exceptions.ReconfiguratorException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.deltacloud.client.DeltaCloudClientException;

import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/16/13
 */
public class Reconfigurator {

    private final Log log = LogFactory.getLog(Reconfigurator.class);

    private AtomicInteger numReconfig = new AtomicInteger(0);

    public void reconfigure(PlatformConfiguration conf) throws ReconfiguratorException {

        // se già sto riconfigurando aspetto (a meno che: riconf attuale >> riconf prec...)
        // x ora riconfigure se e solo se non sto già riconfigurando

        ControllerLogger.log.info("Reconfiguring: " + conf);

        int numNodes = conf.platformSize();
        int numThreads = conf.threadPerNode();
        try {
            DeltaCloudActuator deltaCloudActuator = DeltaCloudActuator.getInstance(numNodes, numThreads);
            log.warn("Actuator is disabled");
            //deltaCloudActuator.actuate();
            ControllerLogger.log.info(numReconfig.incrementAndGet() + "reconfiguration ended");
        } catch (MalformedURLException e) {
            throw new ReconfiguratorException(e);
        } catch (DeltaCloudClientException e) {
            throw new ReconfiguratorException(e);
        }


    }
}
