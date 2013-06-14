package eu.cloudtm.controller.model;

import eu.cloudtm.controller.model.utils.Forecaster;
import eu.cloudtm.controller.model.utils.TuningState;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/14/13
 */
public class Tuning {

    private TuningState tuning;
    private Forecaster forecaster;

    public Tuning(){
        tuning = TuningState.SELF;
        forecaster = Forecaster.ANALYTICAL;
    }

    public Tuning(Forecaster forecaster){
        set(forecaster);
    }

    public TuningState getState(){
        return tuning;
    }

    public Forecaster getForecaster(){
        return forecaster;
    }

    public void set(Forecaster _forecaster){
        if( _forecaster==null || forecaster==Forecaster.NONE ){
            tuning = TuningState.MANUAL;
            forecaster = Forecaster.NONE;
        } else {
            forecaster = _forecaster;
            tuning = TuningState.SELF;
        }
    }

    @Override
    public boolean equals(Object obj) {
        Tuning o = (Tuning) obj;
        if(getState().equals(o.getState())){
            if(getForecaster().equals(o.getForecaster()))
                return true;
        }
        return false;
    }

}
