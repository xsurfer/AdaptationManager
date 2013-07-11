package eu.cloudtm.statistics;


import eu.cloudtm.commons.ACF;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/8/13
 * Time: 8:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class TWOPCInputOracle extends ProcessedSample {

    public TWOPCInputOracle(WPMSample sample) {
        super(sample);
    }

    @Override
    public double getACF() {
        double PaoloLocalTakenLocks = getAvgParam(WPMParam.PaoloLocalTakenLocks);
        double NumPuts = getAvgParam(WPMParam.NumPuts);
        double PaoloLocalTakenHoldTime = getAvgParam(WPMParam.PaoloLocalTakenHoldTime);
        double PaoloRemoteTakenHoldTime = getAvgParam(WPMParam.PaoloRemoteTakenHoldTime);
        double PaoloRemoteTakenLocks = getAvgParam(WPMParam.PaoloRemoteTakenLocks);
        double threads = 0; //Controller.getInstance().getCurrentConfiguration().threadPerNode();
        double timeWindow = 60D; // Controller.TIME_WINDOW;

        double acf = new ACF(
                PaoloLocalTakenLocks,
                NumPuts,
                PaoloLocalTakenHoldTime,
                PaoloRemoteTakenHoldTime,
                PaoloRemoteTakenLocks,
                threads,
                timeWindow).evaluate();

        return acf;
    }

}
