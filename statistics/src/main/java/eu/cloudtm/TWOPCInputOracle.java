package eu.cloudtm;


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
        double PaoloLocalTakenLocks = getAvgParam(WPMParam.PaoloLocalTakenLocks, 0);
        double NumPuts = getAvgParam(WPMParam.NumPuts, 0);
        double PaoloLocalTakenHoldTime = getAvgParam(WPMParam.PaoloLocalTakenHoldTime, 0);
        double PaoloRemoteTakenHoldTime = getAvgParam(WPMParam.PaoloRemoteTakenHoldTime, 0);
        double PaoloRemoteTakenLocks = getAvgParam(WPMParam.PaoloRemoteTakenLocks, 0);
        double threads = Controller.getInstance().getCurrentConfiguration().threadPerNode();
        double timeWindow = Controller.TIME_WINDOW;

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
