package eu.cloudtm;

import eu.cloudtm.commons.Forecaster;
import eu.cloudtm.commons.KPI;
import eu.cloudtm.commons.PlatformConfiguration;
import eu.cloudtm.commons.dto.WhatIfCustomParamDTO;
import eu.cloudtm.oracles.OracleService;
import eu.cloudtm.statistics.CustomSample;
import eu.cloudtm.statistics.EvaluatedParam;
import eu.cloudtm.statistics.Param;
import eu.cloudtm.statistics.ProcessedSample;
import eu.cloudtm.wpm.logService.remote.events.PublishAttribute;

import java.util.HashMap;
import java.util.Map;

/**
* Created with IntelliJ IDEA.
* User: fabio
* Date: 7/8/13
* Time: 1:33 PM
* To change this template use File | Settings | File Templates.
*/
public class WhatIf {

    private ProcessedSample processedSample;

    public WhatIf(ProcessedSample processedSample){
        this.processedSample = processedSample;

    }

    public WhatIfCustomParamDTO retrieveCurrentValues(){

        WhatIfCustomParamDTO customParam = new WhatIfCustomParamDTO();

        Double acf = processedSample.getEvaluatedParam(EvaluatedParam.ACF);
        customParam.setACF( acf  );
        customParam.setCommitBroadcastWallClockTime( (Double) processedSample.getParam(Param.AvgCommitAsync) );
        customParam.setRTT( (Double) processedSample.getParam( Param.AvgPrepareAsync ) );
        customParam.setPrepareCommandBytes( (Double) processedSample.getParam( Param.AvgPrepareCommandSize ) );
        customParam.setSuxNumPuts( (Double) processedSample.getParam( Param.AvgNumPutsBySuccessfulLocalTx ) );
        customParam.setRetryWritePercentage( (Double) processedSample.getParam( Param.PercentageSuccessWriteTransactions ) );
        customParam.setLocalUpdateTxLocalServiceTime( (Double) processedSample.getParam( Param.LocalUpdateTxLocalServiceTime ) );
        customParam.setLocalReadOnlyTxLocalServiceTime( (Double) processedSample.getParam( Param.LocalReadOnlyTxLocalServiceTime ) );
        return customParam;
    }


    public Map<Forecaster, Map<PlatformConfiguration, KPI>> evaluate(WhatIfCustomParamDTO customParamDTO){

        Map<Param, Double> customParam = new HashMap<Param, Double>();
        Map<EvaluatedParam, Double> customEvaluatedParam = new HashMap<EvaluatedParam, Double>();

        Map<Forecaster, Map<PlatformConfiguration, KPI>> result = new HashMap<Forecaster, Map<PlatformConfiguration, KPI>>();
        for(Forecaster forecaster : customParamDTO.getForecasters()){
            if(forecaster.equals(Forecaster.COMMITTEE)){
                throw new RuntimeException("Not yet implemented");
            } else {
                OracleService oracleService = OracleService.getInstance( forecaster.getOracleClass() );
                CustomSample customSample = new CustomSample(processedSample, customParam, customEvaluatedParam);
                result.put(forecaster, oracleService.whatIf(customSample));
            }
        }
        return result;
    }

    private Map<Param, Double> extractCustomParam(WhatIfCustomParamDTO whatIfCustomParam){
        Map<Param, Double> customParam = new HashMap<Param, Double>();
        customParam.put(Param.AvgCommitAsync, whatIfCustomParam.getCommitBroadcastWallClockTime());
        customParam.put(Param.AvgPrepareAsync, whatIfCustomParam.getRTT());
        customParam.put(Param.AvgPrepareCommandSize, whatIfCustomParam.getPrepareCommandBytes() );
        customParam.put(Param.AvgNumPutsBySuccessfulLocalTx, whatIfCustomParam.getSuxNumPuts() );
        customParam.put(Param.PercentageSuccessWriteTransactions, whatIfCustomParam.getRetryWritePercentage() );
        customParam.put(Param.LocalUpdateTxLocalServiceTime, whatIfCustomParam.getLocalUpdateTxLocalServiceTime() );
        customParam.put(Param.LocalReadOnlyTxLocalServiceTime, whatIfCustomParam.getLocalReadOnlyTxLocalServiceTime() );

        return customParam;
    }

    private Map<EvaluatedParam, Double> extractCustomEvaluatedParam(WhatIfCustomParamDTO whatIfCustomParam){
        Map<EvaluatedParam, Double> customParam = new HashMap<EvaluatedParam, Double>();
        customParam.put(EvaluatedParam.ACF, whatIfCustomParam.getACF());
        return customParam;
    }

}
