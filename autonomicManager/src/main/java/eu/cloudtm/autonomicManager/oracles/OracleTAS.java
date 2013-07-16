package eu.cloudtm.autonomicManager.oracles;

import eu.cloudtm.commons.*;
import eu.cloudtm.oracles.InputOracle;
import eu.cloudtm.oracles.Oracle;
import eu.cloudtm.oracles.exceptions.OracleException;
import eu.cloudtm.commons.Param;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/12/13
 */
public class OracleTAS implements Oracle {

    private static Log log = LogFactory.getLog(OracleTAS.class);

    @Override
    public OutputOracle forecast(InputOracle inputOracle) throws OracleException {
        double throughput = (Double) inputOracle.getParam(Param.Throughput);

        if(throughput >= 0 && throughput < 100){
            return new KPIimpl(150, 0, 0);
        } else if( throughput >= 100 && throughput < 400 ) {
            return new KPIimpl(600, 0, 0);
        } else {
            return new KPIimpl(1200, 0 , 0);
        }
    }
}



//
//    public OracleTAS(){
//
//    }
//
//    public OracleTAS(ControllerOld _controller) {
//        super(_controller);
//    }
//
//    @Override
//    public KPIimpl forecast(ProcessedSample input) throws OracleException {
//        DSTMScenarioTas2 scenario;
//
//        try {
//            scenario = DSTMScenarioFactory.buildScenario(input, ControllerOld.TIME_WINDOW);
//        } catch (PublishAttributeException e) {
//            throw new RuntimeException(e);
//        } catch (Tas2Exception e) {
//            throw new OracleException(e);
//        }
//
//        try {
//            return realForecast(scenario, numNodes, numThreads);
//        } catch (Tas2Exception e) {
//            throw new OracleException(e);
//        }
//    }

//    @Override
//    public KPIimpl forecastWithCustomParam(WPMSample sample, WhatIfCustomParamDTO customParam, int numNodes, int numThreads) throws OracleException {
//        DSTMScenarioTas2 scenario = null;
//
//        try {
//            scenario = DSTMScenarioFactory.buildCustomScenario(sample.getJmx(),
//                    sample.getMem(),
//                    customParam,
//                    numNodes,
//                    numThreads,
//                    ControllerOld.TIME_WINDOW);
//
//        } catch (PublishAttributeException e) {
//            throw new RuntimeException(e);
//        } catch (Tas2Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        try {
//            return realForecast(scenario,numNodes, numThreads);
//        } catch (Tas2Exception e) {
//            throw new OracleException(e);
//        }
//    }

//    private KPIimpl realForecast(DSTMScenarioTas2 scenario, int numNodes, int numThreads) throws Tas2Exception {
//        ModelResult result;
//        try {
//            log.info("calling tas");
//            result = new Tas2().solve(scenario);
//            log.info("called tas");
//        } catch (Tas2Exception e) {
//            throw e;
//        }
//
//        double throughput, abortP, rtt;
//        throughput = result.getMetrics().getThroughput() * 1e9;
//        rtt = result.getMetrics().getPrepareRtt();
//        abortP = (1.0D - result.getProbabilities().getPrepareProbability() * result.getProbabilities().getCoherProbability());
//
//        PlatformConfiguration config = new PlatformConfiguration(numNodes, numThreads,
//                InstanceConfig.MEDIUM,
//                ReplicationProtocol.TWOPC,
//                2,
//                false);
//
//        KPIimpl ret = new KPIimpl(config, throughput, abortP, rtt);
//
//        return ret;
//    }
//
//    @Override
//    public String toString(){
//        return "OracleTAS";
//    }
