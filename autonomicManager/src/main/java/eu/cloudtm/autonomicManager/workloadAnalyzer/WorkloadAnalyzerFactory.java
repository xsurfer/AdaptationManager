package eu.cloudtm.autonomicManager.workloadAnalyzer;

import eu.cloudtm.autonomicManager.Reconfigurator;
import eu.cloudtm.commons.EvaluatedParam;
import eu.cloudtm.commons.Param;
import eu.cloudtm.statistics.SampleProducer;
import eu.cloudtm.statistics.StatsManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/19/13
 * Time: 1:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorkloadAnalyzerFactory {

    SampleProducer statsManager;
    Reconfigurator reconfigurator;

    Map<Param, Double> param2delta = new HashMap<Param, Double>(){{
        put(Param.AvgNumPutsBySuccessfulLocalTx, 20D);
        put(Param.PercentageSuccessWriteTransactions, 20D);
        put(Param.LocalUpdateTxLocalServiceTime, 20D);
        put(Param.LocalReadOnlyTxLocalServiceTime, 20D);
        put(Param.AvgGetsPerWrTransaction, 20D);
        put(Param.AvgGetsPerROTransaction, 20D);
    }};

    Map<EvaluatedParam, Double> evaluatedParam2delta = new HashMap<EvaluatedParam, Double>(){{
        put(EvaluatedParam.ACF, 20D);
    }};

    public WorkloadAnalyzerFactory(SampleProducer statsManager,
                                   Reconfigurator reconfigurator){

    }

    public WorkloadAnalyzer build(){

        AlertManager alertManager = new AlertManager();

        WorkloadForecaster workloadForecaster = new WorkloadForecaster(
                statsManager,
                param2delta.keySet(),
                evaluatedParam2delta.keySet()
        );

        ChangeDetector proactiveChangeDetector = new ProactiveChangeDetector(
                workloadForecaster,
                alertManager,
                reconfigurator,
                param2delta,
                evaluatedParam2delta);

        ChangeDetector reactiveChangeDetector = new ReactiveChangeDetector(workloadForecaster,
                alertManager,
                reconfigurator,
                param2delta,
                evaluatedParam2delta);


        WorkloadAnalyzer workloadAnalyzer = new WorkloadAnalyzer(reactiveChangeDetector,
                proactiveChangeDetector,
                alertManager);

        return workloadAnalyzer;
    }

}
