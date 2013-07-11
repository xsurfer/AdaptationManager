//package eu.cloudtm;
//
//import eu.cloudtm.commons.ACF;
//import eu.cloudtm.commons.dto.WhatIfCustomParamDTO;
//import eu.cloudtm.wpm.logService.remote.events.PublishAttribute;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created with IntelliJ IDEA.
// * User: fabio
// * Date: 7/8/13
// * Time: 1:33 PM
// * To change this template use File | Settings | File Templates.
// */
//public class WhatIf {
//
//    private ProcessedSample processedSample;
//
//    public WhatIf(ProcessedSample processedSample){
//        this.processedSample = processedSample;
//
//    }
//
//    public static WhatIfCustomParamDTO retrieveCurrentValues(){
//
//        Map<String, PublishAttribute<Double>> customMap = new HashMap<String, PublishAttribute<Double>>();
//
//        Double acf = ACF.evaluate(lastSample.getJmx(), Controller.getInstance().getCurrentConfiguration().threadPerNode(), Controller.TIME_WINDOW);
//
//
//        WhatIfCustomParamDTO customParam = new WhatIfCustomParamDTO();
//        customParam.setACF( acf  );
//        customParam.setCommitBroadcastWallClockTime( lastSample.getAggregate(WPMParam.CommitBroadcastWallClockTime , 0 ) );
//        customParam.setRTT( lastSample.getAggregate( WPMParam.RTT, 0 ) );
//        customParam.setPrepareCommandBytes( lastSample.getAggregate( WPMParam.PrepareCommandBytes, 0 ) );
//        customParam.setSuxNumPuts( lastSample.getAggregate( WPMParam.SuxNumPuts, 0 ) );
//        customParam.setRetryWritePercentage( lastSample.getAggregate( WPMParam.RetryWritePercentage, 0 ) );
//        customParam.setLocalUpdateTxLocalServiceTime( lastSample.getAggregate( WPMParam.LocalUpdateTxLocalServiceTime, 0 ) );
//        customParam.setLocalReadOnlyTxLocalServiceTime( lastSample.getAggregate( WPMParam.LocalReadOnlyTxLocalServiceTime, 0 ) );
//        return customParam;
//
//    }
//
//
//    public static void evaluate(WhatIfCustomParamDTO customParam){
//
////        WPMSample lastSample = SampleDispatcher.getInstance().getLastSample();
////        Map<String, PublishAttribute<Double>> map = lastSample.getJmx();
//
////        CustomSample customSample = new CustomSample();
//
//
//
//    }
//
//}
