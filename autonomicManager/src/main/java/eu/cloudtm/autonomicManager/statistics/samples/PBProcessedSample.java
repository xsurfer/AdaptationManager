package eu.cloudtm.autonomicManager.statistics.samples;

import eu.cloudtm.autonomicManager.statistics.ProcessedSample;
import eu.cloudtm.autonomicManager.statistics.Sample;

/**
 * // TODO: Document this
 *
 * @author diego
 * @since 4.0
 */
public class PBProcessedSample extends ProcessedSample {
   public PBProcessedSample(Sample sample) {
      super(sample);
   }

   @Override
   protected Double getACF() {
      return 0.0D;  // TODO: Customise this generated block
   }
}
