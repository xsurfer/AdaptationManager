package eu.cloudtm.oracles;

import eu.cloudtm.commons.EvaluatedParam;
import eu.cloudtm.commons.ForecastParam;
import eu.cloudtm.commons.Param;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/10/13
 * Time: 6:43 PM
 * To change this template use File | Settings | File Templates.
 */
public interface InputOracle {

    public Object getParam(Param param);

    public double getEvaluatedParam(EvaluatedParam param);

    public Object getForecastParam(ForecastParam param);

}
