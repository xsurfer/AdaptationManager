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
public interface TopKeyNodeBucket {
   public void push(TopKeyParam topKStats,Map<String,Long> topKMap);
   public Map<TopKeyParam,Map<String,Long>> getTopKMaps();
   public Map<String,Long> getTopKMap(TopKeyParam param);
   public String nodeId();
   public TopKeyNodeBucket subTopKeyNodeBucket(TopKeyParam... params);
}
