package eu.cloudtm.model;

import eu.cloudtm.model.utils.ReplicationProtocol;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/1/13
 */
public class Replication extends AbstractTuned {

    private int degree;
    private ReplicationProtocol protocol;

    public Replication(){
        degree = 2;
        protocol = ReplicationProtocol.TWOPC;
    }

    public void setDegree(int value){ degree = value; }
    public void setProtocol(ReplicationProtocol value){ protocol = value; }

}
