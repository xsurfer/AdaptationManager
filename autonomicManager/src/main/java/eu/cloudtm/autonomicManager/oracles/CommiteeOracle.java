package eu.cloudtm.autonomicManager.oracles;

import eu.cloudtm.autonomicManager.commons.Forecaster;
import eu.cloudtm.autonomicManager.oracles.exceptions.OracleException;

import java.util.List;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 9/10/13
 */
public class CommiteeOracle implements Oracle {

    private Oracle analytical = Forecaster.ANALYTICAL.getInstance();
    private Oracle simulator = Forecaster.SIMULATOR.getInstance();
    private Oracle machineLearning = Forecaster.MACHINE_LEARNING.getInstance();

    public CommiteeOracle(){

    }

    @Override
    public OutputOracle forecast(InputOracle input) throws OracleException {

        OutputOracle analyticalOut = analytical.forecast(input);
        OutputOracle simulatorOut = simulator.forecast(input);
        OutputOracle machineLearningOut = machineLearning.forecast(input);

        double throughputClass0 = (double) (analyticalOut.throughput(0) + simulatorOut.throughput(0) + machineLearningOut.throughput(0)) / 3.0;
        double throughputClass1 = (double) (analyticalOut.throughput(1) + simulatorOut.throughput(1) + machineLearningOut.throughput(1)) / 3.0;

        double abortProb0 = (double) (analyticalOut.abortRate(0) + simulatorOut.abortRate(0) + machineLearningOut.abortRate(0)) / 3.0;
        double abortProb1 = (double) (analyticalOut.abortRate(1) + simulatorOut.abortRate(1) + machineLearningOut.abortRate(1)) / 3.0;

        double respTime0 = (double) (analyticalOut.responseTime(0) + simulatorOut.responseTime(0) + machineLearningOut.responseTime(0)) / 3.0;
        double respTime1 = (double) (analyticalOut.responseTime(1) + simulatorOut.responseTime(1) + machineLearningOut.responseTime(1)) / 3.0;

        OutputOracle commiteeOut = new OutputOracleImpl(throughputClass0,
                throughputClass1,
                abortProb0,
                abortProb1,
                respTime0,
                respTime1);

        return commiteeOut;
    }
}
