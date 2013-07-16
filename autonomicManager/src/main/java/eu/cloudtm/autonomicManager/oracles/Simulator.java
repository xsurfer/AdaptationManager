package eu.cloudtm.autonomicManager.oracles;

import eu.cloudtm.oracles.OutputOracle;
import eu.cloudtm.oracles.InputOracle;
import eu.cloudtm.oracles.Oracle;
import eu.cloudtm.oracles.exceptions.OracleException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/24/13
 */
public class Simulator implements Oracle {

    private static final String directory = "/home/fabio/Desktop/simulatore/DAGSwithCubist/Debug";
    private static final String script = "/home/fabio/Desktop/simulatore/DAGSwithCubist/Debug/DAGSwithCubist";

    Process p = null;

    public Simulator(){}

    @Override
    public OutputOracle forecast(InputOracle input) throws OracleException {

        ProcessBuilder pb = new ProcessBuilder(script, "");
        pb.directory(new File(directory));

        try {
            p = pb.start();
            InputStream shellIn = p.getInputStream(); // this captures the output from the command
            int shellExitStatus = p.waitFor(); // wait for the shell to finish and get the return code
            int c;
            while ((c = shellIn.read()) != -1) {
                System.out.write(c);
            }
            shellIn.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        OutputOracle outputOracle = new OutputOracleImpl(0,0,0);
        return outputOracle;
    }
}
