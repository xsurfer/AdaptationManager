package eu.cloudtm.controller.oracles;

import Tas2.core.ModelResult;
import Tas2.core.Tas2;
import Tas2.core.environment.DSTMScenarioTas2;
import Tas2.exception.Tas2Exception;
import eu.cloudtm.common.dto.WhatIfCustomParamDTO;
import eu.cloudtm.controller.Controller;
import eu.cloudtm.controller.model.KPI;
import eu.cloudtm.controller.model.PlatformConfiguration;
import eu.cloudtm.controller.model.utils.InstanceConfig;
import eu.cloudtm.controller.model.utils.ReplicationProtocol;
import eu.cloudtm.stats.Sample;
import eu.cloudtm.controller.oracles.common.DSTMScenarioFactory;
import eu.cloudtm.controller.oracles.common.PublishAttributeException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/12/13
 */
public class OracleTAS extends AbstractOracle {

    private static Log log = LogFactory.getLog(OracleTAS.class);

    public OracleTAS(Controller _controller) {
        super(_controller);
    }

    @Override
    public KPI forecast(Sample sample, int numNodes, int numThreads) {
        DSTMScenarioTas2 scenario;

        try {
            scenario = DSTMScenarioFactory.buildScenario(sample.getJmx(), sample.getMem(), numNodes, numThreads, Controller.TIME_WINDOW);
        } catch (PublishAttributeException e) {
            throw new RuntimeException(e);
        } catch (Tas2Exception e) {
            throw new RuntimeException(e);
        }

        return realForecast(scenario,numNodes, numThreads);
    }

    @Override
    public KPI forecastWithCustomParam(Sample sample, WhatIfCustomParamDTO customParam, int numNodes, int numThreads) {
        DSTMScenarioTas2 scenario = null;

        try {
            scenario = DSTMScenarioFactory.buildCustomScenario(sample.getJmx(),
                    sample.getMem(),
                    customParam,
                    numNodes,
                    numThreads,
                    Controller.TIME_WINDOW);

        } catch (PublishAttributeException e) {
            throw new RuntimeException(e);
        } catch (Tas2Exception e) {
            throw new RuntimeException(e);
        }

        return realForecast(scenario,numNodes, numThreads);
    }

    private KPI realForecast(DSTMScenarioTas2 scenario, int numNodes, int numThreads) {
        ModelResult result;
        try {
            log.info("calling tas");
            result = new Tas2().solve(scenario);
            log.info("called tas");
        } catch (Tas2Exception e) {
            throw new RuntimeException(e);
        }

        double throughput, abortP, rtt;
        throughput = result.getMetrics().getThroughput() * 1e9;
        rtt = result.getMetrics().getPrepareRtt();
        abortP = (1.0D - result.getProbabilities().getPrepareProbability() * result.getProbabilities().getCoherProbability());

        PlatformConfiguration config = new PlatformConfiguration(numNodes, numThreads,
                InstanceConfig.MEDIUM,
                ReplicationProtocol.TWOPC,
                2,
                false);

        KPI ret = new KPI(config, throughput, abortP, rtt);

        return ret;
    }

    @Override
    public String toString(){
        return "I'm OracleTAS";
    }

}
