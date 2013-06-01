package eu.cloudtm.model;

import eu.cloudtm.model.utils.TuningMethod;
import eu.cloudtm.model.utils.TuningType;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/1/13
 */
public abstract class AbstractTuned {

    private TuningType type;
    private TuningMethod method;

    public AbstractTuned(){
        type = TuningType.AUTO;
        method = TuningMethod.ANALYTICAL;
    }

    public void setType(TuningType value){ type = value; }
    public void setMethod(TuningMethod value){ method = value; }

    public String printTuning(){
        String ret;
        if(type.equals(TuningType.AUTO)){
            ret = type + " + " + method;
        } else {
            ret = type.toString();
        }
        return ret;
    }

}
