package eu.cloudtm.controller.model;

import eu.cloudtm.controller.model.utils.Forecaster;
import eu.cloudtm.controller.model.utils.TuningType;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/1/13
 */
public abstract class AbstractTuned  {

    private TuningType tuning;  // self or manual
    private Forecaster forecaster;

    protected AbstractTuned(){}

    public AbstractTuned(TuningType _tuning,Forecaster _forecaster){
        tuning = _tuning;
        forecaster = _forecaster;
    }

    public TuningType getTuning(){ return tuning; }
    public void setTuning(TuningType value){ tuning = value; }

    public Forecaster getForecaster(){ return forecaster; }
    public void setForecaster(Forecaster value){ forecaster = value; }

    public String printTuning(){
        String ret;
        if(tuning.equals(TuningType.SELF)){
            ret = tuning + " + " + forecaster;
        } else {
            ret = tuning.toString();
        }
        return ret;
    }

}
