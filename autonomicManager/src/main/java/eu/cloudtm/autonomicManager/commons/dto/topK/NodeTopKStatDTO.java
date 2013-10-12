package eu.cloudtm.autonomicManager.commons.dto.topK;

import java.util.ArrayList;
import java.util.List;

/**
 * // TODO: Document this
 *
 * @author diego
 * @since 4.0
 */
public class NodeTopKStatDTO {
   private String nodeName;
   private int numEntries = 0;
   private List<KDTO> histogram = new ArrayList<KDTO>();

   public NodeTopKStatDTO(String nodeName) {
      this.nodeName = nodeName;
   }

   public void addSample(KDTO k) {
      histogram.add(k);
      numEntries++;
   }
}
