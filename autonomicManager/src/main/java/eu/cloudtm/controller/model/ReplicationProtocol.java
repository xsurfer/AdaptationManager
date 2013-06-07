package eu.cloudtm.controller.model;

import com.google.gson.Gson;
import eu.cloudtm.controller.model.utils.Forecaster;
import eu.cloudtm.controller.model.utils.ReplicationProtocols;
import eu.cloudtm.controller.model.utils.TuningType;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/1/13
 */
public class ReplicationProtocol extends AbstractTuned implements JSONClonable<ReplicationProtocol> {

    private ReplicationProtocols protocol;

    private ReplicationProtocol(){ super(); }

    public ReplicationProtocol(TuningType type, Forecaster forecaster, ReplicationProtocols protocol){
        super(type, forecaster);
        protocol = ReplicationProtocols.TWOPC;
    }

    public ReplicationProtocols getProtocol() { return protocol; }
    public void setProtocol(ReplicationProtocols value){ protocol = value; }

    @Override
    public ReplicationProtocol cloneJSON() {
        Gson gson = new Gson();
        ReplicationProtocol replicationProtocol = gson.fromJson(gson.toJson(this), ReplicationProtocol.class);
        return replicationProtocol;
    }
}
