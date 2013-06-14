package eu.cloudtm.controller.model;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/12/13
 */
public class KPI implements Comparable {

    private PlatformConfiguration platformConfiguration;
    private double throughput;
    private double abortProbability;
    private double rtt;

    public KPI(PlatformConfiguration platformConfiguration, double throughput, double abortProbability, double rtt) {
        this.platformConfiguration = platformConfiguration;
        this.throughput = throughput;
        this.abortProbability = abortProbability;
        this.rtt = rtt;
    }

    public PlatformConfiguration getPlatformConfiguration() {
        return platformConfiguration;
    }

    public void setPlatformConfiguration(PlatformConfiguration platformConfiguration) {
        this.platformConfiguration = platformConfiguration;
    }

    public double getThroughput() {
        return throughput;
    }

    public void setThroughput(double throughput) {
        this.throughput = throughput;
    }

    public double getAbortProbability() {
        return abortProbability;
    }

    public void setAbortProbability(double abortProbability) {
        this.abortProbability = abortProbability;
    }

    public double getRtt() {
        return rtt;
    }

    public void setRtt(double rtt) {
        this.rtt = rtt;
    }

    @Override
    public String toString() {
        return "KPI{" +
                "platformConfiguration=" + platformConfiguration +
                ", throughput=" + throughput +
                ", abortProbability=" + abortProbability +
                ", rtt=" + rtt +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        // Comparison based #nodes first, #thread later
//        if (this.platformConfiguration.getScaleClone().getSize() == ((KPI) o).platformConfiguration.getScaleClone().getSize()) {
//            return 0;
//            if (this.platformConfiguration.getNumThreads() == ((KPI) o).platformConfiguration.getNumThreads()) {
//
//            } else if (this.platformConfiguration.getNumThreads() > ((KPI) o).platformConfiguration.getNumThreads()) {
//                return 1;
//            } else {
//                return -1;
//            }
//        } else if (this.platformConfiguration.getNumNodes() > ((KPI) o).platformConfiguration.getNumNodes()) {
//            return 1;
//        } else {
//            return -1;
//        }
        throw new RuntimeException("TO IMPLEMENT!");
    }

    @Override
    public boolean equals(Object obj){
        if ( this.platformConfiguration.equals(((KPI) obj).platformConfiguration) )
            if( this.throughput == ((KPI) obj).throughput )
                if( this.abortProbability == ((KPI) obj).abortProbability )
                    if( this.rtt == ((KPI) obj).rtt )
                        return true;
        return false;
    }
}
