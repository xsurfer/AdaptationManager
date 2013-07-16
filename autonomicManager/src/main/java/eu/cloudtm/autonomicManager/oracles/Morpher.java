package eu.cloudtm.autonomicManager.oracles;

import eu.cloudtm.commons.OutputOracle;
import eu.cloudtm.commons.KPIimpl;
import eu.cloudtm.oracles.InputOracle;
import eu.cloudtm.oracles.Oracle;
import eu.cloudtm.oracles.exceptions.OracleException;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/24/13
 */
public class Morpher implements Oracle {

    private static final String directory = "/home/fabio/Desktop/simulatore/DAGSwithCubist/Debug";
    private static final String script = "/home/fabio/Desktop/simulatore/DAGSwithCubist/Debug/DAGSwithCubist";

    Process p = null;

    public Morpher(){}

    @Override
    public OutputOracle forecast(InputOracle input) throws OracleException {

        OutputOracle outputOracle = new KPIimpl(0,0,0);
        return outputOracle;
    }
}
