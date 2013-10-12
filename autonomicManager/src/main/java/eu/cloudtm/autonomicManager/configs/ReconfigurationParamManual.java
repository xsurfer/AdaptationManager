package eu.cloudtm.autonomicManager.configs;

/**
 * // TODO: Document this
 *
 * @author diego
 * @since 4.0
 */
public class ReconfigurationParamManual extends ReconfigurationParam {
   @Override
   public boolean isAutoTuning() {
      return false;
   }
}
