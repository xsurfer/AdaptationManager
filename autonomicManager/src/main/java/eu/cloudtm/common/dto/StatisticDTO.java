package eu.cloudtm.common.dto;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/10/13
 */
public class StatisticDTO {

    private String param;
    private Collection<Collection> data = new ArrayList<Collection>();

    public StatisticDTO(String _param){
        this.param = _param;
    }

    public void addPoint(long x, Object y){
        Collection point = new ArrayList();
        point.add(x);
        point.add(y);
        data.add(point);
    }
}
