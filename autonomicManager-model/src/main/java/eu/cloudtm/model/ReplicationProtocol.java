package eu.cloudtm.model;

import eu.cloudtm.model.utils.ReplicationProtocols;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/1/13
 */
public class ReplicationProtocol extends AbstractTuned {

    private ReplicationProtocols protocol;

    public ReplicationProtocol(){
        protocol = ReplicationProtocols.TWOPC;
    }

    public ReplicationProtocols getProtocol() { return protocol; }
    public void setProtocol(ReplicationProtocols value){ protocol = value; }

}
