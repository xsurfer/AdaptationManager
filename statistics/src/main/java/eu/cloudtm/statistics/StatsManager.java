package eu.cloudtm.statistics;


import eu.cloudtm.commons.dto.StatisticDTO;

import java.util.List;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/19/13
 */
public interface StatsManager {

    public void push(WPMProcessedSample sample);

    public WPMProcessedSample getLastSample();

    public List<WPMProcessedSample> getLastNSample(int n);

    public StatisticDTO getLastAvgStatistic(String param);

    public StatisticDTO getAllAvgStatistic(String param);

}
