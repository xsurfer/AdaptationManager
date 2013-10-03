package eu.cloudtm.autonomicManager.configs;

import eu.cloudtm.autonomicManager.commons.PlatformConfiguration;
import eu.cloudtm.autonomicManager.commons.ReplicationProtocol;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * // TODO: Document this
 *
 * @author diego
 * @since 4.0
 */
public class AdaptationManagerConfig extends PropertiesConfiguration {

   public AdaptationManagerConfig(String fileName) throws ConfigurationException {
      super(fileName);
   }

   public PlatformConfiguration initPlatformConfig() {
      ReplicationProtocol rp = ReplicationProtocol.valueOf(getString(KeyConfig.ENVIRONMENT_INIT_R_PROT.key()));
      int initNodes = getInt(KeyConfig.ENVIRONMENT_INIT_NODES.key());
      int rD = getInt(KeyConfig.ENVIRONMENT_INIT_R_DEGREE.key());
      if (rD < initNodes)
         rD = initNodes;
      return new PlatformConfiguration(initNodes, rD, rp);
   }

}
