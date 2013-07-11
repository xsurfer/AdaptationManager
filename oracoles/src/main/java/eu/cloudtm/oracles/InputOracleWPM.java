package eu.cloudtm.oracles;

import eu.cloudtm.statistics.WPMProcessedSample;
import eu.cloudtm.statistics.WPMParam;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 7/10/13
 */
public class InputOracleWPM implements InputOracle {

    private WPMProcessedSample processedSample;

    public InputOracleWPM(WPMProcessedSample processedSample) {
        this.processedSample = processedSample;
    }

    @Override
    public Object getPerNodeParam(WPMParam param, String nodeIp) {
        return processedSample.getPerNodeParam(param, nodeIp);
    }

    @Override
    public double getAvgParam(WPMParam param) {
        return processedSample.getParam(param);
    }
}
