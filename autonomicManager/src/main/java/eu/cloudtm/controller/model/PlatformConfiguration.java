package eu.cloudtm.controller.model;

import com.google.gson.Gson;
import eu.cloudtm.controller.model.utils.PlatformState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/1/13
 */
public class PlatformConfiguration implements JSONClonable<PlatformConfiguration>{

    private static Log log = LogFactory.getLog(PlatformConfiguration.class);

    private PlatformState platformState;
    private final Scale scale;
    private final ReplicationProtocol replicationProtocol;
    private final ReplicationDegree replicationDegree;
    private final Placement dataPlacement;

    public PlatformConfiguration(PlatformState _platformState, Scale _scale, ReplicationProtocol _replicationProtocol, ReplicationDegree _replicationDegree, Placement _dataPlacement){
        platformState = _platformState;
        scale = _scale;
        replicationProtocol = _replicationProtocol;
        replicationDegree = _replicationDegree;
        dataPlacement = _dataPlacement;
    }

        /* GETTERS */

    public String getStatusClone(){
        return platformState.toString();
    }

    public Scale getScaleClone(){
        return scale.cloneJSON();
    }

    public ReplicationProtocol getReplicationProtocolClone(){
        return replicationProtocol.cloneJSON();
    }

    public ReplicationDegree getReplicationDegreeClone(){
        return replicationDegree.cloneJSON();
    }

    public Placement getDataPlacementClone(){
        return dataPlacement.cloneJSON();
    }


    /* UPDATE METHODS */

    public void updateState(PlatformState _platformState){
        platformState = _platformState;
    }

    public void updateScale(Scale newScale){
        if( newScale.getSize()<0 )
            throw new IllegalArgumentException("Configuration not acceptable!");
        if(newScale.getTuning()==null || newScale.getForecaster()==null)
            throw new IllegalArgumentException("Non-null value must be provided!");

        scale.setSize(newScale.getSize());
        scale.setInstanceType(newScale.getInstanceType());

        scale.setTuning(newScale.getTuning());
        scale.setForecaster(newScale.getForecaster());
    }

    public void updateReplicationProtocol(ReplicationProtocol replicationProtocolConf){
        replicationProtocol.setProtocol(replicationProtocolConf.getProtocol());

        replicationProtocol.setTuning(replicationProtocolConf.getTuning());
        replicationProtocol.setForecaster(replicationProtocolConf.getForecaster());
    }

    public void updateReplicationDegree(ReplicationDegree replicationDegreeConf){
        replicationDegree.setDegree(replicationDegreeConf.getDegree());

        replicationDegree.setTuning(replicationDegreeConf.getTuning());
        replicationDegree.setForecaster(replicationDegreeConf.getForecaster());
    }

    public void updateDataPlacement(Placement dataPlacementConf){
        dataPlacement.setTuning(dataPlacementConf.getTuning());
        dataPlacement.setForecaster(dataPlacementConf.getForecaster());
    }

    @Override
    public PlatformConfiguration cloneJSON() {
        Gson gson = new Gson();
        PlatformConfiguration state = gson.fromJson(gson.toJson(this), PlatformConfiguration.class);
        return state;
    }
}
