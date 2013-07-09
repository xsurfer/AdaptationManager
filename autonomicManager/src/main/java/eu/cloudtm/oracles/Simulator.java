package eu.cloudtm.oracles;

import eu.cloudtm.common.dto.WhatIfCustomParamDTO;
import eu.cloudtm.Controller;
import eu.cloudtm.model.KPI;
import eu.cloudtm.stats.WPMSample;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/24/13
 */
public class Simulator extends AbstractOracle {

    private static final String directory = "/home/fabio/Desktop/simulatore/DAGSwithCubist/Debug";
    private static final String script = "/home/fabio/Desktop/simulatore/DAGSwithCubist/Debug/DAGSwithCubist";

    Process p = null;

    public Simulator(){}

    public Simulator(Controller _controller) {
        super(_controller);
    }

    @Override
    public KPI forecast(InputOracle sample) {

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

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
//
//    @Override
//    public KPI forecastWithCustomParam(WPMSample sample, WhatIfCustomParamDTO customParam, int numNodes, int numThreads) {
//        return null;  //To change body of implemented methods use File | Settings | File Templates.
//    }


}
