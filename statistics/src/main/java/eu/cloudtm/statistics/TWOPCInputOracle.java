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

    public TWOPCInputOracle(Sample sample) {
        super(sample);
    }

    @Override
    public double getACF() {
        double PaoloLocalTakenLocks = (Long) getParam(Param.PaoloLocalTakenLocks);
        double NumPuts = (Double) getParam(Param.NumPuts);
        double PaoloLocalTakenHoldTime = (Double) getParam(Param.PaoloLocalTakenHoldTime);
        double PaoloRemoteTakenHoldTime = (Double) getParam(Param.PaoloRemoteTakenHoldTime);
        double PaoloRemoteTakenLocks = (Double) getParam(Param.PaoloRemoteTakenLocks);
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
