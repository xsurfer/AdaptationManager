package eu.cloudtm.autonomicManager.oracles;


import eu.cloudtm.autonomicManager.commons.PlatformConfiguration;
import eu.cloudtm.autonomicManager.commons.ReplicationProtocol;
import eu.cloudtm.autonomicManager.oracles.exceptions.OracleException;
import eu.cloudtm.autonomicManager.statistics.ProcessedSample;

import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: fabio Date: 7/15/13 Time: 5:07 PM To change this template use File | Settings |
 * File Templates.
 */
public interface OracleService {

   public PlatformConfiguration minimizeCosts(ProcessedSample sample,
                                              double arrivalRateToGuarantee,
                                              double abortRateToGuarantee,
                                              double responseTimeToGuarantee) throws OracleException;


   /**
    * What-if with Protocols on X-axis
    *
    * @param sample
    * @param fixedNodes
    * @param fixedDegree
    * @return
    */
   public Map<PlatformConfiguration, OutputOracle> whatIf(ProcessedSample sample, int fixedNodes, int fixedDegree);

   /**
    * What-if with Nodes on X-axis
    *
    * @param sample
    * @param fixedProtocol
    * @param fixedDegree
    * @return
    */
   public Map<PlatformConfiguration, OutputOracle> whatIf(ProcessedSample sample, int minNumNodes, int maxNumNodes,
                                                          ReplicationProtocol fixedProtocol, int fixedDegree);

   /**
    * What-if with Degree on X-axis
    *
    * @param sample
    * @param fixedNodes
    * @param fixedProtocol
    * @return
    */
   public Map<PlatformConfiguration, OutputOracle> whatIf(ProcessedSample sample, int minNumDegree, int maxNumDegree,
                                                          int fixedNodes, ReplicationProtocol fixedProtocol);


   public PlatformConfiguration maximizeThroughput(ProcessedSample sample) throws OracleException;

}
