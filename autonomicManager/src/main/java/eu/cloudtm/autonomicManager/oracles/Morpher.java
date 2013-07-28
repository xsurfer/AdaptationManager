package eu.cloudtm.autonomicManager.oracles;

import eu.cloudtm.autonomicManager.oracles.exceptions.OracleException;

import java.util.Random;

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

        Random rnd = new Random();
        return new OutputOracleImpl(rnd.nextInt(3000), rnd.nextDouble(), rnd.nextDouble(), rnd.nextDouble());
    }
}
