package eu.cloudtm.oracles;

import eu.cloudtm.Controller;
import eu.cloudtm.IOracle;
import eu.cloudtm.model.KPI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Constructor;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/14/13
 */
public abstract class AbstractOracle implements IOracle {

    private static Log log = LogFactory.getLog(AbstractOracle.class);

    private int imin = 2, imax = 10; // TODO: da rendere parametrizzabili

    private Controller controller;

    public AbstractOracle(){

    }

    public AbstractOracle(Controller _controller) {
        controller = _controller;
    }

    public static AbstractOracle getInstance(String oracleName, Controller _controller) {
        if (oracleName.indexOf('.') < 0) {
            oracleName = "eu.cloudtm.eu.cloudtm.oracles." + oracleName;
        }
        try {
            AbstractOracle obj;
            Constructor c = Class.forName(oracleName).getConstructor(Controller.class);
            obj = (AbstractOracle) c.newInstance(_controller);
            return obj;
        } catch (Exception e) {
            String s = "Could not create oracle of type: " + oracleName;
            log.error(s);
            throw new RuntimeException(e);
        }
    }

    private boolean evaluateKPI(KPI kpi,
                                double abortRateToGuarantee,
                                double responseTimeToGuarantee ) {

        if(kpi.getAbortProbability() < abortRateToGuarantee){
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
}


//    @Override
//    public Set<KPI> whatIf(InputOracle sample) throws OracleException {
//        Set<KPI> ret = new TreeSet<KPI>();
//
//        for(int i=imin;i<=imax;i++){
//            KPI kpi = forecast(sample,  i, controller.getCurrentConfiguration().threadPerNode() );
//            if(kpi != null)
//                ret.add(kpi);
//        }
//
//        return ret;
//    }



//    @Override
//    public KPI minimizeCosts(InputOracle sample,
//                             double arrivalRateToGuarantee,
//                             double abortRateToGuarantee,
//                             double responseTimeToGuarantee)
//            throws OracleException {
//
//        KPI kpi = binarySearch(sample, arrivalRateToGuarantee, abortRateToGuarantee, responseTimeToGuarantee);
//        return kpi;
//    }



//    private KPI binarySearch(WPMSample sample,
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
//            KPI kpi = forecast( sample, imid, controller.getCurrentConfiguration().threadPerNode() );
//            //log.info(kpi.getPlatformConfiguration().platformSize() + " throughput = " + kpi.getThroughput() * 1e9 + " txs/sec, rtt = " + kpi.getRtt() + " msec, abortProb = " + kpi.getAbortProbability());
//
//            throughputForecasted = kpi.getThroughput();
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
//                    log.info("Opt Config: throughput=" + kpi.getThroughput());
//                    return kpi;
//                }
//            }
//        }
//        if(subOptKPI != null) log.info("SubOpt Config: throughput=" + subOptKPI.getThroughput());
//        else log.info("NO SOLUTION");
//        return subOptKPI;
//    }
