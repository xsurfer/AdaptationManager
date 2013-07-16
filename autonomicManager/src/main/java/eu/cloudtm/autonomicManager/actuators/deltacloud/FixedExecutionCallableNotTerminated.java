package eu.cloudtm.autonomicManager.actuators.deltacloud;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/21/13
 */
public class FixedExecutionCallableNotTerminated extends Exception {
    public FixedExecutionCallableNotTerminated(){
        super("FixedExecutionCallableNotTerminated is not yet terminated! Use isTerminated() to verify it");
    }
}
