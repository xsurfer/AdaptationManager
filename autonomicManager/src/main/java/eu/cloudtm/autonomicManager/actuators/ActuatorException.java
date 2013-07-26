package eu.cloudtm.autonomicManager.actuators;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/26/13
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActuatorException extends Exception {

    public ActuatorException(String str){
        super(str);
    }

    public ActuatorException(Throwable e){
        super(e);
    }

}
