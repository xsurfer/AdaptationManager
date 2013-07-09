package eu.cloudtm.actuators;

import eu.cloudtm.exceptions.ActuatorException;
import org.apache.deltacloud.client.DeltaCloudClientException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.Scanner;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/20/13
 */
public class RadarGunTest {

    public static void main(String[] args) {
        System.out.println("Quale ip uccidere??");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Scanner in = new Scanner(System.in);

        String hostname, component;
        int port;

        try {
            hostname = br.readLine();
            port = in.nextInt();
            component = br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            SlaveKillerActuator actuator = SlaveKillerActuator.getInstance(hostname, port, component);
            actuator.actuate();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (DeltaCloudClientException e) {
            e.printStackTrace();
        } catch (ActuatorException e) {
            e.printStackTrace();
        }
    }
}
