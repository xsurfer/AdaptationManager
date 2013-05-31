package eu.cloudtm.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 5/27/13
 */
@XmlRootElement
public class ScaleInfo {

    public int small;
    public int medium;
    public int large;

    public TuningType type;
    public TuningMethod method;


    public ScaleInfo(){
        type = TuningType.AUTO;
        method = TuningMethod.ANALYTICAL;
        small = 0;
        medium = 0;
        large = 0;
    }

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