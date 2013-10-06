package eu.cloudtm.autonomicManager.oracles;

/**
 * // TODO: Document this
 *
 * @author diego
 * @since 4.0
 */
public enum OracleServiceEnum {

   HILL_CLIMBING("hillClimbing"),
   EXHAUSTIVE("exhaustive"),
   FAKE("fake");

   private String key;

   private OracleServiceEnum(String key) {
      this.key = key;
   }

   public String getKey() {
      return key;
   }
}
