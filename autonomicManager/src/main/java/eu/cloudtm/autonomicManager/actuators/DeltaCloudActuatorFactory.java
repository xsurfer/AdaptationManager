package eu.cloudtm.autonomicManager.actuators;

import eu.cloudtm.autonomicManager.configs.Config;
import eu.cloudtm.autonomicManager.configs.KeyConfig;
import org.apache.deltacloud.client.DeltaCloudClient;
import org.apache.deltacloud.client.DeltaCloudClientException;
import org.apache.deltacloud.client.DeltaCloudClientImpl;

import java.net.MalformedURLException;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/23/13
 * Time: 9:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class DeltaCloudActuatorFactory {

    private static DeltaCloudActuatorFactory instance;

    private DeltaCloudActuatorFactory(){ }

    public static DeltaCloudActuatorFactory instance(){
        if(instance==null){
            instance = new DeltaCloudActuatorFactory();
        }
        return instance;
    }

    public DeltaCloudActuator build() throws MalformedURLException, DeltaCloudClientException {

        String host = Config.getInstance().getString(KeyConfig.DELTACLOUD_URL.key());
        String user = Config.getInstance().getString(KeyConfig.DELTACLOUD_USER.key());
        String password = Config.getInstance().getString(KeyConfig.DELTACLOUD_PASSWORD.key());

        String imageId = Config.getInstance().getString(KeyConfig.DELTACLOUD_IMAGE.key());
        String flavorId = Config.getInstance().getString(KeyConfig.DELTACLOUD_FLAVOR.key());

        DeltaCloudClient client = new DeltaCloudClientImpl( host, user, password );
        DeltaCloudActuator actuator = new DeltaCloudActuator(client, imageId, flavorId);

        return actuator;
    }

}
