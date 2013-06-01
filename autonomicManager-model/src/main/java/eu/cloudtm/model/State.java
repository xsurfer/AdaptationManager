package eu.cloudtm.model;

import eu.cloudtm.model.utils.Status;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/1/13
 */
public class State {

    private static State instance;

    private Status status;
    private Scale scale;
    private Replication replication;
    private Placement placement;

    public static State getInstance(){
        if(instance==null){
            instance = new State();
        }
        return instance;
    }

    private State(){
        status = Status.WORKING;
        scale = new Scale();
        replication = new Replication();
        placement = new Placement();
    }

    public Scale getScale(){
        return scale;
    }

    public Replication getReplication(){
        return replication;
    }

    public Placement getPlacement(){
        return placement;
    }

    public void updateScale(Scale newScale){
        scale = newScale;
    }

    public void updateReplication(Replication newReplication){
        replication = newReplication;
    }

    public void updateDataPlacement(Placement newDataPlacement){
        placement = newDataPlacement;
    }

}
