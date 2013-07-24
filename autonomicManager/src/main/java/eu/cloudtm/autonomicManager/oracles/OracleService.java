package eu.cloudtm.autonomicManager.oracles;

import eu.cloudtm.commons.*;
import eu.cloudtm.commons.ForecastParam;
import eu.cloudtm.oracles.InputOracle;
import eu.cloudtm.oracles.Oracle;
import eu.cloudtm.oracles.OutputOracle;
import eu.cloudtm.oracles.exceptions.OracleException;
import eu.cloudtm.statistics.ProcessedSample;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/14/13
 */
public class OracleService implements IOracleService {

    private static Log log = LogFactory.getLog(OracleService.class);

    private Oracle oracle;

    private int imin = 2, imax = 10; // TODO: da rendere parametrizzabili

    public OracleService(Oracle oracle){
        this.oracle = oracle;
    }

    public static OracleService getInstance(String oracleName) {
        if (oracleName.indexOf('.') < 0) {
            oracleName = "eu.cloudtm.autonomicManager.oracles." + oracleName;
        }
        try {
            Oracle obj;
            Constructor c = Class.forName(oracleName).getConstructor();
            obj = (Oracle) c.newInstance();
            OracleService oracleService = new OracleService(obj);
            return oracleService;
        } catch (Exception e) {
            String s = "Could not create oracle of type: " + oracleName;
            log.error(s);
            throw new RuntimeException(e);
        }
    }

    private boolean evaluateKPI(OutputOracle outputOracle,
                                double abortRateToGuarantee,
                                double responseTimeToGuarantee ) {

        if(outputOracle.abortRate() < abortRateToGuarantee){
            return false;
        }

        log.warn("WARN WARN WARN WARN WARN WARN WARN WARN WARN WARN WARN WARN");
        log.warn("EDITO RESPONSE TIME");
        log.warn("WARN WARN WARN WARN WARN WARN WARN WARN WARN WARN WARN WARN");
        if(responseTimeToGuarantee < responseTimeToGuarantee){
            return false;
        }
        return true;
    }


    public PlatformConfiguration minimizeCosts(ProcessedSample sample,
                             double arrivalRateToGuarantee,
                             double abortRateToGuarantee,
                             double responseTimeToGuarantee)
            throws OracleException {

        PlatformConfiguration configuration = exploreAllCases(sample, arrivalRateToGuarantee, abortRateToGuarantee, responseTimeToGuarantee);
        return configuration;
    }

    @Override
    public TreeMap<PlatformConfiguration, OutputOracle> whatIf(ProcessedSample sample, ReplicationProtocol repProtocol, int repDegree) {

        TreeMap<PlatformConfiguration, OutputOracle> result = new TreeMap<PlatformConfiguration, OutputOracle>();
        if(repDegree <= 0)
            throw new IllegalArgumentException("repDegree must be > 0");

        int currRepDegree;

        for( int numNodes = imin; numNodes<=imax; numNodes++){
            if(numNodes < repDegree){
                currRepDegree = numNodes;
            } else {
                currRepDegree = repDegree;
            }

            PlatformConfiguration currConf = new PlatformConfiguration(numNodes, currRepDegree, repProtocol);

            Map<ForecastParam, Object> forecastParam = new HashMap<ForecastParam, Object>();
            forecastParam.put(ForecastParam.NumNodes, numNodes );
            forecastParam.put(ForecastParam.ReplicationDegree, currRepDegree );
            forecastParam.put(ForecastParam.ReplicationProtocol, repProtocol );

            InputOracle inputOracle = new InputOracleWPM(sample, forecastParam);
            OutputOracle currOutputOracle = null;
            try {
                currOutputOracle = oracle.forecast(inputOracle);
            } catch (OracleException e) {
                log.warn("Oracle exception during what if analysis...skipping that configuration");
            }
            result.put(currConf, currOutputOracle);
        }
        return result;
    }

    @Override
    public PlatformConfiguration maximizeThroughput(ProcessedSample sample) throws OracleException {

        int finalNumNodes = 0;
        int finalRepDegree = 0;
        ReplicationProtocol finalRepProt = null;
        boolean found = false;
        double maxThroughput = 0;

        int numNodes=imin;

        while( numNodes<=imax ){
            int repDegree=imin;
            while( repDegree<=numNodes ){
                for(ReplicationProtocol protocol : ReplicationProtocol.values()){

                    Map<ForecastParam, Object> forecastParams = new HashMap<ForecastParam, Object>();
                    forecastParams.put(ForecastParam.ReplicationProtocol, protocol);
                    forecastParams.put(ForecastParam.ReplicationDegree, repDegree);
                    forecastParams.put(ForecastParam.NumNodes, numNodes);

                    InputOracleWPM inputOracle = new InputOracleWPM(sample, forecastParams);

//                    ControllerLogger.log.info("Forecasting with: " +
//                            "nodes "      + numNodes + ", " +
//                            "repDegree "    + repDegree + ", " +
//                            "repProt "      + protocol
//                    );
                    OutputOracle outputOracle = oracle.forecast( inputOracle );


                    if( outputOracle.throughput() > maxThroughput ){
                        finalNumNodes = numNodes;
                        finalRepDegree = repDegree;
                        finalRepProt = protocol;
                    }
                }
                repDegree++;
            }
            numNodes++;
        }

        PlatformConfiguration configuration = null;

        configuration = new PlatformConfiguration();
        configuration.setPlatformScale(finalNumNodes, InstanceConfig.MEDIUM);
        configuration.setRepDegree(finalRepDegree);
        configuration.setRepProtocol(finalRepProt);

        return configuration;

    }




    private PlatformConfiguration exploreAllCases(ProcessedSample sample,
                                double arrivalRateToGuarantee,
                                double abortRateToGuarantee,
                                double responseTimeToGuarantee) throws OracleException{

        int finalNumNodes = 0;
        int finalRepDegree = 0;
        ReplicationProtocol finalRepProt = null;
        boolean found = false;

        int numNodes=imin;

        while(numNodes<=imax && !found){
            int repDegree=imin;
            while(repDegree<=numNodes && !found){
                for(ReplicationProtocol protocol : ReplicationProtocol.values()){

                    Map<ForecastParam, Object> forecastParams = new HashMap<ForecastParam, Object>();
                    forecastParams.put(ForecastParam.ReplicationProtocol, protocol);
                    forecastParams.put(ForecastParam.ReplicationDegree, repDegree);
                    forecastParams.put(ForecastParam.NumNodes, numNodes);

                    InputOracleWPM inputOracle = new InputOracleWPM(sample, forecastParams);

//                    ControllerLogger.log.info("Forecasting with: " +
//                            "nodes "      + numNodes + ", " +
//                            "repDegree "    + repDegree + ", " +
//                            "repProt "      + protocol
//                    );
                    OutputOracle outputOracle = oracle.forecast( inputOracle );

                    if( outputOracle.throughput() >= arrivalRateToGuarantee && outputOracle.abortRate() <= abortRateToGuarantee ){
                        found = true;
                        finalNumNodes = numNodes;
                        finalRepDegree = repDegree;
                        finalRepProt = protocol;
                        break;
                    }
                }
                repDegree++;
            }
            numNodes++;
        }

        PlatformConfiguration configuration = null;
        if(found){
            configuration = new PlatformConfiguration();
            configuration.setPlatformScale(finalNumNodes, InstanceConfig.MEDIUM);
            configuration.setRepDegree(finalRepDegree);
            configuration.setRepProtocol(finalRepProt);
        }
        return configuration;
    }

}


//    private OutputOracle binarySearch(AbstractProcessedSample sample,
//                             double arrivalRateToGuarantee,
//                             double abortRateToGuarantee,
//                             double responseTimeToGuarantee)
//            throws OracleException {
//
//        double throughputForecasted;
//
//        OutputOracle subOptKPI = null;
//        while(imax>=imin){
//            log.info("current metrics: " +
//                    "[ arrivalRateToGuarantee:" + arrivalRateToGuarantee +"]" +
//                    "[ SLA_abortRate:" + abortRateToGuarantee +"]" +
//                    "[ SLA_responseTime:" + responseTimeToGuarantee +"]" );
//
//            int imid = (int) ( Math.floor(imin+imax)/2 );
//
//
//            InputOracleWPM inputOracle = new InputOracleWPM(sample, null);
//            // TODO CARICARE l'inputORACLE
//
//            OutputOracle kpi = forecast( inputOracle );
//            //log.info(kpi.getPlatformConfiguration().platformSize() + " throughput = " + kpi.getThroughput() * 1e9 + " txs/sec, rtt = " + kpi.getRtt() + " msec, abortProb = " + kpi.getAbortProbability());
//
//            throughputForecasted = kpi.throughput();
//            log.info("Throughput sopportabile con " + imid + ": " + throughputForecasted);
//
//            if( throughputForecasted < arrivalRateToGuarantee ){
//                imin = imid+1; // questa configurazione non mi da garanzie sufficienti
//            } else if( throughputForecasted > arrivalRateToGuarantee ){
//                imax = imid-1;
//                if( evaluateKPI(kpi, abortRateToGuarantee, responseTimeToGuarantee ) ){
//                    if(subOptKPI!=null){
//                        if( subOptKPI.getPlatformConfiguration().platformSize() > kpi.getPlatformConfiguration().platformSize() ){
//                            subOptKPI = kpi;
//                        }
//                    } else {
//                        subOptKPI = kpi;
//                    }
//                }
//            } else {
//                if( evaluateKPI(kpi, abortRateToGuarantee, responseTimeToGuarantee ) ){
//                    log.info("Opt Config: throughput=" + kpi.throughput());
//                    return kpi;
//                }
//            }
//        }
//        if(subOptKPI != null) log.info("SubOpt Config: throughput=" + subOptKPI.throughput());
//        else log.info("NO SOLUTION");
//        return subOptKPI;
//    }