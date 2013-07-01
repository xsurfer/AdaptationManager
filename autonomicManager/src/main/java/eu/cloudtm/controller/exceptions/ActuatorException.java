package eu.cloudtm.controller.exceptions;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/21/13
 */
public class ActuatorException extends Exception {

    public ActuatorException(String str){
        super(str);
    }

    public ActuatorException(Throwable t){
        super(t);
    }

}
