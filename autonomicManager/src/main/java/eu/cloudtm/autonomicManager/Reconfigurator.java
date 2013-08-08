package eu.cloudtm.autonomicManager;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/24/13
 * Time: 11:09 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Reconfigurator<T> {

    public void reconfigure(T toReconfigure);

    public boolean isReconfiguring();

}
