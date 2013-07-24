package eu.cloudtm.autonomicManager.actuators;

import org.apache.deltacloud.client.DeltaCloudClientException;

import java.net.MalformedURLException;
import java.util.Scanner;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/20/13
 */
public class DeltaCloudTest {

    public static void main(String[] args) {
        System.out.println("Quante istanze vuoi?");
        Scanner in = new Scanner(System.in);
        int num = in.nextInt();

        try {
            DeltaCloudActuator actuator = DeltaCloudActuatorFactory.instance().build();
            actuator.actuate(num, 2);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (DeltaCloudClientException e) {
            e.printStackTrace();
        }
    }
}
