package eu.cloudtm.autonomicManager.commons;

/**
 * // TODO: Document this
 *
 * @author diego
 * @since 4.0
 */
public enum TopKeyParam {

   LOCAL_GET("RemoteTopGets"),
   REMOTE_GET("LocalTopGets"),
   LOCAL_PUT("RemoteTopPuts"),
   REMOTE_PUT("LocalTopPuts"),
   CONTENDED("TopLockedKeys"),
   LOCKED("TopContendedKeys"),
   FAILED("TopLockFailedKeys"),
   WRITE_SKEW_FAILED("TopWriteSkewFailedKeys");

   private String key;

   private TopKeyParam(String key) {
      this.key = key;
   }

   public String getKey() {
      return key;
   }
}
