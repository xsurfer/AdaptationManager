package eu.cloudtm.statistics;

import eu.cloudtm.commons.EvaluatedParam;
import eu.cloudtm.commons.Param;
import eu.cloudtm.commons.SystemType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/8/13
 * Time: 5:31 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ProcessedSample implements Sample {

    private static final int CORE_PER_CPU = 8;
    private static final SystemType DEFAULT_SYSTEM_TYPE = SystemType.MULE;

    protected Sample sample;

    private AtomicBoolean initialized = new AtomicBoolean(false);

    private Map<EvaluatedParam, Object> evaluatedParams = new HashMap<EvaluatedParam, Object>();

    public ProcessedSample(Sample sample){
        this.sample = sample;
    }

    @Override
    public long getId() {
        return sample.getId();
    }

    private void init(){
        // push here all the customizations
        evaluatedParams.put(EvaluatedParam.ACF, getACF());
        evaluatedParams.put(EvaluatedParam.CORE_PER_CPU, getCoreCPU());

    }

    @Override
    public Object getParam(Param param) {
        return sample.getParam(param);
    }


    public Object getEvaluatedParam(EvaluatedParam param) {
        if(!initialized.compareAndSet(false, true)){
            init();
        }
        Object ret = evaluatedParams.get(param);
        if(ret != null)
            return ret;
        else
            throw new IllegalArgumentException("param " + param + " is not present" );
    }

    protected abstract Double getACF();

    protected final int getCoreCPU(){
        return CORE_PER_CPU;
    }

    protected final SystemType getSystemType(){
        return DEFAULT_SYSTEM_TYPE;
    }

}
