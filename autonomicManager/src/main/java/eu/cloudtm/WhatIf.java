package eu.cloudtm;

import eu.cloudtm.common.dto.WhatIfCustomParamDTO;
import eu.cloudtm.model.ACF;
import eu.cloudtm.stats.CustomSample;
import eu.cloudtm.stats.WPMParam;
import eu.cloudtm.stats.WPMSample;
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


    public static WhatIfCustomParamDTO retrieveCurrentValues(){

        WPMSample lastSample = StatsManager.getInstance().getLastSample();

        Map<String, PublishAttribute<Double>> customMap = new HashMap<String, PublishAttribute<Double>>();

        Double acf = ACF.evaluate(lastSample.getJmx(), Controller.getInstance().getCurrentConfiguration().threadPerNode(), Controller.TIME_WINDOW);


        WhatIfCustomParamDTO customParam = new WhatIfCustomParamDTO();
        customParam.setACF( acf  );
        customParam.setCommitBroadcastWallClockTime( lastSample.getAggregate(WPMParam.CommitBroadcastWallClockTime , 0 ) );
        customParam.setRTT( lastSample.getAggregate( WPMParam.RTT, 0 ) );
        customParam.setPrepareCommandBytes( lastSample.getAggregate( WPMParam.PrepareCommandBytes, 0 ) );
        customParam.setSuxNumPuts( lastSample.getAggregate( WPMParam.SuxNumPuts, 0 ) );
        customParam.setRetryWritePercentage( lastSample.getAggregate( WPMParam.RetryWritePercentage, 0 ) );
        customParam.setLocalUpdateTxLocalServiceTime( lastSample.getAggregate( WPMParam.LocalUpdateTxLocalServiceTime, 0 ) );
        customParam.setLocalReadOnlyTxLocalServiceTime( lastSample.getAggregate( WPMParam.LocalReadOnlyTxLocalServiceTime, 0 ) );
        return customParam;

    }


    public static void evaluate(WhatIfCustomParamDTO customParam){

//        WPMSample lastSample = StatsManager.getInstance().getLastSample();
//        Map<String, PublishAttribute<Double>> map = lastSample.getJmx();

//        CustomSample customSample = new CustomSample();



    }

}
