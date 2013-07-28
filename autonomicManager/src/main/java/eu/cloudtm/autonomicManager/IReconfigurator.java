package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.commons.IPlatformConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/24/13
 * Time: 11:09 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IReconfigurator {

    public boolean reconfigure(IPlatformConfiguration nextConf);

    public boolean isReconfiguring();

}
