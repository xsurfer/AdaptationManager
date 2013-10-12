package eu.cloudtm.autonomicManager.statistics.topKeys;

import eu.cloudtm.autonomicManager.commons.TopKeyParam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * // TODO: Document this
 *
 * @author diego
 * @since 4.0
 */
public class TopKeySampleImpl implements TopKeySample {
   private List<TopKeyNodeBucket> nodeMap = new ArrayList<TopKeyNodeBucket>();

   @Override
   public void push(TopKeyNodeBucket nodeBucket) {
      nodeMap.add(nodeBucket);
   }

   @Override
   //Not optimal but I don't want to keep an hashmap yet
   public TopKeyNodeBucket getNodeBucket(String node) {
      for (TopKeyNodeBucket t : nodeMap)
         if (t.nodeId().equals(node))
            return t;
      return null;
   }

   @Override
   public Collection<TopKeyNodeBucket> getNodeBuckets() {
      return nodeMap;
   }

   @Override
   public int numOfNodeBuckets() {
      return nodeMap.size();
   }

   public TopKeySample subTopKeySample(TopKeyParam... params) {
      TopKeySample toRet = new TopKeySampleImpl();
      for (TopKeyNodeBucket t : nodeMap)
         toRet.push(t.subTopKeyNodeBucket(params));
      return toRet;
   }

   @Override
   public String toString() {
      return "TopKeySampleImpl{" +
            "nodeMap=" + nodeMap +
            '}';
   }
}
