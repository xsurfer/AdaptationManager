package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.commons.*;
import eu.cloudtm.autonomicManager.commons.dto.WhatIfCustomParamDTO;
import eu.cloudtm.autonomicManager.commons.dto.WhatIfDTO;
import eu.cloudtm.autonomicManager.optimizers.OptimizerType;
import eu.cloudtm.autonomicManager.statistics.ProcessedSample;
import eu.cloudtm.autonomicManager.statistics.StatsManager;

import java.util.List;
import java.util.Map;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 9/11/13
 */
public interface AutonomicManager {

    List<WhatIfDTO> whatIf(WhatIfCustomParamDTO customParamDTO );

    void updateProtocol(boolean tuning, ReplicationProtocol protocol);

    void updateDegree(boolean tuning, int degree);

    void updateScale(boolean tuning, int size, InstanceConfig instanceConfig);

    void updateForecaster(Forecaster forecaster);

    State state();

    PlatformConfiguration platformConfiguration();

    PlatformTuning platformTuning();

    PlatformConfiguration currentConfiguration();

    /*void reconfigureNow(ProcessedSample sample);*/

    void optimizeAndReconfigureNow();

    PlatformConfiguration forecast();

    void switchWorkloadAnalyzer();

    boolean isWorkloadAnalyzerEnabled();

    void customConfiguration(Map<OptimizerType, Object> configuration);

    StatsManager getStatsManager();

    void reconfigureNow(Map<OptimizerType, Object> configuration);

}
