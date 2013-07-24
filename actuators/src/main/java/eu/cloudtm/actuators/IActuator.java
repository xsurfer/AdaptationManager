package eu.cloudtm.actuators;

import eu.cloudtm.actuators.exceptions.ActuatorException;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/27/13
 */
public interface IActuator {

    public void actuate(int nodes, int threads) throws ActuatorException;

}
