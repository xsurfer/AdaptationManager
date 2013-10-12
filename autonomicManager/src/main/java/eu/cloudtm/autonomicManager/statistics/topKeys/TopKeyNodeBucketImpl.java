package eu.cloudtm.autonomicManager.statistics.topKeys;

import eu.cloudtm.autonomicManager.commons.TopKeyParam;

import java.util.HashMap;
import java.util.Map;

/**
 * // TODO: Document this
 *
 * @author diego
 * @since 4.0
 */
public class TopKeyNodeBucketImpl implements TopKeyNodeBucket {
   String nodeID;
   private Map<TopKeyParam, Map<String, Long>> topKMap = new HashMap<TopKeyParam, Map<String, Long>>();

   public TopKeyNodeBucketImpl(String nodeID) {
      this.nodeID = nodeID;
   }

   public String nodeId() {
      return nodeID;
   }

   @Override
   public void push(TopKeyParam topKStats, Map<String, Long> topKMap) {
      this.topKMap.put(topKStats, topKMap);
   }

   @Override
   public Map<TopKeyParam, Map<String, Long>> getTopKMaps() {
      return topKMap;
   }

   @Override
   public Map<String, Long> getTopKMap(TopKeyParam param) {
      return topKMap.get(param);
   }

   @Override
   public TopKeyNodeBucket subTopKeyNodeBucket(TopKeyParam... params) {
      TopKeyNodeBucketImpl sub = new TopKeyNodeBucketImpl(this.nodeID);
      for (TopKeyParam t : params)
         sub.push(t, this.getTopKMap(t));
      return sub;
   }

   @Override
   public String toString() {
      return "TopKeyNodeBucketImpl{" +
            "nodeID='" + nodeID + '\'' +
            ", topKMap=" + topKMap.toString() +
            '}';
   }
}
