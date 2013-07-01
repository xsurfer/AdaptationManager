package eu.cloudtm.controller.oracles;

import eu.cloudtm.controller.actuators.DeltaCloudActuator;
import org.apache.deltacloud.client.DeltaCloudClientException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Scanner;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/24/13
 */
public class SimulatorTest {

    public static void main(String[] args) {
        Simulator sim = new Simulator();
        sim.forecast(null,1,1);



    }

}
