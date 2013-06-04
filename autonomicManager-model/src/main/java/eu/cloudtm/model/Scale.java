package eu.cloudtm.model;

import eu.cloudtm.model.utils.InstanceConfigurations;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 5/27/13
 */
@XmlRootElement
public class Scale extends AbstractTuned {

    private int size;
    private InstanceConfigurations instanceType;


    public Scale(){
        size = 0;
        instanceType = InstanceConfigurations.NONE;
    }

    public int getSize() { return size; }
    public void setSize(int value){ size = value; }

    public InstanceConfigurations getInstanceType() { return instanceType; }
    public void setInstanceType(InstanceConfigurations value){ instanceType = value; }

}