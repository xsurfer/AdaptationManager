package eu.cloudtm.autonomicManager.commons.dto.topK;

/**
 * // TODO: Document this
 *
 * @author diego
 * @since 4.0
 */

import java.util.ArrayList;
import java.util.List;

/**
 * It contains, on a per node granularity, the topK relevant to a certain parameter (e.g., top puts or gets)
 */
public class TopKStatDTO {
   private int numNodes;
   private String statName;
   private List<NodeTopKStatDTO> nodeTopK = new ArrayList<NodeTopKStatDTO>();

   public TopKStatDTO(int numNodes, String statName) {
      this.numNodes = numNodes;
      this.statName = statName;
   }

   public void addNode(NodeTopKStatDTO nodeTopKStatDTO) {
      nodeTopK.add(nodeTopKStatDTO);
   }
}
