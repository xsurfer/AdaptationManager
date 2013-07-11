package eu.cloudtm.oracles;

import eu.cloudtm.statistics.ProcessedSample;
import eu.cloudtm.statistics.WPMParam;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 7/10/13
 */
public class InputOracleWPM implements InputOracle {

    private ProcessedSample processedSample;

    public InputOracleWPM(ProcessedSample processedSample) {
        this.processedSample = processedSample;
    }

    @Override
    public Object getPerNodeParam(WPMParam param, String nodeIp) {
        return processedSample.getPerNodeParam(param, nodeIp);
    }

    @Override
    public double getAvgParam(WPMParam param) {
        return processedSample.getAvgParam(param);
    }
}
