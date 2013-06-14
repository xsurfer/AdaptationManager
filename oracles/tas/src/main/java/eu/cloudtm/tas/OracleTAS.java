package eu.cloudtm.tas;

import Tas2.core.ModelResult;
import Tas2.core.Tas2;
import Tas2.core.environment.DSTMScenarioTas2;
import Tas2.exception.Tas2Exception;
import eu.cloudtm.controller.AbstractOracle;
import eu.cloudtm.controller.model.KPI;
import eu.cloudtm.controller.model.PlatformConfiguration;
import eu.cloudtm.controller.model.utils.InstanceConfig;
import eu.cloudtm.controller.model.utils.ReplicationProtocol;
import eu.cloudtm.stats.Sample;
import eu.cloudtm.tas.common.DSTMScenarioFactory;
import eu.cloudtm.tas.common.PublishAttributeException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/12/13
 */
public class OracleTAS extends AbstractOracle {

    private static Log log = LogFactory.getLog(OracleTAS.class);
    private DSTMScenarioFactory factory = new DSTMScenarioFactory();

    @Override
    public KPI forecast(Sample sample, int numNodes, int numThreads) {
        DSTMScenarioTas2 scenario;
        ModelResult result;

        try {
            scenario = factory.buildScenario(sample.getJmx(), sample.getMem());
            scenario.getWorkParams().setNumNodes( numNodes );
            scenario.getWorkParams().setThreadsPerNode( numThreads );

            result = new Tas2().solve(scenario);
        } catch (PublishAttributeException e) {
            throw new RuntimeException(e);
        } catch (Tas2Exception e) {
            throw new RuntimeException(e);
        }

        double throughput, abortP, rtt;
        throughput = result.getMetrics().getThroughput();
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

}
