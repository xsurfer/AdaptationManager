package eu.cloudtm.exceptions;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/21/13
 */
public class OracleException extends Exception {

    public OracleException(String str){
        super(str);
    }

    public OracleException(Throwable t){
        super(t);
    }

}
