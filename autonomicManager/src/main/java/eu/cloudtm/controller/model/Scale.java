package eu.cloudtm.controller.model;

import com.google.gson.Gson;
import eu.cloudtm.controller.model.utils.Forecaster;
import eu.cloudtm.controller.model.utils.InstanceConfigurations;
import eu.cloudtm.controller.model.utils.TuningType;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 5/27/13
 */
@XmlRootElement
public class Scale extends AbstractTuned implements JSONClonable<Scale>{

    private int size;
    private InstanceConfigurations instanceType;

    private Scale(){super();}

    public Scale(TuningType _type, Forecaster _forecaster, int _size, InstanceConfigurations _instanceType){
        super(_type, _forecaster);
        size = _size;
        instanceType = _instanceType;
    }

    public int getSize() { return size; }
    public void setSize(int value){ size = value; }

    public InstanceConfigurations getInstanceType() { return instanceType; }
    public void setInstanceType(InstanceConfigurations value){ instanceType = value; }

    @Override
    public Scale cloneJSON() {
        Gson gson = new Gson();
        Scale scale = gson.fromJson(gson.toJson(this), Scale.class);
        return scale;
    }

}