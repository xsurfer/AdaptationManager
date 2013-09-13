package eu.cloudtm.autonomicManager.oracles;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 9/13/13
 */
public class ComparableOutputOracle implements Comparable<ComparableOutputOracle> {

    private final OutputOracle outputOracle;

    public ComparableOutputOracle(OutputOracle outputOracle){
        this.outputOracle = outputOracle;
    }

    private double getThroughputSum(OutputOracle o){
        return o.throughput(0) + o.throughput(1);
    }

    public OutputOracle getOutputOracle(){
        return outputOracle;
    }

    @Override
    public int compareTo(ComparableOutputOracle o) {
        if(getThroughputSum(getOutputOracle()) < getThroughputSum(o.getOutputOracle()) ){
            return -1;
        } else if(getThroughputSum(getOutputOracle()) > getThroughputSum(o.getOutputOracle()) ){
            return 1;
        } else {
            return 0;
        }
    }
}
