package eu.cloudtm.controller.exceptions;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/21/13
 */
public class OutputFilterException extends Exception {

    public OutputFilterException(String str){
        super(str);
    }

    public OutputFilterException(Throwable t){
        super(t);
    }

}
