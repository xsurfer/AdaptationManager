package eu.cloudtm.controller;

import eu.cloudtm.controller.actuators.DeltaCloudActuator;
import eu.cloudtm.controller.exceptions.ActuatorException;
import eu.cloudtm.controller.exceptions.OutputFilterException;
import eu.cloudtm.controller.model.PlatformConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.SimpleLog;
import org.apache.deltacloud.client.DeltaCloudClientException;

import java.net.MalformedURLException;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/16/13
 */
public class OutputFilter {

    private final Log log = LogFactory.getLog(OutputFilter.class);

    public void doFilter(PlatformConfiguration conf) throws OutputFilterException {

        // se già sto riconfigurando aspetto (a meno che: riconf attuale >> riconf prec...)
        // x ora riconfigure se e solo se non sto già riconfigurando

        int numNodes = conf.platformSize();
        int numThreads = conf.threadPerNode();
        try {
            DeltaCloudActuator deltaCloudActuator = DeltaCloudActuator.getInstance(numNodes, numThreads);
            deltaCloudActuator.actuate();
            ControllerLogger.log.info("Riconfigurazione terminata");
        } catch (MalformedURLException e) {
            throw new OutputFilterException(e);
        } catch (DeltaCloudClientException e) {
            throw new OutputFilterException(e);
        }


    }
}
