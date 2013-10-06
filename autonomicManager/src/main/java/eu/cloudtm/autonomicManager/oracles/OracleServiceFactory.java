package eu.cloudtm.autonomicManager.oracles;

import eu.cloudtm.autonomicManager.configs.AdaptationManagerConfig;
import eu.cloudtm.autonomicManager.configs.Config;

/**
 * // TODO: Document this
 *
 * @author diego
 * @since 4.0
 */
public class OracleServiceFactory {

   public static OracleService buildOracleService(Oracle o) {
      AdaptationManagerConfig config = Config.getInstance();
      if(config.isOracleServiceHillClimbing())
         return new HillClimbingOracleService(o);
      if(config.isOracleServiceExhaustive())
         return new OracleServiceImpl(o);
      throw new IllegalArgumentException(("Please specify a valid oracle service"));
   }
}
