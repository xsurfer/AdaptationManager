package eu.cloudtm.autonomicManager.oracles;

/**
 * Created by: Fabio Perfetti E-mail: perfabio87@gmail.com Date: 6/12/13
 */
public class OutputOracleImpl implements OutputOracle {

   private final double throughput0;
   private final double throughput1;
   private final double abortProbability0;
   private final double abortProbability1;
   private final double respTime0;
   private final double respTime1;

   public OutputOracleImpl(double throughput0, double throughput1, double abortProbability0, double abortProbability1, double respTime0, double respTime1) {
      this.throughput0 = throughput0;
      this.throughput1 = throughput1;
      this.abortProbability0 = abortProbability0;
      this.abortProbability1 = abortProbability1;
      this.respTime0 = respTime0;
      this.respTime1 = respTime1;
   }

   @Override
   public double throughput(int txClassId) {
      if (txClassId == 0) {
         return throughput0;
      } else if (txClassId == 1) {
         return throughput1;
      }
      throw new IllegalArgumentException("Tx Class with id=" + txClassId + " not supported");
   }

   @Override
   public double abortRate(int txClassId) {
      if (txClassId == 0) {
         return abortProbability0;
      } else if (txClassId == 1) {
         return abortProbability1;
      }
      throw new IllegalArgumentException("Tx Class with id=" + txClassId + " not supported");
   }

   @Override
   public double responseTime(int txClassId) {
      if (txClassId == 0) {
         return respTime0;
      } else if (txClassId == 1) {
         return respTime1;
      }
      throw new IllegalArgumentException("Tx Class with id=" + txClassId + " not supported");
   }

   @Override
   public double getConfidenceThroughput(int txClassId) {
      return 0;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public double getConfidenceAbortRate(int txClassId) {
      return 0;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public double getConfidenceResponseTime(int txClassId) {
      return 0;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public String toString() {
      return "OutputOracleImpl{ " +
            "throughput0=" + throughput0 +
            ", throughput1 = " + throughput1 +
            ", abortProbability0 = " + abortProbability0 +
            ", abortProbability1 = " + abortProbability1 +
            ", respTime0=" + respTime0 +
            ", respTime1=" + respTime1 +
            '}';
   }

//    @Override
//    public int compareTo(Object o) {
//        //Comparison based #nodes first, #thread later
//        if( this.platformConfiguration.platformSize() == ((OutputOracleImpl) o).platformConfiguration.platformSize() ) {
//            return 0;
////            if (this.platformConfiguration.getNumThreads() == ((OutputOracleImpl) o).platformConfiguration.getNumThreads()) {
////
////            } else if (this.platformConfiguration.getNumThreads() > ((OutputOracleImpl) o).platformConfiguration.getNumThreads()) {
////                return 1;
////            } else {
////                return -1;
////            }
//        } else if( this.platformConfiguration.platformSize() > ((OutputOracleImpl) o).platformConfiguration.platformSize() ) {
//            return 1;
//        } else {
//            return -1;
//        }
//        //throw new RuntimeException("TO IMPLEMENT!");
//    }

   @Override
   public boolean equals(Object obj) {
      if (this.throughput0 == ((OutputOracleImpl) obj).throughput0)
         if (this.throughput1 == ((OutputOracleImpl) obj).throughput1)
            if (this.abortProbability0 == ((OutputOracleImpl) obj).abortProbability0)
               if (this.abortProbability1 == ((OutputOracleImpl) obj).abortProbability1)
                  if (this.respTime0 == ((OutputOracleImpl) obj).respTime0)
                     if (this.respTime1 == ((OutputOracleImpl) obj).respTime1)
                        return true;
      return false;
   }

}
