package eu.cloudtm.oracles;

import eu.cloudtm.commons.*;
import eu.cloudtm.statistics.ProcessedSample;
import eu.cloudtm.oracles.exceptions.OracleException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/14/13
 */
public class OracleService {

    private static Log log = LogFactory.getLog(OracleService.class);

    private Oracle oracle;

    private int imin = 2, imax = 10; // TODO: da rendere parametrizzabili

    public OracleService(Oracle oracle){
        this.oracle = oracle;
    }

    public static OracleService getInstance(String oracleName) {
        if (oracleName.indexOf('.') < 0) {
            oracleName = "eu.cloudtm.oracles." + oracleName;
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

    private boolean evaluateKPI(KPI kpi,
                                double abortRateToGuarantee,
                                double responseTimeToGuarantee ) {

        if(kpi.abortRate() < abortRateToGuarantee){
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

                    KPI kpi = oracle.forecast( inputOracle );

                    if( kpi.throughput()>arrivalRateToGuarantee && kpi.abortRate() < abortRateToGuarantee ){
                        found = true;
                        finalNumNodes = numNodes;
                        finalRepDegree = repDegree;
                        finalRepProt = protocol;
                        break;
                    }
                }
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


//    private KPI binarySearch(ProcessedSample sample,
//                             double arrivalRateToGuarantee,
//                             double abortRateToGuarantee,
//                             double responseTimeToGuarantee)
//            throws OracleException {
//
//        double throughputForecasted;
//
//        KPI subOptKPI = null;
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
//            KPI kpi = forecast( inputOracle );
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

}
