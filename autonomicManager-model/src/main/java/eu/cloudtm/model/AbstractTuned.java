package eu.cloudtm.model;

import eu.cloudtm.model.utils.Forecasters;
import eu.cloudtm.model.utils.TuningType;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/1/13
 */
public abstract class AbstractTuned {

    private TuningType tuning;  // self or manual
    private Forecasters forecaster;

    public AbstractTuned(){
        tuning = TuningType.SELF;
        forecaster = Forecasters.ANALYTICAL;
    }

    public TuningType getTuning(){ return tuning; }
    public void setTuning(TuningType value){ tuning = value; }

    public Forecasters getForecaster(){ return forecaster; }
    public void setForecaster(Forecasters value){ forecaster = value; }

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
