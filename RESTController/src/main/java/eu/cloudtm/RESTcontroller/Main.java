package eu.cloudtm.RESTcontroller;

import com.sun.jersey.api.container.grizzly2.GrizzlyWebContainerFactory;
import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.grizzly.http.server.HttpServer;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.UriBuilder;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 5/30/13
 */

public class Main {

    private static Log log = LogFactory.getLog(Main.class);

    private static String hostname;

    private static int getPort(int defaultPort) {
        //grab port from environment, otherwise fall back to default port 9998
        String httpPort = System.getProperty("jersey.test.port");
        if (null != httpPort) {
            try {
                return Integer.parseInt(httpPort);
            } catch (NumberFormatException e) {
            }
        }
        return defaultPort;
    }

    private static URI getBaseURI() {

        String uri = "http://"+hostname+"/";
        return UriBuilder.fromUri(uri).port(getPort(9998)).build();
    }

    //public static final URI BASE_URI = getBaseURI();

    protected static HttpServer startServer() throws IOException {
        final Map<String, String> initParams = new HashMap<String, String>();

        initParams.put("com.sun.jersey.config.property.packages", "eu.cloudtm.RESTcontroller.resources");

        log.info("Starting grizzly2...");
        return GrizzlyWebContainerFactory.create(getBaseURI(), initParams);
    }

    public static void main(String[] args) throws IOException {
        Options options = new Options();
        options.addOption("host", true, "hostname to listen on");

        CommandLineParser parser = new GnuParser();
        CommandLine cmd;
        try {
            cmd = parser.parse( options, args);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        hostname = cmd.getOptionValue("host");
        log.info("HOSTNAME: " + hostname);

        // Grizzly 2 initialization
        HttpServer httpServer = startServer();
        log.info(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...",
                getBaseURI()));
        System.in.read();
        httpServer.stop();
    }
}