package eu.cloudtm.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 5/27/13
 */
@XmlRootElement
public class Scale extends AbstractTuned {

    private int small;
    private int medium;
    private int large;

    public Scale(){
        small = 0;
        medium = 0;
        large = 0;
    }

    public int getSmall() { return small; }
    public void setSmall(int value){ small = value; }

    public int getMedium() { return medium; }
    public void setMedium(int value){ medium = value; }

    public int getLarge() { return large; }
    public void setLarge(int value){ large = value; }

}