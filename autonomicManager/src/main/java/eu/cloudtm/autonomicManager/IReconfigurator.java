package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.commons.PlatformConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/24/13
 * Time: 11:09 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IReconfigurator {

    public void reconfigure(PlatformConfiguration nextConf);

    public boolean isReconfiguring();

}
