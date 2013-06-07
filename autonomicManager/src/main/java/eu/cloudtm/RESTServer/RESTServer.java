package eu.cloudtm.RESTServer;

import com.sun.jersey.api.container.grizzly2.GrizzlyWebContainerFactory;
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

public class RESTServer {

    private static Log log = LogFactory.getLog(RESTServer.class);

    private final static String DEFAULT_HOST = "localhost";
    private final static int DEFAULT_PORT = 9998;

    private HttpServer httpServer;

    private int getPort(int defaultPort) {
        //grab port from environment, otherwise fall back to default port 9998
        String httpPort = System.getProperty("autonomicManager.rest.port");
        if (null != httpPort) {
            try {
                return Integer.parseInt(httpPort);
            } catch (NumberFormatException e) {
            }
        }
        return defaultPort;
    }

    private URI getBaseURI(String defaultHost) {
        //grab port from environment, otherwise fall back to default port 9998
        String host = System.getProperty("autonomicManager.rest.host");
        if (null != host) {
            try {
                String uri = "http://" + host + "/";
                return UriBuilder.fromUri(uri).port(getPort(DEFAULT_PORT)).build();
            } catch (NumberFormatException e) {
            }
        }

        String uri = "http://" + defaultHost + "/";
        return UriBuilder.fromUri(uri).port(getPort(DEFAULT_PORT)).build();
    }

    //public static final URI BASE_URI = getBaseURI();

    public void startServer() {
        final Map<String, String> initParams = new HashMap<String, String>();

        initParams.put("com.sun.jersey.config.property.packages", "eu.cloudtm.RESTServer.resources");

        log.info("Starting grizzly2...");
        try {
            this.httpServer=GrizzlyWebContainerFactory.create(getBaseURI(DEFAULT_HOST), initParams);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl",
                getBaseURI(DEFAULT_HOST)));
    }

    public void stopServer(){
        httpServer.stop();
    }

    /*
    public static void main(String[] args) throws IOException {
        String hostname;
        Options options = new Options();
        options.addOption("host", true, "hostname to listen on");

        CommandLineParser parser = new GnuParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        hostname = cmd.getOptionValue("host");
        if (hostname == null || hostname.length() <= 0)
            hostname = "localhost";
        log.info("HOSTNAME: " + hostname);

        RESTServer server = new RESTServer();
        server.startServer();

        log.info("Hit 1000 to stop it...");
        int num = 0;
        while (num != 1000) {
            Scanner in = new Scanner(System.in);
            num = in.nextInt();
        }
        server.stopServer();

    }
    */
}
