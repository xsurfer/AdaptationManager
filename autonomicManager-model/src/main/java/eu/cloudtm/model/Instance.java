package eu.cloudtm.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 5/27/13
 */
@XmlRootElement(name = "job")
public class Instance {

    public int instances;

    // just to make JAXB happy
    public Instance(){};

    public Instance(int instances) {
        this.instances = instances;
    }

}