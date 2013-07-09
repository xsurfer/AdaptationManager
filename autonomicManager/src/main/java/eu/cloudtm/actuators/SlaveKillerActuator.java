package eu.cloudtm.actuators;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import eu.cloudtm.IActuator;
import eu.cloudtm.actuators.radargun.RadargunException;
import eu.cloudtm.actuators.radargun.SlaveKillerResponse;
import eu.cloudtm.exceptions.ActuatorException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.deltacloud.client.DeltaCloudClientException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.MalformedURLException;
import java.net.URI;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/27/13
 */
public class SlaveKillerActuator implements IActuator {

    private Log log = LogFactory.getLog(SlaveKillerActuator.class);

    private final String HOST;
    private final String PORT;

    private final String SLAVE;
    private final String JMX_PORT;
    private final String COMPONENT;

    public static SlaveKillerActuator getInstance(String slaveHost, int jmxPort, String component) throws MalformedURLException, DeltaCloudClientException {

        String slaveKillerHost = "cloudtm.ist.utl.pt";
        int slaveKillerPort = 5455;
        SlaveKillerActuator actuator = new SlaveKillerActuator(slaveKillerHost, slaveKillerPort, slaveHost, jmxPort, component);

        return actuator;
    }

    private SlaveKillerActuator(String slaveKillerHost, int slaveKillerPort, String slaveHost, int jmxPort, String component){
        HOST = slaveKillerHost;
        PORT = String.valueOf(slaveKillerPort);

        SLAVE = slaveHost;
        JMX_PORT = String.valueOf(jmxPort);
        COMPONENT = component;
    }

    @Override
    public void actuate() throws ActuatorException {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource service = client.resource(getBaseURI());

        String res = service.path(SLAVE)
                .path( String.valueOf(JMX_PORT) )
                .path(COMPONENT)
                .accept(MediaType.APPLICATION_JSON)
                .get(String.class);

        log.info(res);
        SlaveKillerResponse response = new Gson().fromJson(res, SlaveKillerResponse.class);

        if(!response.getResult().equals("Success"))
            throw new RadargunException("Problem while stopping " + response.toString());

        //log.info("response result: " + response.getResult());
    }

    private URI getBaseURI() {
        URI uri = UriBuilder.fromUri("http://" + HOST + ":" + PORT + "/stop").build();
        log.info(uri);
        return uri;
    }


    /* **** */
    /* ENUM */


}
