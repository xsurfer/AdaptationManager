package eu.cloudtm.autonomicManager.commons.dto.topK;

import java.util.ArrayList;
import java.util.List;

/**
 * // TODO: Document this
 *
 * @author diego
 * @since 4.0
 */

/**
 * It contains, for each stats, a container with top k at node granularity
 */
public class GlobalTopKDTO {
   private int numStats = 0;
   private List<TopKStatDTO> statKeys = new ArrayList<TopKStatDTO>();

   public void addTopKStat(TopKStatDTO tk) {
      statKeys.add(tk);
      numStats++;
   }

}
