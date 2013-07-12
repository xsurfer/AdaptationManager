package eu.cloudtm;

import eu.cloudtm.commons.ACF;
import eu.cloudtm.commons.dto.WhatIfCustomParamDTO;
import eu.cloudtm.statistics.EvaluatedParam;
import eu.cloudtm.statistics.WPMParam;
import eu.cloudtm.statistics.WPMProcessedSample;
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

    private WPMProcessedSample processedSample;

    public WhatIf(WPMProcessedSample processedSample){
        this.processedSample = processedSample;

    }

    public WhatIfCustomParamDTO retrieveCurrentValues(){

        Map<String, PublishAttribute<Double>> customMap = new HashMap<String, PublishAttribute<Double>>();

        Double acf = processedSample.getEvaluatedParam(EvaluatedParam.ACF);


        WhatIfCustomParamDTO customParam = new WhatIfCustomParamDTO();
        customParam.setACF( acf  );
        customParam.setCommitBroadcastWallClockTime( processedSample.getParam(WPMParam.CommitBroadcastWallClockTime) );
        customParam.setRTT( processedSample.getParam( WPMParam.RTT ) );
        customParam.setPrepareCommandBytes( processedSample.getParam( WPMParam.PrepareCommandBytes ) );
        customParam.setSuxNumPuts( processedSample.getParam( WPMParam.SuxNumPuts ) );
        customParam.setRetryWritePercentage( processedSample.getParam( WPMParam.RetryWritePercentage ) );
        customParam.setLocalUpdateTxLocalServiceTime( processedSample.getParam( WPMParam.LocalUpdateTxLocalServiceTime ) );
        customParam.setLocalReadOnlyTxLocalServiceTime( processedSample.getParam( WPMParam.LocalReadOnlyTxLocalServiceTime ) );
        return customParam;

    }


    public static void evaluate(WhatIfCustomParamDTO customParam){

//        WPMSample lastSample = StatsManager.getInstance().getLastSample();
//        Map<String, PublishAttribute<Double>> map = lastSample.getJmx();

//        CustomSample customSample = new CustomSample();

    }

}
