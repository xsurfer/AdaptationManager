package eu.cloudtm.autonomicManager.actuators;

import eu.cloudtm.autonomicManager.Actuator;
import eu.cloudtm.autonomicManager.actuators.excepions.ActuatorException;
import eu.cloudtm.autonomicManager.commons.ReplicationProtocol;

import java.util.List;

/**
 * Created by: Fabio Perfetti E-mail: perfabio87@gmail.com Date: 9/27/13
 */
public abstract class ActuatorDecorator implements Actuator {

   protected final Actuator decoratedActuator;

   public ActuatorDecorator(Actuator decoratedActuator) {
      this.decoratedActuator = decoratedActuator;
   }


   @Override
   public abstract void stopApplication(String machine) throws ActuatorException;

   @Override
   public void stopInstance() throws ActuatorException {
      decoratedActuator.stopInstance();
   }

   @Override
   public void startInstance() throws ActuatorException {
      decoratedActuator.startInstance();
   }

   @Override
   public List<String> runningInstances() {
      return decoratedActuator.runningInstances();
   }

   @Override
   public void switchProtocol(ReplicationProtocol repProtocol) throws ActuatorException {
      decoratedActuator.switchProtocol(repProtocol);
   }

   @Override
   public void switchDegree(int degree) throws ActuatorException {
      decoratedActuator.switchDegree(degree);
   }

   @Override
   public void triggerRebalancing(boolean enabled) throws ActuatorException {
      decoratedActuator.triggerRebalancing(enabled);
   }
}
