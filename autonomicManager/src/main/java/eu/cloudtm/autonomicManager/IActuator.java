package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.actuators.ActuatorException;
import eu.cloudtm.commons.ReplicationProtocol;
import org.apache.deltacloud.client.Instance;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/26/13
 * Time: 2:53 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IActuator {

    public void stopInstance() throws ActuatorException;

    public void startInstance() throws ActuatorException;

    public List<Instance> runningInstances();

    public void switchProtocol(ReplicationProtocol repProtocol) throws ActuatorException;

    public void switchDegree(int degree) throws ActuatorException;

}
