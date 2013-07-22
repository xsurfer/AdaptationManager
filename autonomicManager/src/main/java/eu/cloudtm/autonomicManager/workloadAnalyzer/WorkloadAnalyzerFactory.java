package eu.cloudtm.autonomicManager.workloadAnalyzer;

import eu.cloudtm.autonomicManager.Optimizer;
import eu.cloudtm.autonomicManager.Reconfigurator;
import eu.cloudtm.autonomicManager.configs.AlertManagerConfig;
import eu.cloudtm.commons.EvaluatedParam;
import eu.cloudtm.commons.Param;
import eu.cloudtm.statistics.SampleProducer;
import eu.cloudtm.statistics.StatsManager;
import org.apache.commons.configuration.Configuration;

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


    private Configuration config;
    private SampleProducer statsManager;
    private Reconfigurator reconfigurator;
    private Optimizer optimizer;

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

    public WorkloadAnalyzerFactory( Configuration config,
                                    SampleProducer statsManager,
                                    Reconfigurator reconfigurator,
                                    Optimizer optimizer){
        this.config = config;
        this.statsManager = statsManager;
        this.reconfigurator = reconfigurator;
        this.optimizer = optimizer;

    }

    public WorkloadAnalyzer build(){

        WorkloadForecaster workloadForecaster = new WorkloadForecaster(
                param2delta.keySet(),
                evaluatedParam2delta.keySet()
        );

        ChangeDetector proactiveChangeDetector = new ProactiveChangeDetector(
                statsManager,
                param2delta,
                evaluatedParam2delta,
                workloadForecaster
        );

        ChangeDetector reactiveChangeDetector = new ReactiveChangeDetector(
                statsManager,
                param2delta,
                evaluatedParam2delta
        );


        String policy = config.getString( AlertManagerConfig.POLICY.key() );
        AlertManager alertManager = AlertManager.createInstance( policy, optimizer, reconfigurator );

        proactiveChangeDetector.addEventListener(alertManager);
        reactiveChangeDetector.addEventListener(alertManager);

        WorkloadAnalyzer workloadAnalyzer = new WorkloadAnalyzer(reactiveChangeDetector,
                proactiveChangeDetector,
                alertManager);

        return workloadAnalyzer;
    }


}
