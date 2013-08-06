package eu.cloudtm.autonomicManager.actuators.test;

import eu.cloudtm.autonomicManager.IActuator;
import eu.cloudtm.autonomicManager.actuators.CloudTMActuator;
import eu.cloudtm.autonomicManager.actuators.FutureGridActuator;
import eu.cloudtm.autonomicManager.actuators.excepions.ActuatorException;
import eu.cloudtm.autonomicManager.commons.ReplicationProtocol;
import org.apache.deltacloud.client.DeltaCloudClient;
import org.apache.deltacloud.client.DeltaCloudClientException;
import org.apache.deltacloud.client.DeltaCloudClientImpl;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Scanner;

/**
* Created by: Fabio Perfetti
* E-mail: perfabio87@gmail.com
* Date: 6/20/13
*/
public class FutureGridActuatorTest {

    public static void main(String[] args) {

        IActuator mockActuator = new IActuator() {
            @Override
            public void stopInstance() throws ActuatorException {
                System.out.println("no-op");
            }

            @Override
            public void startInstance() throws ActuatorException {
                System.out.println("no-op");
            }

            @Override
            public List<String> runningInstances() {
                System.out.println("no-op");
                return null;
            }

            @Override
            public void switchProtocol(ReplicationProtocol repProtocol) throws ActuatorException {
                System.out.println("no-op");
            }

            @Override
            public void switchDegree(int degree) throws ActuatorException {
                System.out.println("no-op");
            }
        };

        FutureGridActuator actuator = new FutureGridActuator(mockActuator);

        try {
            actuator.startInstance();
        } catch (ActuatorException e) {
            e.printStackTrace();
        }

    }

}