package eu.cloudtm.oracles;

import eu.cloudtm.commons.KPI;
import eu.cloudtm.commons.KPIimpl;
import eu.cloudtm.oracles.exceptions.OracleException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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
    public KPI forecast(InputOracle input) throws OracleException {

        KPI kpi = new KPIimpl(0,0,0);
        return kpi;
    }
}
