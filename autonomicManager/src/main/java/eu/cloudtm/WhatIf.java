package eu.cloudtm;

import eu.cloudtm.commons.dto.WhatIfCustomParamDTO;
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

        Map<String, PublishAttribute<Double>> customMap = new HashMap<String, PublishAttribute<Double>>();

        Double acf = processedSample.getEvaluatedParam(EvaluatedParam.ACF);


        WhatIfCustomParamDTO customParam = new WhatIfCustomParamDTO();
        customParam.setACF( acf  );
        customParam.setCommitBroadcastWallClockTime( (Double) processedSample.getParam(Param.CommitBroadcastWallClockTime) );
        customParam.setRTT( (Double) processedSample.getParam( Param.RTT ) );
        customParam.setPrepareCommandBytes( (Double) processedSample.getParam( Param.PrepareCommandBytes ) );
        customParam.setSuxNumPuts( (Double) processedSample.getParam( Param.SuxNumPuts ) );
        customParam.setRetryWritePercentage( (Double) processedSample.getParam( Param.RetryWritePercentage ) );
        customParam.setLocalUpdateTxLocalServiceTime( (Double) processedSample.getParam( Param.LocalUpdateTxLocalServiceTime ) );
        customParam.setLocalReadOnlyTxLocalServiceTime( (Double) processedSample.getParam( Param.LocalReadOnlyTxLocalServiceTime ) );
        return customParam;

    }


    public static void evaluate(WhatIfCustomParamDTO customParam){

//        WPMSample lastSample = StatsManager.getInstance().getLastSample();
//        Map<String, PublishAttribute<Double>> map = lastSample.getJmx();

//        CustomSample customSample = new CustomSample();

    }

}
