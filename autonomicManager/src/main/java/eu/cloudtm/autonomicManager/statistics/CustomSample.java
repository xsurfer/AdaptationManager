package eu.cloudtm.autonomicManager.statistics;

import eu.cloudtm.autonomicManager.commons.EvaluatedParam;
import eu.cloudtm.autonomicManager.commons.Param;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/8/13
 * Time: 3:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomSample extends ProcessedSample {

    private static Log log = LogFactory.getLog(CustomSample.class);

    private ProcessedSample sample;
    private Map<Param, Object> customParam;
    private Map<EvaluatedParam, Object> customEvaluatedParam;

    public CustomSample(ProcessedSample sample, Map<Param, Object> customParam, Map<EvaluatedParam, Object> customEvaluatedParam ) {
        super(sample);
        this.sample = sample;
        this.customParam = customParam;
        this.customEvaluatedParam = customEvaluatedParam;
    }

    @Override
    public Object getParam(Param param) {
        Object retVal = customParam.get(param);
        if(retVal==null) {
            log.info("WhatIf: user didn't set " + param + ", using the one measured");
            retVal = sample.getParam(param);
        }
        return retVal;
    }

    @Override
    public Double getACF() {
        Object retVal;
        retVal = customEvaluatedParam.get(EvaluatedParam.ACF);

        if(retVal==null) {
            log.info("WhatIf: user didn't set ACF, using the one measured");
            Object retObject = sample.getEvaluatedParam(EvaluatedParam.ACF);
            if(retObject != null){
                retVal =  retObject;
            }
        }
        if(retVal!=null)
            return (Double) retVal;
        return null;
    }

}
