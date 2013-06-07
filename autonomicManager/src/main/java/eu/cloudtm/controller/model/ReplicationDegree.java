package eu.cloudtm.controller.model;

import com.google.gson.Gson;
import eu.cloudtm.controller.model.utils.Forecaster;
import eu.cloudtm.controller.model.utils.TuningType;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/1/13
 */
public class ReplicationDegree extends AbstractTuned implements JSONClonable<ReplicationDegree> {

    private int degree;

    public ReplicationDegree(TuningType type, Forecaster forecaster, int _degree){
        super(type, forecaster);
        degree = _degree;
    }

    public int getDegree(){ return degree; }
    public void setDegree(int value){ degree = value; }

    @Override
    public ReplicationDegree cloneJSON() {
        Gson gson = new Gson();
        ReplicationDegree replicationDegree = gson.fromJson(gson.toJson(this), ReplicationDegree.class);
        return replicationDegree;
    }
}
