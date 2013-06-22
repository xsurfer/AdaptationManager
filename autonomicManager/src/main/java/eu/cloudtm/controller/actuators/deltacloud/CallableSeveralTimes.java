package eu.cloudtm.controller.actuators.deltacloud;

import java.util.concurrent.Callable;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/21/13
 */
public interface CallableSeveralTimes<V> extends Callable<V> {
    public void setIterations(int val);
}