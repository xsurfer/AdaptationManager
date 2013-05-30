package eu.cloudtm.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 5/27/13
 */
@XmlRootElement
public class ScaleInfo {

    public int small;
    public int medium;
    public int large;

    public Tuning tuning;

    public ScaleInfo(){
        tuning = new Tuning();
        small = 0;
        medium = 0;
        large = 0;
    }




}