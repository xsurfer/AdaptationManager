package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.oracles.OracleService;
import eu.cloudtm.commons.EvaluatedParam;
import eu.cloudtm.commons.Forecaster;
import eu.cloudtm.commons.Param;
import eu.cloudtm.commons.PlatformConfiguration;
import eu.cloudtm.commons.dto.WhatIfCustomParamDTO;
import eu.cloudtm.commons.dto.WhatIfDTO;
import eu.cloudtm.oracles.OutputOracle;
import eu.cloudtm.statistics.CustomSample;
import eu.cloudtm.statistics.ProcessedSample;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
* Created with IntelliJ IDEA.
* User: fabio
* Date: 7/8/13
* Time: 1:33 PM
* To change this template use File | Settings | File Templates.
*/
public class WhatIf {

    private static Log log = LogFactory.getLog(WhatIf.class);

    private ProcessedSample processedSample;

    public WhatIf(ProcessedSample processedSample){
        this.processedSample = processedSample;

    }

    public WhatIfCustomParamDTO retrieveCurrentValues(){

        WhatIfCustomParamDTO customParam = new WhatIfCustomParamDTO();

        Double acf = null;
        Object acfObj = processedSample.getEvaluatedParam(EvaluatedParam.ACF);
        if(acfObj != null){
            acf = (Double) acfObj;
        }
        customParam.setACF( acf  );
        customParam.setAvgCommitAsync((Long) processedSample.getParam(Param.AvgCommitAsync));
        customParam.setAvgPrepareAsync((Long) processedSample.getParam(Param.AvgPrepareAsync));
        customParam.setAvgPrepareCommandSize((Long) processedSample.getParam(Param.AvgPrepareCommandSize));
        customParam.setAvgNumPutsBySuccessfulLocalTx((Double) processedSample.getParam(Param.AvgNumPutsBySuccessfulLocalTx));
        customParam.setPercentageSuccessWriteTransactions((Double) processedSample.getParam(Param.PercentageSuccessWriteTransactions));
        customParam.setLocalUpdateTxLocalServiceTime( (Long) processedSample.getParam( Param.LocalUpdateTxLocalServiceTime ) );
        customParam.setLocalReadOnlyTxLocalServiceTime( (Long) processedSample.getParam( Param.LocalReadOnlyTxLocalServiceTime ) );
        customParam.setAvgRemoteGetRtt( (Long) processedSample.getParam( Param.AvgRemoteGetRtt ) );
        customParam.setAvgGetsPerWrTransaction( (Long) processedSample.getParam( Param.AvgGetsPerWrTransaction ) );
        customParam.setAvgGetsPerROTransaction( (Long) processedSample.getParam( Param.AvgGetsPerROTransaction ) );
        return customParam;
    }


    public List<WhatIfDTO> evaluate(WhatIfCustomParamDTO customParamDTO){

        Map<Param, Object> customParam = extractCustomParam(customParamDTO);
        Map<EvaluatedParam, Object> customEvaluatedParam = extractCustomEvaluatedParam(customParamDTO);

        List<WhatIfDTO> result = new ArrayList<WhatIfDTO>();

        for(Forecaster forecaster : customParamDTO.getForecasters()){
            WhatIfDTO currWhatIfResult = new WhatIfDTO(forecaster);

            if(forecaster.equals(Forecaster.COMMITTEE)){
                throw new RuntimeException("Not yet implemented");
            } else {
                OracleService oracleService = OracleService.getInstance( forecaster.getOracleClass() );
                CustomSample customSample = new CustomSample(processedSample, customParam, customEvaluatedParam);
                TreeMap<PlatformConfiguration, OutputOracle> currForecast = new TreeMap<PlatformConfiguration, OutputOracle>();
                currForecast = oracleService.whatIf( customSample, customParamDTO.getReplicationProtocol(), customParamDTO.getReplicationDegree() );

                for (Map.Entry<PlatformConfiguration, OutputOracle> entry : currForecast.entrySet()){
                    long platformSize = entry.getKey().platformSize();
                    OutputOracle currOut = entry.getValue();

                    currWhatIfResult.addThroughputPoint( platformSize, currOut.throughput() );
                    log.warn("FIX TX CLASSES");
                    currWhatIfResult.addReadResponseTimePoint(platformSize, currOut.responseTime(0));
                    currWhatIfResult.addWriteResponseTimePoint(platformSize, currOut.responseTime(1));
                    currWhatIfResult.addAbortRatePoint(platformSize, currOut.abortRate());
                }

            }
            result.add(currWhatIfResult);
        }
        return result;
    }

    private Map<Param, Object> extractCustomParam(WhatIfCustomParamDTO whatIfCustomParam){
        Map<Param, Object> customParam = new HashMap<Param, Object>();
        if( whatIfCustomParam!=null ){
            customParam.put(Param.AvgCommitAsync, whatIfCustomParam.getAvgCommitAsync());
            customParam.put(Param.AvgPrepareAsync, whatIfCustomParam.getAvgPrepareAsync());
            customParam.put(Param.AvgPrepareCommandSize, whatIfCustomParam.getAvgPrepareCommandSize() );
            customParam.put(Param.AvgNumPutsBySuccessfulLocalTx, whatIfCustomParam.getAvgNumPutsBySuccessfulLocalTx() );
            customParam.put(Param.PercentageSuccessWriteTransactions, whatIfCustomParam.getPercentageSuccessWriteTransactions() );
            customParam.put(Param.LocalUpdateTxLocalServiceTime, whatIfCustomParam.getLocalUpdateTxLocalServiceTime() );
            customParam.put(Param.LocalReadOnlyTxLocalServiceTime, whatIfCustomParam.getLocalReadOnlyTxLocalServiceTime() );
        }
        return customParam;
    }

    private Map<EvaluatedParam, Object> extractCustomEvaluatedParam(WhatIfCustomParamDTO whatIfCustomParam){
        Map<EvaluatedParam, Object> customParam = new HashMap<EvaluatedParam, Object>();
        if( whatIfCustomParam!=null ){
            customParam.put(EvaluatedParam.ACF, whatIfCustomParam.getACF());
        }
        return customParam;
    }

}
