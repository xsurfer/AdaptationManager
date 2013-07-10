package eu.cloudtm.oracles;

import eu.cloudtm.ProcessedSample;
import eu.cloudtm.WPMSample;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 7/10/13
 */
public class InputOracle extends ProcessedSample {

    public InputOracle(WPMSample sample) {
        super(sample);
    }

    @Override
    protected double getACF() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
