package eu.cloudtm.autonomicManager.statistics;


import eu.cloudtm.autonomicManager.commons.dto.StatisticDTO;
import eu.cloudtm.autonomicManager.statistics.topKeys.TopKeySample;

import java.util.List;

/**
 * Created by: Fabio Perfetti E-mail: perfabio87@gmail.com Date: 6/19/13
 */
public interface StatsManager {

   public void push(ProcessedSample sample);

   public ProcessedSample getLastSample();

   public List<ProcessedSample> getLastNSample(int n);

   public StatisticDTO getLastAvgStatistic(String param);

   public StatisticDTO getAllAvgStatistic(String param);

   public void notifyListeners(ProcessedSample sample);

   public void pushTopKSample(TopKeySample sample);

   public TopKeySample getLastTopKSample();
}
