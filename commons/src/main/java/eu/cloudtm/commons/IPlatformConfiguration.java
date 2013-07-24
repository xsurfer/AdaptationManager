package eu.cloudtm.commons;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 7/12/13
 */
public interface IPlatformConfiguration {

    public void setPlatformScale(int size, InstanceConfig nodesConfig);

    public void setRepProtocol(ReplicationProtocol repProtocol);

    public void setRepDegree(int repDegree);


    public int platformSize();

    public int threadPerNode();

    public InstanceConfig nodeConfiguration();

    public ReplicationProtocol replicationProtocol();

    public int replicationDegree();

    public boolean isDataPlacement();
}
