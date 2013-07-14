package eu.cloudtm;

import eu.cloudtm.actuators.DeltaCloudActuator;
import eu.cloudtm.commons.PlatformConfiguration;
import eu.cloudtm.exceptions.ReconfiguratorException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.deltacloud.client.DeltaCloudClientException;

import java.net.MalformedURLException;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/16/13
 */
public class Reconfigurator {

    private final Log log = LogFactory.getLog(Reconfigurator.class);

    public void reconfigure(PlatformConfiguration conf) throws ReconfiguratorException {

        // se già sto riconfigurando aspetto (a meno che: riconf attuale >> riconf prec...)
        // x ora riconfigure se e solo se non sto già riconfigurando

        int numNodes = conf.platformSize();
        int numThreads = conf.threadPerNode();
        try {
            DeltaCloudActuator deltaCloudActuator = DeltaCloudActuator.getInstance(numNodes, numThreads);
            deltaCloudActuator.actuate();
            ControllerLogger.log.info("Riconfigurazione terminata");
        } catch (MalformedURLException e) {
            throw new ReconfiguratorException(e);
        } catch (DeltaCloudClientException e) {
            throw new ReconfiguratorException(e);
        }


    }
}
