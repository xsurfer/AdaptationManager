package eu.cloudtm.autonomicManager.actuators;

import eu.cloudtm.autonomicManager.Actuator;
import eu.cloudtm.autonomicManager.ControllerLogger;
import eu.cloudtm.autonomicManager.actuators.clients.RadargunClient;
import eu.cloudtm.autonomicManager.actuators.excepions.ActuatorException;
import eu.cloudtm.autonomicManager.actuators.excepions.RadargunException;

/**
 * Created by: Fabio Perfetti E-mail: perfabio87@gmail.com Date: 9/27/13
 */
public class RadargunDecorator extends ActuatorDecorator {

   private final RadargunClient radargunClient;

   private final int jmxPort;

   public RadargunDecorator(Actuator decoratedActuator, RadargunClient radargunClient, int jmxPort) {
      super(decoratedActuator);
      this.radargunClient = radargunClient;
      this.jmxPort = jmxPort;
   }

   @Override
   public void stopApplication(String machine) throws ActuatorException {
      ControllerLogger.log.info(" * Stopping application on machine " + machine);

      try {
         radargunClient.stop(machine, jmxPort);
      } catch (RadargunException e) {
         throw new ActuatorException(e);
      }

   }
}
