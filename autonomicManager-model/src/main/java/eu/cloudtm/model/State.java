package eu.cloudtm.model;

import eu.cloudtm.model.utils.Status;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/1/13
 */
public class State {

    private static State instance;

    private final Status status;
    private final Scale scale;
    private final ReplicationProtocol replicationProtocol;
    private final ReplicationDegree replicationDegree;
    private final Placement dataPlacement;

    public static State getInstance(){
        if(instance==null){
            instance = new State();
        }
        return instance;
    }

    private State(){
        status = Status.WORKING;
        scale = new Scale();
        replicationProtocol = new ReplicationProtocol();
        replicationDegree = new ReplicationDegree();
        dataPlacement = new Placement();
    }


    /* GETTERS */

    public Scale getScale(){ return scale; }

    public ReplicationProtocol getReplicationProtocol(){ return replicationProtocol; }

    public ReplicationDegree getReplicationDegree(){ return replicationDegree; }

    public Placement getDataPlacement(){ return dataPlacement; }


    /* UPDATE METHODS */

    public void updateScale(Scale newScale){
        if( (newScale.getSmall()+newScale.getMedium()+newScale.getLarge())<0 )
            throw new IllegalArgumentException("Configuration not acceptable!");
        if(newScale.getType()==null || newScale.getMethod()==null)
            throw new IllegalArgumentException("Non-null value must be provided!");

        scale.setSmall(newScale.getSmall());
        scale.setMedium(newScale.getMedium());
        scale.setLarge(newScale.getLarge());

        scale.setType(newScale.getType());
        scale.setMethod(newScale.getMethod());
    }

    public void updateReplicationProtocol(ReplicationProtocol replicationProtocolConf){
        replicationProtocol.setProtocol(replicationProtocolConf.getProtocol());

        replicationProtocol.setType(replicationProtocolConf.getType());
        replicationProtocol.setMethod(replicationProtocolConf.getMethod());
    }

    public void updateReplicationDegree(ReplicationDegree replicationDegreeConf){
        replicationDegree.setDegree(replicationDegreeConf.getDegree());

        replicationDegree.setType(replicationDegreeConf.getType());
        replicationDegree.setMethod(replicationDegreeConf.getMethod());
    }

    public void updateDataPlacement(Placement dataPlacementConf){
        dataPlacement.setType(dataPlacementConf.getType());
        dataPlacement.setMethod(dataPlacementConf.getMethod());
    }

}
