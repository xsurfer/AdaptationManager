package eu.cloudtm.controller;

import eu.cloudtm.controller.actuators.radargun.RadargunException;
import eu.cloudtm.controller.exceptions.ActuatorException;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/27/13
 */
public interface IActuator {

    public void actuate() throws ActuatorException;

}
