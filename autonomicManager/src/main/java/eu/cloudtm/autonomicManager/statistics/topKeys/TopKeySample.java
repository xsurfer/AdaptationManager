package eu.cloudtm.autonomicManager.statistics.topKeys;

import eu.cloudtm.autonomicManager.commons.TopKeyParam;

import java.util.Collection;

/**
 * // TODO: Document this
 *
 * @author diego
 * @since 4.0
 */
public interface TopKeySample {

   public void push(TopKeyNodeBucket nodeBucket);
   public TopKeyNodeBucket getNodeBucket(String node);
   public Collection<TopKeyNodeBucket> getNodeBuckets();
   public int numOfNodeBuckets();
   public TopKeySample subTopKeySample(TopKeyParam... params);

}
