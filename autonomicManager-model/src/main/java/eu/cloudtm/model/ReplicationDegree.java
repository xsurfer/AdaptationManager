package eu.cloudtm.model;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/1/13
 */
public class ReplicationDegree extends AbstractTuned {

    private int degree;

    public ReplicationDegree(){
        degree = 2;
    }

    public int getDegree(){ return degree; }
    public void setDegree(int value){ degree = value; }

}
