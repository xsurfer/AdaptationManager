package eu.cloudtm.autonomicManager.oracles;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 9/13/13
 */

import eu.cloudtm.autonomicManager.commons.PlatformConfiguration;
import eu.cloudtm.autonomicManager.commons.ReplicationProtocol;
import eu.cloudtm.autonomicManager.oracles.exceptions.OracleException;
import eu.cloudtm.autonomicManager.statistics.ProcessedSample;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class HillClimbingOracleService extends OracleServiceImpl {

   private static Log log = LogFactory.getLog(HillClimbingOracleService.class);

   public HillClimbingOracleService(Oracle oracle) {
      super(oracle);
   }

   @Override
   public final PlatformConfiguration minimizeCosts(ProcessedSample sample,
                                                    double arrivalRateToGuarantee,
                                                    double abortRateToGuarantee,
                                                    double responseTimeToGuarantee)
         throws OracleException {

      PlatformConfiguration initState = new PlatformConfiguration(2, 2, ReplicationProtocol.TWOPC);

      boolean running = true;

      while (running) {

         Set<PlatformConfiguration> neighborhood = neighbors(initState);
         TreeMap<ComparableOutputOracle, PlatformConfiguration> neighborsForecast = queryNeighbors(neighborhood, sample);
         log.trace("Neighborhood size: " + neighborsForecast.size());

         TreeMap<PlatformConfiguration, ComparableOutputOracle> aa = new TreeMap<PlatformConfiguration, ComparableOutputOracle>();
         for (Map.Entry<ComparableOutputOracle, PlatformConfiguration> entry : neighborsForecast.entrySet()) {
            aa.put(entry.getValue(), entry.getKey());
         }

         //looking for smallest platform which satisfies QoS
         Map.Entry<PlatformConfiguration, ComparableOutputOracle> lowestNeighbor;
         PlatformConfiguration lowestNeighborPlatform = null;
         OutputOracle lowestNeighborOutput = null;

         boolean satisfiedQoS = false;
         while (!satisfiedQoS) {
            lowestNeighbor = aa.pollFirstEntry(); // entry with lowest platform size
            if (lowestNeighbor == null) {
               log.trace("No platform satisfies the qos!");
               return null;
            }
            lowestNeighborPlatform = lowestNeighbor.getKey();
            lowestNeighborOutput = lowestNeighbor.getValue().getOutputOracle();

            if (lowestNeighborOutput.throughput(0) >= arrivalRateToGuarantee && lowestNeighborOutput.abortRate(0) <= abortRateToGuarantee) {
               satisfiedQoS = true;
            }
         }

         log.trace("Neighbor with lowest size, satisfying qos: " + lowestNeighborPlatform);
         log.trace("Neighbor with lowest size, satisfying qos, throughput: (0) " + lowestNeighborOutput.throughput(0) + ", (1) " + lowestNeighborOutput.throughput(1));

         if (lowestNeighborPlatform.platformSize() < initState.platformSize()) {
            log.trace("Neighbor has lower platform size");
            initState = lowestNeighborPlatform;
         } else {
            log.trace("Neighbor hasn't lower platform size");
            running = false;
         }
      }

      log.trace("Configuration with the highest throughput: " + initState);

      return initState;

   }


   @Override
   public final PlatformConfiguration maximizeThroughput(ProcessedSample sample) throws OracleException {

      PlatformConfiguration initState = new PlatformConfiguration(2, 2, ReplicationProtocol.TWOPC);
      OutputOracle initOutput = doForecast(initState, sample);
      double currentThroughput = initOutput.throughput(0) + initOutput.throughput(1);

      boolean running = true;

      while (running) {

         Set<PlatformConfiguration> neighborhood = neighbors(initState);
         TreeMap<ComparableOutputOracle, PlatformConfiguration> neighborsForecast = queryNeighbors(neighborhood, sample);

         log.trace("Neighborhood size: " + neighborsForecast.size());

         Map.Entry<ComparableOutputOracle, PlatformConfiguration> higherNeighbor = neighborsForecast.lastEntry(); // entry with highest throughput

         PlatformConfiguration higherNeighborPlatform = higherNeighbor.getValue();
         OutputOracle higherNeighborOutput = higherNeighbor.getKey().getOutputOracle();

         log.trace("Neighbor with  throughput: " + higherNeighborPlatform);
         log.trace("Highest Neighbor throughput output: (0)" + higherNeighborOutput.throughput(0) + ", (1) " + higherNeighborOutput.throughput(1));

         double higherNeighborThroughput = higherNeighborOutput.throughput(0) + higherNeighborOutput.throughput(1);


         log.trace(higherNeighborThroughput + " > " + currentThroughput);
         if (higherNeighborThroughput > currentThroughput) {
            log.trace("Neighbor has higher throughput");
            initState = higherNeighborPlatform;
            currentThroughput = higherNeighborOutput.throughput(0) + higherNeighborOutput.throughput(1);
         } else {
            log.trace("Neighbor hasn't higher throughput");
            running = false;
         }
      }

      log.trace("Configuration with the highest throughput: " + initState);

      return initState;
   }


   private TreeMap<ComparableOutputOracle, PlatformConfiguration> queryNeighbors(Set<PlatformConfiguration> neighborhood, ProcessedSample sample) {

      TreeMap<ComparableOutputOracle, PlatformConfiguration> ret = new TreeMap<ComparableOutputOracle, PlatformConfiguration>();

      for (PlatformConfiguration state : neighborhood) {
         log.trace("Preparing query for <" + state.platformSize() + ", " + state.replicationDegree() + ", " + state.replicationProtocol() + ">");

         OutputOracle outputOracle = doForecast(state, sample);

         if (outputOracle != null) {
            ret.put(new ComparableOutputOracle(outputOracle), state);
         }
      }
      return ret;
   }


   private Set<PlatformConfiguration> neighbors(PlatformConfiguration initState) {
      Set<PlatformConfiguration> resultSet = new HashSet<PlatformConfiguration>();

      int size = nodesMax + initState.platformSize() - initState.replicationDegree();
      log.trace("Neighborhood size: " + size);

        /* changing num nodes */
      for (int nodes = initState.replicationDegree(); nodes <= nodesMax; nodes++) {
         if (nodes == initState.platformSize()) {
            continue;
         }
         PlatformConfiguration state = new PlatformConfiguration(nodes, initState.replicationDegree(), initState.replicationProtocol());
         resultSet.add(state);
      }

        /* changing degree */
      for (int degree = nodesMin; degree <= initState.platformSize(); degree++) {
         if (degree == initState.replicationDegree()) {
            continue;
         }
         PlatformConfiguration state = new PlatformConfiguration(initState.platformSize(), degree, initState.replicationProtocol());
         //System.out.println(state);
         resultSet.add(state);
      }

        /* changing protocol */
      for (ReplicationProtocol protocol : ReplicationProtocol.values()) {
         if (protocol == initState.replicationProtocol()) {
            continue;
         }
         PlatformConfiguration state = new PlatformConfiguration(initState.platformSize(), initState.replicationDegree(), protocol);
         //System.out.println(state);
         resultSet.add(state);
      }
      //System.out.println("SizeCheck: " + size +"==" + resultSet.size());
      return resultSet;

   }


}
