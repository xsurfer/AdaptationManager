package eu.cloudtm.commons;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/12/13
 */
public class KPIimpl implements OutputOracle {

    private double throughput;
    private double abortProbability;
    private double rtt;

    public KPIimpl(double throughput, double abortProbability, double responseTime) {
        this.throughput = throughput;
        this.abortProbability = abortProbability;
        this.rtt = responseTime;
    }

    @Override
    public double throughput() {
        return throughput;
    }

    @Override
    public double abortRate() {
        return abortProbability;
    }

    @Override
    public double responseTime(int txClassId) {
        return rtt;
    }

    @Override
    public String toString() {
        return "KPIimpl{" +
                "throughput=" + throughput +
                ", abortProbability=" + abortProbability +
                ", rtt=" + rtt +
                '}';
    }

//    @Override
//    public int compareTo(Object o) {
//        //Comparison based #nodes first, #thread later
//        if( this.platformConfiguration.platformSize() == ((KPIimpl) o).platformConfiguration.platformSize() ) {
//            return 0;
////            if (this.platformConfiguration.getNumThreads() == ((KPIimpl) o).platformConfiguration.getNumThreads()) {
////
////            } else if (this.platformConfiguration.getNumThreads() > ((KPIimpl) o).platformConfiguration.getNumThreads()) {
////                return 1;
////            } else {
////                return -1;
////            }
//        } else if( this.platformConfiguration.platformSize() > ((KPIimpl) o).platformConfiguration.platformSize() ) {
//            return 1;
//        } else {
//            return -1;
//        }
//        //throw new RuntimeException("TO IMPLEMENT!");
//    }

    @Override
    public boolean equals(Object obj){
        if( this.throughput == ((KPIimpl) obj).throughput )
            if( this.abortProbability == ((KPIimpl) obj).abortProbability )
                if( this.rtt == ((KPIimpl) obj).rtt )
                    return true;
        return false;
    }

}
