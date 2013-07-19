package eu.cloudtm.autonomicManager.workloadAnalyzer;

import eu.cloudtm.commons.EvaluatedParam;
import eu.cloudtm.commons.Param;
import eu.cloudtm.statistics.CustomSample;
import eu.cloudtm.statistics.ProcessedSample;
import eu.cloudtm.statistics.SampleListener;
import eu.cloudtm.statistics.SampleProducer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/19/13
 * Time: 11:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class WorkloadForecaster extends SampleProducer implements SampleListener {

    private static final int POLYNOMIAL_GRADE = 1;

    public static final int TIME_WINDOW = 5; // in seconds; it means that each 5 seconds I have a new sample
    public static final int RECONFIGURATION_TIME = 5*60; // in seconds, to divide for the TIME_WINDOW
    public static final int SAFE_TIME = 2*60; // in seconds, to divide for the TIME_WINDOW

    private Set<Param> monitoredParams;
    private Map<Param, PolynomialFitter> paramForecasters = new HashMap<Param, PolynomialFitter>();

    private Set<EvaluatedParam> monitoredEvaluatedParam;
    private Map<EvaluatedParam, PolynomialFitter> evaluatedParamForecasters = new HashMap<EvaluatedParam, PolynomialFitter>();

    public WorkloadForecaster(SampleProducer statsManager,
                              Set<Param> monitoredParams,
                              Set<EvaluatedParam> monitoredEvaluatedParam){
        this.monitoredParams = monitoredParams;
        this.monitoredEvaluatedParam = monitoredEvaluatedParam;
        statsManager.addListener(this);
        init();
    }

    private void init(){
        for(Param param : monitoredParams){
            PolynomialFitter forecaster = new PolynomialFitter(POLYNOMIAL_GRADE);
            paramForecasters.put(param, forecaster);
        }
        for(EvaluatedParam param : monitoredEvaluatedParam){
            PolynomialFitter forecaster = new PolynomialFitter(POLYNOMIAL_GRADE);
            evaluatedParamForecasters.put(param, forecaster);
        }
    }

    @Override
    public void onNewSample(ProcessedSample sample) {

        for(Param param : monitoredParams){
            PolynomialFitter forecaster = paramForecasters.get(param);
            forecaster.addPoint(sample.getId(), (Double) sample.getParam(param));
        }
        for(EvaluatedParam param : monitoredEvaluatedParam){
            PolynomialFitter forecaster = evaluatedParamForecasters.get(param);
            forecaster.addPoint(sample.getId(), (Double) sample.getEvaluatedParam(param));
        }

        ProcessedSample forecastedSample = new CustomSample(sample, createCustomMap(sample.getId()), createCustomEvaluatedMap(sample.getId()) );
        notify(forecastedSample);
    }

    private Map<Param, Object> createCustomMap(long instance){
        Map<Param, Object> customMap = new HashMap<Param, Object>();

        for(Param param : monitoredParams){
            PolynomialFitter forecaster = paramForecasters.get(param);
            customMap.put(param, forecaster.getBestFit().getY( timeToForecast(instance) ) );
        }

        return customMap;
    }

    private Map<EvaluatedParam, Object> createCustomEvaluatedMap(long instance){
        Map<EvaluatedParam, Object> customEvaluatedMap = new HashMap<EvaluatedParam, Object>();

        for(EvaluatedParam param : monitoredEvaluatedParam){
            PolynomialFitter forecaster = evaluatedParamForecasters.get(param);
            customEvaluatedMap.put(param, forecaster.getBestFit().getY( timeToForecast(instance) ) );
        }

        return customEvaluatedMap;
    }

    private double timeToForecast(long instance){
        return  ((double) instance + ((double) (RECONFIGURATION_TIME + SAFE_TIME) / TIME_WINDOW));
    }

}
