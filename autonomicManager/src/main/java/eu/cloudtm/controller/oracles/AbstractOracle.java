package eu.cloudtm.controller.oracles;

import eu.cloudtm.common.dto.WhatIfCustomParamDTO;
import eu.cloudtm.controller.IOracle;
import eu.cloudtm.controller.model.KPI;
import eu.cloudtm.stats.Sample;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Constructor;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/14/13
 */
public abstract class AbstractOracle implements IOracle {

    private static Log log = LogFactory.getLog(AbstractOracle.class);

    private static final int THREAD = 2;


    public AbstractOracle() {

    }

    public static AbstractOracle getInstance(String oracleName) {
        if (oracleName.indexOf('.') < 0) {
            oracleName = "eu.cloudtm.controller.oracles." + oracleName;
        }
        try {
            AbstractOracle obj;
            Constructor c = Class.forName(oracleName).getConstructor();
            obj = (AbstractOracle) c.newInstance();
            return obj;
        } catch (Exception e) {
            String s = "Could not create oracle of type: " + oracleName;
            log.error(s);
            throw new RuntimeException(e);
        }
    }

    @Override
    public KPI minimizeCosts(Sample sample, double arrivalRate, double abortRate, double responseTime) {
        KPI kpi = binarySearch(sample, arrivalRate, abortRate, responseTime);

        return kpi;
    }

    private KPI binarySearch(Sample sample, double arrivalRate, double abortRate, double responseTime) {
        int imin = 2;
        int imax = 10;

        double throughputForecasted;

        KPI subOptKPI = null;
        while(imax>=imin){
            log.info("current metrics: " +
                    "[ PREDICT_arrivalRate:" + arrivalRate +"]" +
                    "[ SLA_abortRate:" + abortRate +"]" +
                    "[ SLA_responseTime:" + responseTime +"]" );

            int imid = (int) ( Math.floor(imin+imax)/2 );

            KPI kpi = forecast(sample,imid,THREAD);
            //log.info(kpi.getPlatformConfiguration().platformSize() + " throughput = " + kpi.getThroughput() * 1e9 + " txs/sec, rtt = " + kpi.getRtt() + " msec, abortProb = " + kpi.getAbortProbability());

            throughputForecasted = kpi.getThroughput();
            log.info("Throughput sopportabile con " + imid + ": " + throughputForecasted);

            if( throughputForecasted < arrivalRate ){
                imin = imid+1; // questa configurazione non mi da garanzie sufficienti
            } else if( throughputForecasted > arrivalRate ){
                imax = imid-1;
                if( evaluateKPI(kpi, abortRate, responseTime ) ){
                    if(subOptKPI!=null){
                        if( subOptKPI.getPlatformConfiguration().platformSize() > kpi.getPlatformConfiguration().platformSize() ){
                            subOptKPI = kpi;
                        }
                    } else {
                        subOptKPI = kpi;
                    }
                }
            } else {
                if( evaluateKPI(kpi, abortRate, responseTime ) ){
                    log.info("Opt Config: throughput=" + kpi.getThroughput());
                    return kpi;
                }
            }
        }
        if(subOptKPI != null) log.info("SubOpt Config: throughput=" + subOptKPI.getThroughput());
        else log.info("NO SOLUTION");
        return subOptKPI;
    }

    private boolean evaluateKPI(KPI kpi, double abortRate, double responseTime ) {
        if(kpi.getAbortProbability() > abortRate){
            return false;
        }

        log.warn("WARN WARN WARN WARN WARN WARN WARN WARN WARN WARN WARN WARN");
        log.warn("EDITO RESPONSE TIME");
        log.warn("WARN WARN WARN WARN WARN WARN WARN WARN WARN WARN WARN WARN");
        if(responseTime > responseTime){
            return false;
        }
        return true;
    }

    @Override
    public Set<KPI> whatIf(Sample sample, WhatIfCustomParamDTO customParam) {
        int imin = 2, imax = 10;
        Set<KPI> ret = new TreeSet<KPI>();

        for(int i=imin;i<=imax;i++){
            KPI kpi = forecastWithCustomParam(sample, customParam, i, THREAD);
            if(kpi != null)
                ret.add(kpi);
        }

        return ret;
    }


    @Override
    public KPI maximizeThroughput(Sample sample) {
        // TODO: logica di ricerca
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
