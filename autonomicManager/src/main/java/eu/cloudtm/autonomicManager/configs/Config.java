package eu.cloudtm.autonomicManager.configs;

import eu.cloudtm.autonomicManager.commons.PlatformConfiguration;
import eu.cloudtm.autonomicManager.commons.ReplicationProtocol;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Created with IntelliJ IDEA. User: fabio Date: 7/23/13 Time: 10:22 AM To change this template use File | Settings |
 * File Templates.
 */
public class
      Config {

   private static AdaptationManagerConfig instance;

   public static AdaptationManagerConfig getInstance() {
      if (instance == null) {
         try {
            instance = new AdaptationManagerConfig("config/config.properties");
         } catch (ConfigurationException e) {
            throw new RuntimeException(e);
         }
      }
      return instance;
   }



}
