package eu.cloudtm;

import eu.cloudtm.exceptions.ActuatorException;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/27/13
 */
public interface IActuator {

    public void actuate() throws ActuatorException;

}
